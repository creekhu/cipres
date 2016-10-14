/*
 * JobMetadata.java
 */
package org.ngbw.sdk.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.WorkbenchSession;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;



/**
 * @author Terri Liebowitz Schwartz
 *
 */
/*
	Todo: check field lenghts, depending on where we store this data in the database.
*/
public class JobMetadata
{
	private static final Log log = LogFactory.getLog(Job.class.getName());
	private static final int MAXLEN = 100;
	String clientJobId;
	String clientJobName;
	String clientToolName;
	boolean statusEmail;
	String emailAddress;
	String statusUrlPut;
	String statusUrlGet;
	
	public JobMetadata()
	{
	}

	public JobMetadata(
				String clientJobId,
				String clientJobName,
				String clientToolName,
				boolean statusEmail,
				String emailAddress,
				String statusUrlPut,
				String statusUrlGet)

	{
		setClientJobId(clientJobId);
		setClientJobName(clientJobName);
		setClientToolName(clientToolName);
		setStatusEmail(statusEmail);
		setEmailAddress(emailAddress);
		setStatusUrlPut(statusUrlPut);
		setStatusUrlGet(statusUrlGet);
	}

	public static JobMetadata fromMap(Map<String, String> properties) throws Exception
	{
		JobMetadata m = new JobMetadata();
		if (properties == null)
		{
			return null;
		}
		for (String key : properties.keySet())
		{
			String value = properties.get(key);
			//log.debug("name=" + key + ", value=" + value);
			if (value == null)
			{
				continue;
			}
			key = key.trim();
			if (key.length() == 0)
			{
				//log.debug("Metadata value is empty string for " + key + ", discarding");
				continue;
			}
			if (key.length() > MAXLEN)
			{
				// TODO: should throw an error - see where this method is called and how it would be handled. 
				log.debug("Metadata value too long for field " + key);
				continue;
			}
			if (key.equals("clientJobId"))
			{
				m.setClientJobId(properties.get(key));
			}
			if (key.equals("clientJobName"))
			{
				m.setClientJobName(properties.get(key));
			}
			if (key.equals("clientToolName"))
			{
				m.setClientToolName(properties.get(key));
			}
			if (key.equals("statusEmail"))
			{
				m.setStatusEmail(Boolean.valueOf(properties.get(key)));
			}
			if (key.equals("emailAddress"))
			{
				m.setEmailAddress((properties.get(key)));
			}
			if (key.equals("statusUrlPut"))
			{
				m.setStatusUrlPut(properties.get(key));
			}
			if (key.equals("statusUrlGet"))
			{
				m.setStatusUrlGet(properties.get(key));
			}
		}
		if (m.isEmpty())
		{
			return null;
		}
		return m;
	}

	public Map<String, String> toMap() throws Exception
	{
		HashMap<String, String>p = new HashMap<String, String>();
		if (clientJobId != null)
		{
			p.put("clientJobId", clientJobId);
		}
		if (clientJobName != null)
		{
			p.put("clientJobName", clientJobName);
		}
		if (clientToolName != null)
		{
			p.put("clientToolName", clientToolName);
		}
		if (statusEmail == true)
		{
			p.put("statusEmail", Boolean.toString(statusEmail));
		}
		if (emailAddress != null)
		{
			p.put("emailAddress", emailAddress);
		}
		if (statusUrlPut != null)
		{
			p.put("statusUrlPut", statusUrlPut);
		}
		if (statusUrlGet != null)
		{
			p.put("statusUrlGet", statusUrlGet);
		}
		if (p.size() > 0)
		{
			return p;
		}
		return null;
	}

	public boolean isEmpty()
	{
		return	clientJobId == null && clientJobName == null && clientToolName == null &&
				statusEmail == false  && statusUrlPut == null && statusUrlGet == null;
	}

	private String checkLen(String s)
	{
		if ((s != null) && s.length() > MAXLEN)
		{
			return s.substring(0, MAXLEN);
		} 
		return s;
	}


	public String getClientJobId() { return clientJobId; }
	public String getClientJobName() { return clientJobName; }
	public String getClientToolName() { return clientToolName; }
	public boolean getStatusEmail() { return statusEmail; }
	public String getEmailAddress() { return emailAddress; }
	public String getStatusUrlPut() { return statusUrlPut; }
	public String getStatusUrlGet() { return statusUrlGet; }

	public void setClientJobId(String clientJobId) { this.clientJobId = clientJobId; }
	public void setClientJobName(String clientJobName) { this.clientJobName = clientJobName; }
	public void setClientToolName(String clientToolName) { this.clientToolName = clientToolName; }
	public void setStatusEmail(boolean statusEmail) { this.statusEmail = statusEmail; }
	public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
	public void setStatusUrlPut(String statusUrlPut) { this.statusUrlPut = statusUrlPut; }
	public void setStatusUrlGet(String statusUrlGet) { this.statusUrlGet = statusUrlGet; }

}
