package org.ngbw.web.interceptors;

import java.util.Map;

import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.web.actions.NgbwSupport;
import org.ngbw.web.actions.ManageTasks;
import org.ngbw.sdk.database.Task;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import org.apache.log4j.Logger;

/**
 * Interceptor class to ensure that an authenticated user is registered
 * in the session before proceeding with the selected action.
 * 
 * This interceptor should be referenced by any action requiring that
 * a user be logged in, and should typically be referenced last.
 * 
 * @author Jeremy Carver
 */
@SuppressWarnings("serial")
public class LoginInterceptor extends AbstractInterceptor
{
	public static Logger logger = Logger.getLogger(AuthenticationInterceptor.class);

	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation invocation) throws Exception 
	{
		// check the session for an authenticated user
		NgbwSupport action = (NgbwSupport)invocation.getAction();

		Map session = null;
		session = action.getSession();
		WorkbenchSession workbenchSession =
			(WorkbenchSession)session.get(NgbwSupport.WORKBENCH_SESSION);
	
		// if authenticated session present, continue.
		if (workbenchSession != null) 
		{
			return NgbwSupport.HOME;
		}
		return invocation.invoke();
	}
}
