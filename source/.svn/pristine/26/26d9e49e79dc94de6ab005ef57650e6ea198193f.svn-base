/*
 * ExampleRow.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;


/**
 *
 * Here's an example of a Java object that represents a row from a
 * database table. It's pretty basic, the only columns are a primary key,
 * a date value, and a foreign key to the tasks table.
 *
 * @author Paul, Luci, Juliane
 *
 */
public class ExampleRow extends GeneratedKeyRow implements Comparable<ExampleRow> {

	// data fields


	private static final String TABLE_NAME = "example_table";
	private static final String KEY_NAME = "EXAMPLE_ID";
	private final Column<Long> m_taskId = new LongColumn("TASK_ID", false);
	private final Column<Date> m_actualDateTime = new DateColumn("ACTUAL_DATE_TIME", false);


	// constructors


	public ExampleRow() throws IOException, SQLException
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_taskId, m_actualDateTime);
	}

	/**
	 * @param exampleId
	 * @throws IOException
	 * @throws SQLException
	 */
	public ExampleRow(long exampleId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(exampleId);

		load();
	}

	/**
	 * @param dbConn
	 * @param exampleId
	 * @throws IOException
	 * @throws SQLException
	 */
	ExampleRow(Connection dbConn, long exampleId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(exampleId);

		load(dbConn);
	}


	// public methods


	/**
	 * @return
	 */
	public long getExampleId()
	{
		return m_key.getValue();
	}

	/**
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
	 * @param actualDateTime
	 */

	public void getActualDateTime(Date actualDateTime)
	{
		m_actualDateTime.getValue();
	}

	/**
	 * @param actualDateTime
	 */

	public void setActualDateTime(Date actualDateTime)
	{
		m_actualDateTime.setValue(actualDateTime);
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
	 * @param task
	 */
	public void setTask(Task task)
	{
		if (task != null && !task.isNew())
			setTaskId(task.getTaskId());
		else
			setTaskId(null);
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

		if (other instanceof ExampleRow == false)
			return false;

		ExampleRow otherRow = (ExampleRow) other;

		// an object that hasn't been persisted can only be equal to itself
		if (isNew() || otherRow.isNew())
			return false;

		return getExampleId() == otherRow.getExampleId();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return (new Long(getExampleId())).hashCode();
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(ExampleRow other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getExampleId() - other.getExampleId());
	}
}
