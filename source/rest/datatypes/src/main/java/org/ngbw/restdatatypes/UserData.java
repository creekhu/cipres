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
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserData
{
	public static final String name = "user";

	public LinkData selfUri;
	public String username;
	public String first_name;
	public String last_name;
	public String institution;
	public String street_address;
	public String city;
	public String state;
	public String country;
	public String mailcode;
	public String zip_code;
	public String area_code;
	public String phone_number;
	public String email;
	public String website_url;
	public String comment;
	public String role;
	public Date last_login;
	public Date date_created;

	// START only return these to cipresadmin user
	public String encryptedPassword;
	public boolean active;
	public boolean canSubmit;
	public String xsedeId;
	public String activationCode;
	public Date activationSent;
	// END only return these to cipresadmin user

	public UserData() 
	{
		;
	} 

}
