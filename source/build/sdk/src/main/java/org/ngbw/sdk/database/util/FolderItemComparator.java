/*
 * FolderItemComparator.java
 */
package org.ngbw.sdk.database.util;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ngbw.sdk.database.FolderItem;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class FolderItemComparator implements Comparator<FolderItem> {

	private final FolderItemSortableField field;
	private final boolean reverse;


	public FolderItemComparator(FolderItemSortableField field, boolean reverse)
	{
		this.field = field;
		this.reverse = reverse;
	}


	public int compare(FolderItem a, FolderItem b)
	{
		if (a == null)
			return -1;

		if (b == null) 
			return 1;

		if (a.equals(b))
			return 0;

		if (field.equals(FolderItemSortableField.OWNER))
			return ComparatorUtils.compareOwner(a, b, reverse);
		else if (field.equals(FolderItemSortableField.GROUP))
			return ComparatorUtils.compareGroup(a, b, reverse);
		else if (field.equals(FolderItemSortableField.LABEL))
			return ComparatorUtils.compareLabel(a, b, reverse);
		else if (field.equals(FolderItemSortableField.CREATION_DATE))
			return ComparatorUtils.compareCreationDate(a, b, reverse);
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

		if (other instanceof FolderItemComparator == false)
			return false;

		FolderItemComparator otherComp = (FolderItemComparator) other;

		return field == otherComp.field && reverse == otherComp.reverse;
	}

	public static void sort(List<FolderItem> folderItems, FolderItemSortableField field, boolean reverse)
	{
		Collections.sort(folderItems, new FolderItemComparator(field, reverse));
	}
}
