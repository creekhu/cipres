package org.ngbw.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.tool.BaseProcessWorker;
import org.ngbw.sdk.tool.TaskMonitor;

/**
	Stand alone program to retrieve the results for a single task using
	information in the running task table.
*/
public class StoreRTaskOutput
{
	private static final Log log = LogFactory.getLog(StoreRTaskOutput.class.getName());

	public static void main(String[] args)
	{
		try
		{
			Workbench.getInstance();
			if (args.length != 1)
				throw new Exception("usage: StoreRTaskOutput jobhandle");
			String jobhandle = args[0];
			// TODO: not sure this code works correctly unless RunningTask status
			// is STATUS_TERMINATED.  Don't think it works if status is already
			// STATUS_FAILED.  Last argument to getResults is the max number of
			// retries.  We always attempt to load Results and only inc retries
			// and check it's value oafter failing.  When we detect that we've
			// exceeded the specified number of retries we set status = STATUS_FAILED.
			TaskMonitor.getResults(RunningTask.find(jobhandle),  null);
		}
		catch(Exception e)
		{
		}
	}
}
