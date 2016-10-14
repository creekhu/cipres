/*
 * RecordField.java
 */
package org.ngbw.sdk.core.types;


import java.util.Map;
import java.util.EnumMap;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public enum RecordFieldType {

	ALIGNED_QUERY_SEQUENCE,
	ALIGNED_SUBJECT_SEQUENCE,
	ALIGNMENT_LENGTH,
	ALTERNATIVE_ID,
	AUTHOR,
	CHAIN,
	DATASET,
	DATA_ID,
	DEPOSITION_DATE,
	E_VALUE,
	GAP_EXTENSION_PENALTY,
	GAP_PENALTY,
	LENGTH,
	MODIFICATION_DATE,
	MOLECULE,
	NAME,
	NUMBER_OF_ALIGNED_SEQUENCES,
	NUMBER_OF_BYTES,
	NUMBER_OF_HITS,
	NUMBER_OF_IDENTITIES,
	NUMBER_OF_LETTERS,
	NUMBER_OF_POSITIVES,
	NUMBER_OF_SEQUENCES,
	ORGANISM,
	PERCENTAGE_IDENTITY,
	PRIMARY_ID,
	PROGRAM,
	PROGRAM_VERSION,
	QUERY,
	QUERY_END,
	QUERY_ID,
	QUERY_START,
	RESOLUTION,
	SCORE,
	SCORING_MATRIX,
	SUBJECT_END,
	SUBJECT_ID,
	SUBJECT_START,
	TYPE,
	VERSION;

	private static final Map<RecordFieldType, FieldDataType> m_dataTypes = new EnumMap<RecordFieldType, FieldDataType>(RecordFieldType.class);

	static {
		m_dataTypes.put(ALIGNED_QUERY_SEQUENCE, FieldDataType.STRING);
		m_dataTypes.put(ALIGNED_SUBJECT_SEQUENCE, FieldDataType.STRING);
		m_dataTypes.put(ALIGNMENT_LENGTH, FieldDataType.DOUBLE);
		m_dataTypes.put(ALTERNATIVE_ID, FieldDataType.STRING);
		m_dataTypes.put(AUTHOR, FieldDataType.STRING);
		m_dataTypes.put(CHAIN, FieldDataType.STRING);
		m_dataTypes.put(DATASET, FieldDataType.DATASET);
		m_dataTypes.put(DATA_ID, FieldDataType.STRING);
		m_dataTypes.put(DEPOSITION_DATE, FieldDataType.DATE);
		m_dataTypes.put(E_VALUE, FieldDataType.DOUBLE);
		m_dataTypes.put(GAP_EXTENSION_PENALTY, FieldDataType.FLOAT);
		m_dataTypes.put(GAP_PENALTY, FieldDataType.FLOAT);
		m_dataTypes.put(LENGTH, FieldDataType.INTEGER);
		m_dataTypes.put(MODIFICATION_DATE, FieldDataType.DATE);
		m_dataTypes.put(MOLECULE, FieldDataType.STRING);
		m_dataTypes.put(NAME, FieldDataType.STRING);
		m_dataTypes.put(NUMBER_OF_ALIGNED_SEQUENCES, FieldDataType.INTEGER);
		m_dataTypes.put(NUMBER_OF_BYTES, FieldDataType.INTEGER);
		m_dataTypes.put(NUMBER_OF_HITS, FieldDataType.INTEGER);
		m_dataTypes.put(NUMBER_OF_IDENTITIES, FieldDataType.INTEGER);
		m_dataTypes.put(NUMBER_OF_LETTERS, FieldDataType.LONG);
		m_dataTypes.put(NUMBER_OF_POSITIVES, FieldDataType.INTEGER);
		m_dataTypes.put(NUMBER_OF_SEQUENCES, FieldDataType.LONG);
		m_dataTypes.put(ORGANISM, FieldDataType.STRING);
		m_dataTypes.put(PERCENTAGE_IDENTITY, FieldDataType.DOUBLE);
		m_dataTypes.put(PRIMARY_ID, FieldDataType.STRING);
		m_dataTypes.put(PROGRAM, FieldDataType.STRING);
		m_dataTypes.put(PROGRAM_VERSION, FieldDataType.STRING);
		m_dataTypes.put(QUERY, FieldDataType.STRING);
		m_dataTypes.put(QUERY_END, FieldDataType.INTEGER);
		m_dataTypes.put(QUERY_ID, FieldDataType.STRING);
		m_dataTypes.put(QUERY_START, FieldDataType.INTEGER);
		m_dataTypes.put(RESOLUTION, FieldDataType.FLOAT);
		m_dataTypes.put(SCORE, FieldDataType.FLOAT);
		m_dataTypes.put(SCORING_MATRIX, FieldDataType.STRING);
		m_dataTypes.put(SUBJECT_END, FieldDataType.INTEGER);
		m_dataTypes.put(SUBJECT_ID, FieldDataType.STRING);
		m_dataTypes.put(SUBJECT_START, FieldDataType.INTEGER);
		m_dataTypes.put(TYPE, FieldDataType.STRING);
		m_dataTypes.put(VERSION, FieldDataType.STRING);
	}

	public FieldDataType dataType()
	{
		return m_dataTypes.get(this);
	}
}
