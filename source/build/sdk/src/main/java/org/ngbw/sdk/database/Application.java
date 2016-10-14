/*
 * Application.java
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
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.ValidationException;


public class Application extends Row implements Comparable<Application> {
	private static final Log log = LogFactory.getLog(Application.class.getName());

	public static final String DIRECT = "DIRECT";
	public static final String UMBRELLA = "UMBRELLA";


	private static final String TABLE_NAME = "applications";

	// 2nd argument is false if field is not nullable, true if null is allowed.
	private final Column<String> m_appId = new StringColumn("APP_ID", true, 255);
	private final Column<String> m_name = new StringColumn("NAME", false, 30);
	private final Column<String> m_longName = new StringColumn("LONGNAME", false, 100);
	private final Column<String> m_authType = new StringColumn("AUTH_TYPE", false, 30);
	private final Column<Long> m_authUserId = new LongColumn("AUTH_USER_ID", true);
	private final Column<String> m_websiteUrl = new StringColumn("WEBSITE_URL", true, 255);
	private final Column<String> m_comment = new StringColumn("COMMENT", true, 255);
	private final Column<Boolean> m_active = new BooleanColumn("ACTIVE", false);
	private final Column<Date> m_dateCreated = new DateColumn("DATE_CREATED", false);
	private PreferenceMap m_preferences;


	private boolean m_isNew = true;


	/*
		Find existing record in the db by it's primary key, "name"
	*/
	public static Application find(String name) throws IOException, SQLException
	{
		return findApplication(new StringCriterion("NAME", name));
	}

	/*
		Find existing record by appId, which is a unique key, but not the primary.
	*/
	public static Application findKey(String id) throws IOException, SQLException
	{
		return findApplication(new StringCriterion("APP_ID", id));
	}


	// constructors


	private Application()
	{
		super(TABLE_NAME);

		construct(m_appId, m_longName, m_authType, m_authUserId, m_websiteUrl, m_comment, m_active, m_dateCreated);
	}

	// Create a new record, doesn't exist in db until this object is saved.
	public Application(String name)
	{
		this();
		m_name.assignValue(name);
		m_dateCreated.assignValue(new Date());
	}

	Application(Connection dbConn, String name) throws IOException, SQLException
	{
		this();
		m_name.assignValue(name);
		m_isNew = false;
		load(dbConn);
	}

	/*
		Only allow letters, numbers and underscores
		Call this in save since that's where field length ValidationExceptions
		will also be thrown.
	*/
	private void validateName(String name) throws ValidationException
	{
		if (!name.matches("[a-zA-Z0-9_]+"))
		{
			throw new ValidationException("Only letters, numbers and underscore are allowed in application name");
		}
	}


	// public methods


	public String getAppId()
	{
		return m_appId.getValue();
	}
	public void setAppId(String id)
	{
		m_appId.setValue(id);
	}

	public String getName() { return m_name.getValue(); }

	public String getLongName() { return m_longName.getValue(); }
	public void setLongName(String name) { m_longName.setValue(name); }

	public String getAuthType() { return m_authType.getValue(); }
	public void setAuthType(String a) { m_authType.setValue(a); }

	public long  getAuthUserId() { return m_authUserId.getValue(); }
	public void setAuthUserId(long a) { m_authUserId.setValue(a); }

	public String getWebsiteUrl() { return m_websiteUrl.getValue(); }
	public void setWebsiteUrl(String websiteUrl) { m_websiteUrl.setValue(websiteUrl); }

	public String getComment() { return m_comment.getValue(); }
	public void setComment(String comment) { m_comment.setValue(comment); }

	public boolean isActive() { return m_active.getValue(); }
	public void setActive(Boolean active) { m_active.setValue(active); }

	public Date getDateCreated() { return m_dateCreated.getValue(); }

	@Override
	public boolean isNew()
	{
		return m_isNew;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof Application == false)
			return false;

		Application otherUser = (Application) other;

		if (isNew() || otherUser.isNew())
			return false;

		return getName().equals(otherUser.getName());
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}

	@Override
	public int compareTo(Application other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getName().compareTo(other.getName()));
	}


	// package methods


	@Override
	Criterion getKey()
	{
		return new SimpleKey(m_name);
	}

	@Override
	void save(Connection dbConn) throws IOException, SQLException
	{
		validateName(m_name.getValue());

		super.save(dbConn);

		m_isNew = false;
	}

	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_preferences = null;
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		delete(dbConn, m_name.getValue());

		m_isNew = true;
	}

	static void delete(Connection dbConn, String name) throws IOException, SQLException
	{
		log.debug("Deleting application " + name);
		deleteEndUsers(dbConn, name);
		(new DeleteOp("application_preferences", new StringCriterion("NAME", name))).execute(dbConn);
		PreparedStatement deleteStmt = dbConn.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE NAME = ?");

		try {
			deleteStmt.setString(1, name);
			deleteStmt.executeUpdate();
		}
		finally {
			deleteStmt.close();
		}
	}

	private static void deleteEndUsers(Connection dbConn, String appname) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
			"SELECT USER_ID FROM users where ROLE = 'REST_END_USER_UMBRELLA'  AND username like ?");
		ResultSet eu = null;

		try {
			selectStmt.setString(1, appname + ".%");

			eu = selectStmt.executeQuery();

			while (eu.next())
			{
				log.debug("Deleting EU " + eu.getLong(1) + " associated with application " + appname);
				User.delete(dbConn, eu.getLong(1));
			}
		}
		finally {
			if (eu != null)
				eu.close();

			selectStmt.close();
		}
	}

	@Override
	protected void pushInsertOps()
	{
		List<Column<?>> allColumns = new ArrayList<Column<?>>();

		allColumns.add(m_name);
		allColumns.addAll(m_columns);

		m_opQueue.push(new InsertOp(m_tableName, allColumns));
	}


	public static List<Application> findApplicationsByAuthType(String authtype) throws IOException, SQLException
	{
		return findApplications(new StringCriterion("AUTH_TYPE", authtype));
	}

	public static List<Application> findApplicationsByUserId(long userId) throws IOException, SQLException
	{
		return findApplications(new LongCriterion("AUTH_USER_ID", userId));
	}


	/* private */

	private static Application findApplication(Criterion key) throws IOException, SQLException
	{
		List<Application> apps = findApplications(key);
		if (apps.size() > 0)
		{
			return apps.get(0);
		}
		return null;
	}

	/*
		Returns empty list (not null) if no items meet criterion.
	*/
	private static List<Application> findApplications(Criterion key) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT NAME FROM " + TABLE_NAME + " WHERE ");
		stmtBuilder.append(key.getPhrase());

		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet appRow = null;

		try {
			selectStmt = dbConn.prepareStatement(stmtBuilder.toString());

			key.setParameter(selectStmt, 1);

			appRow = selectStmt.executeQuery();

			List<Application> apps = new ArrayList<Application>();

			while (appRow.next())
				apps.add(new Application(dbConn, appRow.getString(1)));
			return apps;

		}
		finally {
			if (appRow != null)
				appRow.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	/*
		Start Preferences implementation, yanked from User.java
	*/
	private class PreferenceMap extends MonitoredMap<String, String> {

		// constructors


		protected PreferenceMap(Map<String, String> prefMap)
		{
			super(prefMap);
		}


		// protected methods


		@Override
		protected void addMapPutOp(String key, String value)
		{
			Column<String> prefName = new StringColumn("PREFERENCE", false, 100, key);
			Column<String> prefValue = new StringColumn("VALUE", true, 100, value);
			List<Column<?>> cols = new ArrayList<Column<?>>();

			cols.add(m_name);
			cols.add(prefName);
			cols.add(prefValue);

			m_opQueue.add(new InsertOp("application_preferences", cols));
		}

		@Override
		protected void addMapSetOp(String key, String oldValue, String newValue)
		{
			Column<String> prefName = new StringColumn("PREFERENCE", false, 100, key);
			Column<String> prefValue = new StringColumn("VALUE", true, 100, newValue);
			CompositeKey prefKey = new CompositeKey(m_name, prefName);

			m_opQueue.add(new UpdateOp("application_preferences", prefKey, prefValue));
		}

		@Override
		protected void addMapRemoveOp(String key)
		{
			Column<String> prefName = new StringColumn("PREFERENCE", false, 100, key);
			CompositeKey prefKey = new CompositeKey(m_name, prefName);

			m_opQueue.add(new DeleteOp("application_preferences", prefKey));
		}

		@Override
		protected void addMapClearOp()
		{
			m_opQueue.add(new DeleteOp("application_preferences", getKey()));
		}
	}

	public Map<String, String> preferences() throws IOException, SQLException
	{
		if (m_preferences == null) {
			Map<String, String> newPreferences = new TreeMap<String, String>();

			if (!isNew()) {
				Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
				PreparedStatement selectStmt = null;
				ResultSet prefRows = null;

				try {
					selectStmt = dbConn.prepareStatement("SELECT PREFERENCE, VALUE FROM application_preferences WHERE NAME = ?");

					m_name.setParameter(selectStmt, 1);

					prefRows = selectStmt.executeQuery();

					while (prefRows.next())
						newPreferences.put(prefRows.getString(1), prefRows.getString(2));
				}
				finally {
					if (prefRows != null)
						prefRows.close();

					if (selectStmt != null)
						selectStmt.close();

					dbConn.close();
				}
			}

			m_preferences = new PreferenceMap(newPreferences);
		}

		return m_preferences;
	}

}
