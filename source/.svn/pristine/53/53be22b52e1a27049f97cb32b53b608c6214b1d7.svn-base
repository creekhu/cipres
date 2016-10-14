package org.ngbw.cipresrest.webresource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.clients.ClientApplication;
import org.ngbw.sdk.core.shared.UserRole;

import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.ApplicationData;
import org.ngbw.restdatatypes.UserData;
import org.ngbw.cipresrest.auth.AuthorizationException;

/*
	For REST Application registration and management. 

	PUT	/v1/application/<username>/<appname> 		(put or post)?  
		Registers or updates a client Application record, identified by appname.
		Requires formdata.  Requires cipresadmin credentials or credentials of
		username, the rest admin user that owns (or will own) this application record.
		Returns ApplicationData record

		I changed this to require cipresAdmin credentials when I wrote the restusers web app.
		I don't see why end users need this api and I don't want users to automatically register
		and activate UMBRELLA apps, or create too many apps.  The easiest way to enforce that
		is to take away access to this endpoint.


	GET /v1/application/<username>/<appname>
		Credential requirement is same as above.
		Returns ApplicationData record.  

	GET /v1/application/<username>
		Credential requirement is same as above.
		Returns ApplicationData record for each of the user's applications.  

	DELETE /v1/application/<username>/<appname>
		useful primarily for testing.  not sure if it should really be exposed.
		Require cipresadmin credentials for now.  Not implmemented yet.

	GET /v1/application/	(with authtype=DIRECT or UMBRELLA query param)
		Returns info about all registered applications having selected authtype.  
		No credentials required.  If cipresadmin credentials are used, returns full 
		info about each app.  Otherwise info is limited.
		May be used to let DIRECT users register account for themselves and select 
		the application they'll be using the account with.
*/

@Path("/v1/application/")
public class CipresApplication 
{
	private static final Log log = LogFactory.getLog(CipresApplication.class.getName());
	@Context SecurityContext securityContext;
	@Context UriInfo uriInfo;

	@DELETE
	@Path("{user}/{appname}")
	public void deleteApplication( @PathParam("user") String username, @PathParam("appname") String appname)
		throws Exception
	{
		securityContext.isUserInRole(UserRole.ADMIN.toString());
		User user = getUser(username);
		Application app = find(appname, user);
		app.delete();
	}

	/* 
		See SecurityFilter - basic auth isn't required, but if admin creds
		are present we return more info.
	*/
	@GET
	@Produces( MediaType.APPLICATION_XML )
	public Collection<ApplicationData> getApplications(
		@DefaultValue("") @QueryParam("authtype") String authtype) throws Exception
	{
		List<Application> apps;
		if (authtype.length() == 0)
		{
			apps = Application.findApplicationsByAuthType(Application.UMBRELLA);
			apps.addAll(Application.findApplicationsByAuthType(Application.DIRECT));
		} else
		{
			apps = Application.findApplicationsByAuthType(authtype);
		}
		boolean restricted = true;
		try
		{
			securityContext.isUserInRole(UserRole.ADMIN.toString());
			log.debug("user is in admin role");
			restricted = false;
		} catch (AuthorizationException ae)
		{
			;
		}

		List<ApplicationData> adlist = new ArrayList<ApplicationData>(apps.size());
		for (Application app : apps)
		{
			adlist.add(createApplicationData(app, new User(app.getAuthUserId()), restricted));
		}
		return adlist;
	}

	/* 
		Return list of user's applications. May return an empty list.
		Requires users credentials or cipresadmin credentials
	*/
	@GET
	@Path("{user}")
	@Produces( MediaType.APPLICATION_XML )
	public Collection<ApplicationData> getUsersApplications(@PathParam("user") String username) throws Exception
	{
		securityContext.isUserInRole(UserRole.STANDARD.toString());
		User user = getUser(username);
		List<Application> apps = Application.findApplicationsByUserId(user.getUserId());

		boolean restricted = false;
		List<ApplicationData> adlist = new ArrayList<ApplicationData>(apps.size());
		for (Application app : apps)
		{
			adlist.add(createApplicationData(app, user, restricted));
		}
		return adlist;
	}


	/*
		Returns specified application.
	*/
	@GET
	@Path("{user}/{appname}")
	@Produces( MediaType.APPLICATION_XML )
	public ApplicationData getApplication(
			@PathParam("user") String username, 
			@PathParam("appname") String appname) throws Exception
	{
		Collection<ApplicationData> adlist = getUsersApplications(username);
		for (ApplicationData ad : adlist)
		{
			if (ad.app_name.equals(appname))
			{
				return ad;
			}
		}
		throw new CipresException("Application " + appname + " with owner " + username + " not found.", 
			Status.NOT_FOUND, ErrorData.NOT_FOUND);
	}


