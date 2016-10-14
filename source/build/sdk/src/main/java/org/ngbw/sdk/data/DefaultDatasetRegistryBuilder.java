/*
 * DefaultDatasetRegistryBuilder.java
 */
package org.ngbw.sdk.data;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DatasetConfig;
import org.ngbw.sdk.api.data.DatasetRegistry;
import org.ngbw.sdk.api.data.DatasetRegistryBuilder;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultDatasetRegistryBuilder implements DatasetRegistryBuilder {

	private static Log log = LogFactory.getLog(DefaultDatasetRegistryBuilder.class);
	
	DatasetRegistry dataRegistry;
	Map<String, Map<RecordFieldType, String>> recordFieldMappings =  new HashMap<String, Map<RecordFieldType, String>>();
	Map<String, SourceDocumentType> types =  new HashMap<String, SourceDocumentType>();

	public DatasetRegistry buildRegistry(Resource cfg, CoreRegistry coreRegistry) {
		dataRegistry = new DefaultDatasetRegistry(coreRegistry);
		Document document = XMLHelper.parseXML(cfg.getInputStream());
		Element dataRegistryNode = XMLHelper.findNode(document, "DatasetRegistry");
		handleDataRegistryNode(dataRegistryNode);
		return dataRegistry;
	}
	
	private void handleDataRegistryNode(Element dataRegistryNode) {
		
		//Processing the DataResource nodes - needs to run before Dataset processing
		NodeList dataResources = dataRegistryNode.getElementsByTagName("DataResource");
		if(log.isDebugEnabled()) log.debug("Processing " + dataResources.getLength() + " DataResources");
		if(dataResources == null) return;
		for(int i=0; i<dataResources.getLength(); i++) {
			Element element = (Element) dataResources.item(i);
			String dataResourceId = element.getAttribute("id");
			if(log.isDebugEnabled()) log.debug("Dispatching DataResource " + dataResourceId);
			handleDataResource(element);
		}

		//Processing the SourceDocumentType nodes
		NodeList sdtNodes = dataRegistryNode.getElementsByTagName("SourceDocumentType");
		if(log.isDebugEnabled()) log.debug("Processing " + sdtNodes.getLength() + " SourceDocumentTypes");
		for(int i=0; i<sdtNodes.getLength(); i++) {
			Element sdtNode = (Element) sdtNodes.item(i);
			String type = sdtNode.getAttribute("id");
			EntityType entityType = EntityType.valueOf(sdtNode.getAttribute("entityType"));
			DataType dataType = DataType.valueOf(sdtNode.getAttribute("dataType"));
			DataFormat dataFormat = DataFormat.valueOf(sdtNode.getAttribute("dataFormat"));
			if(log.isDebugEnabled()) log.debug("SourceDocumentType " + type 
					+ ": EntityType." + entityType
					+ " | DataType." + dataType
					+ " | DataFormat." + dataFormat);
			SourceDocumentType sdt = new SourceDocumentType(entityType, dataType, dataFormat);
			types.put(type, sdt);
		}

		//Processing the Dataset nodes
		NodeList dsNodes = dataRegistryNode.getElementsByTagName("Dataset");
		for(int i=0; i<dsNodes.getLength(); i++) {
			Element dsNode = (Element) dsNodes.item(i);
			String datasetName = dsNode.getAttribute("id");
			if(log.isDebugEnabled()) log.debug("Dispatching Dataset " + datasetName);
			DatasetConfig dsc = handleDataset(dsNode);
			dataRegistry.registerDataset(dsc);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void handleDataResource(Element element) {
		if (element == null)
			throw new NullPointerException("Element cannot be null!");
		if(log.isDebugEnabled()) log.debug("Handling node " + element.getNodeName());
		String dataResourceId = element.getAttribute("id");
		String configFile = element.getAttribute("configFile");
		String className = element.getAttribute("class");
		
		Class<DataResource> dataResourceClass;
		try {
			dataResourceClass = (Class<DataResource>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class " + className + " not found.", e);
		}
		dataRegistry.registerDataResource(dataResourceId, dataResourceClass, configFile);
		if(log.isDebugEnabled()) log.debug("Registered DataResource " + dataResourceId);
	}
	
	private DatasetConfig handleDataset(Element dsNode) {
		if (dsNode == null)
			throw new NullPointerException("Element cannot be null!");
		if(log.isDebugEnabled()) log.debug("Handling node " + dsNode.getNodeName());
		String id = dsNode.getAttribute("id");
		if (id == null)
			throw new NullPointerException("Dataset id attribute cannot be null!");
		Dataset dataset = Dataset.valueOf(id);
		if(log.isDebugEnabled()) log.debug("Dataset: " + dataset);
		DatasetConfig dsc = dataRegistry.getDatasetConfig(dataset);
		String name = dsNode.getAttribute("name");
		if (name == null || name.trim().length() ==0)
			throw new NullPointerException("Dataset name attribute cannot be null!");
		if(log.isDebugEnabled()) log.debug("Dataset name: " + name);
		dsc.setName(name);
		String sourceDocumentTypeString = dsNode.getAttribute("sourceDocumentType");
		if (sourceDocumentTypeString == null || sourceDocumentTypeString.trim().length() ==0)
			throw new NullPointerException("Dataset sourceDocumentType attribute cannot be null!");
		if(log.isDebugEnabled()) log.debug("Dataset sourceDocumentType attribute: " + sourceDocumentTypeString);
		SourceDocumentType sourceDocumentType = types.get(sourceDocumentTypeString);
		if (sourceDocumentType == null)
			throw new NullPointerException("No SourceDocumentType found for: " + sourceDocumentTypeString);
		dsc.setSourceDocumentType(sourceDocumentType);
		String dataResource = dsNode.getAttribute("dataResource");
		if (dataResource == null || dataResource.trim().length() ==0)
			throw new NullPointerException("Dataset dataResource attribute cannot be null!");
		if(log.isDebugEnabled()) log.debug("Dataset dataResource: " + name);
		dsc.setDataResourceId(dataResource);
		return dsc;
	}
}
