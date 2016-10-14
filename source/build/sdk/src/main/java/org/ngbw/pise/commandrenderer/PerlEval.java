package org.ngbw.pise.commandrenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.InputStreamCollector;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.ngbw.sdk.common.util.ProcessRunner;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;

public class PerlEval
{
	private static final Log log = LogFactory.getLog(PerlEval.class.getName());
	Process worker = null;
	BufferedReader stdout = null;
	BufferedReader stderr = null;
	PrintStream stdin = null;

	public static void main(String[] args) throws Exception
	{
		String[] perlStatements = {
			"print \"hi there\"\n",
			"print \"two\\nlines\\n\"\n",
			"(! 1 )? print \" -endgaps\": print \"\"\n",	
			"((\"-align\" =~ /align/ ))? print \"true\" : print \"false\";\n",
			"print  <<\"EOT\"\n<commands>set Increase = no;</commands>\\n\nEOT\n",
			"print  <<\"EOT\"\n<commands>set Increase = no;</commands>\\nsecond line\n\n\nEOT\n",
			"print"
		};

		PerlEval perlEval = null;
		try
		{
			String result;
			perlEval = new PerlEval();
			perlEval.initialize();
			for (int i = 0; i < perlStatements.length; i++)
			{
				log.debug("Statement: " + perlStatements[i]);
				result = perlEval.evaluateStatement(perlStatements[i]);
				log.debug("Evaluates to: " + result);
			}
			perlEval.terminate();
		}
		catch (Exception e)
		{
			log.error("", e);
			perlEval.cleanup();
		}
	}

	public PerlEval() {;}

	public void initialize() throws Exception
	{
		String[] command = new String[1];

		try
		{
			command[0] = "piseEval";
			worker = Runtime.getRuntime().exec(command);
			stdout = new BufferedReader(new InputStreamReader(worker.getInputStream()));
			stderr = new BufferedReader(new InputStreamReader(worker.getErrorStream()));
			stdin = new PrintStream(new BufferedOutputStream(worker.getOutputStream(), 8192));
		} 
		catch (Exception e)
		{
			log.error("Runtime exec of piseEval failed.  Make sure piseEval script is in the path and executable.", e);
			throw e;
		}
	}

	public void terminate() throws Exception
	{
		log.debug("In terminate");
		try
		{
			stdin.close();
			stdin = null;
			// todo: need to read stdout and stderr?

			final int exitCode = worker.waitFor();
			if (exitCode != 0)
			{
				throw new RuntimeException("Exit value was not 0 but " + exitCode);
			}
		}
		finally
		{
			cleanup();
		}
	}


	public void cleanup() 
	{
		log.debug("In cleanup");
		try
		{
			if (stdin != null)
			{
				stdin.close();
				stdin = null;
			}
			if (stdout != null)
			{
				stdout.close();
				stdout = null;
			}
			if (stderr != null)
			{
				stderr.close();
				stderr = null;
			}
		}
		catch(Exception e)
		{
			log.error("Caught exception:"+  e.getMessage());
		}
	}

	public String evaluateStatement(String statement) throws Exception
	{
		String response;
		String error = null;
		String result = "";

		//log.debug("Sending:" + statement);
		if (!statement.endsWith("\n"))
		{
			statement += "\n";
		}
		stdin.print(statement);
		stdin.print("piseEnd\n");
		stdin.flush();

		while ((response = stdout.readLine()) != null)
		{
			//log.debug("Read:" + response);
			if (response.equals("PiseEval:"))
			{
				break;
			} else if (response.startsWith("PiseEvalError="))
			{
				log.error("Starting error message: " + error);
				error = response + "\n";
			} else if (error != null)
			{
				log.error("Adding to error message: " + error);
				error += (response + "\n"); 
			} else
			{
				//log.debug("Adding to result: " + result);
				result += (response + "\n");
			}
		}
		if (response == null)
		{
			throw new Exception("Unexepected EOF from piseEval");
		}
		if (error != null)
		{
			log.error(error);
			throw new Exception("piseEval error");
		}
		return result.trim();
	}

}
