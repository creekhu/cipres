/*
 * DefaultDatasetService.java
 */
package org.ngbw.sdk.data;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.data.AdvancedSearchMetaQuery;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DatasetConfig;
import org.ngbw.sdk.api.data.DatasetRegistry;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.api.data.SimpleSearchMetaQuery;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultDatasetService implements DatasetService {
	
	private DatasetRegistry datasetRegistry;
	
	DefaultDatasetService(DatasetRegistry datasetRegistry) {
		this.datasetRegistry = datasetRegistry;
	}

	public DatasetRegistry getDatasetRegistry() {
		return datasetRegistry;
	}

	public String getDatasetName(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		DatasetConfig dsi = datasetRegistry.getDatasetConfig(dataset);
		return dsi.getName();
	}

	public Set<Dataset> getDatasets() {
		return datasetRegistry.getDatasets();
	}

	public Set<Dataset> getDatasets(EntityType entityType) {
		if (entityType == null) 
			throw new NullPointerException("EntityType is null!");
		return datasetRegistry.getDatasets(entityType);
	}

	public Set<Dataset> getDatasets(DataType dataType) {
		if (dataType == null) 
			throw new NullPointerException("DataType is null!");
		return datasetRegistry.getDatasets(dataType);
	}

	public Set<Dataset> getDatasets(EntityType entityType, DataType dataType) {
		if (entityType == null) 
			throw new NullPointerException("EntityType is null!");
		if (dataType == null) 
			throw new NullPointerException("DataType is null!");
		return datasetRegistry.getDatasets(entityType, dataType);
	}

	public DataResource getDataResource(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		return datasetRegistry.getDataResource(dataset);
	}

	public SourceDocumentType getSourceDocumentType(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		return datasetRegistry.getSourceDocumentType(dataset);
	}

	public DataFormat getDataFormat(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		return datasetRegistry.getSourceDocumentType(dataset)
		.getDataFormat();
	}

	public DataType getDataType(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		return datasetRegistry.getDataType(dataset);
	}

	public RecordType getRecordType(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		return datasetRegistry.getRecordType(dataset);
	}

	public Set<RecordFieldType> getFieldTypes(RecordType recordType) {
		if (recordType == null) 
			throw new NullPointerException("RecordType is null!");
		return datasetRegistry.getCoreRegistry().getRecordFields(recordType);
	}

	public Set<RecordFieldType> getRecordFields(RecordType recordType) {
		return datasetRegistry.getCoreRegistry().getRecordFields(recordType);
	}

	public String getFieldName(Dataset dataset, RecordFieldType recordField) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		if (recordField == null) 
			throw new NullPointerException("RecordField is null!");
		DataResource dataResource = getDataResource(dataset);
		return dataResource.getFieldName(dataset, recordField);
	}

	public EntityType getEntityType(Dataset dataset) {
		return datasetRegistry.getEntityType(dataset);
	}


	public Map<String, FieldDataType> getFields(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		CoreRegistry coreRegistry = datasetRegistry.getCoreRegistry();
		RecordType recordType = datasetRegistry.getRecordType(dataset);
		Set<RecordFieldType> recFields = coreRegistry.getRecordFields(recordType);
		Map<String, FieldDataType> fields = new HashMap<String, FieldDataType>();
		DataResource dataResource = getDataResource(dataset);
		for(RecordFieldType recordField : recFields) {
			String fieldName = dataResource.getFieldName(dataset, recordField);
			fields.put(fieldName, coreRegistry.getFieldType(recordField));
		}
		return fields;
	}
	
	public RecordFieldType getRecordField(Dataset dataset, String field) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		if (field == null) 
			throw new NullPointerException("field is null!");
		DataResource dataResource = getDataResource(dataset);
		return dataResource.getRecordField(dataset, field);
	}

	public AdvancedSearchMetaQuery getMetaQuery(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		RecordType recordType = datasetRegistry.getRecordType(dataset);
		if (recordType == null)
			throw new NullPointerException("No RecordType registered for " + dataset);
		AdvancedSearchMetaQuery query = new AdvancedSearchMetaQueryImpl(this, recordType, dataset);
		return query;
	}

	public AdvancedSearchMetaQuery getMetaQuery(Set<Dataset> datasets) {
		if (datasets == null || datasets.isEmpty()) 
			throw new NullPointerException("Datasets is null or empty!");
		RecordType recordType = datasetRegistry.getRecordType(datasets);
		if (recordType == null)
			throw new NullPointerException("No RecordType common registered for submitted  datasets");
		AdvancedSearchMetaQuery query = new AdvancedSearchMetaQueryImpl(this, recordType, datasets);
		return query;
	}

	public SimpleSearchMetaQuery getSimpleSearchQuery(Dataset dataset) {
		if (dataset == null) 
			throw new NullPointerException("Dataset is null!");
		RecordType recordType = datasetRegistry.getRecordType(dataset);
		if (recordType == null)
			throw new NullPointerException("No RecordType registered for " + dataset);
		SimpleSearchMetaQuery query = new SimpleSearchMetaQueryImpl(this, recordType, dataset);
		return query;
	}

	public SimpleSearchMetaQuery getSimpleSearchQuery(Set<Dataset> datasets) {
		if (datasets == null || datasets.isEmpty()) 
			throw new NullPointerException("Datasets is null or empty!");
		RecordType recordType = datasetRegistry.getRecordType(datasets);
		if (recordType == null)
			throw new NullPointerException("No RecordType common registered for submitted  datasets");
		SimpleSearchMetaQuery query = new SimpleSearchMetaQueryImpl(this, recordType, datasets);
		return query;
	}

	public Set<DataType> getSearchableDataTypes() {
		return datasetRegistry.getSearchableDataTypes();
	}

	public Set<EntityType> getSearchableEntityTypes() {
		return datasetRegistry.getSearchableEntityTypes();
	}

	public Set<RecordType> getSearchableRecordTypes() {
		return datasetRegistry.getSearchableRecordTypes();
	}
}
