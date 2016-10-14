package org.ngbw.cipresrest.webresource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.clients.Client;
import org.ngbw.sdk.core.shared.UserRole;

import org.ngbw.cipresrest.auth.AuthorizationException;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.UserData;

/*

	For managing users with role = REST_USER.  (Not REST_END_USER_UMBRELLA though). 

	POST	/v1/user/<username> 		
		registers username if it doesn't exist, otherwise updates username.
		Requires formdata. Password is sent in form data unencrypted, but this is all over ssl
		so that should be fine.  Note that unexpected form parameters are quietly ignored.
		Requires cipresadmin credentials.

		Optional action=add or action=update parameter.  If not specified, we update or create
		depending on whether username already exists.

	GET /v1/user/<username>
		Retrieves all the info, minus password, requires that user send his credentials.
		When invoked by cipresadmin user, includes the encrypted password in the returned user data.

	DELETE /v1/user/<username>
		useful primarily for testing.  not sure if it should really be exposed.
		Require cipresadmin credentials for now.

	GET /v1/user/	(with role=UserRole query param)
		Requires cipres admin credentials.
		Returns full info about all registered users (with specified role)

	The restusers web application uses these methods.  Users need to use the restusers web application
	to register a new user and to reset a forgotten password, as these functions can't be accomplished
	by (non cipresadmin) users via the api alone.
*/


