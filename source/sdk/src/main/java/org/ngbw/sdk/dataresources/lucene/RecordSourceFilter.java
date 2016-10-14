package org.ngbw.sdk.dataresources.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RecordSourceFilter {

	private static Log log = LogFactory.getLog(RecordSourceFilter.class);
	protected Pattern startPattern;
	protected Pattern endPattern;

	public RecordSourceFilter(String startPattern, String endPattern) {
		if (startPattern != null && startPattern.trim().length() > 0)
			this.startPattern = Pattern.compile(startPattern);
		if (endPattern != null && endPattern.trim().length() > 0)
			this.endPattern = Pattern.compile(endPattern);
	}
	
	public String filter(String sourceData) {
		if(sourceData == null) 
			throw new NullPointerException("sourceData input is null");
		if (log.isDebugEnabled()) log.debug("startPattern: " + startPattern);
		if (log.isDebugEnabled()) log.debug("endPattern: " + endPattern);
		
		if (startPattern == null && endPattern == null) {
			if (log.isInfoEnabled()) log.info("Start and End pattern are null: Pass through unchanged");
			return sourceData;
		}
		StringBuffer recordBuffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new StringReader(sourceData));
		boolean capture = false;
		try {
			String line;
			while ((line = br.readLine()) != null) {
				//no start pattern means we start at the first line
				if (startPattern == null)
					capture = true;
				//ok, we start capturing once we hit the start pattern
				else if (startPattern.matcher(line).find()) 
					capture = true;
				//we stop looping through the lines - no more capturing
				else if (endPattern != null && endPattern.matcher(line).find())
					break;
				//you say capture so I obey
				if (capture) recordBuffer.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException("Error looping through the source data, duh?", e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// ignored
					log.error(e);
				}
		}
		return recordBuffer.toString();
	}
}
