/*
 * TaskInspector.java
 */
package org.ngbw.utils;


import java.io.IOException;
import java.sql.SQLException;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.RunningTask;


/**
 * 
 * @author 
 *
 */
public class RunningTaskInspector 
{

	public static void inspect(String jobhandle) throws IOException, SQLException, Exception
	{
		RunningTask inspected = RunningTask.find(jobhandle);

		if (inspected == null) {
			System.err.println("Couldn't find a running task with that id");

			return;
		}

		inspect(inspected);
	}

	public static void inspect(RunningTask inspected) throws IOException, SQLException
	{
		System.out.println("JobHandle: " + inspected.getJobhandle());
		//System.out.println("WorkingDir: " + inspected.getWorkspace());
		System.out.println("OutputDesc: " + inspected.getOutputDesc());
	}

	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
				throw new Exception("usage: RunningTask jobhandle ");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			String jobhandle;

			
				jobhandle = args[0];
				inspect(jobhandle);
			

		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}

}
