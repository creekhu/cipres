/*
 * TaskLogMessage.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.core.shared.TaskRunStage;


/**
 *
 * @author Paul Hoover
 *
 */
public class TaskLogMessage extends Row implements Comparable<TaskLogMessage> {

	// nested classes


	/**
	 *
	 */
	private class GetNextMessageIndexOp implements RowOperation {

		// public methods


		/**
		 *
		 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
		 * @throws IOException
		 * @throws SQLException
		 */
		@Override
		public void execute(Connection dbConn) throws IOException, SQLException
		{
			int index = getNextIndex(dbConn);

			m_messageIndex.setValue(index);
		}


		// private methods


		/**
		 *
		 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
		 * @return
		 * @throws IOException
		 * @throws SQLException
		 */
		private int getNextIndex(Connection dbConn) throws IOException, SQLException
		{
			PreparedStatement selectStmt = dbConn.prepareStatement("SELECT MAX(MESSAGE_INDEX) FROM " + TABLE_NAME + " WHERE TASK_ID = ?");
			ResultSet indexRow = null;

			try {
				m_taskId.setParameter(selectStmt, 1);

				indexRow = selectStmt.executeQuery();

				if (!indexRow.next())
					return 0;

				return indexRow.getInt(1) + 1;
			}
			finally {
				if (indexRow != null)
					indexRow.close();

				selectStmt.close();
			}
		}
	}


	// data fields


	private static final String TABLE_NAME = "task_log_messages";
	private final Column<Long> m_taskId = new LongColumn("TASK_ID", false);
	private final Column<Integer> m_messageIndex = new IntegerColumn("MESSAGE_INDEX", false);
	private final Column<Date> m_creationDate = new DateColumn("CREATION_DATE", false);
	private final Column<String> m_stage = new StringColumn("STAGE", false, 20);
	private final Column<Boolean> m_error = new BooleanColumn("ERROR", false);
	private final StreamColumn<String> m_message = new TextColumn("MESSAGE", true, this);
	private String m_jobHandle;


	// constructors


	public TaskLogMessage(Task owner)
	{
		this();

		if (owner.isNew())
			throw new WorkbenchException("Can't create a log message for an unpersisted task");

		setError(false);
		setTaskId(owner.getTaskId());
		setJobHandle(owner.getJobHandle());

		m_creationDate.setValue(Calendar.getInstance().getTime());
	}

	public TaskLogMessage(long taskId, int messageIndex) throws IOException, SQLException
	{
		this();

		m_taskId.assignValue(taskId);
		m_messageIndex.assignValue(messageIndex);

		load();
	}

	TaskLogMessage(Connection dbConn, long taskId, int messageIndex) throws IOException, SQLException
	{
		this();

		m_taskId.assignValue(taskId);
		m_messageIndex.assignValue(messageIndex);

		load(dbConn);
	}

	private TaskLogMessage()
	{
		super(TABLE_NAME);

		construct(m_creationDate, m_stage, m_error, m_message);
	}


	// public methods


	public long getTaskId()
	{
		return m_taskId.getValue();
	}

	public int getMessageIndex()
	{
		return m_messageIndex.getValue();
	}

	public Date getCreationDate()
	{
		return m_creationDate.getValue();
	}

	public TaskRunStage getStage()
	{
		return TaskRunStage.valueOf(m_stage.getValue());
	}

	public void setStage(TaskRunStage stage)
	{
		m_stage.setValue(stage.toString());
	}

	public boolean isError()
	{
		return m_error.getValue();
	}

	public void setError(Boolean error)
	{
		m_error.setValue(error);
	}

	public String getMessage() throws IOException, SQLException
	{
		return m_message.getValue();
	}

	public Reader getMessageAsStream() throws IOException, SQLException
	{
		return new InputStreamReader(m_message.getValueAsStream());
	}

	public void setMessage(String message)
	{
		m_message.setValue(message);
	}

	@Override
	public boolean isNew()
	{
		return m_taskId.isNull() || m_messageIndex.isNull();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof TaskLogMessage == false)
			return false;

		TaskLogMessage otherMessage = (TaskLogMessage) other;

		if (isNew() || otherMessage.isNew())
			return false;

		return getTaskId() == otherMessage.getTaskId() && getMessageIndex() == otherMessage.getMessageIndex();
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + (new Long(getTaskId())).hashCode();

		return 31 * hash + (new Integer(getMessageIndex())).hashCode();
	}

	@Override
	public int compareTo(TaskLogMessage other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		int comparison = (int) (getTaskId() - other.getTaskId());

		if (comparison != 0)
			return comparison;

		return (int) (getMessageIndex() - other.getMessageIndex());
	}

	@Override
	public String toString()
	{
		try {
			StringBuilder messageBuilder = new StringBuilder();
			DateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

			messageBuilder.append(dateFormatter.format(m_creationDate.getValue()));
			messageBuilder.append(" > ");
			messageBuilder.append(m_stage.getValue());
			messageBuilder.append(m_error.getValue() ? " : ERROR : " : " : SUCCESS : ");
			messageBuilder.append(m_jobHandle);
			messageBuilder.append(" : ");
			messageBuilder.append(m_message.getValue());
			messageBuilder.append("\n");

			return messageBuilder.toString();
		}
		catch (Exception err) {
			throw new RuntimeException(err);
		}
	}


	// package methods


	void setTaskId(Long taskId)
	{
		m_taskId.setValue(taskId);
	}

	void setJobHandle(String jobHandle)
	{
		m_jobHandle = jobHandle;
	}

	@Override
	Criterion getKey()
	{
		return new CompositeKey(m_taskId, m_messageIndex);
	}

	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_jobHandle = getJobHandle(dbConn);
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		super.delete(dbConn);

		m_taskId.reset();
		m_messageIndex.reset();
	}

	static List<TaskLogMessage> findLogMessages(long taskId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet messageRows = null;

		try {
			selectStmt = dbConn.prepareStatement("SELECT MESSAGE_INDEX FROM " + TABLE_NAME + " WHERE TASK_ID = ? ORDER BY MESSAGE_INDEX");

			selectStmt.setLong(1, taskId);

			messageRows = selectStmt.executeQuery();

			List<TaskLogMessage> messages = new ArrayList<TaskLogMessage>();

			while (messageRows.next())
				messages.add(new TaskLogMessage(dbConn, taskId, messageRows.getInt(1)));

			return messages;
		}
		finally {
			if (messageRows != null)
				messageRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}


	// protected methods


	/**
	 *
	 */
	@Override
	protected void pushInsertOps()
	{
		List<Column<?>> allColumns = new ArrayList<Column<?>>();

		allColumns.add(m_taskId);
		allColumns.add(m_messageIndex);
		allColumns.addAll(m_columns);

		m_opQueue.push(new InsertOp(m_tableName, allColumns));
		m_opQueue.push(new GetNextMessageIndexOp());
	}


	// private methods


	private String getJobHandle(Connection dbConn) throws IOException, SQLException
	{
		Criterion key = new LongCriterion("TASK_ID", m_taskId.getValue());
		Column<String> jobHandle = new StringColumn("JOBHANDLE", true, 255);

		(new SelectOp("tasks", key, jobHandle)).execute(dbConn);

		return jobHandle.getValue();
	}
}
