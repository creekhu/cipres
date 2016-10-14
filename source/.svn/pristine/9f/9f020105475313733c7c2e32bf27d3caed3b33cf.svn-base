/*
 * DatasetService.java
 */
package org.ngbw.sdk.api.data;


import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * The DatasetService catalogues Datasets and DataResources. Implementations
 * of this interface translate physical DataResource access into logical
 * access via the Dataset interface. It also provides methods to access
 * critical metadata (logical sdk concepts) to alleviate the need to directly
 * interact with the central CoreRegistry - thus it serves as a facade that 
 * bridges communication between data access and sdk.
 * <br />
 * 	Dataset - maintained collection of DataRecords such as Genbank, Swissprot, PDB (all record have the same RecordType).
 * 			  In difference to a DataRecordCollection, which is the working- or shuttle- utility class for multiple
 * 			  DataRecords, a Dataset serves as an unique term to identify a persisted collection.
 * 			  Even though the physical access to Datasets is brokered in the DatasetService, Datasets are part of the 
 *	 		  core logic as they can be characterized by all core concepts.
 *
 * @author Roland H. Niedner
 *
 */
public interface DatasetService {
	
	
	/**
	 * Method returns the DatasetRegistry used by the service.
	 * 
	 * @return datasetRegistry
	 */
	public DatasetRegistry getDatasetRegistry();

	/**
	 * Method returns all Datasets that are plugged into the sdk. 
	 * 
	 * @return Set of Datasets
	 */
	public Set<Dataset> getDatasets();

	/**
	 * Method returns all Datasets with records of the submitted EntityType.
	 * 
	 * @param entityType
	 * @return Set of Datasets
	 */
	public Set<Dataset> getDatasets(EntityType entityType);

	/**
	 * Method returns all Datasets with records of the submitted DataType.
	 * 
	 * @param dataType
	 * @return Set of Datasets
	 */
	public Set<Dataset> getDatasets(DataType dataType);

	/**
	 * Method returns all Datasets with records of the submitted EntityType and DataType.
	 * 
	 * @param entityType
	 * @param dataType
	 * @return Set of Datasets
	 */
	public Set<Dataset> getDatasets(EntityType entityType, DataType dataType);
	
	/**
	 * Method returns the SourceDocumentType of the submitted Dataset.
	 * 
	 * @param dataset
	 * @return sourceDocumentType
	 */
	public SourceDocumentType getSourceDocumentType(Dataset dataset);
	
	/**
	 * Method returns the RecordType of the submitted Dataset.
	 * 
	 * @param dataset
	 * @return recordType
	 */
	public RecordType getRecordType(Dataset dataset);

	/**
	 * Method returns the EntityType for the submitted Dataset.
	 * 
	 * @param dataset
	 * @return entityType
	 */
	public EntityType getEntityType(Dataset dataset);

	/**
	 * Method returns the DataType for the submitted Dataset.
	 * 
	 * @param dataset
	 * @return dataType
	 */
	public DataType getDataType(Dataset dataset);

	/**
	 * At the time of specifying this interface this method serves merely
	 * as a proxy to the methods located in the central registry. But it
	 * is conceivable that this mapping is actually located in the DatasetService.
	 * 
	 * @param dataset
	 * @return DataFormat
	 */
	public DataFormat getDataFormat(Dataset dataset);

	/**
	 * Method returns the descriptive name for
	 * the submitted Dataset.
	 * 
	 * @param dataset
	 * @return name
	 */
	public String getDatasetName(Dataset dataset);
	
	/**
	 * Method return a the implementation that provides the 
	 * physical access to the submitted dataset, via Queries for Example.
	 * 
	 * @param dataset
	 * @return dataResource
	 */
	public DataResource getDataResource(Dataset dataset);
	
