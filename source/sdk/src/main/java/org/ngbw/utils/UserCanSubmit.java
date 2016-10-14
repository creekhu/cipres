/*
 * UserCanSubmit.java
 */
package org.ngbw.utils;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.User;


/**
 *
 * @author Paul Hoover
 *
 */
public class UserCanSubmit {

  public static void main(String[] args)
  {
    try {
      if (args.length < 1)
        throw new Exception("usage: UserCanSubmit username [boolean]");

      ConnectionManager.setConnectionSource(new DriverConnectionSource());

      List<User> users = new ArrayList<User>();

      if (args[0].indexOf('*') < 0) {
        User user = User.findUserByUsername(args[0]);

        if (user == null)
          throw new Exception("Couldn't find a user with username " + args[0]);

        users.add(user);
      }
      else {
        String namePattern = args[0].replace('*', '%');
        Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
        Statement selectStmt = null;
        ResultSet rows = null;

        try {
          selectStmt = dbConn.createStatement();
          rows = selectStmt.executeQuery(
              "SELECT user_id " +
              "FROM users " +
              "WHERE username LIKE '" + namePattern + "'"
          );

          while (rows.next()) {
            long userId = rows.getLong(1);

            users.add(new User(userId));
          }
        }
        finally {
          dbConn.close();
        }
      }

      for (User user : users) {
        boolean canSubmit;
        if (args.length > 1)
        {
          canSubmit = Boolean.parseBoolean(args[1]);
          user.setCanSubmit(canSubmit);
          user.save();
        }
        canSubmit = user.canSubmit();
        System.out.println("Task submission " + (canSubmit ? "enabled" : "disabled") + " for user " + user.getUsername());
      }
    }
    catch (Exception err) {
      System.out.println(err.toString());
      err.printStackTrace(System.err);

      System.exit(-1);
    }
  }
}
