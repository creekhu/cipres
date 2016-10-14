package org.ngbw.web.model;

import java.util.Comparator;
import java.util.List;

public interface Page<T>
{
	/**
	 * Sort the backing collection with the supplied Comparator.
	 * Resets the page number to the first page of the newly sorted collection.
	 * voids the elements for query and criteria based page implementations
	 */
	void sortElements(Comparator<T> comparator);
	
	/**
	 * Set pageNumber to the  pageNumber of the next Page
	 * voids the elements for query and criteria based page implementations
	 */
	void next();
	
	/**
	 * Set pageNumber to the  pageNumber of the previous Page
	 * voids the elements for query and criteria based page implementations
	 */
	void previous();

	/**
	 * Set pageNumber to the  pageNumber of the first Page
	 * voids the elements for query and criteria based page implementations
	 */
	void first();
	
	/**
	 * Set pageNumber to the  pageNumber of the last Page
	 * voids the elements for query and criteria based page implementations
	 * 
	 */
	void last();
	
	/**
	 * Set pageNumber to the submitted pageNumber
	 * voids the elements for query and criteria based page implementations
	 */
	void setPageNumber(int pageNumber);
	
	/**
	 * Set pageSize to the submitted pageSize
	 * voids the elements for query and criteria based page implementations
	 */
	void setPageSize(int pageSize);
	
	/**
	 * @returns true if this page is the first page
	 */
	boolean isFirstPage();

	/**
	 * @returns true if this page is the last page
	 */
	boolean isLastPage();

	/**
	 * @returns true if this page has a next page
	 */
	boolean hasNextPage();

	/**
	 * @returns true if this page has a previous page
	 */
	boolean hasPreviousPage();
	
	/**
	 * @returns true whether a page with this number (0 based) exists
	 */
	boolean hasPageWithNumber(int pageNumber);

	/**
	 * @returns true whether a page with this number label (1 based) exists
	 */
	boolean hasPageWithLabel(String pageNumberLabel);
	
	/**
	 * @return page number of this page 0-based
	 */
	int getPageNumber();

	/**
	 * @return page number of this page 1-based
	 */
	String getPageNumberLabel();

	/**
	 * @return page number of the last page 0-based
	 */
	int getLastPageNumber();

	/**
	 * @return page number of the last page 1-based
	 */
	String getLastPageNumberLabel();

	/**
	 * @return page number of the next page 0-based
	 */
	int getNextPageNumber();

	/**
	 * @return page number of the next page 1-based
	 */
	String getNextPageNumberLabel();

	/**
	 * @return page number of the previous page 0-based
	 */
	int getPreviousPageNumber();

	/**
	 * @return page number of the previous page 1-based
	 */
	String getPreviousPageNumberLabel();

	/**
	 * @return list of all elements on this page
	 */
	List<T> getThisPageElements();

	/**
	 * @return all elements in the backing list
	 */
	List<T> getAllElements();
	
	/**
	 * A page element corresponds to a row in the result list
	 * and has getters for the values for all columns 
	 * and the Long value for the id property
	 * 
	 * @param index
	 * @return the page element with this index (0-based)
	 */
	T getPageElement(int index);

	/**
	 * @return number of elements per page
	 */
	int getPageSize();

	/**
	 * @return number of elements actually displayed on this page
	 */
	int getThisPageNumberOfElements();

	/**
	 * @return total number of elements
	 */
	int getTotalNumberOfElements();

	/**
	 * @return (row) number of the first element this page 1-based
	 */
	int getThisPageFirstElementNumber();

	/**
	 * @return (row) number of the last element this page 1-based
	 */
	int getThisPageLastElementNumber();
	
	/**
	 * @return List of pageNumberLabels (page 1 - last)
	 */
	List<String> getPageNumberLabels();
}
