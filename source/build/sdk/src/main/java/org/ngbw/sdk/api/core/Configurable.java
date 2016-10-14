/**
 * 
 */
package org.ngbw.sdk.api.core;


/**
 * Interface to be indicate that the instance needs configuration.
 * <br />
 * @author hannes
 *
 */
public interface Configurable<T> {
	
	/**
	 * Method uses the key value pairs of the submitted to configure the Configurable
	 * instance by setting. It returns the same value as the method isConfigured().
	 * 
	 * @param cfg
	 * @return true or false
	 */
	public boolean configure(T cfg);
	
	/**
	 * Method allows clients to check whether the Configurable instance is ready to perform
	 * its duties. If the method returns false then it is an indication that the 
	 * configure(Map<String, String> cfg) method still needs to be called or the 
	 * call was unsuccessful or incomplete.
	 * 
	 * @return true or false
	 */
	public boolean isConfigured();
}
