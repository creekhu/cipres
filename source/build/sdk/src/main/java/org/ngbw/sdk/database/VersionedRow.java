/*
 * VersionedRow.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 * @author Paul Hoover
 *
 */
public abstract class VersionedRow extends GeneratedKeyRow {

	// nested classes


	/**
	 *
	 */
	private class CheckDbVersionOp implements RowOperation {

		// public methods


		/**
		 *
		 * @param dbConn
		 * @throws IOException
		 * @throws SQLException
		 */
		@Override
		public void execute(Connection dbConn) throws IOException, SQLException
		{
			Criterion key = getKey();
			int dbVersion = getDbVersion(dbConn, key);
			int memoryVersion = m_version.getValue();

			if (memoryVersion != dbVersion + 1)
				throw new StaleRowException(m_tableName, key, dbVersion, memoryVersion);
		}


		// private methods


		/**
		 *
		 * @param dbConn
		 * @param key
		 * @return
		 * @throws IOException
		 * @throws SQLException
		 */
		private int getDbVersion(Connection dbConn, Criterion key) throws IOException, SQLException
		{
			Column<Integer> dbVersion = new IntegerColumn(m_version.getName(), false);

			(new SelectOp(m_tableName, key, dbVersion)).execute(dbConn);

			return dbVersion.getValue();
		}
	}


	// data fields


	private final Column<Integer> m_version;


	// constructors


	/**
	 *
	 * @param tableName
	 * @param keyName
	 */
	protected VersionedRow(String tableName, String keyName)
	{
		super(tableName, keyName);

		m_version = new IntegerColumn("VERSION", false);
	}


	// public methods


	/**
	 *
	 * @return
	 */
	public int getVersion()
	{
		return m_version.getValue();
	}


	// protected methods


	/**
	 *
	 * @param columns
	 */
	@Override
	protected void construct(Column<?>... columns)
	{
		Column<?>[] allColumns = new Column<?>[columns.length + 1];

		System.arraycopy(columns, 0, allColumns, 0, columns.length);

		allColumns[columns.length] = m_version;

		super.construct(allColumns);
	}

	/**
	 *
	 */
	@Override
	protected void pushInsertOps()
	{
		m_version.setValue(0);

		super.pushInsertOps();
	}

	/**
	 *
	 */
	@Override
	protected void pushUpdateOps()
	{
		int memoryVersion = m_version.getValue();

		m_version.setValue(memoryVersion + 1);

		super.pushUpdateOps();

		m_opQueue.push(new CheckDbVersionOp());
	}
}
