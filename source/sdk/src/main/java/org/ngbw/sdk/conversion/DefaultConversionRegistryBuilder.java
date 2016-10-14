/*
 * DefaultConversionRegistryBuilder.java
 */
package org.ngbw.sdk.conversion;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.ConversionRegistryBuilder;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SequenceParser;
import org.ngbw.sdk.api.conversion.SourceDocumentConverter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultConversionRegistryBuilder implements
		ConversionRegistryBuilder {

	private static Log log = LogFactory.getLog(DefaultConversionRegistryBuilder.class);
	private ConversionRegistry conversionRegistry;

	public ConversionRegistry buildRegistry(Resource cfg,
			CoreRegistry coreRegistry) {
		conversionRegistry = new DefaultConversionRegistry(coreRegistry);
		Document document = XMLHelper.parseXML(cfg.getInputStream());
		Element conversionRegistryNode = XMLHelper.findNode(document, "ConversionRegistry");
		handleConversionRegistry(conversionRegistryNode);
		return conversionRegistry;
	}
	
	private void handleConversionRegistry(Element conversionRegistryNode) {
		//process DataFormatReader Nodes
		NodeList readerNodes = conversionRegistryNode.getElementsByTagName("SourceDocumentReader");
		if(log.isDebugEnabled()) log.debug("Processing " + readerNodes.getLength() + " SourceDocumentReaders");
		if(readerNodes == null) return;
		for(int i=0; i<readerNodes.getLength(); i++) {
			Element readerNode = (Element) readerNodes.item(i);
			handleReader(readerNode);
		}

		//process DataFormatConverter Nodes
		NodeList converterNodes = conversionRegistryNode.getElementsByTagName("SourceDocumentConverter");
		if(log.isDebugEnabled()) log.debug("Processing " + converterNodes.getLength() + " SourceDocumentConverters");
		if(converterNodes == null) return;
		for(int i=0; i<converterNodes.getLength(); i++) {
			Element converterNode = (Element) converterNodes.item(i);
			handleConverter(converterNode);
		}

		//process RecordFilter Nodes
		NodeList filterNodes = conversionRegistryNode.getElementsByTagName("RecordFilter");
		if(log.isDebugEnabled()) log.debug("Processing " + filterNodes.getLength() + " RecordFilters");
		if(filterNodes == null) return;
		for(int i=0; i<filterNodes.getLength(); i++) {
			Element filterNode = (Element) filterNodes.item(i);
			handleFilter(filterNode);
		}

		//process RecordFilter Nodes
		NodeList sequenceParserNodes = conversionRegistryNode.getElementsByTagName("SequenceParser");
		if(log.isDebugEnabled()) log.debug("Processing " + sequenceParserNodes.getLength() + " SequenceParsers");
		if(sequenceParserNodes == null) return;
		for(int i=0; i<sequenceParserNodes.getLength(); i++) {
			Element sequenceParserNode = (Element) sequenceParserNodes.item(i);
			handleSequenceParser(sequenceParserNode);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleSequenceParser(Element sequenceParserNode) {
		String className = sequenceParserNode.getAttribute("class");
		NodeList dataFormatNodes = sequenceParserNode.getElementsByTagName("DataFormat");
		Class<SequenceParser> parserClass;
		try {
			parserClass = (Class<SequenceParser>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("SequenceParser class " + className + " not found.", e);
		}
		if(log.isDebugEnabled()) log.debug("Processing " + dataFormatNodes.getLength() 
				+ " Pattern nodes for SequenceParser " + className);
		for(int i=0; i<dataFormatNodes.getLength(); i++) {
			Element dataFormatNode = (Element) dataFormatNodes.item(i);
			if (dataFormatNode.hasAttribute("id") == false)
				throw new NullPointerException("Missing dataFormat attribute for Pattern node!");
			DataFormat dataFormat = DataFormat.valueOf(dataFormatNode.getAttribute("id"));
			conversionRegistry.registerSequenceParserClass(parserClass, dataFormat);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleFilter(Element filterNode) {
		String className = filterNode.getAttribute("class");
		NodeList dataFormatNodes = filterNode.getElementsByTagName("DataFormat");
		Class<RecordFilter> filterClass;
		try {
			filterClass = (Class<RecordFilter>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("RecordFilter class " + className + " not found.", e);
		}
		if(log.isDebugEnabled()) log.debug("Processing " + dataFormatNodes.getLength() 
				+ " DataFormat nodes for RecordFilter " + className);
		for(int i=0; i<dataFormatNodes.getLength(); i++) {
			Element dataFormatNode = (Element) dataFormatNodes.item(i);
			if (dataFormatNode.hasAttribute("id") == false)
				throw new NullPointerException("Missing dataFormat attribute for Pattern node!");
			DataFormat dataFormat = DataFormat.valueOf(dataFormatNode.getAttribute("id"));
			conversionRegistry.registerRecordFilterClass(filterClass, dataFormat);
		}// end for DataFormat nodes
	}

	@SuppressWarnings("unchecked")
	private void handleConverter(Element converterNode) {
		if (converterNode == null)
			throw new NullPointerException("converterNode cannot be null!");
		String className = converterNode.getAttribute("class");
		Class<SourceDocumentConverter> converterClass;
		try {
			converterClass = (Class<SourceDocumentConverter>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("SourceDocumentConverters class " + className + " not found.", e);
		}
		conversionRegistry.registerConverterClass(converterClass);
	}

	@SuppressWarnings("unchecked")
	private void handleReader(Element readerNode) {
		if (readerNode == null)
			throw new NullPointerException("readerNode cannot be null!");
		if (readerNode.hasAttribute("class") == false)
			throw new RuntimeException("SourceDocumentReader class attribute is missing!");
		String className = readerNode.getAttribute("class");
		Class<SourceDocumentReader> readerClass;
		try {
			readerClass = (Class<SourceDocumentReader>) Class.forName(className);
		}  catch (ClassNotFoundException e) { 
			throw new RuntimeException("Could not find: " + className, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException for: " + className, e);
		} catch (SecurityException e) {
			throw new RuntimeException("SecurityException for: " + className, e);
		}
		//process RecordFilter Nodes
		NodeList typeNodes = readerNode.getElementsByTagName("SourceDocumentType");
		if(log.isDebugEnabled()) log.debug("Processing " + typeNodes.getLength() + " SourceDocumentTypes");
		if(typeNodes == null) 
			throw new RuntimeException("Missing SourceDocumentType nodes!");
		for(int i=0; i<typeNodes.getLength(); i++) {
			Element typeNode = (Element) typeNodes.item(i);
			SourceDocumentType type = handleSourceDocumentType(typeNode);
			conversionRegistry.registerReaderClass(readerClass, type);
		}
	}

	private SourceDocumentType handleSourceDocumentType(Element typeNode) {
		if (typeNode.hasAttribute("entityType") == false)
			throw new RuntimeException("SourceDocumentType entityType attribute is missing!");
		if (typeNode.hasAttribute("dataType") == false)
			throw new RuntimeException("SourceDocumentType dataType attribute is missing!");
		if (typeNode.hasAttribute("dataFormat") == false)
			throw new RuntimeException("SourceDocumentType dataFormat attribute is missing!");
		EntityType entityType = EntityType.valueOf(typeNode.getAttribute("entityType"));
		DataType dataType = DataType.valueOf(typeNode.getAttribute("dataType"));
		DataFormat dataFormat = DataFormat.valueOf(typeNode.getAttribute("dataFormat"));
		SourceDocumentType type = new SourceDocumentType(entityType, dataType, dataFormat);
		return type;
	}
}
