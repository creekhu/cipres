/*
 * RecordField.java
 */
package org.ngbw.sdk.database;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;


/**
 * A container for an <code>Object</code> object and a <code>RecordFieldType</code> value that
 * describes that object. <code>RecordField</code> objects are themselves contained within a
 * {@link DataRecord DataRecord} object.
 *
 * @author Paul Hoover
 *
 */
public class RecordField implements Comparable<RecordField> {

	private static final String DATE_FORMAT = "dd-MMM-yy";

	private final RecordFieldType m_fieldType;
	private boolean m_isModified;
	private Object m_value;


	// constructors


	/**
	 * Constructs an object whose value is described by the given <code>RecordFieldType</code> value.
	 *
	 * @param fieldType
	 */
	public RecordField(RecordFieldType fieldType)
	{
		m_fieldType = fieldType;
	}

	/**
	 *
	 * @param fieldType
	 * @param value
	 */
	public RecordField(RecordFieldType fieldType, Object value)
	{
		this(fieldType);

		if (!m_fieldType.dataType().matches(value))
			throw new WorkbenchException("Incompatible data type");

		m_value = value;
	}

	/**
	 *
	 * @param fieldType
	 * @param value
	 * @throws ParseException
	 */
	public RecordField(RecordFieldType fieldType, String value) throws ParseException
	{
		this(fieldType);

		m_value = parse(value);
	}

	/**
	 *
	 * @param other
	 */
	public RecordField(RecordField other)
	{
		this(other.getFieldType(), other.getValue());
	}


	// public methods


	/**
	 * Returns the field type of the object.
	 *
	 * @return a <code>RecordFieldType</code> value indicating the field type of the object
	 */
	public RecordFieldType getFieldType()
	{
		return m_fieldType;
	}

	/**
	 * Returns the current value of the object as an <code>Object</code> object.
	 *
	 * @return the current value of the object
	 */
	public Object getValue()
	{
		return m_value;
	}

	/**
	 * Returns a string representation of the current value of the object.
	 *
	 * @return the current value of the object
	 */
	public String getValueAsString()
	{
		if (m_value == null)
			return null;

		switch (m_fieldType.dataType()) {
		case DATASET:
		case DOUBLE:
		case FLOAT:
		case INTEGER:
		case LONG:
			return m_value.toString();

		case DATE:
			return (new SimpleDateFormat(DATE_FORMAT)).format((Date) m_value);

		default:
			return (String) m_value;
		}
	}

	/**
	 * Returns the current value of the object as a <code>Dataset</code> object.
	 *
	 * @return the current value of the object
	 */
	public Dataset getValueAsDataset()
	{
		if (m_fieldType.dataType() != FieldDataType.DATASET)
			throw new WorkbenchException("Incompatible data type");

		return (Dataset) m_value;
	}

	/**
	 * Returns the current value of the object as a <code>Double</code> object.
	 *
	 * @return the current value of the object
	 */
	public Double getValueAsDouble()
	{
		if (m_fieldType.dataType() != FieldDataType.DOUBLE)
			throw new WorkbenchException("Incompatible data type");

		return (Double) m_value;
	}

	/**
	 * Returns the current value of the object as a <code>Float</code> object.
	 *
	 * @return the current value of the object
	 */
	public Float getValueAsFloat()
	{
		if (m_fieldType.dataType() != FieldDataType.FLOAT)
			throw new WorkbenchException("Incompatible data type");

		return (Float) m_value;
	}

	/**
	 * Returns the current value of the object as an <code>Integer</code> object.
	 *
	 * @return the current value of the object
	 */
	public Integer getValueAsInteger()
	{
		if (m_fieldType.dataType() != FieldDataType.INTEGER)
			throw new WorkbenchException("Incompatible data type");

		return (Integer) m_value;
	}

	/**
	 * Returns the current value of the object as a <code>Long</code> object.
	 *
	 * @return the current value of the object
	 */
	public Long getValueAsLong()
	{
		if (m_fieldType.dataType() != FieldDataType.LONG)
			throw new WorkbenchException("Incompatible data type");

		return (Long) m_value;
	}

	/**
	 * Returns the current value of the object as a <code>Date</code> object.
	 *
	 * @return the current value of the object
	 */
	public Date getValueAsDate()
	{
		if (m_fieldType.dataType() != FieldDataType.DATE)
			throw new WorkbenchException("Incompatible data type");

		return (Date) m_value;
	}

