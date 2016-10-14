/*
*/
package  org.ngbw.cipresrest.cors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import java.security.Principal;
import java.io.IOException;
import java.util.List;

import org.ngbw.cipresrest.webresource.CipresException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.ConstraintException;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.common.util.CountryCode;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.clients.UmbrellaUser;


/*
*/
@Provider
public class CorsFilter implements ContainerResponseFilter 
{
	private static final Log log = LogFactory.getLog(CorsFilter.class.getName());

	@Override
	public void filter(final ContainerRequestContext requestContext,
		final ContainerResponseContext responseCtx) throws IOException 
	{
		String origin = ((ContainerRequest)requestContext.getRequest()).getHeaderString("origin");
		if (origin != null)
		{
			if (isValid(origin))
			{
				log.debug("Received CORS origin header: '" + origin + "'" + ", returning CORS response headers.");
				//responseCtx.getHeaders().add("Access-Control-Allow-Origin", "*");
				responseCtx.getHeaders().add("Access-Control-Allow-Origin", origin);
				responseCtx.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, cipres-appkey");
				responseCtx.getHeaders().add("Access-Control-Allow-Credentials", "true");
				responseCtx.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

				// Number of seconds that browser will cache a preflight response: 300 sec = 5 min.
				responseCtx.getHeaders().add("Access-Control-Max-Age", "300");
			} else
			{
				log.debug("Received invalid CORS origin header: " + origin + ", not returning CORS response headers.");
			}
		}
		return;
	}

	private boolean isValid(String origin)
	{
		return origin.startsWith("http") || origin.startsWith("https");
	}
}


