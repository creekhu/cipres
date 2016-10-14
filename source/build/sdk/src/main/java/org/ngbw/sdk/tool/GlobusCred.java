/**
 *  Copyright (c) 2007-2009 CyberInfrastructure and Geospatial
 *  Information Laboratory (CIGI), University of Illinois at
 *  Urbana-Champaign, All Rights Reserved.
 */

package org.ngbw.sdk.tool;


/**
	This is a singleton that we use instead of using SimpleCred directly.

	If it weren't a singleton we'd end up reading the proxy credential file every time we
	instantiated a GlobusCred or SimpleCred and that's OK, but its not very efficient.

	This way, every time someone class GlobusCred.getInstance().get(), they're calling get()
	on the same SimpleCred object which has a proxy object in memory.  If the proxy is about
	to expire SimpleCred will get a new one from myproxy updating the copy in memory and on
	disk.
*/
public class GlobusCred extends SimpleCred
{
	//private static final Log m_log = LogFactory.getLog(GlobusCred.class.getName());
	private static GlobusCred instance = new GlobusCred();

	private GlobusCred() 
	{
		super(GlobusConfig.getHost(), GlobusConfig.getPort(), GlobusConfig.getUsername(), 
			GlobusConfig.getPassword(), GlobusConfig.getProxyFile(), 
			GlobusConfig.getLifeTime(), GlobusConfig.getMinLifeTime());
	}

	public static GlobusCred getInstance() throws Exception
	{
		return instance;
	}
}
