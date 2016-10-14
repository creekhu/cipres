package org.ngbw.sdk.conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.SequenceParser;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.EntityType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefaultSequenceParser implements SequenceParser {
	
	private static Log log = LogFactory.getLog(DefaultSequenceParser.class);
	private static Map<DataFormat, Pattern> sequencePatternLookup = new HashMap<DataFormat, Pattern>();
	private static Map<EntityType, Pattern> entityTypePatternLookup = new HashMap<EntityType, Pattern>();
	private DataFormat dataFormat;

	static {
		Resource cfg;
		try {
			cfg = Resource.getResource("conversion/sequence-parser.cfg.xml");
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
		init(cfg);
	}

	public String extractSequence(String sourceRecord) {
		if (sourceRecord == null)
			throw new NullPointerException("sourceRecord argument is null.");
		return extractSequence(sourceRecord, dataFormat);
	}

	public boolean configure(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat argument is null.");
		this.dataFormat = dataFormat;
		return isConfigured();
	}

	public boolean isConfigured() {
		if (dataFormat == null)
			return false;
		return sequencePatternLookup.containsKey(dataFormat);
	}
	
	public static EntityType getEntityType(String sourceRecord, DataFormat dataFormat) {
		String sequence = extractSequence(sourceRecord, dataFormat);
		EntityType NUCLEIC_ACID = EntityType.valueOf("NUCLEIC_ACID");
		EntityType PROTEIN = EntityType.valueOf("PROTEIN");
		if (entityTypePatternLookup.get(NUCLEIC_ACID).matcher(sequence).find())
			return NUCLEIC_ACID;
		else if	(entityTypePatternLookup.get(PROTEIN).matcher(sequence).find())
			return PROTEIN;
		else
			throw new NullPointerException("No EntityType could be determined for:\n" + sourceRecord);
	}
	
	public static String extractSequence(String sourceRecord, DataFormat dataFormat) {
		if (sourceRecord == null)
			throw new NullPointerException("sourceRecord argument is null!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat argument is null!");
		Pattern pattern = sequencePatternLookup.get(dataFormat);
		return extractSequence(sourceRecord, pattern);
	}

	public static String extractSequence(String sourceRecord, Pattern pattern) {
		if (sourceRecord == null)
			throw new NullPointerException("sourceRecord argument is null!");
		if (pattern == null)
			throw new NullPointerException("Pattern is null!");
		Matcher matcher = pattern.matcher(sourceRecord);
		if (matcher.find()) 
			return matcher.group(1).replaceAll("[^A-Za-z]", "");
		throw new NullPointerException("No Sequence could be extracted from:\n" + sourceRecord);
	}

	private static void init(Resource resource) {
		Document document = XMLHelper.parseXML(resource.getInputStream());
		Element sequenceParserConfigNode = XMLHelper.findNode(document,
				"SequenceParserConfig");
		if (sequenceParserConfigNode == null)
			throw new NullPointerException(
					"Config file does not appear to be a valid SequenceParser Configuration.");
		NodeList dataFormatNodes = XMLHelper.findNodes(
				sequenceParserConfigNode, "DataFormat");
		for (int i = 0; i < dataFormatNodes.getLength(); i++) {
			Element dataFormatNode = (Element) dataFormatNodes.item(i);
			DataFormat dataFormat = DataFormat.valueOf(dataFormatNode
					.getAttribute("id"));
			if (dataFormatNode.hasAttribute("regex") == false)
				throw new NullPointerException("DataFormat node attribute regex is missing");
			String sequenceRegex = dataFormatNode.getAttribute("regex");
			sequencePatternLookup.put(dataFormat, Pattern.compile(sequenceRegex , Pattern.DOTALL));
			if (log.isInfoEnabled())
				log.info("Registered DataFormat." + dataFormat + " regex: '" + sequenceRegex + "'");
		}

		NodeList entityTypeNodes = XMLHelper.findNodes(
				sequenceParserConfigNode, "EntityType");
		for (int i = 0; i < entityTypeNodes.getLength(); i++) {
			Element entityTypeNode = (Element) entityTypeNodes.item(i);
			EntityType entityType = EntityType.valueOf(entityTypeNode
					.getAttribute("id"));
			if (entityTypeNode.hasAttribute("regex") == false)
				throw new NullPointerException("DataFormat node attribute regex is missing");
			String entityTypeRegex = entityTypeNode.getAttribute("regex");
			entityTypePatternLookup.put(entityType, Pattern.compile(entityTypeRegex , Pattern.CASE_INSENSITIVE));
			if (log.isInfoEnabled())
				log.info("Registered EntityType." + entityType + " regex: '" + entityTypeRegex + "'");
		}
	}
}
