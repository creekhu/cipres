package  org.ngbw.cipresrest.webresource;

import  org.ngbw.sdk.tool.DisabledResourceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.ErrorData;

@Provider
public class DisabledResourceExceptionMapper implements ExceptionMapper<DisabledResourceException> 
{
	private static final Log log = LogFactory.getLog(DisabledResourceExceptionMapper.class.getName());
	public Response toResponse(DisabledResourceException e)
	{
		// http status is 503 - temporarily unavailable 
		return Response.
				status(503).
				type("application/xml").
				entity(new ErrorData("" + e.toString(), e.getMessage(), ErrorData.DISABLED_RESOURCE)).build();
	}
}
