/*
 * DataResource.java
 */
package org.ngbw.sdk.api.data;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.core.Configurable;
import org.ngbw.sdk.api.core.DatasetRegistryAware;
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


/**
 * A DataResource implements the physical access to one or several
 * Datasets. It also provides Criteria to define the restrictions for
 * querying the Datasets it hosts. Per definition all search Criteria
 * are equally applicable for all Datasets hosted by a DataResource.
 * 
 * @author Roland H. Niedner
 *
 */
public interface DataResource extends DatasetRegistryAware, Configurable<Resource> {
	
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
	 * Returns the type of this DataResource.
	 * 
	 * @return type
	 */
	public DataResourceType getType();
	
	/**
	 * Every result row comes with the primary key that
	 * identifies the record in the DataResource. This
	 * primary key is now submitted to return the source
	 * of the DatasetRecord
	 * 
	 * @param dataset
	 * @param recordId
	 * @return source
	 */
	public byte[] getRecordSource(Dataset dataset, Object recordId);
	
	/**
	 * Returns the DatasetRecord of the specified Dataset with the
	 * submitted primary key (RecordField.PRIMARY_ID).
	 * 
	 * @param dataset
	 * @param recordKey
	 * @return datasetRecord
	 */
	public DatasetRecord getDatasetRecordById(Dataset dataset, Object recordKey);

	/**
	 * 
	 * @return all Datasets that this DataResource provides access to.
	 * 
	 */
	public Set<Dataset>getDatasets();
	
	/**
	 * Create a DataResource Query that will search the submitted Dataset.
	 * This DataResourceQuery will not be access by the endconsumer but 
	 * relayed via the Query that translates physical field names and 
	 * access limitation into CoreRegistry concepts and may also federate
	 * multiple DataResourceQueries
	 *  
	 * @param dataset
	 * @param fields - Map of DataTypes keyed to the field names
	 * @return the created DataResource Query
	 */
	public AdvancedQuery createQuery(Dataset dataset, Map<String, FieldDataType> fields);
	
	/**
	 * Create a DataResource Query that will search the submitted Datasets.
	 * This DataResourceQuery will not be access by the endconsumer but 
	 * relayed via the Query that translates physical field names and 
	 * access limitation into CoreRegistry concepts and may also federate
	 * multiple DataResourceQueries
	 *  
	 * @param datasets
	 * @param fields - Map of DataTypes keyed to the field names
	 * @return the created DataResource Query
	 */
	public AdvancedQuery createQuery(Set<Dataset> datasets, Map<String, FieldDataType> fields);
	
	/**
	 * Create a DataResource SimpleQuery that will search the submitted Dataset.
	 * The SimpleQuery is a trimmed down Query that gets initialized with the
	 * search string, presumably using the SimpleSearchCriterion
	 * see: getSimpleSearchCriterion()
	 * This DataResourceQuery will not be access by the endconsumer but 
	 * relayed via the Query that translates physical field names and 
	 * access limitation into CoreRegistry concepts and may also federate
	 * multiple DataResourceQueries
	 *  
	 * @param dataset
	 * @param fields - Map of DataTypes keyd to the field names
	 * @param searchPhrase
	 * @return the created SimpleQuery
	 */
	public SimpleQuery createSimpleQuery(Dataset dataset, Map<String, FieldDataType> fields, String searchPhrase);
	
	/**
	 * Create a DataResource SimpleQuery that will search the submitted Datasets.
	 * The SimpleQuery is a trimmed down Query that gets initialized with the
	 * search string, presumably using the SimpleSearchCriterion
	 * see: getSimpleSearchCriterion()
	 * This DataResourceQuery will not be access by the endconsumer but 
	 * relayed via the Query that translates physical field names and 
	 * access limitation into CoreRegistry concepts and may also federate
	 * multiple DataResourceQueries
	 *  
	 * @param datasets
	 * @param fields - Map of DataTypes keyd to the field names
	 * @param searchPhrase
	 * @return the created SimpleQuery
	 */
	public SimpleQuery createSimpleQuery(Set<Dataset> datasets, Map<String, FieldDataType> fields, String searchPhrase);
	
	/**
	 * Return a NullValueCriterion expressing: "queryField is Null"
	 * 
	 * @param op
	 * @param queryField
	 * @return nullValueCriterion
	 */
	public Criterion getCriterion(NullValueQueryOperator op, String queryField);
	
	/**
	 * Return a SingleValueCriterion expressing (depending on the QueryOperator):
	 * 	
	 * "queryField = value"
	 * "queryField > value"
	 * "queryField < value"
	 * "queryField =< value"
	 * "queryField => value"
	 * 
	 * @param op
	 * @param queryField
	 * @param value
	 * @return singleValueCriterion
	 */
	public Criterion getCriterion(SingleValueQueryOperator op, String queryField, Object value);
	
	/**
	 * Return a SingleValueCriterion expressing (depending on the QueryOperator):
	 * 	
	 * "queryField = 'value'"
	 * "queryField like '%value%'"
	 * "queryField like '%value'"
	 * "queryField like 'value%'"
	 * 
	 * @param op
	 * @param queryField
	 * @param value
	 * @return singleStringValueCriterion
	 */
	public Criterion getCriterion(SingleStringValueQueryOperator op, String queryField, String value);
	
	/**
	 * Return a SingleValueCriterion expressing (depending on the QueryOperator):
	 * 	
	 * "queryField = 'value'"
	 * "queryField like '%value%'"
	 * "queryField like '%value'"
	 * "queryField like 'value%'"
	 * 
	 * @param op
	 * @param queryField
	 * @param value
	 * @param caseSensitive
	 * @return singleStringValueCriterion
	 */
	public Criterion getCriterion(SingleStringValueQueryOperator op, String queryField, String value, boolean caseSensitive);
	
	/**
	 * Return a TwoValueCriterion expressing: "low =< queryField =< hi"
	 * 
	 * @param op
	 * @param queryField
	 * @param low
	 * @param high
	 * @return twoValueCriterion
	 */
	public Criterion getCriterion(TwoValueQueryOperator op, String queryField, Object low, Object high);
	
	/**
	 * Return a MultiValueCriterion expressing: "queryField in values1, value2, ... valueX"
	 * 
	 * @param op
	 * @param queryField
	 * @param values
	 * @return multiValueCriterion
	 */
	public Criterion getCriterion(MultiValueQueryOperator op, String queryField, List<?> values);
	
	/**
	 * Return a MultiStringValueCriterion expressing: "queryField in 'values1', 'value2', ... 'valueX'"
	 * 
	 * @param op
	 * @param queryField
	 * @param values
	 * @return multiStringValueCriterion
	 */
	public Criterion getCriterion(MultiStringValueQueryOperator op, String queryField, List<String> values);
	
	/**
	 * Return a LogicalCriterion to join 2 criteria, expressing either depending on the queryOperator:
	 * 
	 * "leftHandCriterion AND rightHandCriterion"
	 * "leftHandCriterion OR rightHandCriterion"
	 * 
	 * @param op
	 * @param lhc
	 * @param rhc
	 * @return logicalCriterion
	 */
	public Criterion getCriterion(LogicalQueryOperator op, Criterion lhc, Criterion rhc);
}
