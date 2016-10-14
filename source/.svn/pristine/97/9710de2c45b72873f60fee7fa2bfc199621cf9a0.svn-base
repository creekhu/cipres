package  org.ngbw.cipresrest.webresource;

import  org.ngbw.sdk.jobs.JobNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.ErrorData;

@Provider
public class JobNotFoundExceptionMapper implements ExceptionMapper<JobNotFoundException> 
{
	private static final Log log = LogFactory.getLog(JobNotFoundExceptionMapper.class.getName());
	public Response toResponse(JobNotFoundException e)
	{
		//log.error(e.toString(), e);
		return Response.
				status(Status.NOT_FOUND).
				type("application/xml").
				entity(new ErrorData("Job Not Found Error: " + e.toString(), "Job not found.", ErrorData.NOT_FOUND)).
				build();

	}
}
