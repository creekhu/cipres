package org.ngbw.utils;

import java.util.concurrent.*;
import java.util.Date;
import java.util.List;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.database.RunningTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;
import java.util.Vector;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;


/**
	Standalone program to submit jobs for running_tasks have status = RunningTask.STATUS_NEW. 

	System Properties:
		submitter = submit for this submitter id only (see submitter column in db, normally
		the url of the web site that created the job)

		recover = if "true" handle entries that have already been tried once. 
		I want to use a separate process for 1st tries so that they get priority over jobs we're we've already
		failed, in case there's something that bogs them down or means they'll never succeed.
		NOT USED RIGHT NOW. HANDLES ALL JOBS with status NEW, regardless of ATTEMPTS FIELD.

*/
public class SubmitJob
{
	private static final Log log = LogFactory.getLog(SubmitJob.class.getName());
	private static ThreadPoolExecutor threadPool = null;
	Vector<String> inProgressList = new Vector<String>();
	private static String m_submitter;
	private static String m_default_submitter;
	private static long m_poll_interval;
	private static int m_pool_size;
	private static boolean m_recover = false;
	private static String  m_local;
	private static String m_status;

	// this number is kind of arbitrary, see use of threshold in the code below.
	private static int threshold;

	public SubmitJob() throws Exception
	{
		threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(m_pool_size);
	}

	public static void main(String[] args)
	{
		try
		{
			Workbench wb = Workbench.getInstance();

			// This overrides db connection pool establised by Workbench.  
			//ConnectionManager.getConnectionSource().close();
			//ConnectionManager.setConnectionSource(new DriverConnectionSource());

			m_default_submitter = wb.getProperties().getProperty("application.instance");

			String p;
			p = wb.getProperties().getProperty("submitJob.poll.seconds");
			if (p == null)
			{
				throw new Exception("Missing workbench property: submitJob.poll.seconds");
			}
			m_poll_interval = new Long(p);

			p = wb.getProperties().getProperty("submitJob.pool.size");
			if (p == null)
			{
				throw new Exception("Missing workbench property: submitJob.pool.size");
			}
			m_pool_size = new Integer(p);
			//threshold = thread_pool_size * 4;
			threshold = 0;

			m_submitter = System.getProperty("submitter");
			if (m_submitter == null)
			{
				throw new Exception("Missing system property submitter");
			}

			// Properties that control behavior when used in RecoverResults mode.
			// Todo: not using m_local yet.  Also need to add ability to specify just a single task or resource
			// to process.  
			m_recover = Boolean.getBoolean("recover");
			m_local = System.getProperty("local");
			
			m_status =  RunningTask.STATUS_NEW;


			SubmitJob lr = new SubmitJob();
			log.debug("SUBMIT JOB: for submitter=" + m_submitter + ", poll_interval in seconds=" 
				+ m_poll_interval + ", thread pool size=" + m_pool_size + ", max jobs queued = " + threshold
				+ (m_recover ? ", Recovery Mode" : ", Normal Mode"));

			/* If previous process died, unlock it's records.  Pass m_recover instead of null if we care about initial versus retry*/
			RunningTask.unlockReadyToSubmit(m_submitter, null);

			/* 
				In recover mode, theoretically, keepWorking returns after one pass.  We want to give the 
				threads plenty of time to complete their work after keepWorking has queued up the jobs.
			*/
			lr.keepWorking();
			shutdownAndAwaitTermination(24, TimeUnit.HOURS);
			log.debug("SUBMIT JOB: exitting normally."); 
		}
		/*
			Main thread can catch an exception if, for example it isn't able to connect
			to the db.  If main thread exits but leaves the threadpool threads alive but
			idle the process sticks around and doesn't do anything.
		*/
		catch (Exception e)
		{
			log.error("Caught Exception.  Calling shutdownAndAwaitTermination().", e);
			shutdownAndAwaitTermination(1, TimeUnit.MINUTES);
			log.debug("SUBMIT JOB: exitting due to exception in main."); 
			return;
		}
	}

