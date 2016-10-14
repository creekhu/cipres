package org.ngbw.utils;
import java.util.List;
import java.util.concurrent.*;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.tool.BaseProcessWorker;
import org.ngbw.sdk.tool.TaskMonitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.ConnectionSource;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;

/*
	Rest service user registration requires users to click on a link
	that is emailed to him, to verify his email address.  When he does so, we set 
	User.activation_code = null, which 'activates' his account.

	But, sometimes a user won't receive the email, so we want to get rid of unactivated
	accounts after a certain period of time, mostly so that the user can register
	again with the same username and maybe the same email address.

	See sdk/scripts/DeleteUnactivatedAccountsD.
*/
public class DeleteUnactivatedAccounts
{
	private static final Log log = LogFactory.getLog(DeleteUnactivatedAccounts.class.getName());
	private static String m_maxHours;
	private static Boolean m_debug;
	private static Boolean m_restOnly = true;

	public static void main(String[] args)
	{
		try
		{
			Workbench wb = Workbench.getInstance();
			log.debug("DELETE UNACTIVATED ACCOUNTS: workbench initialized.");

			//These 2 lines close the connection pool created in Workbench.getInstance() and open an 
			// unpooled connection source. 
			ConnectionManager.getConnectionSource().close();
			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			m_debug = Boolean.valueOf(System.getProperty("debug"));
			m_restOnly = Boolean.valueOf(System.getProperty("restOnly"));
			m_maxHours = System.getProperty("hours");
			if (m_maxHours == null)
			{
				throw new Exception("Missing system property hours");
			}
			int hours;
			try
			{
				hours = Integer.valueOf(m_maxHours);
			}
			catch(NumberFormatException nfe)
			{
				throw new Exception("hours must be an integer");
			}
			log.debug("hours=" + hours + ", debug=" + m_debug + ", restOnly=" + m_restOnly);

			List<User> candidates = User.findExpiredRegistrations(hours);
			log.debug("There are " + candidates.size() + " unactivated accounts older than " + hours + " hours.");

			for (User user : candidates)
			{
				String adate = user.getActivationSent().toString();
				long uid = user.getUserId();
				String username = user.getUsername();

				if (m_restOnly)
				{
					if (user.getRole() != UserRole.REST_USER)
					{
						log.debug("Skipping " + user.getUsername() + " with role " + user.getRole()); 
						continue;
					}
				}
				if (m_debug == false)
				{
					user.delete();
					log.info("Deleted user " + username + ", uid=" + uid + ", created=" + adate); 
				} else
				{
					log.info("Would delete user " + username + ", uid=" + uid + ", created=" + adate); 
				}
			}
		}
		catch (Exception e)
		{
			log.error("", e);
			return;
		}
		finally
		{
			ConnectionSource source = ConnectionManager.getConnectionSource();
			if (source != null)
			{
				try
				{
					source.close();
				}
				catch(Exception e)
				{
					log.error("", e);
				}
			}
		}
	}

}
