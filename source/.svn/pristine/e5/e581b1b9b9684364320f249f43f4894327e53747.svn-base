/*
 * Row.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;


/**
 * Represents a row from a database table.
 *
 * @author Paul Hoover
 *
 */
public abstract class Row {

	// data fields


	protected final List<Column<?>> m_columns = new ArrayList<Column<?>>();
	protected final Deque<RowOperation> m_opQueue = new LinkedList<RowOperation>();
	protected final String m_tableName;
	private static final Log m_log = LogFactory.getLog(Row.class.getName());


	// constructors


	/**
	 * Constructs a row representation.
	 *
	 * @param tableName the name of the backing table
	 */
	protected Row(String tableName)
	{
		m_tableName = tableName;
	}


	// public methods


	/**
	 * Indicates whether or not the object has been persisted.
	 *
	 * @return <code>true</code> if the object has not been persisted
	 */
	public abstract boolean isNew();

	/**
	 * Saves any changes to the columns of the row to the backing table.
	 * Only updates columns that have been modified.
	 *
	 * @throws IOException
	 * @throws SQLException
	 */
	public void save() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			dbConn.setAutoCommit(false);

			save(dbConn);

			dbConn.commit();
		}
		catch (IOException | SQLException err) {
			// UmbrellaUser constraint violations are expected with concurrency and I don't want
			// to fill the log with stack traces.
			//m_log.error("", sqlErr);
			m_log.error(err);

			dbConn.rollback();

			throw err;
		}
		finally {
			dbConn.close();
		}
	}

	/**
	 * Synchronize the fields of this object with the current values from the database.
	 *
	 * @throws IOException
	 * @throws SQLException
	 */
	public void load() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			dbConn.setAutoCommit(false);

			load(dbConn);

			dbConn.commit();
		}
		catch (IOException | SQLException err) {
			m_log.error("", err);

			dbConn.rollback();

			throw err;
		}
		finally {
			dbConn.close();
		}
	}

	/**
	 *
	 * @throws IOException
	 * @throws SQLException
	 */
	public void delete() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			dbConn.setAutoCommit(false);

			delete(dbConn);

			dbConn.commit();
		}
		catch (IOException | SQLException err) {
			m_log.error("", err);

			dbConn.rollback();

			throw err;
		}
		finally {
			dbConn.close();
		}
	}


	// package methods


	/**
	 * Returns a <code>Criterion</code> object that describes the primary key of the record
	 * that the object represents.
	 *
	 * @return a <code>Criterion</code> object that describes the primary key
	 */
	abstract Criterion getKey();

	/**
	 * Saves the current state of the object to the database. If the object has not yet been persisted,
	 * new records are inserted in the appropriate tables. If the object has been persisted, then any
	 * changes are written to the backing tables. Only those values that have changed are written, and
	 * if the state of the object is unchanged, the method does nothing.
	 *
	 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
	 * @throws IOException
	 * @throws SQLException
	 */
	void save(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			pushInsertOps();
		else
			pushUpdateOps();

		executeOps(dbConn);
	}

	/**
	 *
	 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
	 * @throws IOException
	 * @throws SQLException
	 */
	void load(Connection dbConn) throws IOException, SQLException
	{
		assert !m_columns.isEmpty();

		if (isNew())
			throw new WorkbenchException("Not persisted");

		m_opQueue.add(new SelectOp(m_tableName, getKey(), m_columns));

		executeOps(dbConn);
	}

	/**
	 *
	 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
	 * @throws IOException
	 * @throws SQLException
	 */
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		m_opQueue.add(new DeleteOp(m_tableName, getKey()));

		executeOps(dbConn);
	}

	/**
	 *
	 * @return the name of the backing table
	 */
	String getTableName()
	{
		return m_tableName;
	}


	// protected methods


	/**
	 *
	 */
	protected abstract void pushInsertOps();

	/**
	 *
	 */
	protected void pushUpdateOps()
	{
		assert !m_columns.isEmpty();

		List<Column<?>> modifiedCols = new ArrayList<Column<?>>();

		for (Column<?> col : m_columns) {
			if (col.isModified())
				modifiedCols.add(col);
		}

		m_opQueue.push(new UpdateOp(m_tableName, getKey(), modifiedCols));
	}

	/**
	 *
	 * @param columns
	 */
	protected void construct(Column<?>... columns)
	{
		assert m_columns.isEmpty();

		for (Column<?> col : columns)
			m_columns.add(col);
	}

	/**
	 *
	 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	protected void executeOps(Connection dbConn) throws IOException, SQLException
	{
		while (!m_opQueue.isEmpty()) {
			RowOperation op = m_opQueue.remove();

			op.execute(dbConn);
		}
	}
}
