package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

import java.util.Date;
import java.util.Map;
import java.util.Collection;

/*
*/
@XmlRootElement(name="tool")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolData
{
	public static final String name = "tool";

	public String toolId;
	public String toolName;
	public LinkData selfUri;
	public LinkData piseUri;
	public LinkData portal2Uri;
	public LinkData exampleUri;
	public LinkData parameterUri;

	public ToolData() 
	{
		;
	} 

}
