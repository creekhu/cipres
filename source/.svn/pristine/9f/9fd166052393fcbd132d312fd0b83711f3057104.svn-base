package org.ngbw.restclient; 
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

/**
 * Struts action class to handle file download based on current task, and request parameter "inputPath". 
 * 
 */
@SuppressWarnings("serial")
public class Download extends BaseAction 
{
	private static final Logger log = Logger.getLogger(Download.class.getName());
	InputStream is;
	private String fileUrl;

	public String getFileUrl() { return this.fileUrl; }
	public void setFileUrl(String url) { this.fileUrl = url; }

	public InputStream getInputStream() 
	{
		return is;
	}

	@SkipValidation
	private InputStream getTheData(String inputPath)  throws Exception
	{
		log.debug("In getTheData with url: " + this.fileUrl);
		InputStream stream = getStreamFromUrl(this.fileUrl);
		return stream;
	}

	public long getDataLength() 
	{
		log.debug("In getDataLength");
		return contentLength; // from BaseAction, available after getStreamFromUrl has been called.
	}

	//method for downloading file
	public String execute() 
	{
		try
		{
			is = getTheData(this.fileUrl);
			if (is == null)
			{
				throw new Exception("There was an error opening a stream from the specified url:'" +
					fileUrl + "'");
			}
		}
		catch (Exception t)
		{
			// An error is expected here if the user tries to download the file as it's being deleted or after it's
			// been deleted.   
			log.debug(t);
			is = new ByteArrayInputStream(new byte[0]);
		}
		return SUCCESS;
	}

	public String getContentDisposition()
	{
		//String retval = "filename=\"" + inputPath + "\"";

		if (contentDisposition == null)
		{
			return "filename = \"unknown\"";
		} 
		return contentDisposition;
	}

	public String getContentType()
	{
		if (contentType == null)
		{
			return "application/x-unknown";
		}
		return contentType;
	}
}
