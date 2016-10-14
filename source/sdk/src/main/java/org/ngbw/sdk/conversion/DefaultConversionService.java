/*
 * DefaultConversionService.java
 */
package org.ngbw.sdk.conversion;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.ConversionService;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SourceDocumentConverter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultConversionService implements ConversionService {

	private static Log log = LogFactory.getLog(DefaultConversionService.class);
	
	ConversionRegistry conversionRegistry;
	
	DefaultConversionService(ConversionRegistry conversionRegistry) {
		if(conversionRegistry == null) 
			throw new NullPointerException("ConversionRegistry must not be null!");
		this.conversionRegistry = conversionRegistry;
	}

	public ConversionRegistry getConversionRegistry() {
		return conversionRegistry;
	}

	public Set<SourceDocumentType> getReadableDocumentTypes() {
		return conversionRegistry.getReadableDocumentTypes();
	}

	public boolean canConvert(SourceDocumentType sourceKey, SourceDocumentType targetKey) {
		if(sourceKey == null) 
			throw new NullPointerException("sourceKey must not be null!");
		if(targetKey == null) 
			throw new NullPointerException("targetKey must not be null!");
		if(sourceKey.equals(targetKey)) return true;
		return conversionRegistry.hasConverter(sourceKey, targetKey);
	}

	public boolean canConvert(SourceDocument srcDocument, SourceDocumentType targetKey) {
		if(srcDocument == null) 
			throw new NullPointerException("srcDocument must not be null!");
		if(targetKey == null) 
			throw new NullPointerException("targetKey must not be null!");
		SourceDocumentType sourceKey = srcDocument.getType();
		if(sourceKey.equals(targetKey)) return true;
		if(canConvert(sourceKey, targetKey)== false)
			throw new RuntimeException("No converter for this transformation registered yet! Please check first with " +
					"calling canConvert(DataFormat sourceFormat, DataFormat targetFormat).");
		SourceDocumentConverter converter = conversionRegistry.getConverter(sourceKey, targetKey);
		return converter.canRead(srcDocument) && converter.canWrite(targetKey);
	}

	public Set<SourceDocumentType> getTargetSourceDocumentTypes(SourceDocumentType sourceDocumentType){
		if(sourceDocumentType == null) 
			throw new NullPointerException("sourceDocumentType must not be null!");
		return conversionRegistry.getTargetSourceDocumentTypes(sourceDocumentType);
	}

	public boolean canRead(SourceDocumentType sourceDocumentType) {
		if(sourceDocumentType == null) 
			throw new NullPointerException("sourceDocumentType must not be null!");
		return conversionRegistry.hasReader(sourceDocumentType);
	}

	public boolean validate(SourceDocument srcDocument) throws IOException, SQLException {
		if(srcDocument == null) 
			throw new NullPointerException("SourceDocument must not be null!");
		SourceDocumentType sourceDocumentType = srcDocument.getType();
		if (log.isDebugEnabled())
			log.debug("validating SourceDocumentType: " + sourceDocumentType);
		if (canRead(sourceDocumentType) == false)
			throw new RuntimeException("No Reader for this SourceDocumentType " + sourceDocumentType
					+ " registered yet! Please check first with " +
			"calling canRead(SourceDocumentType).");
		SourceDocumentReader reader = conversionRegistry.getReader(sourceDocumentType);
		ValidationResult result = reader.validate(srcDocument);
		if(result == null) 
			throw new NullPointerException("ValidationResult is null - How can that happen!");
		if (result.isValid() == false) {
			if (log.isDebugEnabled())
				log.debug("invalid SourceDocumentType: " + sourceDocumentType + " : Reader reports: " 
						+ StringUtils.join(result.getErrors(), " | "));
		}
		return result.isValid();
	}
	
	public boolean check(SourceDocument srcDocument, SourceDocumentType sourceDocumentType) throws IOException, SQLException {
		if (srcDocument == null || srcDocument.getData() == null)
			throw new NullPointerException("No input data!");
		if (sourceDocumentType == null)
			throw new NullPointerException("The SourceDocumentType of the SourceDocument is null!");
		if(log.isDebugEnabled())
			log.debug("Testing srcDocument with type " + srcDocument.getType()
					+ " against " + sourceDocumentType);
		if (SourceDocumentType.UNKNOWN.equals(sourceDocumentType))
			return true; //everything goes
		if (sourceDocumentType.isIncomplete())
			return true; //everything goes
		if(canRead(srcDocument.getType()) == false)
			throw new RuntimeException("Cannot read sourceDocument. Please check with canRead method first!");
		return validate(new SourceDocumentBean(sourceDocumentType, srcDocument.getData()));
	}
	
	public SourceDocumentType check(SourceDocument srcDocument, Set<SourceDocumentType> sourceDocumentTypes) throws IOException, SQLException {
		if(srcDocument == null) 
			throw new NullPointerException("SourceDocument must not be null!");
		if(sourceDocumentTypes == null || sourceDocumentTypes.isEmpty()) 
			throw new NullPointerException("sourceDocumentTypes must not be null or empty!");
		for (SourceDocumentType type : sourceDocumentTypes) {
			if (check(srcDocument, type))
				return type;
		}
		return null;
	}
	
	public SourceDocumentType detectSourceDocument(SourceDocument srcDocument) throws IOException, SQLException {
		if(srcDocument == null) 
			throw new NullPointerException("SourceDocument must not be null!");
		// Return the specified SourceDocumentType if is is 
		// (NOT UNKNOWN AND NOT INCOMPLETE AND is validated by the registered SourceDocumentTypeReader)
		// OR its is flagged validated.
		if (srcDocument.isValidated() ||
				(SourceDocumentType.UNKNOWN.equals(srcDocument.getType()) == false
				&& srcDocument.getType().isIncomplete() == false
				&& check(srcDocument, srcDocument.getType()))
				)
				return srcDocument.getType();
		
		// Return the SourceDocumentType that is returned by the check method.
//		SourceDocumentType validatedType = check(srcDocument, getReadableDocumentTypes());
//		if(log.isDebugEnabled())
//			log.debug("validatedType: " + validatedType);
		return null;
	}

	public SourceDocument convert(SourceDocument srcDocument, SourceDocumentType sourceType, SourceDocumentType targetType) throws IOException, SQLException, ParseException {
		if(srcDocument == null) 
			throw new NullPointerException("SourceDocument must not be null!");
		if(sourceType == null) 
			throw new NullPointerException("sourceType must not be null!");
		if(targetType == null) 
			throw new NullPointerException("targetType must not be null!");
		if(sourceType.equals(targetType))
			return srcDocument;
		if(canConvert(sourceType, targetType) == false)
			throw new NullPointerException("No converter registered yet! Please check with the canConvert method first.");
		return conversionRegistry.getConverter(sourceType, targetType).convert(srcDocument, targetType);
	}

	public SourceDocument convert(SourceDocument srcDocument, SourceDocumentType targetKey) throws IOException, SQLException, ParseException {
		if(srcDocument == null) 
			throw new NullPointerException("SourceDocument must not be null!");
		if(targetKey == null) 
			throw new NullPointerException("targetKey must not be null!");
		SourceDocumentType srcKey = srcDocument.getType();
		if(canConvert(srcKey, targetKey) == false)
			throw new NullPointerException("No converter registered yet! Please check with the canConvert method first.");
		return conversionRegistry.getConverter(srcKey, targetKey).convert(srcDocument, targetKey);
	}

	public SourceDocument convert(Collection<SourceDocument> srcDocuments, SourceDocumentType targetKey) throws IOException, SQLException, ParseException {
		if(srcDocuments == null || srcDocuments.isEmpty()) 
			throw new NullPointerException("srcDocuments must not be null or empty!");
		if(targetKey == null) 
			throw new NullPointerException("targetKey must not be null!");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = null;
		for(SourceDocument srcDocument : srcDocuments)
			try {
				buffer.write(convert(srcDocument,targetKey).getData());
				buffer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (buffer != null)
					try {
						buffer.close();
					} catch (IOException e) {
						log.warn(e.toString(), e);
					}
			}
		return new SourceDocumentBean(targetKey, data, true);
	}

	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument srcDocument) throws IOException, SQLException, ParseException {
		if (srcDocument == null || srcDocument.getData() == null)
			throw new NullPointerException("No input data!");
		SourceDocumentType sourceDocumentType = srcDocument.getType();
		if (sourceDocumentType == null)
			throw new NullPointerException("The DataFormat of the SourceDocument is null!");
		if(validate(srcDocument) == false)
			throw new RuntimeException("Cannot read sourceDocument of type " + srcDocument.getType()
					+ ". Please check with canRead method first!");
		SourceDocumentReader reader = conversionRegistry.getReader(sourceDocumentType);
		return reader.read(srcDocument);
	}

	public DataRecord readSingle(SourceDocument srcDocument) throws IOException, SQLException, ParseException {
		if (srcDocument == null || srcDocument.getData() == null)
			throw new NullPointerException("No input data!");
		if (srcDocument.getDataFormat() == null)
			throw new NullPointerException("The DataFormat of the SourceDocument is null!");
		SourceDocumentReader reader = conversionRegistry.getReader(srcDocument.getType());
		return reader.readSingle(srcDocument);
	}

	public RecordFilter getRecordFilter(DataFormat dataFormat) {
		RecordFilter filter = conversionRegistry.getRecordFilter(dataFormat);
		if(log.isDebugEnabled()) log.debug(dataFormat + " return: " + filter.getClass() 
				+ "(" + filter.getFilteredFormat() + ")");
		return filter;
	}

	public boolean hasRecordFilter(DataFormat dataFormat) {
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		return conversionRegistry.getFilterableDataFormats().contains(dataFormat);
	}

	public boolean canExtractSequence(SourceDocument srcDocument) throws IOException, SQLException {
		if (srcDocument == null || srcDocument.getData() == null)
			throw new NullPointerException("No input data!");
		DataFormat dataFormat = srcDocument.getDataFormat();
		return conversionRegistry.hasSequenceParser(dataFormat);
	}

	public String extractSequence(SourceDocument srcDocument) throws IOException, SQLException {
		if (srcDocument == null || srcDocument.getData() == null)
			throw new NullPointerException("No input data!");
		DataFormat dataFormat = srcDocument.getDataFormat();
		String sourceRecord = new String(srcDocument.getData());
		return conversionRegistry.getSequenceParser(dataFormat).extractSequence(sourceRecord);
	}

	public String extractSequence(DataFormat dataFormat, String sourceRecord) {
		if (dataFormat == null)
			throw new NullPointerException("dataFormat is null!");
		if (sourceRecord == null)
			throw new NullPointerException("sourceRecord is null!");
		return conversionRegistry.getSequenceParser(dataFormat).extractSequence(sourceRecord);
	}
}