	static void shutdownAndAwaitTermination(int count, TimeUnit units) 
	{
		threadPool.shutdown(); // Disable new tasks from being submitted
		try 
		{
			// Wait a while for existing tasks to terminate
			if (!threadPool.awaitTermination(count, units)) 
			{
				threadPool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!threadPool.awaitTermination(20 , TimeUnit.SECONDS))
				{
					log.error("ThreadPool did not terminate");
				}
			}
		} 
		catch (InterruptedException ie) 
		{
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
 	
	
	private void keepWorking() throws Exception
	{
		while(true)
		{
			/*
			log.debug("Threads busy=" + threadPool.getActiveCount() + ", jobs in Q=" +
				threadPool.getQueue().size() + ", taskCount=" + threadPool.getTaskCount());
			*/

			int jobsQueued;
			if ((threshold == 0) || (jobsQueued = threadPool.getQueue().size()) < threshold)
			{
				int jobCount = scanAndProcess();
			} else
			{
				log.warn("Thread pool has " + jobsQueued + " jobs queued, not queuing more until queue drains.");
			}
			// In recovery mode we only scan the database once.  Otherwise if a resource is down we would keep
			// processing the same records over and over.
			if (m_recover || (m_local != null))
			{
				return;
			}
			Thread.sleep(1000 * m_poll_interval);
		}
	}

	private int scanAndProcess() throws Exception
	{
		// select tasks with specified submitter and status that aren't locked.  Pass in m_recover instead
		// of null if we care about recover versus inital attempt.
		List<RunningTask> list = 
			RunningTask.findReadyToSubmit(m_submitter, null, false);
		if (list.size() > 0)
		{
			String tmp = "";
			for (RunningTask rt : list)
			{
				tmp += rt.getJobhandle() + "-" + rt.getStatus() + "-" + (rt.getLocked() == null ? "" :  rt.getLocked().toString()) + ", ";
			}
			log.debug("Found " + list.size() + " tasks to process: " + tmp);
		}

		for (RunningTask rt : list)
		{
			if (!inProgressList.contains(rt.getJobhandle()))
			{
				threadPool.execute(this.new ProcessRunningTask(rt.getJobhandle()));
				inProgressList.add(rt.getJobhandle());
			}
		}
		return list.size();
	}


	private class ProcessRunningTask implements Runnable 
	{
		long m_taskId;
		String m_jobhandle;

		ProcessRunningTask(String jobhandle) throws Exception
		{
			m_jobhandle = jobhandle;
		}

		public void run()
		{
			long startTime = System.currentTimeMillis();
			NDC.push("[jh =" + m_jobhandle + "]");
			boolean gotLock = false;
			try
			{
				gotLock = RunningTask.lock(m_jobhandle);
				if (gotLock)
				{
					RunningTask rt = RunningTask.find(m_jobhandle); 
					m_taskId = rt.getTaskId();
					m_jobhandle = rt.getJobhandle();
					NDC.pop();
					NDC.push("[task=" + m_taskId +", job=" + m_jobhandle + "]");

					// It's possible someone else changed the status before we managed to lock the record.
					if (!rt.getStatus().equals(m_status))
					{
						log.debug("Skipping " + m_jobhandle + ". Status isn't " + m_status + ", it's " +
							rt.getStatus());
					} else
					{
						log.debug("Submitting job " + m_jobhandle);
						StageAndSubmitATask.stageAndSubmit(m_jobhandle);
					}
				} else
				{
					log.debug("Skipping " + m_jobhandle + ". Already locked.");
				}
			}
			catch(Exception e)
			{
				log.error("", e);
			}
			catch(Error t)
			{
				log.error("THREAD IS DYING.", t);
				throw t;
			}
			finally
			{
				try
				{
					if (gotLock)
					{
						RunningTask.unlock(m_jobhandle);
					}
				}
				catch(Exception e)
				{
					log.debug("", e);	
				}
				catch(Error t)
				{
					log.error("THREAD IS DYING.", t);
					throw t;
				}
				long elapsedTime = System.currentTimeMillis() - startTime;
				log.debug("SubmitJob took " + elapsedTime + " ms, or " + elapsedTime/1000 + " seconds.");
				inProgressList.remove(m_jobhandle);
				NDC.pop();
				NDC.remove();
			}
		}
	}

}
