
/*
 * UmbrellaUser.java
 */
package org.ngbw.sdk.clients;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.ConstraintException;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.database.StaleRowException;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.tool.Tool;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.tool.TaskValidator;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.core.shared.UserRole;


/**
 * @author Terri Liebowitz Schwartz
 *
 */

/*
	Use these methods to create and update umbrella user records as they enforce
	additional constraints and hardcode certain fields.

	Umbrella accounts are created (and updated) automatically when a REST client
	that is a web app with it's own user accounts, sends endusername, email and
	institution in request headers.  

	Unfortunately database/User is a versioned object - it isn' really designed to
	handle concurrent updates so we need to handle that here.  Other types of
	user objects are only created/updated manually via a web portal so concurrency isn't
	an issue for them.
*/
public class UmbrellaUser 
{
	private static final Log log = LogFactory.getLog(UmbrellaUser.class.getName());
	private String appname;

	public static User find(String appname, String endUserName) throws Exception
	{
		String username = appname + "." + endUserName;
		User user = User.findUserByUsername(username);
		if (user != null && user.getRole() != UserRole.REST_END_USER_UMBRELLA)
		{
			throw new Exception("User '" + username + "' is not of type REST_END_USER_UMBRELLA"); 
		}
		return user;
	}

	/*
		Creates a new umbrella user, or if a constraint violation occurs because someone else
		already created the user record, return the existing record.  
		
		Note that Constraint violation can also occur if the email address is already in use by a different user
		of this umbrella app.  If we don't find the user we assume that's email already in use is the constraint violation
		and return null.

		Before calling this ensure that CountryCode.isValid(country) is true.  Or pass in null for country.
	*/
	public static User create(String appname, String username, String email, String institution, String country) throws Exception
	{
		// username needs to be unique so prefix with appname.
		String uniqueUsername = appname + "." + username;

		User user = new User();
		user.setUsername(uniqueUsername);
		user.setEmail(email);
		if (institution != null)
		{
			user.setInstitution(institution);
		}
		if (country != null)
		{
			user.setCountry(country);
		}
		user.setFirstName("umbrella");
		user.setLastName("user");

		String password = "REST" + username + Calendar.getInstance().getTimeInMillis();
		user.setPassword(password);

		user.setRole(UserRole.REST_END_USER_UMBRELLA);
		user.setActive(true);
		user.setCanSubmit(true);
		user.setUmbrellaAppname(appname);

		try
		{
			user.save();
		}
		catch(java.sql.SQLIntegrityConstraintViolationException cv)
		{
			log.debug("Failed to create user: '" + uniqueUsername + "'");
			log.debug(cv.toString());
			log.debug("Trying to find user: '" +  uniqueUsername +  "'");
			user = find(appname, uniqueUsername);
			if (user == null)
			{
				throw new ConstraintException("Failed to create or retrieve user record. " +   
					"The email address may already in use by a different user of the same application.");
			}
		}
		return user;
	}

	/*
		Updates if possible, but if StaleRowException is thrown, we quietly ignore it. 
		This should be ok since updates of email or institution shouldn't be
		frequent and most likely whoever did the update that caused the StaleRowException
		set the same values we would have.

		Before calling this ensure that CountryCode.isValid(country) is true.  Or pass in null for country.
	*/
	public static User update(User user, String email, String institution, String country) throws Exception
	{
        if (!user.getEmail().equals(email))
        {
            user.setEmail(email);
        }
        if (institution != null && (user.getInstitution() == null || !user.getInstitution().equals(institution)))
        {
            user.setInstitution(institution);
        }
        if (country != null && (user.getCountry() == null || !user.getCountry().equals(country)))
        {
            user.setCountry(country);
        }
		try
		{
        	user.save();
		}
		catch(java.sql.SQLIntegrityConstraintViolationException cv)
		{
			log.debug("Failed to update user: '" + user.getUsername() + "'");
			log.debug(cv.toString());
			throw new ConstraintException("Error accessing the user record. " +   
					"The same email address may already in use by a different user.");
		}
		catch(StaleRowException sr)
		{
			log.debug("Unable to update user " + user.getUsername() + " because another request already did it.");
		}
		return user;
	}
}	
