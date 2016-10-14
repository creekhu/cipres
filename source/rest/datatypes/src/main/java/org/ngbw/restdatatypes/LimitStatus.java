package org.ngbw.restdatatypes;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="limitStatus")
public class LimitStatus 
{
	public static final String name = "limitStatus";
	public String type;
	public Long ceiling;
	public Long remaining;
	public Long resetMinutes;
	public Long resetDate;

	public LimitStatus() {;} 
	public LimitStatus(String type, Long ceiling)
	{
		this.type = type;
		this.ceiling = ceiling;
	}
}
