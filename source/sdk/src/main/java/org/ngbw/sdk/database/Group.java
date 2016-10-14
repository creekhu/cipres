/*
 * Group.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ngbw.sdk.WorkbenchException;


/**
 *
 * @author Paul Hoover
 *
 */
public class Group extends VersionedRow implements Comparable<Group> {

	// nested classes


	/**
	 *
	 */
	private class RemoveMemberOp implements RowOperation {

		// data fields


		private final long m_userId;


		// constructors


		protected RemoveMemberOp(long userId)
		{
			m_userId = userId;
		}


		// public methods


		@Override
		public void execute(Connection dbConn) throws IOException, SQLException
		{
			if (isDefaultMembership(dbConn))
				throw new WorkbenchException("Can't remove membership in default group");

			Column<Long> userId = new LongColumn("USER_ID", false, m_userId);
			CompositeKey key = new CompositeKey(userId, m_key);

			(new DeleteOp("user_group_lookup", key)).execute(dbConn);
		}


		// private methods


		private boolean isDefaultMembership(Connection dbConn) throws IOException, SQLException
		{
			Column<Long> userId = new LongColumn("USER_ID", false, m_userId);
			Column<Long> defaultGroupId = new LongColumn("DEFAULT_GROUP_ID", false, m_key.getValue());
			Column<Integer> result = new IntegerColumn("result", true);
			CompositeKey key = new CompositeKey(userId, defaultGroupId);

			(new CountOp("users", key, result)).execute(dbConn);

			return result.getValue() > 0;
		}
	}

	/**
	 *
	 */
	private class RemoveAllMembersOp implements RowOperation {

		// public methods


		@Override
		public void execute(Connection dbConn) throws IOException, SQLException
		{
			PreparedStatement deleteStmt = dbConn.prepareStatement(
					"DELETE FROM user_group_lookup " +
					"WHERE GROUP_ID = ? AND USER_ID NOT IN ( " +
						"SELECT USER_ID " +
						"FROM users " +
						"WHERE DEFAULT_GROUP_ID = ? " +
					")"
			);

			try {
				m_key.setParameter(deleteStmt, 1);
				m_key.setParameter(deleteStmt, 2);

				deleteStmt.executeUpdate();
			}
			finally {
				deleteStmt.close();
			}

			m_members = null;
		}
	}

	/**
	 *
	 */
	private class MemberSet extends MonitoredSet<User> {

		// constructors


		protected MemberSet(Set<User> users)
		{
			super(users);
		}


		// protected methods


		@Override
		protected void addSetAddOp(User element)
		{
			if (element.isNew())
				throw new WorkbenchException("Can't add membership for an unpersisted user");

			Column<Long> userId = new LongColumn("USER_ID", false, element.getUserId());
			List<Column<?>> cols = new ArrayList<Column<?>>();

			cols.add(m_key);
			cols.add(userId);

			m_opQueue.add(new InsertOp("user_group_lookup", cols));
		}

		@Override
		protected void addSetRemoveOp(User element)
		{
			m_opQueue.add(new RemoveMemberOp(element.getUserId()));
		}

		@Override
		protected void addSetClearOp()
		{
			m_opQueue.add(new RemoveAllMembersOp());
		}
	}


	// data fields


	private static final String TABLE_NAME = "groups";
	private static final String KEY_NAME = "GROUP_ID";
	private final Column<String> m_comment = new StringColumn("COMMENT", true, 255);
	private final Column<Date> m_creationDate = new DateColumn("CREATION_DATE", false);
	private final Column<String> m_description = new StringColumn("DESCRIPTION", true, 1023);
	private final Column<String> m_groupname = new StringColumn("GROUPNAME", false, 255);
	private final Column<Long> m_administrator = new LongColumn("ADMINISTRATOR", true);
	private MemberSet m_members;


	// constructors


	public Group()
	{
		this(TABLE_NAME, KEY_NAME);

		m_creationDate.setValue(Calendar.getInstance().getTime());
	}

	public Group(long groupId) throws IOException, SQLException
	{
		this(TABLE_NAME, KEY_NAME);

		m_key.assignValue(groupId);

		load();
	}

	Group(Connection dbConn, long groupId) throws IOException, SQLException
	{
		this(TABLE_NAME, KEY_NAME);

		m_key.assignValue(groupId);

		load(dbConn);
	}

	private Group(String tableName, String keyName)
	{
		super(tableName, keyName);

		construct(m_comment, m_creationDate, m_description, m_groupname, m_administrator);
	}


	// public methods


	public long getGroupId()
	{
		return m_key.getValue();
	}

	public String getComment()
	{
		return m_comment.getValue();
	}

	public void setComment(String comment)
	{
		m_comment.setValue(comment);
	}

	public Date getCreationDate()
	{
		return m_creationDate.getValue();
	}

	public String getDescription()
	{
		return m_description.getValue();
	}

