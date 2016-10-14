
package org.ngbw.cipresrest.webresource;

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
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
//import javax.activation.MimetypesFileTypeMap;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import javax.ws.rs.core.MultivaluedMap;
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

public class BaseResource 
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(BaseResource.class.getName());
	@Context UriInfo uriInfo;


	public JobUriBuilder getJobUriBuilder(String jobHandle, String username)
	{
		return new JobUriBuilder(uriInfo, jobHandle, username);
	}


	/*
		BIG problem with this method is that isSimple() is returned for all fields
		so field.getValue() reads entire files into memory.
	*/
	public String multiPartFormDataAsString(FormDataMultiPart formData, int maxlen) 
	{
		String retval = "";
		String value;
		for(String name:  formData.getFields().keySet())
		{
			FormDataBodyPart field = formData.getField(name);
			ContentDisposition cd = field.getContentDisposition();
			// hopefully all file parameters have non null filename,  though I"m not sure if that's true.
			if ((cd != null) && cd.getFileName() != null)
			{
				// just print the filename not its contents.
				retval += (name + "=" + cd.getFileName());
			} else
			{
				if (field.isSimple())
				{
					if (maxlen > 0)
					{
						// this call to getValue() reads whole field into memory.  
						// There's no way to check the length beforehand as far as I can tell.
						int len = field.getValue().length();
						retval += (name + "=" + field.getValue().substring(0, Math.min(maxlen, len)));
						if (len > maxlen)
						{
							retval += "...(" + len + ")";
						}
					} else
					{
						retval += (name + "=" + field.getValue());
					}
				} else
				{
					retval += (name + " is not a simple field type.");
				}
			} 
			retval +="\n";
		}
		return retval;
	}

}


