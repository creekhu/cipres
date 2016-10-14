package  org.ngbw.cipresrest.auth;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.ErrorData;

@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> 
{
	private static final Log log = LogFactory.getLog(AuthorizationExceptionMapper.class.getName());
    public Response toResponse(AuthorizationException e) 
	{
		//log.error(e.toString(), e);
		log.info(e.toString());
		return Response.
				status(Status.FORBIDDEN).
				type("application/xml").
				entity(new ErrorData("" + e.toString(), "Authorization Error", ErrorData.AUTHORIZATION)).
				build();
	}
}
