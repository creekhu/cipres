/*
 * UserCountryIs.java
 */
package org.ngbw.utils;


import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.User;


/**
 *
 * @author Paul Hoover
 *
 */
public class UserCountryIs {

	/**
	 *
	 * @param args
	 */
  public static void main(String[] args)
  {
    try {
      if (args.length < 1 || args.length > 2)
        throw new Exception("usage: UserCountryIs username [country]");

      ConnectionManager.setConnectionSource(new DriverConnectionSource());

      User user = User.findUserByUsername(args[0]);

      if (user == null)
        throw new Exception("Couldn't find a user with username " + args[0]);

      if (args.length == 1)
      	System.out.println("Country for user " + user.getUsername() + " is " + user.getCountry());
      else {
      	String oldCountry = user.getCountry();

      	if (oldCountry == null || !oldCountry.equals(args[1])) {
      		user.setCountry(args[1]);
      		user.save();

      		System.out.println("Changed country for user " + user.getUsername() + " to " + args[1]);
      	}
      	else
      		System.out.println("User " + user.getUsername() + " already has country " + args[1]);
      }
    }
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(1);
		}
  }
}
