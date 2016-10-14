package org.ngbw.sdk.conversion;

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

public class XMLFileRecordFilter extends FlatfileRecordFilter implements RecordFilter {
	
	private static Log log = LogFactory.getLog(XMLFileRecordFilter.class);
	
	static {
		Resource cfg;
		try {
			cfg = Resource.getResource("conversion/xml-recordfilter.cfg.xml");
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
		init(cfg);
	}
	
	public XMLFileRecordFilter() { }

	private static void init(Resource resource) {
		Document document = XMLHelper.parseXML(resource.getInputStream());
		Element flatfileRecordFilterConfigNode = XMLHelper.findNode(document,
				"XMLRecordFilterConfig");
		if (flatfileRecordFilterConfigNode == null)
			throw new NullPointerException(
					"Config file does not appear to be a valid XMLFileRecordFilter Configuration.");
		NodeList dataFormatNodes = XMLHelper.findNodes(
				flatfileRecordFilterConfigNode, "DataFormat");
		for (int i = 0; i < dataFormatNodes.getLength(); i++) {
			Element dataFormatNode = (Element) dataFormatNodes.item(i);
			DataFormat dataFormat = DataFormat.valueOf(dataFormatNode
					.getAttribute("id"));
			if (dataFormatNode.hasAttribute("recordNode") == false)
				throw new NullPointerException("DataFormat node attribute recordNode is missing");
			String nodeName = dataFormatNode.getAttribute("recordNode");
			startPatternLookup.put(dataFormat, Pattern.compile("^\\s*<"+nodeName+"[^<>]*>\\s*"));
			endPatternLookup.put(dataFormat, Pattern.compile("^[^<>]*</"+nodeName+">\\s*"));
			if (log.isInfoEnabled())
				log.info("Registered DataFormat." + dataFormat + " recordNode: '" + nodeName + "'");
		}
	}
}
