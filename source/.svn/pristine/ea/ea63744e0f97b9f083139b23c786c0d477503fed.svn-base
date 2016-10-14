package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.Date;


/*
	Todo: should probably make 2 classes, one for working dir files and one for
	result docs.  In the meantime, though, for 
		- working dir files, documentId is 0, and we use the filename in the uri
		- results, documentId is non-zero and we use it in the uri
*/
@XmlRootElement(name="jobfile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FileData
{
	public static final String name = "jobfile";
	public static final String linkName = "fileDownload";

	@XmlElement public LinkData downloadUri;
	@XmlElement public String jobHandle;
	@XmlElement public String pathname;
	@XmlElement public String filename;
	@XmlElement public Long length;
	@XmlElement public Date dateCreated;
	@XmlElement public Date dateModified;
	@XmlElement public String parameterName;
	@XmlElement public Long outputDocumentId;

	public void FileData() {;}

	public void FileData(LinkData url)
	{
		this.downloadUri = url;
	}

}
