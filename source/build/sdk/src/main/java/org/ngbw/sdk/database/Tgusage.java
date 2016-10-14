/*
 * Tgusage.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 *
 * @author Terri Liebowitz Schwartz
 *
 */
public class Tgusage extends GeneratedKeyRow implements Comparable<Tgusage>
{
	private static final Log log = LogFactory.getLog(Tgusage.class.getName());

	/**
	 *
	 */

	private static final String TABLE_NAME = "tgusage";
	private static final String KEY_NAME = "TGUSAGE_ID";

	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");


	// not-null = false, nullable = true
	private final Column<String> m_resource = new StringColumn("RESOURCE", false, 100);
	private final Column<String> m_jobid = new StringColumn("JOBID", false, 100);
	private final Column<String> m_full_jobid = new StringColumn("FULL_JOBID", false, 255);
	private final Column<String> m_full_resource = new StringColumn("FULL_RESOURCE", false, 255);
	private final Column<Integer> m_charge = new IntegerColumn("CHARGE", false);
	private final Column<Date> m_start_time = new DateColumn("START_TIME", false);
	private final Column<Date> m_end_time = new DateColumn("END_TIME", false);
	private final Column<Date> m_submit_time = new DateColumn("SUBMIT_TIME", false);
	private final Column<String> m_charge_number = new StringColumn("CHARGE_NUMBER", false, 100);
	private final Column<String> m_wall_hrs = new StringColumn("WALL_HRS", false, 10);
	private final Column<Integer> m_su = new IntegerColumn("SU", false);
	private final Column<Integer> m_nodecount = new IntegerColumn("NODECOUNT", false);
	private final Column<Integer> m_processors = new IntegerColumn("PROCESSORS", false);
	private final Column<String> m_queue = new StringColumn("QUEUE", false, 100);


