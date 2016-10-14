/*
 * GridFtpInputStream.java
 */
package org.ngbw.sdk.tool;


import java.io.IOException;
import java.io.InputStream;

import org.globus.ftp.GridFTPClient;
import org.globus.ftp.InputStreamDataSink;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author Paul Hoover
 *
 */
class GridFtpInputStream extends InputStream {
	private static final Log log = LogFactory.getLog(GridFtpInputStream.class.getName());

	private final GridFTPClient m_client;
	private final InputStreamDataSink m_sink = new InputStreamDataSink();
	private final InputStream m_stream;
	private boolean m_closed = false;


	// constructors


	/**
	 *
	 * @param client
	 * @param fileName
	 * @throws IOException
	 * @throws ClientException
	 * @throws ServerException
	 */
	GridFtpInputStream(GridFTPClient client, String fileName) throws IOException, ClientException, ServerException
	{
		m_client = client;

		m_client.asynchGet(fileName, m_sink, null);

		m_stream = m_sink.getInputStream();
	}


	// public methods


	/**
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public int available() throws IOException
	{
		return m_stream.available();
	}

	/**
	 *
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException
	{
		try 
		{
			if (!m_closed)
			{
				m_stream.close();
				m_sink.close();
				m_client.close();	
				m_closed = true; // can't close m_client multiple times w/o error.
			}
		}
		catch (ServerException serverErr) {
			throw new IOException(serverErr.getMessage());
		}
	}

	/**
	 *
	 * @param readlimit
	 */
	@Override
	public void mark(int readlimit)
	{
		m_stream.mark(readlimit);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean markSupported()
	{
		return m_stream.markSupported();
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public int read() throws IOException
	{
		return m_stream.read();
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
		return m_stream.read(b);
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
		return m_stream.read(b, off, len);
	}

	/**
	 *
	 * @throws IOException
	 */
	@Override
	public void reset() throws IOException
	{
		m_stream.reset();
	}

	/**
	 *
	 * @param n
	 * @return
	 * @throws IOException
	 */
	@Override
	public long skip(long n) throws IOException
	{
		return m_stream.skip(n);
	}
}
