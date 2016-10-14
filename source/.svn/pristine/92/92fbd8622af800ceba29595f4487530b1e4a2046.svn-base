/*
 * SFTPInputStream.java
 */
package org.ngbw.sdk.tool;


import java.io.IOException;
import java.io.InputStream;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SFTPv3Client;
import com.trilead.ssh2.SFTPv3FileHandle;


/**
 *
 * @author Paul Hoover
 *
 */
class SFTPInputStream extends InputStream {

	private final Connection m_conn;
	private final SFTPv3Client m_client;
	private final SFTPv3FileHandle m_handle;
	private long m_offset = 0;
	private boolean m_isClosed = false;


	// constructors


	/**
	 *
	 * @param conn
	 * @param client
	 * @param fileName
	 * @throws IOException
	 */
	SFTPInputStream(Connection conn, SFTPv3Client client, String fileName) throws IOException
	{
		m_conn = conn;
		m_client = client;
		m_handle = client.openFileRO(fileName);
	}


	// public methods


	/**
	 *
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException
	{
		// Terri - added m_isClosed because we can't set m_conn to null after closing it and 
		// I don't think it's safe to close SSLConnectionPool objects multiple times.  m_conn may be an SSLConnectionPool object.
		// Not sure this is needed but I saw something in the logs that suggested a connection was being closed more than once. 
		if (!m_isClosed)
		{
			m_isClosed = true;
			m_client.closeFile(m_handle);
			m_client.close();
			m_conn.close();
		}
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public int read() throws IOException
	{
		byte[] readBuffer = new byte[1];

		if (read(readBuffer, 0, 1) <= 0)
			return -1;

		return readBuffer[0] & 0xff;
	}

	/**
	 *
	 * @param b
	 * @return
	 * @throws IOException
	 */
	@Override
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	/**
	 *
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (off < 0 || len < 0 || len > b.length - off)
			throw new IndexOutOfBoundsException();

		int bytesRead = m_client.read(m_handle, m_offset, b, off, len);

		if (bytesRead <= 0)
			return -1;

		m_offset += bytesRead;

		return bytesRead;
	}
}
