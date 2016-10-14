/*
 * DataRecordCollection.java
 */
package org.ngbw.sdk.api.core;


import org.ngbw.sdk.database.DataRecord;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public interface DataRecordCollection<T extends DataRecord> extends GenericDataRecordCollection<T> {
    
    /**
     * Add another compatible DataRecordCollection to this DataRecordCollection.
     * This requires that they share the same RecordType. The method will result
     * in a UNION of both collections with possible duplicates (underlying List).
     * 
     * @param dataRecords
     */
    public void addAll(DataRecordCollection<T> dataRecords);

}
