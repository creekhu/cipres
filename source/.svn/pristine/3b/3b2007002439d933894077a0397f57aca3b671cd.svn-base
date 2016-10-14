package org.ngbw.cipresrest.webresource;

import org.ngbw.sdk.database.User;
import org.ngbw.sdk.jobs.JobFile;

import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.FileData;


/*
	Todo: should probably make 2 classes, one for working dir files and one for
	result docs.  In the meantime, though, for 
		- working dir files, documentId is 0, and we use the filename in the uri
		- results, documentId is non-zero and we use it in the uri
*/
public class JFile 
{
	BaseResource resource;
	JobFile jobFile;
	String jobHandle;
	User user;

	public JFile() {;} 

	public JFile(BaseResource resource, String jobHandle, User user, JobFile jobFile)
	{
		this.resource = resource;
		this.jobFile = jobFile;
		this.jobHandle = jobHandle;
		this.user = user;
	}

	public FileData toFileData() throws Exception
	{
		FileData fd = new FileData();
		fd.downloadUri = getFileUri(); 
		fd.jobHandle = jobFile.getJobHandle(); 
		fd.pathname = jobFile.getPathname(); 
		fd.filename = jobFile.getFilename(); 
		fd.length = jobFile.getLength(); 
		fd.dateCreated = jobFile.getDateCreated(); 
		fd.dateModified = jobFile.getDateModified(); 
		fd.parameterName = jobFile.getParameterName(); 
		fd.outputDocumentId = jobFile.getOutputDocumentId(); 
		return fd;
	}

	private LinkData getFileUri() throws Exception
	{
		if (jobFile.getOutputDocumentId() == 0)
		{
			return resource.getJobUriBuilder(jobHandle, user.getUsername()).getWorkingFileUri(jobFile.getFilename());
		}
		return resource.getJobUriBuilder(jobHandle, user.getUsername()).getResultUri(String.valueOf(jobFile.getOutputDocumentId()), jobFile.getFilename());
	}

}
