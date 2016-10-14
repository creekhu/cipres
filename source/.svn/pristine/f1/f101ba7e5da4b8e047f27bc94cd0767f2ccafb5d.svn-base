/*
 * ConversionService.java 
 */
package org.ngbw.sdk.api.conversion;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Set;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * The <tt>ConversionService</tt> details the basic methods for reading and converting
 * SourceDocuments, that come in one of the registered DataFormats. It is
 * assumed that implementing classes will have access to the central
 * <tt>CoreRegistry</tt> to look up the required metadata such as <tt>EntityType</tt> and <tt>DataType</tt>.
 * The actual parsing and converting is done by DataFormatConverters and 
 * DataFormatReaders that are registered for one or several DataFormats.
 * Thus the <tt>ConversionService</tt> merely provides a common facade for all registered 
 * parsers and converters.<br>
 * 
 * @author R. Hannes Niedner
 * 
 */
public interface ConversionService {
	
	/**
	 * @return conversionRegistry
	 */
	public ConversionRegistry getConversionRegistry();

	/**
	 * Parse a SourceDocument instance that contains a one or more entries (eg. single sequence fasta file)
	 * and populate a DataRecordCollection.
	 * 
	 * @param srcDocument
	 * @return dataRecordCollection
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument srcDocument) throws IOException, SQLException, ParseException;

	/**
	 * This method will parse a SourceDocument and reassemble the data into the
	 * submitted target SourceDocumentType. This conversion will only then lead to a successful result
	 * if the information content of source format <= target format.
	 * 
	 * @param srcDocument
	 * @param targetType
	 * @return sourceDocument
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public SourceDocument convert(SourceDocument srcDocument, SourceDocumentType targetType) throws IOException, SQLException, ParseException;
	
	/**
	 * This method will parse a SourceDocument and reassemble the data into the
	 * submitted target SourceDocumentType. This conversion will only then lead to a successful result
	 * if the information content of source format <= target format. The second argument will
	 * override the SourceDocumentType of the submitted SourceDocument.
	 * 
	 * @param srcDocument
	 * @param sourceType
	 * @param targetType
	 * @return sourceDocument
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public SourceDocument convert(SourceDocument srcDocument, SourceDocumentType sourceType, SourceDocumentType targetType) throws IOException, SQLException, ParseException;

	/**
	 * This method will parse all SourceDocuments of the submitted input collection
	 * and reassemble all of them into one new SourceDocuments of the
	 * submitted target SourceDocumentType. This conversion will only then lead to a successful result
	 * if the information content of source format <= target format.
	 * 
	 * @param srcDocuments
	 * @param targetType
	 * @return sourceDocument
	 * @throws IOException
	 * @throws SQLException
	 */
	public SourceDocument convert(Collection<SourceDocument> srcDocuments, SourceDocumentType targetType) throws IOException, SQLException, ParseException;

	/**
	 * Method returns all target SourceDocumentType that the submitted
	 * source DataFormat can be converted into.
	 * 
	 * @param sourceDocumentType
	 * @return targetSemanticKeys
	 */
	public Set<SourceDocumentType> getTargetSourceDocumentTypes(SourceDocumentType sourceDocumentType);
	
	/**
	 * Method checks whether there is a SourceDocumentReader registered in the 
	 * ConversionService for the submitted SourceDocumentType. 
	 * 
	 * @param sourceDocumentType
	 * @return canRead
	 */
	public boolean canRead(SourceDocumentType sourceDocumentType);
	
	/**
	 * Method checks the submitted SourceDocument whether the data are indeed formatted
	 * in the declared SourceDocumentType. It will also set the flag of the sourceDocument to
	 * validated = true if the check is successful.
	 * 
	 * @param srcDocument
	 * @return isValid
	 * @throws IOException
	 * @throws SQLException
	 */
	public boolean validate(SourceDocument srcDocument) throws IOException, SQLException;
	
