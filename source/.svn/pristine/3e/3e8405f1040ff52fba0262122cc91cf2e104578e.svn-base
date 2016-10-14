/**
 * 
 * @author Terri Liebowitz Schwartz
 *
 */
package org.ngbw.sdk.common.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

/**
	ClassPath.java
*/
public class ClassPath
{
	/**
		@return ArrayList of urls searched by the classloaders, starting with the
		thread's context class loader.  URls searched by the loader at the top 
		of the heirarchy are listed first, those searched by the context class
		loader are listed last.
	*/
	public static ArrayList<URL> getAllUrls()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null)
		{
			return getUrls(loader);
		}
		return new ArrayList<URL>();
	}

	/**
		Same as getAllUrls(), except starts with the specified loader.
	*/
	public static ArrayList<URL> getUrls(ClassLoader loader)
	{
		ArrayList<URL> list = new ArrayList<URL>();
		if (loader.getParent()  != null)
		{
			list.addAll(getUrls(loader.getParent()));
		}
		if (loader instanceof URLClassLoader)
		{
			URL[] urls = ((URLClassLoader)loader).getURLs();
			list.addAll(Arrays.asList(urls)); 
		}
		return list;
		
	}

	/**
		Converts list of urls to a classpath.
	*/
	public static String toClassPath(ArrayList<URL> list)
	{
		StringBuffer sb = new StringBuffer();
		if (list.size() > 0)
		{
			sb.append(list.get(0));
		}
		for (int i= 1; i < list.size(); i++)
		{
			sb.append(File.pathSeparator);
			sb.append(list.get(i));
		}
		return sb.toString();
	}
}
