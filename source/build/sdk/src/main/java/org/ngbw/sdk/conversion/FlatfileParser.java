/**
 * 
 */
package org.ngbw.sdk.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * FlatfileParser parses individual records of a flat file dataset into a
 * DataRecord. It can be used a generic parser for DataFormatReaders for flat
 * file based DataFormats. The class is configured via an xml file. a default
 * config file is located in the resources/conversion package. <br />
 * 
 * @author hannes
 * 
 */
public class FlatfileParser {

	private static Log log = LogFactory.getLog(FlatfileParser.class);
	private static final String DEFAULT_SEPARATOR = "*";
	private static final String DEFAULT_SEPARATOR_REGEX = "\\*";
	private static Map<DataFormat, Set<RecordType>> recordTypeLookup = new HashMap<DataFormat, Set<RecordType>>();
	private static Map<DataFormat, Map<RecordFieldType, Pattern>> patternLookup = new HashMap<DataFormat, Map<RecordFieldType, Pattern>>();
	private static Map<DataFormat, Map<RecordFieldType, Boolean>> collectionLookup = new HashMap<DataFormat, Map<RecordFieldType, Boolean>>();
	private static Map<DataFormat, Map<RecordFieldType, String>> dateFormatLookup = new HashMap<DataFormat, Map<RecordFieldType, String>>();
	private static Map<DataFormat, Map<RecordFieldType, String>> separatorLookup = new HashMap<DataFormat, Map<RecordFieldType, String>>();
	private Set<RecordFieldType> fields;
	
	static {
		try {
			init(Resource.getResource("conversion/flatfile-parser.cfg.xml"));
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}
	
	public FlatfileParser(Set<RecordFieldType> fields) {
		if (fields == null)
			throw new NullPointerException("fields is null!");
		this.fields = fields;
		if (log.isDebugEnabled()) {
			for (RecordFieldType field : fields)
				log.debug(field + " : " + field.dataType());
		}
	}
	
	public static Map<DataFormat, Set<RecordType>> canParse() {
		return recordTypeLookup;
	}
	
	public static Map<RecordFieldType, String> parseFields(DataFormat dataFormat, Set<RecordFieldType> fields, String record) {
		if (fields == null)
			throw new NullPointerException("fields is null!");
		if (record == null)
			throw new NullPointerException("record is null!");
		FlatfileParser parser = new FlatfileParser(fields);
		Map<RecordFieldType, StringBuffer> values = new HashMap<RecordFieldType, StringBuffer>();
		BufferedReader reader = new BufferedReader(new StringReader(record));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				parser.processLine(dataFormat, fields, line, values);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Map<RecordFieldType, String>  result = new HashMap<RecordFieldType, String>(values.size());
		for (RecordFieldType field : fields) {
			StringBuffer value = values.get(field);
			if (value == null)
				throw new NullPointerException("Value for RecordField." + field + " is null!");
			result.put(field, value.toString());
		}
		return result;
	}

	public IndexedDataRecord parse(int index, RecordType recordType, DataFormat dataFormat, String data) throws ParseException {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (data == null || data.trim().length() == 0)
			throw new NullPointerException("input data is null or empty!");
		Map<RecordFieldType, StringBuffer> values = new HashMap<RecordFieldType, StringBuffer>();
		BufferedReader reader = new BufferedReader(new StringReader(data));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				processLine(dataFormat, fields, line, values);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		IndexedDataRecord dataRecord = createDataRecord(index, dataFormat, recordType, values);
		return dataRecord;
	}

	private IndexedDataRecord createDataRecord(int index, DataFormat dataFormat,
			RecordType recordType, Map<RecordFieldType, StringBuffer> values) throws ParseException {
		IndexedDataRecord dataRecord = new IndexedDataRecord(recordType, fields, index);
		for (RecordFieldType recordField : values.keySet()) {
			String value = values.get(recordField).toString();
			if (isCollection(dataFormat, recordField)) {
				String separator = getSeparator(dataFormat, recordField);
				if (separator == null)
					separator = DEFAULT_SEPARATOR_REGEX;
				String[] elements = value.split(separator);
				ArrayList<String> list = new ArrayList<String>();
				for (String element : elements) {
					element = element.trim();
					if (list.contains(element) == false)
						list.add(element);
				}
				value = StringUtils.join(list, ", ");
			} else {
				value = value.trim();
			}
			dataRecord.getField(recordField).setValue(value);
		}

		return dataRecord;
	}

	private void processLine(DataFormat dataFormat,
			Set<RecordFieldType> recordFields, String line,
			Map<RecordFieldType, StringBuffer> values) {
		if (dataFormat == null)
			throw new NullPointerException("dataFormat is null!");
		if (recordFields == null)
			throw new NullPointerException("recordFields is null!");
		if (line == null)
			throw new NullPointerException("line is null!");
		if (values == null)
			throw new NullPointerException("values is null!");
		for (RecordFieldType recordField : recordFields) {
			Pattern pattern = getPattern(dataFormat, recordField);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				StringBuffer value = values.get(recordField);
				if (value == null)
					value = new StringBuffer();
				String currentMatch = matcher.group(1);
				if (currentMatch == null)
					continue;
				if (isCollection(dataFormat, recordField)) {
					String separator = getSeparator(dataFormat, recordField);
					if (separator == null && value.length() > 0)
						value.append(DEFAULT_SEPARATOR + currentMatch);
					else
						value.append(currentMatch);
				} else {
					if (value.length() == 0 || value.toString().endsWith(" ")
							|| value.toString().endsWith(",")
							|| value.toString().endsWith("-"))
						value.append(currentMatch);
					else
						value.append(" " + currentMatch);
				}
				values.put(recordField, value);
			}
		}
	}

