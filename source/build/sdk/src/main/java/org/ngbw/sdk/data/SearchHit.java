/*
 * SearchHit.java
 */
package org.ngbw.sdk.data;


import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.core.shared.DatasetRecord;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class SearchHit extends DatasetRecord {

	static final long serialVersionUID = 5497800471434920836L;

	private float rank;


	// constructors


	public SearchHit(RecordType type, Set<RecordFieldType> fields, Dataset dataset, float rank)
	{
		super(type, fields, dataset);

		this.rank = rank;
	}

	public SearchHit(RecordType type, Map<RecordFieldType, Object> values, Dataset dataset, float rank)
	{
		super(type, values, dataset);

		this.rank = rank;
	}

	protected SearchHit()
	{
		// for conformance with the Serializable interface
		super();
	}


	// public methods


	public float getRank()
	{
		return rank;
	}

	public void setRank(float rank)
	{
		this.rank = rank;
	}
}
