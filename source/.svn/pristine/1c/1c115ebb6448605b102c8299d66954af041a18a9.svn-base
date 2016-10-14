package org.ngbw.examples;


import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.api.core.SourceDocumentTransformer;
import org.ngbw.sdk.common.util.FileUtils;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.SourceDocument;




public class BlastTransformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			org.ngbw.sdk.Workbench workbench = Workbench.getInstance();
			DataFormat dataFormat = DataFormat.BLAST_TEXT;
			DataType dataType = DataType.BLAST_OUTPUT;
			EntityType entityType = EntityType.NUCLEIC_ACID;
			byte[] data = FileUtils.readFile(args[0]);

			RecordType targetRecordType = RecordType.NUCLEIC_ACID_SIMILARITY_SEARCH_HIT;
			SourceDocument sourceDocument = new SourceDocumentBean(entityType, dataType, dataFormat, data);

			boolean canRead = workbench.canRead(sourceDocument.getType());

			if (canRead == false) {
				System.err.println("Can't read SourceDocument " + sourceDocument.getType());
				System.exit(1);
			}
			GenericDataRecordCollection<IndexedDataRecord> drc = workbench.read(sourceDocument);
			System.out.println("Read " + drc.size() + " DataRecords from SourceDocument of type: " + sourceDocument.getType());
			for (IndexedDataRecord idr : drc) {
				System.out.println(idr);
				SourceDocument singleDoc = workbench.extractSubSourceDocument(sourceDocument, idr.getIndex());
				SourceDocumentTransformer transformer = workbench.getTransformer(singleDoc, targetRecordType);
				GenericDataRecordCollection<IndexedDataRecord> records = transformer.getDataRecordCollection(0, 50);
				System.out.println("Transformed " + transformer.getTotalDataRecordCount() + " DataRecords from SourceDocument of type: " + singleDoc.getType());
				for (IndexedDataRecord record : records) {
					System.out.println(record);
					System.out.println(
							record.getField(RecordFieldType.DATASET) + ":\t"
							+ "index: " + record.getIndex() + ":\t"
							+ record.getField(RecordFieldType.PRIMARY_ID) + ":\t"
							+ record.getField(RecordFieldType.NAME) + ":\t"
							+ record.getField(RecordFieldType.SCORE) + ":\t"
							+ record.getField(RecordFieldType.E_VALUE));
					SourceDocument tsd = transformer.getTransformedSourceDocument(record);
					System.out.println(new String(tsd.getData()));
				}
			}
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}

}
