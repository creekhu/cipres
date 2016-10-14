/*
 * SourceDocumentBean.java
 */
package org.ngbw.sdk.core.shared;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 * 
 */
public class SourceDocumentBean implements SourceDocument {

	private final String name;
	private final byte[] data;
	private final SourceDocumentType sourceDocumentType;
	private boolean validated = false;


	public SourceDocumentBean(EntityType entityType, DataType dataType, DataFormat dataFormat, byte[] data) {
		this(entityType, dataType, dataFormat, null, data, false);
	}

	public SourceDocumentBean(EntityType entityType, DataType dataType, DataFormat dataFormat, byte[] data, boolean validated) {
		this(entityType, dataType, dataFormat, null, data, validated);
	}

	public SourceDocumentBean(EntityType entityType, DataType dataType, DataFormat dataFormat, String name, byte[] data) {
		this(entityType, dataType, dataFormat, name, data, false);
	}

	public SourceDocumentBean(EntityType entityType, DataType dataType, DataFormat dataFormat, String name, byte[] data, boolean validated) {
		if (entityType == null)
			throw new NullPointerException("EntityType cannot be null!");
		if (dataType == null)
			throw new NullPointerException("DataType cannot be null!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat cannot be null!");
		if (data == null)
			throw new NullPointerException("Data cannot be null!");
		this.sourceDocumentType = new SourceDocumentType(entityType, dataType, dataFormat);
		this.data = data;
		this.validated = validated;
		this.name = name;
	}

	public SourceDocumentBean(SourceDocumentType sourceDocumentType, byte[] data) {
		this(sourceDocumentType, null, data, false);
	}

	public SourceDocumentBean(SourceDocumentType sourceDocumentType, byte[] data, boolean validated) {
		this(sourceDocumentType, null, data, validated);
	}

	public SourceDocumentBean(SourceDocumentType sourceDocumentType, String name, byte[] data) {
		this(sourceDocumentType, name, data, false);
	}

	public SourceDocumentBean(SourceDocumentType sourceDocumentType, String name, byte[] data, boolean validated) {
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType cannot be null!");
		if (data == null)
			throw new NullPointerException("Data cannot be null!");
		this.sourceDocumentType = sourceDocumentType;
		this.data = data;
		this.validated = validated;
		this.name = name;
	}

	public boolean isValidated() {
		return validated;
	}

	public String getName() {
		return name;
	}

	public byte[] getData() {
		return data;
	}

	public InputStream getDataAsStream() throws IOException, SQLException {
		if (data == null)
			return null;

		return new ByteArrayInputStream(data);
	}

	public long getDataLength() throws IOException, SQLException {
		if (data == null)
			return 0;

		return data.length;
	}

	public EntityType getEntityType() {
		return sourceDocumentType.getEntityType();
	}

	public DataType getDataType() {
		return sourceDocumentType.getDataType();
	}

	public DataFormat getDataFormat() {
		return sourceDocumentType.getDataFormat();
	}

	public SourceDocumentType getType() {
		return sourceDocumentType;
	}

	public String toString() {
		return new String(data);
	}

	public void setValidated() {
		this.validated = true;
	}

	public long getSourceDocumentId() {
		return 0;
	}
}
