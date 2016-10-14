
/*
 * StatisticsEvent.java
 */
package org.ngbw.sdk.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class StatisticsEvent extends GeneratedKeyRow implements Comparable<StatisticsEvent>
{
	private static final Log log = LogFactory.getLog(StatisticsEvent.class.getName());

	private static final String TABLE_NAME   = "job_events";
	private static final String KEY_NAME = "SE_ID";

	// false in 3rd column = NOT NULLABLE, true in optional 4th column means truncate string if too long.
	private final Column<String>	m_jobhandle		= new StringColumn("JOBHANDLE", false, 255);
	private final Column<Date>	m_eventDate	= new DateColumn("EVENT_DATE", false);
	private final Column<String>	m_taskStage		= new StringColumn("TASK_STAGE", true, 100);
	private final Column<String>	m_eventName		= new StringColumn("NAME", false, 100);
	private final StreamColumn<String> m_eventValue = new TextColumn("VALUE", true, this);

	// constructors

	/* Create new StatisticsEvent */
	public StatisticsEvent(String jobhandle) throws IOException, SQLException
	{
		this();
		m_jobhandle.assignValue(jobhandle);
		m_eventDate.assignValue(new Date());
	}

	private StatisticsEvent()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_jobhandle, m_eventDate, m_taskStage, m_eventName, m_eventValue);
	}

	// public methods
	public long getId()
	{
		return m_key.getValue();
	}

	public String getJobhandle()
	{
		return m_jobhandle.getValue();
	}

	public Date getEventDate()
	{
		return m_eventDate.getValue();
	}

	public String getTaskStage()
	{
		return m_taskStage.getValue();
	}
	public void setTaskStage(String s)
	{
		m_taskStage.setValue(s);
	}

	public String getEventName()
	{
		return m_eventName.getValue();
	}

	public void setEventName(String s)
	{
		m_eventName.setValue(s);
	}

	public String getEventValue()
	{
		return m_eventValue.getValue();
	}

	public void setEventValue(String s)
	{
		m_eventValue.setValue(s);
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

		if (other instanceof StatisticsEvent == false)
			return false;

		StatisticsEvent otherRow = (StatisticsEvent) other;

		// an object that hasn't been persisted can only be equal to itself
		if (isNew() || otherRow.isNew())
			return false;

		return getId() == otherRow.getId();
	}

	/**
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return (new Long(getId())).hashCode();
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(StatisticsEvent other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return  0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getId() - other.getId());
	}

}

