/*
 * TaskOutputSourceDocument.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.common.util.IOUtils;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;


/**
 *
 * @author Paul Hoover
 *
 */
public class TaskOutputSourceDocument extends GeneratedKeyRow implements SourceDocument, Comparable<TaskOutputSourceDocument> {

	// data fields


	private static final String TABLE_NAME = "task_output_source_documents";
	private static final String KEY_NAME = "OUTPUT_DOCUMENT_ID";
	private final Column<Long> m_outputId = new LongColumn("OUTPUT_ID", false);
	private final Column<String> m_dataFormat = new StringColumn("DATAFORMAT", false, 255);
	private final Column<String> m_dataType = new StringColumn("DATATYPE", false, 255);
	private final Column<String> m_entityType = new StringColumn("ENTITYTYPE", false, 255);
	private final Column<String> m_name = new StringColumn("NAME", true, 255);
	private final Column<Boolean> m_validated = new BooleanColumn("VALIDATED", false);
	private final Column<Long> m_sourceDocumentId = new LongColumn("SOURCE_DOCUMENT_ID", false);
	private SourceDocumentRow m_sourceDocument;


	// constructors


	public TaskOutputSourceDocument()
	{
		this(TABLE_NAME, KEY_NAME);

		setDataFormat(DataFormat.UNKNOWN);
		setDataType(DataType.UNKNOWN);
		setEntityType(EntityType.UNKNOWN);
		setValidated(false);

		m_sourceDocument = new SourceDocumentRow();
	}

	public TaskOutputSourceDocument(long outputDocumentId) throws IOException, SQLException
	{
		this(TABLE_NAME, KEY_NAME);

		m_key.assignValue(outputDocumentId);

		load();
	}

	public TaskOutputSourceDocument(String name, byte[] data)
	{
		this(name, EntityType.UNKNOWN, DataType.UNKNOWN, DataFormat.UNKNOWN, data, false);
	}

	public TaskOutputSourceDocument(String name, EntityType entity, DataType dataType, DataFormat format, byte[] data, boolean validated)
	{
		this(TABLE_NAME, KEY_NAME);

		setDataFormat(format);
		setDataType(dataType);
		setEntityType(entity);
		setName(name);
		setValidated(validated);

		m_sourceDocument = new SourceDocumentRow(data);
	}

	public TaskOutputSourceDocument(String name, InputStream data) throws IOException
	{
		this(name, EntityType.UNKNOWN, DataType.UNKNOWN, DataFormat.UNKNOWN, data, false);
	}

	public TaskOutputSourceDocument(String name, EntityType entity, DataType dataType, DataFormat format, InputStream data, boolean validated) throws IOException
	{
		this(TABLE_NAME, KEY_NAME);

		setDataFormat(format);
		setDataType(dataType);
		setEntityType(entity);
		setName(name);
		setValidated(validated);

		m_sourceDocument = new SourceDocumentRow(data);
	}

	TaskOutputSourceDocument(Connection dbConn, long outputDocumentId) throws IOException, SQLException
	{
		this(TABLE_NAME, KEY_NAME);

		m_key.assignValue(outputDocumentId);

		load(dbConn);
	}

