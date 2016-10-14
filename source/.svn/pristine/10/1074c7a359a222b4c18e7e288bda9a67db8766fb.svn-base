/*
	This is run on application startup.  We use it to initialize our Application singleton.
	At the moment, the Application just initializes the Workbench singleton.
*/
package org.ngbw.cipresrest.webresource;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;


public class Startup extends ResourceConfig 
{
	private static final Log log = LogFactory.getLog(Startup.class.getName());

	public Startup()
	{
		packages(true, "org.ngbw.cipresrest");
		register(MultiPartFeature.class);
		register(RolesAllowedDynamicFeature.class);
		property("jersey.config.xml.formatOutput", true);
		property("jersey.config.jaxb.collections.processXmlRootElement", true);
		property("jersey.config.contentLength.buffer", 0);
		log.debug("Application Startup.");
		try
		{
			ThisApplication.getInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
