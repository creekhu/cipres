package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.Date;


/*
*/
@XmlRootElement(name="dataitem")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DataItem
{
	public static final String name = "dataitem";

	@XmlElement public LinkData selfUri;
	@XmlElement public String UUID;
	@XmlElement public Date dateCreated;
	@XmlElement public String label;
	@XmlElement public Long length;
	@XmlElement public String username;

	public void DataItem() {;}

	public void DataItem(LinkData uri)
	{
		this.selfUri= uri;
	}

}
