/*
 * SSLConnectionManager.java
 */
package org.ngbw.sdk.core.io;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;

import com.trilead.ssh2.Connection;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class SSLConnectionManager {

	private static final String SSL_CONFIG_URL = "ssl.properties";

	private static final Log m_log = LogFactory.getLog(SSLConnectionManager.class);
	private static SSLConnectionManager m_instance;

	private final Map<String, SSLConnectionPool> m_pools = new TreeMap<String, SSLConnectionPool>();


	// constructors


	private SSLConnectionManager(Properties props) throws IOException
	{
		Set<String> poolNames = new TreeSet<String>();

		for (Iterator<Map.Entry<Object, Object>> entries = props.entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<Object, Object> entry = entries.next();
			String propName = (String) entry.getKey();
			int dotChar = propName.indexOf('.');

			if (dotChar == -1) {
				m_log.error("Unexpected property in " + SSL_CONFIG_URL + ": " + propName);
				continue;
			}

			poolNames.add(propName.substring(0, dotChar));
		}

		for (Iterator<String> entries = poolNames.iterator() ; entries.hasNext() ; ) {
			String poolName = entries.next();
            String host = props.getProperty(poolName + ".host");

            if (host == null) {
            	m_log.error("No host specified for " + poolName);
                continue;
            }

            String username = props.getProperty(poolName + ".username");

            if (username == null) {
            	m_log.error("No username specified for " + poolName);
                continue;
            }

            String password = props.getProperty(poolName + ".password");
            String keyFile = props.getProperty(poolName + ".keyFile");
            if (password == null && keyFile == null) 
			{
            	m_log.error("No password or keyFile specified for " + poolName);
				continue;
            }

            String minconn = props.getProperty(poolName + ".minconn", "0");
            int minconnIntValue;

            try {
            	minconnIntValue = Integer.parseInt(minconn);
            }
            catch (NumberFormatException e) {
                m_log.error("Invalid minconn value " + minconn + " for " + poolName);

                minconnIntValue = 0;
            }

            String maxconn = props.getProperty(poolName + ".maxconn", "10");
            int maxconnIntValue;

            try {
            	maxconnIntValue = Integer.parseInt(maxconn);
            }
            catch (NumberFormatException e) {
                m_log.error("Invalid maxconn value " + maxconn + " for " + poolName);

                maxconnIntValue = 10;
            }

			try
			{
            	SSLConnectionPool pool = new SSLConnectionPool(host, username, password, keyFile, minconnIntValue, maxconnIntValue);
            	m_pools.put(poolName, pool);
			}
			catch (Throwable t)
			{
				m_log.error("Error creating connection to " + host + ".", t);
				// catch exception so that we continue with other resources.
			}
		}
	}


	// public methods


	public static synchronized SSLConnectionManager getInstance() throws IOException
	{
		if (m_instance == null) {
			InputStream stream = SSLConnectionManager.class.getClassLoader().getResourceAsStream(SSL_CONFIG_URL);

			if (stream == null)
				throw new WorkbenchException("Couldn't find resource " + SSL_CONFIG_URL);

			Properties props = new Properties();

			props.load(stream);

			m_instance = new SSLConnectionManager(props);
		}

		return m_instance;
	}

	public static synchronized void releaseInstance()
	{
		if (m_instance != null) {
			m_instance.release();

			m_instance = null;
		}
	}

	public Connection getConnection(String poolName) throws IOException
	{
		SSLConnectionPool pool = m_pools.get(poolName);

		if (pool == null)
			throw new WorkbenchException("No connection pool exists for " + poolName);

		return pool.getConnection();
	}

	public Connection getConnection(String poolName, long timeout) throws IOException
	{
		SSLConnectionPool pool = m_pools.get(poolName);

		if (pool == null)
			throw new WorkbenchException("No connection pool exists for " + poolName);

		return pool.getConnection(timeout);
	}

	public void release()
	{
		for (Iterator<SSLConnectionPool> pools = m_pools.values().iterator() ; pools.hasNext() ; )
			pools.next().release();

		m_pools.clear();
	}

	public String getHost(String name)
	{
		return m_pools.get(name).m_host;
	}

	public String getUsername(String name)
	{
		return m_pools.get(name).m_username;
	}

	public String getPassword(String name)
	{
		return m_pools.get(name).m_password;
	}

	public File getKeyfile(String name)
	{
		return m_pools.get(name).m_keyFile;
	}
}
