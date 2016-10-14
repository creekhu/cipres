/*
 * SequenceAlignmentReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.IOException;
import java.sql.SQLException;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.bioutils.alignment.Alignment;
import org.ngbw.sdk.bioutils.alignment.AlignmentParser;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * Deploy the AlignmentParser to read aligned sequence documents into
 * DataRecords.
 * 
 * @author Roland H. Niedner
 *
 */
public class SequenceAlignmentReader extends BaseSourceDocumentReader {

	/**
	 * @param conversionRegistry
	 */
	public SequenceAlignmentReader(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#read(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException {
		GenericDataRecordCollection<IndexedDataRecord> drc = getDataRecordCollection(document);
		drc.add(readSingle(document));
		return drc;
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#readSingle(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public IndexedDataRecord readSingle(SourceDocument document) throws IOException, SQLException {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		IndexedDataRecord record = getDataRecord(0, document);
		Alignment aln = AlignmentParser.parseAlignment(new String(document.getData()));
		record.getField(RecordFieldType.NUMBER_OF_ALIGNED_SEQUENCES).setValue(aln.getSequenceCount());
		record.getField(RecordFieldType.LENGTH).setValue(aln.getSiteCount());
		return record;
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#validate(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public ValidationResult validate(SourceDocument document) {
		ValidationResult result = new ValidationResult();
		Alignment aln;
		try {
			aln = AlignmentParser.parseAlignment(new String(document.getData()));
			if (aln == null || aln.getSequenceCount() == 0)
				result.addError("no data parsed");
		} catch (Exception e) {
			result.addError(e.toString());
		}
		return result;
	}

	private void init() {
		// register formats that can be read and
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE_ALIGNMENT, DataFormat.FASTA));
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE_ALIGNMENT, DataFormat.NEEDLE));
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE_ALIGNMENT, DataFormat.PHYLIP));
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE_ALIGNMENT, DataFormat.NEXUS));
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE_ALIGNMENT, DataFormat.CLUSTAL));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE_ALIGNMENT, DataFormat.FASTA));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE_ALIGNMENT, DataFormat.NEEDLE));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE_ALIGNMENT, DataFormat.PHYLIP));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE_ALIGNMENT, DataFormat.NEXUS));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE_ALIGNMENT, DataFormat.CLUSTAL));
	}

}