	public void setDescription(String description)
	{
		m_description.setValue(description);
	}

	public String getGroupname()
	{
		return m_groupname.getValue();
	}

	public void setGroupname(String groupname)
	{
		m_groupname.setValue(groupname);
	}

	public long getAdministratorId()
	{
		return m_administrator.getValue();
	}

	public void setAdministratorId(Long administratorId)
	{
		m_administrator.setValue(administratorId);
	}

	public User getAdministrator() throws IOException, SQLException
	{
		if (m_administrator.isNull())
			return null;

		return new User(m_administrator.getValue());
	}

	public void setAdministrator(User administrator)
	{
		if (administrator != null && !administrator.isNew())
			setAdministratorId(administrator.getUserId());
		else
			setAdministratorId(null);
	}

	public Set<User> members() throws IOException, SQLException
	{
		if (m_members == null) {
			Set<User> groupMembers;

			if (!isNew())
				groupMembers = User.findMembers(m_key.getValue());
			else
				groupMembers = new TreeSet<User>();

			m_members = new MemberSet(groupMembers);
		}

		return m_members;
	}

	public List<Folder> findFolders() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return Folder.findFolders(new LongCriterion("GROUP_ID", m_key.getValue()));
	}

	public List<UserDataItem> findDataItems() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return UserDataItem.findDataItems(new LongCriterion("GROUP_ID", m_key.getValue()));
	}

	public List<Task> findTasks() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return Task.findTasks(new LongCriterion("GROUP_ID", m_key.getValue()));
	}

	public static Group findGroupByGroupname(String groupname) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			StringCriterion name = new StringCriterion("GROUPNAME", groupname);
			Column<Long> groupId = new LongColumn("GROUP_ID", false);

			(new SelectOp(TABLE_NAME, name, groupId, false)).execute(dbConn);

			if (groupId.isNull())
				return null;

			return new Group(dbConn, groupId.getValue());
		}
		finally {
			dbConn.close();
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof Group == false)
			return false;

		Group otherGroup = (Group) other;

		if (isNew() || otherGroup.isNew())
			return false;

		return getGroupId() == otherGroup.getGroupId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getGroupId())).hashCode();
	}

	@Override
	public int compareTo(Group other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getGroupId() - other.getGroupId());
	}


	// package methods


	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_members = null;
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		Criterion key = getKey();

		if (numDefaultMemberships(dbConn, m_key.getValue()) > 0)
			throw new WorkbenchException("Can't delete a default group");

		if (isOwner(dbConn, key))
			throw new WorkbenchException("Can't delete a group that owns other entities");

		deleteGroup(dbConn, key);

		m_key.reset();
	}

	static Set<Group> findMemberships(long userId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet groupRows = null;

		try {
			selectStmt = dbConn.prepareStatement(
					"SELECT " + TABLE_NAME + ".GROUP_ID " +
					"FROM users " +
						"INNER JOIN user_group_lookup ON users.USER_ID = user_group_lookup.USER_ID " +
						"INNER JOIN " + TABLE_NAME + " ON user_group_lookup.GROUP_ID = " + TABLE_NAME + ".GROUP_ID " +
					"WHERE users.USER_ID = ?"
			);

			selectStmt.setLong(1, userId);

			groupRows = selectStmt.executeQuery();

			Set<Group> memberships = new TreeSet<Group>();

			while (groupRows.next())
				memberships.add(new Group(dbConn, groupRows.getLong(1)));

			return memberships;
		}
		finally {
			if (groupRows != null)
				groupRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	static void delete(Connection dbConn, long groupId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion(KEY_NAME, groupId);

		if (numDefaultMemberships(dbConn, groupId) > 0 || isOwner(dbConn, key))
			return;

		deleteGroup(dbConn, key);
	}


	// private methods


	private static void deleteGroup(Connection dbConn, Criterion groupKey) throws IOException, SQLException
	{
		(new DeleteOp("user_group_lookup", groupKey)).execute(dbConn);
		(new DeleteOp(TABLE_NAME, groupKey)).execute(dbConn);
	}

	private static int numDefaultMemberships(Connection dbConn, long groupId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion("DEFAULT_GROUP_ID", groupId);
		Column<Integer> result = new IntegerColumn("result", true);

		(new CountOp("users", key, result)).execute(dbConn);

		return result.getValue();
	}

	private static boolean isOwner(Connection dbConn, Criterion groupKey) throws IOException, SQLException
	{
		Column<Integer> result = new IntegerColumn("result", true);

		(new CountOp("folders", groupKey, result)).execute(dbConn);

		if (result.getValue() > 0)
			return true;

		(new CountOp("tasks", groupKey, result)).execute(dbConn);

		if (result.getValue() > 0)
			return true;

		(new CountOp("userdata", groupKey, result)).execute(dbConn);

		if (result.getValue() > 0)
			return true;

		return false;
	}
}
