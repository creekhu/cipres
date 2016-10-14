package org.ngbw.restdatatypes;

import javax.xml.bind.annotation.XmlRootElement;

/*
	url is an absolute url
	rel is the type of object the url will return.  Should = the XmlRootElement, which we 
		usually also store in a string constant called "name"
	title can be null, if present it is a name for the link, as in: 
	html <a href=url>title</a>
*/

@XmlRootElement(name="link")
public class LinkData 
{
	public static final String name = "link";

	public String url;
	public String rel;	
	public String title;

	public LinkData() {;} 
	public LinkData(String url, String rel, String title)
	{
		this.url = url;
		this.rel = rel;
		this.title = title;
	}

}
