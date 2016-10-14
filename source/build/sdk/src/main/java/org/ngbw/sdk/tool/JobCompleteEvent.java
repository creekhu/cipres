/*
 * JobCompleteEvent.java
 *
 *	@author Terri Liebowitz Schwartz
 */
package org.ngbw.sdk.tool;

import java.util.HashMap;
import java.util.Map;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.jobs.JobMetadata;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.cipres.utils.MailService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JobCompleteEvent
{
	private static final Log log = LogFactory.getLog(JobCompleteEvent.class.getName());
	private Task task;
	private User user;
	private String username;
	private String emailAddress;
	private String jobLabel;
	private String errMsg;
	private boolean isRestUser = false;
	private boolean sendRestEmail = false;
	JobMetadata md = null;
	private String appname;

//	public JobCompleteEvent(String username, String emailAddress, String jobLabel, String errMsg)
	public JobCompleteEvent(Task task, User user, String errMsg) throws Exception
	{
		this.task = task;
		this.user = user;
		this.appname = task.getAppname();
		this.username = user.getUsername();
		this.emailAddress = user.getEmail();
		this.jobLabel = task.getLabel();
		if (errMsg == null)
		{
			this.errMsg = "";
		} else
		{
			this.errMsg = errMsg;
		}
		if (user.getRole().isRestEndUser()) 
		{
			log.debug("User is a REST End User");
			isRestUser = true;
			md = JobMetadata.fromMap(task.properties());
			if (md != null && md.getStatusEmail() == true)
			{
				log.debug("Metadata says YES, send email");
				sendRestEmail = true;
				if (md.getEmailAddress() != null && md.getEmailAddress().length() > 0)
				{
					this.emailAddress = md.getEmailAddress();
				}
			} else
			{
				log.debug("Metadata says NO, don't send email." );
				if (md == null)
				{
					log.debug("Metadata is null.");
				}
			}
		}
	}
	
	public void notifyComplete() throws Exception
	{
		ClassPathXmlApplicationContext appContext = 
			new ClassPathXmlApplicationContext("tool/spring-mail.xml");
		MailService mailservice = (MailService)appContext.getBean("mailService");

		Enable enabled = (Enable)appContext.getBean("jobEmailEnable");
		if (enabled.getEnable() == false)
		{
			log.debug("Job completion emails are disabled.");
			return;
		}
		if (emailAddress.equals("") ||  !emailAddress.contains("@"))
		{
			log.debug("Email address '" + emailAddress + "' isn't valid.");
		}

		Map<String, String> properties = new HashMap<String, String>();
		properties.put("errMsg", errMsg);

		if (isRestUser)
		{
			if (sendRestEmail)
			{
				String subject;
				String see_website;
				Application app = Application.find(appname);
				appname = app.getLongName();
				properties.put("application", appname);
				if (app.getWebsiteUrl() == null || app.getWebsiteUrl().length() == 0)
				{
					see_website = "";
				} else
				{
					see_website = "See " + app.getWebsiteUrl();
				}
				properties.put("see_website", see_website);
				if (md != null && md.getClientJobName() != null)
				{
					jobLabel = md.getClientJobName();
				} else
				{
					jobLabel = task.getJobHandle(); 
				}
				properties.put("jobLabel", jobLabel);
				properties.put("username", user.getEndUsername());

				subject = appname + " Job Finished";
				log.debug("Sending job completion email for rest user to " + emailAddress);
				mailservice.sendMimeMessage(emailAddress, null, subject, "restjob", properties);
			}
		} else
		{
			properties.put("username", username);
			properties.put("jobLabel", jobLabel);

			// arguments are <to>, <from>, <subject>, <resource-template-prefix>, <properties>
			// we leave <from> as null so that it's picked up from spring-mail.xml which can
			// be customized at build time, depending on the project, etc.
			log.debug("Sending job completion email for cipres portal user to " + emailAddress);
			mailservice.sendMimeMessage(emailAddress, null, "Job Finished", "job", properties);
		}
	}

	public static class Enable
	{
		private boolean enable;
		public Enable(){;}
		public boolean getEnable() { return enable; }
		public void setEnable(boolean enable) { this.enable = enable; } 
	}

	public static void main(String[] args) throws Exception
	{
		String jh = System.getProperty("task");
		String msg = System.getProperty("msg");

		if (jh == null)
		{
			log.error("Missing system property 'task' - which should give the jobhandle");
			System.exit(1);
		}
		Workbench.getInstance();
		Task task  = Task.findTaskByJobHandle(jh);
		User user = new User(task.getUserId());
		String email = user.getEmail();
		if (!email.equals("") && email.contains("@"))
		{
			JobCompleteEvent e = new JobCompleteEvent(task, user, msg);
			e.notifyComplete();
		} else
		{
			log.info("Not a valid email address for user " + user.getUsername());
		}

	}

};
