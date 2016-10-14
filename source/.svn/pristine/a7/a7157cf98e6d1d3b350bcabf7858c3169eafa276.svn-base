/*
 * CountOp.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author Paul Hoover
 *
 */
class CountOp implements RowOperation {

	// data fields


	private final String m_tableName;
	private final Criterion m_key;
	private final Column<Integer> m_result;


	// constructors


	/**
	 *
	 * @param tableName
	 * @param key
	 * @param result
	 */
	CountOp(String tableName, Criterion key, Column<Integer> result)
	{
		m_tableName = tableName;
		m_key = key;
		m_result = result;
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
		StringBuilder stmtBuilder = new StringBuilder();

		stmtBuilder.append("SELECT COUNT(*) FROM ");
		stmtBuilder.append(m_tableName);
		stmtBuilder.append(" WHERE ");
		stmtBuilder.append(m_key.getPhrase());

		PreparedStatement selectStmt = dbConn.prepareStatement(stmtBuilder.toString());
		ResultSet row = null;

		try {
			m_key.setParameter(selectStmt, 1);

			row = selectStmt.executeQuery();

			if (row.next())
				m_result.assignValue(row, 1);
			else
				m_result.assignValue(null);
		}
		finally {
			if (row != null)
				row.close();

			selectStmt.close();
		}
	}
}
