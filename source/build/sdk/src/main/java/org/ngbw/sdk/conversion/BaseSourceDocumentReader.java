/*
 * BaseSourceDocumentReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.GenericDataRecordCollectionImpl;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public abstract class BaseSourceDocumentReader implements SourceDocumentReader {
	
	protected final ConversionRegistry conversionRegistry;
	protected final Set<SourceDocumentType> srcTypes = new HashSet<SourceDocumentType>();
	
	public BaseSourceDocumentReader(ConversionRegistry conversionRegistry) {
		this.conversionRegistry = conversionRegistry;
	}
	
	public ConversionRegistry getConversionRegistry() {
		return conversionRegistry;
	}

	public Set<SourceDocumentType> getSupportedTypes() {
		return srcTypes;
	}
	
	public boolean canRead(SourceDocument srcDoc) {
		return srcTypes.contains(srcDoc.getType());
	}

	public abstract GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException;

	public abstract DataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException;

	public abstract ValidationResult validate(SourceDocument document) throws IOException, SQLException;

	protected SourceDocumentType getSourceDocumentType(EntityType entityType, DataType dataType, DataFormat dataFormat) {
		if (entityType == null)
			throw new NullPointerException("EntityType cannot be NULL!");
		if (dataType == null)
			throw new NullPointerException("DataType cannot be NULL!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be NULL!");
		return new SourceDocumentType(entityType, dataType, dataFormat);
	}

	protected SourceDocumentType getSourceDocumentType(RecordType recordType, DataFormat dataFormat) {
		if (recordType == null)
			throw new NullPointerException("RecordType cannot be NULL!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be NULL!");
		CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
		DataType dataType = coreRegistry.getDataType(recordType);
		EntityType entityType  = coreRegistry.getEntityType(recordType);
		if (entityType == null)
			throw new NullPointerException("EntityType cannot be NULL!");
		return new SourceDocumentType(entityType, dataType, dataFormat);
	}

	protected IndexedDataRecord getDataRecord(int index, SourceDocument document) {
		if (document == null)
			throw new NullPointerException("SourceDocument cannot be NULL!");
		CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
		RecordType recordType = coreRegistry.getRecordType(document.getEntityType(), document.getDataType()); 
		return getDataRecord(index, recordType);
	}

	protected IndexedDataRecord getDataRecord(int index, RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType cannot be NULL!");
		CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
		Set<RecordFieldType> fields = coreRegistry.getRecordFields(recordType);
		IndexedDataRecord dataRecord = new IndexedDataRecord(recordType, fields, index);
		return dataRecord;
	}
	
	protected GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection(SourceDocument document) {
		if (document == null)
			throw new NullPointerException("SourceDocument cannot be NULL!");
		CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
		RecordType recordType = coreRegistry.getRecordType(document.getEntityType(), document.getDataType()); 
		if (recordType == null)
			throw new NullPointerException("No RecordType registered for: " + 
					document.getType().toString());
		Set<RecordFieldType> fields = coreRegistry.getRecordFields(recordType);
		GenericDataRecordCollection<IndexedDataRecord> drc = new GenericDataRecordCollectionImpl<IndexedDataRecord>(recordType, fields);
		return drc;
	}
}
