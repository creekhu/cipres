package org.ngbw.sdk.core.shared;

import java.util.Comparator;

import org.ngbw.sdk.core.types.RecordFieldType;

public class IndexedDataRecordComparator extends DataRecordComparator<IndexedDataRecord> implements Comparator<IndexedDataRecord> {

	protected boolean byIndex = false;
	
	public IndexedDataRecordComparator(RecordFieldType field, boolean reverse) {
		super(field, reverse);
	}
	
	public IndexedDataRecordComparator(boolean reverse) {
		super(null, reverse);
		byIndex = true;
	}

	public int compare(IndexedDataRecord dra, IndexedDataRecord drb) {
		if (byIndex) {
			return 0;
		} else {
			return super.compare(dra, drb);
		}
	}

}
