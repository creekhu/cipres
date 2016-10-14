/*
 * LuceneDatasetIndexer.java 
 */
package org.ngbw.sdk.dataresources.lucene;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.conversion.ConversionService;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.core.shared.DatasetRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class LuceneDatasetIndexer {

	private static Log log = LogFactory.getLog(LuceneDatasetIndexer.class);
	private IndexWriter writer;
	private IndexWriter sourceWriter;
	private SourceDocumentType sourceDocumentType;
	private SourceDocumentReader sourceReader;
	private Map<String, IndexFieldType> fieldIndexTypeLookup;
	private RecordFilter filter;
	private RecordSourceFilter sourceFilter;

	private LuceneDataResourceConfig config;
	private Workbench workbench;

	public LuceneDatasetIndexer(Workbench workbench) {
		if (workbench == null)
			throw new NullPointerException("Workbench is null!");
		this.workbench = workbench;
	}

	public void indexDataset(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		initializeFields(dataset);
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");
		RecordFieldType recordKeyField = getRecordKeyField();
		if (recordKeyField == null)
			throw new NullPointerException("recordKeyField is null!");
		Resource sourceDirectory = config.getSourceLocation(dataset);
		String[] sourceFileNames = config.getSourceFileNames(dataset);
		int total = 0;
		initializeWriters(dataset);
		for (String name : sourceFileNames) {
			//don't validate the extended resource
			try {
				total += processResource(sourceDirectory.extend(name, false),
						recordKeyField, dataset);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException(e.toString(), e);
			}
			if (log.isInfoEnabled())
				log.info("Current total " + total);
		}
		closeWriters();
		if (log.isInfoEnabled())
			log.info(total + " DatasetRecords indexed total for Dataset: "
					+ dataset);
	}

	private void initializeFields(Dataset dataset) {
		// Data Module
		DatasetService ds = workbench.getServiceFactory().getDatasetService();
		SourceDocumentType sourceDocumentType = ds
				.getSourceDocumentType(dataset);
		LuceneDataResource dataResource = (LuceneDataResource) ds
				.getDatasetRegistry().getDataResource(dataset);
		this.config = dataResource.getConfig();

		// Core Module
		CoreRegistry coreRegistry = workbench.getServiceFactory()
				.getCoreRegistry();
		RecordType recordType = coreRegistry.getRecordType(sourceDocumentType
				.getEntityType(), sourceDocumentType.getDataType());

		// Conversion Module
		ConversionService cs = workbench.getServiceFactory()
				.getConversionService();
		DataFormat dataFormat = sourceDocumentType.getDataFormat();
		SourceDocumentReader sourceReader = cs.getConversionRegistry()
				.getReader(sourceDocumentType);
		this.sourceDocumentType = sourceDocumentType;
		this.sourceReader = sourceReader;
		this.fieldIndexTypeLookup = config.getFieldMappings(recordType);
		this.filter = cs.getRecordFilter(dataFormat);
		this.sourceFilter = config.getRecordSourceFilter(dataFormat);
	}

	private void initializeWriters(Dataset dataset) {
		writer = LuceneUtils.getIndexWriter(config.getIndexDirectory(dataset));
		sourceWriter = LuceneUtils.getSourceIndexWriter(config
				.getSourceIndexDirectory(dataset));
	}

	private void closeWriters() {
		try {
			if (log.isInfoEnabled())
				log.info("Optimizing the FIELD_INDEX");
			writer.optimize();
		} catch (Exception e) {
			log.error("Error encountered on optimizing FIELD_INDEX!", e);
		} finally {
			try {
				writer.close();
				if (log.isInfoEnabled())
					log.info("Closed IndexWriter for " + "FIELD_INDEX");
			} catch (Exception e) {
				/* we tried */
				log.error(e);
			}
		}
		try {
			if (log.isInfoEnabled())
				log.info("Optimizing the SOURCE_INDEX");
			sourceWriter.optimize();
		} catch (Exception e) {
			log.error("Error encountered on optimizing SOURCE_INDEX!", e);
		} finally {
			try {
				sourceWriter.close();
				if (log.isInfoEnabled())
					log.info("Closed IndexWriter for " + "SOURCE_INDEX");
			} catch (Exception e) {
				/* we tried */
				log.error(e);
			}
		}
	}

	private int processResource(Resource resource, RecordFieldType recordKeyField,
			Dataset dataset) {
		if (log.isInfoEnabled())
			log.info("Indexing Dataset " + dataset + " from "
					+ resource.getName());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				resource.getInputStream()));
		filter.setInput(reader);
		int count = 0;
		while (filter.hasNext()) {
			count++;
			// the source record in plain text
			String sourceData = filter.next();
			// the filtered source - everything that should be indexed for
			// simple search
			String filteredSourceData = sourceFilter.filter(sourceData);
			// conversion service only deals with SourceDocuments
			SourceDocument srcDocument = new SourceDocumentBean(
					sourceDocumentType, sourceData.getBytes());
			try {
				// index the values (RecordFields) for advanced search
				DatasetRecord datasetRecord = new DatasetRecord(sourceReader.readSingle(srcDocument), dataset);
				// determine the value of the primary key/unique id field
				String recordKey = datasetRecord.getField(recordKeyField).getValueAsString();
				// Convert DataRecord + Filtered SourceData into Lucene Document and
				// Index it
				Document document = LuceneUtils.buildDocument(datasetRecord,
						filteredSourceData, fieldIndexTypeLookup);
				LuceneUtils.writeToIndex(writer, document);

				// We save the the source to a separate index to increase
				// performance
				LuceneUtils.writeToSourceIndex(sourceWriter, sourceData, recordKey);
			} catch (Exception e) {
				log.error("invalid source record " + resource.getName() + " [" + count + "]");
				continue;
			}
			// log progress all 25K records
			if (count % 25000 == 0 && log.isInfoEnabled())
				log.info(count + " DatasetRecords processed.");
		}
		filter.close();
		if (log.isInfoEnabled())
			log.info(count + " DatasetRecords indexed - closing RecordFilter");
		return count;
	}

	// determine the recordKey for this record FIXME
	private RecordFieldType getRecordKeyField() {
		for (String field : fieldIndexTypeLookup.keySet()) {
			IndexFieldType type = fieldIndexTypeLookup.get(field);
			if (IndexFieldType.ID.equals(type)) {
				return RecordFieldType.valueOf(field);
			}
		}
		throw new NullPointerException("No record key field found!");
	}

	public static void main(String[] args) {
		Dataset dataset = Dataset.valueOf(args[0]);
		Workbench wb = Workbench.getInstance();
		LuceneDatasetIndexer indexer = new LuceneDatasetIndexer(wb);
		indexer.indexDataset(dataset);
	}
}
