/*
 * ParameterValidator.java
 */
package org.ngbw.sdk.api.tool;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.database.TaskInputSourceDocument;


/**
 * The <tt>ParameterValidator</tt> specifies validation method for
 * <tt>Tool</tt> parameters. It tries to test whether all the 
 * the parameters set by the client are valid
 * 
 * @author Roland H. Niedner
 *
 */
public interface ParameterValidator {

	/**
	 * Check whether all parameter values in the map are valid.
	 * Method returns null if there are no errors, otherwise
	 * a Map of error messages keyed to their respective parameter.
	 * 
	 * @param parameters
	 * @return errorMap or null
	 */
	public Map<String, String> validateParameters(Map<String, String> parameters);
	
	/**
	 * Check whether all input values in the map are valid.
	 * Method returns null if there are no errors, otherwise
	 * a Map of error messages keyed to their respective parameter.
	 * 
	 * @param input
	 * @return errorMap or null
	 */
	public Map<String, String> validateInput(Map<String, List<TaskInputSourceDocument>> input);
	
	/**
	 * Check whether the submitted value for the submitted parameter 
	 * is valid.  Method returns null if there is no error, otherwise
	 * an error message.
	 * 
	 * @param parameter
	 * @param value
	 * @return error or null
	 */
	public String validate(String parameter, Object value);
}
