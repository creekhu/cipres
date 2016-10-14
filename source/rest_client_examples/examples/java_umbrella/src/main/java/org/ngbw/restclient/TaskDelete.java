package org.ngbw.restclient; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@SuppressWarnings("serial")
public class TaskDelete extends BaseAction 
{
	private static final Log log = LogFactory.getLog(TaskStatus.class.getName());
	public String cipresUrl = null;

	/*
		cipresUrl is a query string parameter on the link that deletes a specific task
		so if we're coming from there, setCipresUrl will have been called before execute().

		However, delete all my tasks link doesn't have a cipresUrl parameter.

		DELETE ALL is no longer allowed by the rest api except for cipres
		admin's because it is too slow/ties up a thread for too long.
	*/
	public void setCipresUrl(String url)
	{
		log.debug("got job status url: " + url);
		this.cipresUrl = url;
	}

	public String execute()
	{
		if (cipresUrl != null)
		{
			if (deleteUrl(cipresUrl) == true)
			{
				addActionMessage("Job Deleted.");
			}
		} else
		{
			addActionError("Something went wrong, no job specified.");
			return "error";
			/*
			if (deleteUrl(app.getBaseUrl() + getCipresUser()) == true)
			{
				addActionMessage("All jobs deleted.");
			}
			*/
		}
		return "success";
	}
}