	private TaskOutputSourceDocument(String tableName, String keyName)
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_outputId, m_dataFormat, m_dataType, m_entityType, m_name, m_validated, m_sourceDocumentId);
	}


	// public methods


	public long getOutputDocumentId()
	{
		return m_key.getValue();
	}

	public long getOutputId()
	{
		return m_outputId.getValue();
	}

	@Override
	public DataFormat getDataFormat()
	{
		return DataFormat.valueOf(m_dataFormat.getValue());
	}

	public void setDataFormat(DataFormat dataFormat)
	{
		m_dataFormat.setValue(dataFormat.toString());
	}

	@Override
	public DataType getDataType()
	{
		return DataType.valueOf(m_dataType.getValue());
	}

	public void setDataType(DataType dataType)
	{
		m_dataType.setValue(dataType.toString());
	}

	@Override
	public EntityType getEntityType()
	{
		return EntityType.valueOf(m_entityType.getValue());
	}

	public void setEntityType(EntityType entityType)
	{
		m_entityType.setValue(entityType.toString());
	}

	@Override
	public SourceDocumentType getType()
	{
		return new SourceDocumentType(getEntityType(), getDataType(), getDataFormat());
	}

	@Override
	public String getName()
	{
		return m_name.getValue();
	}

	public void setName(String name)
	{
		m_name.setValue(name);
	}

	@Override
	public boolean isValidated()
	{
		return m_validated.getValue();
	}

	public void setValidated(Boolean validated)
	{
		m_validated.setValue(validated);
	}

	@Override
	public void setValidated()
	{
		setValidated(true);
	}

	@Override
	public byte[] getData()
	{
		return m_sourceDocument.getData();
	}

	@Override
	public InputStream getDataAsStream() throws IOException, SQLException
	{
		return m_sourceDocument.getDataAsStream();
	}

	@Override
	public long getDataLength()
	{
		return m_sourceDocument.getDataLength();
	}

	public void setData(byte[] data)
	{
		m_sourceDocument.setData(data);
	}

	public void setData(String data)
	{
		setData(data.getBytes());
	}

	public void setData(Serializable data) throws IOException
	{
		setData(IOUtils.toBytes(data));
	}

	public void setData(InputStream data) throws IOException
	{
		m_sourceDocument.setData(data);
	}

	@Override
	public long getSourceDocumentId()
	{
		return m_sourceDocumentId.getValue();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof TaskOutputSourceDocument == false)
			return false;

		TaskOutputSourceDocument otherDoc = (TaskOutputSourceDocument) other;

		if (isNew() || otherDoc.isNew())
			return false;

		return getSourceDocumentId() == otherDoc.getSourceDocumentId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getSourceDocumentId())).hashCode();
	}

	@Override
	public int compareTo(TaskOutputSourceDocument other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getSourceDocumentId() - other.getSourceDocumentId());
	}


	// package methods


	void setOutputId(Long outputId)
	{
		m_outputId.setValue(outputId);
	}

	@Override
	void save(Connection dbConn) throws IOException, SQLException
	{
		m_sourceDocument.save(dbConn);

		m_sourceDocumentId.setValue(m_sourceDocument.getSourceDocumentId());

		super.save(dbConn);
	}

	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_sourceDocument = new SourceDocumentRow(dbConn, m_sourceDocumentId.getValue());
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		delete(dbConn, getKey(), m_sourceDocumentId.getValue());

		m_key.reset();
	}

	static List<TaskOutputSourceDocument> findOutputDocuments(Connection dbConn, long outputId) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT OUTPUT_DOCUMENT_ID FROM " + TABLE_NAME + " WHERE OUTPUT_ID = ?");
		ResultSet outputRows = null;

		try {
			selectStmt.setLong(1, outputId);

			outputRows = selectStmt.executeQuery();

			List<TaskOutputSourceDocument> outputDocs = new ArrayList<TaskOutputSourceDocument>();

			while (outputRows.next())
				outputDocs.add(new TaskOutputSourceDocument(dbConn, outputRows.getLong(1)));

			return outputDocs;
		}
		finally {
			if (outputRows != null)
				outputRows.close();

			selectStmt.close();
		}
	}

	static void delete(Connection dbConn, long outputDocumentId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion(KEY_NAME, outputDocumentId);
		long sourceDocumentId = getSourceDocId(dbConn, key);

		delete(dbConn, key, sourceDocumentId);
	}


	// private methods


	private static void delete(Connection dbConn, Criterion outputDocumentKey, long sourceDocumentId) throws IOException, SQLException
	{
		(new DeleteOp(TABLE_NAME, outputDocumentKey)).execute(dbConn);

		SourceDocumentRow.delete(dbConn, sourceDocumentId);
	}

	private static long getSourceDocId(Connection dbConn, Criterion outputDocumentKey) throws IOException, SQLException
	{
		Column<Long> sourceDocumentId = new LongColumn("SOURCE_DOCUMENT_ID", false);

		(new SelectOp(TABLE_NAME, outputDocumentKey, sourceDocumentId)).execute(dbConn);

		return sourceDocumentId.getValue();
	}
}
