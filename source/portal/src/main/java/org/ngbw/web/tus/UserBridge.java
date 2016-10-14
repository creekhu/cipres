package org.ngbw.web.tus;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.web.actions.NgbwSupport;
import org.tus.filter.auth.PrincipalManager;

public class UserBridge implements PrincipalManager
{
	private static final Logger logger = Logger.getLogger(UserBridge.class.getName());

    @Override
    public String getUser(HttpSession session)
    {
		String username = null;
        WorkbenchSession wbs = (WorkbenchSession)session.getAttribute(NgbwSupport.WORKBENCH_SESSION);
        if (wbs != null)
        {
			try
			{
            	username = wbs.getUsername();
			}
			catch(Exception e)
			{
				logger.error("", e);
			}
        }
        return username;
    }
}
