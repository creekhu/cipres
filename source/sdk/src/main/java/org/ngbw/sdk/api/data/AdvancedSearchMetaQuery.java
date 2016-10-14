/**
 * 
 */
package org.ngbw.sdk.api.data;

import java.util.List;
import java.util.Set;

import org.ngbw.sdk.core.query.LogicalQueryOperator;
import org.ngbw.sdk.core.query.MultiStringValueQueryOperator;
import org.ngbw.sdk.core.query.MultiValueQueryOperator;
import org.ngbw.sdk.core.query.NullValueQueryOperator;
import org.ngbw.sdk.core.query.QueryOperator;
import org.ngbw.sdk.core.query.SingleStringValueQueryOperator;
import org.ngbw.sdk.core.query.SingleValueQueryOperator;
import org.ngbw.sdk.core.query.TwoValueQueryOperator;

/**
 * A AdvancedSearchMetaQuery extends the SimpleSearchMetaQuery enabling
 * Criterion based searches. It wraps the federation for all spawned
 * AdvancedQueries (one for each requested Dataset).
 * 
 * @author Roland H. Niedner <br />
 *
 */
public interface AdvancedSearchMetaQuery extends SimpleSearchMetaQuery {
	
	/**
	 * 
	 * @return set of field names designating all
	 * queryable fields across all datasets of this MetaQuery.
	 */
	public Set<String> getQueryableFields();
	
	/**
	 * This method will list all available QueryOperators
	 * for the submitted particular queryableField across all 
	 * datasets of this MetaQuery.
	 * 
	 * @param queryField
	 * @return Set of QueryOperators
	 */
	public Set<QueryOperator> getQueryOperators(String queryField);

	/**
	 * Add a query criterion to restrict the number of returned results.
	 * Query criteria have to be set before the query is executed.
	 * @param criterion
	 * @return query
	 */
	public AdvancedSearchMetaQuery addCriterion(Criterion criterion);
	
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

	/**
	 * Execute query triggers the execution of all federated DataResource
	 * queries.
	 * 
	 * @return total number of results
	 */
	public int execute();
}
