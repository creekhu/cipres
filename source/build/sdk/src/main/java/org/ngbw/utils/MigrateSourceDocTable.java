/*
 * MigrateSourceDocTable.java
 */
package org.ngbw.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.ConnectionSource;
import org.ngbw.sdk.database.DriverConnectionSource;


/**
 *
 * @author Paul Hoover
 *
 */
public class MigrateSourceDocTable {

	private static final String FILE_ROOT_PROPERTY = ConnectionSource.DATABASE_PROP_PREFIX + "fileRoot";
	private static final int MAX_MINOR_DIRS = 1024;
	private static final int MAX_FILES = 2048;
	private static String m_fileRoot;
	private static int m_majorDirName;
	private static int m_minorDirName;

	private final Map<Integer, Long> m_documentIdMap = new TreeMap<Integer, Long>();


	// constructors


	/**
	 *
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private MigrateSourceDocTable() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (m_fileRoot == null) {
			Properties configProps = ConnectionSource.getDatabaseConfiguration();
			String fileRoot = configProps.getProperty(FILE_ROOT_PROPERTY);

			if (fileRoot == null || fileRoot.length() == 0)
				throw new WorkbenchException("Property " + FILE_ROOT_PROPERTY + " is empty");

			String fileSeparator = System.getProperty("file.separator");

			if (fileRoot.endsWith(fileSeparator))
				m_fileRoot = fileRoot;
			else
				m_fileRoot = fileRoot + fileSeparator;

			(new File(m_fileRoot + String.valueOf(m_majorDirName) + fileSeparator + String.valueOf(m_minorDirName))).mkdirs();
		}

		// only one connection will be needed at any one time, so we don't need a pool of them
		ConnectionManager.setConnectionSource(new DriverConnectionSource());
	}


	// public methods


	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			(new MigrateSourceDocTable()).migrate();
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}

	/**
	 *
	 * @throws IOException
	 * @throws SQLException
	 */
	public void migrate() throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();

