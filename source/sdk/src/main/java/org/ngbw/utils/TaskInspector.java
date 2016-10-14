/*
 * TaskInspector.java
 */
package org.ngbw.utils;


import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.TaskLogMessage;
import org.ngbw.sdk.database.TaskOutputSourceDocument;


/**
 * 
 * @author Paul Hoover
 *
 */
public class TaskInspector {

	private static final String INDENT = "   ";


	public static void inspect(long id) throws IOException, SQLException
	{
		Task inspected = new Task(id);

		

		inspect(inspected);
	}

	public static void inspect(Task inspected) throws IOException, SQLException
	{
		System.out.println("Label:  " + inspected.getLabel().replaceAll("\\s+", " "));
		System.out.println("Key:    " + inspected.getTaskId());
		System.out.println("Date:   " + inspected.getCreationDate());
		System.out.println("Tool:   " + inspected.getToolId());
		System.out.println("Handle: " + inspected.getJobHandle());
		System.out.println("\nTool Parameters:");

		for (Iterator<Map.Entry<String, String>> params = inspected.toolParameters().entrySet().iterator() ; params.hasNext() ; ) {
			Map.Entry<String, String> entry = params.next();

			System.out.println(INDENT + entry.getKey() + " = " + entry.getValue());
		}

		System.out.println("\nLog Messages:");

		for (Iterator<TaskLogMessage> elements = inspected.logMessages().iterator() ; elements.hasNext() ; )
			System.out.println(INDENT + elements.next());

		System.out.println("Input parameters:");

		for (Iterator<Map.Entry<String, List<TaskInputSourceDocument>>> entries = inspected.input().entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<String, List<TaskInputSourceDocument>> entry = entries.next();

			System.out.println(INDENT + entry.getKey() + ":");

			for (Iterator<TaskInputSourceDocument> elements = entry.getValue().iterator() ; elements.hasNext() ; ) {
				TaskInputSourceDocument doc = elements.next();

				saveDocument(doc.getName(), doc.getDataAsStream());

				System.out.println(INDENT + INDENT + doc.getName());
			}

			System.out.println();
		}

		System.out.println("Output Parameters:");

		for (Iterator<Map.Entry<String, List<TaskOutputSourceDocument>>> entries = inspected.output().entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<String, List<TaskOutputSourceDocument>> entry = entries.next();

			System.out.println(INDENT + entry.getKey() + ":");

			for (Iterator<TaskOutputSourceDocument> elements = entry.getValue().iterator() ; elements.hasNext() ; ) {
				TaskOutputSourceDocument doc = elements.next();

				saveDocument(doc.getName(), doc.getDataAsStream());

				System.out.println(INDENT + INDENT + doc.getName());
			}

			System.out.println();
		}

		User user = inspected.getUser();
		System.out.printf("Uid=%s, name=%s %s, email=%s, phone=%s\n",
			user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber());
	}

	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
				throw new Exception("usage: TaskInspector id | handle");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			long id;

			try {
				id = Long.parseLong(args[0]);
			}
			catch (NumberFormatException formatErr) {
				id = findTaskIdByHandle(args[0]);

				if (id == 0) {
					System.err.println("Couldn't find a task with that handle");

					System.exit(0);
				}
			}

			inspect(id);
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}


	private static long findTaskIdByHandle(String handle) throws SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet taskRow = null;

		try {
			selectStmt = dbConn.prepareStatement("SELECT TASK_ID FROM tasks WHERE JOBHANDLE = ?");

			selectStmt.setString(1, handle);

			taskRow = selectStmt.executeQuery();

			if (!taskRow.next())
				return 0;

			return taskRow.getLong(1);
		}
		finally {
			if (taskRow != null)
				taskRow.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	private static void saveDocument(String fileName, InputStream inStream) throws IOException
	{
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileName));

		try {
			byte[] readBuffer = new byte[8192];
			int bytesRead;

			while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) >= 0)
				outStream.write(readBuffer, 0, bytesRead);
		}
		finally {
			inStream.close();
			outStream.close();
		}
	}
}
