package org.ngbw.sdk.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/*
	We have a number of cases where we need to convert between Map<String, String> and
	Map<String, List<String>> where the list only has one item or we only care about
	the first item.  
*/

public class MapUtil
{
	public static <T> Map<String, T> collapseList(Map<String, List<T>> inmap)
	{
		Map<String, T> outmap = new HashMap<String, T>(inmap.size());
		for (String key : inmap.keySet())
		{
			T firstItem = null;
			List<T> list = inmap.get(key);
			if (list != null && list.size() > 0)
			{
				firstItem = list.get(0);	
			}
			outmap.put(key, firstItem); 
		}
		return outmap;
	}

	public static <T> Map<String, List<T>> wrapInList(Map<String, T> inmap)
	{
		Map<String, List<T>> outmap = new HashMap<String, List<T>>(inmap.size());
		for (String key : inmap.keySet())
		{
			ArrayList<T> al = new ArrayList<T>(1);
			al.add(inmap.get(key));
			outmap.put(key, al);
		}
		return outmap;
	}
}

