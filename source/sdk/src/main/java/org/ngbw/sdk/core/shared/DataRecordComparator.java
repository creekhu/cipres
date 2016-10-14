/*
 * DataRecordComparator.java
 */
package org.ngbw.sdk.core.shared;


import java.util.Comparator;
import java.util.Date;

import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.RecordField;


/**
 * 
 * @author Roland H. Niedner
 *
 * @param <T>
 */
public class DataRecordComparator<T extends DataRecord> implements Comparator<T> {

	protected final RecordFieldType field;
	protected boolean reverse = false;
	
	public DataRecordComparator(RecordFieldType field, boolean reverse) {
		this.field = field;
		this.reverse  = reverse;
	}

	public int compare(T dra, T drb) {
		if (dra == drb) return 0;
		if (dra == null) return -1;
		if (drb == null) return 1;
		if (dra.equals(drb)) return 0;
		if (dra.getRecordType().equals(drb.getRecordType()) == false)
			throw new RuntimeException("Incompatible RecordTypes!");
		
		RecordField comp = dra.getField(field);
		FieldDataType type = field.dataType();
		
		if (FieldDataType.INTEGER.equals(type)) {
			Integer a = comp.getValueAsInteger();
			Integer b = comp.getValueAsInteger();
			if (reverse) return a - b;
			return b - a;
			
		} else if (FieldDataType.FLOAT.equals(type)) {
			Float a = comp.getValueAsFloat();
			Float b = comp.getValueAsFloat();
			if (reverse) {
				if (b > a) return 1;
				return -1;
			}
			if (b > a) return -1;
			return 1;
			
		} else if (FieldDataType.DOUBLE.equals(type)) {
			Double a = comp.getValueAsDouble();
			Double b = comp.getValueAsDouble();
			if (reverse) {
				if (b > a) return 1;
				return -1;
			}
			if (b > a) return -1;
			return 1;
			
		} else if (FieldDataType.DATE.equals(type)) {
			Date a = comp.getValueAsDate();
			Date b = comp.getValueAsDate();
			if (reverse) {
				if (a.after(b)) return -1;
				return 1;
			}
			if (a.after(b)) return 1;
			return -1;
			
		} else {
			String a = comp.getValueAsString();
			String b = comp.getValueAsString();
			if (reverse) return -a.compareToIgnoreCase(b);
			return a.compareToIgnoreCase(b);
		}
	}

}