	/**
	 * Method tries to read the submitted SourceDocument's 
	 * data with the DataFormatReader registered for the submitted
	 * DataFormat, ignoring the DataFormat specified in the SourceDocument's metadata.
	 * If the DataFormatReader can parse the data successfully 
	 * the method returns true. If no DataFormatReader is registered for the
	 * submitted DataFormat the method will throw an Exception.
	 * 
	 * @param srcDocument
	 * @param targetType
	 * @return valid
	 * @throws IOException
	 * @throws SQLException
	 */
	public boolean check(SourceDocument srcDocument, SourceDocumentType targetType) throws IOException, SQLException;
	
	/**
	 * Method calls the the check(SourceDocument, SourceDocumentType)
	 * for each of the SemanticKeys in the submitted set, ignoring the SourceDocumentType 
	 * specified in the SourceDocument's metadata. 
	 * The method returns the first DataFormat that checked out ok. If no such
	 * format exists the method returns null.
	 *  
	 * @param srcDocument
	 * @param semanticKeys
	 * @return validSemanticKey
	 * @throws IOException
	 * @throws SQLException
	 */
	public SourceDocumentType check(SourceDocument srcDocument, Set<SourceDocumentType> semanticKeys) throws IOException, SQLException;
	
	/**
	 * Method tries to determine the SourceDocumentType by checking it against all
	 * available SourceDocumentReaders. If no SourceDocumentReader can read the document
	 * null is returned.
	 * 
	 * @param srcDocument
	 * @return type
	 * @throws IOException
	 * @throws SQLException
	 */
	public SourceDocumentType detectSourceDocument(SourceDocument srcDocument) throws IOException, SQLException;
	
	/**
	 * Parse a SourceDocument instance that contains a single entry (eg. single sequence fasta file)
	 * and populate a DataRecord instance. This method is for special purpose only as it requires the
	 * caller to know whether the SourceDocument contains indeed only one entry.
	 * 
	 * @param srcDocument
	 * @return dataRecord
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public DataRecord readSingle(SourceDocument srcDocument) throws IOException, SQLException, ParseException;
	
	/**
	 * Method checks whether there is a converter registered in the ConversionService
	 * that can submitted SourceDocument can be converted into the target format.
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return true if the SourceDocument can be converted into the target format.
	 */
	public boolean canConvert(SourceDocumentType sourceType, SourceDocumentType targetType);
	/**
	 * Method checks whether the submitted SourceDocument can be converted into the target format.
	 * 
	 * @param srcDocument
	 * @param targetType
	 * @return true if the SourceDocument can be converted into the target format.
	 */
	public boolean canConvert(SourceDocument srcDocument, SourceDocumentType targetType);
	
	/**
	 * Method returns whether there is a RecordFilter registered for
	 * the submitted DataFormat.
	 * 
	 * @param dataFormat
	 * @return hasFilter
	 */
	public boolean hasRecordFilter(DataFormat dataFormat);

	/**
	 * Method returns a filter that can filter individual records from
	 * a CharacterStream (BufferedReader).
	 * 
	 * @param dataFormat
	 * @return filter
	 */
	public RecordFilter getRecordFilter(DataFormat dataFormat);
	
	/**
	 * Method returns whether a sequence can be extracted from the
	 * submitted SourceDocument.
	 * 
	 * @param srcDocument
	 * @return canExtract
	 * @throws IOException
	 * @throws SQLException
	 */
	public boolean canExtractSequence(SourceDocument srcDocument) throws IOException, SQLException;
	
	/**
	 * Method returns the extracted sequence as a plain String from the
	 * submitted SourceDocument.
	 * 
	 * @param srcDocument
	 * @return sequence
	 * @throws IOException
	 * @throws SQLException
	 */
	public String extractSequence(SourceDocument srcDocument) throws IOException, SQLException;

	/**
	 * Method returns the extracted sequence as a plain String from the
	 * submitted single record String of the submitted DataFormat.
	 * 
	 * @param dataFormat
	 * @param sourceRecord
	 * @return sequence
	 */
	public String extractSequence(DataFormat dataFormat, String sourceRecord);
}