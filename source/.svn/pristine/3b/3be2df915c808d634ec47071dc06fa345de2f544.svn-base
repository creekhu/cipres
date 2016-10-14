/*
 * TaskComparator.java
 */
package org.ngbw.sdk.database.util;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ngbw.sdk.database.Task;


/**
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class TaskComparator implements Comparator<Task> {

	private final TaskSortableField field;
	private final boolean reverse;


	public TaskComparator(TaskSortableField field, boolean reverse)
	{
		this.field = field;
		this.reverse = reverse;
	}

	public int compare(Task a, Task b)
	{
		if (a == null)
			return -1;

		if (b == null)
			return 1;

		if (a.equals(b))
			return 0;

		if (field.equals(TaskSortableField.ID))
			return ComparatorUtils.compareLong(a.getTaskId(), b.getTaskId(), reverse);
		else if (field.equals(TaskSortableField.OWNER))
			return ComparatorUtils.compareOwner(a, b, reverse);
		else if (field.equals(TaskSortableField.GROUP))
			return ComparatorUtils.compareGroup(a, b, reverse);
		else if (field.equals(TaskSortableField.LABEL))
			return ComparatorUtils.compareLabel(a, b, reverse);
		else if (field.equals(TaskSortableField.CREATION_DATE))
			return ComparatorUtils.compareCreationDate(a, b, reverse);
		else if (field.equals(TaskSortableField.TOOL))
			return ComparatorUtils.compareString(a.getToolId(), b.getToolId(), reverse);
		else if (field.equals(TaskSortableField.JOBHANDLE))
			return ComparatorUtils.compareString(a.getJobHandle(), b.getJobHandle(), reverse);
		else if (field.equals(TaskSortableField.STAGE))
			return a.getStage().compareTo(b.getStage());
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

		if (other instanceof TaskComparator == false)
			return false;

		TaskComparator otherComp = (TaskComparator) other;

		return field == otherComp.field && reverse == otherComp.reverse;
	}

	public static void sort(List<Task> tasks, TaskSortableField field, boolean reverse)
	{
		Collections.sort(tasks, new TaskComparator(field, reverse));
	}
}
