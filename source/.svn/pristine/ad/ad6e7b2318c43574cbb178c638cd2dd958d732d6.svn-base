/*
 * BwbImporter.java
 */
package org.ngbw.sdk;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.UserItemDataRecord;
import org.ngbw.sdk.common.util.InputStreamCollector;


/**
 *
 * @author Paul Hoover
 *
 */
class BwbImporter {

	private static class SessionDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name)
		{
			return name.matches(SESSION_DIR_PREFIX + "\\S+$");
		}
	}


	private static final String BWB_ROOT_DIR = "/misc/workbench/3.2/";
	private static final String SESSION_DIR_PREFIX = "^S\\.[0-9]\\.[0-9]_[0-9]+_";
	private static final int MAX_LABEL_LENGTH = 1023;
	private static final Log m_log = LogFactory.getLog(BwbImporter.class);

	private final Workbench m_workbench;


	/**
	 * Constructs an instance of the class.
	 *
	 * @param workbench
	 */
	BwbImporter(Workbench workbench)
	{
		m_workbench = workbench;
	}

	/**
	 * Imports data from the Biology Workbench to the Next Generation Biology Workbench.
	 *
	 * @param username a BWB user name
	 * @param password a plaintext password
	 * @param parentFolder a <code>Folder</code> object that will contain the imported data items
	 * @return the number of data items imported
	 * @throws Exception
	 * @throws UserAuthenticationException
	 */
	public int importBwbData(String username, String password, Folder parentFolder) throws Exception
	{
		if (!authenticateUser(username, password))
			throw new UserAuthenticationException("Authentication failed for user " + username);

		File dataDir = new File(BWB_ROOT_DIR + "USER/" + username);

		// users who haven't logged in to BWB recently might have had their data erased by the BWB
		// administrator, so having an account doesn't guarantee that there's anything to import
		if (!dataDir.exists() || !dataDir.isDirectory())
			return 0;

		int numItems = 0;
		String[] sessionDirNames = dataDir.list(new SessionDirFilter());
		List<Folder> subFolders = parentFolder.findSubFolders();

		for (int i = 0 ; i < sessionDirNames.length ; i += 1) {
			File sessionFile = new File(dataDir.getAbsolutePath() + "/" + sessionDirNames[i] + "/Session_File");

			if (!sessionFile.exists() || sessionFile.length() < 1)
				continue;

			Folder newFolder = new Folder(parentFolder);
			String folderLabel = createUniqueLabel(subFolders, sessionDirNames[i].replaceFirst(SESSION_DIR_PREFIX, "").replaceAll("_+", " ").trim());

			newFolder.setLabel(folderLabel);

			newFolder.save();

			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sessionFile));

			try {
				while (true) {
					String typeSection = readSection(inputStream);

					// this will only occur if we've reached EOF
					if (typeSection == null)
						break;

					String fromSection = readSection(inputStream);
					String labelSection = readSection(inputStream);
					String sequenceSection = readSection(inputStream);
					DataType dataType;
					EntityType entityType;

					if(typeSection.equalsIgnoreCase("Alignment")) {
						dataType = DataType.SEQUENCE_ALIGNMENT;

						if(fromSection.matches(".+\\sProtein$"))
							entityType = EntityType.PROTEIN;
						else if (fromSection.matches(".+\\sNucleic$"))
							entityType = EntityType.NUCLEIC_ACID;
						else
							entityType = EntityType.UNKNOWN;
					}
					else {
						dataType = DataType.SEQUENCE;

						if(typeSection.equals("Protein"))
							entityType = EntityType.PROTEIN;
						else if (typeSection.equals("Nucleic"))
							entityType = EntityType.NUCLEIC_ACID;
						else
							entityType = EntityType.UNKNOWN;
					}

					if (labelSection.length() > MAX_LABEL_LENGTH) {
						m_log.warn("Truncating a label in session " + folderLabel + " for BWB user " + username + " from " + labelSection.length() + " characters to " + String.valueOf(MAX_LABEL_LENGTH));

						labelSection = labelSection.substring(0, MAX_LABEL_LENGTH);
					}

					UserDataItem newItem = new UserDataItem(newFolder);

					newItem.setLabel(labelSection);
					newItem.setDataFormat(DataFormat.FASTA);
					newItem.setDataType(dataType);
					newItem.setEntityType(entityType);
					newItem.setData(sequenceSection);

					try {
						GenericDataRecordCollection<IndexedDataRecord> records = m_workbench.extractDataRecords(newItem);

						for (Iterator<IndexedDataRecord> elements = records.toList().iterator() ; elements.hasNext() ; )
							newItem.dataRecords().add(new UserItemDataRecord(elements.next(), newItem));
					}
					catch (Exception err) {
						// torpedoed by one of Hannes' RuntimeExceptions. We have no way of knowing
						// if this indicates something serious or not, so we just ignore it
						m_log.warn("Caught an exception while extracting data records", err);
					}

					newItem.metaData().put("importedFrom", "BWB");

					newItem.save();

					numItems += 1;
				}
			}
			finally {
				inputStream.close();
			}
		}

		return numItems;
	}

	/**
	 * Authenticates a BWB user using the given name and password. These credentials are compared to
	 * entries in an Apache htpasswd file that the BWB uses.
	 *
	 * @param username a BWB user name
	 * @param password a plaintext password
	 * @return <code>true</code> if the user name was found, and the password matches the stored password
	 * @throws Exception
	 */
	private boolean authenticateUser(String username, String password) throws Exception
	{
		BufferedReader htpasswdReader = new BufferedReader(new FileReader(BWB_ROOT_DIR + "CONF/htpasswd"));

		try {
			String line;

			while ((line = htpasswdReader.readLine()) != null) {
				int colonChar = line.indexOf(':');

				if (colonChar == -1)
					continue;

				String name = line.substring(0, colonChar);

				if (!name.equals(username))
					continue;

				// passwords in the BWB htpasswd file are encrypted using the system crypt() function, so
				// we need to encrypt the submitted password with the same routine in order to compare it
				// with the password listed for this user in htpasswd. Java doesn't expose crypt(), so we
				// let openssl handle the encryption for us. Note that crypt() stores the random salt value
				// that it used in the first two bytes of the result, and we need to use the salt from the
				// previous encryption in order to get the same result when encrypting the same value again
				String hash = line.substring(colonChar + 1).trim();
				String[] commandArray = new String[6];

				commandArray[0] = "openssl";
				commandArray[1] = "passwd";
				commandArray[2] = "-crypt";
				commandArray[3] = "-salt";
				commandArray[4] = hash.substring(0, 2);
				commandArray[5] = password;

				Process sslJob = Runtime.getRuntime().exec(commandArray);
				Future<String> stdOut = InputStreamCollector.readInputStream(sslJob.getInputStream());
				Future<String> stdErr = InputStreamCollector.readInputStream(sslJob.getErrorStream());
				int exitCode = sslJob.waitFor();

				if (exitCode != 0)
					throw new Exception("Execution of openssl failed: " + stdErr.get());

				return hash.equals(stdOut.get().trim());
			}
		}
		finally {
			htpasswdReader.close();
		}

		return false;
	}

	/**
	 * Ensures that a given label is unique with respect to a given list of folders. If the label is
	 * unique, the unaltered label is returned. If not, a unique version of the label is produced by
	 * appending a numeric value to the original label.
	 *
	 * @param folders a <code>List</code> object containing the folders to search
	 * @param label a <code>String</code> object which is the original label
	 * @return
	 */
	private String createUniqueLabel(List<Folder> folders, String label)
	{
		int suffix = 1;
		String newLabel = label;

		while (labelIsDuplicate(folders, newLabel)) {
			suffix += 1;
			newLabel = label + ' ' + String.valueOf(suffix);
		}

		return newLabel;
	}

	/**
	 * Searches a list of folders for the given label.
	 *
	 * @param folders a <code>List</code> object containing the folders to search
	 * @param label a <code>String</code> object which is the label to search for
	 * @return <code>true</code> if the given label was found in the list of folders
	 */
	private boolean labelIsDuplicate(List<Folder> folders, String label)
	{
		for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
			if (elements.next().getLabel().equals(label))
				return true;
		}

		return false;
	}

	/**
	 * Reads the data from a section of a BWB session file. Each data item stored in a session file has
	 * four sections associated with it, TYPE, FROM, LABEL, and SEQ. Sections are written in the file
	 * in this order. Each section is preceded by the length of the section, in characters. The data
	 * for each section is written as a key / value pair, with the following format:
	 *
	 * 		KEY[ n ]=[ data... ]
	 *
	 * where KEY is one of the section names and n is the ordinal of the data item. There can be any
	 * number of data items stored in a particular session file.
	 *
	 * @param input an <code>InputStream</code> object opened on the session file
	 * @return a <code>String</code> object containing the characters from this section
	 * @throws IOException
	 */
	private String readSection(InputStream input) throws IOException
	{
		int length = 0;

		// the length of a section is a 4-byte value written in network byte order, which is big-endian.
		// The following operations reverse the byte order to little-endian, so hopefully we're running
		// on that sort of architecture
		length |= input.read() << 24;
		length |= input.read() << 16;
		length |= input.read() << 8;
		length |= input.read();

		// we've reached EOF
		if (length < 0)
			return null;

		byte[] buffer = new byte[length];

		input.read(buffer, 0, length);

		int offset = 0;

		// skip over the section key
		while ((char) buffer[offset] != '=')
			offset += 1;

		// skip over the equals sign
		offset += 1;

		return new String(buffer, offset, length - offset);
	}
}
