package org.ngbw.restclient; 

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.FileData;
import org.ngbw.restdatatypes.ResultsData;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.WorkingDirData;


@SuppressWarnings("serial")
public class TaskStatus extends BaseAction 
{
	private static final Log log = LogFactory.getLog(TaskStatus.class.getName());
	public String cipresUrl;

	// taskStatus.jsp accesses this variable directly.
	public StatusData statusData;
	public ResultsData rd = null;
	public WorkingDirData wdd = null;
	public Collection<FileData> resultFiles = null;
	public Collection<FileData> workingDirFiles = null;

	/*
		"cipresUrl" is a query string parameter on taskList.jsp page link that 
		brings us to this action.  Therefore struts calls this method before
		the body of the action (which is "execute" by default).
	*/
	public void setCipresUrl(String url)
	{
		log.debug("got job status url: " + url);
		this.cipresUrl = url;
	}

	public String execute()
	{
		// Todo: Make a call to get the resultsUri or workingDir uri 
		// while we're at it so we can display a table of the files in the taskStatus page.

		statusData = getFromUrl(cipresUrl, StatusData.class);
		if (statusData.terminalStage == true)
		{
			//resultFiles = getFromUrl(statusData.resultsUri.url, new GenericType<Collection<FileData>>() {});
			rd = getFromUrl(statusData.resultsUri.url, ResultsData.class);
			if (rd == null || rd.jobfiles == null || rd.jobfiles.size() == 0)
			{
				log.debug("resultFiles is NULL");
			} else
			{
				resultFiles = rd.jobfiles;
				log.debug("resultFiles has " + resultFiles.size() + " elements.");
			}
			
		} else
		{
			//workingDirFiles = getFromUrl(statusData.workingDirUri.url, new GenericType<Collection<FileData>>() {});
			wdd = getFromUrl(statusData.workingDirUri.url, WorkingDirData.class);
			if (wdd == null || wdd.jobfiles == null || wdd.jobfiles.size() == 0)
			{
				;
			} else
			{
				workingDirFiles = wdd.jobfiles;
			}
		}
		if (resultFiles == null)
		{
			resultFiles = new ArrayList<FileData>();
		}
		if (workingDirFiles  == null)
		{
			workingDirFiles = new ArrayList<FileData>();
		}
		return "list";
	}
}
	

