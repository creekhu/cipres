package org.ngbw.cipresrest.webresource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.ngbw.sdk.database.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataDownloader
{
	private static final Log log = LogFactory.getLog(DataDownloader.class.getName());
	private User user;
	private String uuid;


	@GET
	public Response download()
	{
		log.debug("In download");
		InputStream is = null;
		String dummy = "Hi this is dummy data just for a test.";
		String dummyMimeType = "text/plain";
		try
		{
			is = new ByteArrayInputStream(dummy.getBytes());

			return Response.ok(is).
				header("Content-Disposition", "attachment; filename=" + "dummy.txt").
				type(dummyMimeType).
				build();
		}
		catch(Exception e)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw e;
		}
	}
}
