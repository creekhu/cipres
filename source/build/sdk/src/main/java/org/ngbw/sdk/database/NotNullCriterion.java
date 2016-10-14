/*
 * NotNullCriterion.java
 */
package org.ngbw.sdk.database;


import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * An object that represents a <code>WHERE</code> clause search criterion that test for <code>NOT NULL</code>.
 *
 * @author Paul Hoover
 *
 */
class NotNullCriterion implements Criterion {

	private final String m_colName;
	private final int m_colType;


	// constructors


	/**
	 * Constructs a representation of a search criterion for the given column name.
	 *
	 * @param colName the name of the column
	 * @param colType the SQL type of the column
	 */
	NotNullCriterion(String colName, int colType)
	{
		m_colName = colName;
		m_colType = colType;
	}


	// public methods


	/**
	 * Creates a phrase describing the search clause criterion using the name and value given
	 * in the constructor.
	 *
	 * @return a phrase describing the criterion
	 */
	@Override
	public String getPhrase()
	{
		return m_colName + " IS NOT ? ";
	}

	/**
	 * Sets the value of a parameter in a <code>PreparedStatement</code> object using the name
	 * given in the constructor.
	 *
	 * @param statement the <code>PreparedStatement</code> object for which a parameter will be set
	 * @param index the offset that indicates the parameter to set
	 * @return the next offset to use when setting parameters
	 * @throws SQLException
	 */
	@Override
	public int setParameter(PreparedStatement statement, int index) throws SQLException
	{
		statement.setNull(index, m_colType);

		return index + 1;
	}

	@Override
	public String toString()
	{
		return m_colName + " IS NOT NULL ";
	}
}
