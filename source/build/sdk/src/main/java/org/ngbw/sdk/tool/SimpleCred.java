/**
 *  Copyright (c) 2007-2009 CyberInfrastructure and Geospatial
 *  Information Laboratory (CIGI), University of Illinois at
 *  Urbana-Champaign, All Rights Reserved.
 */
 /**
 	Terri's changes:
	1) I'm treating this object as a singleton, created and accessed via my GlobusCred wrapper class.
	There's one instance in the web application and one in each process worker.   I simplified the
	interface a little so that I could make most of the instance vars final.  Only myproxy and proxy
	are modified on the fly.  I made all methods that access myproxy or proxy synchronized.

	2) Since multiple processes use this and may update the proxy file on disk, I added file locking.

	3) Log, don't print to stdout.
 */
package org.ngbw.sdk.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.myproxy.GetParams;
import org.globus.myproxy.MyProxyException;
import org.globus.util.Util;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ngbw.sdk.common.util.WholeFileLock;

/**
 * A simple grid proxy credential manager that wraps Java COG and MyProxy API 
 * and provide an easy-to-use interface for grid portal developers. Once initialized,
 * user just needs to call the get() method which fetches a proxy from either a local
 * file or MyProxy server, maintains the proxy, and automatically renew it when the
 * current proxy is expired.
 * 
 * @author liuyan
 *
 */
public class SimpleCred 
{
	private static final Log log = LogFactory.getLog(SimpleCred.class.getName());
	//public static final int DEFAULT_LIFETIME = 7 * 24 * 3600;
	//public static final int DEFAULT_MIN = 6  * 24 * 3600;
	final String proxy_file; // this is a valid proxy file that exists already
	final String host;
	final int port;
	final int requested_lifetime;
	final int min_lifetime;
	final String username;
	final String password;

	GSSCredential proxy;
	org.globus.myproxy.MyProxy myproxy;

	boolean logon_save = true; // save as proxy file after myproxy logon

	/**
	 * SimpleCred constructor
	 * @param server myproxy server address
	 * @param p myproxy server port
	 * @param user myproxy user name
	 * @param passwd myproxy user password
	 * @param fname local proxy file name
	 * @throws Exception
	 */
	/*
	public SimpleCred(String server, int p, String user, String passwd, String fname) 
	{
		host = server;
		port = p;
		username = user;
		password = passwd;
		proxy_file = fname;
		requested_lifetime = SimpleCred.DEFAULT_LIFETIME;
		min_lifetime = SimpleCred.DEFAULT_MIN;
	}
	*/

	public SimpleCred(String server, int p, String user, String passwd, String fname, int lifetime, 
		int min) 
	{
		host = server;
		port = p;
		username = user;
		password = passwd;
		proxy_file = fname;

		// convert hours to seconds
		requested_lifetime = lifetime * 3600;
		min_lifetime = min * 3600;
	}

	/**
	 * Load proxy from a valid proxy file. It is implemented by calling Java COG API
	 * @param certFile local proxy file
	 */
	synchronized public void load(String certFile) 
	{
		releaseProxy();
		try
		{
			
			log.debug("Loading credential from " + certFile);
			WholeFileLock fl = null;
			File proxyFile = new File(certFile);
			byte [] credData = null; 
			if (proxyFile.exists()) 
			{
				try
				{
					// Get a read lock
					fl = new WholeFileLock(certFile, !WholeFileLock.WRITABLE);

					credData = new byte[(int)proxyFile.length()];
					FileInputStream in = fl.getInputStream(); 
					in.read(credData);
				}
				finally
				{
					if (fl != null) { fl.releaseLock();}
				}

				// create credential by loading from proxy file
				ExtendedGSSManager manager = (ExtendedGSSManager)ExtendedGSSManager.getInstance();
				proxy = manager.createCredential(credData,
		  		                         ExtendedGSSCredential.IMPEXP_OPAQUE,
		                                 GSSCredential.DEFAULT_LIFETIME,
		                                 null, // use default mechanism - GSI
		                                 GSSCredential.INITIATE_AND_ACCEPT);
			}
		}
		catch (Exception e)
		{
			log.error("", e);
		}
        if (!this.isValid())
		{
			releaseProxy();
		}
        log.debug("Loaded credential: " + this.info());
	}
	/**
	 * Fetch a proxy from MyProxy server by calling MyProxy API
	 * @param user user name on myproxy server
	 * @param passwd password
	 * @param ltseconds lifetime to request in seconds
	 * @throws GSSException
	 */
	synchronized public void logon() throws GSSException 
	{
		GetParams getRequest = new GetParams();
		getRequest.setUserName(username);
		getRequest.setPassphrase(password);
		getRequest.setCredentialName(null); // anonymous logon
		getRequest.setLifetime(requested_lifetime);
		    
		try 
		{
			releaseProxy();
			if (myproxy == null) 
			{
				myproxy = new org.globus.myproxy.MyProxy(this.host, this.port);
			}
			log.debug("Contacting myproxy server ...");
		    proxy = myproxy.get(null, getRequest);
		    if (!this.isValid()) 
			{
				releaseProxy();
			}
			log.debug("Myproxy sent credential: " + this.info());
		} catch(MyProxyException e) 
		{
		    proxy = null;
			log.error("", e);
		}
	}

