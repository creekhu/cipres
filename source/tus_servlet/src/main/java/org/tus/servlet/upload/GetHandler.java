package org.tus.servlet.upload;
 

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/*
	This implementation of GET is just a placeholder:  it returns
	a string of the form "user=username\npathname=path", where the username is null,
	if authentication not in use; otherwise the name of the authenicated
	user who did the upload.  pathname is the path of the uploaded file. 

	A derived class could do something useful, like return info about the 
	status of the upload and/or the contents of the uploaded file.

	Sends same headers as HEAD: It's an http requirement that HEAD headers
	are the same as GET headers.
*/
public class GetHandler extends BaseHandler 
{
	private static final Logger log = LoggerFactory.getLogger(HeadHandler.class.getName());

	public GetHandler(Composer composer, HttpServletRequest request, Response response)
	{
		super(composer, request, response);
	}

	@Override
	public void go() throws Exception
	{
		String id = getID();
		if (id == null)
		{
			log.debug("url has no valid id part");
			throw new TusException.NotFound();
		}
		boolean locked = false;
		try
		{
			locked = locker.lockUpload(id);
			if (!locked)
			{
				log.info("Couldn't lock " + id);
				throw new TusException.FileLocked();
			}
			whileLocked(id);
		}
		finally
		{
			if (locked)
			{
				locker.unlockUpload(id);
			}
		}

	}

	private void whileLocked(String id)
		throws Exception
	{
		FileInfo fileInfo = datastore.getFileInfo(id);
		if (fileInfo == null)
		{
			log.debug("id '" + id + "' not found");
			throw new TusException.NotFound();
		}

		if (fileInfo.metadata != null && fileInfo.metadata.length() > 0)
		{
			response.setHeader("Upload-Metadata", fileInfo.metadata);
		}
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Upload-Length", Long.toString(fileInfo.entityLength));
		response.setHeader("Upload-Offset", Long.toString(fileInfo.offset));
		response.setStatus(Response.OK);
		response.setText(datastore.get(id));
	}
}
