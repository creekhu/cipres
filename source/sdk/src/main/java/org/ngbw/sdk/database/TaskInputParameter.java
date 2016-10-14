/*
 * TaskInputParameter.java
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
class TaskInputParameter extends GeneratedKeyRow implements Comparable<TaskInputParameter> {

	// data fields


	private static final String TABLE_NAME = "task_input_parameters";
	private static final String KEY_NAME = "INPUT_ID";
	private final Column<Long> m_taskId = new LongColumn("TASK_ID", false);
	private final Column<String> m_parameter = new StringColumn("PARAMETER", false, 255);


	// constructors


	TaskInputParameter()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_taskId, m_parameter);
	}

	TaskInputParameter(long taskId, String parameter)
	{
		this();

		setTaskId(taskId);
		setParameter(parameter);
	}

	TaskInputParameter(long inputId) throws IOException, SQLException
	{
		this();

		m_key.setValue(inputId);

		load();
	}

	TaskInputParameter(Connection dbConn, long inputId) throws IOException, SQLException
	{
		this();

		m_key.setValue(inputId);

		load(dbConn);
	}


	// public methods


	/**
	 *
	 * @return
	 */
	public long getInputId()
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

		if (other instanceof TaskInputParameter == false)
			return false;

		TaskInputParameter otherRow = (TaskInputParameter) other;

		// an object that hasn't been persisted can only be equal to itself
		if (isNew() || otherRow.isNew())
			return false;

		return getInputId() == otherRow.getInputId();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return (new Long(getInputId())).hashCode();
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(TaskInputParameter other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int)(getInputId() - other.getInputId());
	}


	// package methods


	static Map<String, List<TaskInputSourceDocument>> findTaskInput(long taskId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet paramRows = null;

		try {
			selectStmt = dbConn.prepareStatement("SELECT INPUT_ID, PARAMETER FROM task_input_parameters WHERE TASK_ID = ?");

			selectStmt.setLong(1, taskId);

			paramRows = selectStmt.executeQuery();

			Map<String, List<TaskInputSourceDocument>> inputMap = new TreeMap<String, List<TaskInputSourceDocument>>();

			while (paramRows.next()) {
				long inputId = paramRows.getLong(1);
				String inputParam = paramRows.getString(2);
				List<TaskInputSourceDocument> outputDocs = TaskInputSourceDocument.findInputDocuments(dbConn, inputId);

				inputMap.put(inputParam, outputDocs);
			}

			return inputMap;
		}
		finally {
			if (paramRows != null)
				paramRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		delete(dbConn, getKey());

		m_key.reset();
	}

	static void delete(Connection dbConn, long inputId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion(KEY_NAME, inputId);

		delete(dbConn, key);
	}


	// private methods


	static void delete(Connection dbConn, Criterion inputKey) throws IOException, SQLException
	{
		deleteInputDocs(dbConn, inputKey);

		(new DeleteOp("task_input_parameters", inputKey)).execute(dbConn);
	}

	private static void deleteInputDocs(Connection dbConn, Criterion inputKey) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT INPUT_DOCUMENT_ID FROM task_input_source_documents WHERE ");

		stmtBuilder.append(inputKey.getPhrase());

		PreparedStatement selectStmt = dbConn.prepareStatement(stmtBuilder.toString());
		ResultSet inputRows = null;

		try {
			inputKey.setParameter(selectStmt, 1);

			inputRows = selectStmt.executeQuery();

			while (inputRows.next())
				TaskInputSourceDocument.delete(dbConn, inputRows.getLong(1));
		}
		finally {
			if (inputRows != null)
				inputRows.close();

			selectStmt.close();
		}
	}
}
