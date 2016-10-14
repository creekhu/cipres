/*
 * ConnectionSource.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;


/**
 * A class that provides a source of JDBC <code>Connection</code> objects.
 * 
 * @author Paul Hoover
 *
 */
public abstract class ConnectionSource {

	/**
	 * The location of the database configuration resource.
	 */
	public static final String DATABASE_CONFIG_URL = "database/database.properties";

	/**
	 * The prefix used for properties specific to the database package.
	 */
	public static final String DATABASE_PROP_PREFIX = "database.";

	private static final Log m_baseLog = LogFactory.getLog(ConnectionSource.class.getName());


	// public methods


	/**
	 * Returns a <code>Connection</code> object from the data source.
	 * 
	 * @return a <code>Connection</code> object
	 * @throws SQLException
	 */
	public abstract Connection getConnection() throws SQLException;

	/**
	 * Releases any resources required by the data source.
	 * 
	 * @throws SQLException
	 */
	public abstract void close() throws SQLException;

	/**
	 * Returns a set of database properties from the resource located at <code>DATABASE_CONFIG_URL</code>.
	 * 
	 * @return a <code>Properties</code> object containing the set of database properties
	 * @throws IOException
	 */
	public static Properties getDatabaseConfiguration() throws IOException
	{
		InputStream stream = ConnectionSource.class.getClassLoader().getResourceAsStream(DATABASE_CONFIG_URL);

		if (stream == null)
			throw new WorkbenchException("Couldn't find resource " + DATABASE_CONFIG_URL);

		Properties configProps = new Properties();

		configProps.load(stream);

		return configProps;
	}


	// protected methods


	/**
	 * Loads the database vendor's JDBC driver class, using a property from the provided <code>Properties</code> object.
	 * 
	 * @param configProps a <code>Properties</code> object that contains a value for the key "driverClass"
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	protected static void loadDriver(Properties configProps) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String driverClassName = configProps.getProperty(DATABASE_PROP_PREFIX + "driverClass");

		m_baseLog.debug("Loading driver " + driverClassName);

		Class.forName(driverClassName).newInstance();
	}
}
