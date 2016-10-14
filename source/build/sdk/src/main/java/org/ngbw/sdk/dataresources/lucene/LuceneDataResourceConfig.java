package org.ngbw.sdk.dataresources.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LuceneDataResourceConfig {

	private static Log log = LogFactory.getLog(LuceneDataResourceConfig.class);

	String DATARESOURCE_ID;
	String INDEX_FILE_PREFIX;
	String INDEX_ROOT_DIRECTORY;
	private String localhost;
	private String host;
	private Map<Dataset, Resource> datasetSourcesLookup = new HashMap<Dataset, Resource>();
	private Map<Dataset, String> datasetSourceFileLookup = new HashMap<Dataset, String>();
	private Map<Dataset, Boolean> multipleFilesLookup = new HashMap<Dataset, Boolean>();
	private Map<Dataset, String> indexLocationLookup = new HashMap<Dataset, String>();
	private Map<RecordType, Map<String, IndexFieldType>> fieldMapLookup = new HashMap<RecordType, Map<String, IndexFieldType>>();
	//private Map<RecordType, Set<Dataset>> recordTypeDatasetLookup = new HashMap<RecordType, Set<Dataset>>();
	private Map<DataFormat, String> startPatternLookup = new HashMap<DataFormat, String>();
	private Map<DataFormat, String> endPatternLookup = new HashMap<DataFormat, String>();

	public LuceneDataResourceConfig() {
		// where am I
		try {
			localhost = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			localhost = "localhost";
		}
		if (log.isInfoEnabled()) log.info("JVM host is: " + localhost);
	}

	public LuceneDataResourceConfig(Resource config) {
		this();
		readConfigFile(config);
	}

	@SuppressWarnings("unchecked")
	public void readConfigFile(Resource configFile) {
		InputStream is = configFile.getInputStream();
		Element cfgNode = XMLHelper.findNode(XMLHelper.parseXML(is),
				"DataResourceConfiguration");
		if (cfgNode == null)
			throw new NullPointerException("No DataResourceConfiguration node!");

		Element dataResourceNode = XMLHelper.findNode(cfgNode, "DataResource");
		if (dataResourceNode == null)
			throw new NullPointerException("No DataResource node!");
		if (dataResourceNode.hasAttribute("id") == false)
			throw new NullPointerException("No DataResource id attribute!");
		DATARESOURCE_ID = dataResourceNode.getAttribute("id");
		if (log.isInfoEnabled())
			log.info("DATARESOURCE_ID: " + DATARESOURCE_ID);
		if (dataResourceNode.hasAttribute("indexRoot") == false)
			throw new NullPointerException("No DataResource indexRoot attribute!");
		INDEX_ROOT_DIRECTORY = dataResourceNode.getAttribute("indexRoot");
		if (log.isInfoEnabled())
			log.info("INDEX_ROOT_DIRECTORY: " + INDEX_ROOT_DIRECTORY);
		// getting host for index location (may be remote)
		if (dataResourceNode.hasAttribute("host") == false)
			throw new NullPointerException(
					"DataResource host attribute cannot be null!");
		String host = dataResourceNode.getAttribute("host");
		setHost(host);

		// Processing the RecordFieldMapping nodes - needs to run before
		// DataResource processing
		Element recordFieldMappingsNode = XMLHelper.findNode(cfgNode,
				"RecordFieldMappings");
		NodeList recordTypeNodes = recordFieldMappingsNode
				.getElementsByTagName("RecordType");
		if (log.isDebugEnabled())
			log.debug("Processing " + recordTypeNodes.getLength()
					+ " RecordField Mappings");
		if (recordTypeNodes == null)
			throw new NullPointerException("No RecordFielMapping nodes!");
		for (int i = 0; i < recordTypeNodes.getLength(); i++) {
			Element recordTypeNode = (Element) recordTypeNodes.item(i);
			String recordTypeId = recordTypeNode.getAttribute("id");
			if (recordTypeId == null || recordTypeId.trim().length() == 0)
				throw new NullPointerException("No RecordType id attribute!");
			RecordType recordType;
			try {
				recordType = RecordType.valueOf(recordTypeId);
			} catch (Exception e) {
				throw new NullPointerException("No such RecordType: " + recordTypeId);
			}
			Map<String, IndexFieldType> mapping = new HashMap<String, IndexFieldType>();
			NodeList recordFields = recordTypeNode.getElementsByTagName("RecordField");
			if (recordFields == null)
				throw new NullPointerException("No RecordField nodes!");
			for (int j = 0; j < recordFields.getLength(); j++) {
				Element fieldNode = (Element) recordFields.item(j);
				String field = fieldNode.getAttribute("name");
				String value = fieldNode.getAttribute("indexFieldType");
				IndexFieldType indexFieldType = IndexFieldType.valueOf(value);
				mapping.put(field, indexFieldType);
			}
			setFieldMapping(recordType, mapping);
		}

		// Processing RecordSourceFilterPatterns nodes
		Element filterPatternsNode = XMLHelper.findNode(cfgNode,
				"RecordSourceFilterPatterns");
		NodeList patternNodes = filterPatternsNode
				.getElementsByTagName("Pattern");
		if (log.isDebugEnabled())
			log.debug("Processing " + patternNodes.getLength()
					+ " Filter Patterns");
		if (patternNodes == null)
			throw new NullPointerException("No Pattern nodes!");
		for (int i = 0; i < patternNodes.getLength(); i++) {
			Element patternNode = (Element) patternNodes.item(i);
			String dataFormatAttr = patternNode.getAttribute("dataFormat");
			if (dataFormatAttr == null || dataFormatAttr.trim().length() == 0)
				throw new NullPointerException("No dataFormat attribute!");
			DataFormat dataFormat = DataFormat.valueOf(dataFormatAttr);
			String startPattern = patternNode.getAttribute("startPattern");
			String endPattern = patternNode.getAttribute("endPattern");
			this.addSourceFilterPatterns(dataFormat, startPattern, endPattern);
		}

		NodeList datasetNodes = cfgNode.getElementsByTagName("Dataset");
		if (log.isDebugEnabled())
			log.debug("Processing " + datasetNodes.getLength() + " Datasets");
		if (datasetNodes == null)
			throw new NullPointerException("No Dataset node!");
		// process Dataset Nodes
		for (int i = 0; i < datasetNodes.getLength(); i++) {
			Element datasetNode = (Element) datasetNodes.item(i);
			// resolve the Dataset
			if (datasetNode.hasAttribute("id") == false)
				throw new NullPointerException(
						"Missing datasetId attribute value!");
			String datasetId = datasetNode.getAttribute("id");
			Dataset dataset = Dataset.valueOf(datasetId);
			// where are the source files for the Indexer
			if (datasetNode.hasAttribute("sourceURL") == false)
				throw new NullPointerException("Missing Dataset sourceURL attribute value!");
			String url = datasetNode.getAttribute("sourceURL");
			Resource resource = Resource.getUnvalidatedResource(url);
			addSourceLocation(dataset, resource);
			// what is the actual file name or wildcard
			if (datasetNode.hasAttribute("file") == false)
				throw new NullPointerException("Missing Dataset file attribute value!");
			String file = datasetNode.getAttribute("file");
			addSourceFile(dataset, file);
			// more than 1 file at the source location
			String multipleFilesAttr = datasetNode
					.getAttribute("multipleFiles");
			Boolean multipleFiles;
			if (multipleFilesAttr == null
					|| multipleFilesAttr.trim().length() == 0)
				multipleFiles = false;
			else
				multipleFiles = Boolean.valueOf(multipleFilesAttr);
			setMultipleFilesFlag(dataset, multipleFiles);
			// which indexDirectory to use for this Dataset's index
			if (datasetNode.hasAttribute("indexDirectory") == false)
				throw new NullPointerException(
						"Dataset indexDirectory attribute cannot be null!");
			String indexDirectory = datasetNode.getAttribute("indexDirectory");
			setIndexSubdirectory(dataset, indexDirectory);
//
//			// map Dataset to RecordType
//			if (datasetNode.hasAttribute("recordType") == false)
//				throw new NullPointerException(
//						"Dataset recordType attribute cannot be null!");
//			RecordType recordType = RecordType.valueOf(datasetNode.getAttribute("recordType"));
//			addDatasetToRecordType(dataset, recordType);
		}
	}

	public void addSourceFile(Dataset dataset, String file) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		if (file == null)
			throw new NullPointerException("file must not be null!");
		datasetSourceFileLookup.put(dataset, file);
	}

	public void setDataResourceId(String dataResourceId) {
		if (dataResourceId == null)
			throw new NullPointerException("dataResourceId must not be null!");
		this.DATARESOURCE_ID = dataResourceId;
	}

	public void addSourceLocation(Dataset dataset, Resource resource) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		if (resource == null)
			throw new NullPointerException("Resource must not be null!");
		datasetSourcesLookup.put(dataset, resource);
	}

	public Resource getSourceLocation(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		if (datasetSourcesLookup.containsKey(dataset) == false)
			throw new NullPointerException("No source location registered for " + dataset);
		return datasetSourcesLookup.get(dataset);
	}

	public String[] getSourceFileNames(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		if (datasetSourceFileLookup.containsKey(dataset) == false)
			throw new NullPointerException("No file registered for " + dataset);
		String filePattern = datasetSourceFileLookup.get(dataset);
		Resource res = getSourceLocation(dataset);
		return res.listDirectoryEntries(filePattern);
	}

	public boolean hasMultipleFiles(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		if (datasetSourcesLookup.containsKey(dataset) == false)
			throw new NullPointerException("No value registered for " + dataset);
		Boolean multipleFiles = multipleFilesLookup.get(dataset);
		if (multipleFiles == null)
			throw new NullPointerException("No value set for " + dataset);
		return multipleFiles;
	}

	public void setMultipleFilesFlag(Dataset dataset, boolean multipleFiles) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		multipleFilesLookup.put(dataset, multipleFiles);
		if (log.isInfoEnabled())
			log.info("setMultipleFilesFlag: " + dataset + " : " + multipleFiles);
	}
	
	public void setIndexSubdirectory(Dataset dataset, String indexLocation) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (indexLocation == null)
			throw new NullPointerException("indexLocation is null!");
		indexLocationLookup.put(dataset, indexLocation);
		if (log.isInfoEnabled())
				log.info("index subdirectory: " + dataset + " : " + indexLocation);
	}

	public Directory getIndexDirectory(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		String indexLocation = indexLocationLookup.get(dataset);
		if (indexLocation == null)
			throw new NullPointerException("Index location is null for "
					+ dataset);
		indexLocation = INDEX_ROOT_DIRECTORY + indexLocation;
		if (indexLocation.endsWith("/") == false)
			indexLocation += "/";
		indexLocation += "FIELD_INDEX/";
		Directory indexDirectory;
		try {
			if ("localhost".equals(host) || "127.0.0.1".equals(host) || localhost.equals(host))
				indexDirectory = FSDirectory.getDirectory(indexLocation);
			else {
				indexDirectory = SFTPDirectory.getDirectory(indexLocation, host);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot access index location: "
					+ indexLocation, e);
		}
		return indexDirectory;
	}

	public Directory getSourceIndexDirectory(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset must not be null!");
		String indexLocation = indexLocationLookup.get(dataset);
		if (indexLocation == null)
			throw new NullPointerException("Index location is null for "
					+ dataset);
		indexLocation = INDEX_ROOT_DIRECTORY + indexLocation;
		if (indexLocation.endsWith("/") == false)
			indexLocation += "/";
		// add the SOURCE suffix to the indexLocation
		indexLocation += "SOURCE_INDEX/";
		Directory indexDirectory;
		try {
			if ("localhost".equals(host) || "127.0.0.1".equals(host) || localhost.equals(host))
				indexDirectory = FSDirectory.getDirectory(indexLocation);
			else {
				indexDirectory = SFTPDirectory.getDirectory(indexLocation, host);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot access index location: "
					+ indexLocation, e);
		}
		return indexDirectory;
	}

	public void setHost(String host) {
		if (host == null)
			throw new NullPointerException("host must not be null!");
		this.host = host;
		if (log.isInfoEnabled())
			log.info("setHost:" + host);
	}

	public Set<Dataset> getDatasets() {
		return indexLocationLookup.keySet();
	}

	public Map<String, IndexFieldType> getFieldMappings(RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (fieldMapLookup.containsKey(recordType) == false)
			throw new NullPointerException("No FieldMapping for recordType "
					+ recordType);
		return fieldMapLookup.get(recordType);
	}

	public void setFieldMapping(RecordType recordType,
			Map<String, IndexFieldType> fieldMapping) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (fieldMapping == null || fieldMapping.isEmpty())
			throw new NullPointerException("fieldMapping is null or empty!");
		fieldMapLookup.put(recordType, fieldMapping);
		if (log.isInfoEnabled()) {
			log.info("Register index type mapping for " + recordType);
			for (String field : fieldMapping.keySet()) 
				log.info(field + " : " + fieldMapping.get(field));
		}
	}

	public void addSourceFilterPatterns(DataFormat dataFormat,
			String startPattern, String endPattern) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		startPatternLookup.put(dataFormat, startPattern);
		endPatternLookup.put(dataFormat, endPattern);
		if (log.isInfoEnabled())
			log.info(dataFormat + " start pattern \"" + startPattern + "\" end pattern \"" 
					+ endPattern + "\"");
	}

	public RecordSourceFilter getRecordSourceFilter(DataFormat dataFormat) {
		String startPattern = startPatternLookup.get(dataFormat);
		String endPattern = endPatternLookup.get(dataFormat);
		RecordSourceFilter filter = new RecordSourceFilter(startPattern,
				endPattern);
		return filter;
	}
}
