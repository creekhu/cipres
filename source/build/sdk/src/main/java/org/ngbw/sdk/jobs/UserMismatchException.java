package  org.ngbw.sdk.jobs;

@SuppressWarnings("serial")
public class UserMismatchException extends RuntimeException 
{
	public UserMismatchException() 
	{
		super("Wrong user.");
	}

	public UserMismatchException(String message) 
	{
		super(message);
	}
}
