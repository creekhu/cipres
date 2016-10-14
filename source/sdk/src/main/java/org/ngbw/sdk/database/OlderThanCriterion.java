/*
 * OlderThanCriterion.java
 */
package org.ngbw.sdk.database;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;


/**
 * 
 * 
 * 
 *
 */
class OlderThanCriterion implements Criterion {

	private final String m_colName;
	private final long m_value;
	private final String m_interval;

	// constructors


	/**
	 * Constructs a representation of a search criterion for the given column name and value.
	 * 
	 * @param colName the name of the column
	 * @param value the value of the column
	 */
	OlderThanCriterion(String colName, long value, String interval)
	{
		m_colName = colName;
		m_value = value;
		m_interval = interval;
	}


	// public methods


	/**
	 * Creates a phrase describing the search clause criterion using the name and value given
	 * in the constructor.
	 * 
	 * @return a phrase describing the criterion
	 */
	public String getPhrase()
	{
		//return m_colName + " = ?";
		// Todo: is there a way to use a prepared statement with date interval?
		return m_colName + " < DATE_SUB(NOW(), INTERVAL " + m_value  + " "  + m_interval + " ) ";
	}

	/**
	 * Sets the value of a parameter in a <code>PreparedStatement</code> object using the name and
	 * value given in the constructor.
	 * 
	 * @param statement the <code>PreparedStatement</code> object for which a parameter will be set
	 * @param index the offset that indicates the parameter to set
	 * @return the next offset to use when setting parameters
	 * @throws SQLException
	 */
	public int setParameter(PreparedStatement statement, int index) throws SQLException
	{
		//statement.setLong(index, m_value);
		//return index + 1;
		return index;
	}

	/**
	 * Returns a string representation of the object. The representation is built using the
	 * name and value of the column given in the constructor.
	 * 
	 * @return a string representation of the object
	 */
	@Override
	public String toString()
	{
		//return m_colName + " = " + String.valueOf(m_value);
		return "what's this for?"; 
	}
}
