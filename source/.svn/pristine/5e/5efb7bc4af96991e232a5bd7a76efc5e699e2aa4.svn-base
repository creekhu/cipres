package org.ngbw.sdk.api.data;

import java.util.Set;

/**
 * @author Roland H. Niedner <br />
 * 
 * A QueryResult is a generic data structure that provides
 * access to the query results (ResultTuples) of a DataResourceQuery for one Dataset.
 * This QueryResult is typically only accessed by MetaQuery implementations
 * and directly translated into a DataRecordCollection (potentially together 
 * with QueryResult from other Datasets as specified by the MetaQuery.
 * 
 */
public interface QueryResult extends Iterable<ResultTuple> {

	/**
	 * Method returns the field names for each tuple in the result.
	 * 
	 * @return field names
	 */
	public Set<String> getReturnedFields();

	/**
	 * Method returns the number of returned Records.
	 * @return size
	 */
	public Integer getSize();

}
