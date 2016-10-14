/**
 * 
 */
package org.ngbw.sdk.core;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.CoreRegistryBuilder;
import org.ngbw.sdk.api.core.SourceDocumentTransformer;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author hannes
 * 
 */
public class DefaultCoreRegistryBuilder implements CoreRegistryBuilder {

	private static Log log = LogFactory .getLog(DefaultCoreRegistryBuilder.class);
	private CoreRegistry coreRegistry;

	public CoreRegistry buildRegistry(Resource cfg) {
		coreRegistry = new DefaultCoreRegistry();
		Document document = XMLHelper.parseXML(cfg.getInputStream());
		Element coreRegistryNode = XMLHelper.findNode(document, "CoreRegistry");
		if (coreRegistryNode == null)
			throw new NullPointerException("CoreRegistry node is missing!");
		Element recordFieldsNode = XMLHelper.findNode(coreRegistryNode,
				"RecordFields");
		if (recordFieldsNode == null)
			throw new NullPointerException("RecordFields node is missing!");
		// make sure you register the RecordFields before the RecordType
		handleRecordFields(recordFieldsNode);
		Element recordTypesNode = XMLHelper.findNode(coreRegistryNode,
				"RecordTypes");
		if (recordTypesNode == null)
			throw new NullPointerException("RecordTypes node is missing!");
		handleRecordTypes(recordTypesNode);
		Element transformationsNode = XMLHelper.findNode(coreRegistryNode,
				"Transformations");
		if (transformationsNode == null)
			throw new NullPointerException("Transformations node is missing!");
		handleTransformations(transformationsNode);
		return coreRegistry;
	}

	// private methods //

	private void handleRecordFields(Element recordFieldsNode) {
		// process RecordField Nodes
		NodeList recordFieldNodes = recordFieldsNode
				.getElementsByTagName("RecordField");
		if (recordFieldNodes == null)
			throw new NullPointerException("RecordField nodes are missing!");
		if (log.isDebugEnabled())
			log.debug("Processing " + recordFieldNodes.getLength()
					+ " RecordFields");
		for (int i = 0; i < recordFieldNodes.getLength(); i++) {
			Element element = (Element) recordFieldNodes.item(i);
			if (element.hasAttribute("id") == false)
				throw new NullPointerException(
						"RecordField id attribute is missing!");
			String recordFieldId = element.getAttribute("id");
			if (element.hasAttribute("fieldType") == false)
				throw new NullPointerException(
						"RecordField fieldType attribute is missing!");
			String fieldTypeString = element.getAttribute("fieldType");
			RecordFieldType recordField = RecordFieldType.valueOf(recordFieldId);
			FieldDataType fieldType = FieldDataType.valueOf(fieldTypeString);
			if (log.isDebugEnabled())
				log.debug("Registered RecordField." + recordFieldId
						+ " : FieldType." + fieldTypeString);
			coreRegistry.registerRecordField(recordField, fieldType);
		}
	}

	private void handleRecordTypes(Element recordTypesNode) {
		// process RecordType Nodes
		NodeList recordTypeNodes = recordTypesNode
				.getElementsByTagName("RecordType");
		if (recordTypeNodes == null)
			throw new NullPointerException("RecordType nodes are missing!");
		if (log.isDebugEnabled())
			log.debug("Processing " + recordTypeNodes.getLength()
					+ " RecordTypes");
		for (int i = 0; i < recordTypeNodes.getLength(); i++) {
			Element recordTypeNode = (Element) recordTypeNodes.item(i);
			if (recordTypeNode.hasAttribute("id") == false)
				throw new NullPointerException(
						"RecordType id attribute is missing!");
			String recordTypeId = recordTypeNode.getAttribute("id");
			if (recordTypeNode.hasAttribute("entityType") == false)
				throw new NullPointerException(
						"RecordType entityType attribute is missing!");
			String entityTypeString = recordTypeNode.getAttribute("entityType");
			if (recordTypeNode.hasAttribute("dataType") == false)
				throw new NullPointerException(
						"RecordType dataType attribute is missing!");
			String dataTypeString = recordTypeNode.getAttribute("dataType");
			if (log.isDebugEnabled())
				log.debug("RecordType." + recordTypeId + ": EntityType."
						+ entityTypeString + " and DataType." + dataTypeString);
			RecordType extendedRecordType = null;
			if (recordTypeNode.hasAttribute("extends")) {
				extendedRecordType = RecordType.valueOf(recordTypeNode
						.getAttribute("extends"));
				if (log.isDebugEnabled())
					log.debug("RecordType." + recordTypeId + " extends " + extendedRecordType);
			}
			boolean isAbstract = false;
			if (recordTypeNode.hasAttribute("abstract")) {
				isAbstract = Boolean.parseBoolean(recordTypeNode
						.getAttribute("abstract"));
				if (log.isDebugEnabled())
					log.debug("RecordType." + recordTypeId + " has abstract attribute set to "
							+ isAbstract);
			}
			Set<RecordFieldType> fields = handleFields(recordTypeNode);
			RecordType recordType = RecordType.valueOf(recordTypeId);
			EntityType entityType = EntityType.valueOf(entityTypeString);
			DataType dataType = DataType.valueOf(dataTypeString);
			coreRegistry.registerRecordType(recordType, entityType, dataType,
					fields, isAbstract, extendedRecordType);
		}
	}

