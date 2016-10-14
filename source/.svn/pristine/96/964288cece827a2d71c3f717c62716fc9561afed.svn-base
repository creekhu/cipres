package org.ngbw.cipresrest.webresource;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.model.Resource;
import org.ngbw.cipresrest.auth.AuthHelper;
import org.ngbw.cipresrest.auth.AuthorizationException;
import org.ngbw.directclient.CiCipresException;
import org.ngbw.directclient.CiCommunicator;
import org.ngbw.restdatatypes.DataItem;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;


/*
	Note that jersey resource classes like CipresImport are instantiated per request.
*/

/*
	POST /file 
		source=/tmpfile/v1/TUS_UUID
		
		Imports an uploaded file to a new UserDataItem in the database. 
		Returns a DataItem object with UserData.self.url = /file/TUS_UUID.

	GET /file/<UUID> ? alt=media
		Returns UserData object with info about the data item, including the 
		label, creation data, folder path, etc.  For the time being, all that
		matters is that this returns NOT_FOUND status until the upload is finished
		and the data has been imported.

		With the optional "alt=media" query parameter downloads the data instead
		of returning a UserData object

	DELETE /file/<UUID>
		Deletes the UserDataItem
*/



@Path("/v1/file/id")
public class CipresImport extends BaseResource
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresImport.class.getName());
	@Context SecurityContext securityContext;
	@Context ContainerRequestContext requestContext;
	User user;
	Application application;

	private String sourceUsername;
	private String sourcePathname;
	private String sourceSuggestedFilename;
	private String sourceID;


	void initialize() throws Exception
	{
		user = (User)requestContext.getProperty(AuthHelper.USER);
		application = (Application)requestContext.getProperty(AuthHelper.APPLICATION);
		if (user == null)
		{
			throw new Exception("No user in security context.");
		}
	}

	/*
		POST /file
			initiate an import to a UserDataItem in the cipres DB.

		TODO: distinguish errors :
			Wrap whatever it is up and return and CipresException???
	*/
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public DataItem importFile(@FormDataParam("source") String sourceURL) throws Exception
	{
		log.debug("In importFile");
		initialize();

		// Make sure source is tus url on this site.  
		verifySourceURL(sourceURL);

		// Do a GET to tus url to get info about the file.
		getSourceInfo(sourceURL.trim()); 

		// Make sure logged in user owns the tus uploaded file.
		if (!sourceUsername.equals(user.getUsername()))
		{
			throw new AuthorizationException("Not authorized to access source document.", AuthHelper.REALM);
		}
		UserDataItem udi = importToDB();

		DataItem di = makeDataItem(udi);
		return di;
	}

	/*
		Verify that the URL passed in with the "source" form parameter refers
		to the tus servlet on this site. 
	*/
	private void verifySourceURL(String sourceURL) throws Exception
	{
		if (sourceURL == null)
		{
			throw new ValidationException("'source' parameter is required");
		}
		String tusURLString = ThisApplication.getInstance().getTusURLString();
		URL wantURL = ThisApplication.getInstance().getTusURL();
		URL gotURL = null;
		try
		{
			gotURL = new URL(sourceURL.trim());
			if (gotURL.getHost() == null || ! gotURL.getHost().equals(wantURL.getHost()))
			{
				log.debug("bad source url, host doesn't match");
				throw new ValidationException("source URL must start with " + tusURLString);
			}
			if (gotURL.getPort() != wantURL.getPort())
			{
				throw new ValidationException("source URL, port doesn't match" );
			} 
			if (!gotURL.getPath().startsWith( wantURL.getPath()))
			{
				throw new ValidationException("source URL, path doesn't match" );
			}
		}
		catch(MalformedURLException e)
		{
			log.debug("malformed source url " + e.getMessage());
			throw new ValidationException("source URL must start with " + tusURLString);
		}
	}

	private UserDataItem importToDB() throws Exception
	{
		String importFolder = ThisApplication.getInstance().getDefaultImportFolder();

		// This moves the source file to db storage location.  If the save fails, we've more or less lost the file.
		UserDataItem udi = new UserDataItem(sourcePathname, user.getHomeFolder().findOrCreateSubFolder(importFolder));

		udi.setLabel(sourceSuggestedFilename);
		udi.setUUID(sourceID);
		
		try
		{
			udi.save();
		}
		catch(SQLIntegrityConstraintViolationException e)
		{
			throw new ValidationException("Source already exists.");
		}
		return udi;
	}

	private void getSourceInfo(String sourceURL) throws Exception
	{
		CiCommunicator comm = new CiCommunicator("", "", "");
		try
		{
			String text = comm.getRawTextFromUrl(sourceURL);
			Properties p = new Properties();
			p.load(new StringReader(text));
			sourceUsername = p.getProperty("username");
			sourcePathname  = p.getProperty("pathname");
			sourceSuggestedFilename = p.getProperty("suggestedFilename");
			sourceID = p.getProperty("id");

			sourceSuggestedFilename = (sourceSuggestedFilename == null ? sourceID : sourceSuggestedFilename);

			log.debug("username=" + sourceUsername + ", pathname=" + sourcePathname + ", suggestedFilename=" +
				sourceSuggestedFilename + ", id=" + sourceID);
			if (sourceUsername == null || sourcePathname == null || sourceSuggestedFilename == null || sourceID == null)
			{
				throw new Exception("source url didn't return the expected information");
			}
			sourceUsername = sourceUsername.trim();
			sourcePathname = sourcePathname.trim();
			sourceSuggestedFilename = sourceSuggestedFilename.trim();
			sourceID = sourceID.trim();
		}
		catch(CiCipresException ce)
		{
			ErrorData ed = ce.getErrorData();
			log.error("Cipres error code=" + ed.code + ", message=" + ed.message);
			if (ce.getHttpStatus() ==  Status.NOT_FOUND.getStatusCode())
			{
				throw new CipresException("source url not found", ce.getHttpStatus(), ErrorData.NOT_FOUND);
			}
			throw new CipresException("source url error: " + ed.displayMessage, ce.getHttpStatus(), ed.code);
		}
		catch(Exception e)
		{
			log.error("Error getting info from source url", e);
			throw e;
		}
	}



	/*
		GET /file/id/<uuid>
			Returns a DataItem object describing the UserDataItem record.
			Or, with alt=media query param, downloads the data instead.

			TODO: not implemented
	*/
	@Path("{uuid}")
	public Resource getUserData(
		@PathParam("uuid") String  uuid,
		@DefaultValue("") @QueryParam("alt") String alt) 
		throws Exception
	{
		// Can't call initialize here because SecurityFilter is run after this returns
		// and before the handler in the returned class is called.
		//initialize();
		if (alt.equals("media"))
		{
			return Resource.from(DataDownloader.class);
		}
		return Resource.from(DataInfoRetriever.class);
	}

	private DataItem makeDataItem(UserDataItem udi) throws Exception
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresImport.class).path(udi.getUUID()).build();
		LinkData link = new LinkData(uri.toString(), DataItem.name, "dataitem");

		DataItem di = new DataItem();
		di.selfUri = link;
		di.UUID = udi.getUUID();
		di.dateCreated = udi.getCreationDate();
		di.label = udi.getLabel();
		di.length = udi.getDataLength();
		di.username = user.getUsername();
		return di;
	}

}
