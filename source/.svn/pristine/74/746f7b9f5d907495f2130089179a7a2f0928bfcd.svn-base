/*
 * FolderComparator.java
 */
package org.ngbw.sdk.database.util;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ngbw.sdk.database.Folder;


/**
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class FolderComparator implements Comparator<Folder> {

	private final FolderSortableField field;
	private final boolean reverse;


	public FolderComparator(FolderSortableField field, boolean reverse)
	{
		this.field = field;
		this.reverse = reverse;
	}


	public int compare(Folder a, Folder b)
	{
		if (a == null)
			return -1;

		if (b == null)
			return 1;

		if (a.equals(b))
			return 0;

		if (field.equals(FolderSortableField.ID))
			return ComparatorUtils.compareLong(a.getFolderId(), b.getFolderId(), reverse);
		else if (field.equals(FolderSortableField.OWNER))
			return ComparatorUtils.compareOwner(a, b, reverse);
		else if (field.equals(FolderSortableField.GROUP))
			return ComparatorUtils.compareGroup(a, b, reverse);
		else if (field.equals(FolderSortableField.LABEL))
			return ComparatorUtils.compareLabel(a, b, reverse);
		else if (field.equals(FolderSortableField.CREATION_DATE))
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

		if (other instanceof FolderComparator == false)
			return false;

		FolderComparator otherComp = (FolderComparator) other;

		return field == otherComp.field && reverse == otherComp.reverse;
	}

	public static void sort(List<Folder> folders, FolderSortableField field, boolean reverse)
	{
		Collections.sort(folders, new FolderComparator(field, reverse));
	}
}
