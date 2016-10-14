/*
 * DefaultDatasetRegistry.java
 */
package org.ngbw.sdk.data;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DatasetConfig;
import org.ngbw.sdk.api.data.DatasetRegistry;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author R. Hannes Niedner
 *
 */
public class DefaultDatasetRegistry implements DatasetRegistry {
	
	private final CoreRegistry coreRegistry;
	private Map<Dataset, DatasetConfig> datasetConfigMap = new HashMap<Dataset, DatasetConfig>();
	private HashMap<String, DataResource> dataResourceMap =  new HashMap<String, DataResource>(10);
	private HashMap<Dataset, RecordType> datasetRecordTypesMap = new HashMap<Dataset, RecordType>();
	private Map<RecordType, Set<Dataset>> recordTypeDatasetLookup = new HashMap<RecordType, Set<Dataset>>();
	
	DefaultDatasetRegistry(CoreRegistry coreRegistry) {
		this.coreRegistry = coreRegistry;
	}
	
	public CoreRegistry getCoreRegistry() {
		return coreRegistry;
	}

	public DatasetService getDatasetService() {
		return new DefaultDatasetService(this);
	}

	public DataResource getDataResource(Dataset dataset) {
		return dataResourceMap.get(getDatasetConfig(dataset).getDataResourceId());
	}

	public DataResource getDataResource(String dataResourceId) {
		return dataResourceMap.get(dataResourceId);
	}

	public DatasetConfig getDatasetConfig(Dataset dataset) {
		if (datasetConfigMap.containsKey(dataset))
			return datasetConfigMap.get(dataset);
		return new DatasetConfigBean(dataset);
	}

	public void registerDataset(DatasetConfig datasetConfig) {
		if(datasetConfig == null)
			throw new NullPointerException("DatasetConfig cannot be null!");
		String dataResourceId = datasetConfig.getDataResourceId();
		if(dataResourceId == null)
			throw new NullPointerException("DataResourceId in the DatasetConfig cannot be null!");
		if(dataResourceMap.containsKey(dataResourceId) == false)
			throw new NullPointerException("You must register a DataResource with the Id " + dataResourceId + " first!");
		datasetConfigMap.put(datasetConfig.getDataset(), datasetConfig);
		SourceDocumentType sourceDocumentType = datasetConfig.getSourceDocumentType();
		if(sourceDocumentType == null)
			throw new NullPointerException("DatasetConfig is missing SourceDocumentType!");
		RecordType recordType = coreRegistry.getRecordType(sourceDocumentType.getEntityType(), sourceDocumentType.getDataType());
		Dataset dataset = datasetConfig.getDataset();
		if(recordType == null)
			throw new NullPointerException("No recordType for SourceDocumentType: " + sourceDocumentType);
		datasetRecordTypesMap.put(dataset, recordType);
		if (recordTypeDatasetLookup.containsKey(recordType) == false)
			recordTypeDatasetLookup.put(recordType, new HashSet<Dataset>());
		recordTypeDatasetLookup.get(recordType).add(dataset);
	}

	public void registerDataResource(String uniqueId,
			Class<DataResource> className, String configFile) {
		DataResource dataResource;
		try {
			dataResource = className.getConstructor(DatasetRegistry.class).newInstance(this);
		} catch (InstantiationException e) {
			throw new RuntimeException("Can't instantiate class " + className, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Illegal access trying to instantiate class " + className, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Argument is not a DatasetRegistry for class " + className, e);
		} catch (SecurityException e) {
			throw new RuntimeException("Insufficient visibility of constructor taking a constructor taking a DatasetRegistry argument in class argument in class " + className, e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot invoke constructor taking a DatasetRegistry argument in class " + className, e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No constructor taking a DatasetRegistry argument in class " + className, e);
		}
		if (dataResource.isConfigured() == false) {
			if (configFile != null) {
				try {
					dataResource.configure(Resource.getResource(configFile));
				} catch (ResourceNotFoundException e) {
					throw new RuntimeException(e.toString(), e);
				}
			} else {
				throw new RuntimeException("Missing DataResource configuration for DataResource: " + className);
			}
			if (dataResource.isConfigured() == false)
				throw new RuntimeException("DataResource configuration: " + className + " failed!");
		}
		dataResourceMap.put(uniqueId, dataResource);
	}

