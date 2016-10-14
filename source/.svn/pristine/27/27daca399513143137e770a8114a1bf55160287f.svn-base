/*
 * GeneratedKeyRow.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


/**
 *
 * @author Paul Hoover
 *
 */
abstract class GeneratedKeyRow extends Row {

	// nested classes


	/**
	 *
	 */
	private interface InsertOpBuilder {

		/**
		 *
		 * @param tableName
		 * @param key
		 * @param columns
		 * @param opQueue
		 */
		void pushInsertOps(String tableName, Column<Long> key, List<Column<?>> columns, Deque<RowOperation> opQueue);
	}

	/**
	 *
	 */
	private static class AutoGenInsertOpBuilder implements InsertOpBuilder {

		/**
		 *
		 * @param tableName
		 * @param key
		 * @param columns
		 * @param opQueue
		 */
		@Override
		public void pushInsertOps(String tableName, Column<Long> key, List<Column<?>> columns, Deque<RowOperation> opQueue)
		{
			opQueue.push(new AutoGenKeyInsertOp(tableName, key, columns));
		}
	}

	/**
	 *
	 */
	private static class SequenceInsertOpBuilder implements InsertOpBuilder {

		/**
		 *
		 * @param tableName
		 * @param key
		 * @param columns
		 * @param opQueue
		 */
		@Override
		public void pushInsertOps(String tableName, Column<Long> key, List<Column<?>> columns, Deque<RowOperation> opQueue)
		{
			String seqName = tableName + "_" + key.getName() + "_seq";
			List<Column<?>> allColumns = new ArrayList<Column<?>>();

			allColumns.add(key);
			allColumns.addAll(columns);

			opQueue.push(new InsertOp(tableName, allColumns));
			opQueue.push(new ReadSequenceOp(seqName, key));
		}
	}


	// data fields


	protected final Column<Long> m_key;
	private static InsertOpBuilder m_opBuilder;


	static {
		if (DatabaseTools.usesGeneratedKeys())
			m_opBuilder = new AutoGenInsertOpBuilder();
		else
			m_opBuilder = new SequenceInsertOpBuilder();
	}


	// constructors


	/**
	 *
	 * @param tableName
	 * @param keyName
	 */
	protected GeneratedKeyRow(String tableName, String keyName)
	{
		super(tableName);

		m_key = new LongColumn(keyName, false);
	}


	// public methods


	/**
	 * Indicates whether or not the object has been persisted.
	 *
	 * @return <code>true</code> if the object has not been persisted
	 */
	@Override
	public boolean isNew()
	{
		return m_key.isNull() || m_key.isModified();
	}


	// package methods


	/**
	 * Returns a <code>Criterion</code> object that describes the primary key of the record
	 * that the object represents.
	 *
	 * @return a <code>Criterion</code> object that describes the primary key
	 */
	@Override
	Criterion getKey()
	{
		return new SimpleKey(m_key);
	}

	/**
	 *
	 * @param dbConn a <code>Connection</code> object that will be used to communicate with the database
	 * @throws IOException
	 * @throws SQLException
	 */
	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		super.delete(dbConn);

		m_key.reset();
	}


	// protected methods


	/**
	 *
	 */
	@Override
	protected void pushInsertOps()
	{
		m_opBuilder.pushInsertOps(m_tableName, m_key, m_columns, m_opQueue);
	}
}
