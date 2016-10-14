package org.ngbw.restclient; 

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;


@SuppressWarnings("serial")
public class AuthenticationInterceptor implements Interceptor
{
	private static final Log log = LogFactory.getLog(AuthenticationInterceptor.class.getName());

	public void init() 
	{
		log.info("Intializing AuthenticationInterceptor");
	}

	public void destroy() 
	{
	}

	public String intercept(ActionInvocation invocation) throws Exception 
	{
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		String user = (String)session.get("user");
		if (user == null) 
		{
			log.debug("interceptor returning login");
			return "login";
		} 
		log.debug("interceptor invoking action");
		return invocation.invoke();
  	}

}

