/*
	Tests out the interaraction between a FixedThreadPool and a shutdown hook.
	If you hit ^C jobs that are already running will finish, queued jobs will 
	then be started and immediately finish, and no new jobs will be added to the queue.

	Program exits immediately when shutdown hook returns, so hook must call threadPool.awaitTermination.
	After shutdown hook finishes, control is NOT returned to the main thread.

	If you don't set awaitTermination timeout high enough program will exit before the running
	jobs finish.  Theoretically running jobs could check isShutdown() periodically to
	know whether they can continue or should stop and cleanup.

	If program has criteria for exitting besides waiting for ^C (like a limit on the 
	number of jobs it queues up) then the main thread will also need to call threadPool.shutdown(), 
	as implemented below.

	Run with 'sdkrun org.ngbw.utils.ThreadPoolTest', watch stderr, issue ^C after several jobs
	have been queued.

*/
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
import org.ngbw.sdk.tool.TaskMonitor;


public class ThreadPoolTest
{
	private static final Log log = LogFactory.getLog(ThreadPoolTest.class.getName());
	private static ThreadPoolExecutor threadPool = null;
	Vector<String> inProgressList = new Vector<String>();
	private static String m_submitter;
	private static String m_default_submitter;
	private static long m_poll_interval;
	private static int m_pool_size;
	private static int m_recover = 0;
	private static String  m_local;

	// this number is kind of arbitrary, see use of threshold in the code below.
	private static int threshold;

	public ThreadPoolTest() throws Exception
	{
		threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(3);
	}

	public static void main(String[] args)
	{
		try
		{
			Workbench wb = Workbench.getInstance();
			m_pool_size = 10;
			ThreadPoolTest lr = new ThreadPoolTest();

			log.debug("LOAD RESULTS: for submitter=" + m_submitter + ", poll_interval in seconds=" 
				+ m_poll_interval + ", thread pool size=" + m_pool_size + ", max jobs queued = " + threshold);

			lr.keepWorking();
			if (!threadPool.isShutdown())
			{
				threadPool.shutdown();
			}
			log.debug("Main thread Calling awaitTermination");
			threadPool.awaitTermination(120, java.util.concurrent.TimeUnit.SECONDS);
			log.debug("Main thread back from awaitTermination");

			log.debug("LOAD RESULTS: exitting."); 
		}
		catch (Exception e)
		{
			log.error("Caught Exception", e);
			return;
		}
	}
	
	private void keepWorking() throws Exception
	{
		/*
			Add a shutdown hook which will be run in it's own thread when the program gets a signal (like ^C).
			The program will exit as soon as the shutdown hook returns, so the hook must call
			threadPool.awaitTermination to let the worker threads finish first.
		*/
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				log.debug("In shutdown hook, calling threadPool.shutdown()");
				threadPool.shutdown();
				try
				{
					log.debug("Hook Calling awaitTermination");
					threadPool.awaitTermination(120, java.util.concurrent.TimeUnit.SECONDS);
					log.debug("Hook back from awaitTermination");
				}
				catch (InterruptedException ie)
				{
					log.debug("Caught " + ie.toString());
					Thread.currentThread().interrupt();
				}
				catch(Exception e)
				{
					log.debug("Caught " + e.toString());
				}
			}
		});


		// Queue up the jobs until we're signaled to stop.
		int i = 0;
		while(threadPool.isShutdown() == false )
		{
			i++;
			int jobsQueued;
			threadPool.execute(this.new ProcessRunningTask(i));
			log.debug("Queued another job.  Threads busy=" + threadPool.getActiveCount() + ", jobs in Q=" +
				threadPool.getQueue().size() + ", taskCount=" + threadPool.getTaskCount());
			Thread.sleep(1000 * 2);
		}
	}


	private class ProcessRunningTask implements Runnable 
	{
		int job;

		ProcessRunningTask(int i) throws Exception
		{
			job = i;
		}

		public void run()
		{
			try
			{
				NDC.push("[job =" + job + "]");
				if (threadPool.isShutdown())
				{
					log.debug("ThreadPool is shutdown.");
					return;
				}

				log.debug("starting");

				Thread.sleep(1000 * 10);
				log.debug("step 1");

				Thread.sleep(1000 * 10);
				log.debug("step 2");

				Thread.sleep(1000 * 10);
				log.debug("step 3");

				Thread.sleep(1000 * 10);
				log.debug("step 4");

				Thread.sleep(1000 * 10);
				log.debug("DONE");
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
				NDC.pop();
				NDC.remove();
			}
		}
	}

}
