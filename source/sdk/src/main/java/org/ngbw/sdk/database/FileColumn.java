/*
 * FileColumn.java
 */
package org.ngbw.sdk.database;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;


/**
 * Represents a <code>LONGVARBINARY</code> column from a database table. The binary value is stored
 * in a file, rather than the database, and the value of the column is the name of the file stored as
 * a <code>VARCHAR</code>.
 *
 * @author Paul Hoover
 *
 */
class FileColumn extends StreamColumn<byte[]> {

	// nested classes


	/**
	 *
	 */
	private class FileValue implements StreamValue<byte[]> {

		// public methods


		/**
		 *
		 * @return
		 */
		@Override
		public byte[] getValue()
		{
			try {
				InputStream inStream = getValueAsStream();

				if (inStream == null)
					return null;

				try {
					int bytesRead;
					byte[] readBuffer = new byte[8192];
					ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

					while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0)
						outBytes.write(readBuffer, 0, bytesRead);

					return outBytes.toByteArray();
				}
				finally {
					inStream.close();
				}
			}
			catch (IOException ioErr) {
				m_log.error("", ioErr);

				return null;
			}
		}

		/**
		 *
		 * @return
		 * @throws IOException
		 */
		@Override
		public InputStream getValueAsStream() throws IOException
		{
			if (m_fileName == null)
				return null;

			InputStream fileStream = new FileInputStream(m_fileName);
			InputStream inStream;

			if (m_compressed)
				inStream = new BufferedInputStream(new GZIPInputStream(fileStream));
			else
				inStream = new BufferedInputStream(fileStream);

			return inStream;
		}

		/**
		 *
		 * @param value
		 */
		@Override
		public void setValue(byte[] value)
		{
			try {
				InputStream inStream = null;

				if (value != null)
					inStream = new ByteArrayInputStream(value);

				setValue(inStream);
			}
			catch (IOException ioErr) {
				m_log.error("", ioErr);
			}
		}

		/**
		 *
		 * @param value
		 * @throws IOException
		 */
		@Override
		public void setValue(InputStream value) throws IOException
		{
			if (value == null)
				value = new ByteArrayInputStream(new byte[0]);

			File newFile = createTempFile();
			OutputStream outFile = new FileOutputStream(newFile);
			OutputStream outStream;

			if (m_compressed)
				outStream = new BufferedOutputStream(new GZIPOutputStream(outFile));
			else
				outStream = new BufferedOutputStream(outFile);

			m_streamLength = 0;

			try {
				int bytesRead;
				byte[] readBuffer = new byte[8192];
				MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

				while ((bytesRead = value.read(readBuffer, 0, readBuffer.length)) > 0) {
					outStream.write(readBuffer, 0, bytesRead);
					shaDigest.update(readBuffer, 0, bytesRead);

					m_streamLength += bytesRead;
				}

				m_signature = Hex.encodeHexString(shaDigest.digest());
			}
			catch (NoSuchAlgorithmException algErr) {
				throw new WorkbenchException(algErr);
			}
			finally {
				outStream.close();
			}

			if (m_isModified)
				(new File(m_fileName)).delete();
			else
				m_oldFileName = m_fileName;

			m_fileName = newFile.getAbsolutePath();
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
			setValue(m_fileRoot + value.getString(index));
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
			if (m_isModified) {
				String fileName = createFileName();
				File newFile = new File(fileName);
				File tempFile = new File(m_fileName);

				if (!newFile.exists()) {
					if (!tempFile.renameTo(newFile))
						throw new IOException("failed to rename " + tempFile.getAbsolutePath() + " to " + newFile.getAbsolutePath());
				}
				else
					tempFile.delete();

				m_fileName = fileName;

				if (m_oldFileName == null && !m_owner.isNew())
					m_oldFileName = m_fileRoot + fetchDbFileName(statement.getConnection());
			}

			statement.setString(index, m_fileName.substring(m_fileRoot.length()));
		}

		/**
		 *
		 * @return
		 */
		@Override
		public long getLength()
		{
			return m_streamLength;
		}

		/**
		 *
		 * @return
		 */
		@Override
		public boolean isNull()
		{
			return m_fileName == null;
		}

		/**
		 *
		 * @param other
		 * @return
		 */
		@Override
		public boolean isEqual(byte[] other)
		{
			if (other == null)
				return isNull();

			if (isNull())
				return false;

			try {
				MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

				shaDigest.update(other);

				String otherSignature = Hex.encodeHexString(shaDigest.digest());

				return m_signature.equals(otherSignature);
			}
			catch (NoSuchAlgorithmException algErr) {
				throw new WorkbenchException(algErr);
			}
		}

		/**
		 *
		 */
		@Override
		public void reset()
		{
			if (m_isModified)
				(new File(m_fileName)).delete();

			m_fileName = null;
			m_oldFileName = null;
			m_signature = null;
			m_streamLength = 0;
		}

		/**
		 *
		 * @param fileName
		 * @throws IOException
		 */
		public void setValueFromFile(String fileName) throws IOException
		{
			if (m_compressed) {
				File importedFile = new File(fileName);
				InputStream fileStream = new FileInputStream(importedFile);

				try {
					setValue(fileStream);
				}
				finally {
					fileStream.close();
				}

				importedFile.delete();
			}
			else
				setValue(fileName);
		}


