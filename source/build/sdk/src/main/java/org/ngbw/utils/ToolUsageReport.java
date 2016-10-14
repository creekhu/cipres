/*
 * ToolUsageReport.java
 */
package org.ngbw.utils;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;


/**
 * 
 * @author Paul Hoover
 *
 */
public class ToolUsageReport {

	private static class ToolStatistics
	{
		public final String toolId;
		public int numExecutions = 0;
		public Double minInputSize = Double.MAX_VALUE;
		public Double maxInputSize = 0.0;
		public Double totalInputSize = 0.0;
		public int numInputs = 0;
		public Double minOutputSize = Double.MAX_VALUE;
		public Double maxOutputSize = 0.0;
		public Double totalOutputSize = 0.0;
		public int numOutputs = 0;


		ToolStatistics(String id)
		{
			toolId = id;
		}
	}


	public static void report() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		Map<String, ToolStatistics> tools = new TreeMap<String, ToolStatistics>();

		try {
			getNumExecutions(dbConn, tools);
			getInputSizes(dbConn, tools);
			getOutputSizes(dbConn, tools);
		}
		finally {
			dbConn.close();
		}

		System.out.println("TOOL_ID,NUM_USES,MIN_INPUT_SIZE,MAX_INPUT_SIZE,AVG_INPUT_SIZE,MIN_OUTPUT_SIZE,MAX_OUTPUT_SIZE,AVG_OUTPUT_SIZE");

		for (Iterator<ToolStatistics> values = tools.values().iterator() ; values.hasNext() ; ) {
			ToolStatistics stats = values.next();
			Double averageInputSize;
			Double averageOutputSize;

			if (stats.numInputs > 0)
				averageInputSize = stats.totalInputSize / stats.numInputs;
			else {
				averageInputSize = 0.0;

				stats.minInputSize = 0.0;
			}

			if (stats.numOutputs > 0)
				averageOutputSize = stats.totalOutputSize / stats.numOutputs;
			else {
				averageOutputSize = 0.0;

				stats.minOutputSize = 0.0;
			}

			System.out.print(stats.toolId);
			System.out.print(',');
			System.out.print(stats.numExecutions);
			System.out.print(',');
			System.out.print(stats.minInputSize.longValue());
			System.out.print(',');
			System.out.print(stats.maxInputSize.longValue());
			System.out.print(',');
			System.out.print(averageInputSize.longValue());
			System.out.print(',');
			System.out.print(stats.minOutputSize.longValue());
			System.out.print(',');
			System.out.print(stats.maxOutputSize.longValue());
			System.out.print(',');
			System.out.print(averageOutputSize.longValue());
			System.out.print('\n');
		}
	}

	public static void main(String[] args)
	{
		try {
			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			report();
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}


	private static void getNumExecutions(Connection dbConn, Map<String, ToolStatistics> tools) throws SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT TOOL_ID, COUNT(*) " +
				"FROM tasks " +
				"GROUP BY TOOL_ID");
		ResultSet toolRows = null;

		try {
			toolRows = selectStmt.executeQuery();

			while (toolRows.next()) {
				String id = toolRows.getString(1);
				ToolStatistics stats;

				if (!tools.containsKey(id)) {
					stats = new ToolStatistics(id);

					tools.put(id, stats);
				}
				else
					stats = tools.get(id);

				stats.numExecutions = toolRows.getInt(2);
			}
		}
		finally {
			if (toolRows != null)
				toolRows.close();

			selectStmt.close();
		}
	}

	private static void getInputSizes(Connection dbConn, Map<String, ToolStatistics> tools) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT tasks.TOOL_ID, source_documents.DATA " +
				"FROM tasks " +
					"INNER JOIN task_input_parameters ON tasks.TASK_ID = task_input_parameters.TASK_ID " +
					"INNER JOIN task_input_source_documents ON task_input_parameters.INPUT_ID = task_input_source_documents.INPUT_ID " +
					"INNER JOIN source_documents ON task_input_source_documents.SOURCE_DOCUMENT_ID = source_documents.SOURCE_DOCUMENT_ID");
		ResultSet toolRows = null;

		try {
			toolRows = selectStmt.executeQuery();

			while (toolRows.next()) {
				String id = toolRows.getString(1);
				ToolStatistics stats;

				if (!tools.containsKey(id)) {
					stats = new ToolStatistics(id);

					tools.put(id, stats);
				}
				else
					stats = tools.get(id);

				byte[] data = inflateBytes(toolRows.getBytes(2));

				if (data.length < stats.minInputSize)
					stats.minInputSize = (double) data.length;

				if (data.length > stats.maxInputSize)
					stats.maxInputSize = (double) data.length;

				stats.totalInputSize += data.length;
				stats.numInputs += 1;
			}
		}
		finally {
			if (toolRows != null)
				toolRows.close();

			selectStmt.close();
		}
	}

	private static void getOutputSizes(Connection dbConn, Map<String, ToolStatistics> tools) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT tasks.TOOL_ID, source_documents.DATA " +
				"FROM tasks " +
					"INNER JOIN task_output_parameters ON tasks.TASK_ID = task_output_parameters.TASK_ID " +
					"INNER JOIN task_output_source_documents ON task_output_parameters.OUTPUT_ID = task_output_source_documents.OUTPUT_ID " +
					"INNER JOIN source_documents ON task_output_source_documents.SOURCE_DOCUMENT_ID = source_documents.SOURCE_DOCUMENT_ID " +
				"WHERE task_output_parameters.PARAMETER <> 'PROCESS_OUTPUT'");
		ResultSet toolRows = null;

		try {
			toolRows = selectStmt.executeQuery();

			while (toolRows.next()) {
				String id = toolRows.getString(1);
				ToolStatistics stats;

				if (!tools.containsKey(id)) {
					stats = new ToolStatistics(id);

					tools.put(id, stats);
				}
				else
					stats = tools.get(id);

				byte[] data = inflateBytes(toolRows.getBytes(2));

				if (data.length < stats.minOutputSize)
					stats.minOutputSize = (double) data.length;

				if (data.length > stats.maxOutputSize)
					stats.maxOutputSize = (double) data.length;

				stats.totalOutputSize += data.length;
				stats.numOutputs += 1;
			}
		}
		finally {
			if (toolRows != null)
				toolRows.close();

			selectStmt.close();
		}
	}

	private static byte[] inflateBytes(byte[] compressed) throws IOException
	{
		ByteArrayInputStream inBytes = new ByteArrayInputStream(compressed);
		BufferedInputStream inBuffer = new BufferedInputStream(new GZIPInputStream(inBytes));
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

		try {
			int bytesRead;
			byte[] readBuffer = new byte[8192];

			while ((bytesRead = inBuffer.read(readBuffer)) > 0)
				outBytes.write(readBuffer, 0, bytesRead);
		}
		finally {
			inBuffer.close();
		}

		return outBytes.toByteArray();
	}
}
