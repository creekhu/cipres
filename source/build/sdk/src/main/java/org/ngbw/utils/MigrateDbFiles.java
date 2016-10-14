/*
 * MigrateDbFiles.java
 */
package org.ngbw.utils;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.apache.commons.codec.binary.Hex;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.ConnectionSource;
import org.ngbw.sdk.database.DatabaseTools;
import org.ngbw.sdk.database.DriverConnectionSource;


/**
 *
 * @author Paul Hoover
 *
 */
public class MigrateDbFiles {

	// nested classes


	/**
	 *
	 */
	private static class RegExFilter implements FilenameFilter {

		private final Pattern m_pattern;


		/**
		 *
		 * @param expression
		 */
		public RegExFilter(String expression)
		{
			m_pattern = Pattern.compile(expression);
		}


		/**
		 *
		 * @param dir
		 * @param name
		 * @return
		 */
		public boolean accept(File dir, String name)
		{
			return m_pattern.matcher(name).matches();
		}
	}

	/**
	 *
	 */
	private static class RegExDirFilter implements FileFilter {

		private final Pattern m_pattern;


		/**
		 *
		 * @param expression
		 */
		public RegExDirFilter(String expression)
		{
			m_pattern = Pattern.compile(expression);
		}


		/**
		 *
		 * @param pathname
		 * @return
		 */
		@Override
		public boolean accept(File pathname)
		{
			if (!pathname.isDirectory())
				return false;

			return m_pattern.matcher(pathname.getName()).matches();
		}
	}


	// data fields


	private static final String FILE_ROOT_PROPERTY = ConnectionSource.DATABASE_PROP_PREFIX + "fileRoot";
	private static final int FETCH_SIZE = 100;


	// public methods


	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
			Statement selectStmt = null;
			ResultSet rows = null;