	private Set<RecordFieldType> handleFields(Element recordTypeNode) {
		String recordTypeId = recordTypeNode.getAttribute("id");
		NodeList fieldTypeNodes = recordTypeNode
				.getElementsByTagName("RecordField");
		if (fieldTypeNodes == null)
			throw new NullPointerException("RecordField nodes are missing!");
		Set<RecordFieldType> fields = new HashSet<RecordFieldType>(fieldTypeNodes
				.getLength());
		if (log.isDebugEnabled())
			log.debug("Processing " + fieldTypeNodes.getLength()
					+ " RecordFields for RecordType." + recordTypeId);
		for (int i = 0; i < fieldTypeNodes.getLength(); i++) {
			Element fieldTypeNode = (Element) fieldTypeNodes.item(i);
			if (fieldTypeNode.hasAttribute("name") == false)
				throw new NullPointerException(
						"RecordType id attribute is missing!");
			String recordFieldName = fieldTypeNode.getAttribute("name");
			RecordFieldType recordField = RecordFieldType.valueOf(recordFieldName);
			fields.add(recordField);
			if (log.isDebugEnabled())
				log.debug("RecordType." + recordTypeId + " : RecordField."
						+ recordFieldName);
		}
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	private void handleTransformations(Element transformationsNode) {
		// process Transformation Nodes
		NodeList transformationNodes = transformationsNode
				.getElementsByTagName("Transformation");
		if (transformationNodes == null)
			throw new NullPointerException("Transformation nodes are missing!");
		if (log.isDebugEnabled())
			log.debug("Processing " + transformationNodes.getLength()
					+ " Transformations");
		for (int i = 0; i < transformationNodes.getLength(); i++) {
			Element element = (Element) transformationNodes.item(i);
			if (element.hasAttribute("sourceRecordType") == false)
				throw new NullPointerException(
						"Transformation sourceRecordType attribute is missing!");
			RecordType sourceRecordType = RecordType.valueOf(element.getAttribute("sourceRecordType"));
			if (element.hasAttribute("targetRecordType") == false)
				throw new NullPointerException(
						"Transformation targetRecordType attribute is missing!");
			RecordType targetRecordType = RecordType.valueOf(element.getAttribute("targetRecordType"));
			if (element.hasAttribute("dataFormat") == false)
				throw new NullPointerException(
						"Transformation dataFormat attribute is missing!");
			DataFormat dataFormat = DataFormat.valueOf(element.getAttribute("dataFormat"));
			if (element.hasAttribute("class") == false)
				throw new NullPointerException(
						"Transformation class attribute is missing!");
			String className = element.getAttribute("class");
			Class<SourceDocumentTransformer> transformerClass;
			try {
				transformerClass = (Class<SourceDocumentTransformer>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.toString(), e);
			}
			if (log.isDebugEnabled())
				log.debug("Registered RecordType." + sourceRecordType
						+ " transformation to RecordType." + transformerClass);
			coreRegistry.registerTransformerClass(sourceRecordType, dataFormat, targetRecordType, transformerClass);
		}
	}
}
