package org.ngbw.web.interceptors;

import org.ngbw.web.actions.NgbwSupport;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Interceptor class to supply the most recent action requested by the user
 * to the session, so that the resulting JSP will know which menu tab to
 * highlight.
 * 
 * This interceptor should only be referenced by actions requesting a
 * top-level NGBW menu resource, and should be referenced before the
 * authentication interceptor, if present.
 * 
 * @author Jeremy Carver
 */
@SuppressWarnings("serial")
public class NavigationInterceptor extends AbstractInterceptor
{
	// add target action to session, so that the resulting
	// JSP will know which menu tab to highlight
	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation invocation) throws Exception {
		NgbwSupport action = (NgbwSupport)invocation.getAction();
		String target = invocation.getInvocationContext().getName();
		action.getSession().put(NgbwSupport.TAB, target);
		return invocation.invoke();
	}
}
