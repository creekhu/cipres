/*
 * UserDelete.java
 */
package org.ngbw.utils;


import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 *
 */
public class UserDelete 
{
	private static final Log log = LogFactory.getLog(UserDelete.class.getName());

	public static void main(String[] args)
	{
        try 
		{
            if (args.length < 3)
                throw new Exception("usage: UserDelete [-u username | -i uid ]  account|data");

            ConnectionManager.setConnectionSource();

            User user = null;
            if (args[0].equals("-u"))
            {
                user = User.findUserByUsername(args[1]);
            } else if (args[0].equals("-i"))
            {
                user = new User(Long.valueOf(args[1]));
            }
            if (user == null)
            {
                throw new Exception("Couldn't find a user with username/uid" + args[1]);
            }
            String action = args[2];

			if (action.equals("account"))
			{
				user.delete();
				log.debug("deleted account for user " + args[1]);
			} else if (action.equals("data"))
			{
				user.deleteData();
				log.debug("deleted tasks and data for user " + args[1]);
			} 
		}
		catch (Exception err) {
			if (args.length >= 3)
			{
				log.error("Error deleting: " + args[1] + " " + args[2]);
			}
			System.out.println(err.toString());
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
