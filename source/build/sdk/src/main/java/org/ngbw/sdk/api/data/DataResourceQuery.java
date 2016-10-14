package org.ngbw.sdk.api.data;


/**
 * A DataResourceQuery gives access to all Datasets hosted by oneDataResource.
 * This interface typically is implemented for 1 RDBMS where 
 * 1 SQL statement can interrogate and return DataRecords from
 * multiple (all) Datasets.
 * 
 * Execute will return a List of Results wrapped in a QueryResult.  
 * Each Result is again a List of Objects wrapped in a ResultTuple. 
 * The method getReturnedFieldNames() will list the names of the fields
 * in the same order as the order of the returned values.
 * the method getReturnedDataTypes() lists the DataTypes in the order of 
 * the returned values.
 * 
 * @author Roland H. Niedner <br />
 *
 */
public interface DataResourceQuery {
	
	/**
	 * Method set the maximum number of returned results to the
	 * submitted positive integer.
	 * 
	 * @param maxResults
	 */
	public void setMaxResults(int maxResults);
	
	/**
	 * Executes the query, closes it and returns the results.
	 * 
	 * @return List of List of values (Objects)
	 */
	public QueryResult execute();
	
	/**
	 * Close the query without executing it and
	 * return system resources.
	 */
	public void close();
}
