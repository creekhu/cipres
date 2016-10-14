/*
 * SequenceFormatConverter.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.RecordField;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class SequenceFormatConverter extends BaseSourceDocumentConverter {
	
	public SequenceFormatConverter(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	public SourceDocument convert(SourceDocument srcDoc, SourceDocumentType targetType) throws IOException, SQLException, ParseException {
		if (srcDoc == null || srcDoc.getData() == null) {
			throw new RuntimeException("No input data!");
		}
		if (targetType == null) {
			throw new RuntimeException("You need to specify a target SourceDocumentType!");
		}
		SourceDocumentType sourceType = srcDoc.getType();
		//nothing to be done
		if(targetType.equals(sourceType))
			return srcDoc;
		EntityType srcEntityType = srcDoc.getEntityType();
		DataType srcDataType = srcDoc.getDataType();
		EntityType targetEntityType = targetType.getEntityType();
		DataType targetDataType = targetType.getDataType();
		//can only deal with Sequence DataType
		if (DataType.SEQUENCE.equals(srcDataType) == false)
			throw new RuntimeException("Can only convert documents with DataType.SEQUENCE");
		if (DataType.SEQUENCE.equals(targetDataType) == false)
			throw new RuntimeException("Can only convert documents to DataType.SEQUENCE");
		if (srcEntityType.equals(targetEntityType) == false)
			throw new RuntimeException("Cannot convert EntityTypes: src and target EntityType must be equal!");
		if(canRead(srcDoc) == false)
			throw new RuntimeException("Can't read input with SourceDocumentType! " + sourceType);
		if(canWrite(targetType) == false)
			throw new RuntimeException("Can't write target with SourceDocumentType! " + targetType);

		if (srcDoc.getDataFormat() == DataFormat.PDB_FINDER)
			return convertPdbFinderToFasta(srcDoc, targetType);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(srcDoc.getData())));
		DataFormat dataFormat = sourceType.getDataFormat();
		RecordFilter filter = conversionRegistry.getRecordFilter(sourceType.getDataFormat());
		filter.setInput(br);
		SourceDocumentReader reader = conversionRegistry.getReader(sourceType);
		try {
			while (filter.hasNext()) {
				String sourceRecord = filter.next();
				SourceDocument document = new SourceDocumentBean(sourceType, sourceRecord.getBytes());
				DataRecord dr = reader.readSingle(document);
				String sequence = DefaultSequenceParser.extractSequence(sourceRecord, dataFormat);
				String label = writeFastaLabel(dr, dataFormat);
				FastaWriter.write(out, label, sequence);
			} // end while
		} catch (IOException e) {
			throw new RuntimeException("Can't write to output data", e);
		}
		
		
		SourceDocumentBean targetDoc = new SourceDocumentBean(targetType, out.toByteArray(), true);
		return targetDoc;
	}
	
	private String writeFastaLabel(DataRecord dr, DataFormat df) {
		StringBuffer sb = new StringBuffer();
		sb.append("ngbw|");
		sb.append(dr.getField(RecordFieldType.PRIMARY_ID).getValueAsString());
		sb.append("|");
		RecordField dataset = dr.getField(RecordFieldType.DATASET);

		if (dataset == null)
			sb.append(df.toString() + "|");
		else
			sb.append(dataset.getValueAsString() + "|");

		sb.append(dr.getField(RecordFieldType.NAME).getValueAsString());
		return sb.toString();
	}

	private void init() {
		addConversion(SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.UNIPROT),
				SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.FASTA));
		addConversion(SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.GENPEPT),
				SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.FASTA));
		addConversion(SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.PDB_FINDER),
				SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.FASTA));
		addConversion(SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.GENBANK),
				SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.FASTA));
		addConversion(SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.EMBL),
				SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.FASTA));
		addConversion(SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.PDB_FINDER),
				SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.FASTA));
	}

	private SourceDocument convertPdbFinderToFasta(SourceDocument srcDoc, SourceDocumentType targetType) throws IOException, SQLException
	{
		PdbFinderParser parser = new PdbFinderParser();

		parser.setInput(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(srcDoc.getData()))));

		SequenceRecord record;
		StringBuilder converted = new StringBuilder();

		while ((record = parser.nextRecord()) != null) {
			converted.append(">ngbw|");
			converted.append(record.primaryId);
			converted.append("|PDBSEQ|");
			converted.append(record.organism);
			converted.append('|');
			converted.append(record.name);
			converted.append('\n');

			if (record.sequence != null) {
				converted.append(record.sequence);
				converted.append('\n');
			}
		}

		return new SourceDocumentBean(targetType, converted.toString().getBytes(), true);
	}
}
