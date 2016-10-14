/*
 * SearchHitComparator.java
 */
package org.ngbw.sdk.data;


import java.util.Comparator;

import org.ngbw.sdk.core.shared.DataRecordComparator;
import org.ngbw.sdk.core.types.RecordFieldType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class SearchHitComparator extends DataRecordComparator<SearchHit> implements Comparator<SearchHit> {

	protected boolean byDataset = false;
	
	public SearchHitComparator(RecordFieldType field, boolean reverse) {
		super(field, reverse);
	}
	
	public SearchHitComparator(boolean reverse) {
		super(null, reverse);
		byDataset = true;
	}

	public int compare(SearchHit dra, SearchHit drb) {
		if (byDataset) {
			String a = dra.getDataset().getValue();
			String b = drb.getDataset().getValue();
			if (reverse) return -a.compareToIgnoreCase(b);
			return a.compareToIgnoreCase(b);
		} else {
			return super.compare(dra, drb);
		}
	}
}
