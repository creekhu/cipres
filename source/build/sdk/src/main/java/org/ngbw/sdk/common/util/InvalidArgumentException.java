package org.ngbw.sdk.common.util;

/**
 * Created by IntelliJ IDEA.
 * User: hannes
 * Date: Jan 30, 2007
 * Time: 9:17:46 PM
 * <p/>
 * This application exception is used to report a problem with a method
 * or constructor argument.
 */
public class InvalidArgumentException extends Exception {


    /**
	 * 
	 */
	private static final long serialVersionUID = -450123836445445752L;

	/**
     * Constructor for the InvalidArgumentException object
     */
    public InvalidArgumentException() {
        super();
    }


    /**
     * Constructor for the InvalidArgumentException object
     *
     * @param message
     */
    public InvalidArgumentException(String message) {
        super(message);
    }


    /**
     * Constructor for the InvalidArgumentException object
     *
     * @param e
     */
    public InvalidArgumentException(Throwable e) {
        super(e);
    }

    /**
     * Constructor for the InvalidArgumentException object
     *
     * @param message
     * @param e
     */
    public InvalidArgumentException(String message, Throwable e) {
        super(message, e);
    }
}

