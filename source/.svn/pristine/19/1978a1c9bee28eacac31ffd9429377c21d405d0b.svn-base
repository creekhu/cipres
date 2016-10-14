package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

import java.util.Date;
import java.util.Map;
import java.util.Collection;

/*
*/
@XmlRootElement(name="workingdir")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkingDirData
{
	public static final String name = "workingdir";

	@XmlElementWrapper(name = "jobfiles")
	@XmlElement(name = "jobfile")
	public Collection<FileData> jobfiles;


	public WorkingDirData() 
	{
		;
	} 

}
