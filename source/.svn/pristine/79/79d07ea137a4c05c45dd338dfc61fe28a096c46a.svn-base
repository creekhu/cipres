package org.ngbw.cipresrest.webresource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.cipresrest.auth.AuthHelper;
import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;


/*
	GET /v1/job
		Query parameter jh=jobhandle may be used multiple times.  Returns status of requested jobs.
	Purpose of this class is to get status of multiple jobs in a single call.  

	As part of the job api, SecurityFilter insures that cipres-appkey header is present or that
	the user is a cipresadmin.  Unlike the rest of the methods in the job's api, SecurityFilter
	doesn't require end user headers for this method when application.authType = UMBRELLA.

	If the application is an UMBRELLA app, then jh of any job owned by the app is allowed.  This lets
	an umbrella app get status of all it's users jobs in a single call.   If the app is DIRECT, jh must
	correspond to a job submitted by this user, using this app.

	Cipresadmin can call these methods w/o an cipres-appkey to get status of any job: 
	see SecurityFilter.validateApplication.  In that case, we get to this method with 
	application = null.  

*/
@Path("/v1/")
public class CipresRestAdmin extends BaseResource
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresRestAdmin.class.getName());
	//@Context UriInfo uriInfo;

	// The logged in user, or end user specified by request headers 
	@Context SecurityContext securityContext;
	@Context ContainerRequestContext requestContext;
	User user;
	Application application;

	void initialize() throws Exception
	{
		user = (User)requestContext.getProperty(AuthHelper.USER);

		// note that application will be null if user logged in as cipres admin.
		application = (Application)requestContext.getProperty(AuthHelper.APPLICATION);
	}


	@Path("job")
	@GET
	@Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
	public JobList getSubmittedJobs(@QueryParam("jh") List<String> jobhandle) throws Exception
	{
		initialize();
		List<StatusData> list = new ArrayList<StatusData>();

		for (String jh : jobhandle)
		{
			StatusData sd;
			JStatus js;

			if (application == null || application.getAuthType().equals(Application.UMBRELLA))
			{
				// will throw UserMismatchException if job isn't owned by this application, unless app = null. 
				js = new JStatus(jh, this, this.application);
			} else
			{
				// will throw UserMismatchException if job isn't owned by this application and user.
				js = new JStatus(jh, this, this.user, this.application);
			}
			sd = js.toStatusData();
			list.add(sd);
		}

		JobList jl = new JobList();
		jl.title = "Status";
		jl.jobstatus = list;
		return jl;
	}
}
