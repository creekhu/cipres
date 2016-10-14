/*
 * JobFile.java
 */
package org.ngbw.sdk.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
 



/**
 * @author Terri Liebowitz Schwartz
 *
 */
/*
	This class is the info that's returned to a caller about an input or output
	file related to the job.  The class is used both to return info about the
	files in a job's working directory, while the job is running, or the result
	files produced by the job and stored in the db, once the job has finished.

	Rest api will add the url that can be used to retrieve the contents of the file.
*/
/*
@XmlRootElement(name="details")
@XmlAccessorType(XmlAccessType.PROPERTY)
*/
public class JobFile 
{
	private static final Log log = LogFactory.getLog(Job.class.getName());

	// not sure if there's any reason for both pathname and filename. 
	private String jobHandle;
	private String pathname; 
	private String filename;
	private long length;
	private Date dateCreated;
	private Date dateModified;

	// only used for result files:
	private String parameterName;

	// This is the primary key of the TaskOutputSourceDocument table.  You can 
	// create a TaskOutputSourceDocument from it.
	private long outputDocumentId;

	public JobFile() {;}


	public String getJobHandle() { return jobHandle; }
	public String getPathname() { return pathname; }
	public String getFilename() { return filename; }
	public long getLength() { return length; }
	public Date getDateCreated() { return dateCreated; }
	public Date getDateModified() { return dateModified; }
	public String getParameterName() { return parameterName; }
	public long getOutputDocumentId() { return outputDocumentId; }

	public void setJobHandle(String i) { this.jobHandle = i; }
	public void setPathname(String pathname) { this.pathname = pathname; }
	public void setFilename(String filename) { this.filename = filename; }
	public void setLength(long length) { this.length = length; }
	public void setDateCreated(Date date) { this.dateCreated = date; }
	public void setDateModified(Date date) { this.dateModified = date; }
	public void setParameterName(String parameterName) { this.parameterName = parameterName; }
	public void setOutputDocumentId(long id) { this.outputDocumentId = id; }

}	
