/*
 * UserDelete.java
 */
package org.ngbw.utils;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.jobs.Job;


/*
	Examples:
		-m -u terri /Users/terri/foo.txt
		-c -u terri /Users/terri/abc.txt
*/
public class UserDataItemImport
{
	private static final Log log = LogFactory.getLog(UserDataItemImport.class.getName());
	private static final String FOLDER = "importedData";
	private static boolean move = false;

	public static void main(String[] args) throws IOException
	{
		BufferedInputStream is = null;
        try
		{
            if (args.length < 4)
                throw new Exception("usage: UserDataItemImport [-m|-c] [-u username | -i uid ] path.\n -m means move the data, -c means copy it");

            ConnectionManager.setConnectionSource();

            User user = null;
            if (args[0].equals("-m"))
			{
				move = true;
			} else if (!args[0].equals("-c"))
			{
                throw new Exception("usage: UserDataItemImport [-m|-c] [-u username | -i uid ] path.\n -m means move the data, -c means copy it");
			}

            if (args[1].equals("-u"))
            {
                user = User.findUserByUsername(args[2]);
            } else if (args[1].equals("-i"))
            {
                user = new User(Long.valueOf(args[2]));
            }
            if (user == null)
            {
                throw new Exception("Couldn't find a user with username/uid" + args[1]);
            }
            String path = args[3];
			String filename;
			File file = new File(path);
			if (!file.canRead() || !file.isFile())
			{
				throw new Exception("File cannot be read or is not a regular file.");
			}
			filename = file.getName();
			System.out.println("Importing the data ...");

			/*
				Can save an empty udi and add data later, as demonstrated here.
			UserDataItem udi = new UserDataItem(user.getHomeFolder().findOrCreateSubFolder(FOLDER));
			udi.setData("".getBytes());
			udi.setLabel(filename);
			udi.save();

			udi.setData(file);
			udi.save();
			*/

			if (move)
			{
				UserDataItem udi = new UserDataItem(path, user.getHomeFolder().findOrCreateSubFolder(FOLDER));
				udi.setLabel(filename);
				udi.save();
				System.out.println("Imported to user data ID " + udi.getUserDataId() + " to folder " + FOLDER);
			} else
			{
				is = new BufferedInputStream(new FileInputStream(file));
				System.out.println("Importing the data ...");
				UserDataItem udi = Job.createUserDataItem(user, is, filename, FOLDER);
				System.out.println("Imported to user data ID " + udi.getUserDataId() + " to folder " + FOLDER);
			}
		}
		catch (Exception err)
		{
			System.out.println(err.toString());
			err.printStackTrace(System.err);

			System.exit(-1);
		}
		finally
		{
			if (is != null)
			{
				is.close();
			}
		}
	}
}
