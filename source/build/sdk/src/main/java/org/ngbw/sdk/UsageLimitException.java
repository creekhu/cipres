package org.ngbw.sdk;
import org.ngbw.sdk.jobs.UsageLimit;


public class UsageLimitException extends RuntimeException 
{
	public UsageLimit.LimitStatus status;

    public UsageLimitException() 
	{
        super();
    }


    public UsageLimitException(String message) 
	{
        super(message);
    }

    public UsageLimitException(String message, UsageLimit.LimitStatus status) 
	{
		super(message);
		this.status = status;
	}


}
