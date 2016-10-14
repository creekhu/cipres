/*
 * DatasetRecord.java 
 */
package org.ngbw.sdk.core.shared;


import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DatasetRecord extends DataRecordImpl {

	static final long serialVersionUID = 8722034186863328960L;

	private Dataset dataset;


	// constructors


	public DatasetRecord(RecordType type, Set<RecordFieldType> fields, Dataset dataset)
	{
		super(type, fields);

		this.dataset = dataset;
	}

	public DatasetRecord(RecordType type, Map<RecordFieldType, Object> values, Dataset dataset)
	{
		super(type, values);

		this.dataset = dataset;
	}

	public DatasetRecord(DataRecord other, Dataset dataset)
	{
		super(other);

		this.dataset = dataset;
	}

	protected DatasetRecord()
	{
		// for conformance with the Serializable interface
		super();
	}


	// public methods


	public Dataset getDataset()
	{
		return dataset;
	}
}
