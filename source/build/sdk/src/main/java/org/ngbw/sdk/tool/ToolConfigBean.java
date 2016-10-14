/*
 * ToolConfigBean.java
 */
package org.ngbw.sdk.tool;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.api.tool.ToolIOMode;
import org.ngbw.sdk.core.shared.SourceDocumentType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class ToolConfigBean implements ToolConfig {

	private String toolId;
	private String name;
	private boolean isActive = true;
	private URL configFileURL;
	private String toolResourceId;
	private String parameterValidator;
	private String commandRenderer;
	private Map<String, ToolIOMode> inputParameters = new HashMap<String, ToolIOMode>();
	private Map<String, ToolIOMode> outputParameters = new HashMap<String, ToolIOMode>();
	private HashMap<String, SourceDocumentType> sourceDocumentTypes = new HashMap<String, SourceDocumentType>();


	public ToolConfigBean(String toolId)
	{
		if (toolId == null)
			throw new NullPointerException("toolId must not be null!");

		setToolId(toolId);
	}

	public void setActive(boolean b)
	{
		isActive = b;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public String getToolId()
	{
		return toolId;
	}

	public void setToolId(String toolId)
	{
		if (toolId == null)
			throw new NullPointerException("toolId must not be null!");

		this.toolId = toolId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		if (name == null)
			throw new NullPointerException("name must not be null!");

		this.name = name;
	}

	public ToolIOMode getInputMode(String parameter)
	{
		if (parameter == null)
			throw new NullPointerException("parameter must not be null!");

		return inputParameters.get(parameter);
	}

	public ToolIOMode getOutputMode(String parameter)
	{
		if (parameter == null)
			throw new NullPointerException("parameter must not be null!");

		return outputParameters.get(parameter);
	}

	public SourceDocumentType getSourceDocumentType(String parameter)
	{
		if (parameter == null)
			throw new NullPointerException("parameter must not be null!");

		return sourceDocumentTypes.get(parameter);
	}

	public URL getConfigFileURL()
	{
		return configFileURL;
	}

	public void setConfigFileURL(URL configFileName)
	{
		if (configFileName == null)
			throw new NullPointerException("configFileName must not be null!");

		this.configFileURL = configFileName;
	}

	public String getParameterValidatorClassName()
	{
		return parameterValidator;
	}

	public String getCommandRendererClassName()
	{
		return commandRenderer;
	}

	public void setCommandRendererClassName(String commandLineRenderer)
	{
		if (commandLineRenderer == null)
			throw new NullPointerException("commandLineRenderer must not be null!");

		this.commandRenderer = commandLineRenderer;
	}

	public Map<String, ToolIOMode> getInputParameters()
	{
		return inputParameters;
	}

	public void setInputParameters(Map<String, ToolIOMode> inputParameters)
	{
		if (inputParameters == null)
			throw new NullPointerException("inputParameters must not be null!");

		this.inputParameters = inputParameters;
	}

	public Map<String, ToolIOMode> getOutputParameters()
	{
		return outputParameters;
	}

	public void setOutputParameters(Map<String, ToolIOMode> outputParameters)
	{
		if (outputParameters == null)
			throw new NullPointerException("outputParameters must not be null!");

		this.outputParameters = outputParameters;
	}

	public String getToolResourceId()
	{
		return toolResourceId;
	}

	public void setToolResourceId(String toolResourceId)
	{
		this.toolResourceId = toolResourceId;
	}
	
	public void setSourceDocumentType(String parameter, SourceDocumentType type)
	{
		if (parameter == null)
			throw new NullPointerException("parameter must not be null!");

		if (type == null)
			throw new NullPointerException("type must not be null!");

		sourceDocumentTypes.put(parameter, type);
	}
}