	@POST
	@Path("{user}/{appname}")
	@Produces( MediaType.APPLICATION_XML )
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED)
	public ApplicationData addOrUpdateApp( 
						@PathParam("user") String username, 
						@PathParam("appname") String appname, 
						MultivaluedMap<String, String> fp) throws Exception
	{
		//securityContext.isUserInRole(UserRole.STANDARD.toString());
		securityContext.isUserInRole(UserRole.ADMIN.toString());
		User user = getUser(username);
		Application app = Application.find(appname);

		if (app == null)
		{
			return addApplication(user, appname, fp);
		} else if (app.getAuthUserId() != user.getUserId())
		{
			/*
				app exists, but is owned by someone else.  Go ahead and try to create the app
				and the ClientApplication.createApplication() method will report the 'appname
				alread in use error'
			*/
			return addApplication(user, appname, fp);
		} else
		{
			return updateApplication(user, app, fp);
		} 
	}

	private ApplicationData addApplication(User user, String appname, MultivaluedMap<String, String> fp) 
		throws Exception
	{
		// throw exception if attempt to set activate by anyone except a CipresAdmin user
		// Applcations created by non cipres-admin user are created in inactive state. 
		Boolean activate = false;
		if (fp.getFirst("activate") != null)
		{
			securityContext.isUserInRole(UserRole.ADMIN.toString());
			activate = Boolean.valueOf(fp.getFirst("activate"));
		}
		// throw exception if not logged in as cipres admin or User matching username in url.
		securityContext.isUserInRole(UserRole.STANDARD.toString());
		Application app = ClientApplication.createApplication(user, appname, 
				fp.getFirst("longname"), fp.getFirst("authtype"), fp.getFirst("website"), 
				fp.getFirst("comment"),
				activate);
		return createApplicationData(app, user, false);
	}

	/*
		Note - 
			'generateId' must equal 'true' or it will be evaluated to false.  That's the way java Boolean.valueOf()
		works.  Only send a generateId parameter when you want to change the id of an existing application.

			'activate' must be 'true' to be evaluated to true.
	*/
	private ApplicationData updateApplication(User user, Application app, MultivaluedMap<String, String> fp) 
			throws Exception
	{
		// throw exception if attempt to set activate by anyone except a CipresAdmin user
		Boolean activate = null;
		if (fp.getFirst("activate") != null)
		{
			securityContext.isUserInRole(UserRole.ADMIN.toString());
			activate = Boolean.valueOf(fp.getFirst("activate"));
		}
		// For everything else Make sure user is a cipres admin or same user as in url (standard behavior).
		securityContext.isUserInRole(UserRole.STANDARD.toString());
		ClientApplication.updateApplication(user, app, 
				fp.getFirst("longname"), fp.getFirst("authtype"), fp.getFirst("website"), 
				fp.getFirst("comment"), 
				activate,
				Boolean.valueOf(fp.getFirst("generateId")));
		return createApplicationData(app, user, false);
	}


	private  ApplicationData createApplicationData(Application app, User user, boolean restricted)
	{
		ApplicationData ad = new ApplicationData();

		ad.app_name = app.getName();
		ad.longname = app.getLongName();
		ad.website_url = app.getWebsiteUrl();
		ad.active = app.isActive();
		ad.auth_type = app.getAuthType();
		ad.selfUri = getApplicationUri(app, user);
		ad.date_created = app.getDateCreated();
		if (restricted == false)
		{
			ad.adminUserUri = getAdminUserUri(user);
			ad.app_id = app.getAppId();
			//ad.auth_type = app.getAuthType();
			ad.admin_user_name = user.getUsername();
			ad.comment = app.getComment();
		}
		return ad;
	}

	private LinkData getApplicationUri(Application app, User user)
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresApplication.class).path(user.getUsername()).path(app.getName()).build();
		return new LinkData(uri.toString(), ApplicationData.name, app.getName());
	}

	private LinkData getAdminUserUri(User user)
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresUser.class).path(user.getUsername()).build();
		return new LinkData(uri.toString(), UserData.name, user.getUsername());
	}

	private Application find(String appname, User user) throws Exception
	{
		Application app = Application.find(appname);
		if (app != null && (app.getAuthUserId() == user.getUserId()))
		{
			return app;
		}
		throw new CipresException("Application" + appname + "with owner " + user.getUsername() + " not found.", 
			Status.NOT_FOUND, ErrorData.NOT_FOUND);
	}

	private User getUser(String username) throws Exception
	{
		User user = User.findUserByUsername(username);
		if (user == null)
		{
			throw new CipresException("User " + username + " not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		return user;
	}

}

