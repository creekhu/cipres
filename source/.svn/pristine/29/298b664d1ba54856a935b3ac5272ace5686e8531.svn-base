package org.ngbw.restclient; 

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.SessionAware;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.ParamError;

import com.opensymphony.xwork2.ActionSupport;



@SuppressWarnings("serial")
public class BaseAction extends ActionSupport implements SessionAware, ParameterAware
{
	private static final Log log = LogFactory.getLog(BaseAction.class.getName());

	// Session attribute keys:
	public static final String S_USER = "user";
	public static final String S_PASSWORD = "password";
	public static final String S_EMAIL = "email";
	public static final String S_INSTITUTION = "institution";
	public static final String S_COUNTRY = "country";

	protected static Application app = Application.getInstance();

	protected SessionMap<String, Object> session = null;
	protected Map<String, String[]> parameters = null;

	public void setSession(Map<String, Object> session)
	{
		this.session = (SessionMap<String, Object>)session;
	}

	public void setParameters(Map<String, String[]> p)
	{
		this.parameters = p;
	}

	public String getSessionUser()
	{
		log.debug("getSessionUser returns: " + (String)session.get(S_USER));
		return (String)session.get(S_USER);
	}

	public String getCipresUser()
	{
		return app.getAppname() + "." + (String)session.get(S_USER);
	}

	public String getSessionPassword()
	{
		return (String)session.get(S_PASSWORD);
	}

	public String getSessionEmail()
	{
		return (String)session.get(S_EMAIL);
	}

	public String getSessionInstitution()
	{
		return (String)session.get(S_INSTITUTION);
	}

	public String getSessionCountry()
	{
		return (String)session.get(S_COUNTRY);
	}

	public boolean deleteUrl(String url)
	{
		try
		{
			Client client = ClientHelper.getClient(app.getUmbrellaUsername(), app.getUmbrellaPassword());

			WebTarget target = client.target(url);
			Response response = target.request().
				header("cipres-appkey", app.getAppKey()).
				header("cipres-eu", getSessionUser()).
				header("cipres-eu-email", getSessionEmail()).
				header("cipres-eu-institution", getSessionInstitution()).
				header("cipres-eu-country", getSessionCountry()).
				delete();

			log.debug(response.getStatus());
			if (response.getStatus() != 200 && response.getStatus() != 204)
			{
				ErrorData ed = RequestHelper.getErrorData(response);
				if (ed != null)
				{
					addActionError("http status " + response.getStatus() + 
						", code: " + ed.code + ", message:  " + ed.message);
				}  else
				{
					addActionError("http status " + response.getStatus() + ". No message body");
				}
				log.debug("Added action error: " + ed.message);
				return false;
			} 
		}
		catch(Exception e)
		{
			log.error("", e);
			addActionError("An exception occurred:" +  e.getMessage());
			return false;
		}
		return true;
	}


	public <T> T getFromUrl(String url, GenericType<T> gt)
	{
		log.debug("getFromUrl " + url);
		try
		{
			Client client = ClientHelper.getClient(app.getUmbrellaUsername(), app.getUmbrellaPassword());

			WebTarget target = client.target(url);
			Response response = target.request("application/xml").
				header("cipres-appkey", app.getAppKey()).
				header("cipres-eu", getSessionUser()).
				header("cipres-eu-email", getSessionEmail()).
				header("cipres-eu-institution", getSessionInstitution()).
				header("cipres-eu-country", getSessionCountry()).
				get();

			log.debug(response.getStatus());
			if (response.getStatus() != 200)
			{
				ErrorData ed = RequestHelper.getErrorData(response);
				if (ed != null)
				{
					addActionError("http status " + response.getStatus() + 
						", code: " + ed.code + ", message:  " + ed.message);
					log.debug("Added action error: " + ed.message);
				} 
			} else
			{
				return response.readEntity(gt);
			}
		}
		catch(Exception e)
		{
			log.error("", e);
			addActionError("An exception occurred:" +  e.getMessage());
		}
		return null;
	}

