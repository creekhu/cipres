/*
 * DataRecord.java
 */
package org.ngbw.sdk.database;


import java.util.List;

import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * A <code>DataRecord</code> is a generic container (aka "one size fits all") to
 * hold and shuttle information among the various sdk components as well as to sdk
 * clients. A generic <code>DataRecord</code> is typed via its <code>RecordType</code>
 * attribute that defines which <code>RecordFields</code> this record has. The
 * <code>RecordFieldTypes</code> are centrally registered terms that serve as keys
 * to enable named value access.
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 * 
 */
public interface DataRecord {

    /**
     * Returns the type of this record. The <code>RecordType</code> defines which
     * <code>RecordFields</code> (keys) this record has.
     * 
     * @return a <code>RecordType</code> enumeration value that indicates the type of this record
     */
    RecordType getRecordType();

    /**
     * Returns a list of <code>RecordField</code> objects available for this record.
     * 
     * @return a <code>List</code> object containing the <code>RecordField</code> objects available for this record
     */
    List<RecordField> getFields();

    /**
     * Returns the <code>RecordField</code> object that matches the given key. If no match is found,
     * <code>null</code> is returned. 
     * 
     * @param field a <code>RecordFieldType</code> enumeration value that indicates which field to return
     * @return a <code>RecordField</code> object
     */
    RecordField getField(RecordFieldType field);
}
