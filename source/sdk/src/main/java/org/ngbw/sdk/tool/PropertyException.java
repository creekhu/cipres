package org.ngbw.sdk.tool;

public class PropertyException extends Exception 
{
	public PropertyException(String message)
	{
		super("invalid property: " + message);
	}
}

