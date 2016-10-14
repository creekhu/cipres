/*
 * GenericDataRecordCollection.java 
 */
package org.ngbw.sdk.api.core;


import java.util.List;
import java.util.Set;

import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;


/**
 * A DataRecordCollection is a List like collection of DataRecord elements.
 * Equivalent to a typed List all elements must have the same RecordType.
 * 
 * @author Roland H. Niedner
 *
 */
public interface GenericDataRecordCollection<T extends DataRecord> extends Iterable<T> {

	/**
	 * Method return the RecordType of the DataRecord. The RecordType 
	 * corresponds to the concept name in the central registry and
	 * entails the definition of DataType, EntityType as well as to
	 * which RecordFields can be extracted from each DataRecord.
	 * 
	 * @return RecordType
	 */
    public RecordType getRecordType();

    /**
     * Method return the Set of RecordFields which can be extracted
     * from each DataRecord.
     * 
     * @return recordFields
     */
    public Set<RecordFieldType> getFields();
    
    /**
     * Returns the centrally registered FieldType for the submitted RecordField.
     * 
     * @param field
     * @return FieldType
     */
    public FieldDataType getType(RecordFieldType field);
    
    /**
     * Add the submitted DataRecord to the collection at the submitted index.
     * Behavior is inherited from the underlying List.
     * 
     * @param index
     * @param dataRecord
     */
    public void add(int index, T dataRecord);
    
    /**
     * Add the submitted DataRecord to the collection.
     * Behavior is inherited from the underlying List.
     * 
     * @param dataRecord
     * @return boolean indication whether the Record was added.
     */
    public boolean add(T dataRecord);
    
    /**
     * Returns a slice of the List of DataRecords 
     * that this collection is based upon.
     * 
     * @return List<DataRecord>
     */
    public List<T> toList(int fromIndex, int toIndex);
    
    /**
     * Returns the List of DataRecords that this collection is based upon.
     * 
     * @return List<DataRecord>
     */
    public List<T> toList();
    
    /**
     * Returns a slice of the DataRecordCollection.
     * 
     * @param fromIndex
     * @param toIndex
     * @return records
     */
    public GenericDataRecordCollection<T> slice(int fromIndex, int toIndex);
    
    
    /**
     * Return the DataRecord positioned at the submitted index.
     * Behavior is inherited from the underlying List.
     * 
     * @param index
     * @return dataRecord
     */
    public T get(int index);
    
    /**
     * Returns the size of the DataRecordCollection.
     * 
     * @return size of the collection
     */
    public int size();
    
    /**
     * Method sorts the DataRecords in this collection based on the submitted
     * RecordField. The second boolean argument triggers reverse sorting if true.
     * 
     * @param field
     * @param reverse
     */
    public void sortByField(RecordFieldType field, boolean reverse);
}
