/*
 * ClientApplication.java
 */
package org.ngbw.sdk.clients;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.NotExistException;
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

public class ClientApplication 
{
	private static final Log log = LogFactory.getLog(ClientApplication.class.getName());


	// Returns the guiid generated as this application's ID 
	public static Application createApplication(
				User app_admin, 
				String appname,
				String longname,
				String authtype, 
				String website,
				String comment,
				boolean activate) throws Exception
	{
		List<FieldError> errors = new ArrayList<FieldError>();

		// validate appname
		if (appname == null || appname.length() == 0)
		{
			errors.add(new FieldError("appname", "Appname is required."));
			throw new ValidationException(errors);
		} 
		// is appname valid?
		if  (!appname.matches("[a-zA-Z0-9_]+"))
		{
			errors.add(new FieldError("appname", "Appname is limited to letters, numbers and underscore."));
		}
		if (Application.find(appname) != null)
		{
			errors.add(new FieldError("appname", appname + " is already in  use."));
		}
		Application app = new Application(appname);
		app.setActive(false); 	// inactive until we manually vet the information
		app.setAppId(generateAppKey(appname));
		app.setLongName(longname);
		app.setAuthType(authtype);
		app.setAuthUserId(app_admin.getUserId());
		app.setWebsiteUrl(website);
		app.setComment(comment);
		app.setActive(activate);

		validate(errors, app, app_admin);
		if (errors.size() > 0)
		{
			throw new ValidationException(errors);
		}
		// can throw ValidationException if fields are too long.  
		app.save();

		return app;
	}

	/*
		Cant change admin user, appname (because it's the primary key) or active flag.  
		Can change all other fields.
		Pass null for string fields you don't want to change.
	*/
	public static Application updateApplication(
				User app_admin,
				Application app,
				String longname,
				String authtype, 
				String website,
				String comment,
				Boolean activate,
				Boolean generateNewId) throws Exception
	{
		List<FieldError> errors = new ArrayList<FieldError>();
		if (generateNewId == true)
		{
			String appkey = generateAppKey(app.getName());
			app.setAppId(appkey);
		}
		if (longname != null) app.setLongName(longname);
		if (authtype != null) app.setAuthType(authtype);
		if (website != null) app.setWebsiteUrl(website);
		if (comment != null) app.setComment(comment);
		if (activate != null) app.setActive(activate);

		validate(errors, app, app_admin);
		if (errors.size() > 0)
		{
			throw new ValidationException(errors);
		}
		// can throw ValidationException if fields are too long.
		app.save();
		return app;
	}


	private static List<FieldError> validate(
			List<FieldError> errors,
			Application app,
			User app_admin) throws Exception
	{
		/*
			I don't think it matters, why not let anyone create and own applications.
		if ( app_admin.getRole() != UserRole.REST_ADMIN ) 
		{
			errors.add(new FieldError("app_admin", app_admin.getUsername() + " is not an application administrator."));
		}
		*/
		// validate longname 
		if (app.getLongName() == null || app.getLongName().length() == 0)
		{
			errors.add(new FieldError("longname", "Long Name is required."));
		}

		if (app.getAuthType() == null || (!app.getAuthType().equals(Application.DIRECT) && !app.getAuthType().equals(Application.UMBRELLA)))
		{
			errors.add(new FieldError("authtype", "Must be " + Application.DIRECT + " or " +
				Application.UMBRELLA));
		}
		return errors;
	}


	private static String generateAppKey(String appname)
	{
		String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
		return appname + "-" + uuid;
	}

}
