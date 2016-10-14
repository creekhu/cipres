/*
 * DataRecordImpl.java
 */
package org.ngbw.sdk.core.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.RecordField;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class DataRecordImpl implements DataRecord, Serializable, Comparable<DataRecord> {

	static final long serialVersionUID = -2631963217880521464L;

	private RecordType m_recordType;
	private Map<RecordFieldType, RecordField> m_fields = new EnumMap<RecordFieldType, RecordField>(RecordFieldType.class);


	// constructors


	public DataRecordImpl(RecordType type, Set<RecordFieldType> fields)
	{
		m_recordType = type;

		for (Iterator<RecordFieldType> types = fields.iterator() ; types.hasNext() ; ) {
			RecordFieldType fieldType = types.next();

			m_fields.put(fieldType, new RecordField(fieldType));
		}
	}

	public DataRecordImpl(RecordType type, Map<RecordFieldType, Object> values)
	{
		m_recordType = type;

		for (Iterator<Map.Entry<RecordFieldType, Object>> entries = values.entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<RecordFieldType, Object> entry = entries.next();
			RecordFieldType fieldType = entry.getKey();

			m_fields.put(fieldType, new RecordField(fieldType, entry.getValue()));
		}
	}

	public DataRecordImpl(DataRecord other)
	{
		m_recordType = other.getRecordType();

		for (Iterator<RecordField> fields = other.getFields().iterator() ; fields.hasNext() ; ) {
			RecordField field = fields.next();

			m_fields.put(field.getFieldType(), new RecordField(field));
		}
	}

	protected DataRecordImpl()
	{
		// for conformance with the Serializable interface
	}


	// public methods


	public RecordType getRecordType()
	{
		return m_recordType;
	}

	public List<RecordField> getFields()
	{
		return new ArrayList<RecordField>(m_fields.values());
	}

	public RecordField getField(RecordFieldType field)
	{
		return m_fields.get(field);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof DataRecord == false)
			return false;

		DataRecord otherRecord = (DataRecord) other;

		return getRecordType() == otherRecord.getRecordType() && getFields() == otherRecord.getFields();
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + getRecordType().hashCode();

		return 31 * hash + getFields().hashCode();
	}

	public int compareTo(DataRecord other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		int comparison = getRecordType().compareTo(other.getRecordType());

		if (comparison != 0)
			return comparison;

		Iterator<RecordField> fields = getFields().iterator();
		Iterator<RecordField> otherFields = other.getFields().iterator();

		while (fields.hasNext() && otherFields.hasNext()) {
			comparison = fields.next().compareTo(otherFields.next());

			if (comparison != 0)
				return comparison;
		}

		assert !fields.hasNext() && !otherFields.hasNext();

		return 0;
	}
}
