package org.ngbw.sdk.api.conversion;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.core.shared.SourceDocumentType;


/**
 * A SourceDocumentConverter implements the capability to 
 * transform (text) data from one SourceDocumentType into another. This
 * functionality is then plugged into the ConversionService via 
 * a configuration (file).<br>
 * 
 * @author R. Hannes Niedner
 */
public interface SourceDocumentConverter {
	
	/**
	 * Method is used by the Registry to retrieve a map that
	 * lists all target SourceDocumentTypes keyed to their SourceDocumentType
	 * like key: srcSourceDocumentType value: targetSourceDocumentType
	 * 
	 * @return conversions
	 */
	public Map<SourceDocumentType, Set<SourceDocumentType>> getSupportedConversions();
	
	/**
	 * Convert the data in the submitted SourceDocument into the
	 * submitted targetFormat.
	 * 
	 * @param srcDoc
	 * @param targetKey
	 * @return convertedSourceDocument
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public SourceDocument convert(SourceDocument srcDoc, SourceDocumentType targetKey) throws IOException, SQLException, ParseException;
	
	/**
	 * Method returns whether the converter can read the source data. 
	 * 
	 * @param srcDoc
	 * @return validated
	 */
	public boolean canRead(SourceDocument srcDoc);
	
	/**
	 * Method returns whether the converter can transform the data into the desired target SourceDocumentType.
	 * 
	 * @param targetKey
	 * @return true or false
	 */
	public boolean canWrite(SourceDocumentType targetKey);
}