		// private methods


		/**
		 *
		 * @param fileName
		 * @throws IOException
		 */
		private void setValue(String fileName) throws IOException
		{
			if (m_isModified)
				(new File(m_fileName)).delete();

			m_fileName = fileName;
			m_oldFileName = null;
			m_signature = null;
			m_streamLength = 0;

			InputStream inStream = getValueAsStream();

			if (inStream == null)
				return;

			try {
				int bytesRead;
				byte[] readBuffer = new byte[8192];
				MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

				while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0) {
					shaDigest.update(readBuffer, 0, bytesRead);

					m_streamLength += bytesRead;
				}

				m_signature = Hex.encodeHexString(shaDigest.digest());
			}
			catch (NoSuchAlgorithmException algErr) {
				throw new WorkbenchException(algErr);
			}
			finally {
				inStream.close();
			}
		}
	}


	// data fields


	private static final Log m_log = LogFactory.getLog(FileColumn.class);
	private static final String FILE_ROOT_PROPERTY = ConnectionSource.DATABASE_PROP_PREFIX + "fileRoot";
	private static String m_fileRoot;
	private static String m_tempDirName;
	private final boolean m_compressed;
	private boolean m_cleanup;
	private String m_fileName;
	private String m_oldFileName;
	private String m_signature;
	private long m_streamLength;


	static {
		try {
			Properties configProps = ConnectionSource.getDatabaseConfiguration();
			String fileRoot = configProps.getProperty(FILE_ROOT_PROPERTY);

			if (fileRoot == null || fileRoot.length() == 0)
				throw new Exception("Property " + FILE_ROOT_PROPERTY + " is empty");

			String fileSeparator = System.getProperty("file.separator");

			if (fileRoot.endsWith(fileSeparator))
				m_fileRoot = fileRoot;
			else
				m_fileRoot = fileRoot + fileSeparator;

			m_tempDirName = m_fileRoot + "temp";

			File tempDir = new File(m_tempDirName);

			if (!tempDir.exists())
				tempDir.mkdir();
		}
		catch (Exception err) {
			m_log.error("", err);
		}
	}


	// constructors


	/**
	 * Constructs a column representation and assigns a <code>null</code> value to it.
	 *
	 * @param name the name of the column
	 * @param nullable whether the column lacks or has a <code>NOT NULL</code> constraint
	 * @param compressed whether or not data stored in the column is compressed
	 * @param owner a reference to the <code>Row</code> object that owns this object
	 */
	FileColumn(String name, boolean nullable, boolean compressed, Row owner)
	{
		super(name, nullable, owner);

		m_compressed = compressed;
		m_cleanup = true;
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
	FileColumn(String name, boolean nullable, boolean compressed, Row owner, byte[] value)
	{
		this(name, nullable, compressed, owner);

		assignValue(value);
	}


	// public methods


	/**
	 *
	 * @return
	 */
	public String getSignature()
	{
		try {
			if (!m_populated && !m_owner.isNew())
				assignDbValue();

			return m_signature;
		}
		catch (Exception err) {
			m_log.error("", err);

			return null;
		}
	}

	/**
	 *
	 */
	public void cancelCleanup()
	{
		m_cleanup = false;
	}

	/**
	 *
	 */
	@Override
	public void finishUpdate()
	{
		super.finishUpdate();

		if (m_isModified) {
			if (m_cleanup) {
				if (m_oldFileName != null && !m_oldFileName.equals(m_fileName))
					(new File(m_oldFileName)).delete();
			}
			else
				m_cleanup = true;
		}
	}

	/**
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void setValue(String fileName) throws IOException
	{
		((FileValue)m_value).setValueFromFile(fileName);

		m_isModified = true;
		m_populated = true;
	}


	// package methods


	/**
	 *
	 * @param fileName
	 */
	static void delete(String fileName)
	{
		(new File(m_fileRoot + fileName)).delete();
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
		return Types.VARCHAR;
	}

	/**
	 *
	 * @return
	 */
	@Override
	protected Value<byte[]> createValue()
	{
		return new FileValue();
	}


	// private methods


	/**
	 *
	 * @param dbConn
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private String fetchDbFileName(Connection dbConn) throws IOException, SQLException
	{
		Column<String> dbFileName = new StringColumn(getName(), false, 1023);

		(new SelectOp(m_owner.getTableName(), m_owner.getKey(), dbFileName)).execute(dbConn);

		return dbFileName.getValue();
	}

	/**
	 *
	 * @return
	 */
	private String createFileName()
	{
		String suffix = ".bin";

		if (m_compressed)
			suffix += ".gz";

		String majorDir = m_signature.substring(0, 2);
		String minorDir = m_signature.substring(2, 4);
		String fileSeparator = System.getProperty("file.separator");
		File subDir = new File(m_fileRoot + majorDir + fileSeparator + minorDir);

		if (!subDir.exists())
			subDir.mkdirs();

		return subDir.getAbsolutePath() + fileSeparator + m_signature + suffix;
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	private File createTempFile() throws IOException
	{
		String prefix = "temp-";
		String suffix = ".bin";

		if (m_compressed)
			suffix += ".gz";

		File tempDir = new File(m_tempDirName);

		return File.createTempFile(prefix, suffix, tempDir);
	}
}