	private boolean isCollection(DataFormat dataFormat, RecordFieldType recordField) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (recordField == null)
			throw new NullPointerException("RecordField is null!");
		if (collectionLookup.containsKey(dataFormat) == false)
			throw new NullPointerException(
					"No collection lookup map registered for " + dataFormat);
		if (collectionLookup.get(dataFormat).containsKey(recordField) == false)
			throw new NullPointerException(
					"No collection lookup registered for " + recordField);
		return collectionLookup.get(dataFormat).get(recordField);
	}

	private Pattern getPattern(DataFormat dataFormat, RecordFieldType recordField) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (recordField == null)
			throw new NullPointerException("RecordField is null!");
		if (patternLookup.containsKey(dataFormat) == false)
			throw new NullPointerException(
					"No pattern lookup map registered for " + dataFormat);
		if (patternLookup.get(dataFormat).containsKey(recordField) == false)
			throw new NullPointerException("No pattern registered for "
					+ recordField);
		return patternLookup.get(dataFormat).get(recordField);
	}

	/*
	private String getDateFormat(DataFormat dataFormat, RecordFieldType recordField) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (recordField == null)
			throw new NullPointerException("RecordField is null!");
		if (dateFormatLookup.containsKey(dataFormat) == false)
			throw new NullPointerException(
					"No dateFormat lookup map registered for " + dataFormat);
		if (dateFormatLookup.get(dataFormat).containsKey(recordField) == false)
			throw new NullPointerException("No dateFormat registered for "
					+ recordField);
		return dateFormatLookup.get(dataFormat).get(recordField);
	}
	*/

	private String getSeparator(DataFormat dataFormat, RecordFieldType recordField) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (recordField == null)
			throw new NullPointerException("RecordField is null!");
		if (separatorLookup.containsKey(dataFormat) == false)
			return null;
		if (separatorLookup.get(dataFormat).containsKey(recordField) == false)
			return null;
		return separatorLookup.get(dataFormat).get(recordField);
	}

	private static void addRecordFieldMapping(DataFormat dataFormat,
			RecordFieldType recordField, String regex, boolean isCollection,
			String separator, String dateFormat) {
		if (patternLookup.containsKey(dataFormat) == false)
			patternLookup.put(dataFormat, new HashMap<RecordFieldType, Pattern>());
		patternLookup.get(dataFormat).put(recordField, Pattern.compile(regex));
		if (collectionLookup.containsKey(dataFormat) == false)
			collectionLookup.put(dataFormat,
					new HashMap<RecordFieldType, Boolean>());
		collectionLookup.get(dataFormat).put(recordField, isCollection);
		if (separator != null) {
			if (separatorLookup.containsKey(separator) == false)
				separatorLookup.put(dataFormat,
						new HashMap<RecordFieldType, String>());
			separatorLookup.get(dataFormat).put(recordField, separator);
		}
		if (dateFormat != null) {
			if (dateFormatLookup.containsKey(dataFormat) == false)
				dateFormatLookup.put(dataFormat,
						new HashMap<RecordFieldType, String>());
			dateFormatLookup.get(dataFormat).put(recordField, dateFormat);
		}
		if (log.isDebugEnabled())
			log.debug("Adding RecordField: " + recordField + " DataFormat." + dataFormat
					+ " regex: " + regex + " isCollection: " + isCollection + " separator: " + separator
					+ " dateFormat: " + dateFormat);
	}

	private static void mapDataFormat(RecordType recordType, DataFormat dataFormat) {
		if (recordTypeLookup.containsKey(dataFormat) == false)
			recordTypeLookup.put(dataFormat, new HashSet<RecordType>());
		recordTypeLookup.get(dataFormat).add(recordType);	
		if (log.isDebugEnabled())
			log.debug("Map RecordType." + recordType + " to DataFormat." + dataFormat);
		
	}

	private static void init(Resource resource) {
		Document document = XMLHelper.parseXML(resource.getInputStream());
		Element flatfileParserConfigNode = XMLHelper.findNode(document,
				"FlatfileParserConfig");
		if (flatfileParserConfigNode == null)
			throw new NullPointerException(
					"Config file does not appear to be a valid FlatfileParser Configuration.");
		NodeList recordTypeNodes = XMLHelper.findNodes(
				flatfileParserConfigNode, "RecordType");
		for (int i = 0; i < recordTypeNodes.getLength(); i++) {
			Element recordTypeNode = (Element) recordTypeNodes.item(i);
			RecordType recordType = RecordType.valueOf(recordTypeNode
					.getAttribute("id"));
			NodeList dataFormatNodes = XMLHelper.findNodes(
					recordTypeNode, "DataFormat");
			for (int j = 0; j < dataFormatNodes.getLength(); j++) {
				Element dataFormatNode = (Element) dataFormatNodes.item(j);
				DataFormat dataFormat = DataFormat.valueOf(dataFormatNode
						.getAttribute("id"));
				mapDataFormat(recordType, dataFormat);
				NodeList recordFieldNodes = XMLHelper.findNodes(
						dataFormatNode, "RecordField");
				for (int k = 0; k < recordFieldNodes.getLength(); k++) {
					Element recordFieldNode = (Element) recordFieldNodes
							.item(k);
					RecordFieldType recordField = RecordFieldType
							.valueOf(recordFieldNode.getAttribute("id"));
					String regex = recordFieldNode.getAttribute("regex");
					String dateFormat = null;
					if (recordFieldNode.hasAttribute("dateFormat"))
						dateFormat = recordFieldNode.getAttribute("dateFormat");
					String separator = null;
					if (recordFieldNode.hasAttribute("separator"))
						separator = recordFieldNode.getAttribute("separator");
					Boolean isCollection = Boolean.valueOf(recordFieldNode
							.getAttribute("collection"));
					if (regex == null || regex.trim().length() == 0)
						throw new NullPointerException(
								"Missing regex attribute for recordField "
										+ recordField);
					if (isCollection == null)
						throw new NullPointerException(
								"Missing collection attribute for recordField "
										+ recordField);
					addRecordFieldMapping(dataFormat, recordField, regex,
							isCollection, separator, dateFormat);
				}
			}
		}
	}
}
