package org.ngbw.cipresrest.webresource;
import org.ngbw.restdatatypes.ErrorData;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class CipresException extends Exception
{
	public int httpstatus;
	public int errorcode;

	public  CipresException()
	{
		super("Internal Cipres Error");
		this.httpstatus = Status.INTERNAL_SERVER_ERROR.getStatusCode();
		this.errorcode = ErrorData.GENERIC_SERVICE_ERROR;
	}

	/*
		Message whould be something appropriate for an end user to see.
		It will be put in ErrorData.displayMessage.
	*/
	public  CipresException(String message, Status httpstatus, int errorcode)
	{
		super(message);
		this.httpstatus = httpstatus.getStatusCode();
		this.errorcode = errorcode;
	}

	public  CipresException(String message, int httpstatusCode, int errorcode)
	{
		super(message);
		this.httpstatus = httpstatusCode;
		this.errorcode = errorcode;
	}
}
