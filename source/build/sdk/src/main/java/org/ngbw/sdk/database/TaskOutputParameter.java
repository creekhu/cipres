/*
 * TaskOutputParameter.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ngbw.sdk.WorkbenchException;


/**
 *
 * @author Paul Hoover
 *
 */
class TaskOutputParameter extends GeneratedKeyRow implements Comparable<TaskOutputParameter> {

	// data fields


	private static final String TABLE_NAME = "task_output_parameters";
	private static final String KEY_NAME = "OUTPUT_ID";
	private final Column<Long> m_taskId = new LongColumn("TASK_ID", false);
	private final Column<String> m_parameter = new StringColumn("PARAMETER", false, 255);
	private final Column<Boolean> m_intermediate = new BooleanColumn("INTERMEDIATE", false);


	// constructors


	TaskOutputParameter()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_taskId, m_parameter, m_intermediate);
	}

	TaskOutputParameter(long taskId, String parameter, boolean intermediate)
	{
		this();

		setTaskId(taskId);
		setParameter(parameter);
		setIntermediate(intermediate);
	}

	TaskOutputParameter(long outputId) throws IOException, SQLException
	{
		this();

		m_key.setValue(outputId);

		load();
	}

	TaskOutputParameter(Connection dbConn, long outputId) throws IOException, SQLException
	{
		this();

		m_key.setValue(outputId);

		load(dbConn);
	}


	// public methods


	/**
	 *
	 * @return
	 */
	public long getOutputId()
	{
		return m_key.getValue();
	}

	/**
	 *
	 * @return
	 */
	public long getTaskId()
	{
		return m_taskId.getValue();
	}

	/**
	 * @param taskId
	 */
	public void setTaskId(Long taskId)
	{
		m_taskId.setValue(taskId);
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public Task getTask() throws IOException, SQLException
	{
		if (m_taskId.isNull())
			return null;

		return new Task(m_taskId.getValue());
	}

	/**
	 *
	 * @return
	 */
	public String getParameter()
	{
		return m_parameter.getValue();
	}

	/**
	 *
	 * @param parameter
	 */
	public void setParameter(String parameter)
	{
		m_parameter.setValue(parameter);
	}

	/**
	 *
	 * @return
	 */
	public boolean getIntermediate()
	{
		return m_intermediate.getValue();
	}

	/**
	 *
	 * @param intermediate
	 */
	public void setIntermediate(boolean intermediate)
	{
		m_intermediate.setValue(intermediate);
	}

	/**
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof TaskOutputParameter == false)
			return false;

		TaskOutputParameter otherRow = (TaskOutputParameter) other;

		// an object that hasn't been persisted can only be equal to itself
		if (isNew() || otherRow.isNew())
			return false;

		return getOutputId() == otherRow.getOutputId();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return (new Long(getOutputId())).hashCode();
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(TaskOutputParameter other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int)(getOutputId() - other.getOutputId());
	}


	// package methods


	static Map<String, List<TaskOutputSourceDocument>> findTaskOutput(long taskId) throws IOException, SQLException
	{
		return findTaskOutput(taskId, false);
	}

	static Map<String, List<TaskOutputSourceDocument>> findIntermediateTaskOutput(long taskId) throws IOException, SQLException
	{
		return findTaskOutput(taskId, true);
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		delete(dbConn, getKey());

		m_key.reset();
	}

	static void delete(Connection dbConn, long outputId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion(KEY_NAME, outputId);

		delete(dbConn, key);
	}


	// private methods


	static void delete(Connection dbConn, Criterion outputKey) throws IOException, SQLException
	{
		deleteOutputDocs(dbConn, outputKey);

		(new DeleteOp("task_output_parameters", outputKey)).execute(dbConn);
	}

	private static Map<String, List<TaskOutputSourceDocument>> findTaskOutput(long taskId, boolean intermediate) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet paramRows = null;

		try {
			selectStmt = dbConn.prepareStatement("SELECT OUTPUT_ID, PARAMETER FROM task_output_parameters WHERE TASK_ID = ? AND INTERMEDIATE = ?");

			selectStmt.setLong(1, taskId);
			selectStmt.setBoolean(2, intermediate);

			paramRows = selectStmt.executeQuery();

			Map<String, List<TaskOutputSourceDocument>> outputMap = new TreeMap<String, List<TaskOutputSourceDocument>>();

			while (paramRows.next()) {
				long outputId = paramRows.getLong(1);
				String outputParam = paramRows.getString(2);
				List<TaskOutputSourceDocument> outputDocs = TaskOutputSourceDocument.findOutputDocuments(dbConn, outputId);

				outputMap.put(outputParam, outputDocs);
			}

			return outputMap;
		}
		finally {
			if (paramRows != null)
				paramRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	private static void deleteOutputDocs(Connection dbConn, Criterion outputKey) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT OUTPUT_DOCUMENT_ID FROM task_output_source_documents WHERE ");

		stmtBuilder.append(outputKey.getPhrase());

		PreparedStatement selectStmt = dbConn.prepareStatement(stmtBuilder.toString());
		ResultSet outputRows = null;

		try {
			outputKey.setParameter(selectStmt, 1);

			outputRows = selectStmt.executeQuery();

			while (outputRows.next())
				TaskOutputSourceDocument.delete(dbConn, outputRows.getLong(1));
		}
		finally {
			if (outputRows != null)
				outputRows.close();

			selectStmt.close();
		}
	}
}
