/*
 * Folder.java
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
import java.util.Map;
import java.util.TreeMap;

import org.ngbw.sdk.WorkbenchException;


/**
 *
 * @author Paul Hoover
 *
 */
public class Folder extends VersionedRow implements FolderItem, Comparable<Folder> {

	// nested classes


	/**
	 *
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

			cols.add(m_key);
			cols.add(prefName);
			cols.add(prefValue);

			m_opQueue.add(new InsertOp("folder_preferences", cols));
		}

		@Override
		protected void addMapSetOp(String key, String oldValue, String newValue)
		{
			Column<String> prefName = new StringColumn("PREFERENCE", false, 100, key);
			Column<String> prefValue = new StringColumn("VALUE", true, 100, newValue);
			CompositeKey prefKey = new CompositeKey(m_key, prefName);

			m_opQueue.add(new UpdateOp("folder_preferences", prefKey, prefValue));
		}

		@Override
		protected void addMapRemoveOp(String key)
		{
			Column<String> prefName = new StringColumn("PREFERENCE", false, 100, key);
			CompositeKey prefKey = new CompositeKey(m_key, prefName);

			m_opQueue.add(new DeleteOp("folder_preferences", prefKey));
		}

		@Override
		protected void addMapClearOp()
		{
			m_opQueue.add(new DeleteOp("folder_preferences", getKey()));
		}
	}


	// data fields


	public static final String SEPARATOR = "/";
	private static final String TABLE_NAME = "folders";
	private static final String KEY_NAME = "FOLDER_ID";
	private final Column<String> m_comment = new StringColumn("COMMENT", true, 255);
	private final Column<Boolean> m_groupReadable = new BooleanColumn("GROUP_READABLE", false);
	private final Column<Boolean> m_worldReadable = new BooleanColumn("WORLD_READABLE", false);
	private final Column<Long> m_groupId = new LongColumn("GROUP_ID", false);
	private final Column<Long> m_userId = new LongColumn("USER_ID", false);
	private final Column<Date> m_creationDate = new DateColumn("CREATION_DATE", false);
	private final Column<String> m_label = new StringColumn("LABEL", false, 255);
	private final Column<Long> m_enclosingFolderId = new LongColumn("ENCLOSING_FOLDER_ID", true);
	private PreferenceMap m_preferences;


	// constructors


	public Folder(User owner)
	{
		this();

		if (owner.isNew())
			throw new WorkbenchException("Can't create a folder for an unpersisted user");

		setGroupReadable(false);
		setWorldReadable(false);
		setUserId(owner.getUserId());
		setGroupId(owner.getDefaultGroupId());

		m_creationDate.setValue(Calendar.getInstance().getTime());
	}

	public Folder(Folder enclosingFolder)
	{
		this();

		if (enclosingFolder.isNew())
			throw new WorkbenchException("Can't create a folder in an unpersisted folder");

		setGroupReadable(false);
		setWorldReadable(false);
		setUserId(enclosingFolder.getUserId());
		setGroupId(enclosingFolder.getGroupId());
		setEnclosingFolderId(enclosingFolder.getFolderId());

		m_creationDate.setValue(Calendar.getInstance().getTime());
	}

	public Folder(long folderId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(folderId);

		load();
	}

	Folder(Connection dbConn, long folderId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(folderId);

		load(dbConn);
	}

	private Folder()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_comment, m_groupReadable, m_worldReadable, m_groupId, m_userId, m_creationDate, m_label, m_enclosingFolderId);
	}


	// public methods


	public long getFolderId()
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

	public boolean isGroupReadable()
	{
		return m_groupReadable.getValue();
	}

	public void setGroupReadable(Boolean groupReadable)
	{
		m_groupReadable.setValue(groupReadable);
	}

	public boolean isWorldReadable()
	{
		return m_worldReadable.getValue();
	}

	public void setWorldReadable(Boolean worldReadable)
	{
		m_worldReadable.setValue(worldReadable);
	}

	@Override
	public long getGroupId()
	{
		return m_groupId.getValue();
	}

	@Override
	public void setGroupId(Long groupId)
	{
		m_groupId.setValue(groupId);
	}

	@Override
	public Group getGroup() throws IOException, SQLException
	{
		if (m_groupId.isNull())
			return null;

		return new Group(m_groupId.getValue());
	}

	@Override
	public void setGroup(Group group)
	{
		if (group != null && !group.isNew())
			setGroupId(group.getGroupId());
		else
			setGroupId(null);
	}

	@Override
	public long getUserId()
	{
		return m_userId.getValue();
	}

	@Override
	public void setUserId(Long userId)
	{
		m_userId.setValue(userId);
	}

	@Override
	public User getUser() throws IOException, SQLException
	{
		if (m_userId.isNull())
			return null;

		return new User(m_userId.getValue());
	}

	@Override
	public void setUser(User user)
	{
		if (user != null && !user.isNew())
			setUserId(user.getUserId());
		else
			setUserId(null);
	}

	@Override
	public Date getCreationDate()
	{
		return m_creationDate.getValue();
	}

	@Override
	public String getLabel()
	{
		return m_label.getValue();
	}

	@Override
	public void setLabel(String label)
	{
		if (label.indexOf(SEPARATOR) >= 0)
			throw new WorkbenchException("Folder labels can't contain " + SEPARATOR + " character");

		if (label.isEmpty())
			throw new WorkbenchException("Folder labels can't be empty");

		m_label.setValue(label);
	}

	@Override
	public long getEnclosingFolderId()
	{
		return m_enclosingFolderId.getValue();
	}

	@Override
	public void setEnclosingFolderId(Long enclosingFolderId)
	{
		m_enclosingFolderId.setValue(enclosingFolderId);
	}

	@Override
	public Folder getEnclosingFolder() throws IOException, SQLException
	{
		if (m_enclosingFolderId.isNull())
			return null;

		return new Folder(m_enclosingFolderId.getValue());
	}

	@Override
	public void setEnclosingFolder(Folder enclosingFolder)
	{
		if (enclosingFolder != null && !enclosingFolder.isNew())
			setEnclosingFolderId(enclosingFolder.getFolderId());
		else
			setEnclosingFolderId(null);
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
					selectStmt = dbConn.prepareStatement("SELECT PREFERENCE, VALUE FROM folder_preferences WHERE FOLDER_ID = ?");

					m_key.setParameter(selectStmt, 1);

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

	public List<Task> findTasks() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return Task.findTasks(new LongCriterion("ENCLOSING_FOLDER_ID", m_key.getValue()));
	}

	public Task findTask(String label) throws IOException, SQLException
	{
		if (isNew())
			return null;

		List<Task> result = Task.findTasks(new LongCriterion("ENCLOSING_FOLDER_ID", m_key.getValue()), new StringCriterion("LABEL", label));

		if (result.isEmpty())
			return null;

		return result.get(0);
	}

	public List<UserDataItem> findDataItems() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return UserDataItem.findDataItems(new LongCriterion("ENCLOSING_FOLDER_ID", m_key.getValue()));
	}

	public UserDataItem findDataItem(String label) throws IOException, SQLException
	{
		if (isNew())
			return null;

		List<UserDataItem> result = UserDataItem.findDataItems(new LongCriterion("ENCLOSING_FOLDER_ID", m_key.getValue()));

		if (result.isEmpty())
			return null;

		return result.get(0);
	}

	public List<Folder> findSubFolders() throws IOException, SQLException
	{
		if (isNew())
			return null;

		return findFolders(new LongCriterion("ENCLOSING_FOLDER_ID", m_key.getValue()));
	}

	public Folder findSubFolder(String path) throws IOException, SQLException
	{
		if (path.startsWith(SEPARATOR))
			throw new WorkbenchException("Path must be relative");

		return findFolder(m_enclosingFolderId.getValue(), path, null);
	}

	public Folder findOrCreateSubFolder(String path) throws IOException, SQLException
	{
		if (path.startsWith(SEPARATOR))
			throw new WorkbenchException("Path must be relative");

		return findFolder(m_enclosingFolderId.getValue(), path, new User(m_userId.getValue()));
	}

	public static Folder findFolder(String path) throws IOException, SQLException
	{
		if (!path.startsWith(SEPARATOR))
			throw new WorkbenchException("Path must be absolute");

		return findFolder(null, path.substring(1), null);
	}

	public static Folder findOrCreateFolder(User owner, String path) throws IOException, SQLException
	{
		if (!path.startsWith(SEPARATOR))
			throw new WorkbenchException("Path must be absolute");

		return findFolder(null, path.substring(1), owner);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof Folder == false)
			return false;

		Folder otherFolder = (Folder) other;

		if (isNew() || otherFolder.isNew())
			return false;

		return getFolderId() == otherFolder.getFolderId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getFolderId())).hashCode();
	}

	@Override
	public int compareTo(Folder other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getFolderId() - other.getFolderId());
	}


	// package methods


	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_preferences = null;
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		delete(dbConn, m_key.getValue());

		m_key.reset();
	}

	static List<Folder> findFolders(Criterion... keys) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT FOLDER_ID FROM " + TABLE_NAME + " WHERE ");

		stmtBuilder.append(keys[0].getPhrase());

		for (int i = 1 ; i < keys.length ; i += 1) {
			stmtBuilder.append(" AND ");
			stmtBuilder.append(keys[i].getPhrase());
		}

		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet folderRows = null;

		try {
			selectStmt = dbConn.prepareStatement(stmtBuilder.toString());

			int index = 1;

			for (int i = 0 ; i < keys.length ; i += 1)
				index = keys[i].setParameter(selectStmt, index);

			folderRows = selectStmt.executeQuery();

			List<Folder> folders = new ArrayList<Folder>();

			while (folderRows.next())
				folders.add(new Folder(dbConn, folderRows.getLong(1)));

			return folders;
		}
		finally {
			if (folderRows != null)
				folderRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	static void delete(Connection dbConn, long folderId) throws IOException, SQLException
	{
		Criterion folderKey = new LongCriterion(KEY_NAME, folderId);

		(new DeleteOp("folder_preferences", folderKey)).execute(dbConn);

		deleteData(dbConn, folderId);
		deleteTasks(dbConn, folderId);
		deleteSubFolders(dbConn, folderId);

		(new DeleteOp(TABLE_NAME, folderKey)).execute(dbConn);
	}


	// private methods


	private static Folder findFolder(Long enclosingFolderId, String path, User owner) throws IOException, SQLException
	{
		int index = 0;
		String[] folderNames = path.split(SEPARATOR);
		Folder result;

		while (true) {
			List<Folder> folders = findFolders(new LongCriterion("ENCLOSING_FOLDER_ID", enclosingFolderId), new StringCriterion("LABEL", folderNames[index]));

			if (folders.isEmpty()) {
				if (owner != null) {
					while (true) {
						result = new Folder(owner);

						result.setEnclosingFolderId(enclosingFolderId);
						result.setLabel(folderNames[index]);
						result.save();

						index += 1;

						if (index == folderNames.length)
							break;

						enclosingFolderId = result.getFolderId();
					}
				}
				else
					result = null;

				break;
			}

			result = folders.get(0);
			index += 1;

			if (index == folderNames.length)
				break;

			enclosingFolderId = result.getFolderId();
		}

		return result;
	}

	private static void deleteData(Connection dbConn, long folderId) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT USERDATA_ID FROM userdata WHERE ENCLOSING_FOLDER_ID = ?");
		ResultSet dataRows = null;

		try {
			selectStmt.setLong(1, folderId);

			dataRows = selectStmt.executeQuery();

			while (dataRows.next())
				UserDataItem.delete(dbConn, dataRows.getLong(1));
		}
		finally {
			if (dataRows != null)
				dataRows.close();

			selectStmt.close();
		}
	}

	private static void deleteTasks(Connection dbConn, long folderId) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT TASK_ID FROM tasks WHERE ENCLOSING_FOLDER_ID = ?");
		ResultSet taskRows = null;

		try {
			selectStmt.setLong(1, folderId);

			taskRows = selectStmt.executeQuery();

			while (taskRows.next())
				Task.delete(dbConn, taskRows.getLong(1));
		}
		finally {
			if (taskRows != null)
				taskRows.close();

			selectStmt.close();
		}
	}

	private static void deleteSubFolders(Connection dbConn, long folderId) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT FOLDER_ID FROM folders WHERE ENCLOSING_FOLDER_ID = ?");
		ResultSet folderRows = null;

		try {
			selectStmt.setLong(1, folderId);

			folderRows = selectStmt.executeQuery();

			while (folderRows.next())
				Folder.delete(dbConn, folderRows.getLong(1));
		}
		finally {
			if (folderRows != null)
				folderRows.close();

			selectStmt.close();
		}
	}
}
