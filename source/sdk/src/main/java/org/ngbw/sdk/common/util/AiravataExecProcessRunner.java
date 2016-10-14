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
public class AiravataExecProcessRunner extends SSHExecProcessRunner 
{
	private static final Log log = LogFactory.getLog(AiravataExecProcessRunner.class.getName());

	// Derived class can override sshCommand and connectString.  See GsiSSHProcessRunner.
        //String airavataCommand = "/users/u4/nsgdevuser/kennethtest/scigap/PythonClients/samples/subwrap.py";
        String airavataCommand = "subwrap.py";

	// These are only used with regular ssh, not gsissh. 
        private String acommand;
        private String stdOut;
        private String stdErr;


	/**
		Constructors
	*/

	public AiravataExecProcessRunner(String sshCommand, String connectString, Map<String, String> vars)
	{
		this.sshCommand = sshCommand;
		this.connectString = connectString;
		setEnv(vars);
	}


	public AiravataExecProcessRunner()
	{
	}

	/**
		Public Methods
	*/

        public String  getStdOut()
        {
                String retval = "";
                //try
                //{
                 //       log.debug("stdOut:  " + stdOut);
                  //      retval = stdOut.get();
                //}
                //catch(Exception e)
                //{
                 //       log.error("", e);
                //}
                retval = this.stdOut;
                return retval == null ? "" : retval;
        }

        public String  getStdErr()
        {
                String retval = "";
                //try
                //{
                //        retval = stdErr.get();
                //}
                //catch(Exception e)
                //{
                //        log.error("", e);
                //}
                retval = this.stdErr;
                return retval == null ? "" : retval;
        }


	public int run(String command) throws IOException, InterruptedException, Exception
	{
                acommand = this.airavataCommand + " " + this.connectString + " " + command;
                log.debug("running acommand " + acommand);
                int retval=1;
                try
                {
                        //retval = super.run(acommand);
                        //retval = AsyncProcessRunner.run(acommand);
                        AsyncProcessRunner apr = new AsyncProcessRunner();
                        apr.run(acommand);
                        log.debug("apr.getStdOut(): " + apr.getStdOut());
                        log.debug("AiravataExecProcessRunner stdOut: " + stdOut);
                        //stdOut = apr.stdOut;
                        //apr.stdOut = stdOut;
                        this.stdOut = apr.getStdOut();
                        this.stdErr = apr.getStdErr();
                        retval = apr.getExitStatus();
                }
                catch(Exception e)
                {
                        retval = 1;
                }
                if (retval != 0) {
                        log.debug("bad retval " + retval + " retrying...");
                        command = this.sshCommand + " " + this.connectString + " " + command;
                	log.debug("running sshCommand " + command);
                        AsyncProcessRunner apr = new AsyncProcessRunner();
                        apr.run(command);
                        log.debug("apr.getStdOut(): " + apr.getStdOut());
                        //stdOut = apr.stdOut;
                        this.stdOut = apr.getStdOut();
                        this.stdErr = apr.getStdErr();
                        retval = apr.getExitStatus();
                } else {
                        log.debug("good retval " + retval);
                        return retval;
                }
                return retval;
	}

	/**
		Private Methods
	*/
}

