package org.ngbw.web.model.impl;

import java.util.Comparator;

public class ConceptComparator implements Comparator<String>
{
	// concept constants
	public static final String UNKNOWN = "UNKNOWN";
	
	public int compare(String s1, String s2) {
		if (s1 == null || s2 == null)
			throw new NullPointerException("Compared concepts cannot be null.");
		// UNKNOWN always comes first
		else if (s1.equalsIgnoreCase(UNKNOWN)) {
			// if both concepts are UNKNOWN, then of course they're the same
			if (s2.equalsIgnoreCase(UNKNOWN))
				return 0;
			else return -1;
		} else if (s2.equalsIgnoreCase(UNKNOWN))
			return 1;
		else return s1.compareTo(s2);
	}
}
