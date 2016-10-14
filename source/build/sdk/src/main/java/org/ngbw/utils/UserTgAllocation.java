/*
 * UserTgAllocation.java
 */
package org.ngbw.utils;


import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.User;


/**
 *
 * @author Terri Liebowitz Schwartz
 *
 */
public class UserTgAllocation {

	public static void main(String[] args)
	{
		String account = null;
		try {
			if (args.length < 1)
				throw new Exception("usage: UserTgAllocation username [account | NULL]");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			User user = User.findUserByUsername(args[0]);
			if (user == null)
				throw new Exception("Couldn't find a user with username " + args[0]);
			if (args.length > 1)
			{
				account = args[1];
				if (account.equals("NONE") || account.equals("NULL"))
				{
					account = null;
				}
				user.setAccount(User.TERAGRID, account);
				user.save();
			}
			boolean canSubmit = user.canSubmit();
			account = user.getAccount(User.TERAGRID);
			System.out.println("Teragrid account for user '" + args[0] + "' is set to '" +  account + "'");
			System.out.println("Task submission " + (canSubmit ? "enabled" : "disabled"));
		}
		catch (Exception err) {
			System.out.println(err.toString());
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
