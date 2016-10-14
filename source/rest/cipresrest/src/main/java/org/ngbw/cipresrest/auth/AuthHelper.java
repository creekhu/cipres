/*
*/
package  org.ngbw.cipresrest.auth;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.clients.UmbrellaUser;
import org.ngbw.sdk.common.util.CountryCode;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.ConstraintException;
import org.ngbw.sdk.database.User;


public class AuthHelper 
{
	private static final Log log = LogFactory.getLog(AuthHelper.class.getName());
	public static final String USER =			"cipresuser";
	public static final String APPLICATION =	"cipresapp";
    public static final String REALM =			"Cipres Authentication";
	public static final int MAX_HEADER_LEN =	200;
	public static final String H_APP =			"cipres-appkey";
	public static final String H_EU =			"cipres-eu";
	public static final String H_EMAIL =		"cipres-eu-email";
	public static final String H_INST =			"cipres-eu-institution";
	public static final String H_COUNTRY =		"cipres-eu-country";

	/*********************************************************************************************
		START: BASIC AUTHENTICATION SUPPORT
	*********************************************************************************************/
	// Work with request headers
	public static void dumpHeaders(HttpServletRequest request)
	{
		Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) 
		{
			log.debug("Request Headers:");
			while (headerNames.hasMoreElements()) 
			{
				String name = headerNames.nextElement();
				log.debug(name + ":" + request.getHeader(name));
			}
        }
	}

	// If header appears multiple times, this returns first.  This is different
	// from Jerseys getHeaderString() which concats multiplue values with a comma.
	public static String getHeaderString(HttpServletRequest request, String name)
	{
		String value = request.getHeader(name);
		return normalizeHeader(value);
	}

	/*
		Return null if header is missing, all whitespace or empty.
		Throw AuthException if header is too long.
		Return value otherwise.
	*/
	public static String normalizeHeader(String value)
	{
		if (value != null)
        {   
            value = value.trim();
            if (value.length() == 0)
            {
                value = null;
            }
        }
        if (value != null && value.length() > MAX_HEADER_LEN)
        {
            throw new AuthenticationException("Header too long. Maximum length is " + MAX_HEADER_LEN, 
            	REALM);
        }
        return value;
	}


	/*
		Ensures that request uses basic auth and that supplied username and password
		represents a valid user in the cipres database.
	*/
    public  static SecurityUser authenticate(String authHeaderValue) 
	{
        String authentication = authHeaderValue;
        if (authentication == null) 
		{
			throw new AuthenticationException("Authentication credentials are required.\n", REALM);
        }
        if (!authentication.startsWith("Basic ")) 
		{
			throw new AuthenticationException("Only HTTP Basic authentication is supported.\n", REALM);
        }

		// requires java 8
        authentication = authentication.substring("Basic ".length());
		String credentials = new String(Base64.getDecoder().decode(authentication), Charset.forName("UTF-8"));
		final String values[] = credentials.split(":", 2);

        if (values.length < 2) 
		{
			throw new AuthenticationException("Invalid syntax for username and password.\n", REALM);
        }
        String username = values[0];
        String password = values[1];

        if ((username == null) || (password == null)) 
		{
			throw new AuthenticationException("Missing username or password.\n", REALM);
        }

        // Validate the extracted credentials
        SecurityUser su = null;
		User user = null;
		if ((user = validateCipresUser(username, password)) != null)
		{
            su = new SecurityUser(user, user.getRole());
            log.debug("Logged in user:'" + username + "', role: '" + user.getRole().toString() + "'");
		} else 
		{
			log.debug("Received invalid credentials: " + username + ", " + password);
            throw new AuthenticationException("Invalid username or password\n", REALM);
        }
		return su;
    }


	public static User validateCipresUser(String username, String password) 
	{
		User user = null;
		try
		{
			user = User.findUserByUsername(username);
			if (user == null )
			{
				log.debug("Username '" + username + "' not found.");
				return null;
			}
			if (!user.getUsername().equals(username))
			{
				log.debug("Found user but Username '" + username + "' doesn't match '" + user.getUsername() + "'");
			}
			String hash = StringUtils.getMD5HexString(password);
			if (!user.getPassword().equals(hash))
			{
				log.debug("Invalid password received for username '" + username + "'.");
				return null;
			}
			return user;
		}
		catch(Exception e)
		{
			log.error("validateCipresUser caught unexpected exception:", e);
			throw new RuntimeException("Internal Error: " + e.toString());
		}
	}


	public static class SecurityUser 
	{
		public User user;
		public UserRole role;

		public SecurityUser(User user, UserRole role) 
		{
			this.user = user;
			this.role = role;
		}
	}
	/*********************************************************************************************
		END: BASIC AUTHENTICATION SUPPORT
	*********************************************************************************************/

	/*********************************************************************************************
		START: PROCESS CIPRES SPECIFIC AUTHENTICATION HEADERS 
	*********************************************************************************************/
	/*
		Called for resources that require cipres-appkey header. Makes sure application exists and is active.
		If it's an umbrella app, makes sure the logged in user (SecurityUser) is the admin of the
		umbrella app.
	*/
	public static Application validateApplication( SecurityUser su, String appkey)
	{
		log.debug("validateApplication");
		String appname;

		Application application = getApplicationRecord(appkey);
		appname = application.getName();

		if (!application.isActive())
		{
			throw new AuthenticationException("Application '" + application.getName() + ", " 
				+ application.getLongName() + "' is not activated for use with cipres.\n", AuthHelper.REALM);
		}
		if (application.getAuthType().equals(Application.DIRECT))
		{
			if (!su.role.equals(UserRole.REST_USER))
			{
				throw new AuthenticationException("User role is not REST USER.\n", AuthHelper.REALM);
			}
		} else if (application.getAuthType().equals( Application.UMBRELLA))
		{
			// Make sure account used to login is the one authorized for this app, in other 
			// words the rest administrator for this app.
			if (application.getAuthUserId() != su.user.getUserId())
			{
				throw new AuthenticationException("Wrong basic auth credentials for specified application.\n", 
					AuthHelper.REALM);
			}
		} else // unexpected application.authType
		{
			throw new AuthenticationException("The application is incorrectly registered with cipres:" + 
				" unsupported auth type.\n", AuthHelper.REALM);
		}
		return application;
	}

	private static Application getApplicationRecord(String appkey) 
	{
		if (appkey == null) 
		{
			throw new AuthenticationException("Custom cipres request headers are required.\n", 
				AuthHelper.REALM);
		}
		log.debug("cipres-appkey is '" + appkey + "'");

		Application application = null;
		try
		{
			application = Application.findKey(appkey);
		}
		catch(Exception e)
		{
			log.error("", e);
			throw new RuntimeException("Internal Error:" + e.toString());
		}
		if (application == null)
		{
			throw new AuthenticationException("Application key not found.\n", AuthHelper.REALM);
		}
		return application;
	}

	/*
		- su is the user corresponding to the basic auth headers

		- Application was retrieved from the cipres-appkey header

		- Remaining strings are cipres headers that identify the end user of an umbrella application.
			Direct apps don't send these so they may be null.
		
		If application is umbrella: modifies the passed in SecurityUser object to contain the end user 
		id'd by the cipres headers (if app is direct, doesn't modify the SecurityUser).  Returns the
		possibly modified SecurityUser.
	*/
	public static AuthHelper.SecurityUser getEndUser(
									SecurityUser su, 
									Application application,
									String eu,
									String euEmail,
									String euInstitution,
									String euCountry)
	{
		if (application.getAuthType().equals(Application.DIRECT))
		{
			return su;
		}
		log.debug("getEndUser");
		if (eu == null || euEmail == null )
		{
			throw new AuthenticationException("Missing end user request header(s).\n", AuthHelper.REALM);
		}
		if (euCountry != null)
		{
			if (!CountryCode.isValid(euCountry))
			{
				throw new AuthenticationException("Invalid country code.\n", AuthHelper.REALM);
			}
		}
		// TODO: check length of headers.  

		/*
			We lookup the end user and if not found, create an account for him.  We return a new SecurityUser 
			that represents the end user.
		*/
		try
		{
			String appname = application.getName();
			log.debug("Looking for user:'" + appname + "', '" + eu + "'");
			User user = UmbrellaUser.find(appname, eu);
			if (user == null)
			{
				user = UmbrellaUser.create(appname, eu, euEmail, euInstitution, euCountry);
				log.debug("Created account for user: '" + user.getUsername() + "'");
			} else
			{
				user = UmbrellaUser.update(user, euEmail, euInstitution, euCountry);
			}
			// Substitute end user, don't care about basic auth user anymore.
			su.user = user;
			return su;
		}
		catch(ConstraintException ce)
		{
			throw new AuthenticationException(ce.toString() + "\n", AuthHelper.REALM);
		}
		catch(Exception e)
		{
			log.error("", e);
			throw new RuntimeException("Internal Error:" + e.toString());
		}
	}



}