@Path("/v1/user/")
public class CipresUser 
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresUser.class.getName());
	@Context SecurityContext securityContext;
	@Context UriInfo uriInfo;

	/* 
		Note that we have not been deleting users in cipres so it isn't well tested.
		Also, it may be dangerous to delete and let user_id be reused - not sure.
		I'm just implementing this so we can use it in test scripts.  Deleting a 
		user may end up deleting many associated records.
		This lets you delete *any* type of user - not checking since only cipres
		admin can use the method and should cipres admin should have the sense know what's up.
	*/
	@DELETE
	@Path("{user}")
	public void deleteUser(@PathParam("user") String username) throws Exception
	{
		securityContext.isUserInRole(UserRole.ADMIN.toString());

		// this finds activated and unactivated users both
		User user = User.findAllUserByUsername(username);
		if (user == null)
		{
			throw new CipresException("User not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		user.delete();
	}

 	@GET
    @Path("{user}")
    @Produces( MediaType.APPLICATION_XML )
    public UserData getUser(@PathParam("user") String username) throws Exception
    {
		// If user is cipres admin, then restricted = false and we'll return user's encryptedPass
		// and activation code
		boolean restricted = false;
		try
		{
			securityContext.isUserInRole(UserRole.ADMIN.toString());
		}
		catch (AuthorizationException ae)
		{
			restricted = true;
		}
        securityContext.isUserInRole(UserRole.STANDARD.toString());
        User user = User.findUserByUsername(username);
        if (user == null)
        {
            throw new CipresException("User " + username + " not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
        }
        return createUserData(user, restricted);
    }

	/*
		Requires cipresadmin credentials.

		By default, returns a list of all activated user accounts.  
		Use option query param to switch the search from activated accounts to unactivated, or all.
		Use filter query param to limit the search to a specific role and/or email address.

		QueryParameters:
			- filter
				values: "" (default), role:REST_USER, role:REST_END_USER_UMBRELLA, role:ADMIN, email:an_email_address
					
			- option 
				values: activated (default), inactivated, includeInactivated

		Unrecognized query params and values are quietly ignored.  TODO: should handle
		this consistently throughout the api.  Do we ignore these or through an exception?
		
	*/

	// these two class members are set by parseFilter() and used in getUsers()
	String filterEmail = null;
	UserRole filterRole = null;

    @GET
    @Produces( MediaType.APPLICATION_XML )
    public List<UserData> getUsers(
		@DefaultValue("") @QueryParam("filter") final List<String> filter,
		@DefaultValue("activated") @QueryParam("option") final String option) throws Exception
    {
        securityContext.isUserInRole(UserRole.ADMIN.toString());
        List<User> users;

		// this will set filterEmail and filterRole
		log.debug("received filter:'" + filter + "'");
		parseFilter(filter);

		if (option.equals("inactivated"))
		{
			users = User.findInactiveUsersByRole(filterRole);
		} else if (option.equals("includeInactivated"))
		{ 
			users = User.findAllUsersByRole(filterRole);
		} else
		{
			users = User.findActiveUsersByRole(filterRole);
		}
		List<UserData> retval = new ArrayList<UserData>(users.size());
        for (User u : users)
        {
			if (filterEmail != null && !filterEmail.equals(u.getEmail()))
			{
				continue;
			}
            try
            {
                // If role is invalid enum due to old data in the db, createData will throw exception.
                retval.add(createFullUserData(u));
            }
            catch(Exception e)
            {
                log.warn(e.toString());
            }
        }
        return retval;
    }

	/*
		If multiple roles set, uses the last.  Same for email, only uses one, the last.
		Ignores any errors or unexpected values.

		Each string in filter should be of the form:
			role:a_user_role
		or
			email:an_email_address


		Returns a UserRole object or null, if a valid role filter isn't found.
	*/
	public void parseFilter(List<String>filter)
	{
		String roleString;

		for (String f : filter)
		{
			String[] parts = f.split(":");
			if (parts.length != 2)
			{
				continue;
			}
			if (parts[0].equals("role"))
			{
				roleString = parts[1];
				try
				{
					filterRole = UserRole.valueOf(parts[1]);
				}
				catch(IllegalArgumentException ie)
				{
					// logging this as error because the calling method is only allowed for
					// cipresadmins and our code should know better.
					log.error("Filter " + f + " is not a valid role", ie);
				}
			} else if (parts[0].equals("email"))
			{
				// todo: be careful of sql injections here.
				filterEmail = parts[1];
			}
		}
	}


	@POST
	@Path("{user}")
	@Produces( MediaType.APPLICATION_XML )
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED)
	public UserData addOrUpdateUser( 
						@PathParam("user") String username, 
						MultivaluedMap<String, String> fp) throws Exception
	{
		// action is optional. Can use it to make sure you don't accidentally update an or create. 
		// when you meant to do the opposite. 
		String action = fp.getFirst("action");
		if (action != null && action.equals("activate"))
		{
			return activateDirectEndUser(username, fp);
		}

		User user = User.findUserByUsername(username);

		if (action != null)
		{
			if (action.equals("add") )
			{
				if (user != null)
				{
					throw new ValidationException("username", username + " already exists.");
				}
			} else if (action.equals("update"))
			{
				if (user == null)
				{
					throw new ValidationException("username", username + " doesn't exist.");
				}
			} else 
			{
				throw new ValidationException("action", "Invalid value.");
			}
		}

		// this is a request to add a new user.  
		if (user == null)
		{
				return addDirectEndUser(username, fp);
		} else // update or activate existing user 
		{
				return updateDirectEndUser(user, fp);
		}

	}

	/*
		Anyone can call this, no auth required. NO - CHANGING THIS: ONLY CIPRES ADMIN, I don't
		want someone programmatically creating lots of unactivated accounts.

		This implementation requires that user activate his account.  To change that simply change
		last parameter to createUser to false.

		Form parameter 'activationUrl' is a url in the registration application that we will put in
		the email to the user to activate his account.  Registration application must post the user's 
		activation code to this url. 

		In theory if activationUrl were null we could just email the user the activation code and 
		he would have to figure out how to post it to the url.  Or we could create an email that does
		a javascript post to our url.  For now though, I'm requiring activationUrl.
	*/
	private UserData addDirectEndUser(String username, MultivaluedMap<String, String> fp) throws Exception
	{
		securityContext.isUserInRole(UserRole.ADMIN.toString());

		boolean activationRequired = false;
		String activationUrl = fp.getFirst("activation_url");	
		if (activationUrl != null && activationUrl.length() > 0)
		{
			activationRequired = true;
		}
		User user = Client.createUser(username, fp.getFirst("password"), 
				fp.getFirst("first_name"), fp.getFirst("last_name"), fp.getFirst("institution"), 
				fp.getFirst("street_address"), fp.getFirst("city"), fp.getFirst("state"), fp.getFirst("country"), 
				fp.getFirst("mailcode"), fp.getFirst("zip_code"),
				fp.getFirst("area_code"), fp.getFirst("phone_number"), fp.getFirst("email"), 
				fp.getFirst("website_url"), fp.getFirst("comment"),
				activationRequired);
		if (activationRequired)
		{
			String url = UriBuilder.fromUri(activationUrl). queryParam("user", user.getUsername()).
					queryParam("code", user.getActivationCode()).build().toString();
			log.debug("Activation url is: " + url);
			// todo: if we definitely can't email user can we return a specific error code?
			Client.emailActivationCode(url, user);
		}
		return createFullUserData(user);
	}

	// todo - better error messages
	private UserData activateDirectEndUser(String username, MultivaluedMap<String, String>fp) throws Exception
	{
		User user = User.findAllUserByUsername(username);
		if (user == null)
		{
			//throw new ValidationException("username", "User " + username + " not found");
			throw new CipresException("User " + username + " not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		if (user.getRole() != UserRole.REST_USER)
		{
			throw new ValidationException("username", "User " + username + " has the wrong role");
		}
		String code = fp.getFirst("code");
		User activatedUser =  Client.activate(user, code);
		return createUserData(activatedUser);
	}

	private UserData updateDirectEndUser(User user, MultivaluedMap<String, String> fp) throws Exception
	{
		boolean restricted = false;
		try
		{
			securityContext.isUserInRole(UserRole.ADMIN.toString());
		}
		catch (AuthorizationException ae)
		{
			restricted = true;
		}
        securityContext.isUserInRole(UserRole.STANDARD.toString());
		if (user.getRole() != UserRole.REST_USER)
		{
			throw new ValidationException("username", "User " +user.getUsername() + " has the wrong role");
		}
		Client.updateUser(user, fp.getFirst("password"), 
				fp.getFirst("first_name"), fp.getFirst("last_name"), fp.getFirst("institution"), 
				fp.getFirst("street_address"), fp.getFirst("city"), fp.getFirst("state"), fp.getFirst("country"), 
				fp.getFirst("mailcode"), fp.getFirst("zip_code"),
				fp.getFirst("area_code"), fp.getFirst("phone_number"), fp.getFirst("email"), 
				fp.getFirst("website_url"), fp.getFirst("comment"),

				//only cipres admin can modify these:
				(restricted ? null : fp.getFirst("xsedeId")), (restricted ? null : fp.getFirst("canSubmit")));

		return restricted ? createUserData(user) : createFullUserData(user);
	}


	private  UserData createUserData(User user) throws Exception
	{
		return createUserData(user, true);
	}

	private  UserData createFullUserData(User user) throws Exception
	{
		return createUserData(user, false);
	}

	private  UserData createUserData(User user, boolean restricted) throws Exception
	{
		UserData ud = new UserData();
		ud.selfUri = getUserUri(user);
		ud.username = user.getUsername();
		ud.first_name = user.getFirstName();
		ud.last_name = user.getLastName();
		ud.institution = user.getInstitution();
		ud.street_address = user.getStreetAddress();
		ud.city = user.getCity();
		ud.state = user.getState();
		ud.country = user.getCountry();
		ud.mailcode = user.getMailCode();
		ud.zip_code = user.getZipCode();
		ud.area_code = user.getAreaCode();
		ud.phone_number = user.getPhoneNumber();
		ud.email = user.getEmail();
		ud.website_url = user.getWebsiteUrl();
		ud.comment = user.getComment();
		ud.role = user.getRole().toString();
		ud.last_login = user.getLastLogin();
		ud.active = user.isActivated();
		ud.canSubmit = user.canSubmit();
		ud.date_created = user.getDateCreated();
		if (!restricted)
		{
			ud.encryptedPassword = user.getPassword();
			ud.xsedeId = user.getAccount(User.TERAGRID); // may be null
			ud.activationCode = user.getActivationCode();
			ud.activationSent = user.getActivationSent();
		}
		return ud;
	}

	private LinkData getUserUri(User user)
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresUser.class).path(user.getUsername()).build();
		return new LinkData(uri.toString(), UserData.name, user.getUsername());
	}

}

