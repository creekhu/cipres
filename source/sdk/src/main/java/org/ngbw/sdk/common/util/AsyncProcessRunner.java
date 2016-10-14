/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */

/**
	AsyncProcessRunner provides a simple way to run an external command 
	Similar to ProcessRunner except that with AsyncProcessRunner you can
	launch the command, write to it's stdin, then wait for it to complete.

	run* methods wait for completion
	start* methods are identical to run methods but you wait for completion with waitForExit() method.
*/

package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.InputStreamCollector;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;

public class AsyncProcessRunner
{
	private static final Log log = LogFactory.getLog(AsyncProcessRunner.class.getName());
	int exitStatus;
	Future<String> stdOut;
	Future<String> stdErr;
	boolean mergeStreams;
	Map<String,String> vars;
	File directory;
	Process process;

	/**
		Constructors
	*/
	public AsyncProcessRunner()
	{
	}

	public AsyncProcessRunner(boolean mergeStreams)
	{
		this.mergeStreams = mergeStreams;
	}

	/**
		Public Methods
	*/
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

	public OutputStream getStdin()
	{
		return new BufferedOutputStream(process.getOutputStream(), 8192);
	}


	public void setMergeStreams(boolean mergeStreams) { this.mergeStreams = mergeStreams; }
	public void setDirectory(File directory) { this.directory = directory; }
	public void setEnv(Map<String,String> vars) { this.vars = vars; }




	/**
		Can invoke this as either multiple strings as the argument or with an
		array of strings as the argument.
	*/
	public int run(String... command) throws IOException, InterruptedException
	{
		ProcessBuilder pb = runInit();
		pb.command(command);
		return runInternal(pb);
	}

	public void start(String... command) throws IOException, InterruptedException, Exception
	{
		ProcessBuilder pb = runInit();
		pb.command(command);
		startInternal(pb);
	}

	public int run(List<String> command) throws IOException, InterruptedException, Exception
	{
		ProcessBuilder pb = runInit();
		pb.command(command);
		return runInternal(pb);
	}

	public void start(List<String> command) throws IOException, InterruptedException, Exception
	{
		ProcessBuilder pb = runInit();
		pb.command(command);
		startInternal(pb);
	}


	/**
		Specify command as a single string containing the arguments (but no shell
		wildcards, redirection, etc.).  Command will be split on whitespace.
	*/
	public int run(String command) throws IOException, InterruptedException, Exception
	{
		String[] commandArray = command.split("\\s+");
		ProcessBuilder pb = runInit();
		pb.command(commandArray);
		return runInternal(pb);
	}

	public void start(String command) throws IOException, InterruptedException, Exception
	{
		String[] commandArray = command.split("\\s+");
		ProcessBuilder pb = runInit();
		pb.command(commandArray);
		startInternal(pb);
	}

	public int runSh(String command) throws IOException, InterruptedException, Exception
	{
		ProcessBuilder pb = runInit();
		pb.command("/bin/sh", "-c", command);
		return runInternal(pb);
	}

	public void startSh(String command) throws IOException, InterruptedException, Exception
	{
		ProcessBuilder pb = runInit();
		pb.command("/bin/sh", "-c", command);
		startInternal(pb);
	}


	/**
		Todo: add a timeout.
	*/
	public int waitForExit() throws Exception
	{
		return exitStatus = process.waitFor();
	}

	public void close()
	{
	}


	/**
		Private Methods
	*/
	private ProcessBuilder runInit()
	{
		ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(mergeStreams);
		if (directory != null)
		{
			pb.directory(directory);
		}
		if (vars != null)
		{
			pb.environment().putAll(vars);
		}
		return pb;
	}

	private int runInternal(ProcessBuilder pb) throws IOException, InterruptedException
	{
		String command = SuperString.valueOf(pb.command(), ' ').toString();
		log.debug("Waiting for:  " + command);
		Process p = pb.start();
			// start throws io exception if the command can't be found/run.

		stdOut = InputStreamCollector.readInputStream(p.getInputStream());
		stdErr = InputStreamCollector.readInputStream(p.getErrorStream());
		exitStatus = p.waitFor();
		log.debug("Completed :  " + command);
		return exitStatus;
	}

	private void startInternal(ProcessBuilder pb) throws IOException, InterruptedException
	{
		String command = SuperString.valueOf(pb.command(), ' ').toString();
		log.debug("Running:  " + command);
		process  = pb.start();
			// throws io exception if the command can't be found/run.

		stdOut = InputStreamCollector.readInputStream(process.getInputStream());
		stdErr = InputStreamCollector.readInputStream(process.getErrorStream());
	}

	/**
		Main method is just for testing the class.
	*/
	public static void main(String args[]) throws Exception
	{
		// Initialize using varargs with stdout and stderr merged
		AsyncProcessRunner pr = new AsyncProcessRunner(true);
		int exitCode;

		/*
		exitCode = pr.run("cat",  "-x");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		// Initialize using varargs with stdout and stderr separate
		pr.setMergeStreams(false);
		exitCode = pr.run("cat",  "-x");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		// Initialize using array of strings 
		String[] commandArray = {"ls", "-l"};
		pr.setMergeStreams(true);
		exitCode = pr.run(commandArray);
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		exitCode = pr.run("ls   -l  ");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		exitCode = pr.run("ls   -l  *");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		exitCode = pr.runSh("ls   -l  *");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());
		*/

		exitCode = pr.runSh("echo 'fiddlefaddle\nbiddle' > xxx 2>&1; wc -l xxx; cat xxx; test -f xxx");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		exitCode = pr.runSh("echo testing again &&  test -b xxx");
		System.out.println("exitCode is " + exitCode + ". Output is " + pr.getStdOut());

		AsyncProcessRunner myRunner;
		PrintStream stdin;
		myRunner = new AsyncProcessRunner();
		myRunner.start("ls -l");
		exitCode = myRunner.waitForExit();
		if (exitCode == 0)
		{
			System.out.printf("stdout=%s\n", myRunner.getStdOut());
		} else
		{
			System.out.printf("exitCode=%d\n", exitCode);
		}

		myRunner = new AsyncProcessRunner();
		myRunner.start("cat");
		stdin = new PrintStream(myRunner.getStdin());
		stdin.printf("hi there\n");
		stdin.printf("bye now\n");
		stdin.flush();
		stdin.close();

		exitCode = myRunner.waitForExit();
		if (exitCode == 0)
		{
			System.out.printf("stdout=%s\n", myRunner.getStdOut());
		} else
		{
			System.out.printf("exitCode=%d\n", exitCode);
		}

		myRunner = new AsyncProcessRunner();
		Map<String, String> p = new HashMap();
		p.put("X509_USER_PROXY", "/tmp/terri");
		myRunner.setEnv(p);
		System.out.printf("Running a cat command via gsissh on cipres@lonestar");
		myRunner.start("gsissh -o BatchMode=yes tg804218@lonestar.tacc.utexas.edu cat");
		stdin = new PrintStream(myRunner.getStdin());
		stdin.printf("hi there\n");
		stdin.printf("bye now\n");
		stdin.flush();
		stdin.close();

		exitCode = myRunner.waitForExit();
		if (exitCode == 0)
		{
			System.out.printf("stdout=%s\n", myRunner.getStdOut());
		} else
		{
			System.out.printf("exitCode=%d\n", exitCode);
		}
	}
}
