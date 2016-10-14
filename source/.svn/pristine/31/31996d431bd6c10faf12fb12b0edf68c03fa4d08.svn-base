package org.ngbw.restclient; 

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.restdatatypes.ErrorData;



public class RequestHelper 
{
	private static final Log log = LogFactory.getLog( RequestHelper.class.getName());

	public static final String S_EMAIL = "email";
	public static final String S_INSTITUTION = "institution";

	public static <T> T getFromUrl(String url, 
		Class<T> theClass, 
		String username,
		String password,
		Properties headers) throws Exception
	{
		
			Client client = ClientHelper.getClient(username, password);
			WebTarget target = client.target(url);
			Response response;
			Builder builder = target.request("application/xml");
			if (headers != null)
			{
				for (String name : headers.stringPropertyNames())
				{
					builder = builder.header(name, headers.getProperty(name));
				}
			}
			response = builder.get();
			log.debug(response.getStatus());
			if (response.getStatus() != 200)
			{
				ErrorData ed = getErrorData(response);
				if (ed != null)
				{
					log.debug("Parsed an ErrorData response");
					throw new Exception("http status " + response.getStatus() + 
						", code: " + ed.code + ", message:  " + ed.message);
				} 
			} else
			{
				return response.readEntity(theClass);
			}
		
		return null;
	}

	/*
		Actions that make REST service calls can use this when response.getStatus() != 200,
		or whatever status is expected.

		I wrote this to make sure I knew how to get the response as a string and then 
		unmarshal and cast it.  One could do it this way all the time, if unsure of the
		type of object to expect in the response.
	*/
	public static ErrorData getErrorData(Response response)
	{
		String text = "";
		ErrorData ed = null;
		try
		{
			text = response.readEntity(String.class);
			log.debug("Error Response as text:'" +  text + "'.");
			InputStream is = new ByteArrayInputStream(text.getBytes());

			ClassLoader cl = org.ngbw.restdatatypes.ErrorData.class.getClassLoader();

			JAXBContext ctx = JAXBContext.newInstance("org.ngbw.restdatatypes", cl);
			Object obj = ctx.createUnmarshaller().unmarshal(is);
			if (obj instanceof ErrorData)
			{
				ed = (ErrorData)obj;
			} else
			{
				ed = new ErrorData("Unmarshalled unexpected error response object:" + text, 
					null,	ErrorData.GENERIC_COMM_ERROR);
			}
		}
		catch(javax.xml.bind.UnmarshalException ue)
		{
			log.debug("Error unmarshalling response body.  Most likely body is not an expected xml type.", ue);
			ed = new ErrorData("Couldn't unmarshal error from server.  Raw error text is: " + text, 
				null, ErrorData.GENERIC_COMM_ERROR);
		}
		catch (Exception e)
		{
			log.error("", e);
			ed = new ErrorData("Caught exception reading/unmarshalling error response: " + e.toString() +
				". Text received: " + text, null, ErrorData.GENERIC_COMM_ERROR);
		}
		return ed;
	}


}
