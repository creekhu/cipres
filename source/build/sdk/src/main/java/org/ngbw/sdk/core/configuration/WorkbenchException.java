/**
 *
 */
package org.ngbw.sdk.core.configuration;

/**
 * @author Roland H. Niedner <br />
 *
 */
public class WorkbenchException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5625967969080948220L;

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