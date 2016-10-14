package  org.ngbw.sdk;

import org.ngbw.sdk.api.tool.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException 
{
	public List<FieldError> fieldError = new ArrayList<FieldError>();

	public ValidationException() 
	{
		super();
	}

	public ValidationException(String message) 
	{
		super(message);
	}

	public ValidationException(String field, String message) 
	{
		super();
		fieldError = new ArrayList<FieldError>(1);
		fieldError.add(new FieldError(field, message));
	}

	public ValidationException(List<FieldError> error) 
	{
		super("");
		fieldError = error;
	}

	public String toString()
	{
		String retval = super.toString();
		for (FieldError fe : fieldError)
		{
			retval += (fe.getField() + ":" + fe.getError() + "\n");
		}
		return retval;
	}
}
