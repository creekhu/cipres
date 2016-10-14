package org.ngbw.restclient; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@SuppressWarnings("serial")
public class Login extends BaseAction 
{
	private static final Log log = LogFactory.getLog(Login.class.getName());
	private String username;
	private String password;
	private String email;
	private String institution;
	private String country;

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getInstitution() { return institution; }
	public void setInstitution(String institution) { this.institution = institution; }

	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }

	public String login() 
	{
		if (validateFields())
		{
			// this is where we'd validate username and password combo.
			log.debug("TERRI In login, username = " + username);
			session.put(S_USER, getUsername());
			session.put(S_PASSWORD, getPassword());
			session.put(S_EMAIL, getEmail());
			session.put(S_INSTITUTION, getInstitution());
			session.put(S_COUNTRY, getCountry());
			log.debug("login returning success");
			return SUCCESS;
	 	}
		log.debug("login returning input");
		return INPUT;
	 }

	 public String logout()
	 {
	 	log.debug("In logout");
		session.invalidate();
		return "success";
	 }

	/*
	*/
	private boolean validateFields()
	{
		boolean errors = false;
		log.debug("In validate");
		if (getUsername() == null || getUsername().length() == 0)
		{
			errors = true;
		}
		if (getPassword() == null || getPassword().length() == 0)
		{
			errors = true;
		}
		if (getEmail() == null || getEmail().length() == 0)
		{
			errors = true;
		}
		if (getInstitution() == null || getInstitution().length() == 0)
		{
			errors = true;
		}
		if (getCountry() == null || getCountry().length() == 0)
		{
			errors = true;
		}
		return !errors;
	}
}
	