	// constructors
	public Tgusage()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_resource, m_jobid, m_full_jobid, m_full_resource, m_charge,
				m_start_time, m_end_time, m_submit_time, m_charge_number, m_wall_hrs,
				m_su, m_nodecount, m_processors, m_queue);
	}

	// retrieve a record based on it's id and populate a Tgusage object.
	public Tgusage(long id) throws IOException, SQLException
	{
		this();

		m_key.assignValue(id);
		load();
	}

	// retrieve a record based on it's id and populate a Tgusage object.
	Tgusage(Connection dbConn, long id) throws IOException, SQLException
	{
		this();

		m_key.assignValue(id);
		load(dbConn);
	}

	/**
		Can throw field index out of bounds or parsing exceptions
	*/
	public Tgusage(String fields[]) throws Exception
	{
		this();

		// We match Tgusage rows with Statistic and maybe RunningTask rows based on the resource and jobid
		// so we need to make sure the resource names and the job id format that we get from the tgusage
		// database is mapped to what the cipres tables use.
		setResource(toCipresResource(fields[0]));
		setFullResource(fields[0]);

		setJobid(toCipresJobid(fields[1]));
		setFullJobid(fields[1]);

		setCharge(toInt(fields[2]));

		// need to convert 2010-07-03 08:14:58-07 type string to a Date object
		setStartTime(toDate(fields[3]));
		setEndTime(toDate(fields[4]));
		setSubmitTime(toDate(fields[5]));

		setChargeNumber(fields[6]);
		setWallHrs(fields[7]);

		setSu(toInt(fields[8]));
		setNodeCount(toInt(fields[9]));
		setProcessors(toInt(fields[10]));

		setQueue(fields[11]);
	}

	int toInt(String str)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	// Job Ids on the hosts we use tend to have the format number.host.  We just store the numeric part
	// since systems seem to change the host part on occassion and remote qstat, etc commands seem to
	// accept just the numeric part.
	private String toCipresJobid(String s)
	{
		int dot = s.indexOf('.');
		return (dot == -1) ? s : s.substring(0, dot);
	}

	// Looks like resource name in tgusage records happens to be fully qualified host name
	// and fortunately for teragrid machines we use just the unqualifed hostname, lowercase,
	// so we should match up like this.
	private String toCipresResource(String s)
	{
		int dot = s.indexOf('.');
		return (dot == -1) ? s : s.substring(0, dot);
	}

	private Date toDate(String s) throws java.text.ParseException
	{
		return dateFormat.parse(s);
	}



	// public methods

	public long getTgusageId()
	{
		return m_key.getValue();
	}

	public String getResource() { return m_resource.getValue(); }
	public void setResource(String i) { m_resource.setValue(i); }

	public String getJobid() { return m_jobid.getValue(); }
	public void setJobid(String i) { m_jobid.setValue(i); }

	public String getFullJobid() { return m_full_jobid.getValue(); }
	public void setFullJobid(String i) { m_full_jobid.setValue(i); }

	public String getFullResource() { return m_full_resource.getValue(); }
	public void setFullResource(String i) { m_full_resource.setValue(i); }

	public int getCharge() { return m_charge.getValue(); }
	public void setCharge(int i) { m_charge.setValue(i); }

	public Date getStartTime() { return m_start_time.getValue(); }
	public void setStartTime(Date i) { m_start_time.setValue(i); }

	public Date getEndTime() { return m_end_time.getValue(); }
	public void setEndTime(Date i) { m_end_time.setValue(i); }

	public Date getSubmitTime() { return m_submit_time.getValue(); }
	public void setSubmitTime(Date i) { m_submit_time.setValue(i); }

	public String getChargeNumber() { return m_charge_number.getValue(); }
	public void setChargeNumber(String i) { m_charge_number.setValue(i); }

	public String getWallHhrs() { return m_wall_hrs.getValue(); }
	public void setWallHrs(String i) { m_wall_hrs.setValue(i); }

	public int getSu() { return m_su.getValue(); }
	public void setSu(int i) { m_su.setValue(i); }

	public int getNodeCount() { return m_nodecount.getValue(); }
	public void setNodeCount(int i) { m_nodecount.setValue(i); }

	public int getProcessors() { return m_processors.getValue(); }
	public void setProcessors(int i) { m_processors.setValue(i); }

	public String getQueue() { return m_queue.getValue(); }
	public void setQueue(String i) { m_queue.setValue(i); }


	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof Tgusage == false)
			return false;

		Tgusage otherTgusage = (Tgusage) other;

		if (isNew() || otherTgusage.isNew())
			return false;

		return getTgusageId() == otherTgusage.getTgusageId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getTgusageId())).hashCode();
	}

	@Override
	public int compareTo(Tgusage other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getTgusageId() - other.getTgusageId());
	}


	// package methods


	static void delete(Connection dbConn, long tgusage_id) throws IOException, SQLException
	{
		LongCriterion usageId = new LongCriterion(KEY_NAME, tgusage_id);

		(new DeleteOp(TABLE_NAME, usageId)).execute(dbConn);
	}

	public static List<Tgusage> findTgusage(String resource, String jobid) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE ");
		stmtBuilder.append(" RESOURCE = ? AND JOBID = ?");

		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet tgusageRows = null;

		try {
			selectStmt = dbConn.prepareStatement(stmtBuilder.toString());
			selectStmt.setString(1, resource);
			selectStmt.setString(2, jobid);

			tgusageRows = selectStmt.executeQuery();
			List<Tgusage> retval = new ArrayList<Tgusage>();
			while (tgusageRows.next())
				retval.add(new Tgusage(dbConn, tgusageRows.getLong(1)));

			return retval;
		}
		finally {
			if (tgusageRows != null)
				tgusageRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}




	static List<Tgusage> findTgusage(Criterion key) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE ");

		stmtBuilder.append(key.getPhrase());

		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet tgusageRows = null;

		try {
			selectStmt = dbConn.prepareStatement(stmtBuilder.toString());

			key.setParameter(selectStmt, 1);

			tgusageRows = selectStmt.executeQuery();

			List<Tgusage> retval = new ArrayList<Tgusage>();

			while (tgusageRows.next())
				retval.add(new Tgusage(dbConn, tgusageRows.getLong(1)));

			return retval;
		}
		finally {
			if (tgusageRows != null)
				tgusageRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}



	// protected methods


	// private methods

}
