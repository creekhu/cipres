/*
 * BlastTEXTReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.bioutils.blast.blasttxt.BlastResult;
import org.ngbw.sdk.bioutils.blast.blasttxt.BlastTEXTParser;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
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
public class BlastTEXTReader extends BaseSourceDocumentReader {

	private void init() {
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.BLAST_OUTPUT, DataFormat.BLAST_TEXT));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.BLAST_OUTPUT, DataFormat.BLAST_TEXT));
	}
	
	public BlastTEXTReader(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	public ValidationResult validate(SourceDocument document) throws IOException, SQLException {
		if(document == null || document.getData() == null)
			throw new RuntimeException("No data!");
		ValidationResult result = new ValidationResult();
		try {
			parse(document);
		} catch (Exception e) {
			result.addError(e.toString());
		}
		if (result.isValid()) document.setValidated();
		return result;
	}

	@SuppressWarnings("unchecked")
	public GenericDataRecordCollection read(SourceDocument document) throws IOException, SQLException, ParseException {
		if(document == null || document.getData() == null)
			throw new RuntimeException("No data!");
		GenericDataRecordCollection drc = getDataRecordCollection(document);
		List<BlastResult> results = parse(document);
		if(results == null || results.size() == 0)
			throw new RuntimeException("No data returned from parser!");
		//output some blast details
		for (int i=0; i<results.size(); i++) {
			BlastResult result = results.get(i);
			IndexedDataRecord dr = getDataRecord(i, document);
			populateDataRecord(dr, result);
			drc.add(dr);
		}
		return drc;
	}

	@SuppressWarnings("unchecked")
	public IndexedDataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException {
		List<BlastResult> results = parse(document);
		if(results.size() > 1)
			throw new RuntimeException("More than 1 query listed in the SourceDocument!");
		IndexedDataRecord dr = getDataRecord(0, document);
		populateDataRecord(dr, results.get(0));
		return dr;
	}

	@SuppressWarnings("unchecked")
	public List<BlastResult> parse(SourceDocument document) throws IOException, SQLException {
		if(document == null || document.getData() == null)
			throw new RuntimeException("No data!");
		String data = new String(document.getData());
		BufferedReader br = new BufferedReader(new StringReader(data));
		List<BlastResult> results = new ArrayList<BlastResult>();
		RecordFilter filter = conversionRegistry.getRecordFilter(document.getDataFormat());
		filter.setInput(br);
		while (filter.hasNext()) {
			String record = filter.next();
			if (record == null || record.trim().length() == 0)
				continue;
			results.add(parseRecord(record));
		}
		filter.close();
		if (results.isEmpty())
			throw new RuntimeException("Document does not contain Blast text output!");
		return results;
	}
	
	public BlastResult parseRecord(String record) {
		return BlastTEXTParser.parse(record);
	}
	
	private void populateDataRecord(DataRecord dataRecord, BlastResult br) throws ParseException {
		dataRecord.getField(RecordFieldType.DATASET).setValue(br.DATASET);
		dataRecord.getField(RecordFieldType.SCORING_MATRIX).setValue(br.SCORING_MATRIX);
		dataRecord.getField(RecordFieldType.PROGRAM).setValue(br.PROGRAM);
		dataRecord.getField(RecordFieldType.PROGRAM_VERSION).setValue(br.PROGRAM_VERSION);
		dataRecord.getField(RecordFieldType.NUMBER_OF_LETTERS).setValue(br.NUMBER_OF_LETTERS);
		dataRecord.getField(RecordFieldType.NUMBER_OF_SEQUENCES).setValue(br.NUMBER_OF_SEQUENCES);
		dataRecord.getField(RecordFieldType.GAP_PENALTY).setValue(br.GAP_PENALTY);
		dataRecord.getField(RecordFieldType.GAP_EXTENSION_PENALTY).setValue(br.GAP_EXTENSION_PENALTY);
		dataRecord.getField(RecordFieldType.QUERY).setValue(br.QUERY);
		dataRecord.getField(RecordFieldType.NUMBER_OF_HITS).setValue(br.hits.size());
	}
}
