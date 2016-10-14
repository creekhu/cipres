/*
 * ShowUserItems.java
 */
package org.ngbw.utils;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;


/**
 * 
 * @author Paul Hoover
 *
 */
public class ShowUserItems {

	private static final String INDENT = "   ";


	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
				throw new Exception("usage: ShowUserItems username");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			User inspected = User.findUserByUsername(args[0]);

			if (inspected == null)
				throw new Exception("Couldn't find a user with that name");

			List<Folder> folders = inspected.findFolders();

			System.out.println("Folders:\n");

			for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
				Folder current = elements.next();

				if (current.getEnclosingFolderId() != 0)
					continue;

				showTopFolder(folders, current, INDENT);
			}

			for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
				Folder owner = elements.next();

				System.out.println("\nIn folder " + owner.getLabel() + " (" + owner.getFolderId() + "):");
				System.out.println(INDENT + "Data Items:");

				for (Iterator<UserDataItem> items = owner.findDataItems().iterator() ; items.hasNext() ; ) {
					UserDataItem current = items.next();

					System.out.println(INDENT + INDENT + current.getUserDataId() + "\t" + current.getType() + "\t" + current.getLabel().replaceAll("\\s+", " "));
				}

				System.out.println("\n" + INDENT + "Tasks:");

				for (Iterator<Task> tasks = owner.findTasks().iterator() ; tasks.hasNext() ; ) {
					Task current = tasks.next();

					System.out.println(INDENT + INDENT + current.getTaskId() + "\t" + current.getCreationDate() + "\t" + current.getLabel().replaceAll("\\s+", " "));
				}
			}
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}


	private static void showTopFolder(List<Folder> folders, Folder current, String prefix) throws IOException, SQLException
	{
		System.out.println(prefix + current.getLabel() + " (" + current.getFolderId() + ")");

		List<Folder> subFolders = new ArrayList<Folder>();

		for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
			Folder folder = elements.next();

			if (folder.getEnclosingFolderId() == current.getFolderId())
				subFolders.add(folder);
		}

		for (Iterator<Folder> elements = subFolders.iterator() ; elements.hasNext() ; )
			showSubFolder(folders, elements.next(), prefix, elements.hasNext() == false);
	}

	private static void showSubFolder(List<Folder> folders, Folder current, String prefix, boolean last) throws IOException, SQLException
	{
		System.out.println(prefix + " \\_ " + current.getLabel() + " (" + current.getFolderId() + ")");

		List<Folder> subFolders = new ArrayList<Folder>();

		for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
			Folder folder = elements.next();

			if (folder.getEnclosingFolderId() == current.getFolderId())
				subFolders.add(folder);
		}

		for (Iterator<Folder> elements = subFolders.iterator() ; elements.hasNext() ; ) {
			Folder subFolder = elements.next();
			String newPrefix = prefix;

			if (last)
				newPrefix += "    ";
			else
				newPrefix += " |  ";

			showSubFolder(folders, subFolder, newPrefix, elements.hasNext() == false);
		}
	}
}
