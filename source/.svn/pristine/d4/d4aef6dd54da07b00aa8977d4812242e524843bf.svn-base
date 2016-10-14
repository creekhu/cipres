/*
 * LuceneDataResource.java
 */
package org.ngbw.sdk.dataresources.lucene;


import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.ngbw.sdk.api.data.AdvancedQuery;
import org.ngbw.sdk.api.data.Criterion;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DataResourceType;
import org.ngbw.sdk.api.data.DatasetRegistry;
import org.ngbw.sdk.api.data.ResultTuple;
import org.ngbw.sdk.api.data.SimpleQuery;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.core.query.LogicalQueryOperator;
import org.ngbw.sdk.core.query.MultiStringValueQueryOperator;
import org.ngbw.sdk.core.query.MultiValueQueryOperator;
import org.ngbw.sdk.core.query.NullValueQueryOperator;
import org.ngbw.sdk.core.query.SingleStringValueQueryOperator;
import org.ngbw.sdk.core.query.SingleValueQueryOperator;
import org.ngbw.sdk.core.query.TwoValueQueryOperator;
import org.ngbw.sdk.core.shared.DatasetRecord;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class LuceneDataResource implements DataResource {

	private static Log log = LogFactory.getLog(LuceneDataResource.class);
	private DatasetRegistry datasetRegistry;
	private LuceneDataResourceConfig config;

	public LuceneDataResource(DatasetRegistry datasetRegistry) {
		super();
		if (datasetRegistry == null)
			throw new NullPointerException("DatasetRegistry is null!");
		this.datasetRegistry = datasetRegistry;
	}
	
	public LuceneDataResource(DatasetRegistry datasetRegistry, LuceneDataResourceConfig config) {
		this(datasetRegistry);
		if (config == null)
			throw new NullPointerException("LuceneDataResourceConfig is null!");
		this.config = config;
	}

	public DatasetRegistry getDatasetRegistry() {
		return datasetRegistry;
	}

	public boolean configure(Resource configFile) {
		LuceneDataResourceConfig config =  new LuceneDataResourceConfig();
		config.readConfigFile(configFile);
		this.config = config;
		return isConfigured();
	}

	public boolean isConfigured() {
		return (config != null);
	}

	public Set<Dataset> getDatasets() {
		return config.getDatasets();
	}

	public String getFieldName(Dataset dataset, RecordFieldType recordField) {
		// we can ignore the dataset argument since the
		// filename equals the RecordField name 
		return recordField.toString();
	}

	public RecordFieldType getRecordField(Dataset dataset, String field) {
		// we can ignore the dataset argument since the
		// filename equals the RecordField name 
		return RecordFieldType.valueOf(field);
	}
	
	/*
	public DatasetRecord getDatasetRecordById(Dataset dataset, Object recordKey) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (recordKey == null)
			throw new NullPointerException("recordKey is null!");
		Directory directory = getFieldIndexDirectory(dataset);
		Searcher searcher = getIndexSearcher(directory);
		RecordType recordType = this.datasetRegistry.getRecordType(dataset);
		if (recordType == null)
			throw new NullPointerException("No RecordType is registered for Dataset." + dataset);
		Map<String, IndexFieldType> fieldMap = config.getFieldMappings(recordType );
		ResultTuple tuple = LuceneUtils.getResultTupleById(searcher, recordKey.toString(), fieldMap);
		List<RecordField> values = new ArrayList<RecordField>();
		Set<RecordFieldType> types = datasetRegistry.getCoreRegistry().getRecordFields(recordType);
		for(RecordFieldType recordField : types) {
			values.add(new RecordField(recordField, tuple.getValue(recordField.toString())));
		}
		DatasetRecord datasetRecord = new DatasetRecord(recordType, values, dataset);
		try {
			searcher.close();
		} catch (IOException e) {
			log.warn("Could not close searcher");
		}
		try {
			directory.close();
		} catch (IOException e) {
			log.warn("Could not close directory");
		}
		return datasetRecord;
	}
	*/
	
	public DatasetRecord getDatasetRecordById(Dataset dataset, Object recordKey) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (recordKey == null)
			throw new NullPointerException("recordKey is null!");
		Directory directory = getFieldIndexDirectory(dataset);
		Searcher searcher = getIndexSearcher(directory);
		RecordType recordType = this.datasetRegistry.getRecordType(dataset);
		if (recordType == null)
			throw new NullPointerException("No RecordType is registered for Dataset." + dataset);
		Map<String, IndexFieldType> fieldMap = config.getFieldMappings(recordType );
		ResultTuple tuple = LuceneUtils.getResultTupleById(searcher, recordKey.toString(), fieldMap);
		Set<RecordFieldType> types = datasetRegistry.getCoreRegistry().getRecordFields(recordType);
		DatasetRecord datasetRecord = new DatasetRecord(recordType, types, dataset);

		try {
			for(RecordFieldType recordField : types)
				datasetRecord.getField(recordField).setValue(tuple.getValue(recordField.toString()));
		}
		catch (ParseException parseErr) {
			throw new RuntimeException(parseErr);
		}

		try {
			searcher.close();
		} catch (IOException e) {
			log.warn("Could not close searcher");
		}
		try {
			directory.close();
		} catch (IOException e) {
			log.warn("Could not close directory");
		}
		return datasetRecord;
	}

	public byte[] getRecordSource(Dataset dataset, Object recordKey) {
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (recordKey == null)
			throw new NullPointerException("recordKey is null!");
		Directory directory = getSourceIndexDirectory(dataset);
		Searcher searcher = getIndexSearcher(directory);
		String sourceString = LuceneUtils.getRecordSource(searcher, recordKey.toString());
		if (sourceString == null)
			throw new NullPointerException("no SourceEntry was returned from the source Index for: "
					+ dataset + " : " + recordKey);
		byte[] source = sourceString.getBytes();
		try {
			searcher.close();
		} catch (IOException e) {
			log.warn("Could not close searcher");
		}
		try {
			directory.close();
		} catch (IOException e) {
			log.warn("Could not close directory");
		}
		return source;
	}

	public DataResourceType getType() {
		return DataResourceType.LUCENE_INDEX;
	}

	public SimpleQuery createSimpleQuery(Dataset dataset,
			Map<String, FieldDataType> fields, String searchPhrase) {
		RecordType recordType = datasetRegistry.getRecordType(dataset);
		Map<String, IndexFieldType> fieldIndexTypes = config.getFieldMappings(recordType);
		Directory directory = getFieldIndexDirectory(dataset);
		return new LuceneSimpleQuery(dataset, directory, fieldIndexTypes, searchPhrase);
	}

	public SimpleQuery createSimpleQuery(Set<Dataset> datasets,
			Map<String, FieldDataType> fields, String searchPhrase) {
		RecordType recordType = datasetRegistry.getRecordType(datasets);
		if (recordType == null)
			throw new RuntimeException("The submitted datasets don't share the same RecordType!");
		Map<String, IndexFieldType> fieldIndexTypes = config.getFieldMappings(recordType);
		Set<Directory> directories = getIndexDirectories(datasets);
		return new LuceneSimpleQuery(directories, fieldIndexTypes, searchPhrase);
	}

	public AdvancedQuery createQuery(Dataset dataset,
			Map<String, FieldDataType> fields) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public AdvancedQuery createQuery(Set<Dataset> datasets,
			Map<String, FieldDataType> fields) {
		// TODO Auto-generated method stub
		return null;
	}

	public Criterion getCriterion(NullValueQueryOperator op, String queryField) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(SingleValueQueryOperator op,
			String queryField, Object value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(SingleStringValueQueryOperator op,
			String queryField, String value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(SingleStringValueQueryOperator op,
			String queryField, String value, boolean caseSensitive) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(TwoValueQueryOperator op, String queryField,
			Object low, Object high) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(MultiValueQueryOperator op,
			String queryField, List<?> values) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(MultiStringValueQueryOperator op,
			String queryField, List<String> values) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}

	public Criterion getCriterion(LogicalQueryOperator op, Criterion lhc,
			Criterion rhc) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Depricated/Not supported yet.");
	}
	
	private Searcher getIndexSearcher(Directory indexDirectory) {
		if (indexDirectory == null)
			throw new NullPointerException("Directory is null!");
		IndexSearcher searcher;
		try {
			searcher = new IndexSearcher(indexDirectory);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Found corrupt index at: "
					+ indexDirectory, e);
		} catch (IOException e) {
			throw new RuntimeException("Encountered IOError accessing index at: "
					+ indexDirectory, e);
		}
		return searcher;
	}
	
	Directory getFieldIndexDirectory(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("dataset is null!");
		return config.getIndexDirectory(dataset);
	}
	
	Directory getSourceIndexDirectory(Dataset dataset) {
		if (dataset == null)
			throw new NullPointerException("dataset is null!");
		return config.getSourceIndexDirectory(dataset);
	}
	
	Set<Directory> getIndexDirectories(Set<Dataset> datasets) {
		if (datasets == null || datasets.isEmpty())
			throw new NullPointerException("datasets are null or empty!");
		Set<Directory> directories = new HashSet<Directory>();
		for (Dataset dataset : datasets) 
			directories.add(getFieldIndexDirectory(dataset));
		return directories;
	}
	
	LuceneDataResourceConfig getConfig() {
		return config;
	}
}
