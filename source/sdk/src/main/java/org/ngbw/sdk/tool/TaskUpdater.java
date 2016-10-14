/*
 * TaskUpdater.java
 */
package org.ngbw.sdk.tool;


import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskLogMessage;


/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */
public class TaskUpdater 
{
	private static final Log log = LogFactory.getLog(TaskUpdater.class);


	public static void logProgress(RunningTask rt, TaskRunStage stage, String message) 
			throws IOException, SQLException, Exception	
	{
		logProgress(rt, stage, message, true);
	}

	public static void logProgress(RunningTask rt, TaskRunStage stage, String message, boolean save) 
			throws IOException, SQLException, Exception	
	{
		Task task = rt.getTask();
		if (task == null)
		{
			log.debug("Task not found for " + rt.getJobhandle() + ". wanted to store task message: " + message);
			return;
		} 
		logProgress(task, stage, message, save);

	}

	public static void logProgress(Task task, TaskRunStage stage, String message) 
			throws IOException, SQLException, Exception
	{
		logProgress(task, stage, message, true); 
	}

	public static void logProgress(Task task, TaskRunStage stage, String message, boolean save) 
			throws IOException, SQLException, Exception
	{
		try
		{
			//log.debug(message);
            if (stage == null)
            {
                stage = task.getStage();
            }
			TaskLogMessage newMessage = new TaskLogMessage(task);
			newMessage.setStage(stage);
			newMessage.setMessage(message);
			task.logMessages().add(newMessage);
			task.setStage(stage);
			if (save == true)
			{
				task.save();
			}
		}
		catch(NotExistException nee)
		{
			log.debug(nee.toString());
		}
		catch (Exception e)
		{
			log.error("", e);
			throw e;
		}
	}

	public static void logError(RunningTask rt, TaskRunStage stage, String msg) 
		throws Exception
	{
		Task task = rt.getTask();
		if (task == null)
		{
			//log.debug("Task not found for " + rt.getJobhandle() + ". Wanted to store task error message:" + msg);
			return;
		} 
		logError(task, stage, msg);
	}

	public static void logError(Task task, TaskRunStage stage, String msg) 
		throws Exception
	{
		logErrorInternal(task, stage, msg, false);
	}

	public static void logError(RunningTask rt, TaskRunStage stage, Throwable thrown) 
		throws Exception
	{
		Task task = rt.getTask();
		if (task == null)
		{
			//log.debug("Task not found for " + rt.getJobhandle() + ". Wanted to store task error message:", thrown);
			return;
		} 
		logError(task, stage, thrown);
	}

	public static void logError(Task task, TaskRunStage stage, Throwable thrown) 
		throws Exception
	{
		logErrorInternal(task, stage, thrown, false);
	}


	public static void logFatal(RunningTask rt, TaskRunStage stage, String msg) 
		throws Exception
	{
		logFatal(rt, stage, msg, true);

	}
	public static void logFatal(RunningTask rt, TaskRunStage stage, String msg, boolean save) 
		throws Exception
	{
		Task task = rt.getTask();
		if (task == null)
		{
			//log.debug("Task not found for " + rt.getJobhandle() + ". Wanted to store fatal task error message:" + msg);
			return;
		} 
		logFatal(task, stage, msg, save);
	}

	public static void logFatal(Task task, TaskRunStage stage, String msg) 
		throws Exception
	{
		logFatal(task, stage, msg, true);
	}

	public static void logFatal(Task task, TaskRunStage stage, String msg, boolean save) 
		throws Exception
	{
		logErrorInternal(task, stage, msg, true, save);
	}

	public static void logFatal(RunningTask rt, TaskRunStage stage, Throwable thrown) 
		throws Exception
	{
		Task task = rt.getTask();
		if (task == null)
		{
			//log.debug("Task not found for " + rt.getJobhandle() + ". Wanted to store fatal task error message:", thrown);
			return;
		} 
		logFatal(task, stage, thrown);
	}

	public static void logFatal(Task task, TaskRunStage stage, Throwable thrown) 
		throws Exception
	{
		logErrorInternal(task, stage, thrown, true);
	}



	public static void logErrorInternal(Task task, TaskRunStage stage, Throwable thrown, boolean isFatal) 
		throws Exception
	{
		logErrorInternal(task, stage, thrown, isFatal, true);
	}

	public static void logErrorInternal(Task task, TaskRunStage stage, Throwable thrown, boolean isFatal, boolean save) 
		throws Exception
	{
		try
		{
			//log.error("", thrown);
            if (stage == null)
            {
                stage = task.getStage();
            }

			TaskLogMessage newMessage = new TaskLogMessage(task);
			newMessage.setError(true);
			newMessage.setStage(stage);
			String msg = thrown.getMessage();
			if (msg == null)
			{
				msg = thrown.toString();
			}
			newMessage.setMessage(msg);
			task.logMessages().add(newMessage);
			task.setOk(!isFatal);
			task.setStage(stage);
			if (save == true)
			{
				task.save();
			}
		}
		catch(NotExistException nee)
		{
			log.debug(nee.toString());
		}
		catch (Exception e)
		{
			log.error("", e);
			throw e;
		}
	}

	public static void logErrorInternal(Task task, TaskRunStage stage, String msg, boolean isFatal) 
		throws Exception
	{
		logErrorInternal(task, stage, msg, isFatal, true);
	}

	public static void logErrorInternal(Task task, TaskRunStage stage, String msg, boolean isFatal, boolean save) 
		throws Exception
	{
		try
		{
			//log.error(msg);
            if (stage == null)
            {
                stage = task.getStage();
            }

			TaskLogMessage newMessage = new TaskLogMessage(task);
			newMessage.setError(true);
			newMessage.setStage(stage);
			newMessage.setMessage(msg);
			task.logMessages().add(newMessage);
			task.setOk(!isFatal);
			task.setStage(stage);
			if (save == true)
			{
				task.save();
			}
		}
		catch(NotExistException nee)
		{
			log.debug(nee.toString());
		}
		catch (Exception e)
		{
			log.error("", e);
			throw e;
		}
	}
}	



