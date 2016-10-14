/**
 * 
 */
package org.ngbw.sdk.common.util;

/**
 * @author hannes
 *
 */
public class ResourceNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216852320952969457L;

	/**
	 * 
	 */
	public ResourceNotFoundException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public ResourceNotFoundException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ResourceNotFoundException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ResourceNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
