/*
 * JobOutput.java
 */
package org.ngbw.sdk.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.TaskOutputSourceDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;



/**
 * @author Terri Liebowitz Schwartz
 *
 */
/*
*/
public class JobOutput 
{
	private static final Log log = LogFactory.getLog(JobOutput.class.getName());
	private String jobHandle;
	User user;
	Application application;

	public JobOutput(String jobHandle, User user , Application application) 
	{
		this.jobHandle = jobHandle; 
		this.user = user;
		this.application = application;
	}

	public List<JobFile> listDocuments() throws Exception
	{
		// This is what we're returning.
		List<JobFile> jfs = new ArrayList<JobFile>();

		JobStatus jobStatus = new JobStatus(jobHandle, user, application);
		Task task = jobStatus.getTask(); 

		Map<String, List<TaskOutputSourceDocument>> outputParameters= task.output();


		for (String parameterName: outputParameters.keySet())
		{
			List<TaskOutputSourceDocument> documents = outputParameters.get(parameterName);
			for (TaskOutputSourceDocument doc : documents)
			{
				JobFile jf= new JobFile();
				jf.setJobHandle(jobStatus.getJobHandle());
				jf.setParameterName(parameterName);
				jf.setOutputDocumentId(doc.getOutputDocumentId());
				jf.setFilename(doc.getName());
				jf.setLength(doc.getDataLength());
				jfs.add(jf);
			}
		}
		return jfs;

	}

	public JobFile getDocumentInfo(long outputDocumentId) throws Exception
	{
		/*
			Make sure that this outputDocumentId is actually associated with the jobhandle. Since 
			we've already made sure that the jobHandle is owned by the specified user 
			this insures that the user is allowed to access this document.
		*/
		boolean foundIt = false;
		List<JobFile> jfs = listDocuments();
		for (JobFile jf : jfs)
		{
			if (jf.getOutputDocumentId() == outputDocumentId)
			{
				return jf;
			}
		}
		throw new FileNotFoundException("document " + outputDocumentId + " not found in job " + this.jobHandle);
	}

	public InputStream getDocument(JobFile jf) throws Exception
	{

		// This will throw a database.NotExistException if the document doesn't exist.
		InputStream is = null;
		TaskOutputSourceDocument doc = new TaskOutputSourceDocument(jf.getOutputDocumentId());
		try
		{
			is = doc.getDataAsStream();
			return is;
		}
		catch (Exception e)
		{
			if (is != null)
			{
				is.close();
			}
			throw e;
		}
	}

}	