			try {
				dbConn.setAutoCommit(false);

				if (!DatabaseTools.columnExists(dbConn, "source_documents", "SIGNATURE")) {
					System.out.println("altering source_documents table...");

					alterSourceDocTable(dbConn);
				}

				System.out.println("migrating documents...");

				selectStmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

				selectStmt.setFetchSize(FETCH_SIZE);

				rows = selectStmt.executeQuery(
						"SELECT SOURCE_DOCUMENT_ID, FILENAME, SIGNATURE " +
						"FROM source_documents"
				);

				String fileRoot = getFileRoot();
				String fileSeparator = System.getProperty("file.separator");
				int numMigrated = 0;
				int numFailed = 0;

				while (rows.next()) {
					long sourceDocId = 0;
					String dbFileName = null;
					String dbSignature = null;

					try {
						sourceDocId = rows.getLong(1);
						dbFileName = rows.getString(2);
						dbSignature = rows.getString(3);

						if (dbSignature != null)
							continue;

						String signature = computeSignature(fileRoot + dbFileName);
						long duplicateId = findDuplicate(dbConn, signature);

						if (duplicateId > 0) {
							updateUserDataRefs(dbConn, duplicateId, sourceDocId);
							updateTaskInputRefs(dbConn, duplicateId, sourceDocId);
							updateTaskOutputRefs(dbConn, duplicateId, sourceDocId);
							deleteSourceDocRecord(dbConn, sourceDocId);

							(new File(fileRoot + dbFileName)).delete();
						}
						else {
							String fileName = createFileName(signature, fileRoot);
							File newFile = new File(fileRoot + fileName);
							File oldFile = new File(fileRoot + dbFileName);

							if (!newFile.exists())
								oldFile.renameTo(newFile);
							else
								oldFile.delete();

							updateSourceDocRecord(dbConn, sourceDocId, fileName, signature);
						}

						dbConn.commit();

						numMigrated += 1;
					}
					catch (Exception err) {
						System.err.print("migration failed");

						if (sourceDocId > 0)
							System.err.print(" for source document " + sourceDocId);

						System.err.println(": " + err.getLocalizedMessage());

						if (err instanceof ZipException && dbFileName != null)
							(new File(fileRoot + dbFileName)).delete();

						dbConn.rollback();

						numFailed += 1;
					}

					int totalProcessed = numMigrated + numFailed;

					if (totalProcessed % 1000 == 0)
						System.out.println("processed " + totalProcessed + " documents...");
				}

				System.out.println("altering source_documents.signature column...");

				setSignatureNotNull(dbConn);

				System.out.println("cleaning up...");

				int numExamined = 0;
				int numOrphans = 0;
				int numEmptyDirs = 0;

				File[] majorDirs = (new File(fileRoot)).listFiles(new RegExDirFilter("[0-9a-fA-F]+"));

				for (File majorDir : majorDirs) {
					File[] minorDirs = majorDir.listFiles(new RegExDirFilter("[0-9a-fA-F]+"));

					for (File minorDir : minorDirs) {
						String minorDirFullName = minorDir.getAbsolutePath();
						String minorDirName = minorDirFullName.substring(fileRoot.length());
						String[] fileNames = minorDir.list(new RegExFilter(".+\\.bin\\.gz"));

						for (String name : fileNames) {
							String dbFileName = minorDirName + fileSeparator + name;

							if (isOrphan(dbConn, dbFileName)) {
								String fullName = minorDirFullName + fileSeparator + name;

								(new File(fullName)).delete();

								numOrphans += 1;
							}
						}

						if (minorDir.list().length < 1) {
							minorDir.delete();

							numEmptyDirs += 1;
						}

						numExamined += 1;

						if (numExamined % 1000 == 0)
							System.out.println("cleaned up " + numExamined + " directories...");
					}

					if (majorDir.list().length < 1) {
						majorDir.delete();

						numEmptyDirs += 1;
					}

					numExamined += 1;
				}

				System.out.println("removed " + numOrphans + " orphaned files and " + numEmptyDirs + " empty directories");

				System.out.println("finished, migrated " + numMigrated + " documents, failed to migrate " + numFailed + " documents");
			}
			finally {
				if (rows != null)
					rows.close();

				if (selectStmt != null)
					selectStmt.close();

				dbConn.close();
			}
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(1);
		}
	}


	// private methods


	/**
	 *
	 * @return
	 * @throws Exception
	 */
	private static String getFileRoot() throws Exception
	{
		Properties configProps = ConnectionSource.getDatabaseConfiguration();
		String fileRoot = configProps.getProperty(FILE_ROOT_PROPERTY);

		if (fileRoot == null || fileRoot.length() == 0)
			throw new Exception("Property " + FILE_ROOT_PROPERTY + " is empty");

		String fileSeparator = System.getProperty("file.separator");

		if (!fileRoot.endsWith(fileSeparator))
			fileRoot += fileSeparator;

		return fileRoot;
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private static void alterSourceDocTable(Connection dbConn) throws SQLException
	{
		Statement updateStmt = dbConn.createStatement();

		try {
			updateStmt.executeUpdate("ALTER TABLE source_documents ADD COLUMN SIGNATURE VARCHAR(255)");
			updateStmt.executeUpdate("CREATE INDEX SD_SIGNATURE_INDEX ON source_documents (SIGNATURE)");

			dbConn.commit();
		}
		catch (SQLException sqlErr) {
			dbConn.rollback();

			throw sqlErr;
		}
		finally {
			updateStmt.close();
		}
}

	/**
	 *
	 * @param signature
	 * @param fileRoot
	 * @return
	 */
	private static String createFileName(String signature, String fileRoot)
	{
		String majorDir = signature.substring(0, 2);
		String minorDir = signature.substring(2, 4);
		String fileSeparator = System.getProperty("file.separator");
		File subDir = new File(fileRoot + majorDir + fileSeparator + minorDir);

		if (!subDir.exists())
			subDir.mkdirs();

		return majorDir + fileSeparator + minorDir + fileSeparator + signature + ".bin.gz";
	}

	/**
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private static String computeSignature(String fileName) throws IOException, NoSuchAlgorithmException
	{
		InputStream inStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(fileName)));

		try {
			int bytesRead;
			byte[] readBuffer = new byte[8192];
			MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

			while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) > 0)
				shaDigest.update(readBuffer, 0, bytesRead);

			return Hex.encodeHexString(shaDigest.digest());
		}
		finally {
			inStream.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param signature
	 * @return
	 * @throws SQLException
	 */
	private static long findDuplicate(Connection dbConn, String signature) throws SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT SOURCE_DOCUMENT_ID FROM source_documents WHERE SIGNATURE = ?");
		ResultSet row = null;

		try {
			selectStmt.setString(1, signature);

			row = selectStmt.executeQuery();

			if (!row.next())
				return 0;

			return row.getLong(1);
		}
		finally {
			if (row != null)
				row.close();

			selectStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param sourceDocId
	 * @param fileName
	 * @param signature
	 * @throws SQLException
	 */
	private static void updateSourceDocRecord(Connection dbConn, long sourceDocId, String fileName, String signature) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE source_documents SET FILENAME = ?, SIGNATURE = ? WHERE SOURCE_DOCUMENT_ID = ?");

		try {
			updateStmt.setString(1, fileName);
			updateStmt.setString(2, signature);
			updateStmt.setLong(3, sourceDocId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param newSourceDocId
	 * @param oldSourceDocId
	 * @throws SQLException
	 */
	private static void updateUserDataRefs(Connection dbConn, long newSourceDocId, long oldSourceDocId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE userdata SET SOURCE_DOCUMENT_ID = ? WHERE SOURCE_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, newSourceDocId);
			updateStmt.setLong(2, oldSourceDocId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param newSourceDocId
	 * @param oldSourceDocId
	 * @throws SQLException
	 */
	private static void updateTaskInputRefs(Connection dbConn, long newSourceDocId, long oldSourceDocId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE task_input_source_documents SET SOURCE_DOCUMENT_ID = ? WHERE SOURCE_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, newSourceDocId);
			updateStmt.setLong(2, oldSourceDocId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param newSourceDocId
	 * @param oldSourceDocId
	 * @throws SQLException
	 */
	private static void updateTaskOutputRefs(Connection dbConn, long newSourceDocId, long oldSourceDocId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE task_output_source_documents SET SOURCE_DOCUMENT_ID = ? WHERE SOURCE_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, newSourceDocId);
			updateStmt.setLong(2, oldSourceDocId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param sourceDocId
	 * @throws SQLException
	 */
	private static void deleteSourceDocRecord(Connection dbConn, long sourceDocId) throws SQLException
	{
		PreparedStatement updateStmt = dbConn.prepareStatement("DELETE FROM source_documents WHERE SOURCE_DOCUMENT_ID = ?");

		try {
			updateStmt.setLong(1, sourceDocId);

			updateStmt.executeUpdate();
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private static void setSignatureNotNull(Connection dbConn) throws SQLException
	{
		Statement updateStmt = dbConn.createStatement();

		try {
			updateStmt.executeUpdate("UPDATE source_documents SET SIGNATURE = '' WHERE SIGNATURE IS NULL");
			updateStmt.executeUpdate("ALTER TABLE source_documents MODIFY COLUMN SIGNATURE VARCHAR(255) NOT NULL");

			dbConn.commit();
		}
		catch (SQLException sqlErr) {
			dbConn.rollback();

			throw sqlErr;
		}
		finally {
			updateStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @param fileName
	 * @return
	 * @throws SQLException
	 */
	private static boolean isOrphan(Connection dbConn, String fileName) throws SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT SOURCE_DOCUMENT_ID FROM source_documents WHERE FILENAME = ?");
		ResultSet row = null;

		try {
			selectStmt.setString(1, fileName);

			row = selectStmt.executeQuery();

			if (!row.next())
				return true;

			return false;
		}
		finally {
			if (row != null)
				row.close();

			selectStmt.close();
		}
	}
}
