package org.ngbw.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.TaskLogMessage;
import org.ngbw.sdk.database.TaskOutputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.tool.BaseProcessWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.SSHProcessRunner;
import java.io.PrintStream;

/**
	To run:

	Log into 8ball as user specified by eightball.username in settings.xml.
	Remove ~/terri.txt if it exists.

	Back over on test machine, run test using the script testSSHProcessRunner.
	Should see exit status of 0.

	Back on 8ball, make sure terri.txt was created with 3 lines of text.

*/
public class TestSSHProcessRunner
{
	private static final Log log = LogFactory.getLog(TestSSHProcessRunner.class.getName());
	public static void main(String[] args)
	{
		Workbench wb = Workbench.getInstance();

		try
		{
			log.debug("STARTING");
			// initialize with the name of a host from ssl.properties
			SSHProcessRunner sr = new SSHProcessRunner("8ball");
			sr.start("cat > terri.txt");

			// will get a null pointer exception here if process has been killed, or ...
			PrintStream stdin = new PrintStream(sr.getStdin());
			stdin.printf("Here's some text I'd like to see\n");
			stdin.printf("and here's the second line.\n");
			stdin.printf("and the third and final.\n");
			stdin.flush();
			stdin.close();
			int exitStatus = sr.waitForExit();
			log.debug("Exit status is " + exitStatus);
		}
		catch (Exception e)
		{
			log.error("", e);
		}

	}
}
