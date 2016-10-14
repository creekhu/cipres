/*
 * BlastOutput2SequenceBlastHitTransformer.java
 */
package org.ngbw.sdk.core.transformation;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.bioutils.blast.blasttxt.BlastHit;
import org.ngbw.sdk.bioutils.blast.blasttxt.BlastResult;
import org.ngbw.sdk.bioutils.blast.blasttxt.BlastTEXTParser;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.core.shared.DatasetId;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class BlastOutput2SequenceBlastHitTransformer extends ParseAndSearchTransformer {

	private GenericDataRecordCollection<IndexedDataRecord> dataRecordCollection;
	private BlastResult blast;
	
	public BlastOutput2SequenceBlastHitTransformer(ServiceFactory serviceFactory,
			SourceDocument srcDocument, RecordType targetType) throws IOException, SQLException {
		super(serviceFactory, srcDocument, targetType);
	}

	@Override
	protected void transform(SourceDocument srcDocument) throws IOException, SQLException {
		if (srcDocument == null)
			throw new NullPointerException("SourceDocument is null!");
		blast = BlastTEXTParser.parse(srcDocument.getData());
	}

	public int getTotalDataRecordCount() {
		if (blast == null)
			throw new NullPointerException("Blast result not initialized is null!");
		if (blast.hits == null)
			return 0;
		return blast.hits.size();
	}

	@Override
	public GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection() throws ParseException {
		if (dataRecordCollection == null || dataRecordCollection.size() < this.getTotalDataRecordCount()) {
			dataRecordCollection = newDataRecordCollection();
			IndexedDataRecord record;
			for (int i=0; i<blast.hits.size(); i++) {
				BlastHit hit = blast.hits.get(i);
				String dataset = hit.DATASET.equals("PDB") ? "PDBSEQ" : hit.DATASET;
				DatasetId datasetId = new DatasetId(dataset, hit.PRIMARY_ID);
				record = newDataRecord(i, findDatasetRecord(datasetId));
				record.getField(RecordFieldType.SCORE).setValue(hit.SCORE);
				record.getField(RecordFieldType.E_VALUE).setValue(hit.E_VALUE);
				record.getField(RecordFieldType.DATASET).setValue(datasetId.dataset);
				record.getField(RecordFieldType.QUERY).setValue(blast.QUERY);
				dataRecordCollection.add(record);
			}
		}
		return dataRecordCollection;
	}

	public GenericDataRecordCollection<IndexedDataRecord> getDataRecordCollection(
			int fromIndex, int toIndex) throws ParseException {
		if (fromIndex >= blast.hits.size() || toIndex > blast.hits.size()
				|| fromIndex < 0 || toIndex < 0)
			throw new RuntimeException("Invalid page arguments!");
		if (dataRecordCollection == null || dataRecordCollection.size() < toIndex) {
			GenericDataRecordCollection<IndexedDataRecord> drc = newDataRecordCollection();
			IndexedDataRecord record;
			for (int i=fromIndex; i<toIndex; i++) {
				BlastHit hit = blast.hits.get(i);
				String dataset = hit.DATASET.equals("PDB") ? "PDBSEQ" : hit.DATASET;
				DatasetId datasetId = new DatasetId(dataset, hit.PRIMARY_ID);
				record = newDataRecord(i, findDatasetRecord(datasetId));
				record.getField(RecordFieldType.SCORE).setValue(hit.SCORE);
				record.getField(RecordFieldType.E_VALUE).setValue(hit.E_VALUE);
				record.getField(RecordFieldType.DATASET).setValue(datasetId.dataset);
				record.getField(RecordFieldType.QUERY).setValue(blast.QUERY);
				drc.add(record);
			}
			return drc;
		}
		return dataRecordCollection.slice(fromIndex, toIndex);
	}
}
