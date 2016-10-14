/*
	This is just a class to modify as needed and use to try
	out different things in the sdk.
*/
package org.ngbw.examples;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.database.User;

public class TestSdk 
{

	//Workbench object instances
	private static Workbench workbench = Workbench.getInstance();

	
	public static void main(String[] args) throws Exception
	{
		User user = new User();
		long id = user.getUserId();
		System.out.println("userid before save is " + id);
	}
}


