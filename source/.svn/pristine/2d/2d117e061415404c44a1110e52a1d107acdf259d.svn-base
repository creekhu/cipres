/**
 * This is a completely static class that holds values from globus.properties. 
 * The globus.properties file is read when this class is loaded.
 *  
 */

package org.ngbw.sdk.tool;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;

public class GlobusConfig 
{
	private static final Log m_log = LogFactory.getLog(GlobusConfig.class.getName());
	private static final String CONFIG_URL = "globus.properties";

	private static String host;
	private static int port;
	private static String username;
	private static String password;
	private static String proxyFile; 
	private static long maxMinutes;
	private static int pollInterval; 
	private static int quickPollInterval; 
	private static int mediumPollInterval; 
	private static int lifetime; 
	private static int minLifetime; 
	private static String globusDir;

	public static String getHost() { return host; }
	public static String getUsername() { return username; }
	public static String getPassword() { return password; }
	public static String getProxyFile() { return proxyFile; }
	public static int getPort() { return port; }
	public static long getMaxMinutes() { return maxMinutes; }
	public static int getPollInterval() { return pollInterval; }
	public static int getQuickPollInterval() { return quickPollInterval; }
	public static int getMediumPollInterval() { return mediumPollInterval; }
	public static int getLifeTime() { return lifetime; }
	public static int getMinLifeTime() { return minLifetime; }
	public static String getGlobusDir() { return globusDir; }

	static
	{
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			throw new WorkbenchException("Error initializing from " + CONFIG_URL, e);
		}
	}


	private static void initialize() throws Exception
	{
		if (proxyFile == null)
		{
			InputStream stream = GlobusCred.class.getClassLoader().getResourceAsStream(CONFIG_URL);
			if (stream == null)
			{
				throw new WorkbenchException("Couldn't find resource " + CONFIG_URL);
			}
			Properties props = new Properties();
			props.load(stream);

			host = props.getProperty("grid.myproxy.server").trim();
			m_log.debug("globus.properties host='" + host + "'");

			String tmp = props.getProperty("grid.myproxy.port").trim();
			m_log.debug("globus.properties port='" + tmp + "'");
			port = new Integer(tmp);
			username = props.getProperty("grid.myproxy.username").trim();
			password = props.getProperty("grid.myproxy.password").trim();
			proxyFile = props.getProperty("grid.myproxyfile").trim();

			tmp = props.getProperty("grid.job.lifetime.max").trim();
			maxMinutes = new Long(tmp);

			tmp = props.getProperty("grid.job.poll.interval").trim();
			pollInterval  = new Integer (tmp);

			tmp = props.getProperty("grid.job.quick.poll.interval").trim();
			quickPollInterval  = new Integer (tmp);

			tmp = props.getProperty("grid.job.medium.poll.interval").trim();
			mediumPollInterval  = new Integer (tmp);

			tmp = props.getProperty("grid.myproxy.lifetime").trim();
			lifetime  = new Integer (tmp);

			tmp = props.getProperty("grid.myproxy.min.lifetime").trim();
			minLifetime  = new Integer (tmp);

			globusDir = props.getProperty("grid.globus.dir").trim();
		}
	}

}

