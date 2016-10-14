/*
 * LongCriterion.java
 */
package org.ngbw.sdk.database;


import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * An object that represents a <code>WHERE</code> clause search criterion that uses a <code>BIGINT</code> value.
 *
 * @author Paul Hoover
 *
 */
class LongCriterion extends ColumnCriterion<Long> {

	// constructors


	/**
	 * Constructs a representation of a search criterion for the given column name and value.
	 *
	 * @param colName the name of the column
	 * @param value the value of the column
	 */
	LongCriterion(String colName, Long value)
	{
		super(colName, value);
	}


	// public methods


	/**
	 * Sets the value of a parameter in a <code>PreparedStatement</code> object using the name and
	 * value given in the constructor.
	 *
	 * @param statement the <code>PreparedStatement</code> object for which a parameter will be set
	 * @param index the offset that indicates the parameter to set
	 * @return the next offset to use when setting parameters
	 * @throws SQLException
	 */
	@Override
	public int setParameter(PreparedStatement statement, int index) throws SQLException
	{
		if (m_value != null) {
			statement.setLong(index, m_value);

			index += 1;
		}

		return index;
	}
}
