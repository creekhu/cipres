/*
 * SourceDocumentReader.java
 */
package org.ngbw.sdk.api.conversion;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Set;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * A SourceDocumentReader implements the parsing of SourceDocuments into
 * DataRecords and DataRecordCollections.
 *
 * @author Roland H. Niedner
 * 
 */
public interface SourceDocumentReader extends ConversionRegistryAware {

	/**
	 * Method used by the ConversionRegistry to interrogate the Reader which
	 * SourceDocumentType can be read(parsed into DataRecords) by this reader
	 * 
	 * @return readables
	 */
	public Set<SourceDocumentType> getSupportedTypes();
	
	/**
	 * Method checks the submitted SourceDocument whether the data are indeed formatted
	 * in the declared DataFormat. The returned ValidationResult contains possible
	 * messages and/or errors from the parser.Method also
	 * set the validated Flag of the submitted SourceDocument to true if
	 * the document is indeed valid. 
	 * 
	 * @param document
	 * @return validationResult
	 * @throws IOException
	 * @throws SQLException
	 */
	public ValidationResult validate(SourceDocument document) throws IOException, SQLException;
	
	/**
	 * Parse all contained DataRecords from the submitted SourceDocument and return
	 * them in a corresponding DataRecordCollection.
	 * 
	 * @param document
	 * @return dataRecordCollection
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException;
	
	/**
	 * Parse a single DataRecord from the submitted SourceDocument. A
	 * RuntimeException is thrown when there is more than one DataRecord
	 * in the SourceDocument.
	 * 
	 * @param document
	 * @return dataRecord
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public DataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException;
}
