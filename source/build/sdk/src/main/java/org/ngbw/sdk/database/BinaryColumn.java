/*
 * BinaryColumn.java
 */
package org.ngbw.sdk.database;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Represents a <code>LONGVARBINARY</code> column from a database table.
 *
 * @author Paul Hoover
 *
 */
class BinaryColumn extends StreamColumn<byte[]> {

	// nested classes


	/**
	 *
	 */
	private class BinaryValue extends MemoryValue<byte[]> implements StreamValue<byte[]> {

		// public methods


		/**
		 *
		 * @return
		 */
		@Override
		public InputStream getValueAsStream()
		{
			if (isNull())
				return null;

			return new ByteArrayInputStream(m_memValue);
		}

		/**
		 *
		 * @param value
		 * @throws IOException
		 */
		@Override
		public void setValue(InputStream value) throws IOException
		{
			byte[] newValue = null;

			if (value != null) {
				ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

				copyStream(value, outBytes);

				newValue = outBytes.toByteArray();
			}

			setValue(newValue);
		}

		/**
		 * Sets the value of the object using a row from a <code>ResultSet</code> object. The row used
		 * is the one indicated by the current position of the <code>ResultSet</code> object's cursor.
		 *
		 * @param value the <code>ResultSet</code> object that contains the row
		 * @param index the offset in the row that indicates the column whose value will be assigned to this object
		 * @throws IOException
		 * @throws SQLException
		 */
		@Override
		public void setValue(ResultSet value, int index) throws IOException, SQLException
		{
			byte[] newValue = value.getBytes(index);

			if (m_compressed && newValue != null) {
				InputStream inBuffer = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(newValue)));
				ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

				try {
					copyStream(inBuffer, outBytes);
				}
				finally {
					inBuffer.close();
				}

				newValue = outBytes.toByteArray();
			}

			setValue(newValue);
		}

		/**
		 *
		 * @param statement
		 * @param index
		 * @throws IOException
		 * @throws SQLException
		 */
		@Override
		public void setParamValue(PreparedStatement statement, int index) throws IOException, SQLException
		{
			byte[] value;

			if (!m_compressed)
				value = m_memValue;
			else {
				ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
				OutputStream outBuffer = new BufferedOutputStream(new GZIPOutputStream(outBytes));

				try {
					outBuffer.write(m_memValue, 0, m_memValue.length);
				}
				finally {
					outBuffer.close();
				}

				value = outBytes.toByteArray();
			}

			statement.setBinaryStream(index, new ByteArrayInputStream(value), value.length);
		}

		/**
		 *
		 * @return
		 */
		@Override
		public long getLength()
		{
			return m_memValue.length;
		}
	}


	// data fields


	private final boolean m_compressed;


	// constructors


	/**
	 * Constructs a column representation and assigns a <code>null</code> value to it.
	 *
	 * @param name the name of the column
	 * @param nullable whether the column lacks or has a <code>NOT NULL</code> constraint
	 * @param compressed whether or not data stored in the column is compressed
	 * @param owner a reference to the <code>Row</code> object that owns this object
	 */
	BinaryColumn(String name, boolean nullable, boolean compressed, Row owner)
	{
		super(name, nullable, owner);

		m_compressed = compressed;
	}

	/**
	 * Constructs a column representation and assigns the given value to it.
	 *
	 * @param name the name of the column
	 * @param nullable whether the column lacks or has a <code>NOT NULL</code> constraint
	 * @param compressed whether or not data in the column is compressed
	 * @param owner a reference to the <code>Row</code> object that owns this object
	 * @param value an initial value to assign to the object
	 */
	BinaryColumn(String name, boolean nullable, boolean compressed, Row owner, byte[] value)
	{
		this(name, nullable, compressed, owner);

		assignValue(value);
	}


	// protected methods


	/**
	 * Returns the SQL data type of the column
	 *
	 * @return the SQL data type of the column
	 */
	@Override
	protected int getType()
	{
		return Types.LONGVARBINARY;
	}

	/**
	 *
	 * @return
	 */
	@Override
	protected Value<byte[]> createValue()
	{
		return new BinaryValue();
	}


	// private methods


	/**
	 *
	 * @param inStream
	 * @param outStream
	 * @throws IOException
	 */
	private void copyStream(InputStream inStream, OutputStream outStream) throws IOException
	{
		int bytesRead;
		byte[] readBuffer = new byte[8192];

		while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0)
			outStream.write(readBuffer, 0, bytesRead);
	}
}
