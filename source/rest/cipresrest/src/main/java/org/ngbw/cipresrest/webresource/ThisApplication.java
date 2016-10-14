/*

*/
package org.ngbw.cipresrest.webresource;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.jobs.UsageLimit;


public class ThisApplication 
{
	private static final Log log = LogFactory.getLog(ThisApplication.class.getName());
	private static ThisApplication instance;
	private String tusStorage;
	private String tusURLString;
	private URL tusURL;
	private String defaultImportFolder;

	public static synchronized ThisApplication getInstance() throws Exception
	{
		if (instance == null)
		{
			instance = new ThisApplication();
		}
		return instance;
	}

	private ThisApplication()  throws MalformedURLException
	{
		log.debug("STARTING APPLICATION INIT");

		// These reads tool registry and other xml config files, also workbench.properties
		// Logs info about the configuration.
		Workbench.getInstance();

		// Gets default values for usage limits from workbench.properties
		// Logs the values. 
		UsageLimit.getInstance().logDefaults();

		log.debug("FINISHED APPLICATION INIT");

		tusURLString = Workbench.getInstance().getProperties().getProperty("tus.url").trim();
		tusStorage = Workbench.getInstance().getProperties().getProperty("tus.storage").trim();
		defaultImportFolder = Workbench.getInstance().getProperties().getProperty("default.import.folder").trim();
		log.debug("tus.url=" + tusURL + ", tus.storage=" + tusStorage);

		try
		{
			tusURL = new URL(tusURLString);
		}
		catch(MalformedURLException e)
		{
			log.error("INVALID TUS URL:" + tusURLString, e);
			throw e;
		}
	}

	public String getTusStorage() { return this.tusStorage; }
	public URL getTusURL() { return this.tusURL; }
	public String getTusURLString() { return this.tusURLString; }
	public String getDefaultImportFolder() { return this.defaultImportFolder; }
}
