/*
 * GenericDataRecordCollectionImpl.java
 */
package org.ngbw.sdk.core.shared;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;


/**
 * 
 * @author Roland H. Niedner
 *
 * @param <T>
 */
public class GenericDataRecordCollectionImpl<T extends DataRecord> implements GenericDataRecordCollection<T> {
	
	protected RecordType recordType;
	protected Set<RecordFieldType> types;
	protected List<T> records = new ArrayList<T>();
	
	public GenericDataRecordCollectionImpl(RecordType recordType, Set<RecordFieldType> types) {
		this.recordType = recordType;
		this.types = types;
	}
	
	public GenericDataRecordCollectionImpl(RecordType recordType, Set<RecordFieldType> types, List<T> records) {
		this(recordType, types);
		this.records = records;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public Set<RecordFieldType> getFields() {
		return types;
	}
	
	public FieldDataType getType(RecordFieldType field) {
		return field.dataType();
	}

	public boolean add(T dataRecord) {
		if (this.recordType.equals(dataRecord.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		return records.add(dataRecord);
	}

	public void add(int index, T dataRecord) {
		if (this.recordType.equals(dataRecord.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		records.add(index, dataRecord);
	}

	public GenericDataRecordCollection<T> slice(int fromIndex, int toIndex) {
		//adjust for the fact that the toIndex element is excluded
		List<T> slice = records.subList(fromIndex, toIndex);
		return new GenericDataRecordCollectionImpl<T>(recordType, types, slice);
	}

	public T get(int index) {
		return records.get(index);
	}

	public int size() {
		return records.size();
	}
	
	public List<T> toList() {
		return records;
	}

	public List<T> toList(int fromIndex, int toIndex) {
		return records.subList(fromIndex, toIndex);
	}

	public Iterator<T> iterator() {
		return records.listIterator();
	}
	
	public void sortByField(RecordFieldType field, boolean reverse) {
		Collections.sort(records, new DataRecordComparator<T>(field, reverse));
	}
}
