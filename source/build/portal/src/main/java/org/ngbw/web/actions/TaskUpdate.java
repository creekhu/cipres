package org.ngbw.web.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.tool.Tool;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.api.tool.ParameterValidator;
import org.ngbw.sdk.core.shared.TaskRunStage;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.util.UserDataItemSortableField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.RunningTask;
import org.ngbw.sdk.tool.WorkQueue;
import org.ngbw.sdk.database.StaleRowException;

/**
	Job submissions scripts on remote hosts do a curl to this url to post
	job status changes.

	Struts.xml links this class to the url /portal2/taskupdate.action and
	cipres-*.properties specifies that url as job.callback.url property, which
	is passed to the ProcessWorker so it can pass it to the remote job script.
*/
@SuppressWarnings("serial")
public class TaskUpdate extends NgbwSupport
{
	private static final Log log = LogFactory.getLog(TaskUpdate.class.getName());
	
	public String execute() 
	{
		long taskId;
		String status;

		try
		{
			String tmp = getRequestParameter("taskId");
			taskId = new Long(tmp); 

			status = getRequestParameter("status");

			if (status == null || (status = status.trim()).equals(""))
			{
				throw new Exception("missing status parameter");
			}

			log.debug("Got status:" + status + " for task:" + taskId);

			// throws a runtime exception (WorkbenchException) if record isn't found.
			// todo: should use jobhandle, not task id in the url, since taskids can be recycled when
			// task deleted.
			RunningTask rt = RunningTask.findRunningTaskByTask(taskId);
			if (status.equals("START"))
			{
				//rt.setStatus(RunningTask.STATUS_STARTED);
			} else if (status.equals("DONE"))
			{
				WorkQueue.markDone(rt);
			} else
			{
				throw new Exception("Unexpected job status.  Task=" + taskId + ". Status=" + status);
			}	
			return "success";
		}
		catch(Throwable t)
		{
			log.debug("", t);
			return "error";
		}
	}	
}
