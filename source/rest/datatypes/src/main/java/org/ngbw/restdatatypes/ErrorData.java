package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import org.ngbw.restdatatypes.ParamError;
import org.ngbw.restdatatypes.LimitStatus;


@XmlRootElement(name="error")
public class ErrorData 
{
	public static final String name = "error";

	/******************************************************************************************************/
	/* START Codes that can be used for programmatic handling of errors */ 
	/******************************************************************************************************/
	// User doesn't have permission (not in correct role) 
	public static final int AUTHORIZATION = 1;

	// Bad or missing basic auth credentials or cipres custom headers
	public static final int AUTHENTICATION = 2;

	// job, user, document, app or tool not found.  
	public static final int NOT_FOUND = 4;

	// Bad form parameter.
	public static final int FORM_VALIDATION = 5;

	// Username in url doesn't own the job or document specified in url.
	public static final int USER_MISMATCH = 6;

	// Something wrong with request, other than bad form param.  Maybe bad or missing query param.
	public static final int BAD_REQUEST = 7;

	//This means jersey didn't like the request.  Didn't even make it to cipres code.
	// Client may not care whether it's BAD_REQUEST or BAD_INVOCATION but cipres admin may want to know.
	public static final int BAD_INVOCATION = 102;

	// displayMessage is "Internal Cipres Error"
	public static final int GENERIC_SERVICE_ERROR = 100;

	// If client gets an exception while making a request, or if status
	// returned is other than 200, but ErrorData can't be parsed from the response,
	// low level client code may construct an ErrorData with this code, in order
	// to provide a consistent interface to higher level code.
	public static final int GENERIC_COMM_ERROR = 101;

	// User error, detected by client application, not thrown by service.
	public static final int USER_ERROR = 102;

	// Too many active requests, too many SUs consumed, etc. See sdk/Jobs/UsageLimit.java.  
	// Http status code will be 429: see cipresrest/webresource/UsageLimitExceptionMapper.java
	// When this is set, ErrorData will include a LimitStatus element.
	public static final int USAGE_LIMIT = 103;

	public static final int DISABLED_RESOURCE = 104;


	/******************************************************************************************************/
	/* END Codes that can be used for programmatic handling of errors */ 
	/******************************************************************************************************/

	public String displayMessage;
	public String message;
	public int code;
	public List<ParamError> paramError;
	public LimitStatus limitStatus;

	public ErrorData() {;} 
	public ErrorData(String message, String displayMessage, int code)
	{
		this.message = message;
		if (displayMessage == null)
		{
			this.displayMessage = message;
		} else
		{
			this.displayMessage = displayMessage;
		}
		this.code = code;
	}


	public ErrorData(String message, String displayMessage, int code, List<ParamError> paramError)
	{
		this.message = message;
		this.displayMessage = displayMessage;
		this.code = code;
		this.paramError = paramError;
	}

	public ErrorData(String message, String displayMessage, int code, LimitStatus  limitStatus)
	{
		this.message = message;
		this.displayMessage = displayMessage;
		this.code = code;
		this.limitStatus = limitStatus;
	}
}
