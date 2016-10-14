/*
 * CompositeKey.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * An object that represents a <code>WHERE</code> clause search criterion based on a composite
 * <code>KEY</code> constraint. The constraint is limited to two columns, which is sufficient
 * for the current structure of the database.
 *
 * @author Paul Hoover
 *
 */
class CompositeKey implements Criterion {

	private final Column<?> m_firstCol;
	private final Column<?> m_secondCol;


	// constructors


	/**
	 * Constructs a representation of a search criterion.
	 *
	 * @param firstCol a <code>Column</code> object that describes the first column of the <code>KEY</code> constraint
	 * @param secondCol a <code>Column</code> object that describes the second column of the <code>KEY</code> constraint
	 */
	CompositeKey(Column<?> firstCol, Column<?> secondCol)
	{
		m_firstCol = firstCol;
		m_secondCol = secondCol;
	}


	// public methods


	/**
	 * Creates a phrase describing the search clause criterion using the names and values of the
	 * contained <code>Column</code> objects.
	 *
	 * @return a phrase describing the criterion
	 */
	@Override
	public String getPhrase()
	{
		return m_firstCol.getName() + " = ? AND " + m_secondCol.getName() + " = ?";
	}

	/**
	 * Sets the value of a parameter in a <code>PreparedStatement</code> object using the value of
	 * the contained <code>Column</code> objects.
	 *
	 * @param statement the <code>PreparedStatement</code> object for which a parameter will be set
	 * @param index the offset that indicates the parameter to set
	 * @return the next offset to use when setting parameters
	 * @throws IOException
	 * @throws SQLException
	 */
	@Override
	public int setParameter(PreparedStatement statement, int index) throws IOException, SQLException
	{
		m_firstCol.setParameter(statement, index);
		m_secondCol.setParameter(statement, index + 1);

		return index + 2;
	}

	/**
	 * Returns a string representation of the object. The representation is built using the
	 * <code>toString</code> methods of the contained <code>Column</code> objects.
	 *
	 * @return a string representation of the object
	 */
	@Override
	public String toString()
	{
		return m_firstCol.toString() + " AND " + m_secondCol.toString();
	}
}