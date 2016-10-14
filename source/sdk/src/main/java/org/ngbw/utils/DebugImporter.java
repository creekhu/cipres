/*
 * DebugImporter.java
 */
package org.ngbw.utils;


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

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.UserItemDataRecord;


/**
 *
 * @author Paul Hoover
 *
 */
class DebugImporter {

	private static class SessionDirFilter implements FilenameFilter {

		public boolean accept(File dir, String name)
		{
			return name.matches(SESSION_DIR_PREFIX + "\\S+$");
		}
	}


	private static final String TARGET_FOLDER_LABEL = "Imported from BWB";
	private static final String BWB_ROOT_DIR = "/misc/workbench/3.2/";
	private static final String SESSION_DIR_PREFIX = "^S\\.[0-9]\\.[0-9]_[0-9]+_";
	private static final int MAX_LABEL_LENGTH = 1023;

	private final Workbench m_workbench;


	/**
	 * Constructs an instance of the class.
	 *
	 * @param workbench
	 */
	DebugImporter(Workbench workbench)
	{
		m_workbench = workbench;
	}

	/**
	 * Imports data from the Biology Workbench to the Next Generation Biology Workbench.
	 *
	 * @param username a BWB user name
	 * @param parentFolder a <code>Folder</code> object that will contain the imported data items
	 * @return the number of data items imported
	 * @throws Exception
	 */
	public int importBwbData(String username, Folder parentFolder) throws Exception
	{
		if (!authenticateUser(username))
			throw new Exception("Authentication failed for user " + username);

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
						System.err.println("Truncating a label in session " + folderLabel + " for BWB user " + username + " from " + labelSection.length() + " characters to " + String.valueOf(MAX_LABEL_LENGTH));

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
						System.err.println("Caught an exception while extracting data records:");

						err.printStackTrace(System.err);
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
	 * @return <code>true</code> if the user name was found, and the password matches the stored password
	 * @throws Exception
	 */
	private boolean authenticateUser(String username) throws Exception
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

				return true;
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


	public static void main(String[] args)
	{
		try {
			if (args.length != 2)
				throw new Exception("usage: DebugImporter ngbw_account bwb_account");

			Workbench workbench = Workbench.getInstance();
			User owner = User.findUserByUsername(args[0]);
			Folder homeFolder = owner.getHomeFolder();
			Folder target = homeFolder.findOrCreateSubFolder(TARGET_FOLDER_LABEL);
			int numItems = (new DebugImporter(workbench)).importBwbData(args[1], target);

			System.out.println("Imported " + numItems + " items");
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
