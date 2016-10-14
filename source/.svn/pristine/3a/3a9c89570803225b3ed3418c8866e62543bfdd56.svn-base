/*
 * SourceDocumentTypeBean.java
 */
package org.ngbw.sdk.core.shared;


import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 * 
 */
public class SourceDocumentType implements Comparable<SourceDocumentType> {

	public static final SourceDocumentType UNKNOWN = new SourceDocumentType(EntityType.UNKNOWN, DataType.UNKNOWN, DataFormat.UNKNOWN);

	private final EntityType m_entityType;
	private final DataType m_dataType;
	private final DataFormat m_dataFormat;


	public SourceDocumentType(EntityType entityType, DataType dataType, DataFormat dataFormat)
	{
		m_entityType = entityType;
		m_dataType = dataType;
		m_dataFormat = dataFormat;
	}

	public DataFormat getDataFormat() {
		return m_dataFormat;
	}

	public DataType getDataType() {
		return m_dataType;
	}

	public EntityType getEntityType() {
		return m_entityType;
	}

	public boolean isIncomplete() {
		return (EntityType.UNKNOWN.equals(getEntityType())
				|| DataType.UNKNOWN.equals(getDataType())
				|| DataFormat.UNKNOWN.equals(getDataFormat()));
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof SourceDocumentType == false)
			return false;

		final SourceDocumentType otherType = (SourceDocumentType) other;

		if(m_entityType.equals(otherType.m_entityType) 
				&& m_dataType.equals(otherType.m_dataType)
				&& m_dataFormat.equals(otherType.m_dataFormat))
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 31 + m_entityType.hashCode();

		hash = 31 * hash + m_dataType.hashCode();

		return 31 * hash + m_dataFormat.hashCode();
	}

	@Override
	public String toString() {
		return m_entityType.toString() + ":" + m_dataType.toString() + ":" + m_dataFormat.toString();
	}

	public int compareTo(SourceDocumentType other) {
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		int comparison = m_entityType.compareTo(other.m_entityType);

		if (comparison != 0)
			return comparison;

		comparison = m_dataType.compareTo(other.m_dataType);

		if (comparison != 0)
			return comparison;

		return m_dataFormat.compareTo(other.m_dataFormat);
	}
}