	public Set<Dataset> getDatasets() {
		return datasetConfigMap.keySet();
	}

	public RecordType getRecordType(Dataset dataset) {
		return datasetRecordTypesMap.get(dataset);
	}

	public SourceDocumentType getSourceDocumentType(Dataset dataset) {
		return datasetConfigMap.get(dataset).getSourceDocumentType();
	}

	public Set<Dataset> getDatasets(EntityType entityType) {
		Set<Dataset> datasets = new HashSet<Dataset>();
		Set<RecordType> recordTypes = coreRegistry.getRecordTypes(entityType);
		if (recordTypes == null)
			return datasets;
		for(Dataset dataset :datasetRecordTypesMap.keySet()) {
			RecordType recordType = datasetRecordTypesMap.get(dataset);
			if (recordTypes.contains(recordType))
				datasets.add(dataset);
		}
		return datasets;
	}

	public Set<Dataset> getDatasets(DataType dataType) {
		Set<Dataset> datasets = new HashSet<Dataset>();
		Set<RecordType> recordTypes = coreRegistry.getRecordTypes(dataType);
		if (datasetRecordTypesMap == null || recordTypes == null)
			return datasets;
		for(Dataset dataset :datasetRecordTypesMap.keySet()) {
			RecordType recordType = datasetRecordTypesMap.get(dataset);
			if (recordTypes.contains(recordType))
				datasets.add(dataset);
		}
		return datasets;
	}

	public Set<Dataset> getDatasets(EntityType entityType, DataType dataType) {
		Set<Dataset> datasets = new HashSet<Dataset>();
		RecordType recordType = coreRegistry.getRecordType(entityType, dataType);
		if (recordType == null) return datasets;
		for(Dataset dataset :datasetRecordTypesMap.keySet())
			if (recordType.equals(datasetRecordTypesMap.get(dataset)))
				datasets.add(dataset);
		return datasets;
	}
	
	public EntityType getEntityType(Dataset dataset) {
		RecordType recordType = datasetRecordTypesMap.get(dataset);
		return coreRegistry.getEntityType(recordType);
	}
	
	public DataType getDataType(Dataset dataset) {
		RecordType recordType = datasetRecordTypesMap.get(dataset);
		return coreRegistry.getDataType(recordType);
	}

	public RecordType getRecordType(Set<Dataset> datasets) {
		if (datasets == null || datasets.isEmpty())
			throw new NullPointerException("Datasets is null or empty!");
		for (RecordType recordType : recordTypeDatasetLookup.keySet()) {
			Set<Dataset> typedds = recordTypeDatasetLookup.get(recordType);
			if (typedds != null && typedds.containsAll(datasets))
				return recordType;
		}
		return null;
	}
	
	public Set<RecordType> getSearchableRecordTypes() {
		return recordTypeDatasetLookup.keySet();
	}

	public Set<DataType> getSearchableDataTypes() {
		Set<DataType> dataTypes = new HashSet<DataType>();
		Set<RecordType> recordTypes = getSearchableRecordTypes();
		for (RecordType recordType : recordTypes)
			dataTypes.add(coreRegistry.getDataType(recordType));
		return dataTypes;
	}

	public Set<EntityType> getSearchableEntityTypes() {
		Set<EntityType> entityTypes = new HashSet<EntityType>();
		Set<RecordType> recordTypes = getSearchableRecordTypes();
		for (RecordType recordType : recordTypes)
			entityTypes.add(coreRegistry.getEntityType(recordType));
		return entityTypes;
	}
}
