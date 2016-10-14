package org.ngbw.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.tool.TaskInitiate;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.tool.RenderedCommand;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.tool.BaseProcessWorker;
import org.ngbw.sdk.tool.TaskInitiate;

/**
	Stand alone program to stage and submit a single task using
	information in the running task table.
*/
public class StageAndSubmitATask
{
	private static final Log log = LogFactory.getLog(StageAndSubmitATask.class.getName());

	public static void stageAndSubmit(String jobhandle) throws Exception
	{
		ServiceFactory factory = Workbench.getInstance().getServiceFactory();
		RunningTask rt = RunningTask.find(jobhandle);

		TaskInitiate runner= new TaskInitiate(factory);

		/* 
			TaskInitiate.runTask() will log any exception that occurs before rethrowing it.
			I don't want to throw it from here because that would cause it to be logged
			again in SubmitJob.java.  There's no further error handling to be done.
		*/
		try
		{
			runner.runTask(rt);
		}
		catch(Exception e)
		{
			;
		}
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			throw new Exception("usage: StageAndSubmitATask running_task_id");
		}
		stageAndSubmit(args[0]);
	}
}
