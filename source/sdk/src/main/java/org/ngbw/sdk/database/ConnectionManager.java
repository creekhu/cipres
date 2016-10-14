/*
 * ConnectionManager.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Maintains a global instance of a <code>ConnectionSource</code> object. This global instance is maintained
 * separately from the <code>Workbench</code> class so that classes from the <code>org.ngbw.sdk.database</code>
 * package can be used without requiring the construction of a <code>Workbench</code> object.
 * 
 * @author Paul Hoover
 *
 */
public class ConnectionManager {

	private static ConnectionSource m_connSource;


	// public methods


	/**
	 * Returns the global <code>ConnectionSource</code> object.
	 * 
	 * @return a ConnectionSource object
	 */
	public static ConnectionSource getConnectionSource()
	{
		return m_connSource;
	}

	/**
	 * Sets the global <code>ConnectionSource</code> object.
	 * 
	 * @param connSource the <code>ConnectionSource</code>
	 */
	public static void setConnectionSource(ConnectionSource connSource)
	{
		m_connSource = connSource;
	}

	/**
	 * Creates an instance of a <code>ConnectionSource</code> object and sets it as the global
	 * instance. The new instance is created using the set of properties returned from the
	 * {@link ConnectionSource#getDatabaseConfiguration() getDatabaseConfiguration} method
	 * of the <code>ConnectionSource</code> class.
	 * 
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void setConnectionSource() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, Exception
	{
		Properties configProps = ConnectionSource.getDatabaseConfiguration();
		String useConnectionPool = configProps.getProperty(ConnectionSource.DATABASE_PROP_PREFIX + "useConnectionPool", "true");
		ConnectionSource connSource;

		if (Boolean.parseBoolean(useConnectionPool))
			//connSource = new C3P0ConnectionSource(configProps);
			connSource = new TomcatConnectionSource(configProps);
		else
			connSource = new DriverConnectionSource(configProps);

		setConnectionSource(connSource);
	}
}
