/*
 * Sso.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ngbw.sdk.database.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author Terri Liebowitz Schwartz
 *
 */
public class Sso extends GeneratedKeyRow implements Comparable<Sso> {
	private static final Log log = LogFactory.getLog(Sso.class.getName());
	private static String accountingPeriodStart = null;

	private static final String TABLE_NAME = "sso";
	private static final String KEY_NAME = "SSO_ID"; // SSO_ID is the primary, auto inc key

	private final Column<Long> m_userId = new LongColumn("USER_ID", false);
	private final Column<String> m_ssoUsername = new StringColumn("SSO_USERNAME", false, 255);

	// constructors
	public Sso()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_userId, m_ssoUsername);
	}

	public Sso(long ssoId) throws IOException, SQLException
	{
		this();
		m_key.assignValue(ssoId);
		load();
	}

	Sso(Connection dbConn, long ssoId) throws IOException, SQLException
	{
		this();
		m_key.assignValue(ssoId);
		load(dbConn);
	}


	// public methods

	public long getSsoId()
	{
		return m_key.getValue();
	}

	public long getUserId()
	{
		return m_userId.getValue();
	}

	public void setUserId(long userId)
	{
		m_userId.setValue(userId);
	}

	public String getSsoUsername()
	{
		return m_ssoUsername.getValue();
	}

	public void setSsoUsername(String username)
	{
		m_ssoUsername.setValue(username);
	}

	public User getUser() throws IOException, SQLException
	{
		return new User(m_userId.getValue());
	}


	public static List<Sso> findAllSso() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet userRows = null;

		try {
			selectStmt = dbConn.prepareStatement("SELECT " + KEY_NAME + " FROM " + TABLE_NAME);

			userRows = selectStmt.executeQuery();

			List<Sso> users = new ArrayList<Sso>();

			while (userRows.next())
				users.add(new Sso(dbConn, userRows.getLong(1)));

			return users;
		}
		finally {
			if (userRows != null)
				userRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	public static Sso findSsoBySsoUsername(String username) throws IOException, SQLException
	{
		return findSso(new StringCriterion("SSO_USERNAME", username));
	}

	public static void addAccount(User user, String iplantName) throws IOException, SQLException
	{
		log.debug("Adding linkage for iplant user " + iplantName + " and cipres user " + user.getUsername());
		Sso sso  = new Sso();
		sso.setSsoUsername(iplantName);
		sso.setUserId(user.getUserId());

		log.debug("sso username is " + sso.getSsoUsername());
		sso.save();
	}




	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof Sso == false)
			return false;

		Sso otherUser = (Sso) other;

		if (isNew() || otherUser.isNew())
			return false;

		return getSsoId() == otherUser.getSsoId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getSsoId())).hashCode();
	}

	@Override
	public int compareTo(Sso other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getSsoId() - other.getSsoId());
	}


	// private methods


	private static Sso findSso(Criterion key) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			Column<Long> ssoId = new LongColumn(KEY_NAME, false);

			(new SelectOp(TABLE_NAME, key, ssoId, false)).execute(dbConn);

			if (ssoId.isNull())
				return null;

			return new Sso(dbConn, ssoId.getValue());
		}
		finally {
			dbConn.close();
		}
	}

}
