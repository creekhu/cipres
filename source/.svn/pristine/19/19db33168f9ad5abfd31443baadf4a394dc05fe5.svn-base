/*
 * SearchHitCollectionImpl.java
 */
package org.ngbw.sdk.data;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.data.SearchHitCollection;
import org.ngbw.sdk.core.shared.GenericDataRecordCollectionImpl;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class SearchHitCollectionImpl extends GenericDataRecordCollectionImpl<SearchHit> implements SearchHitCollection {

	private Dataset dataset;

	public SearchHitCollectionImpl(Dataset dataset, RecordType recordType,
			Set<RecordFieldType> types, List<SearchHit> records) {
		super(recordType, types, records);
		this.dataset = dataset;
	}

	public SearchHitCollectionImpl(Dataset dataset, RecordType recordType,
			Set<RecordFieldType> types) {
		super(recordType, types);
		this.dataset = dataset;
	}

	public Dataset getDataset() {
		return dataset;
	}
	
	public void sortByDataset(boolean reverse) {
		Collections.sort(records, new SearchHitComparator(reverse));
	}

	public SearchHitCollection slice(int fromIndex, int toIndex) {
		//adjust for the fact that the toIndex element is excluded
		List<SearchHit> slice = records.subList(fromIndex, toIndex+1);
		return new SearchHitCollectionImpl(dataset, recordType, types, slice);
	}

	public void addAll(SearchHitCollection dataRecords) {
		if ( dataRecords == null) return;
		if (this.recordType.equals(dataRecords.getRecordType()) == false) 
			throw new RuntimeException("Incompatible RecordType!");
		if (recordType.equals(dataRecords.getRecordType()))
		records.addAll(dataRecords.toList());
	}
}
