/*
 * CancelUsersJobs.java
 */
package org.ngbw.utils;


import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.jobs.Job;


public class CancelUsersJobs 
{
	public static void main(String[] args)
	{
		try {
			if (args.length < 1)
				throw new Exception("usage: CancelUsersJobs username"); 

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			User user = User.findUserByUsername(args[0]);

			if (user == null)
				throw new Exception("Couldn't find a user with username " + args[0]);
				
			Job.cancelAll(user, null);
		}
		catch (Exception err) {
			System.out.println(err.toString());
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
