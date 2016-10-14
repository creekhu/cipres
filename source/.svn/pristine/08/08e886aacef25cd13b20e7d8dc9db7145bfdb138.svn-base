package org.ngbw.cipresrest.webresource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.container.ContainerRequestContext;
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
import java.net.URLDecoder;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.jobs.Job;
import org.ngbw.sdk.jobs.JobFile;
import org.ngbw.sdk.jobs.JobOutput;
import org.ngbw.sdk.jobs.JobMetadata;
import org.ngbw.sdk.jobs.FileNotFoundException;
import org.ngbw.sdk.jobs.JobWorkingDir;
import org.ngbw.sdk.jobs.UserMismatchException;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.UsageLimitException;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.jobs.UsageLimit;
import org.ngbw.sdk.tool.TaskValidator;

import org.ngbw.cipresrest.auth.SecurityFilter;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.FileData;
import org.ngbw.restdatatypes.ToolData;
import org.ngbw.restdatatypes.WorkingDirData;
import org.ngbw.restdatatypes.ResultsData;
import org.ngbw.restdatatypes.StatusData;


/*
	GET $URL
		Returns urls for the top level service entry points.
*/


@Path("/v1/")
public class CipresRoot extends BaseResource
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresRoot.class.getName());


	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<LinkData> getSubmittedJobs(@DefaultValue("false") @QueryParam("expand") boolean expand) 
		throws Exception
	{
		List<LinkData> links = new ArrayList<LinkData>(2);

		UriBuilder ub = uriInfo.getBaseUriBuilder();
		LinkData ld = new LinkData(ub.path(CipresJob.class).toTemplate(), 
			JobList.name + ", a collection of jobstatus objects", 
			"Job API.  User credentials required.");
		links.add(ld);

		ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresTool.class).build();
		ld = new LinkData(uri.toString(), 
			"A collection of " + ToolData.name + " objects", 
			"Tool API.  Public.");
		links.add(ld);

		return links;
	}
}
