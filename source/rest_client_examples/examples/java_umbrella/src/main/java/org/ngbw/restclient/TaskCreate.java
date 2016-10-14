package org.ngbw.restclient; 

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.ngbw.restdatatypes.StatusData;


@SuppressWarnings("serial")
public class TaskCreate extends BaseAction 
{
	private static final Log log = LogFactory.getLog(TaskCreate.class.getName());
	private File file;
	private String contentType;
	private String filename;

	private String toolId;
	private String metadata;
	private String toolParameters;
	private String inputFileParameterName;

	public void setPrimaryInput(File file)
	{
		log.debug("In setPrimaryInput");
		this.file = file;
	}

	public File getPrimaryInput() { return this.file; }

	public void setPrimaryInputContentType(String contentType)
	{
		this.contentType = contentType;
	}
	public String getPrimaryInputContentType() { return this.contentType; }

	public void setPrimaryInputFileName(String filename)
	{
		log.debug("In setPrimaryInput filename");
		this.filename = filename;
	}
	public String getPrimaryInputFileName() { return this.filename; }

	public void setToolId(String toolId) 
	{ 
		log.debug("In setToolId");
		this.toolId = toolId; 
	} 

	public String getToolId() { return this.toolId; }

	public void setMetadata(String s) { this.metadata = s; } 
	public String getMetadata() { return this.metadata; }

	public void setToolParameters(String s) { this.toolParameters = s; } 
	public String getToolParameters() { return this.toolParameters; }

	public void setInputFileParameterName(String s) { this.inputFileParameterName = s; } 
	public String getInputFileParameterName() { return this.inputFileParameterName; }


	public void validate() 
	{
		log.debug("In taskCreate.validate()");
		if (file == null)
		{
			addFieldError("primaryInput", "Required field.");
			log.debug("primary input file is missing");

		}
		if (toolId == null || toolId.length() == 0)
		{
			addFieldError("toolId", "Required field.");
			log.debug("tool id is missing.");
		}
	}

	public String execute() throws Exception
	{
		log.debug("TaskCreate.create() called");
		log.debug("File is: " + file.getAbsolutePath());
		log.debug("Tool Id is: " + toolId);
		log.debug("Metadata is: '" + metadata + "'");
		log.debug("Parameters are: '" + toolParameters + "'");
		
		FormDataMultiPart mp = null;
		try
		{
			mp = new FormDataMultiPart();

			mp.field("tool", toolId);
			mp.bodyPart(new FileDataBodyPart("input.infile_", file));


			// turn the metadata string into form parameters.
			String[] tmp1 = metadata.split("[\r\n]+");
			for (String s : tmp1)
			{
				String[] tmp2 = s.split("=");
				String n;
				String v;
				if (tmp2.length > 0)
				{
					n = tmp2[0].trim();
					if (tmp2.length > 1)
					{
						v = tmp2[1].trim();
					} else
					{
						v = "";
					}
					if (n.length() > 0)
					{
						mp.field("metadata." + n, v);
						log.debug("Added field: metadata." + n + "=" + v);
					}
				}
			}	

			// turn the toolParameters string into vparams. 
			tmp1 =toolParameters.split("[\r\n]+");
			for (String s : tmp1)
			{
				String[] tmp2 = s.split("=");
				String n;
				String v;
				if (tmp2.length > 0)
				{
					n = tmp2[0].trim();
					if (tmp2.length > 1)
					{
						v = tmp2[1].trim();
					} else
					{
						v = "";
					}
					if (n.length() > 0)
					{
						if (n.equals("runtime_"))
						{
							log.debug("Discarding user entered runtime of: " + v);
							continue;
						}
						mp.field("vparam." + n, v);
						log.debug("Added field: vparam." + n + "=" + v);
					}
				}
			}	
			// For this demo, overwrite user's setting of vparam.runtime_, if any, so jobs can't run for more than .1 hours.
			mp.field("vparam.runtime_", ".1");

			String url = Application.getInstance().getBaseUrl() + getCipresUser();
			log.debug("Posting to: " + url);

			StatusData statusData = postToUrl(url, mp, StatusData.class);
			if (statusData != null)
			{
				addActionMessage("Submitted new job " + statusData.jobHandle);
			}
		}		
		catch(Exception e)
		{
			log.error("", e);
			addActionError("An exception occurred:" +  e.getMessage());
		}
		finally
		{
			if (mp != null)
			{
				try
				{
					mp.close();
				}
				catch (Exception e  ) {;}
			}	
		}
		return "success";
	}
}

