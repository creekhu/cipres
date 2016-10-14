package  org.ngbw.cipresrest.webresource;

import  org.ngbw.sdk.jobs.UserMismatchException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.ErrorData;

@Provider
public class UserMismatchExceptionMapper implements ExceptionMapper<UserMismatchException> 
{
	private static final Log log = LogFactory.getLog(UserMismatchExceptionMapper.class.getName());
	/*
		This exception is thrown when the username in the url and the jobhandle 
		or document id in the url don't match.  In other words, it's not this
		user's job.  Returning NOT_FOUND because the specified job or document
		belonging to the specified user isn't found.
	*/
	public Response toResponse(UserMismatchException e)
	{
		log.error(e.toString(), e);
		return Response.
				status(Status.NOT_FOUND).
				type("application/xml").
				entity(new ErrorData("" + e.toString(), "The authenticated user, or the application, does not own the specified job or file.", ErrorData.USER_MISMATCH)).
				build();

	}
}
