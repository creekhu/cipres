package org.ngbw.web.model.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.web.model.Page;

public class DataRecordPage<T extends DataRecord> implements Page<T>
{
	// Page properties
	private GenericDataRecordCollection<T> elements;
	private int pageSize;
	private int pageNumber;
	
	/*
	 * constructors
	 */
	public DataRecordPage() {
		this(null);
	}
	
	public DataRecordPage(GenericDataRecordCollection<T> elements) {
		this(elements, 20);
	}
	
	public DataRecordPage(GenericDataRecordCollection<T> elements, int pageSize) {
		setElements(elements);
		setPageSize(pageSize);
		setPageNumber(0);
	}
	
	/*
	 * Page property accessor methods
	 */
	public GenericDataRecordCollection<T> getElements() {
		return elements;
	}

	public void setElements(GenericDataRecordCollection<T> elements) {
		this.elements = elements;
	}
	
	/*
	 * Page methods
	 */
	@SuppressWarnings("unchecked")
	public void sortElements(Comparator<T> comparator) {
		throw new UnsupportedOperationException();
	}
	
	public void next() {
		setPageNumber(getPageNumber() + 1);
	}

	public void previous() {
		setPageNumber(getPageNumber() - 1);
	}

	public void first() {
		setPageNumber(0);
	}

	public void last() {
		setPageNumber(getLastPageNumber());
	}
	
	public void setPageNumber(int pageNumber) {
		if (pageNumber < 0)
			this.pageNumber = 0;
		else {
			int lastPageNumber = getLastPageNumber();
			if (pageNumber > lastPageNumber)
				this.pageNumber = lastPageNumber;
			else this.pageNumber = pageNumber;
		}
	}
	
	public void setPageSize(int pageSize) {
		if (pageSize < 1)
			this.pageSize = 1;
		else this.pageSize = pageSize;
	}
	
	public boolean isFirstPage() {
		return (getPageNumber() == 0);
	}
	
	public boolean isLastPage() {
		return (getPageNumber() == getLastPageNumber());
	}
	
	public boolean hasNextPage() {
		return (getPageNumber() < getLastPageNumber());
	}
	
	public boolean hasPreviousPage() {
		return (getPageNumber() > 0);
	}
	
	public boolean hasPageWithNumber(int pageNumber) {
		return (pageNumber >= 0 && pageNumber <= getLastPageNumber());
	}
	
	public boolean hasPageWithLabel(String pageNumberLabel) {
		try {
			int pageNumber = Integer.parseInt(pageNumberLabel) - 1;
			return hasPageWithNumber(pageNumber);
		} catch (Exception e) { return false; }
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public String getPageNumberLabel() {
		return Integer.toString(getPageNumber() + 1);
	}
	
	public int getLastPageNumber() {
		int numberOfPages =
			(int)Math.ceil((double)getTotalNumberOfElements() / getPageSize());
		if (numberOfPages <= 0)
			return 0;
		else return (numberOfPages - 1);
	}
	
	public String getLastPageNumberLabel() {
		return Integer.toString(getLastPageNumber() + 1);
	}
	
	public int getNextPageNumber() {
		if (hasNextPage())
			return getPageNumber() + 1;
		else return getPageNumber();
	}
	
	public String getNextPageNumberLabel() {
		return Integer.toString(getNextPageNumber() + 1);
	}
	
	public int getPreviousPageNumber() {
		if (hasPreviousPage())
			return getPageNumber() - 1;
		else return getPageNumber();
	}
	
	public String getPreviousPageNumberLabel() {
		return Integer.toString(getPreviousPageNumber() + 1);
	}
	
	public List<T> getThisPageElements() {
		if (getTotalNumberOfElements() < 1)
			return null;
		else return getElements().slice(getThisPageFirstElementNumber(),
			(getThisPageLastElementNumber() + 1)).toList();
	}
	
	public List<T> getAllElements() {
		return getElements().toList();
	}
	
	public T getPageElement(int index) {
		if (getTotalNumberOfElements() < 1)
			return null;
		else if (index < 0)
			return elements.get(0);
		else if (index >= elements.size())
			return elements.get(elements.size() - 1);
		else return elements.get(index);
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public int getThisPageNumberOfElements() {
		if (isLastPage() == false)
			return getPageSize();
		else return (getTotalNumberOfElements() - getThisPageFirstElementNumber());
	}
	
	public int getTotalNumberOfElements() {
		if (elements == null)
			return 0;
		else return elements.size();
	}
	
	public int getThisPageFirstElementNumber() {
		return (getPageNumber() * getPageSize());
	}
	
	public int getThisPageLastElementNumber() {
		int lastElementNumber =
			getThisPageFirstElementNumber() + getThisPageNumberOfElements() - 1;
		if (lastElementNumber <= 0)
			return 0;
		else return lastElementNumber;
	}

	public List<String> getPageNumberLabels() {
		int numberOfPages = getLastPageNumber() + 1;
		List<String> pageNumberLabels = new Vector<String>(numberOfPages);
		for (int i=0; i<numberOfPages; i++)
			pageNumberLabels.add(Integer.toString(i));
		return pageNumberLabels;
	}
}