	/**
	 * Assigns a value to the object and sets the modified state to <code>true</code>. If, however,
	 * the value provided is equal to the current value of the object, then the assignment is ignored
	 * and the modified state is not set.
	 *
	 * @param value the value to assign to the object
	 */
	public void setValue(Object value)
	{
		if (!m_fieldType.dataType().matches(value))
			throw new WorkbenchException("Incompatible data type");

		if (m_value != value) {
			m_value = value;
			m_isModified = true;
		}
	}

	/**
	 * Assigns a value to the object and sets the modified state to <code>true</code>.
	 *
	 *  If, however,
	 * the value provided is equal to the current value of the object, then the assignment is ignored
	 * and the modified state is not set.
	 *
	 * @param value
	 * @throws ParseException
	 */
	public void setValue(String value) throws ParseException
	{
		Object newValue = parse(value);

		if (m_value != newValue) {
			m_value = newValue;
			m_isModified = true;
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this one. <code>RecordField</code> objects
	 * are equal if both their <code>RecordFieldType</code> and <code>Object</code> members are equal.
	 *
	 * @param other the object to compare this object to
	 * @return <code>true</code> if the two objects are equal
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof RecordField == false)
			return false;

		RecordField otherField = (RecordField) other;

		if (getFieldType() != otherField.getFieldType())
			return false;

		return getValue() == otherField.getValue();
	}

	/**
	 * Returns a hash code value for the object. The hash code value for a <code>RecordField</code>
	 * object is a combination of the hash code values of its <code>RecordFieldType</code> and
	 * <code>Object</code> members.
	 *
	 * @return the hash code value for this object
	 */
	@Override
	public int hashCode()
	{
		int hash = 31 + getFieldType().hashCode();

		Object value = getValue();

		if (value != null)
			return 31 * hash + value.hashCode();
		else
			return hash;
	}

	/**
	 * Compares this object with the specified object for order. Returns a negative integer, zero, or
	 * a positive integer as this object is less than, equal to, or greater than the specified object.
	 * The outcome of the comparison is determined by first comparing the two objects' <code>RecordFieldType</code>
	 * members, then their <code>Object</code> members.
	 *
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
	 * or greater than the specified object
	 */
	@Override
	public int compareTo(RecordField other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		int comparison = getFieldType().compareTo(other.getFieldType());

		if (comparison != 0)
			return comparison;

		Object value = getValue();
		Object otherValue = other.getValue();

		if (value == null)
			return -1;

		if (otherValue == null)
			return 1;

		switch (m_fieldType.dataType()) {
		case DATE:
			return ((Date) value).compareTo((Date) otherValue);

		case DOUBLE:
			return ((Double) value).compareTo((Double) otherValue);

		case FLOAT:
			return ((Float) value).compareTo((Float) otherValue);

		case INTEGER:
			return ((Integer) value).compareTo((Integer) otherValue);

		case LONG:
			return ((Long) value).compareTo((Long) otherValue);

		case STRING:
			return ((String) value).compareTo((String) otherValue);

		default:
			return value.toString().compareTo(otherValue.toString());
		}
	}

	/**
	 * Returns a string representation of the object. The representation is a combination of the
	 * string representation of the object's <code>RecordFieldType</code> member and the string
	 * representation of its current value.
	 *
	 * @return a string representation of the object
	 */
	@Override
	public String toString()
	{
		return m_fieldType.toString() + " = " + getValueAsString();
	}


	// package methods


	/**
	 * Indicates whether or not the value of the object has been modified.
	 *
	 * @return <code>true</code> if the value has been modified
	 */
	boolean isModified()
	{
		return m_isModified;
	}


	// private methods


	/**
	 * Parses a string
	 *
	 * @param value a <code>String</code> value to parse
	 * @return the <code>Object</code> object
	 */
	private Object parse(String value) throws ParseException
	{
		if (value == null)
			return null;
		else {
			switch (m_fieldType.dataType()) {
			case DATASET:
				return Dataset.valueOf(value);

			case DATE:
				return (new SimpleDateFormat(DATE_FORMAT)).parse(value);

			case DOUBLE:
				return Double.parseDouble(value);

			case FLOAT:
				return Float.parseFloat(value);

			case INTEGER:
				return Integer.parseInt(value);

			case LONG:
				return Long.parseLong(value);

			default:
				return value;
			}
		}
	}
}
