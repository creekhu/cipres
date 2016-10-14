package org.ngbw.restclient; 

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Application
{
	private static final Log log = LogFactory.getLog(Application.class.getName());

	private static Application SINGLETON;
	private Properties properties;

	private String restUrl;
	private String baseUrl; 
	private String appname;
	private String umbrella_username;
	private String umbrella_password;
	private String appkey; 

	public String getRestUrl() { return restUrl; }
	public String getBaseUrl() { return baseUrl; }
	public String getAppname() { return appname; }
	public String getUmbrellaUsername() { return umbrella_username; }
	public String getUmbrellaPassword() { return umbrella_password; }
	public String getAppKey() { return appkey; }

	public static synchronized Application getInstance() 
	{
		if (SINGLETON == null)
		{
			SINGLETON = new Application();
		}
		return SINGLETON;
	}

	private boolean loadProperties(Properties properties, String filename) throws Exception
	{
		InputStream is = null;
		File file = new File(filename);
		log.debug("Looking for properties file " + filename);
		if (file.exists())
		{
			try
			{
				is = new FileInputStream(file);
				properties.load(is);
				log.debug("Loaded properties from: " + filename);
				return true;
			}
			catch(Exception e) { ; }
			finally
			{
				if (is != null)
				{
					is.close();
				}
			}
		}
		log.debug("Didn't find or couldn't read properties file " + filename);
		return false;
	}

	private Application() 
	{
		Properties properties = new Properties();
		final List<String> requiredProperties = 
			Arrays.asList("URL", "UMBRELLA_APPNAME", "UMBRELLA_APPID", "ADMIN_USERNAME", "ADMIN_PASSWORD") ;
		try
		{
			String propFile1 =  System.getenv("SDK_VERSIONS") + "/testdata/pycipres.conf";
			String propFile2 = System.getenv("HOME") + "/pycipres.conf";
			String propFile3 =  System.getenv("PYCIPRES");
			loadProperties(properties, propFile1);
			loadProperties(properties, propFile2);
			if (propFile3 != null)
			{
				loadProperties(properties, propFile3);
			}
			String missingProperties = "";
			for (String key : requiredProperties)
			{
				String value = properties.getProperty(key);
				if (value == null || value.trim().length() == 0)
				{
					missingProperties += key + " ";
				}
			}
			if (missingProperties.length() > 0)
			{
				throw new Exception("Missing required properties: " + missingProperties);
			}
			restUrl = properties.getProperty("URL");
			baseUrl = restUrl + "/job/";
			appname = properties.getProperty("UMBRELLA_APPNAME");
			appkey = properties.getProperty("UMBRELLA_APPID"); 
			umbrella_username = properties.getProperty("ADMIN_USERNAME");
			umbrella_password = properties.getProperty("ADMIN_PASSWORD");
			log.info("URL=" + restUrl + 
				", UMBRELLA_APPNAME=" + appname +
				", UMBRELLA_APPID=" + appkey +
				", UMBRELLA_USERNAME=" + umbrella_username );
		}
		catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException("Application Configuration error: " + e.toString());
		}
	}

	public String getProperty(String name)
	{
		return properties.getProperty(name);
	}

}
