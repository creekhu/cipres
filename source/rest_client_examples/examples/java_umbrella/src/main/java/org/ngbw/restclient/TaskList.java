package org.ngbw.restclient; 

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.StatusData;


@SuppressWarnings("serial")
public class TaskList extends BaseAction 
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(TaskList.class.getName());
	public Collection<LinkData> tasks = null;


	public String execute()
	{
		JobList jobList = null;

		//tasks = getFromUrl(app.getBaseUrl() + getCipresUser(), new GenericType<Collection<LinkData>>() {});
		jobList = getFromUrl(app.getBaseUrl() + getCipresUser(), JobList.class);

		tasks = new ArrayList<LinkData>();
		if (jobList != null)
		{
			for (StatusData sd : jobList.jobstatus)
			{
				// We just want the links to the jobstatus objects.
				tasks.add(sd.selfUri);
			}
		}
		return "list";
	}
}
	

