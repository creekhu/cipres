/*
 * DataRecordCollectionImpl.java
 */
package org.ngbw.sdk.core.shared;


import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.core.DataRecordCollection;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DataRecordCollectionImpl extends GenericDataRecordCollectionImpl<DataRecord>
		implements DataRecordCollection<DataRecord> {

	/**
	 * @param recordType
	 * @param types
	 */
	public DataRecordCollectionImpl(RecordType recordType, Set<RecordFieldType> types) {
		super(recordType, types);
	}

	/**
	 * @param recordType
	 * @param types
	 * @param records
	 */
	public DataRecordCollectionImpl(RecordType recordType, Set<RecordFieldType> types, List<DataRecord> records) {
		super(recordType, types, records);
	}

	public void addAll(final DataRecordCollection<DataRecord> dataRecords) {
		if ( dataRecords == null) return;
		if (this.recordType.equals(dataRecords.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		if (recordType.equals(dataRecords.getRecordType()))
			records.addAll(dataRecords.toList());
		throw new RuntimeException("Incomming DataRecordCollection type " + dataRecords.getRecordType()
				+ " does not match: " + recordType);
	}
}
