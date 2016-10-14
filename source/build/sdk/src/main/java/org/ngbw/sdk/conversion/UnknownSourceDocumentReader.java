/*
 * UnknownSourceDocumentReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.IOException;
import java.sql.SQLException;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class UnknownSourceDocumentReader extends BaseSourceDocumentReader {

	private void init() {
		srcTypes.add(getSourceDocumentType(EntityType.UNKNOWN, DataType.UNKNOWN, DataFormat.UNKNOWN));
	}

	/**
	 * @param conversionRegistry
	 */
	public UnknownSourceDocumentReader(ConversionRegistry conversionRegistry) {
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
		record.getField(RecordFieldType.NUMBER_OF_BYTES).setValue(document.getData().length);
		return record;
	}

	/* (non-Javadoc)
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#validate(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public ValidationResult validate(SourceDocument document) {
		// just mock
		ValidationResult result = new ValidationResult();
		return result;
	}

}
