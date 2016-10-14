package org.ngbw.sdk.tool;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.api.tool.ParameterValidator2;
import org.ngbw.sdk.common.util.MapUtil;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.common.util.MapUtil;
import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskValidator
{
	private static final Log log = LogFactory.getLog(TaskValidator.class);
	public static final String toolField = "tool";
	private static final String validatorPackage = "org.ngbw.sdk.tool.validation.";
	List<FieldError> errors;

	private ParameterValidator2 validator = null;
	private String tool;

	public TaskValidator(String tool) throws Exception
	{
		Set<String> toolIds = Workbench.getInstance().getActiveToolIds();
		if (toolIds.contains(tool))
		{
			this.tool  = tool;
			this.validator = getParameterValidator(tool);
		} else
		{
			errors = new ArrayList<FieldError>();
			errors.add(new FieldError(toolField, "invalid tool"));
		}
	}


	/*
		Returns modified toolParamaters, by converting any List values to String values. 

		If tool class was instantiated with was invalid, this.validator will be null and this will return null. 
			getErrors() will return an error about the invalid tool.
		
		Uses toolField as param name for toolId.  

		Caller must call getErrors() after validate() to see if there were any errors.   Don't submit
		the job if there are.
	*/
	public Map<String, String> validate( Map<String, List<String>> toolParameters, Set<String> inputDataItems)
		throws Exception
	{
		Map<String, String> params = null;

		if (validator == null)
		{
			return params;
		}

		// converts List<String> values to simple Strings, concatenating List elements if necessary.
		params = validator.preProcessParameters(toolParameters);

		// Check that required params are present and checks datatype of non-string params
		validator.validateParameters(params);

		// Check whether input file parameters are present.
		validator.validateInput(inputDataItems);

		return params;

	}

	public List<FieldError> getErrors()
	{
		if  (validator != null)
		{
			return validator.getErrors();
		} else
		{
			return this.errors;
		}
	}

 	private  ParameterValidator2 getParameterValidator(String tool) 
		throws Exception
	{
 		String className = validatorPackage + tool.toLowerCase() + "Validator";
		try
		{
			return (ParameterValidator2)Class.forName(className).newInstance();
		}
		catch(Exception e)
		{
			log.error(e.toString());
			throw new Exception("Internal error instantiating " + className);
		}
    }

	// todo need to call this in preprocess and it needs to operate on Map<String, List<String>>
	/*
	private Map<String, String> cleanParameters(Map<String, String> toolParameters) 
	{
		Map<String, String> parameters = new HashMap<String, String>();
		String value;
		for (String parameter : toolParameters.keySet()) 
		{
			parameter = parameter.trim();
			value = toolParameters.get(parameter);
			if (value == null)
			{
				continue;
			}
			value = value.trim();
			if (parameter.length() == 0 || value.length() == 0)
			{
				continue;
			}
			parameters.put(parameter, value);
		}	
		return parameters;
    }
	*/

}
