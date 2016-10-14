/*
 * FieldDataType.java
 */
package org.ngbw.sdk.core.types;


import java.util.Date;
import java.util.EnumMap;
import java.util.Map;


/**
 * A FieldType wraps all sql/java types occurring in the value
 * of DataRecords - accessed via their RecordField key.
 *
 * @author Roland H. Niedner
 * @author Paul Hoover
 * 
 */
public enum FieldDataType {

	DATASET,
	DATE,
	DOUBLE,
	FLOAT,
	INTEGER,
	LONG,
	STRING;

	private static final Map<FieldDataType, Class<?>> m_dataClasses = new EnumMap<FieldDataType, Class<?>>(FieldDataType.class);

	static {
		m_dataClasses.put(DATASET, Dataset.class);
		m_dataClasses.put(DATE, Date.class);
		m_dataClasses.put(DOUBLE, Double.class);
		m_dataClasses.put(FLOAT, Float.class);
		m_dataClasses.put(INTEGER, Integer.class);
		m_dataClasses.put(LONG, Long.class);
		m_dataClasses.put(STRING, String.class);
	}

	public boolean matches(Object data)
	{
		if (data == null)
			return true;

		return m_dataClasses.get(this).isInstance(data);
	}
}
