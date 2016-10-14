package org.ngbw.sdk.conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ParserException;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.common.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * FastaHeaderParser recognizes a number of formats for the descriptive header
 * and will parse them is a map of fields and values.
 * 
 * @author hannes
 *
 */
public class FastaHeaderParser {
	
	private static Log log = LogFactory.getLog(DefaultSequenceParser.class);
	private static Map<String, Pattern> descPatternLookup = new HashMap<String, Pattern>();
	private static Map<String, Map<Integer, String>> fieldLookup = new HashMap<String, Map<Integer, String>>();
	private static Map<String, Map<Integer, Boolean>> fieldOptionalLookup = new HashMap<String, Map<Integer, Boolean>>();
	
	static {
		Resource cfg;
		try {
			cfg = Resource.getResource("conversion/fasta-header-parser.cfg.xml");
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
		init(cfg);
	}
	
	/**
	 * Method accepts a single descriptive line of a FASTA record
	 * (the line starting with '>') and parses it into a map of values
	 * keyed to a field name.
	 * If the format is unknown a ParserException is thrown
	 * 
	 * @param header
	 * @return fieldMap
	 * @throws ParserException 
	 */
	public static Map<String, String> parseFields(String header) throws ParserException {
		for (String fastaFormat : descPatternLookup.keySet()) {
			Pattern pattern = descPatternLookup.get(fastaFormat);
			Matcher matcher = pattern.matcher(header);
			if (matcher.find()) {
				Map<String, String> fields = new HashMap<String, String>();
				Map<Integer, String> fieldMap = fieldLookup.get(fastaFormat);
				Map<Integer, Boolean> fieldOptionalMap = fieldOptionalLookup.get(fastaFormat);
				fields.put("FORMAT", fastaFormat);
				for(Integer group : fieldMap.keySet())
					if ((fieldOptionalMap.get(group) == false || matcher.groupCount() > (group-1)))
						fields.put(fieldMap.get(group), matcher.group(group).trim());
				return fields;
			}
		}
		throw new ParserException("Unrecognized header format: " + header);
	}


	private static void init(Resource resource) {
		Document document = XMLHelper.parseXML(resource.getInputStream());
		Element fastaParserConfigNode = XMLHelper.findNode(document,
				"FastaHeaderParserConfig");
		if (fastaParserConfigNode == null)
			throw new NullPointerException(
					"Config file does not appear to be a valid FlatfileParser Configuration.");
		NodeList fastaFormatNodes = XMLHelper.findNodes(
				fastaParserConfigNode, "Format");
		if (log.isInfoEnabled())
			log.info("Processing " + fastaFormatNodes.getLength() + " Header Format nodes.");
		for (int i = 0; i < fastaFormatNodes.getLength(); i++) {
			Element fastaFormatNode = (Element) fastaFormatNodes.item(i);
			if (fastaFormatNode.hasAttribute("id") == false)
				throw new NullPointerException("Format node attribute id is missing");
			String fastaFormat = fastaFormatNode.getAttribute("id");
			if (fastaFormatNode.hasAttribute("regex") == false)
				throw new NullPointerException("Format node attribute regex is missing");
			String regex = fastaFormatNode.getAttribute("regex");
			descPatternLookup.put(fastaFormat, Pattern.compile(regex , Pattern.DOTALL));
			if (log.isInfoEnabled())
				log.info("Header Format." + fastaFormat + " regex: '" + regex + "'");
			NodeList matchNodes = XMLHelper.findNodes(
					fastaFormatNode, "Match");
			if (log.isInfoEnabled())
				log.info("Processing " + matchNodes.getLength() + " matchNodes nodes.");
			Map<Integer, String> fieldMap = new HashMap<Integer, String>(matchNodes.getLength());
			Map<Integer, Boolean> fieldOptionalMap = new HashMap<Integer, Boolean>(matchNodes.getLength());
			for (int j = 0; j < matchNodes.getLength(); j++) {
				Element matchNode = (Element) matchNodes.item(j);
				if (matchNode.hasAttribute("group") == false)
					throw new NullPointerException("Match node attribute group is missing");
				Integer group = new Integer(matchNode.getAttribute("group"));
				if (matchNode.hasAttribute("field") == false)
					throw new NullPointerException("Match node attribute field is missing");
				String field = matchNode.getAttribute("field");
				if (matchNode.hasAttribute("optional") == false)
					throw new NullPointerException("Match node attribute optional is missing");
				Boolean optional = Boolean.parseBoolean(matchNode.getAttribute("optional"));
				fieldMap.put(group, field);
				fieldOptionalMap.put(group, optional);
				if (log.isInfoEnabled())
					log.info("Registered Header Format" + fastaFormat + " group: " + group + ""
							 + " field: " + field + " optional: " + optional);
			}
			fieldLookup.put(fastaFormat, fieldMap);
			fieldOptionalLookup.put(fastaFormat, fieldOptionalMap);
		}
	}
}
