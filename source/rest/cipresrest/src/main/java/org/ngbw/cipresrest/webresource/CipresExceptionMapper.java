package  org.ngbw.cipresrest.webresource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.ErrorData;

/*
*/
@Provider
public class CipresExceptionMapper implements ExceptionMapper<CipresException> 
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresExceptionMapper.class.getName());

	public Response toResponse(CipresException e)
	{
		//log.error("", e);
		return Response.
				status(e.httpstatus).
				type("application/xml").
				entity(new ErrorData(e.toString(), e.getMessage(), e.errorcode)).
				build();
	}
}
