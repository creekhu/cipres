package  org.ngbw.cipresrest.webresource;

import  org.ngbw.sdk.jobs.UsageLimit;
import  org.ngbw.sdk.UsageLimitException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LimitStatus;

@Provider
public class UsageLimitExceptionMapper implements ExceptionMapper<UsageLimitException> 
{
	private static final Log log = LogFactory.getLog(UsageLimitExceptionMapper.class.getName());
	public Response toResponse(UsageLimitException e)
	{
		//log.error(e.toString(), e);
		// http status is 429 - too many requests

		UsageLimit.LimitStatus limitStatus = e.status;
		return Response.
				status(429).
				type("application/xml").
				entity(new ErrorData("" + e.toString(), e.getMessage(), ErrorData.USAGE_LIMIT, 
					convert(limitStatus))).build();

	}

	LimitStatus convert(UsageLimit.LimitStatus ls)
	{
		log.debug("limitstatus type is " + ls.type);
		log.debug("limitstatus ceiling is " + ls.ceiling);

		LimitStatus limitStatus = new LimitStatus();
		limitStatus.type = ls.type;
		limitStatus.ceiling = ls.ceiling;
		limitStatus.remaining = ls.remaining;
		limitStatus.resetMinutes = ls.resetMinutes;
		limitStatus.resetDate = ls.resetDate;
		return limitStatus;
	}

}


