/*
 * SimpleSearchMetaQuery.java
 */
package org.ngbw.sdk.api.data;


import java.text.ParseException;
import java.util.concurrent.ExecutionException;


/**
 * Interface the realize a SimpleSearch. See comments for MetaQuery. The
 * MetaQuery extends this interface and allows to build a more complex query by
 * adding search criteria. The interface enable SimpleSearchMetaQuery reuse with
 * multiple search phrases thus requires the implementor to trigger cleanup of
 * existing prior search results.
 * 
 * @author Roland H. Niedner <br />
 * 
 */
public interface SimpleSearchMetaQuery extends MetaQuery {
	
	/**
	 * Execute fires the query and returns the number of items found.
	 * 
	 * @param searchPhrase
	 * @return result size
	 */
	public int execute(String searchPhrase) throws ParseException, InterruptedException, ExecutionException;
	
	/**
	 * Execute fires the query and returns the number of items found.
	 * The boolean allows to specify to run in parallel mode or not.
	 * Default is parallel mode.
	 * 
	 * @param searchPhrase
	 * @param parallel
	 * @return result size
	 */
	public int execute(String searchPhrase, boolean parallel) throws ParseException, InterruptedException, ExecutionException;
	
}
