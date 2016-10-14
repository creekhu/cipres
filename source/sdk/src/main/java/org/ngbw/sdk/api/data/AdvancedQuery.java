/*
 * AdvancedQuery.java
 */
package org.ngbw.sdk.api.data;


import java.util.Set;

import org.ngbw.sdk.core.query.QueryOperator;
import org.ngbw.sdk.core.types.RecordFieldType;


/**
 * AdvancedQuery extends SimpleQuery to enable Criterion based
 * queries as well as ordering functionality. All available search
 * Criteria will be provided by the DataResource which hosts the
 * respective Dataset.
 * The AdvancedQuery will be called from the AdvancedSearchMetaQuery
 * when it delegates the search request from the sdk client to all 
 * requested Datasets.
 *
 * @author Roland H. Niedner
 * 
 */
public interface AdvancedQuery extends SimpleQuery {
	
	/**
	 * 
	 * @return set of field names designating all
	 * queryable fields
	 */
	public Set<String> getQueryableFields();
	
	/**
	 * This method will list all available QueryOperators
	 * for the submitted particular queryableField.
	 * 
	 * @param queryField
	 * @return set of applicable QueryOperators
	 */
	public Set<QueryOperator> getQueryOperators(String queryField);
	
	/**
	 * Add an ascending order clause for this field to the query.
	 * 
	 * @param field
	 * @return this DataResourceQuery
	 */
	public AdvancedQuery orderAsc(RecordFieldType field);
	
	/**
	 * Add an ascending order clause for this field to the query.
	 * 
	 * @param field
	 * @return this DataResourceQuery
	 */
	public AdvancedQuery orderDesc(RecordFieldType field);
	
	/**
	 * Add a query criterion to built the query.
	 * @param criterion
	 * @return this DataResourceQuery
	 */
	public AdvancedQuery addCriterion(Criterion criterion);
}
