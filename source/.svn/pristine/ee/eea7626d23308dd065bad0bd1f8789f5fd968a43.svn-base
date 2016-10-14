package org.ngbw.cipresrest.webresource;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.cipresrest.auth.AuthHelper;
import org.ngbw.restdatatypes.FileData;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;

public class DataInfoRetriever
{
	private static final Log log = LogFactory.getLog(DataInfoRetriever.class.getName());

	@Context SecurityContext securityContext;
	@Context ContainerRequestContext requestContext;
	private User user;
	private Application application;
	private String uuid;

	void initialize() throws Exception
	{
		if (requestContext == null)
		{
			throw new Exception("requestContext is null");
		}
		user = (User)requestContext.getProperty(AuthHelper.USER);
		application = (Application)requestContext.getProperty(AuthHelper.APPLICATION);
		if (user == null)
		{
			throw new Exception("No user in security context.");
		}
		log.debug("User is " + user.getUsername());
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public FileData retrieve(@PathParam("uuid") String uuid) throws Exception
	{
		initialize();
		this.uuid = uuid;
		log.debug("uuid is " + uuid);

		FileData fd = new FileData();
		fd.filename = "foo";
		return fd;
	}
}
