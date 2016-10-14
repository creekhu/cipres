/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */
package org.ngbw.sdk.common.util;
import java.lang.reflect.Field;
import java.lang.Process;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*
	Main class at end can be used to test.  Build and deploy the sdk as usual:
		- from cipres-portal directory run "tl.sh sdk"
		- run "sdkrun org.ngbw.sdk.common.util.ProcessUtils 2>log.txt"

		Main prints to stdout.  Logging is controlled by sdk/src/main/resources/log4j.properties
		and goes to stderr.
*/
public class ProcessUtils
{
	private static final Log log = LogFactory.getLog(ProcessUtils.class.getName());

	/**
		Quite a hack to get the pid, but we only use the pid 
		so that we can log it for debugging purposes.
	*/
	static public int getPid(Process p)
	{
		try
		{
			Field field = p.getClass().getDeclaredField("pid");
			field.setAccessible(true);
			return field.getInt(p);
		}
		catch (Throwable t)
		{
			return -1;
		}
	}

	static public String getIP()
	{
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getCanonicalHostName();
		}
		catch (Exception e)
		{
		}
		return "";
	}

	static public String getMyHostname() throws Exception
	{
		ProcessRunner pr = new ProcessRunner(true);
		pr.run("hostname");
		return pr.getStdOut().trim();
	}

	static public boolean isRunning(long pid) throws Exception
	{
		ProcessRunner pr = new ProcessRunner(true);
		int exitCode = pr.run("ps -p " + pid);

		// "ps -p <pid>" returns 0 if pid exists, non-zero otherwise
		return (exitCode == 0); 
	}

	/* JVM specific */
	static public long getMyPid() throws Exception
	{
		// something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');
		log.debug("getMyPid is parsing '" + jvmName + "'");

		if (index < 1) 
		{
			// part before '@' empty (index = 0) / '@' not found (index = -1)
			return 0;
		}
		try 
		{
			long retval = Long.parseLong(jvmName.substring(0, index));
			return retval;
		} catch (NumberFormatException e) 
		{
			return 0;
		}
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("My hostname is: '" + ProcessUtils.getMyHostname() + "'");
		System.out.println("My ip address is: '" + ProcessUtils.getIP() + "'");
		System.out.println("My pid is: " + ProcessUtils.getMyPid());
		System.out.println("isRunning(" + ProcessUtils.getMyPid() + ") = " + isRunning(ProcessUtils.getMyPid()));
		long otherpid = 1;
		System.out.println("isRunning(" + otherpid + ") = " + isRunning(otherpid));
		otherpid = 99999;
		System.out.println("isRunning(" + otherpid + ") = " + isRunning(otherpid));
	}
}
