/*
 * ReadSequenceOp.java
 */
package org.ngbw.sdk.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ngbw.sdk.WorkbenchException;


/**
 *
 * @author Paul Hoover
 *
 */
class ReadSequenceOp implements RowOperation {

	// data fields


	private final String m_sequenceName;
	private final Column<Long> m_column;


	// constructors


	/**
	 *
	 * @param sequenceName
	 * @param column
	 */
	ReadSequenceOp(String sequenceName, Column<Long> column)
	{
		m_sequenceName = sequenceName;
		m_column = column;
	}


	// public methods


	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	@Override
	public void execute(Connection dbConn) throws SQLException
	{
		String query = DatabaseTools.getNextValuePhrase(m_sequenceName);
		Statement selectStmt = dbConn.createStatement();
		ResultSet row = null;

		try {
			row = selectStmt.executeQuery(query);

			if (!row.next())
				throw new WorkbenchException("Query failed: " + query);

			m_column.setValue(row.getLong(1));
		}
		finally {
			if (row != null)
				row.close();

			selectStmt.close();
		}
	}
}
