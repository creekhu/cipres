package org.ngbw.utils;

import java.util.concurrent.*;
import java.util.List;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.database.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;
import org.ngbw.sdk.core.shared.TaskRunStage;

/**
	-Dtask=<taskId>
	-Dnumber=<number>

	taskId is the task to clone and run
	number is the number of clones to make and submit.
*/
public class RunTasks
{
	private static final Log log = LogFactory.getLog(RunTasks.class.getName());
	long taskId;
	int number;
	Workbench wb;

	public RunTasks() throws Exception
	{
		wb = Workbench.getInstance();

		String tmp;
		tmp = System.getProperty("taskId");
		if (tmp == null)
		{
			throw new Exception("Missing system property taskId");
		}
		taskId = new Long(tmp);

		tmp = System.getProperty("number");
		if (tmp == null)
		{
			throw new Exception("Missing system property number");
		}
		number = new Integer(tmp);
	}

	public void go() throws Exception
	{
		log.info("RunTasks with taskId=" + taskId + ", and number=" + number + ".");
		Task originalTask = new Task(taskId);
		Task clone;
		try
		{
			for (int i = 0; i < number; i++)
			{
				clone = new Task(originalTask);
				clone.save();
				clone.setStage(TaskRunStage.READY);
				wb.submitTask(clone, false);
				System.out.printf("taskId=%d\n", clone.getTaskId());
			}
		} catch (Exception e)
		{
			System.out.printf("Caught exception, printing stack trace.\n");
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		try
		{
			RunTasks rt = new RunTasks();
			rt.go();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