		try {
			System.out.println("creating new table...");

			createNewTable(dbConn);

			System.out.println("migrating user data...");

			moveUserData(dbConn);

			System.out.println("migrating task input...");

			moveInputDocs(dbConn);

			System.out.println("migrating task output...");

			moveOutputDocs(dbConn);

			System.out.println("dropping old table...");

			dropOldTable(dbConn);

			System.out.println("finished");
		}
		finally {
			dbConn.close();
		}
	}


	// private methods


	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createNewTable(Connection dbConn) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement(
				"ALTER TABLE source_documents RENAME old_source_documents"
		);

		try {
			updateStmt.executeUpdate();
			updateStmt.close();

			updateStmt = dbConn.prepareStatement(
					"CREATE TABLE source_documents ( " +
						"SOURCE_DOCUMENT_ID  bigint(20) AUTO_INCREMENT NOT NULL, " +
						"FILENAME            varchar(1023) NOT NULL, " +
						"LENGTH              bigint(20) NOT NULL, " +
						"PRIMARY KEY (SOURCE_DOCUMENT_ID) " +
					")"
			);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @return a <code>String</code> object with a random value
	 */
	private static String getRandomName()
	{
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 *
	 * @return a <code>File</code> object opened on the new file
	 * @throws IOException
	 */
	private static synchronized File getNewFile() throws IOException
	{
		String fileSeparator = System.getProperty("file.separator");
		String dirName = m_fileRoot + String.valueOf(m_majorDirName) + fileSeparator + String.valueOf(m_minorDirName);
		String baseName = getRandomName();
		File result;

		while (true) {
			while (true) {
				String fileName = dirName + fileSeparator + baseName + ".bin.gz";

				result = new File(fileName);

				if (result.createNewFile())
					break;

				baseName = getRandomName();
			}

			if ((new File(dirName)).list().length <= MAX_FILES)
				break;

			result.delete();

			if (m_minorDirName < MAX_MINOR_DIRS - 1)
				m_minorDirName += 1;
			else {
				m_majorDirName += 1;
				m_minorDirName = 0;
			}

			dirName = m_fileRoot + String.valueOf(m_majorDirName) + fileSeparator + String.valueOf(m_minorDirName);

			(new File(dirName)).mkdirs();
		}

		return result;
	}

	/**
	 *
	 * @param b
	 * @return
	 */
	private int unsigned(byte b)
	{
		return b & 0xFF;
	}

	/**
	 * reads the uncompressed length from the GZIP footer
	 *
	 * @param deflated
	 * @return
	 */
	private int readUncompressedLength(byte[] deflated)
	{
		int length = 0;

		length |= unsigned(deflated[deflated.length - 4]);
		length |= unsigned(deflated[deflated.length - 3]) << 8;
		length |= unsigned(deflated[deflated.length - 2]) << 16;
		length |= unsigned(deflated[deflated.length - 1]) << 24;

		return length;
	}

	/**
	 * reads the CRC32 checksum from the GZIP footer
	 *
	 * @param deflated
	 * @return
	 */
	private int readChecksum(byte[] deflated)
	{
		int checksum = 0;

		checksum |= unsigned(deflated[deflated.length - 8]);
		checksum |= unsigned(deflated[deflated.length - 7]) << 8;
		checksum |= unsigned(deflated[deflated.length - 6]) << 16;
		checksum |= unsigned(deflated[deflated.length - 5]) << 24;

		return checksum;
	}

	/**
	 *
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private String writeSourceDocFile(byte[] data) throws IOException
	{
		File outFile = getNewFile();
		BufferedOutputStream outBuffer = new BufferedOutputStream(new FileOutputStream(outFile));

		try {
			outBuffer.write(data);
		}
		finally {
			outBuffer.close();
		}

		String fullName = outFile.getAbsolutePath();
		String fileSeparator = System.getProperty("file.separator");
		int offset = fullName.lastIndexOf(fileSeparator);

		offset = fullName.lastIndexOf(fileSeparator, offset - 1);
		offset = fullName.lastIndexOf(fileSeparator, offset - 1);

		return fullName.substring(offset + 1);
	}

	/**
	 *
	 * @param dbConn
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private long addSourceDocRow(Connection dbConn, byte[] data) throws IOException, SQLException
	{
		int length = readUncompressedLength(data);
		String fileName = writeSourceDocFile(data);
		PreparedStatement insertStmt = dbConn.prepareStatement("INSERT INTO source_documents (FILENAME, LENGTH) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		ResultSet newKey = null;

		try {
			insertStmt.setString(1, fileName);
			insertStmt.setLong(2, length);

			insertStmt.executeUpdate();

			newKey = insertStmt.getGeneratedKeys();

			if (!newKey.next())
				return 0;

			return newKey.getLong(1);
		}
		finally {
			if (newKey != null)
				newKey.close();

			insertStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param userDataId
	 * @param sourceDocumentId
	 * @throws SQLException
	 */
	private void updateUserDataSourceDocId(Connection dbConn, long userDataId, long sourceDocumentId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE userdata SET SOURCE_DOCUMENT_ID = ? WHERE USERDATA_ID = ?");

		try {
			updateStmt.setLong(1, sourceDocumentId);
			updateStmt.setLong(2, userDataId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param userDataId
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveUserDataRow(long userDataId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet dataRow = null;

		try {
			selectStmt = dbConn.prepareStatement(
					"SELECT DATA " +
					"FROM userdata " +
						"INNER JOIN old_source_documents ON userdata.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID " +
					"WHERE USERDATA_ID = ?"
			);

			selectStmt.setLong(1, userDataId);

			dataRow = selectStmt.executeQuery();

			if (!dataRow.next())
				return;

			byte[] data = dataRow.getBytes(1);
			int checksum = readChecksum(data);
			boolean duplicate = m_documentIdMap.containsKey(checksum);
			long sourceDocumentId;

			if (duplicate)
				sourceDocumentId = m_documentIdMap.get(checksum);
			else {
				sourceDocumentId = addSourceDocRow(dbConn, data);

				m_documentIdMap.put(checksum, sourceDocumentId);
			}

			updateUserDataSourceDocId(dbConn, userDataId, sourceDocumentId);
		}
		finally {
			if (dataRow != null)
				dataRow.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveUserData(Connection dbConn) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT USERDATA_ID " +
				"FROM userdata " +
					"INNER JOIN old_source_documents ON userdata.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID "
		);
		ResultSet dataRows = null;

		try {
			dataRows = selectStmt.executeQuery();

			while (dataRows.next())
				moveUserDataRow(dataRows.getLong(1));
		}
		finally {
			if (dataRows != null)
				dataRows.close();

			selectStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param inputId
	 * @param sourceDocumentId
	 * @throws SQLException
	 */
	private void updateInputDocsSourceDocId(Connection dbConn, long inputId, long sourceDocumentId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE task_input_source_documents SET SOURCE_DOCUMENT_ID = ? WHERE INPUT_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, sourceDocumentId);
			updateStmt.setLong(2, inputId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param inputId
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveInputDocRow(long inputId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet dataRow = null;

		try {
			selectStmt = dbConn.prepareStatement(
					"SELECT DATA " +
					"FROM task_input_source_documents " +
						"INNER JOIN old_source_documents ON task_input_source_documents.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID " +
					"WHERE INPUT_DOCUMENT_ID = ?"
			);

			selectStmt.setLong(1, inputId);

			dataRow = selectStmt.executeQuery();

			while (dataRow.next()) {
				byte[] data = dataRow.getBytes(2);
				int checksum = readChecksum(data);
				boolean duplicate = m_documentIdMap.containsKey(checksum);
				long sourceDocumentId;

				if (duplicate)
					sourceDocumentId = m_documentIdMap.get(checksum);
				else {
					sourceDocumentId = addSourceDocRow(dbConn, data);

					m_documentIdMap.put(checksum, sourceDocumentId);
				}

				updateInputDocsSourceDocId(dbConn, inputId, sourceDocumentId);
			}
		}
		finally {
			if (dataRow != null)
				dataRow.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveInputDocs(Connection dbConn) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT INPUT_DOCUMENT_ID " +
				"FROM task_input_source_documents " +
					"INNER JOIN old_source_documents ON task_input_source_documents.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID "
		);
		ResultSet dataRows = null;

		try {
			dataRows = selectStmt.executeQuery();

			while (dataRows.next())
				moveInputDocRow(dataRows.getLong(1));
		}
		finally {
			if (dataRows != null)
				dataRows.close();

			selectStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param outputId
	 * @param sourceDocumentId
	 * @throws SQLException
	 */
	private void updateOutputDocsSourceDocId(Connection dbConn, long outputId, long sourceDocumentId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE task_output_source_documents SET SOURCE_DOCUMENT_ID = ? WHERE OUTPUT_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, sourceDocumentId);
			updateStmt.setLong(2, outputId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveOutputDocRow(long outputId) throws IOException, SQLException
	{
		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet dataRow = null;

		try {
			selectStmt = dbConn.prepareStatement(
					"SELECT DATA " +
					"FROM task_output_source_documents " +
						"INNER JOIN old_source_documents ON task_output_source_documents.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID " +
					"WHERE OUTPUT_DOCUMENT_ID = ?"
			);

			selectStmt.setLong(1, outputId);

			dataRow = selectStmt.executeQuery();

			if (!dataRow.next())
				return;

			byte[] data = dataRow.getBytes(2);
			int checksum = readChecksum(data);
			boolean duplicate = m_documentIdMap.containsKey(checksum);
			long sourceDocumentId;

			if (duplicate)
				sourceDocumentId = m_documentIdMap.get(checksum);
			else {
				sourceDocumentId = addSourceDocRow(dbConn, data);

				m_documentIdMap.put(checksum, sourceDocumentId);
			}

			updateOutputDocsSourceDocId(dbConn, outputId, sourceDocumentId);
		}
		finally {
			if (dataRow != null)
				dataRow.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws IOException
	 * @throws SQLException
	 */
	private void moveOutputDocs(Connection dbConn) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement(
				"SELECT OUTPUT_DOCUMENT_ID " +
				"FROM task_output_source_documents " +
					"INNER JOIN old_source_documents ON task_output_source_documents.SOURCE_DOCUMENT_ID = old_source_documents.SOURCE_DOCUMENT_ID "
		);
		ResultSet dataRows = null;

		try {
			dataRows = selectStmt.executeQuery();

			while (dataRows.next())
				moveOutputDocRow(dataRows.getLong(1));
		}
		finally {
			if (dataRows != null)
				dataRows.close();

			selectStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void dropOldTable(Connection dbConn) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement(
				"DROP TABLE old_source_documents"
		);

		try {
			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}
}
