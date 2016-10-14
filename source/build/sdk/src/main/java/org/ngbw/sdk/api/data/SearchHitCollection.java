/*
 * SearchHitCollection.java
 */
package org.ngbw.sdk.api.data;


import java.util.List;

import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.data.SearchHit;


/**
 *
 * @author Roland H. Niedner
 *
 */
public interface SearchHitCollection extends GenericDataRecordCollection<SearchHit> {
    
    /**
     * Returns a slice of the DataRecordCollection.
     * 
     * @param fromIndex
     * @param toIndex
     * @return subCollection
     */
    public SearchHitCollection slice(int fromIndex, int toIndex);
    
    /**
     * Returns a slice of the List of DataRecords 
     * that this collection is based upon.
     * 
     * @return List<DataRecord>
     */
    public List<SearchHit> toList(int fromIndex, int toIndex);
    
    /**
     * Add another compatible DataRecordCollection to this DataRecordCollection.
     * This requires that they share the same RecordType. The method will result
     * in a UNION of both collections with possible duplicates (underlying List).
     * 
     * @param dataRecords
     */
    public void addAll(SearchHitCollection dataRecords);
    
    /**
     * Method sorts the SearchHits in this collection based on the 
     * Dataset. The second boolean argument triggers reverse sorting if true.
     * 
     * @param reverse
     */
    public void sortByDataset(boolean reverse);

}
