package org.ngbw.cipresrest.webresource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.jobs.JobNotFoundException;
import org.ngbw.sdk.tool.WorkQueue;


@Path("/v1/admin")
public class CipresAdmin 
{
	private static final Log log = LogFactory.getLog(CipresAdmin.class.getName());
	@Context SecurityContext securityContext;
	@Context UriInfo uriInfo;

	void initialize() throws Exception
	{
		/*
			Throws Authorization exception if logged in user isn't an admin.
		*/
		securityContext.isUserInRole(UserRole.ADMIN.toString());
	}


	@GET
	@Path("updateJob")
	@Produces( MediaType.TEXT_PLAIN )
	public String updateJob( 
		//@DefaultValue("-1") @QueryParam("taskId") long taskId,
		@DefaultValue("") @QueryParam("jh") String jobhandle,
		@DefaultValue("DONE") @QueryParam("status") String status)
		throws Exception
	{
		initialize();
		if (jobhandle.trim().length() == 0)
		{
			throw new CipresException("Missing jh query parameter.", Status.NOT_FOUND, ErrorData.BAD_REQUEST);
		}
		try
		{
            RunningTask rt = RunningTask.find(jobhandle);
			if (rt == null)
			{
				throw new JobNotFoundException();
			}
            if (status.equals("START"))
			{
				;
			} else if (status.equals("DONE"))
            {
				log.debug("CURL marking " + jobhandle + " DONE.");
                WorkQueue.markDone(rt);
            } else
            {
                throw new Exception("Invalid job status.  Job=" + jobhandle + ". Status=" + status);
            }
            return "\njob status updated.\n";
        }
		catch(JobNotFoundException jfe)
		{
			throw jfe;
		}
        catch(Exception e)
        {
            log.error(e);
			throw new Exception("Error trying to change job " + jobhandle + " status to DONE.\n" + e.getMessage());
        }
	}
}
