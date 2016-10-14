/*
*/
package  org.ngbw.cipresrest.auth;


import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.server.ContainerRequest;
import org.ngbw.sdk.common.util.SuperString;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;


/*
	Looks like filters are almost singletons.  There is one instance to handle
	incoming requests and one to handle responses.  ( In our case, with SecurityFilter,  
	there is no response handler.)  Thus Filter classes should not have member variables.
*/
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter 
{
	private static final Log log = LogFactory.getLog(SecurityFilter.class.getName());

	@Override
    public void  filter(ContainerRequestContext requestContext) throws IOException
	{
		log.debug("In security filter");

		boolean userapi = false;
		boolean jobapi = false;
		boolean appapi = false;
		boolean publicapi = false;
		boolean toolapi = false;
		boolean jobapiGlob= false;
		boolean fileapi = false;

		//	Relative uris start with: "/v1/... "
		String tmp = requestContext.getUriInfo().getPath();
		UriInfo uriInfo = requestContext.getUriInfo();
		String method = requestContext.getMethod();
		List<PathSegment> pathSegs = uriInfo.getPathSegments();
		MultivaluedMap<String, String> pathParams = uriInfo.getPathParameters();
		Request request = requestContext.getRequest();

		// for debugging only
		dumpRequestHeaders(requestContext);

		/*
			Method will be OPTIONS when browser is doing a "preflight" of an ajax CORS request.
			In that case we don't want to execute any resources, we just want to return CORS headers,
			so I call abortWith(Response.ok) and our cors response filter will handle the rest.  
			I don't think there's any reason to check the basic auth creds or check if the url matches 
			a resource.
		*/
		if (method.equals("OPTIONS"))
		{
			log.debug("method=OPTIONS, aborting request.");
			requestContext.abortWith(Response.ok().build());
			return;
		}

		/*
			We set publicapi=true if the url MAY allow public access.  The method resource method
			itelf can still call isUserInRole if it decides authentication is needed.

			Post to /user is allowed w/o authentication to register direct end users.
			Credentials are always required to look at/modify a specific user's data (i.e when
			there's a username in the path).  Credentials are required to get any data about usrers.

			PathSegments are literal, fixed parts of the url while pathParams are variable/templated parts of 
			the url, like the username.  Note that neither of these the query params.
		*/
		for (PathSegment ps : pathSegs)
		{
			if (ps.getPath().equals("job"))
			{
				jobapi = true;
			} else if (ps.getPath().equals("user"))
			{
				userapi = true;
			} else if (ps.getPath().equals("application"))
			{
				appapi = true;
			} else if (ps.getPath().equals("tool"))
			{
				toolapi = true;
				publicapi = true;
			} else if (ps.getPath().equals("file"))
			{
				fileapi = true;
			}
		}

		// GET of root api, at .../cipresrest/v1.  First pathseq is "v1" for all our urls.
		// For the root, there will be only the "v1" or there will be a 2nd segment that is empty.
		if (method.equals("GET"))
		{
			if (pathSegs.size() == 1 || (pathSegs.size() == 2 && pathSegs.get(1).getPath().equals("")))
			{
				publicapi = true;
			}
		}
		if (pathSegs.size() == 0 && method.equals("GET"))
		{
			publicapi = true;
		}
		if ((userapi) && method.equals("POST"))
		{
			// Let users who don't have an account yet create one.  If request tries to modify an account
			// we'll catch that is isUserInRole(), called from the method body.
			publicapi = true;
		} 
		if (appapi && method.equals("GET") && pathParams.size() == 0)
		{
			//	Anyone can get a list of the registered applications.  
			publicapi = true;
		}
		if (jobapi && pathParams.size() == 0)
		{
			/*
				Special case to allow umbrella app owner to get status of multiple user's jobs in a single
				call to GET /cipresrest/v1/job.  Appkey is required, end user headers are not.
			*/
			jobapiGlob = true;
		}

		log.debug("jobapi=" + jobapi + ", userapi=" + userapi + ", appapi=" + appapi + 
			", toolapi=" + toolapi + ", fileapi=" + fileapi + ", publicapi=" + publicapi );

		// Do http basic auth: user specified in auth headers must match  a user in cipres db.
		AuthHelper.SecurityUser su = null;
		try
		{
			su = authenticate(request);
			requestContext.setProperty(AuthHelper.USER, su.user);
		}
		catch (AuthenticationException ae)
		{
			if (publicapi == false)
			{
				throw ae;
			}
			log.debug("Eating the authentication exception because this api is public.");
		}

		/*
			Job management api requires that logged in user either be a CIPRES ADMIN (in which case
			he shouldn't submit jobs) or that special headers are present to specify the application
			and the end user associated with the request.  

			Handling File api the same way.
		*/
		if (jobapi == true || fileapi == true) 
		{

			// validate appkey header
			Application application  = validateApplication(su, request);

			requestContext.setProperty(AuthHelper.APPLICATION, application);
			if (application != null && !jobapiGlob)
			{
				/*
					If app is registered to use umbrella auth, additional headers should be 
					present to specify the end user.  If app is uses direct auth, end user 
					is same as the one who logged in with basic auth.  getEndUser checks
					which type of auth should be in use and returns the end user or throws exception.
				*/
				su = AuthHelper.getEndUser(su, application, 
									getHeader(request, AuthHelper.H_EU),
									getHeader(request, AuthHelper.H_EMAIL),
									getHeader(request, AuthHelper.H_INST),
									getHeader(request, AuthHelper.H_COUNTRY));
				requestContext.setProperty(AuthHelper.USER, su.user);
			}
		}

		// Individual resources will later call isUserInRole to verify that the user has specific type of
		// permission (role) required by the resource.  We must set a securityContext even if we don't
		// have a user logged in, otherwise some default securitycontext is set for us.
		requestContext.setSecurityContext(new Authorizer(su, uriInfo));
    }

	private void dumpRequestHeaders(ContainerRequestContext requestContext) 
	{
		MultivaluedMap<String, String> headers = ((ContainerRequest)requestContext.getRequest()).getRequestHeaders();
		for (Map.Entry<String, List<String>> entry : headers.entrySet())
		{
			String value = SuperString.valueOf(entry.getValue(), ',').toString();
			log.debug("RequestHeader: " + entry.getKey() + " = " + value);
		}
	}


	private String getHeader(Request request, String key)
	{
		String value = ((ContainerRequest)request).getHeaderString(key);
		return AuthHelper.normalizeHeader(value);
	}


	private Application validateApplication(AuthHelper.SecurityUser su, Request request) 
	{
		log.debug("validateApplication");
		if (su.role.equals(UserRole.ADMIN))
		{
			// application headers aren't expected when user is logged in as an administrator.
			log.debug("Cipres headers not required for cipres admin");
			return null;
		}
		return AuthHelper.validateApplication(su, getHeader(request, AuthHelper.H_APP));
	}


	// Basic auth
    private AuthHelper.SecurityUser authenticate(Request request) 
	{
        // Extract authentication credentials
        String authentication = getHeader(request, HttpHeaders.AUTHORIZATION);
		return AuthHelper.authenticate(authentication);
	}




	/*********************************************************************************************
		START: nested Authorizer class
	*********************************************************************************************/
	public class Authorizer implements SecurityContext 
	{
		private AuthHelper.SecurityUser su;
		private Principal principal;
		private UriInfo uriInfo;

		public Authorizer(final AuthHelper.SecurityUser su, UriInfo uriInfo) 
		{
			this.su = su;
			this.uriInfo = uriInfo;
			if (this.su != null)
			{
				// Principal.getName() will return the user ID as a string.
				this.principal = new Principal() 
				{
					public String getName() 
					{
						return String.valueOf(su.user.getUserId());
					}
				};
			}
		}

		public Principal getUserPrincipal() 
		{
			return this.principal;
		}


		/*
			It seems weird to throw an exception instead of returning false but if we
			return false we don't seem to have control over the response and some html
			formatted FORBIDDEN response is returned by default. 

			Parameter 'role' is the role specified with @RolesAllowed for the resource
			being referenced.  Our job here is to see whether the user who sent his 
			credentials in the request headers is in this role.  I think normally 
			you'd get the user's roles from the db and would have stored them in
			the SecurityUser object.   What we're doing instead is ignoring any
			"roles" the user may have been assigned in the db, and making sure
			the user matches the username in the url being requested.

			When this is called SU refers to the end user, not the umbrella app admin.
		*/
		public boolean isUserInRole(String requiredRole) 
		{
			//log.debug("checking isUserInRole");
			//String appnameInUrl= uriInfo.getPathParameters().getFirst("app");
			String usernameInUrl= uriInfo.getPathParameters().getFirst("user");

			if (su == null)
			{
				throw new AuthorizationException("Invalid username or password.\n", AuthHelper.REALM);
			}
			// aUserRole = role of the authenticated user.
			String aUserRole = this.su.role.toString();

			// admin role is required for this resource.  
			if (requiredRole.equals(UserRole.ADMIN.toString()))
			{
				if (aUserRole.equals(UserRole.ADMIN.toString()))
				{
					return true;
				}
				throw new AuthorizationException("Access forbidden for " + this.su.user.getUsername()  + 
					".\n", AuthHelper.REALM);
			}

			// Otherwise we're dealing with the STANDARD role.  
			return credentialsValidForUrl(usernameInUrl, requiredRole);
		}

		private boolean credentialsValidForUrl(String usernameInUrl, String requiredRole)
		{
			// the authenticated user.
			User aUser = this.su.user;
			String aUserRole = this.su.role.toString();

			// CIPRES ADMIN can access all urls, regardless of username in url. 
			if (aUserRole.equals(UserRole.ADMIN.toString()))
			{
				return true;
			}
			/*
				Otherwise, make sure username part of url matches our authenticated user.
				For UMBRELLA users, username in url must be appname.cipres-eu_header_value. 
			*/
			if (usernameInUrl != null && (aUser.getUsername().equals(usernameInUrl)) )
			{
				return true;
			}
			//log.debug("User not authorized, throwing exception.");
			throw new AuthorizationException("Access forbidden for " + aUser.getUsername() + ".\n", 
				AuthHelper.REALM);
		}

		public boolean isSecure() 
		{
			return "https".equals(uriInfo.getRequestUri().getScheme());
		}

		public String getAuthenticationScheme() 
		{
			return SecurityContext.BASIC_AUTH;
		}
	}	
	/*********************************************************************************************
		END: nested Authorizer class
	*********************************************************************************************/

}
