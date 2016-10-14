package org.ngbw.cipresrest.webresource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import javax.activation.MimetypesFileTypeMap;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.ngbw.cipresrest.auth.AuthHelper;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.FileData;
import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.ResultsData;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.WorkingDirData;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.jobs.FileNotFoundException;
import org.ngbw.sdk.jobs.Job;
import org.ngbw.sdk.jobs.JobFile;
import org.ngbw.sdk.jobs.JobMetadata;
import org.ngbw.sdk.jobs.JobOutput;
import org.ngbw.sdk.jobs.JobWorkingDir;
import org.ngbw.sdk.jobs.UsageLimit;
import org.ngbw.sdk.jobs.UserMismatchException;
import org.ngbw.sdk.tool.TaskValidator;


/*
	Note that jersey resource classes like CipresJob are instantiated per request.
*/

/*
	In general we require that the user send basic auth credentials for a cipres administrator or a
	REST_USER.  There must also be a cipres_appkey header that identifies the client application.  If
	the client application uses Direct Auth, then the basic auth user must match the username in the url.  
	If the app uses Umbrella Auth, then the real end user name is formed from the cipres-appkey and cipres-eu 
	headers to get a username of the form <appname>.<username>.  This <appname>.username> is what must appear 
	in the <user> part of the url.

	GET $URL/job/<user>  
		Return list of jobs for this user. With optional 'expand=true' query parameter,
		returns full job status object for each job.  Unless called by a cipres admin there will
		be a cipres-appkey header and this will only return jobs associated with the specified application
		(in case user has submitted jobs through several applications).

	GET $URL/job/<user>/<jobhandle>
		Returns job status object for specified job, belonging to specified user. Job must also 
		belong to the calling application, as specified in the cipres-appkey header.

	GET $URL/job/<user>/<jobhandle>/output
		Returns list of result files for specified job.  List will be empty if the job
		hasn't finished yet.

	GET $URL/job/<user>/<jobhandle>/output/<documentId>
		Downloads the result document

	GET $URL/job/<user>/<jobhandle>/workingdir
		Returns list of files in the working directory of the specified job while it's running.  
		List will be empty if the job has finished and results have been transferred back to cipres, 
		or if the job hasn't been staged yet.

	GET $URL/job/<user>/<jobhandle>/workingdir/<filename>
		Downloads file from the working directory

	POST $URL/job/<user>/validate
		See submit below.  This is the same as submit except that the job won't actually be 
		sent to the remote host - it is just a way to validate that the submission request
		is well formed.  If not, an <error> object will be returned to explain what's wrong (eg
		missing parameters, invalid parameters, parameter value out of range, etc).

	POST $URL/job/<user>
		Submit a job by posting multipart form data.  The parameters to be submitted are of four types:
		1) 'tool' gives the name of the tool to run, 2) 'metadata.*' - there are few params that start with
		'metadata.' that the caller can use to associate information with the job.  3) params that start 
		with 'vparam' and correspond to the visible parameters in the tools pise xml file. 4) Input files
		that are to be uploaded as part of the request.  See getting_started_guide.

	DELETE $URL/job/<user>/jobhandle
		Deletes the task.  Cancels it if it's queued or running.

	DELETE $URL/job/<user>
		Deletes all the user's tasks.  TODO: only exposed for cipres administrator's because 
		due to the very inefficient implementation it can take way too long to run.

	See also CipresRestAdmin.java - it implements a method uder $URL/job that 
	allows cipres admin or owner of an umbrella app to get status of jobs for multiple users in
	a single call without sending any end user headers.
*/


