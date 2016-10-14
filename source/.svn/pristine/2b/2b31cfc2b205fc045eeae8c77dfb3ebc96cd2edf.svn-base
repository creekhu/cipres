package  org.ngbw.cipresrest.webresource;

import java.util.ArrayList; 
import java.util.List; 
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.ParamError;

/*
	Thrown on job submission if required params are missing or data types are wrong, or min/max
	constraints violated.

	Also thrown from other parts of the api like "user" and "applications" that allow post.  These
	forms quietly ignore extra parameters but throw an exception when known parameters have invalid
	values or are missing.

	ValidationException may or may not have a message and may or may not have a list of field errors.
	We try to have field names match field names in the api.  When an error can't be associated with
	a specific field or we don't know the field name at the point the error is detected (for example field
	length errors, detected in Row.save) jve.fieldError is empty but jve.message is populated. 
	
	TODO: validate field length early so that we catch errors before Row.save.

	See jobs.Job.submitJob, tool.TaskValidator, generated code in tool.validation and
	code generator: sdk/src/main/codeGeneration/FreeMarker/pise2validationJava.ftl.
*/
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> 
{
	private static final Log log = LogFactory.getLog(ValidationExceptionMapper.class.getName());

	public Response toResponse(ValidationException jve)
	{
		//log.debug(t.toString(), t);
		String msg = "Validation Error: " + (jve.getMessage() == null ? "" : jve.getMessage());
		//log.debug(msg);
		//log.error(msg, jve);

		return Response.
				status(Status.BAD_REQUEST).
				type("application/xml").
				entity(new ErrorData(msg, "Form validation error.", ErrorData.FORM_VALIDATION, convert(jve.fieldError))).
				build();
	}

	private List<ParamError> convert(List<FieldError> fielderrors)
	{
		ArrayList<ParamError> al = new ArrayList<ParamError>(fielderrors.size());
		for (FieldError fe : fielderrors)
		{
			al.add(new ParamError(fe.getField(), fe.getError()));
		}
		return al;
	}
}
