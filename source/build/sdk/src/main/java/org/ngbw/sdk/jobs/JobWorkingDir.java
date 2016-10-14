/*
 * JobWorkingDir.java
 */
package org.ngbw.sdk.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.tool.WorkingDirectory;
import org.ngbw.sdk.Workbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;




/**
 * @author Terri Liebowitz Schwartz
 *
 */
/*
*/
public class JobWorkingDir 
{
	private static final Log log = LogFactory.getLog(JobWorkingDir.class.getName());
	private String jobHandle;
	private User user;
	private Application application;
	private Task task;
	private WorkingDirectory wd;

	public JobWorkingDir(String jobHandle, User user, Application application)  throws Exception
	{
		this.jobHandle = jobHandle; 
		JobStatus jobStatus = new JobStatus(jobHandle, user, application);
		task = jobStatus.getTask(); 
		user = new User(task.getUserId());
		wd = new WorkingDirectory(Workbench.getInstance().getServiceFactory(), task);
	}

	public boolean fileExists(String filename) throws Exception
	{
		try
		{
			return wd.fileExists(filename);
		} catch(Exception e)
		{
			//log.error("Exception while checking if working directory file exists.", e);
			throw e;
		}
	}

	public boolean directoryExists() throws Exception
	{
		try
		{
			return wd.workingDirectoryExists();
		} catch(Exception e)
		{
			//log.error("Exception while checking if working directory.", e);
			throw e;
		}
	}

	public List<JobFile>listFiles() throws Exception
	{
		List<FileHandler.FileAttributes> files = null; 
		try
		{
			// This can fail for a lot of reasons that are hard to distinguish.  The one 
			// we want to single out, since it's more of a user error (or not an error at all)
			// is if the working directory doesn't exist, so we check for that below.
			files = wd.listWorkingDirectory();
		}

		// Unfortunately different file handlers throw different types of
		// exceptions when we try to list a non-existant directory so we need to
		// catch generic Exception and then try to figure out what happened.
		catch(Exception origE)
		{
			//log.debug("Caught exception "  +  origE.getMessage());
			boolean found;
			try
			{
				found = wd.workingDirectoryExists();
			}
			catch(Exception e)
			{
				//log.debug("Caught exception trying to check whether directory exists.");
				throw e;
			}
			if (!found)
			{
				//log.debug("Directory doesn't exist so throwing FileNotFoundException");
				//throw new FileNotFoundException("Working directory doesn't exist.");
				return new ArrayList<JobFile>();
			}
			//log.debug("Directory DOES exist so re-throwing the exception");
			throw origE;
		}

		List<JobFile> retval = new ArrayList<JobFile>();
		for (FileHandler.FileAttributes fa : files)
		{
			JobFile jf = new JobFile();
			jf.setParameterName("");
			jf.setOutputDocumentId(0);
			jf.setFilename(fa.filename);
			jf.setLength(fa.size);
			jf.setDateModified(fa.mtime);
			retval.add(jf);
		}
		return retval;
	}

	public JobFile getFileInfo(String filename) throws Exception
	{
		List<JobFile> list = listFiles(); 
		for (JobFile jf : list)
		{
			if (jf.getFilename().equals(filename))
			{
				return jf;
			}
		}
		throw new FileNotFoundException("File: " + filename + " not found in job's working directory.");
	}

	public InputStream getFile(JobFile jf) throws Exception
	{
		return getFile(jf.getFilename());
	}

	public InputStream getFile(String filename) throws Exception
	{
		InputStream is = null;
		try
		{
			is = wd.streamFileFromWorkingDirectory(filename);
			return is;
		}
		catch (Exception origE)
		{
			//log.debug("Caught exception "  +  origE.getMessage());
			boolean found;
			try
			{
				found = fileExists(filename);
			}
			catch(Exception e)
			{
				//log.debug("Caught exception trying to check file exists in working dir: " + filename);
				throw e;
			}
			if (!found)
			{
				//log.debug("File doesn't exist so throwing FileNotFoundException");
				throw new FileNotFoundException("Working directory file doesn't exist: " + filename);
			}
			//log.debug("File DOES exist so re-throwing the exception");
			throw origE;
		}
	}
}	
