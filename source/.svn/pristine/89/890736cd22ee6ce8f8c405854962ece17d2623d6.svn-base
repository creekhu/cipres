/*
 * IndexedDataRecord.java
 */
package org.ngbw.sdk.core.shared;


import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class IndexedDataRecord extends DataRecordImpl {

	static final long serialVersionUID = -8827208566962321283L;

	private int index;


	// constructors


	public IndexedDataRecord(RecordType type, Set<RecordFieldType> fields, int index)
	{
		super(type, fields);

		this.index = index;
	}

	public IndexedDataRecord(RecordType type, Map<RecordFieldType, Object> values, int index)
	{
		super(type, values);

		this.index = index;
	}

	public IndexedDataRecord(DataRecord other, int index)
	{
		super(other);

		this.index = index;
	}

	protected IndexedDataRecord()
	{
		// for conformance with the Serializable interface
		super();
	}


	// public methods


	public int getIndex()
	{
		return index;
	}
}