	/**
	 * Method returns a MetaQuery, which provides access to
 	 * the results from a DataResource Query (in this signature,
 	 * its simply a wrapper for a single Query.
 	 * In addition to the SimpleSearchMetaQuery this MetaQuery
 	 * allows you to programatically build a complex query using
 	 * search criteria.
	 * 
	 * @param dataset
	 * @return metaQuery
	 */
	public AdvancedSearchMetaQuery getMetaQuery(Dataset dataset);
	
	/**
	 * Method returns a MetaQuery, which federates the results from
	 * all the different Datasets, which are generated from a set of
	 * DataResource spawned Queries.
 	 * In addition to the SimpleSearchMetaQuery this MetaQuery
 	 * allows you to programatically build a complex query using
 	 * search criteria.
	 * 
	 * @param datasets - Set of Dataset instances
	 * @return metaQuery
	 */
	public AdvancedSearchMetaQuery getMetaQuery(Set<Dataset> datasets);

	
	/**
	 * Method returns a SimpleSearchMetaQuery, which provides access to
 	 * the results from a DataResource Query (in this signature,
 	 * its simply a wrapper for a single Query.
	 * 
	 * @param dataset
	 * @return SimpleSearchMetaQuery
	 */
	public SimpleSearchMetaQuery getSimpleSearchQuery(Dataset dataset);

	
	/**
	 * Method returns a SimpleSearchMetaQuery, which federates the results from
	 * all the different Datasets, which are generated from a set of
	 * DataResource spawned Queries.
	 * 
	 * @param datasets - Set of Dataset instances
	 * @return SimpleSearchMetaQuery
	 */
	public SimpleSearchMetaQuery getSimpleSearchQuery(Set<Dataset> datasets);

	/**
	 * At the time of specifying this interface this method serves merely
	 * as a proxy to the methods located in the central registry. But it
	 * is conceivable that this mapping is actually located in the DatasetService.
	 * 
	 * @param recordType
	 * @return Set of RecordFields
	 */
	public Set<RecordFieldType> getRecordFields(RecordType recordType);
	
	/**
	 * Translate RecordField into the field name for the submitted Dataset.
	 * 
	 * @param dataset
	 * @param recordField
	 * @return field name as used in the DataResource hosting this dataset
	 */
	public String getFieldName(Dataset dataset, RecordFieldType recordField);
	
	/**
	 * Translate field name into the RecordField for the submitted Dataset.
	 * 
	 * @param dataset
	 * @param field
	 * @return centrally registered RecordField concept name for the field of this dataset.
	 */
	public RecordFieldType getRecordField(Dataset dataset, String field);

	
	/**
	 * At the time of specifying this interface this method serves merely
	 * as a proxy to the methods located in the central registry. But it
	 * is conceivable that this mapping is actually located in the DatasetService.
	 * 
	 * @param recordType
	 * @return fieldTypes
	 */
	public Set<RecordFieldType> getFieldTypes(RecordType recordType);
	
	/**
	 * Method looks up the RecordType associated with the submitted
	 * Dataset in the central registry (or perhaps right here), and then retrieves
	 * the RecordFields and the FieldTypes for the RecordType. The RecordFields
	 * finally will be translated into the registered String designation for
	 * the submitted Dataset in its DataResource.
	 * 
	 * @param dataset
	 * @return Map of field names and their respective FieldTypes
	 */
	public Map<String, FieldDataType> getFields(Dataset dataset);

	/**
	 * Method returns all RecordTypes that have at least one Dataset
	 * registered.
	 * 
	 * @return recordTypes
	 */
	public Set<RecordType> getSearchableRecordTypes();

	/**
	 * Method returns all EntityTypes that have at least one Dataset
	 * registered.
	 * 
	 * @return entityTypes
	 */
	public Set<EntityType> getSearchableEntityTypes();

	
	/**
	 * Method returns all DataTypes that have at least one Dataset
	 * registered.
	 * 
	 * @return dataTypes
	 */
	public Set<DataType> getSearchableDataTypes();
}
