package org.ngbw.sdk.api.data;

import java.util.Set;

import org.ngbw.sdk.core.types.Dataset;

/**
 * @author Roland H. Niedner <br />
 *
 * A ResultTuple is the element of a QueryResult returned by a
 * DataResourceQuery. It serves a low level shuttle for a 
 * result row returned for a query and is translated into
 * a DataRecord by the MetaQuery implementor.
 */
public interface ResultTuple {

	/**
	 * Returns the Dataset that this tuple belongs to.
	 * 
	 * @return Dataset
	 */
	public Dataset getDataset();
	
	/**
	 * Return the primaryKey that allows to retrieve the fulltext source
	 * for the returned ResultTuple.
	 * 
	 * @return sourceKey (Object)
	 */
	public Object getSourceKey();
	
	/**
	 * Return a set of strings corresponding to the fields
	 * (columns) of 1 row of the result set.
	 * 
	 * @return fields
	 */
	public Set<String> getFields();
	
	/**
	 * Return the value for the submitted field.
	 * 
	 * @param field
	 * @return value (String)
	 */
	public String getValue(String field);
	
	/**
	 * Return the rank assigned by the search engine.
	 * 
	 * @return rank
	 */
	public float getRank();
}
