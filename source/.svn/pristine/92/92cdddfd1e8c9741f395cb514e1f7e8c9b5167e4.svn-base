/*
 * FastaReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 * 
 */
public class FastaReader extends BaseSourceDocumentReader {

	private static Log log = LogFactory.getLog(FastaReader.class);
	private static Pattern headerPattern = Pattern.compile("^(>.*)$",
			Pattern.MULTILINE);

	public FastaReader(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	private void init() {
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.FASTA));
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.FASTA));
	}

	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		RecordFilter filter = conversionRegistry.getRecordFilter(document
				.getDataFormat());
		return read(document, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#read(org.ngbw.sdk.api.core.SourceDocument)
	 */
	private GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document, RecordFilter filter) throws IOException, SQLException, ParseException {
		if (document.isValidated() == false
				&& checkType(document.getType()) == false)
			throw new RuntimeException("Wrong SourceDocumentType! "
					+ document.getType());
		SourceDocumentType documentType = document.getType();
		String data = new String(document.getData());
		GenericDataRecordCollection<IndexedDataRecord> drc = this
				.getDataRecordCollection(document);
		BufferedReader br = new BufferedReader(new StringReader(data));
		filter.setInput(br);
		int index = 0;
		while (filter.hasNext()) {
			String recordSource = filter.next();
			SourceDocument recordDoc = new SourceDocumentBean(documentType,
					recordSource.getBytes());
			recordDoc.setValidated();
			drc.add(readSingle(index, recordDoc));
			index++;
		}
		return drc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#readSingle(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public DataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException {
		return readSingle(0, document);
	}

	private IndexedDataRecord readSingle(int index, SourceDocument document) throws IOException, SQLException, ParseException {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		if (checkType(document.getType()) == false)
			throw new RuntimeException("Wrong SourceDocumentType! "
					+ document.getType());
		if (checkEntityType(document) == false)
			throw new RuntimeException("Data do not match EntityType: "
					+ document.getType().getEntityType());
		String data = new String(document.getData());
		String header = null;
		Matcher matcher = headerPattern.matcher(data);
		if (matcher.find())
			header = matcher.group(1);
		if (header == null)
			throw new RuntimeException(
					"Invalid input no descriptive header staring with '>'!");
		IndexedDataRecord dataRecord = getDataRecord(index, document);
		dataRecord.getField(RecordFieldType.NAME).setValue(header.replace(">", ""));
		return dataRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#validate(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public ValidationResult validate(SourceDocument document) {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		ValidationResult result = new ValidationResult();
		if (checkType(document.getType()) == false) {
			result.addError("Wrong SourceDocumentType! " + document.getType());
			if (log.isWarnEnabled())
				log.warn("Wrong SourceDocumentType! " + document.getType());
			return result;
		}
		try {
			read(document);
		} catch (Exception e) {
			if (log.isWarnEnabled())
				log.warn("Error reading data: " + e.toString());
			result.addError(e.toString());
		}
		return result;
	}

	private boolean checkEntityType(SourceDocument document) throws IOException, SQLException {
		String data = new String(document.getData());
		DataFormat dataFormat = document.getDataFormat();
		EntityType entityType = document.getEntityType();
		EntityType validatedEntityType = DefaultSequenceParser.getEntityType(
				data, dataFormat);
		if (entityType.equals(validatedEntityType) == false) {
			if (log.isWarnEnabled())
				log.warn("Data do not match EntityType: "
						+ document.getType().getEntityType()
						+ " the validated EntityType is: "
						+ validatedEntityType);
			return false;
		}
		return true;
	}

	private boolean checkType(SourceDocumentType type) {
		if (type == null)
			throw new NullPointerException(
					"SourceDocumentType must not be null!");
		return (type.getEntityType().equals(EntityType.PROTEIN) || type.getEntityType().equals(EntityType.NUCLEIC_ACID))
				&& type.getDataType().equals(DataType.SEQUENCE)
				&& type.getDataFormat().equals(DataFormat.FASTA);
	}
}
