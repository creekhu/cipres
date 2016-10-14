/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */

/**
	SSHProcessRunner provides a simple way to run a remote command and wait for
	it to complete.  You can retrieve the output and error text as strings and
	get the exit code.  For example:

		SSHProcessRunner pr = new SSHProcessRunner(true);
		exitCode = pr.run("echo 'fiddlefaddle\nbiddle' > xxx 2>&1; wc -l xxx; cat xxx; test -f xxx");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

	If you do a fancy command line with i/o redirection like in the example above make
	sure you use the right shell syntax for the login shell of the remote_user@remote_host.

	You specify only the id of the remote host here, the full hostname, username, password/keyfile,
	are looked up with the id in ssl.properties.
*/

package org.ngbw.sdk.common.util;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.core.io.SSLConnectionManager;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;



/**
*/
public class SSHProcessRunner
{
	private static final Log log = LogFactory.getLog(SSHProcessRunner.class.getName());
	int exitStatus;
	Future<String> stdOut;
	Future<String> stdErr;
	OutputStream stdin;
	private Connection m_sshConn;
	private String m_hostName;
	private static final int TIMEOUT = 30; // wait no more than minutes.
	private Session m_session; // only used with start(), wait() methods, not with run().

	public int getExitStatus() { return exitStatus;}

	public String  getStdOut() 
	{ 
		String retval = "";
		try
		{
			retval = stdOut.get(); 
		} 
		catch(Exception e)
		{
			log.error("", e);
		}
		return retval == null ? "" : retval;
	}

	public String  getStdErr() 
	{ 
		String retval = "";
		try
		{
			retval = stdErr.get(); 
		} 
		catch(Exception e)
		{
			log.error("", e);
		}
		return retval == null ? "" : retval;
	}

	public SSHProcessRunner(String hostName)
	{
		m_hostName = hostName;
	}

	private Session openSession() throws Exception
	{
		assert(m_sshConn == null);

		m_sshConn = SSLConnectionManager.getInstance().getConnection(m_hostName);

		if (m_sshConn == null)
			throw new Exception("No connection could be acquired from host: " + m_hostName);

		return m_sshConn.openSession();
	}

	private void closeSession(Session session)
	{
		assert(m_sshConn != null);

		session.close();
		m_sshConn.close();

		m_sshConn = null;
	}

	/**
		If you need to send stdin to the process do this:
			SSHProcessRunner runner = new SSHProcessRunner(host);
			runner.start(command);
			OutputStream stdin = runner.getStdin();
			stdin.write(...); ... stdin.flush(); stdin.close();
			int exitstatus = runner.waitForExit();
	*/

	public void start(String command) throws Exception
	{
		m_session  = null;
		try
		{
			m_session = openSession();
			m_session.execCommand(command);
			stdOut = InputStreamCollector.readInputStream(m_session.getStdout());
			stdErr = InputStreamCollector.readInputStream(m_session.getStderr());
		}
		catch (Exception e)
		{
			if (m_session != null)
			{
				closeSession(m_session);
				m_session = null;
				throw e;
			}
		}
	}

	public OutputStream getStdin()
	{
		return new BufferedOutputStream(m_session.getStdin(), 8192);
	}

	public int waitForExit() throws Exception
	{
		try
		{
			// The problem is that setting a timeout on the Session doesn't kill the remote job!
			long timeout = TIMEOUT > 0 ? ((TIMEOUT * 60) * 1000) : 0;
			int retval = m_session.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);
			if ((retval & ChannelCondition.TIMEOUT) != 0)
			{
				log.debug("TL Got a timeout.");
				throw new java.util.concurrent.TimeoutException();
			} else
			{
				return exitStatus = m_session.getExitStatus();
			}
		}
		finally
		{
			close();
		}
	}

	public void close()
	{
		if (m_session != null)
		{
			closeSession(m_session);
			m_session = null;
		}
	}


		

	/**
		Opens an ssh session, runs the command on the remote host.
		Possible outcomes:
			Can throw an exception (if can't open ssh session for example)
			Timeout waiting for remote command to complete - throws TimeoutException
			Remote command completed, exitStatus is valid.
	*/
	public int run(String command) throws Exception 
	{
		Session session  = null;
		try
		{
			session = openSession();
			session.execCommand(command);
			stdOut = InputStreamCollector.readInputStream(session.getStdout());
			stdErr = InputStreamCollector.readInputStream(session.getStderr());

			// trilead Session.waitForCondition specifies timeout in milliseconds. convert from
			// minutes.
			long timeout = TIMEOUT > 0 ? ((TIMEOUT * 60) * 1000) : 0;

			// The problem is that setting a timeout on the Session doesn't kill the remote job!
			int retval = session.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);

			if ((retval & ChannelCondition.TIMEOUT) != 0)
			{
				log.debug("TL Got a timeout.");
				throw new java.util.concurrent.TimeoutException();
			} else
			{
				return exitStatus = session.getExitStatus();
			}
		}
		finally
		{
			if (session != null)
			{
				closeSession(session);
			}
		}
	}
}

