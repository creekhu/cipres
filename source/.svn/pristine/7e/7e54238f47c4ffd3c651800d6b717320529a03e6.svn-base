/*
 * MetaQuery.java
 */
package org.ngbw.sdk.api.data;


import java.util.List;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.data.SearchHit;


/**
 * A MetaQuery is the interface representing the federating 
 * meta query engine, that houses the necessary DataResourceQuery instances and
 * collects the DataResourceQuery results and finally combines
 * them. The implementation also translates DataResource specific 
 * RecordField names into the canonical RecordField concept names
 * listed in the central CoreRegistry using DatasetService which gives
 * access to the mapping.
 * This Query is spawned by the DatasetService, that assigns all 
 * DataResourceQuery necessary to access all requested Datasets.
 *
 * @author Roland H. Niedner
 * 
 */
public abstract interface MetaQuery {
	
	/**
	 * Method set the maximum number of returned results to the
	 * submitted positive integer for each individual Dataset..
	 * 
	 * @param maxResults
	 */
	public void setMaxResultsPerDataset(int maxResults);

	/**
	 * Method returns the total number of results.
	 * @return count
	 */
	public int getResultCount();

	/**
	 * Method returns the number of SearchHits returned by searching
	 * the submitted Dataset
	 * 
	 * @param dataset
	 * @return number of results for this Dataset
	 */
	public int getResultCount(Dataset dataset);
	
	/**
	 * Method returns whether the number of SearchHits returned by searching
	 * the submitted Dataset was truncated by a set limit.
	 * 
	 * @param dataset
	 * @return true/false
	 */
	public boolean isTruncated(Dataset dataset);
	
	/**
	 * Method a list of Dataset where the number of DataRecords returned
	 * was truncated by a set limit.
	 * 
	 * @return datasets
	 */
	public List<Dataset> getTruncatedDatasets();

	/**
	 * Method retrieves all Results across all datasets.
	 * 
	 * @return searchHits
	 */
	public GenericDataRecordCollection<SearchHit> getResults();

	/**
	 * Method allows for paginated retrieval of QueryResults.
	 * 
	 * @param fromIndex - first record index
	 * @param toIndex - last record index
	 * @return searchHits
	 */
	public GenericDataRecordCollection<SearchHit> getResults(int fromIndex, int toIndex);

	/**
	 * Method retrieves the results for the submitted Dataset.
	 * 
	 * @param dataset
	 * @return searchHits
	 */
	public GenericDataRecordCollection<SearchHit> getResults(Dataset dataset);

	/**
	 * Paginated searchHits for the submitted Dataset.
	 * 
	 * @param dataset
	 * @param fromIndex - first record index
	 * @param toIndex - last record index
	 * @return searchHits
	 */
	public GenericDataRecordCollection<SearchHit> getResults(Dataset dataset, int fromIndex, int toIndex);

	/**
	 * Flag indicating whether the query was executed already. If this method
	 * return true all calls to addCriterion will throw an exception.
	 * 
	 * @return true if the MetaQuery.execute() was called.
	 */
	public boolean isExecuted();

	/**
	 * Method retrieves the Record source for the submitted DataRecord.
	 * 
	 * @param searchHit
	 * @return surceDocument
	 */
	public SourceDocument getRecordSource(SearchHit searchHit);
	
	/**
	 * Method return a formatted String of the elapsed search time of the last search.
	 * If the query was not executed yet the method returns null;
	 * 
	 * @return searchTime
	 */
	public String getSearchTime();

}
