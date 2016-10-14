package org.ngbw.utils;
import java.util.concurrent.*;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.tool.BaseProcessWorker;
import org.ngbw.sdk.tool.TaskMonitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.ConnectionSource;
import org.ngbw.sdk.database.DriverConnectionSource;

/**
	Run periodically to poll remote resources and find out which jobs have
	finished.  We normally expect notification of job completion from a curl
	command in the remote job scripts but if the job timesout or something else
	goes wrong we may not be notified and need to poll.

	We check on all jobs that have a running_task table entry with status = SUBMITTED
	and our submitter id.  If the jobs is completed we set it's status to DONE.
	The whole implementation is a static method in ProcessManager.
*/
public class CheckJobs
{
	private static final Log log = LogFactory.getLog(CheckJobs.class.getName());
	private static String m_submitter;

	private static String m_default_submitter;
	private static long m_poll_interval;

	public static void main(String[] args)
	{
		try
		{
			Workbench wb = Workbench.getInstance();
			log.debug("CHECK JOBS: workbench initialized.");

			/*
				TODO: If we don't use a connection pool checkJobs will crash every time it's 
				been sitting idle and the db connection goes stale - but we don't really need a pool
				so find a better way to handle this.   Currently we have a cron job that runs every
				hour and starts it if it isn't running.

				The error is: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure

				For the time being I think I will use a connection pool.  Assuming we'll configure the pool
				to start out very small (1 or 2 connections), then checkjobs will get one extra connection 
				at most.

			*/
			/*
			//These 2 lines close the connection pool created in Workbench.getInstance() and
			//open an unpooled connection source. 
			ConnectionManager.getConnectionSource().close();
			ConnectionManager.setConnectionSource(new DriverConnectionSource());
			*/

			m_default_submitter = wb.getProperties().getProperty("application.instance");
			String p = wb.getProperties().getProperty("checkJobs.poll.seconds");
			if (p == null)
			{
				throw new Exception("Missing workbench property: checkJobs.poll.seconds");
			}
			m_poll_interval = new Long(p);


			m_submitter = System.getProperty("submitter");
			if (m_submitter == null)
			{
				throw new Exception("Missing system property submitter");
			}
			log.debug("Submitter=" + m_submitter + ", poll interval in seconds=" + m_poll_interval);

			while (true)
			{
				TaskMonitor.checkJobs(m_submitter);
				Thread.sleep(m_poll_interval * 1000);
			}
		}
		catch (Exception e)
		{
			log.error("", e);
			return;
		}
		finally
		{
			ConnectionSource source = ConnectionManager.getConnectionSource();
			if (source != null)
			{
				try
				{
					source.close();
				}
				catch(Exception e)
				{
					log.error("", e);
				}
			}
		}
	}

}
