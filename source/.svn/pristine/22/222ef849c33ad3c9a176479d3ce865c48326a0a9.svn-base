/*
 * DriverConnectionSource.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides <code>Connection</code> objects from the JDBC <code>DriverManager</code> class.
 *
 * @author Paul Hoover
 *
 */
public class DriverConnectionSource extends ConnectionSource {

	private static final Log m_log = LogFactory.getLog(DriverConnectionSource.class.getName());

	private final String m_dbUsername;
	private final String m_dbPassword;
	private final String m_dbUrl;


	// constructors


	/**
	 * Constructs an instance of the object using the set of properties returned from
	 * the {@link ConnectionSource#getDatabaseConfiguration() getDatabaseConfiguration}
	 * method of the <code>ConnectionSource</code> class.
	 *
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public DriverConnectionSource() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this(getDatabaseConfiguration());
	}

	/**
	 * Constructs an instance of the object using the given set of properties.
	 *
	 * @param configProps
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public DriverConnectionSource(Properties configProps) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		loadDriver(configProps);

		m_dbUsername = configProps.getProperty(DATABASE_PROP_PREFIX + "username");
		m_dbPassword = configProps.getProperty(DATABASE_PROP_PREFIX + "password");
		m_dbUrl = configProps.getProperty(DATABASE_PROP_PREFIX + "url");

		m_log.debug("url: " + m_dbUrl);
		m_log.debug("user: " + m_dbUsername);
	}


	// public methods


	/**
	 * Returns a <code>Connection</code> object from the data source.
	 *
	 * @return a <code>Connection</code> object
	 * @throws SQLException
	 */
	@Override
	public Connection getConnection() throws SQLException
	{
		Properties connProps = new Properties();

		connProps.setProperty("user", m_dbUsername);
		connProps.setProperty("password", m_dbPassword);

		Connection connection = DriverManager.getConnection(m_dbUrl, connProps);
		return connection;
	}

	/**
	 * Releases any resources required by the data source. The <code>DriverManager</code> class
	 * doesn't require manual resource management, so this method does nothing.
	 *
	 * @throws SQLException
	 */
	@Override
	public void close() throws SQLException
	{
		// do nothing
	}
}
