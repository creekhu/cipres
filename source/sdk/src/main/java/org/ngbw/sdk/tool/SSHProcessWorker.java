/*
 * SSHProcessWorker.java
 */
package org.ngbw.sdk.tool;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.common.util.InputStreamCollector;
import org.ngbw.sdk.core.io.SSLConnectionManager;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.database.RunningTask;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;


public class SSHProcessWorker extends BaseProcessWorker 
{
	private static final Log m_log = LogFactory.getLog(SSHProcessWorker.class.getName());

	private final String m_hostName;
	private Connection m_sshConn;

	public SSHProcessWorker(RunningTask rt) throws PropertyException, Exception
	{
		super(rt);

		m_hostName = m_tool.getToolResource().getParameters().get("host");
		if (m_hostName == null) 
		{
			throw new PropertyException("Required property 'host' is null");
		}
	}

	public SSHProcessWorker(String jobhandle) 
		throws PropertyException, IOException, InstantiationException, IllegalAccessException, 
		ClassNotFoundException, Exception
	{
		super(jobhandle);
		m_hostName = m_tool.getToolResource().getParameters().get("host");
		if (m_hostName == null) 
		{
			throw new PropertyException("Required property 'host' is null");
		}
	}

	/* retries are not implemented */
	protected String submitImpl()  throws Exception
	{
		Session sshSession = openSession();

		try 
		{
			String finalCommand  = (m_rc == null) ? "" : ("source " + m_rc + "; ");
			finalCommand += 	"export WB_JOBID=" + m_jobHandle + "; "  +
									"setenv WB_JOBID "  + m_jobHandle + "; " +
									"cd " + m_workingDir + "; " +
									m_commandLine;

			m_log.debug("Calling ssh Session.execCommand() on " + finalCommand);
			sshSession.execCommand(finalCommand);

			Future<String> stdOut = InputStreamCollector.readInputStream(sshSession.getStdout());
			Future<String> stdErr = InputStreamCollector.readInputStream(sshSession.getStderr());

			// trilead Session.waitForCondition specifies timeout in milliseconds, but tool registry
			// specifies timeout in minutes.  (1 sec = 1000 millisecs).
			long timeout = m_jobTimeout > 0 ? ((m_jobTimeout * 60) * 1000) : 0;

			// The problem is that setting a timeout on the Session doesn't kill the remote job!
			int retval = sshSession.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);

			WorkQueue.markDone(m_rt);

			if ((retval & ChannelCondition.TIMEOUT) != 0)
			{
				m_log.debug("TL Got a timeout.");
				m_log.debug("TL timeout, Calling storeTaskResults.");
				storeTaskResults("", "", -1, "Timeout of " + m_jobTimeout + " minutes exceeded.", fh);
			} else
			{
				int exitCode = sshSession.getExitStatus();
				storeTaskResults(stdOut.get(), stdErr.get(), exitCode, fh );
			}
			m_log.debug("Output files successfully retrieved.  Task completed.");

			WorkQueue.finish(m_rt);
			archiveTaskDirectory();

			return "";
		}
		finally 
		{
			// neither of these throws exceptions.
			closeSession(sshSession);
			TaskMonitor.notifyJobComplete(m_rt.getTask(), "");
		}
	}

	private Session openSession() throws Exception
	{
		assert(m_sshConn == null);

		m_sshConn = SSLConnectionManager.getInstance().getConnection(m_hostName);

		if (m_sshConn == null)
			throw new Exception("No connection could be acquired from host: " + m_hostName);

		return m_sshConn.openSession();
	}

	private void closeSession(Session sshSession)
	{
		assert(m_sshConn != null);

		sshSession.close();
		m_sshConn.close();

		m_sshConn = null;
	}

	// Todo: change this to be like SSHExecProcessWorker's?
	protected FileHandler getFileHandler() throws Exception
	{
		//return new SFTPFileHandler(m_hostName);
		return toolRegistry.getToolResource(m_rt.getResource()).getFileHandler();
	}

	// Override BaseProcessWorker version of this.  We don't charge for this type of job.
	protected void storeJobCharge()
	{
		return;
	}

	/* 
		TODO: should handle getPredictedSus in the processworker as well but it's a little
		tricky since we can't currently instantiate a processworker without a running task
		record, but we get predicted sus before creating the running task.
	*/
}
