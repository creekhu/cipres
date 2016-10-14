/*
 * WorkbenchException.java
 */
package org.ngbw.sdk;


/**
 * @author Roland H. Niedner
 *
 */
public class WorkbenchException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7331146612162504952L;

	/**
     * Constructor for the FrameworkException object
     */
    public WorkbenchException() {
        super();
    }


    /**
     * Constructor for the FrameworkException object
     *
     * @param message
     */
    public WorkbenchException(String message) {
        super(message);
    }


    /**
     * Constructor for the FrameworkException object
     *
     * @param e
     */
    public WorkbenchException(Throwable e) {
        super(e);
    }

    /**
     * Constructor for the FrameworkException object
     *
     * @param message
     * @param e
     */
    public WorkbenchException(String message, Throwable e) {
        super(message, e);
    }
}