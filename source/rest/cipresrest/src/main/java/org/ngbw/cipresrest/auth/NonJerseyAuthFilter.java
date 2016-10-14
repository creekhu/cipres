/*
	CIPRES REST API security filter for the tus servlet.
	Problem: rest api uses jersey framework, tus servlet does not, so
	we can't directly use our jersey SecurityFilter class for the tus servlet.

	First step - just do basic auth and see if we can get it working with the
	tus servlet in the cipresrest web app.  

	TODO: create a class with code that can be shared between SecurityFilter
	and NonJerseyAuthFilter.  
*/
package  org.ngbw.cipresrest.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.sdk.database.Application;
import org.tus.filter.auth.UserRequestWrapper;
import org.tus.servlet.upload.Response;


public class NonJerseyAuthFilter implements Filter 
{
	private static final Log log = LogFactory.getLog(NonJerseyAuthFilter.class.getName());

    @Override
    public void destroy() {;}

	@Override
    public void init(FilterConfig cfg) throws ServletException {;}

	@Override
    public void  doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
	        throws ServletException, IOException
	{
		log.debug("In NonJerseyAuthFilter security filter");
		HttpServletRequest request = (HttpServletRequest)req;
		Response response = new Response((HttpServletResponse)res);
		boolean publicapi = false;

		// for debugging only
		AuthHelper.dumpHeaders(request);

		// Only handling tus with this filter now and with tus we're making GET public
		// so that it's easy for /files api to GET info from a TUS Url.
		if (request.getMethod().equals("GET"))
		{
			publicapi = true;
		}

		AuthHelper.SecurityUser su;
		Application application = null;
		try
		{
			if (!publicapi)
			{
				// Get the user from the basic auth headers.
				su = authenticate(request);

				// cipres-appkey header is always required.
				application = AuthHelper.validateApplication(su, AuthHelper.getHeaderString(request, AuthHelper.H_APP));

				// For umbrella apps, end user is different from basic auth user.
				su = AuthHelper.getEndUser(su, application, 
									AuthHelper.getHeaderString(request, AuthHelper.H_EU),
									AuthHelper.getHeaderString(request, AuthHelper.H_EMAIL),
									AuthHelper.getHeaderString(request, AuthHelper.H_INST),
									AuthHelper.getHeaderString(request, AuthHelper.H_COUNTRY));

				// Replace request with a wrapper that implements getUserPrincipal() to return end user's username. 
				request = new UserRequestWrapper(su.user.getUsername(), su.role.toString(), request);

				// Set USER record in request so that servlet doesn't need to retrieve it from db.
				request.setAttribute(AuthHelper.USER, su.user); 
				request.setAttribute(AuthHelper.APPLICATION, application); 
			}
			chain.doFilter(request, response.getServletResponse());
		}
		catch(AuthenticationException authenticationE)
		{
			log.info(authenticationE.getMessage());
			sendError(response, HttpServletResponse.SC_UNAUTHORIZED, authenticationE.getMessage().trim());
		}
		catch(AuthorizationException authorizationE)
		{
			log.info(authorizationE.getMessage());
			sendError(response, HttpServletResponse.SC_UNAUTHORIZED, authorizationE.getMessage().trim());
		}
		catch (Exception e)
		{
			log.error("", e);
			sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unable to authenticate user");
		}
    }


	/*
		Ensures that request uses basic auth and that supplied username and password
		represents a valid user in the cipres database.
	*/
    private AuthHelper.SecurityUser authenticate(HttpServletRequest request) 
	{
		return AuthHelper.authenticate(AuthHelper.getHeaderString(request, "AUTHORIZATION"));
    }



	/*
		ARGHH! I hate doing this: hardcoding xml to match what the jersey
		parts of the rest api return for auth errors (via automatic marshaling of 
		restdatatypes.Error object). TODO: create an ErrorData object and marshal
		it so that we stay in synch with any changes to ErrorData class.
	*/
	private void sendError(Response response, int status, String message) throws IOException
	{
		String text = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<error>\n" +
    		"    <displayMessage>%s</displayMessage>\n" +
    		"    <message>%s</message>\n" +
    		"    <code>%d</code>\n" +
			"</error>\n",

			message,
			message,
			ErrorData.AUTHENTICATION );

		response.setStatus(status);
		response.setText(text);
		response.write();
	}
}
