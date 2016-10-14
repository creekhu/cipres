/*
 * BaseTransformer.java
 */
package org.ngbw.sdk.core.transformation;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Set;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.api.core.SourceDocumentTransformer;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.core.shared.GenericDataRecordCollectionImpl;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.RecordField;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public abstract class BaseTransformer implements SourceDocumentTransformer {
	
	protected ServiceFactory serviceFactory;
	protected SourceDocument srcDocument;
	protected RecordType targetType;
	
	
	protected BaseTransformer(ServiceFactory serviceFactory, SourceDocument srcDocument, RecordType targetType) throws IOException, SQLException {
		if (srcDocument == null)
			throw new NullPointerException("SourceDocument is null!");
		this.serviceFactory = serviceFactory;
		this.srcDocument = srcDocument;
		this.targetType = targetType;
		transform(srcDocument);
	}

	public SourceDocumentType getSourceType() {
		return srcDocument.getType();
	}

	public RecordType getTargetType() {
		return targetType;
	}

	public abstract GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection() throws ParseException;

	public abstract SourceDocument getTransformedSourceDocument(DataRecord dataRecord);

	protected abstract void transform(SourceDocument srcDocument) throws IOException, SQLException;

	protected GenericDataRecordCollection<IndexedDataRecord> newDataRecordCollection() {
		CoreRegistry coreRegistry = serviceFactory.getCoreRegistry();
		if (targetType == null)
			throw new NullPointerException("RecordType cannot be NULL!");
		Set<RecordFieldType> fields = coreRegistry.getRecordFields(targetType);
		return new GenericDataRecordCollectionImpl<IndexedDataRecord>(targetType, fields);
	}
	
	protected IndexedDataRecord newDataRecord(int index) {
		CoreRegistry coreRegistry = serviceFactory.getCoreRegistry();
		if (targetType == null)
			throw new NullPointerException("RecordType cannot be NULL!");
		Set<RecordFieldType> fields = coreRegistry.getRecordFields(targetType);
		return new IndexedDataRecord(targetType, fields, index);
	}
	
	protected IndexedDataRecord newDataRecord(int index, DataRecord record) {
		if (targetType == null)
			throw new NullPointerException("RecordType cannot be NULL!");

		Set<RecordFieldType> fields = serviceFactory.getCoreRegistry().getRecordFields(targetType);
		IndexedDataRecord newRecord = new IndexedDataRecord(targetType, fields, index);

		for (RecordField field : record.getFields())
			newRecord.getField(field.getFieldType()).setValue(field.getValue());

		return newRecord;
	}
}
