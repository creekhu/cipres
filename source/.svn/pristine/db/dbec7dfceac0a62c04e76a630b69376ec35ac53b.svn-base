/*
 * UserDataItemComparator.java
 */
package org.ngbw.sdk.database.util;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ngbw.sdk.database.UserDataItem;


/**
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class UserDataItemComparator implements Comparator<UserDataItem> {

	private final UserDataItemSortableField field;
	private final boolean reverse;


	public UserDataItemComparator(UserDataItemSortableField field, boolean reverse)
	{
		this.field = field;
		this.reverse = reverse;
	}

	public int compare(UserDataItem a, UserDataItem b)
	{
		if (a == null)
			return -1;

		if (b == null)
			return 1;

		if (a.equals(b))
			return 0;
	
		if (field.equals(UserDataItemSortableField.ID))
			return ComparatorUtils.compareLong(a.getUserDataId(), b.getUserDataId(), reverse);
		else if (field.equals(UserDataItemSortableField.OWNER))
			return ComparatorUtils.compareOwner(a, b, reverse);
		else if (field.equals(UserDataItemSortableField.GROUP))
			return ComparatorUtils.compareGroup(a, b, reverse);
		else if (field.equals(UserDataItemSortableField.LABEL))
			return ComparatorUtils.compareLabel(a, b, reverse);
		else if (field.equals(UserDataItemSortableField.CREATION_DATE))
			return ComparatorUtils.compareCreationDate(a, b, reverse);
		else if (field.equals(UserDataItemSortableField.ENTITY_TYPE))
			return compareEntityType(a, b);
		else if (field.equals(UserDataItemSortableField.DATA_TYPE))
			return compareDataType(a, b);
		else if (field.equals(UserDataItemSortableField.DATA_FORMAT))
			return compareDataFormat(a, b);
		else
			return 0;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof UserDataItemComparator == false)
			return false;

		UserDataItemComparator otherComp = (UserDataItemComparator) other;

		return field == otherComp.field && reverse == otherComp.reverse;
	}

	public static void sort(List<UserDataItem> items, UserDataItemSortableField field, boolean reverse)
	{
		Collections.sort(items, new UserDataItemComparator(field, reverse));
	}


	private int compareEntityType(UserDataItem a, UserDataItem b)
	{
		String at = (a.getEntityType() == null) ? null : a.getEntityType().toString();
		String bt = (b.getEntityType() == null) ? null : b.getEntityType().toString();

		return ComparatorUtils.compareString(at, bt, reverse);
	}

	private int compareDataType(UserDataItem a, UserDataItem b)
	{
		String at = (a.getDataType() == null) ? null : a.getDataType().toString();
		String bt = (b.getDataType() == null) ? null : b.getDataType().toString();

		return ComparatorUtils.compareString(at, bt, reverse);
	}

	private int compareDataFormat(UserDataItem a, UserDataItem b)
	{
		String at = (a.getDataFormat() == null) ? null : a.getDataFormat().toString();
		String bt = (b.getDataFormat() == null) ? null : b.getDataFormat().toString();

		return ComparatorUtils.compareString(at, bt, reverse);
	}
}
