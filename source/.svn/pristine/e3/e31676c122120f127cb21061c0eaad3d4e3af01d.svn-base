package org.ngbw.sdk.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.types.DataFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FlatfileRecordFilter implements RecordFilter {

	private static Log log = LogFactory.getLog(FlatfileRecordFilter.class);
	protected static Map<DataFormat, Pattern> startPatternLookup = new HashMap<DataFormat, Pattern>();
	protected static Map<DataFormat, Pattern> endPatternLookup = new HashMap<DataFormat, Pattern>();
	
	private BufferedReader br;
	protected DataFormat dataFormat;
	protected Pattern startPattern;
	protected Pattern endPattern;
	private boolean inRecord = false;
	private String firstLine;
	private String nextRecord; 
	
	static {
		Resource cfg;
		try {
			cfg = Resource.getResource("conversion/flatfile-recordfilter.cfg.xml");
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
		init(cfg);
	}

	public FlatfileRecordFilter() {}

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
		readNext();
	}

	public boolean configure(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat argument is null.");
		this.dataFormat = dataFormat;
		this.startPattern = startPatternLookup.get(dataFormat);
		this.endPattern = endPatternLookup.get(dataFormat);
		return isConfigured();
	}

	public boolean isConfigured() {
		if (log.isDebugEnabled()) log.debug("configured dataFormat: " + dataFormat);
		if (log.isDebugEnabled()) log.debug("configured startPattern: " + startPattern);
		if (log.isDebugEnabled()) log.debug("configured endPattern: " + endPattern);
		return (startPattern != null && dataFormat != null);
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
	 * @see org.ngbw.sdk.conversion.RecordFilter#close()
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

	public DataFormat getFilteredFormat() {
		return dataFormat;
	}

	private static void init(Resource resource) {
		Document document = XMLHelper.parseXML(resource.getInputStream());
		Element flatfileRecordFilterConfigNode = XMLHelper.findNode(document,
				"FlatfileRecordFilterConfig");
		if (flatfileRecordFilterConfigNode == null)
			throw new NullPointerException(
					"Config file does not appear to be a valid FlatfileParser Configuration.");
		NodeList dataFormatNodes = XMLHelper.findNodes(
				flatfileRecordFilterConfigNode, "DataFormat");
		for (int i = 0; i < dataFormatNodes.getLength(); i++) {
			Element dataFormatNode = (Element) dataFormatNodes.item(i);
			DataFormat dataFormat = DataFormat.valueOf(dataFormatNode
					.getAttribute("id"));
			if (dataFormatNode.hasAttribute("startPattern") == false)
				throw new NullPointerException("DataFormat node attribute startPattern is missing");
			String startRegex = dataFormatNode.getAttribute("startPattern");
			startPatternLookup.put(dataFormat, Pattern.compile(startRegex));
			String endRegex = "null";
			if (dataFormatNode.hasAttribute("endPattern")) {
				endRegex = dataFormatNode.getAttribute("endPattern");
				if (endRegex.equalsIgnoreCase("null") == false)
					endPatternLookup.put(dataFormat, Pattern.compile(endRegex));
			}
			if (log.isInfoEnabled())
				log.info("Registered DataFormat." + dataFormat + " startRegex: '" + startRegex + "' endRegex: '" + endRegex + "'");
		}
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.RecordFilter#next()
	 */
	private void readNext() {
		StringBuffer recordBuffer = new StringBuffer();
		if (firstLine != null) {
			recordBuffer.append(firstLine + "\n");
			firstLine = null;
		}
		String line = null;
		try {
			while (br.ready() && (line = br.readLine()) != null) {
				if (startPattern.matcher(line).find()) {
					if (inRecord) {
						firstLine = line;
						break;
					}
					inRecord = true;
				} else {
					if (endPattern != null && endPattern.matcher(line).find()) {
						recordBuffer.append(line + "\n");
						inRecord = false;
						break;
					}
				}
				if (inRecord) recordBuffer.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (recordBuffer.length() == 0)
			nextRecord = null;
		else if (recordBuffer.toString().trim().length() == 0)
			nextRecord = null;
		else
			nextRecord = recordBuffer.toString();
	}
}
