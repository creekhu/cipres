/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */

/**
	SSHExecProcessRunner provides a simple way to run a remote command and wait for
	it to complete.  Or you can start it, send it data on it's stdin, and wait for it
	yourself.  You can retrieve the output and error text as strings and
	get the exit code.  For example:

		SSHExecProcessRunner pr = new SSHExecProcessRunner(true);
		exitCode = pr.run("echo 'fiddlefaddle\nbiddle' );
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

	Fancy command line with i/o redirection is not supported.  For that use AsynchProcessRunner
	directly. 

	Uses either ssh or gsissh depending on the ctor you use.

	With the ssh style ctor you specify only the id of the remote host, the full hostname, 
	username, password/keyfile, are looked up with the id in ssl.properties.  Or for gsissh 
	just specify the connect string, e.g.  terri@tghost.foo.edu.  See GsisshProcessRunner for 
	more info about gsissh settings.

	Interface is Very similar to SSHProcessRunner, except that SSHProcessRunner uses the trilead
	java ssh library.  

*/

package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.core.io.SSLConnectionManager;

/**
*/
public class SSHExecProcessRunner extends AsyncProcessRunner 
{
	private static final Log log = LogFactory.getLog(SSHExecProcessRunner.class.getName());
	static final String LOGIN = "login";

	// Derived class can override sshCommand and connectString.  See GsiSSHProcessRunner.
	String sshCommand = "ssh";
	String connectString; // e.g. user@hostname  

	// These are only used with regular ssh, not gsissh. 
	private String hostAlias;
	private String host;
	private String username;
	private String password;
	private File keyfile;

	int exitStatus;
	Future<String> stdOut;
	Future<String> stdErr;
	OutputStream stdin;


	/**
		Constructors
	*/

	/*
		You must use a keyfile, not a password in ssl.properties.
	*/
	public boolean configure(Map<String, String> cfg) 
	{
		if (cfg != null && cfg.containsKey(LOGIN))
		{
			try
			{
				this. hostAlias = cfg.get(LOGIN);
				this.host = SSLConnectionManager.getInstance().getHost(this.hostAlias);
				this.username = SSLConnectionManager.getInstance().getUsername(this.hostAlias);
				this.keyfile = SSLConnectionManager.getInstance().getKeyfile(this.hostAlias);
				this.connectString = "-i " + this.keyfile.getAbsolutePath()  + " " + 
					this.username + "@" + this.host;
				log.debug("Configured SSHExecProcessRunner from ssl.properties for " + this.hostAlias +
					", connectString=" + this.connectString);
					
			}
			catch(Exception e)
			{
				log.error("", e);
			}
		} else
		{
			log.debug("Missing configuration parameter: " + LOGIN);
		}
		return isConfigured();
	}

	public boolean isConfigured()
	{
		return this.host != null && this.username != null && this.keyfile != null;
	}

	public SSHExecProcessRunner(String sshCommand, String connectString, Map<String, String> vars)
	{
		this.sshCommand = sshCommand;
		this.connectString = connectString;
		setEnv(vars);
	}


	public SSHExecProcessRunner()
	{
	}

	/**
		Public Methods
	*/

	public void setSSHCommand(String sshCommand) { this.sshCommand = sshCommand; }
	public void setConnectString(String connectString) { this.connectString = connectString; }

	public void start(String command) throws IOException, InterruptedException, Exception
	{
		command = this.sshCommand + " " + this.connectString + " " + command;
		super.start(command);
	}

	public void close()
	{
	}

	public int run(String command) throws IOException, InterruptedException, Exception
	{
		command = this.sshCommand + " " + this.connectString + " " + command;
		return super.run(command);
	}

	/**
		Private Methods
	*/
}

