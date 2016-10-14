package org.ngbw.web.model.impl;

import java.util.Comparator;

import org.ngbw.sdk.core.types.RecordFieldType;

public class RecordFieldTypeComparator implements Comparator<RecordFieldType>
{
	public int compare(RecordFieldType r1, RecordFieldType r2) {
		if (r1 == null || r2 == null)
			throw new NullPointerException("Compared record fields cannot be null.");
		else {
			if (r1.equals(RecordFieldType.DATA_ID))
				return -1;
			else if (r2.equals(RecordFieldType.DATA_ID))
				return 1;
			else if (r1.equals(RecordFieldType.PRIMARY_ID))
				return -1;
			else if (r2.equals(RecordFieldType.PRIMARY_ID))
				return 1;
			else if (r1.equals(RecordFieldType.ALTERNATIVE_ID))
				return -1;
			else if (r2.equals(RecordFieldType.ALTERNATIVE_ID))
				return 1;
			else if (r1.equals(RecordFieldType.NAME))
				return -1;
			else if (r2.equals(RecordFieldType.NAME))
				return 1;
			else if (r1.equals(RecordFieldType.QUERY))
				return -1;
			else if (r2.equals(RecordFieldType.QUERY))
				return 1;
			else return r1.compareTo(r2);
		}
	}
}
