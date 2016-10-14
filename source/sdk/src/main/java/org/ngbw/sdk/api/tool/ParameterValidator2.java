/*
 * ParameterValidator2.java
 */
package org.ngbw.sdk.api.tool;


import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ParameterValidator2 
{
	public Map<String, List<String>> addDefaultParameters(Map<String, List<String>> parameters);
	public Map<String, String> preProcessParameters(Map<String, List<String>> parameters);
	public void validateParameters(Map<String, String> parameters);
	public void validateInput(Set<String> input);
	public List<FieldError> getErrors();
}
