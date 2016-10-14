/*
 * SourceDocumentTransformer.java
 */
package org.ngbw.sdk.api.core;


import java.text.ParseException;

import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public interface SourceDocumentTransformer {
	
	/**
	 * @return totalNumberOfDataRecords
	 */
	public int getTotalDataRecordCount();
	
	/**
	 * Get the SourceDocument for the transformed DataRecord.
	 * 
	 * @param dataRecord
	 * @return sourceDocument
	 */
	public SourceDocument getTransformedSourceDocument(DataRecord dataRecord);
	
	/**
	 * @return sourceDocumentType
	 */
	public SourceDocumentType getSourceType();
	
	/**
	 * @return recordType
	 */
	public RecordType getTargetType();
	
	/**
	 * @return dataRecords
	 */
	public GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection() throws ParseException;
	
	/**
	 * @param fromIndex
	 * @param toIndex (element with this index is excluded)
	 * @return dataRecords
	 */
	public GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection(int fromIndex, int toIndex) throws ParseException;
}
