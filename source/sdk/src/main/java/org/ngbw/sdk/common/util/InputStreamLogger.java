/*
 * InputStreamLogger.java
 */
package org.ngbw.sdk.common.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;


/**
 * 
 * @author Paul Hoover
 *
 */
public class InputStreamLogger extends Thread {

	private final InputStream m_stream;
	private final String m_label;
	private final Log m_log;


	public static void readInputStream(InputStream stream, String label, Log log)
	{
		(new InputStreamLogger(stream, label, log)).start();
	}

	private InputStreamLogger(InputStream stream, String label, Log log)
	{
		assert(stream != null);
		assert(label != null);
		assert(log != null);

		m_stream = stream;
		m_label = label;
		m_log = log;
	}

	@Override
	public void run()
	{
		try {
			InputStreamReader streamReader = new InputStreamReader(m_stream);
			BufferedReader reader = new BufferedReader(streamReader);
			String line;

			while ((line = reader.readLine()) != null)
				m_log.info(m_label + " ===> " + line);
		}
		catch (IOException ioErr) {
			m_log.info(m_label, ioErr);
		}
	}
}
