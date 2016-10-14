package org.ngbw.cipresrest.webresource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
//import javax.activation.MimetypesFileTypeMap;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import java.net.URI;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.jobs.Job;
import org.ngbw.sdk.jobs.JobFile;
import org.ngbw.sdk.jobs.JobOutput;
import org.ngbw.sdk.jobs.JobMetadata;
import org.ngbw.sdk.jobs.FileNotFoundException;
import org.ngbw.sdk.jobs.JobWorkingDir;
import org.ngbw.sdk.jobs.UserMismatchException;
import org.ngbw.sdk.core.shared.UserRole;

import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.FileData;
import org.ngbw.restdatatypes.WorkingDirData;
import org.ngbw.restdatatypes.ResultsData;
import org.ngbw.restdatatypes.StatusData;


public class JobUriBuilder
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(JobUriBuilder.class.getName());
	String jobHandle;
	String username;
	UriInfo uriInfo;

	public JobUriBuilder(UriInfo uriInfo, String jobHandle, String username)
	{
		this.uriInfo = uriInfo;
		this.jobHandle = jobHandle;
		this.username = username;
	}

	/* package methods */
	LinkData getJobUri() throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresJob.class).path(jobHandle).build(username);
		return new LinkData(uri.toString(), StatusData.name, jobHandle);
	}

	public LinkData getResultListUri() throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		ub = ub.path(CipresJob.class).path(jobHandle).path("output");
		URI uri = ub.build(username);
		return new LinkData(uri.toString(), ResultsData.name, "Job Results");
	}

	public LinkData getResultUri( String documentId, String filename) throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		ub = ub.path(CipresJob.class).path(jobHandle).path("output");
		String name;
		ub = ub.path(documentId);
		URI uri = ub.build(username);

		// Put filename instead of documentId in link's title field, more human friendly
		//return new LinkData(uri.toString(), FileData.linkName, documentId);
		return new LinkData(uri.toString(), FileData.linkName, filename);
	}

	public LinkData getWorkingFileUri(String filename) throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		ub = ub.path(CipresJob.class).path(jobHandle).path("workingdir");
		String name;
		ub = ub.path(filename);
		name = FileData.linkName; 
		URI uri = ub.build(username);
		return new LinkData(uri.toString(), FileData.linkName, filename);
	}

	public LinkData getWorkingListUri() throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		ub = ub.path(CipresJob.class).path(jobHandle).path("workingdir");
		String name;
		URI uri = ub.build(username);
		return new LinkData(uri.toString(), WorkingDirData.name, "Job Working Directory");
	}

}


