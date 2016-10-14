/*
 * IndexedDataRecordCollectionImpl.java
 */
package org.ngbw.sdk.core.shared;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;

/**
 * 
 * @author Roland H. Niedner
 *
 */
public class IndexedDataRecordCollectionImpl extends GenericDataRecordCollectionImpl<IndexedDataRecord> implements GenericDataRecordCollection<IndexedDataRecord> {

	/**
	 * @param recordType
	 * @param types
	 */
	public IndexedDataRecordCollectionImpl(RecordType recordType,
			Set<RecordFieldType> types) {
		super(recordType, types);
	}

	/**
	 * @param recordType
	 * @param types
	 * @param records
	 */
	public IndexedDataRecordCollectionImpl(RecordType recordType,
			Set<RecordFieldType> types, List<IndexedDataRecord> records) {
		super(recordType, types, records);
	}
	
    public void add(int index, IndexedDataRecord dataRecord) {
		if (this.recordType.equals(dataRecord.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		int recordIndex = dataRecord.getIndex();
		if (index != recordIndex)
			throw new RuntimeException("DataRecord index " + recordIndex
					+ " is out of sync with the DataRecordCollection index " + index);
		records.add(index, dataRecord);
    }

	public boolean add(IndexedDataRecord dataRecord) {
		if (this.recordType.equals(dataRecord.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		int recordIndex = dataRecord.getIndex();
		int nextIndex = records.size();
		if (nextIndex != recordIndex)
			throw new RuntimeException("DataRecord index " + recordIndex
					+ " is out of sync with the DataRecordCollection index " + nextIndex);
		return records.add(dataRecord);
	}
	
	public void sortByField(RecordFieldType field, boolean reverse) {
		Collections.sort(records, new IndexedDataRecordComparator(field, reverse));
	}
	
	public void sortByIndex(boolean reverse) {
		Collections.sort(records, new IndexedDataRecordComparator(reverse));
	}
}
