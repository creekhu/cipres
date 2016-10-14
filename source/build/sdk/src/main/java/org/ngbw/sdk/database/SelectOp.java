/*
 * SelectOp.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ngbw.sdk.database.NotExistException;


/**
 *
 * @author Paul Hoover
 *
 */
class SelectOp implements RowOperation {

	// data fields


	private final boolean m_throwException;
	private final String m_tableName;
	private final Criterion m_key;
	private final List<Column<?>> m_columns;


	// constructors


	/**
	 *
	 * @param tableName
	 * @param key
	 * @param columns
	 */
	SelectOp(String tableName, Criterion key, List<Column<?>> columns)
	{
		this(tableName, key, columns, true);
	}

	/**
	 *
	 * @param tableName
	 * @param key
	 * @param columns
	 * @param throwException
	 */
	SelectOp(String tableName, Criterion key, List<Column<?>> columns, boolean throwException)
	{
		m_throwException = throwException;
		m_tableName = tableName;
		m_key = key;
		m_columns = columns;
	}

	/**
	 *
	 * @param tableName
	 * @param key
	 * @param column
	 */
	SelectOp(String tableName, Criterion key, Column<?> column)
	{
		this(tableName, key, column, true);
	}

	/**
	 *
	 * @param tableName
	 * @param key
	 * @param column
	 * @param throwException
	 */
	SelectOp(String tableName, Criterion key, Column<?> column, boolean throwException)
	{
		m_throwException = throwException;
		m_tableName = tableName;
		m_key = key;
		m_columns = new ArrayList<Column<?>>();

		m_columns.add(column);
	}


	// public methods


	/**
	 *
	 * @param dbConn
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	@Override
	public void execute(Connection dbConn) throws IOException, SQLException
	{
		if (m_columns.isEmpty())
			return;

		StringBuilder stmtBuilder = new StringBuilder();
		Iterator<Column<?>> columns = m_columns.iterator();

		stmtBuilder.append("SELECT ");
		stmtBuilder.append(columns.next().getName());

		while (columns.hasNext()) {
			stmtBuilder.append(", ");
			stmtBuilder.append(m_tableName);
			stmtBuilder.append(".");
			stmtBuilder.append(columns.next().getName());
		}

		stmtBuilder.append(" FROM ");
		stmtBuilder.append(m_tableName);
		stmtBuilder.append(" WHERE ");
		stmtBuilder.append(m_key.getPhrase());

		PreparedStatement selectStmt = dbConn.prepareStatement(stmtBuilder.toString());
		ResultSet row = null;

		try {
			m_key.setParameter(selectStmt, 1);

			row = selectStmt.executeQuery();

			if (!row.next()) {
				if (m_throwException)
					throw new NotExistException("No row in " + m_tableName +	" for key " + m_key.toString());
				else
					return;
			}

			int index = 1;

			for (columns = m_columns.iterator() ; columns.hasNext() ; index += 1)
				columns.next().assignValue(row, index);
		}
		finally {
			if (row != null)
				row.close();

			selectStmt.close();
		}
	}
}
