/*
 * C3P0ConnectionSource.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.resourcepool.TimeoutException;


/**
 * Provides <code>Connection</code> objects from a C3P0 connection pool.
 *
 * @author Paul Hoover
 *
 */
public class C3P0ConnectionSource extends ConnectionSource {

	/**
	 * The prefix used for properties specific to the C3P0 library.
	 */
	public static final String DATABASE_C3P0_PROP_PREFIX = DATABASE_PROP_PREFIX + "c3p0";

	private static final Log m_log = LogFactory.getLog(C3P0ConnectionSource.class.getName());

	private final Properties m_configProps;
	private DataSource m_dataSource;


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
	 * @throws SQLException
	 */
	public C3P0ConnectionSource() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		this(getDatabaseConfiguration());
	}

	/**
	 * Constructs an instance of the object using the given set of properties.
	 *
	 * @param configProps a <code>Properties</code> object containing the set of properties
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public C3P0ConnectionSource(Properties configProps) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		loadDriver(configProps);

		m_configProps = new Properties();

		m_configProps.putAll(configProps);

		m_dataSource = createDataSource(configProps);
	}


	// public methods


	/**
	 * Returns a <code>Connection</code> object from the connection pool.
	 *
	 * @return a <code>Connection</code> object
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException
	{
		try {
			return m_dataSource.getConnection();
		}
		catch (SQLException sqlErr) {
			Throwable cause = sqlErr.getCause();

			if (cause == null || !cause.getClass().getName().equals(TimeoutException.class.getName()))
				throw sqlErr;

			m_log.warn("An attempt to acquire a Connections has timed out, restarting the connection pool");

			DataSources.destroy(m_dataSource);

			m_dataSource = createDataSource(m_configProps);

			return m_dataSource.getConnection();
		}
	}

	/**
	 * Destroys the connection pool.
	 *
	 * @throws SQLException
	 */
	public void close() throws SQLException
	{
		DataSources.destroy(m_dataSource);
	}


	// private methods


	/**
	 * Creates a <code>DataSource</code> object using the given set of properties.
	 *
	 * @param configProps a <code>Properties</code> object containing the set of properties
	 * @return a <code>DataSource</code> object
	 * @throws SQLException
	 */
	private DataSource createDataSource(Properties configProps) throws SQLException
	{
		String dbUsername = configProps.getProperty(DATABASE_PROP_PREFIX + "username");
		String dbPassword = configProps.getProperty(DATABASE_PROP_PREFIX + "password");
		String dbUrl = configProps.getProperty(DATABASE_PROP_PREFIX + "url");

		m_log.debug("url: " + dbUrl);
		m_log.debug("user: " + dbUsername);

		Properties connProps = new Properties();

		connProps.setProperty("user", dbUsername);
		connProps.setProperty("password", dbPassword);

		Properties c3p0Props = new Properties();

		for (Map.Entry<Object, Object> entry : configProps.entrySet()) {
			String key = (String) entry.getKey();

			if (key.startsWith(DATABASE_C3P0_PROP_PREFIX)) {
				String propKey = key.substring(DATABASE_PROP_PREFIX.length());
				Object propValue = entry.getValue();

				m_log.debug("Setting property " + propKey + " = " + propValue);

				c3p0Props.put(propKey, propValue);
			}
		}

		DataSource unpooledSource = DataSources.unpooledDataSource(dbUrl, connProps);

		return DataSources.pooledDataSource(unpooledSource, c3p0Props);
	}
}
