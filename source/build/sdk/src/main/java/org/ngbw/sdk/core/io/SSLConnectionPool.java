/*
 * SSLConnectionPool.java
 */
package org.ngbw.sdk.core.io;


import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.WorkbenchException;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionInfo;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
class SSLConnectionPool {

	private static class PooledConnection extends Connection {

		private final SSLConnectionPool m_owner;
		private boolean m_available = true;


		// constructors


		public PooledConnection(String hostname, SSLConnectionPool owner)
		{
			super(hostname);

			m_owner = owner;
		}

		public PooledConnection(String hostname, int port, SSLConnectionPool owner)
		{
			super(hostname, port);

			m_owner = owner;
		}


		// public methods


		@Override
		public void close()
		{
			m_owner.freeConnection(this);
		}


		// package methods


		boolean isAvailable()
		{
			return m_available;
		}

		void setAvailable(boolean available)
		{
			m_available = available;
		}

		void release()
		{
			super.close();
		}
	}


	private static final Log m_log = LogFactory.getLog(SSLConnectionPool.class);

	final String m_host;
	final String m_username;
	final String m_password;
	final File m_keyFile;
	private final List<PooledConnection> m_pool = new LinkedList<PooledConnection>();
	private int m_minConn = 0;
	private int m_maxConn = 10;


	// constructors


	SSLConnectionPool(String host, String username, String password, String keyFile, int minConn, int maxConn) throws IOException
	{
		if (host == null)
			throw new WorkbenchException("host is null");

		if (username == null)
			throw new WorkbenchException("username is null");

		if (password == null && keyFile == null)
			throw new WorkbenchException("both password and keyFile are null");

		m_host = host;
		m_username = username;
		m_password = password;
		m_keyFile = (keyFile != null) ? new File(keyFile) : null;
		if (minConn > 0)
			m_minConn = minConn;

		if (maxConn > 0)
			m_maxConn = maxConn;

		for (int i = 0; i < m_minConn; i += 1)
			m_pool.add(newConnection());

		//m_log.debug("Initialized connection pool for " + m_username + "@" + m_host);
	}


	// public methods


	public synchronized Connection getConnection() throws IOException
	{
		/* START : for debug only */
		/*
		try
		{
			throw new Exception("");
		}
		catch(Exception e)
		{
			m_log.error("GET connection from pool for " + m_username + "@" + m_host, e);
		}
		*/
		/* END : for debug only */

		int index = 0;
		PooledConnection newConn = null;

		/*
			Try to get first connection in pool, if not available, try the next, etc, adding connections
			to the end of the pool as needed until maxConn limit is reached.  When we find an available
			connection in the poll, we ping it and if it's dead we remove it from the pool.  This shifts
			the other connections down in the list so when we continue at the top of the loop we're trying
			the next connection in the pool if any.

		*/
		while (true) 
		{
			// m_log.debug("Consider connection at index " + index);
			if (index >= m_pool.size()) 
			{
				/*
				m_log.debug("At end of pool, can we add a connection? " +
					"Current pool size is " + m_pool.size() + ", max size is " + m_maxConn);
				*/
				if (m_maxConn == 0 || m_pool.size() < m_maxConn) 
				{
					// newConnection() logs START/END as it gets the connection.
					newConn = newConnection();
					newConn.setAvailable(false);
					m_pool.add(newConn);
				} else
				{
					m_log.debug("Connection pool is full and none of the connections are available.");
				}
				break;
			}

			PooledConnection conn = m_pool.get(index);
			if (conn.isAvailable()) 
			{
				try 
				{
					conn.ping();
				}
				catch (IOException ioErr) 
				{
					conn.release();
					m_pool.remove(index);

					m_log.debug("Removed a dead connection from pool for " + m_username + "@" + m_host + " : " + ioErr.toString());
					continue;
				}
				newConn = conn;
				newConn.setAvailable(false);
				break;
			} else
			{
				//m_log.debug("It's not available");
			}
			index += 1;
		}

		if (newConn != null)
		{
			m_log.debug("GOT connection " + newConn.toString());
		}
		return newConn;
	}

	public synchronized Connection getConnection(long timeout) throws IOException
	{
		long startTime = Calendar.getInstance().getTimeInMillis();
		Connection newConn;

		while ((newConn = getConnection()) == null) {
			try {
				wait(timeout);
			}
			catch (InterruptedException interruptErr) {
				// do nothing
			}

			if ((Calendar.getInstance().getTimeInMillis() - startTime) >= timeout)
				break;
		}

		return newConn;
	}

	public synchronized void release()
	{
		for (Iterator<PooledConnection> elements = m_pool.iterator() ; elements.hasNext() ; )
			elements.next().release();

		m_pool.clear();

		//m_log.debug("Closed connection pool for " + m_username + "@" + m_host);
	}

	public synchronized void setMinConnections(int minConn)
	{
		if (minConn >= 0)
			m_minConn = minConn;
	}

	public synchronized void setMaxConnections(int maxConn)
	{
		if (maxConn >= 0)
			m_maxConn = maxConn;
	}


	// private methods


	private synchronized void freeConnection(PooledConnection conn)
	{
		/* START: debug only */
		/*
		try
		{
			throw new Exception();
		}
		catch(Exception e)
		{
			m_log.error("RELEASE connection " + conn.toString(), e);
		}
		*/
		/* END : debug only */


		conn.setAvailable(true);

		notifyAll();

		m_log.debug("RELEASE connection " + this.toString()); 
	}

	private PooledConnection newConnection() throws IOException
	{
		PooledConnection newConn = new PooledConnection(m_host, this);

		try {

			// These debug messages let us see how long it takes to establish a new connection.
			// m_log.debug("START get connection for " + m_username + "@" + m_host);

			ConnectionInfo ci = newConn.connect();

			if (m_keyFile != null)
			{
				if (!newConn.authenticateWithPublicKey(m_username, m_keyFile, m_password)) 
				{
					newConn.release();
					throw new WorkbenchException("Public Key Authentication failed.");
				}
			} else if (!newConn.authenticateWithPassword(m_username, m_password)) 
			{
				newConn.release();
				throw new WorkbenchException("Password Authentication failed.");
			}

			// Log timestamp will show how long it took.
			// m_log.debug("END get connection for " + m_username + "@" + m_host);
		}
		catch (IOException ioErr) {
			newConn.release();
			m_log.error("Error connecting to " + m_username + "@" + m_host + ".", ioErr); 

			// Original exception may be too detailed to show to user so rethrow with less detailed message. 
			IOException ioe = new IOException("Error connecting to " + m_username + "@" + m_host + "."); 
			ioe.initCause(ioErr);
			throw ioErr;
		}
		return newConn;
	}
}
