package org.ngbw.web.interceptors;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.web.actions.NgbwSupport;
import org.ngbw.web.controllers.SessionController;

public class SessionListener implements HttpSessionListener
{
	// static logger class
	public static Logger logger = Logger.getLogger(SessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
		Workbench.getInstance();
		HttpSession session = event.getSession();
		logger.debug("HTTP session " + session.getId() + " was created.");
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();

		logger.debug("HTTP session " + session.getId() + " has expired.");
		WorkbenchSession workbenchSession =
			(WorkbenchSession)session.getAttribute(NgbwSupport.WORKBENCH_SESSION);
		if (workbenchSession != null) {
			SessionController controller = new SessionController(workbenchSession);
			controller.logout();
		}
		String isIplantUser = (String)session.getAttribute(NgbwSupport.IPLANT_USER);
		if (isIplantUser != null)
		{
			logger.debug("This was an iplant user.");
		}
	}
}