	/**
	 * Save proxy into a proxy file
	 * @param proxy_location the proxy file to be written
	 * @return success or failure
	 */
	synchronized public boolean export(String proxy_location) 
	{
		try 
		{
			if (proxy_location != null) 
			{
				log.debug("Storing new credential in " + proxy_location);

			    // create a file
			    File f = new File(proxy_location);
			    if (!f.exists() || f.canWrite()) 
				{
					WholeFileLock fl = null;
					try
					{
						// Get a write lock
						fl = new WholeFileLock(proxy_location, WholeFileLock.WRITABLE);

						// set read only permissions
						Util.setOwnerAccessOnly(proxy_location);

				    	OutputStream out = fl.getOutputStream(); 
						// write the contents
						byte [] data = ((ExtendedGSSCredential)proxy).export(	
							ExtendedGSSCredential.IMPEXP_OPAQUE);
						out.write(data);
					}
					finally
					{
						if (fl != null) { fl.releaseLock(); }
					}
					log.debug("Stored credential OK.");
			    }
			}
			return true;
		}
		catch (Exception e)
		{
			log.error("", e);
			return false;
		}
	}

	/*
	public GSSCredential get(String user, String email, String ip) throws Exception
	{
		GSSCredential p = this.get();

		//When I run with -ea assertions enabled this raises an IndexOutOfBounds
		//assertion.   There's no reason we really need saml assertions right now and
		//I've never been able to test them.  I'd rather have the job running code
		//run gateway-submit-attributes if we need to.  See: 
		//http://gw55.quarry.iu.teragrid.org/mediawiki/index.php?title=Gateway-Submit-Attributes
		return SamlCred.getSamlProxy(p, requested_lifetime, user, email, ip);
		return p;
	}
	*/


	/**
	 * default get() method to fetch a valid proxy
	 * @return a valid grid proxy or null
	 */
	public GSSCredential get()  throws Exception
	{
		return this.get(false);
	}

	/**
	 * Get a valid proxy.
	 * If there is a valid proxy, return it;
	 * If a local proxy file contains a valid proxy, load and return it;
	 * Otherwise, logon to myproxy server and fetch a valid proxy.
	 * User does not need to call <code>logon()</code> or <code>load()</code>
	 * explicitly. <code>get()</code> is sufficient for general use
	 * @param forceRetrieve destroy current proxy and get again
	 * @return a valid grid proxy or null
	 */
	synchronized public GSSCredential get(boolean forceRetrieve)  throws Exception
	{
		if (forceRetrieve && proxy != null) 
		{
			try 
			{
				proxy.dispose();
			} catch (Exception e) 
			{
				log.error("", e);
			}
			proxy = null;
		}
		if (this.isValid())
		{
			return proxy;
		}
		this.load(proxy_file);
		if (proxy != null)
		{
			return proxy;
		}
		this.logon();
		if (proxy != null) 
		{
			if (logon_save) 
			{
				this.export(proxy_file);
			}
		}
		return proxy;
	}

	/**
	 * Print grid proxy information
	 * @return proxy information as a String
	 */
	public synchronized String info() 
	{
		if (!this.isValid())
			return "";
		String s = "";
		s += "Subject: " + this.getDN() + "\n";
		int t = this.getRemainingTime();
		s += "Remaining lifetime: " + (t / 3600) + ":" + ((t % 3600) / 60) + ":" + (t % 60) + " ("+ t + " seconds)\n";
		s += "MyProxy server: " + this.host + ":" + this.port + "\n";
		s += "MyProxy user: " +this.username +"\n";
		
		return s;
	}
	/**
	 * Get subject (DN) information of a proxy
	 * @return Subject of the proxy as a String
	 */
	public synchronized String getDN() 
	{
		try 
		{
			return (this.isValid()?(proxy.getName().toString()):"");
		} catch (GSSException e) {
			return "";
		}
	}
	/**
	 * Test if current proxy is valid
	 * @return true or false
	 */
	public synchronized boolean isValid() 
	{
		if (proxy == null) return false;
		try 
		{
			// the min requirement for a globus job submission
			return proxy.getRemainingLifetime() > min_lifetime; 
		} catch (Exception e) 
		{
			return false;
		}
	}
	/**
	 * Get remaining time in seconds
	 * @return the remaining time of a proxy in seconds
	 */
	public int getRemainingTime() 
	{
		if (proxy == null) return 0;
		try 
		{
			return proxy.getRemainingLifetime();
		} catch (GSSException e) 
		{
			return 0;
		}
	}
	public int getRequestedLifeTime() 
	{
		return this.requested_lifetime;
	}

	private void releaseProxy() 
	{
		// TODO: I had planned to call proxy.dispose() here but I don't
		// think it actually releases any resources and it may not be safe
		// to do since I haven't figured out how to return a cloned 
		// credential to callers AND it isn't clear whether the saml 
		// credentials we're returning are keeping a ref to the proxy
		// they were created with or a copy of it.
		proxy = null;
	}


	// bean set/get methods
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	public String getHost() { return this.host; }
	public int getPort() { return this.port; }
	public String getProxyFile() { return this.proxy_file; }
	//public void setUsername(String uname) { this.username = uname; }
	//public void setPassword(String pass) { this.password = pass; }
	//public void setHost(String h) { this.host = h; }
	//public void setPort(int p) { this.port = p; }
	//public void setProxyFile(String pf) { this.proxy_file = pf; }
	//public void setRemainingTime(int ltseconds) { this.requested_lifetime = ltseconds; }
	
}

