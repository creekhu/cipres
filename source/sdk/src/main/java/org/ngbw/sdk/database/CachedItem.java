/*
 * CachedItem.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.ngbw.sdk.common.util.IOUtils;


/**
 *
 * @author Paul Hoover
 *
 */
public class CachedItem extends VersionedRow implements Comparable<CachedItem> {

	// data fields


	private static final String TABLE_NAME = "cached_items";
	private static final String KEY_NAME = "CACHED_ITEM_ID";
	private final Column<String> m_comment = new StringColumn("COMMENT", true, 255);
	private final Column<String> m_attribute = new StringColumn("ATTRIBUTE", false, 255);
	private final Column<String> m_sessionId = new StringColumn("SESSION_ID", false, 255);
	private final StreamColumn<byte[]> m_value = new BinaryColumn("VALUE", true, true, this);


	// constructors


	/**
	 *
	 * @param sessionId
	 * @param attribute
	 */
	public CachedItem(String sessionId, String attribute)
	{
		this();

		setSessionId(sessionId);
		setAttribute(attribute);
	}

	/**
	 *
	 * @param cachedItemId
	 * @throws IOException
	 * @throws SQLException
	 */
	public CachedItem(long cachedItemId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(cachedItemId);

		load();
	}

	/**
	 *
	 * @param dbConn
	 * @param cachedItemId
	 * @throws IOException
	 * @throws SQLException
	 */
	CachedItem(Connection dbConn, long cachedItemId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(cachedItemId);

		load(dbConn);
	}

	/**
	 *
	 */
	private CachedItem()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_comment, m_attribute, m_sessionId, m_value);
	}


	// public methods


	/**
	 *
	 * @return
	 */
	public long getCachedItemId()
	{
		return m_key.getValue();
	}

	/**
	 *
	 * @return
	 */
	public String getComment()
	{
		return m_comment.getValue();
	}

	/**
	 *
	 * @param comment
	 */
	public void setComment(String comment)
	{
		m_comment.setValue(comment);
	}

	/**
	 *
	 * @return
	 */
	public String getAttribute()
	{
		return m_attribute.getValue();
	}

	/**
	 *
	 * @param attribute
	 */
	public void setAttribute(String attribute)
	{
		m_attribute.setValue(attribute);
	}

	/**
	 *
	 * @return
	 */
	public String getSessionId()
	{
		return m_sessionId.getValue();
	}

	/**
	 *
	 * @param sessionId
	 */
	public void setSessionId(String sessionId)
	{
		m_sessionId.setValue(sessionId);
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public byte[] getValue() throws IOException, SQLException
	{
		return m_value.getValue();
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public InputStream getValueAsStream() throws IOException, SQLException
	{
		return m_value.getValueAsStream();
	}

	/**
	 *
	 * @param value
	 */
	public void setValue(byte[] value)
	{
		m_value.setValue(value);
	}

	/**
	 *
	 * @param value
	 */
	public void setValue(String value)
	{
		setValue(value.getBytes());
	}

	/**
	 *
	 * @param value
	 * @throws IOException
	 */
	public void setValue(Serializable value) throws IOException
	{
		setValue(IOUtils.toBytes(value));
	}

	/**
	 *
	 * @param sessionId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<CachedItem> findCachedItems(String sessionId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE SESSION_ID = ?");
		ResultSet itemRows = null;

		try {
			selectStmt.setString(1, sessionId);

			itemRows = selectStmt.executeQuery();

			List<CachedItem> items = new ArrayList<CachedItem>();

			while (itemRows.next())
				items.add(new CachedItem(dbConn, itemRows.getLong(1)));

			return items;
		}
		finally {
			if (itemRows != null)
				itemRows.close();

			selectStmt.close();
		}
	}

	/**
	 *
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

		if (other instanceof CachedItem == false)
			return false;

		CachedItem otherItem = (CachedItem) other;

		// an object that hasn't been persisted can only be equal to itself
		if (isNew() || otherItem.isNew())
			return false;

		return getCachedItemId() == otherItem.getCachedItemId();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return (new Long(getCachedItemId())).hashCode();
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(CachedItem other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getCachedItemId() - other.getCachedItemId());
	}
}
