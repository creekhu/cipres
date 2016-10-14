/*
 * SourceDocument.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;


/**
 * A <code>SourceDocument</code> is a container for raw information - typically a
 * flat file record. The data are augmented a set of centrally registered 
 * meta data items consisting of <code>DataFormat</code>, <code>DataType</code>
 * and <code>EntityType</code>.
 *
 * @author Roland H. Niedner
 * 
 */
public interface SourceDocument {

	/**
	 * Method returns whether the data in the <code>SourceDocument</code> have 
	 * been validated to actually fit the allocated semantic key.
	 * 
	 * @return validated
	 */
	public boolean isValidated();

	/**
	 * Method sets the validated flag to <code>true</code>.
	 */
	public void setValidated();

	/**
	 * Method returns the name of the document
	 * 
	 * @return name of the document
	 */
	public String getName();

	/**
	 * Method returns the raw data.
	 * 
	 * @return data
	 */
	public byte[] getData() throws IOException, SQLException;

	/**
	 * Method returns an <code>InputStream</code> object backed by the raw data.
	 * 
	 * @return data
	 */
	public InputStream getDataAsStream() throws IOException, SQLException;

	/**
	 * Method returns the length in bytes of the raw data
	 * 
	 * @return length of the data
	 */
	public long getDataLength() throws IOException, SQLException;

	/**
	 * Method returns the <code>DataFormat</code> of the raw data.
	 * 
	 * @return dataFormat
	 */
	public DataFormat getDataFormat();

	/**
	 * Method returns the <code>DataType</code> of the raw data.
	 * 
	 * @return dataType
	 */
	public DataType getDataType();

	/**
	 * Method returns the <code>EntityType</code> of the raw data.
	 * 
	 * @return biotype
	 */
	public EntityType getEntityType();

	/**
	 * Method returns the <code>SourceDocumentType</code> for this <code>SourceDocument</code>
	 * (bundling <code>EntityType</code>, <code>DataType</code> and <code>DataFormat</code>).
	 * 
	 * @return semanticKey
	 */
	public SourceDocumentType getType();

	/**
	 * 
	 * @return primary key for the source document
	 */
	public long getSourceDocumentId();
}
