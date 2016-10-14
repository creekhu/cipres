/*
 * ImportTgusage.java
 *
 * Imports all rows from a csv file produced by running /usr/local/apps/bin/ct-tgusage
 * on dash-login.sdsc.edu.  Expects one argument, name of csv file, on the command line.
 */
package org.ngbw.utils;


import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.Tgusage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.io.File;

/**
 *
 * @author Terri Schwartz
 *
 */
public class ImportTgusage {

	public static void main(String[] args)
	{
		try {
			if (args.length < 1)
				throw new Exception("usage: ImportTgusage file");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			File file = new File(args[0]);
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			String line;
			String[] fields;
			Tgusage tg;
			int totalLines = 0;
			int linesLoaded = 0;
			while (dis.available() != 0)
			{
				totalLines++;
				line = dis.readLine();
				fields = line.split(",");
				for (int i = 0; i < fields.length; i++)
				{
					fields[i] = fields[i].trim();
				}
				System.out.println("\nprocessing: " + line);
				try
				{
					tg = new Tgusage(fields);
					tg.save();
					linesLoaded++;
				}
				catch (SQLException sqlErr) {
					String state = sqlErr.getSQLState();

					// this is an expected error since we may end up running cipres_tgusage on
					// overlapping date ranges and importing.
					if (!state.equals("23000") && !state.equals("23505")) {
						System.out.println("ERROR: " + sqlErr.toString());

						sqlErr.printStackTrace();
					}
				}
				catch (Exception e)
				{
					System.out.println("ERROR: " + e.toString());
					e.printStackTrace();
				}
			}
			System.out.println("Imported " + linesLoaded + " out of " + totalLines + " lines read");
			System.out.println("Duplicate entries (previously loaded) are quietly skipped.");
		}
		catch (Exception err) {
			System.out.println(err.toString());
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
