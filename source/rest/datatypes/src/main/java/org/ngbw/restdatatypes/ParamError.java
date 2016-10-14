package org.ngbw.restdatatypes;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="paramerror")
public class ParamError 
{
	public static final String name = "paramerror";
	public String param;
	public String error;

	public ParamError() {;} 
	public ParamError(String param, String error)
	{
		this.param = param;
		this.error = error;
	}
}
