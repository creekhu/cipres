/*
 * BlastTEXTRecordFilter
 */
package org.ngbw.sdk.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.core.types.DataFormat;

/**
 * 
 * @author Roland H. Niedner
 *
 */
public class BlastTEXTRecordFilter implements RecordFilter {

	private static Log log = LogFactory.getLog(BlastTEXTRecordFilter.class);
	private BufferedReader br;
	private DataFormat dataFormat;
	private static final Pattern startPattern = Pattern.compile("^\\S?BLAST(P|N|X).*\\]$");
	private static final Pattern lastBlockPattern = Pattern.compile("^Matrix:\\s(.*)$");
	private boolean inRecord = false;
	private String firstLine;
	private String infoBlock;
	private String nextRecord; 
	
	public BlastTEXTRecordFilter() {
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.api.conversion.RecordFilter#close()
	 */
	public void close() {
		if (br != null)
			try {
				br.close();
			} catch (IOException e) {
				// ignored
				log.error(e);
			}
	}
	
	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.RecordFilter#hasNext()
	 */
	public boolean hasNext() {
		return (nextRecord != null);
	}
	
	public String next() {
		if (nextRecord == null)
			throw new NullPointerException("No more records!");
		String next = nextRecord;
		readNext();
		return next;
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.api.conversion.RecordFilter#setInput(java.io.BufferedReader)
	 */
	public void setInput(BufferedReader br) {
		boolean ready = false;
		if(br == null) 
			throw new NullPointerException("BufferedReader input is null");
		try {
			ready = br.ready();
		} catch (IOException e) {
			throw new RuntimeException("BufferedReader has an IOException");
		}
		if(ready == false) 
			throw new RuntimeException("BufferedReader input is not ready");
		this.br = br;
		infoBlock = firstPass();
		readNext();
	}

	public DataFormat getFilteredFormat() {
		return dataFormat;
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.api.core.Configurable#configure(java.lang.Object)
	 */
	public boolean configure(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat argument is null.");
		this.dataFormat = dataFormat;
		return isConfigured();
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.api.core.Configurable#isConfigured()
	 */
	public boolean isConfigured() {
		return (startPattern != null && dataFormat != null);
	}
	
	private String firstPass() {
		StringBuffer fullRecord = new StringBuffer();
		StringBuffer blockBuffer = new StringBuffer("\n");
		String line = null;
		boolean capture = false;
		try {
			while ((line = br.readLine()) != null) {
				fullRecord.append(line + "\n");
				if (capture == false && lastBlockPattern.matcher(line).find()) 
					capture = true;
				if (capture)
					blockBuffer.append(line + "\n");
			}
			br = new BufferedReader(new StringReader(fullRecord.toString()));
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		return blockBuffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.api.conversion.RecordFilter#next()
	 */
	private void readNext() {
		StringBuffer recordBuffer = new StringBuffer();
		boolean hasInfoBlock = false;
		if (firstLine != null) {
			recordBuffer.append(firstLine + "\n");
			firstLine = null;
		}
		String line = null;
		try {
			while (br.ready() && (line = br.readLine()) != null) {
				if (hasInfoBlock == false && lastBlockPattern.matcher(line).find())
					hasInfoBlock = true;
				if (startPattern.matcher(line).find()) {
					if (inRecord) {
						firstLine = line;
						break;
					}
					inRecord = true;
				}
				if (inRecord) recordBuffer.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		if (hasInfoBlock == false && recordBuffer.length() > 0)
			recordBuffer.append(infoBlock);
		if (recordBuffer.length() == 0)
			nextRecord = null;
		else if (recordBuffer.toString().trim().length() == 0)
			nextRecord = null;
		else
			nextRecord = recordBuffer.toString();
	}
}
