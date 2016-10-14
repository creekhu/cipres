/*
 * ComparatorUtils.java
 */
package org.ngbw.sdk.database.util;


import java.util.Date;

import org.ngbw.sdk.database.FolderItem;


/**
 * 
 * @author Roland H. Niedner
 *
 */
class ComparatorUtils {


	public static int compareInteger(Integer a, Integer b, boolean reverse) {
		if (a == b) return 0;
		if (a == null) a = 0;
		if (b == null) b = 0;
		if (reverse) return b - a;
		return a - b;
	}
	
	public static int compareLong(Long a, Long b, boolean reverse) {
		if (a == b) return 0;
		if (a == null) a = 0L;
		if (b == null) b = 0L;
		if (reverse) return (int) (b - a);
		return (int) (a - b);
	}
	
	public static int compareFloat(Float a, Float b, boolean reverse) {
		if (a == b) return 0;
		if (a == null) a = 0F;
		if (b == null) b = 0F;
		if (reverse) {
			if (b > a) return 1;
			return -1;
		}
		if (b > a) return -1;
		return 1;
	}
	
	public static int compareDouble(Double a, Double b, boolean reverse) {
		if (a == b) return 0;
		if (a == null) a = 0D;
		if (b == null) b = 0D;
		if (reverse) {
			if (b > a) return 1;
			return -1;
		}
		if (b > a) return -1;
		return 1;
	}
	
	public static int compareDate(Date a, Date b, boolean reverse) {
		if (a == b) return 0;
		if (a == null)
			if (reverse)
				return 1;
			else {
				return -1;
			}
		if (b == null)
			if (reverse)
				return -1;
			else {
				return 1;
			}
		if (reverse) {
			if (a.after(b)) return -1;
			return 1;
		}
		if (a.after(b)) return 1;
		return -1;
	}

	public static int compareString(String a, String b, boolean reverse) {
		if (a == b) return 0;
		if (a == null)
			if (reverse)
				return 1;
			else {
				return -1;
			}
		if (b == null)
			if (reverse)
				return -1;
			else {
				return 1;
			}
		if (reverse) return -a.compareToIgnoreCase(b);
		return a.compareToIgnoreCase(b);
	}

	public static int compareLabel(FolderItem a, FolderItem b, boolean reverse) {
		return compareString(a.getLabel(), b.getLabel(), reverse);
	}

	public static int compareOwner(FolderItem a, FolderItem b, boolean reverse) {
		return compareLong(a.getUserId(), b.getUserId(), reverse);
	}

	public static int compareGroup(FolderItem a, FolderItem b, boolean reverse) {
		return compareLong(a.getGroupId(), b.getGroupId(), reverse);
	}

	public static int compareCreationDate(FolderItem a, FolderItem b, boolean reverse) {
		return compareDate(a.getCreationDate(), b.getCreationDate(), reverse);
	}
}
