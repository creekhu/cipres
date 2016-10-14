/*
 * StaleRowException.java
 */
package org.ngbw.sdk.database;


import org.ngbw.sdk.WorkbenchException;


/**
 * 
 * @author Paul Hoover
 *
 */
public class StaleRowException extends WorkbenchException {

	static final long serialVersionUID = -4446576841446653747L;


	// constructors


	StaleRowException(String tableName, Criterion key)
	{
		super("Stale row state detected for table " + tableName + ", key " + key.toString());
	}

	StaleRowException(String tableName, Criterion key, int oversion, int nversion)
	{
		super("Stale row state detected for table " + tableName + ", key " + key.toString() + ". old_version= " +
			oversion + ", new_version=" + nversion);
	}
}