	public <T> T getFromUrl(String url, Class<T> theClass) 
	{
		log.debug("getFromUrl " + url);
		Properties headers = new Properties();
		headers.put("cipres-appkey", app.getAppKey());
		headers.put("cipres-eu", getSessionUser());
		headers.put("cipres-eu-email", getSessionEmail());
		headers.put("cipres-eu-institution", getSessionInstitution());
		headers.put("cipres-eu-country", getSessionCountry());
		try
		{
			return RequestHelper.getFromUrl(url, theClass, 
				app.getUmbrellaUsername(), app.getUmbrellaPassword(), headers);
		}
		catch (Exception e)
		{
			addActionError(e.getMessage());
			log.error("", e);
		}
		return null;
	}


	public <T> T postToUrl(String url, FormDataMultiPart mp, Class<T> theClass)
	{
		try
		{
			Client client = ClientHelper.getClient(app.getUmbrellaUsername(), app.getUmbrellaPassword());
			WebTarget target = client.target(url);
			Response response = target.request().
				header("cipres-appkey", app.getAppKey()).
				header("cipres-eu", getSessionUser()).
				header("cipres-eu-email", getSessionEmail()).
				header("cipres-eu-institution", getSessionInstitution()).
				header("cipres-eu-country", getSessionCountry()).
				post(Entity.entity(mp, mp.getMediaType()));

			log.debug(response.getStatus());
			if (response.getStatus() != 200)
			{
				ErrorData ed = RequestHelper.getErrorData(response);
				if (ed != null)
				{
					addActionError("http status " + response.getStatus() + 
						", code: " + ed.code + ", message:  " + ed.message);
					log.debug("Added action error: " + ed.message);
					if (ed.paramError != null)
					{
						for (ParamError pe : ed.paramError)
						{
							addActionError("\n" + pe.param+ ": " + pe.error);
						}
					}
				} 
			} else
			{
				return response.readEntity(theClass);
			}
		}
		catch(Exception e)
		{
			log.error("", e);
			addActionError("An exception occurred:" +  e.getMessage());
		}
		return null;
	}

	int contentLength = 0;
	String contentDisposition = null;
	String contentType = null;

	public InputStream getStreamFromUrl(String url) 
	{
		try
		{
			Client client = ClientHelper.getClient(app.getUmbrellaUsername(), app.getUmbrellaPassword());
			WebTarget target = client.target(url);
			Response response = target.request("application/xml").
				header("cipres-appkey", app.getAppKey()).
				header("cipres-eu", getSessionUser()).
				header("cipres-eu-email", getSessionEmail()).
				header("cipres-eu-institution", getSessionInstitution()).
				header("cipres-eu-country", getSessionCountry()).
				get();


			log.debug(response.getStatus());
			if (response.getStatus() != 200)
			{
				ErrorData ed = RequestHelper.getErrorData(response);
				if (ed != null)
				{
					addActionError("http status " + response.getStatus() + 
						", code: " + ed.code + ", message:  " + ed.message);
					log.debug("Added action error: " + ed.message);
				} 
			} else
			{
				this.contentLength = response.getLength();
				log.debug("Got contentLength = " + this.contentLength);

				this.contentType = response.getMediaType().toString();
				log.debug("Got media type= " + this.contentType);

				this.contentDisposition = response.getHeaderString("content-disposition");
				log.debug("Got content disposition= " + this.contentDisposition); 

				logHeaders(response);


				// Note we call getEntity instead of readEntity to return the InputStream.
				return (InputStream)response.getEntity();
			}
		}
		catch(Exception e)
		{
			log.error("", e);
			addActionError("An exception occurred:" +  e.getMessage());
		}
		return null;
	}

	private void logHeaders(Response response)
	{
		for (String key : response.getHeaders().keySet())
		{
			log.debug(key + ":" + response.getHeaderString(key));
		}

	}


}
