
/*
  TomcatConnectionSource.java
  	Tomcat jdbc connection pooling.
*/
package org.ngbw.sdk.database;

import org.ngbw.sdk.database.ConnectionSource;
import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.common.util.TimeUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;
import java.lang.NoSuchMethodException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Provides <code>Connection</code> objects from a Tomcat connection pool.
 *
 * @author Terri Liebowitz Schwartz
 *
 */
public class TomcatConnectionSource extends ConnectionSource {

	/**
	 * The prefix used for properties specific to the Tomcat JDBC Pool library.
	 */
	public static final String DATABASE_POOL_PROP_PREFIX = DATABASE_PROP_PREFIX + "tomcatPool";

	private static final Log m_log = LogFactory.getLog(TomcatConnectionSource.class.getName());

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
	public TomcatConnectionSource() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, Exception
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
	public TomcatConnectionSource(Properties configProps) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, Exception
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
	@Override
	public Connection getConnection() throws SQLException
	{
		try {

			Connection connection = m_dataSource.getConnection();
			return connection;
		}
		catch (SQLException sqlErr) {
			throw sqlErr;
		}
	}

	/**
	 * Destroys the connection pool.
	 *
	 * @throws SQLException
	 */
	@Override
	public void close() throws SQLException
	{
		m_dataSource.close();
	}


	// private methods


	/**
	 * Creates a <code>DataSource</code> object using the given set of properties.
	 *
	 * @param configProps a <code>Properties</code> object containing the set of properties
	 * @return a <code>DataSource</code> object
	 * @throws SQLException
	 */
	private DataSource createDataSource(Properties configProps) throws SQLException, Exception
	{

		Properties tomcatPoolProps = new Properties();
		for (Map.Entry<Object, Object> entry : configProps.entrySet()) {
			String key = (String) entry.getKey();

			if (key.startsWith(DATABASE_POOL_PROP_PREFIX)) {
				String propKey = key.substring(DATABASE_POOL_PROP_PREFIX.length() + 1);
				Object propValue = entry.getValue();

				//m_log.debug("Setting property " + propKey + " k " + propValue);

				tomcatPoolProps.put(propKey, propValue);
			}
		}

		String dbUsername = configProps.getProperty(DATABASE_PROP_PREFIX + "username");
		String dbPassword = configProps.getProperty(DATABASE_PROP_PREFIX + "password");
		String dbUrl = configProps.getProperty(DATABASE_PROP_PREFIX + "url");
		String dbDriver = configProps.getProperty(DATABASE_PROP_PREFIX + "driverClass");

		PoolProperties pp = new PoolProperties();
		pp.setUrl(dbUrl);
		pp.setDriverClassName(dbDriver);
		pp.setUsername(dbUsername);
		pp.setPassword(dbPassword);


		try
		{
			configurePoolProperties(pp, tomcatPoolProps);
		}
		catch(Exception e)
		{
			m_log.error("Unable to configure connection pool from properties file.", e);
			throw new WorkbenchException("Error configuring connection pool from properties file.");
		}

		DataSource datasource = new DataSource();
		datasource.setPoolProperties(pp);
		m_log.debug("DataSource=" + datasource.toString());

		return datasource;
	}

	public void configurePoolProperties(PoolProperties pp, Properties p) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		for (Map.Entry<Object, Object> e : p.entrySet())
		{
			if (e.getKey() instanceof String)
			{
				if (e.getValue() instanceof String)
				{
					String tmp = ((String)e.getValue()).trim();

					//m_log.debug("Try to set pool property " + e.getKey() + "=" + e.getValue());
					BeanUtils.setProperty(pp, (String) e.getKey(), tmp);
					//m_log.debug("Set pool property " + e.getKey() + "=" + BeanUtils.getProperty(pp, (String)e.getKey()));
				}
			}
		}
	}
}
