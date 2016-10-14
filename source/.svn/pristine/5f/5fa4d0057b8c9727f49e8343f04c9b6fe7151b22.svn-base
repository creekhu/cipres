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
	Either joblinks or jobs will be null.  We use joblinks
	normally, if user asks for expanded data we use jobs instead.
*/
@XmlRootElement(name="joblist")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobList
{
	public static final String name = "joblist";

	public String title;

	// Elements in the list will be called <jobstatus>, which matches StatusData.name 
	@XmlElementWrapper(name = "jobs")
	@XmlElement(name = "jobstatus")
	public Collection<StatusData> jobstatus;


	public JobList() 
	{
		;
	} 

}
