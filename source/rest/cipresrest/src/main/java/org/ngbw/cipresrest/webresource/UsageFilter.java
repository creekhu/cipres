package org.ngbw.cipresrest.webresource;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.cipresrest.auth.AuthHelper;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.jobs.UsageLimit;


@Priority(Priorities.USER)
@Provider
public class UsageFilter implements ContainerRequestFilter, ContainerResponseFilter
{
	private static final Log log = LogFactory.getLog(UsageFilter.class.getName());
	private static HashMap<Long, Long> activeRequests = new HashMap<Long, Long>();
	int maximum = 10;

	@Override
    public void filter(ContainerRequestContext requestContext) 
	{
		User user = (User)requestContext.getProperty(AuthHelper.USER);
		if (user != null)
		{
			UsageLimit.getInstance().testAndIncrementUserRequestCount(user);
		}
	}
	
 
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException 
	{
		User user = (User)requestContext.getProperty(AuthHelper.USER);
		if (user != null)
		{
			UsageLimit.getInstance().decrementUserRequestCount(user);
		}
    }


}	

