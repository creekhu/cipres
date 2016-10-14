package org.ngbw.cipresrest.webresource;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/* 
	I believe ContainerRequestFilters are instantiated as singletons 

	I'd like to put a unique request id in the NDC so that I can easily grep the log
	for all messages pertaining to a single request but I'm not sure I should add the 
	overhead of having to syncrhonize to increment a request_id variable.
*/
@PreMatching
@Provider
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter
{
	private static final Log log = LogFactory.getLog(LogFilter.class.getName());
	private long request_id = 0;

	@Override
    public void filter(ContainerRequestContext requestContext) 
	{
		String uri = requestContext.getUriInfo().getRequestUri().toString();
		String method = requestContext.getMethod(); 	
		NDC.push("[" + getRequestId() + ":" + method + ":" + uri +  "]");
		log.info("START");
		//log.info("START " + method + ":" + uri); 
	}
	
 
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException 
	{
		//String uri = requestContext.getUriInfo().getRequestUri().toString();
		//String method = requestContext.getMethod(); 
		log.info("END"); 
		NDC.pop();
		NDC.remove();
    }

	private synchronized long getRequestId()
	{
		request_id += 1;
		return request_id;
	}
}	

