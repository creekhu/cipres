package org.ngbw.sdk.common.util;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.cipres.utils.MailService;
import org.ngbw.sdk.Workbench;

/*
	Email an error message to app's administrator
*/
public class SendError 
{
	private static final Log log = LogFactory.getLog(SendError.class.getName());

	private static ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("tool/spring-mail.xml");
	private static MailService mailservice = (MailService)appContext.getBean("mailService");

	public static void send(String mailString)
	{
		Properties properties = Workbench.getInstance().getProperties();

		String to = properties.getProperty("email.adminAddr");
		String from = properties.getProperty("email.adminAddr");
		String instance = properties.getProperty("application.instance");
		String portalName = properties.getProperty("portal.name");

		String msg = "The following is a message from the application with this ID: " + instance;
		msg += ".  See log files for details.\n\n";
		msg += mailString;

		try
		{
			mailservice.sendSimpleMessage(to, from, portalName + " ALERT", msg);
		}
		catch(Exception e)
		{
			log.error("Unable to email message - check build properties: " + msg, e);
		}
	}
}
