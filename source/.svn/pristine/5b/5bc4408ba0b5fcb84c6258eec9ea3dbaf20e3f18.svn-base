/*
 * ParseAndSearchTransformer.java
 */
package org.ngbw.sdk.core.transformation;


import java.io.IOException;
import java.sql.SQLException;

import org.ngbw.sdk.api.core.SourceDocumentTransformer;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.core.configuration.ServiceFactory;
import org.ngbw.sdk.core.shared.DatasetId;
import org.ngbw.sdk.core.shared.DatasetRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public abstract class ParseAndSearchTransformer extends BaseTransformer implements SourceDocumentTransformer {
	
	protected ParseAndSearchTransformer(ServiceFactory serviceFactory, SourceDocument srcDocument, RecordType targetType) throws IOException, SQLException {
		super(serviceFactory, srcDocument, targetType);
	}

	@Override
	public SourceDocument getTransformedSourceDocument(DataRecord dataRecord) {
		if (dataRecord == null)
			throw new NullPointerException("DataRecord is null!");
		Object recordId = dataRecord.getField(RecordFieldType.PRIMARY_ID).getValue();
		Dataset dataset = (Dataset) dataRecord.getField(RecordFieldType.DATASET).getValue();
		if (recordId == null)
			throw new NullPointerException("DataRecord has no PRIMARY_ID value!");
		if (dataset == null)
			throw new NullPointerException("DataRecord has no DATASET value!");
		DataResource dr = serviceFactory.getDatasetService().getDataResource(dataset);
		byte[] data = dr.getRecordSource(dataset, recordId);
		if (data == null)
			throw new NullPointerException("No source data for Dataset." + dataset + ":" + recordId);
		SourceDocumentType type = serviceFactory.getDatasetService().getSourceDocumentType(dataset);
		if (type == null)
			throw new NullPointerException("No SourceDocumentType registered for Dataset." + dataset);
		return new SourceDocumentBean(type, data);
	}
	
	protected DatasetRecord findDatasetRecord(DatasetId datsetId) {
		if (datsetId == null)
			throw new NullPointerException("DatasetId is null!");
		DataResource dr = serviceFactory.getDatasetService().getDataResource(datsetId.dataset);
		if (dr == null)
			throw new NullPointerException("No DataResource registered for Dataset." + datsetId.dataset);
		return dr.getDatasetRecordById(datsetId.dataset, datsetId.recordId);
	}
}