// This doesn't seem to work, so calling isUserInRole() manually instead.
//@RolesAllowed("STANDARD")
@Path("/v1/job/{user}")
public class CipresJob extends BaseResource
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresJob.class.getName());

	// The logged in user, or end user specified by request headers 
	@Context SecurityContext securityContext;
	@Context ContainerRequestContext requestContext;

	// The username in the url
	@PathParam("user") String username;

	/* 
		The user corresponding to the username in the url.  Normally this will be the 
		same as requestContext.getProperty(SecurityFilter.USER), i.e either the user
		from the basic auth credentials or the cipres-eu header, but if that user is a 
		cipres admin, then he may put a different name in the url, to request info
		about another user's jobs.
	*/
	User user;

	Application application;

	void initialize() throws Exception
	{
		/*
			Throws Authorization exception if logged in user doesn't meet the rules: must
			be an admin user, or if not, then his username must match the username in the url.
		*/
		securityContext.isUserInRole(UserRole.STANDARD.toString());
		if (user == null)
		{
			user = User.findUserByUsername(username); 
			if (user == null)
			{
				throw new UserMismatchException("User " +  username +  " not found.");
			}
		}
		// note that application will be null if user logged in as cipres admin.
		application = (Application)requestContext.getProperty(AuthHelper.APPLICATION);
	}


	@GET
	//@Produces(MediaType.APPLICATION_XML)
	@Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
	public JobList getSubmittedJobs(@DefaultValue("false") @QueryParam("expand") boolean expand) throws Exception
	{
			initialize();
			JobList jl = new JobList();
			jl.title = "Submitted Jobs";

			List<String> handles = Job.getSubmittedJobList(user, this.application);

			List<StatusData> list  = new ArrayList<StatusData>();
			for (String jh : handles)
			{
				StatusData sd;
				if (expand == false)
				{
					//sd = new StatusData(getJobUri(jh)); 
					JobUriBuilder uri = getJobUriBuilder(jh, this.user.getUsername());
					sd = new StatusData(uri.getJobUri()); 
				} else
				{
					JStatus js = new JStatus(jh, this, this.user, this.application);
					sd = js.toStatusData(); 
				}
				list.add(sd);
			}
			jl.jobstatus = list;
			return jl;
	}


	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public StatusData getJobStatus(@PathParam("id") String jobHandle) throws Exception
	{
		initialize();

		JStatus js = new JStatus((String)jobHandle, this, this.user, this.application);

		StatusData sd = js.toStatusData();

		return sd;
	}

	@GET
	@Path("{id}/output")
	@Produces(MediaType.APPLICATION_XML)
	public ResultsData getJobResultList(@PathParam("id") String jobHandle) throws Exception
	{
	// TODO: add expand parameter
		initialize();
		ResultsData rd = new ResultsData();
		List<FileData> list = new ArrayList<FileData>();

		JobOutput output = Job.getOutput(jobHandle, user, this.application);
		List<JobFile> jobfiles = output.listDocuments();
		for (JobFile details : jobfiles)
		{
			FileData fd = (new JFile(this, jobHandle, user, details)).toFileData();
			list.add(fd);
		}
		rd.jobfiles = list;
		return rd;
	}


	/*
		Todo:
		- check memory consumption on huge downloads
		- can set content-length header from jf.getLength()
		- should we try to set mimetype?
	*/
	@GET
	@Path("{id}/output/{documentId}")
	//@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getResultDocument(	@PathParam("id") String jobHandle, 
											@PathParam("documentId") long documentId ) throws Exception
	{
		initialize();
		InputStream is = null;
		try
		{
		 	JobOutput output = Job.getOutput(jobHandle, user, this.application);
			JobFile jf = output.getDocumentInfo(documentId);
			is = output.getDocument(jf);
			String mimeType = getMimeType(jf.getFilename());

			return Response.ok(is).
				header("Content-Disposition", "attachment; filename=" + jf.getFilename()).
				type(mimeType).
				build();
		}
		catch (NotExistException nee)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw new CipresException( "Document Not Found Error: " + nee.getMessage(), Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		catch(Exception e)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw e;
		}
	}

	@GET
	@Path("{id}/workingdir")
	@Produces(MediaType.APPLICATION_XML)
	public WorkingDirData getWorkingDirList(@PathParam("id") String jobHandle) throws Exception
	{
		initialize();
		List<FileData> list = new ArrayList<FileData>();
		List<JobFile> jobfiles = null;
		WorkingDirData wdd = new WorkingDirData();

		try
		{
			JobWorkingDir wd = Job.getWorkingDir(jobHandle, user, this.application);
			jobfiles = wd.listFiles();
		}
		// working directory not found.
		catch (FileNotFoundException e)
		{
			throw new CipresException( e.getMessage(), Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}

		for (JobFile details : jobfiles)
		{
			FileData fd = (new JFile(this, jobHandle, user, details)).toFileData();
			list.add(fd);
		}
		wdd.jobfiles = list;
		return wdd;
	}

	@GET
	@Path("{id}/workingdir/{filename}")
	//@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getWorkingDirFile(	@PathParam("id") String jobHandle, 
											@PathParam("filename") String filename ) throws Exception
	{
		initialize();
		InputStream is = null;
		try
		{
			JobWorkingDir wd = Job.getWorkingDir(jobHandle, user, this.application);
			JobFile jf = wd.getFileInfo(filename);
			is = wd.getFile(jf);
	
			return Response.ok(is).
				header("Content-Disposition", "attachment; filename=" + filename).
				type(getMimeType(filename)).
				build();
		}
		catch (FileNotFoundException fnfe)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw new CipresException(fnfe.getMessage(), Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		catch (Exception e)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw e;
		}
	}


	/*
		Post to cipresrest/v1/job/validate/<user> just validates the posted job submission.  
		Does not actually submit the job.  Throws Exception on error. JobValidationException
		is expected, other exceptions are not.
		Without the validate path segment (submitJob method) it does submit the job.
	*/
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("/validate")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public StatusData validateJob(FormDataMultiPart formData) throws Exception
	{
		return submit(formData, true);
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public StatusData submitJob(FormDataMultiPart formData) throws Exception
	{
		return submit(formData, false);
	}

	private StatusData submit(FormDataMultiPart formData, boolean validateOnly) throws Exception
	{
		List<FieldError> errors;
		initialize();

		// throws UsageLimit exception if user already has too many active jobs 
		UsageLimit.getInstance().verifyActiveJobCount(user, application);

		// START Debugging ONLY - todo: caution: if form is very large this builds a very big string.
		// It doesn't seem possible to determine a field's size before reading it into memory.
		String formdata = multiPartFormDataAsString(formData, 100);
		log.debug("Form Parameters Are:\n" + formdata);
		// END Debugging ONLY

		JobFormParser jfp = new JobFormParser(user);
		errors = jfp.parse(formData);
		String tool = jfp.getToolId();
		if (!Workbench.getInstance().getActiveToolIds().contains(tool))
		{
			errors.add(new FieldError(TaskValidator.toolField, "invalid tool"));
		}
		if (errors.size() > 0)
		{
			throw new ValidationException(errors);
		}
		// throws UsageLimit exception if user already has used too many SUs.   
		UsageLimit.getInstance().verifySULimit(user, application, tool, false);

		Map<String, String> metadata  = jfp.getMetadata(); 
		Map<String, List<String>> visibleParameters  = jfp.getVisibleParameters();

		Map<String, UserDataItem> inputFiles = jfp.getInputFiles(); 

		try
		{
			String jobHandleOrCommandline = Job.submit(user,
								this.application,
								JobMetadata.fromMap(metadata),
								tool,
								visibleParameters,
								inputFiles,
								validateOnly);
			if (validateOnly)
			{
				// In this case, Job.submit returned the commandline, not a jobhandle.
				return new StatusData(jobHandleOrCommandline);
			}
			return (new JStatus(jobHandleOrCommandline, this, this.user, this.application)).toStatusData();
		}
		finally
		{
			//deleteUploadedFiles(new ArrayList<File>(inputFiles.values()));	
		}
	}

	private void deleteUploadedFiles(List<File> filelist)
	{
		for (File f : filelist)
		{
			f.delete();
		}
	}

	@DELETE
	@Path("{id}")
	public void deleteJob(@PathParam("id") String jobHandle) throws Exception
	{
		initialize();
		Job.cancel(jobHandle, user, this.application);
	}

	/* 
		This can be much too slow to safely expose (but keeping it for cipresadmin use).
		It's too slow due to doing separate db queries for each job (and each table in 
		each job) AND doing a gsissh qdel for each running job.
	*/
	@DELETE
	public void deleteJob() throws Exception
	{
		securityContext.isUserInRole(UserRole.ADMIN.toString());

		log.debug("Cancelling all of " + username + " jobs");
		initialize();
		Job.cancelAll(user, this.application);
	}


	/*
		Todo: add types like .aln, .dnd, .fasta, .nex, etc as text/plain.
		Is there a way to say the default is text/plain?
	*/
	private String getMimeType(String filename)
	{

		// This is always returning application/octet-stream, at least on my mac.  I guess
		// I would need to create a map in one of the locations the documentation indicates.
		//String mimeType = mimeTypesMap.getContentType(filename);

		String mimeType = "text/plain";
		return mimeType;
	}
}


