/*
 * DefaultConversionRegistry.java
 */
package org.ngbw.sdk.conversion;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.ConversionService;
import org.ngbw.sdk.api.conversion.MockSourceDocumentReader;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SequenceParser;
import org.ngbw.sdk.api.conversion.SourceDocumentConverter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.shared.SourceDocumentType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultConversionRegistry implements ConversionRegistry {

	private static Log log = LogFactory.getLog(DefaultConversionRegistry.class);
	private CoreRegistry coreRegistry;
	private Map<SourceDocumentType, Class<SourceDocumentReader>> readerClasses = new HashMap<SourceDocumentType, Class<SourceDocumentReader>>();
	private Map<SourceDocumentType, Map<SourceDocumentType, Class<SourceDocumentConverter>>> converterClasses = new TreeMap<SourceDocumentType, Map<SourceDocumentType, Class<SourceDocumentConverter>>>();
	private Map<DataFormat, Class<RecordFilter>> recordFilterClasses = new HashMap<DataFormat, Class<RecordFilter>>();
	private Map<DataFormat, Class<SequenceParser>> sequenceParserClasses = new HashMap<DataFormat, Class<SequenceParser>>();

	public DefaultConversionRegistry(CoreRegistry coreRegistry) {
		this.coreRegistry = coreRegistry;
	}

	public CoreRegistry getCoreRegistry() {
		return coreRegistry;
	}

	public ConversionService getConversionService() {
		return new DefaultConversionService(this);
	}

	public Set<SourceDocumentType> getRegisteredDocumentTypes() {
		return readerClasses.keySet();
	}

	public Set<SourceDocumentType> getReadableDocumentTypes() {
		Set<SourceDocumentType> types = new HashSet<SourceDocumentType>();
		for (SourceDocumentType type : readerClasses.keySet()) {
			if (MockSourceDocumentReader.class.equals(readerClasses.get(type)) == false
					&& UnknownSourceDocumentReader.class.equals(readerClasses.get(type)) == false)
				types.add(type);
		}
		return types;
	}

	public boolean hasConverterFrom(SourceDocumentType sourceType) {
		return converterClasses.containsKey(sourceType);
	}

	public Set<SourceDocumentType> getTargetSourceDocumentTypes(SourceDocumentType srcType) {
		Map<SourceDocumentType, Class<SourceDocumentConverter>> converters = converterClasses.get(srcType);

		if (converters == null)
			return null;

		return converters.keySet();
	}

	public boolean hasConverterTo(SourceDocumentType targetType) {
		for (Iterator<Map<SourceDocumentType, Class<SourceDocumentConverter>>> values = converterClasses.values().iterator() ; values.hasNext() ; ) {
			Map<SourceDocumentType, Class<SourceDocumentConverter>> targetMap = values.next();

			if (targetMap.containsKey(targetType))
				return true;
		}

		return false;
	}

	public boolean hasConverter(SourceDocumentType sourceType, SourceDocumentType targetType) {
		if (sourceType == null)
			throw new NullPointerException("sourceType is null!");

		if (targetType == null)
			throw new NullPointerException("targetType is null!");

		Map<SourceDocumentType, Class<SourceDocumentConverter>> converters = converterClasses.get(sourceType);

		if (converters == null)
			return false;

		return converters.containsKey(targetType);
	}

	public SourceDocumentConverter getConverter(SourceDocumentType sourceType, SourceDocumentType targetType) {
		if (sourceType == null)
			throw new NullPointerException("sourceType is null!");
		if (targetType == null)
			throw new NullPointerException("targetType is null!");
		if(sourceType.equals(targetType))
			throw new RuntimeException("Why do you need to convert identical SourceDocumentTypes "
					+ sourceType + " and target SourceDocumentType " + targetType);
		if(hasConverter(sourceType, targetType) == false)
			throw new NullPointerException("No Converter Registered for source SourceDocumentType "
					+ sourceType + " and target SourceDocumentType "
					+ targetType);
		SourceDocumentConverter converter = getConverterInstance(sourceType, targetType);
		if(log.isDebugEnabled()) log.debug(sourceType + " -> "+ targetType + " return Converter " + converter.getClass().getName());
		return converter;
	}

	public boolean hasReader(SourceDocumentType sourceDocumentType) {
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");
		if (readerClasses.containsKey(sourceDocumentType))
			if (MockSourceDocumentReader.class.equals(readerClasses.get(sourceDocumentType)))
				return false;
		return readerClasses.containsKey(sourceDocumentType);
	}

	public SourceDocumentReader getReader(SourceDocumentType sourceType) {
		if (sourceType == null)
			throw new NullPointerException("SourceDocumentType is null!");
		if(hasReader(sourceType) == false)
			throw new NullPointerException("No Reader Registered for SourceDocumentType " + sourceType);
		SourceDocumentReader reader = getReaderInstance(sourceType);
		if(log.isDebugEnabled()) log.debug(sourceType + " return Reader " + reader.getClass().getName());
		return reader;
	}

	public void registerConverterClass(Class<SourceDocumentConverter> converterClass) {
		if (converterClass == null)
			throw new NullPointerException("converterClass is null!");

		SourceDocumentConverter converter = getConverterInstance(converterClass);
		Map<SourceDocumentType, Set<SourceDocumentType>> conversions = converter.getSupportedConversions();

		for (Iterator<Map.Entry<SourceDocumentType, Set<SourceDocumentType>>> mapNodes = conversions.entrySet().iterator() ; mapNodes.hasNext() ; ) {
			Map.Entry<SourceDocumentType, Set<SourceDocumentType>> node = mapNodes.next();
			SourceDocumentType sourceType = node.getKey();
			Map<SourceDocumentType, Class<SourceDocumentConverter>> converters = converterClasses.get(sourceType);

			if (converters == null) {
				converters = new TreeMap<SourceDocumentType, Class<SourceDocumentConverter>>();

				converterClasses.put(sourceType, converters);
			}

			for (Iterator<SourceDocumentType> targets = node.getValue().iterator() ; targets.hasNext() ; ) {
				SourceDocumentType targetType = targets.next();

				converters.put(targetType, converterClass);

				log.info("Conversion: " + sourceType + " to " + targetType + " converterClass " + converterClass.getName());
			}
		}
	}

	public void registerReaderClass(Class<SourceDocumentReader> readerClass, SourceDocumentType sourceDocumentType) {
		if (readerClass == null)
			throw new NullPointerException("readerClass is null!");
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");
		if (MockSourceDocumentReader.class.equals(readerClass) || UnknownSourceDocumentReader.class.equals(readerClass) ||
				getReaderInstance(readerClass).getSupportedTypes().contains(sourceDocumentType))
			readerClasses.put(sourceDocumentType, readerClass);
		else
			throw new RuntimeException("SourceDocumentReader " + readerClass.getName()
					+ " does not support SourceDocumentType: " + sourceDocumentType);
		if(log.isInfoEnabled()) log.info("Registered SourceDocumentReader for " + sourceDocumentType 
				+ " : " + readerClass.getName());
	}

	public void registerRecordFilterClass(Class<RecordFilter> filterClass, DataFormat dataFormat) {
		if (filterClass == null)
			throw new NullPointerException("filterClass is null!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		recordFilterClasses.put(dataFormat, filterClass);
		if(log.isInfoEnabled()) log.info("Registered RecordFilter for " + dataFormat 
				+ " : " + filterClass.getName());
	}

	public Set<DataFormat> getFilterableDataFormats() {
		return recordFilterClasses.keySet();
	}

	public RecordFilter getRecordFilter(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (recordFilterClasses.containsKey(dataFormat) == false)
			throw new NullPointerException("No RecordFilter is registered for DataFormat." + dataFormat);
		Class<RecordFilter> filterClass = recordFilterClasses.get(dataFormat);
		RecordFilter filter;
		if(log.isDebugEnabled()) log.debug(dataFormat + " return: " + filterClass.getName());
		try {
			filter = (RecordFilter) filterClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("RecordFilter " + filterClass.getName() + " cannot be instantiated.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("RecordFilter class " + filterClass.getName() + " constructor not accessible.", e);
		}
		if (filter.configure(dataFormat) == false)
			throw new RuntimeException("Cannot configure: " + filterClass.getName() +
					" for DataFormat." + dataFormat);
		return filter;
	}

	private SourceDocumentConverter getConverterInstance(SourceDocumentType sourceType, SourceDocumentType targetType) {
		if (sourceType == null)
			throw new NullPointerException("SourceDocumentType cannot be null!");

		if (targetType == null)
			throw new NullPointerException("Target SourceDocumentType cannot be null!");

		Map<SourceDocumentType, Class<SourceDocumentConverter>> converters = converterClasses.get(sourceType);

		if (converters == null || !converters.containsKey(targetType))
			throw new NullPointerException("No SourceDocumentConverter registered for " + sourceType + "-" + targetType);

		return getConverterInstance(converters.get(targetType));
	}

	private SourceDocumentConverter getConverterInstance(Class<SourceDocumentConverter> converterClass) {
		if (converterClass == null)
			throw new NullPointerException("converterClass cannot be null!");
		SourceDocumentConverter converter;
		try {
			converter = converterClass.getConstructor(ConversionRegistry.class)
			.newInstance(this);
		} catch (InstantiationException e) {
			throw new RuntimeException("SourceDocumentConverter " + converterClass.getName() + " cannot be instantiated.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("SourceDocumentConverters class " + converterClass.getName() + " constructor not accessible.", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException for: " + converterClass.getName(), e);
		} catch (SecurityException e) {
			throw new RuntimeException("SecurityException for: " + converterClass.getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("InvocationTargetException for: " + converterClass.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No constructor that takes a CoreRegistry argument " + converterClass.getName(), e);
		}
		return converter;
	}

	private SourceDocumentReader getReaderInstance(SourceDocumentType sourceType) {
		if (sourceType == null)
			throw new NullPointerException("SourceDocumentType cannot be null!");
		if(readerClasses.containsKey(sourceType) == false)
			throw new NullPointerException("No SourceDocumentReader registered for " + sourceType);
		Class<SourceDocumentReader> readerClass = readerClasses.get(sourceType);
		return getReaderInstance(readerClass);
	}

	private SourceDocumentReader getReaderInstance(Class<SourceDocumentReader> readerClass) {
		if (readerClass == null)
			throw new NullPointerException("readerClass cannot be null!");
		if (MockSourceDocumentReader.class.equals(readerClass))
			throw new RuntimeException("MockSourceDocumentReader called!");
		SourceDocumentReader reader;
		try {
			reader = readerClass.getConstructor(ConversionRegistry.class)
			.newInstance(this);
		}  catch (InstantiationException e) {
			throw new RuntimeException("Could not instantiate: " + readerClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Illegal Access instantiating: " + readerClass.getName(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException for: " + readerClass.getName(), e);
		} catch (SecurityException e) {
			throw new RuntimeException("SecurityException for: " + readerClass.getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("InvocationTargetException for: " + readerClass.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No constructor that takes a CoreRegistry argument " + readerClass.getName(), e);
		}
		return reader;
	}

	public void registerSequenceParserClass(Class<SequenceParser> parserClass,
			DataFormat dataFormat) {
		if (parserClass == null)
			throw new NullPointerException("parserClass cannot be null!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be null!");
		sequenceParserClasses.put(dataFormat, parserClass);
		if(log.isInfoEnabled()) log.info("Registered SequenceParser for " + dataFormat 
				+ " : " + parserClass.getName());
	}

	public boolean hasSequenceParser(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be null!");
		return sequenceParserClasses.containsKey(dataFormat);
	}

	public SequenceParser getSequenceParser(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be null!");
		if (sequenceParserClasses.containsKey(dataFormat) == false)
			throw new NullPointerException("No SequenceParser registered for DataFormat." + dataFormat);
		Class<SequenceParser> parserClass = sequenceParserClasses.get(dataFormat);
		SequenceParser parser;			
		try {
			parser = parserClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not instantiate: " + parserClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalArgumentException for: " + parserClass.getName(), e);
		}
		if (parser.configure(dataFormat) == false)
			throw new RuntimeException("Cannot configure: " + parserClass.getName() +
					" for DataFormat." + dataFormat);
		return parser;
	}
}
