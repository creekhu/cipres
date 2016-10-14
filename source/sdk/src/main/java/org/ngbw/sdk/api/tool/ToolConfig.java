/*
 * ToolConfig.java
 */
package org.ngbw.sdk.api.tool;


import java.net.URL;
import java.util.Map;

import org.ngbw.sdk.core.shared.SourceDocumentType;


/**
 * The ToolConfig implementation hold basic tool execution parameters and
 * is managed by the ToolService.
 *
 * @author Roland H. Niedner
 * 
 */
public interface ToolConfig {

	/**
	 * Return the identifier of the tool that this configuration is about.
	 * 
	 * @return the identifier of the tool
	 */
	public String getToolId();

	/**
	 * Set the identifier of the tool that this configuration is about.
	 * 
	 * @param toolId
	 */
	public void setToolId(String toolId);


	public void setActive(boolean b);
	public boolean isActive();
	
	/**
	 * Return the descriptive name for the Tool 
	 * that this configuration is about.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Set the descriptive name for the Tool 
	 * that this configuration is about.
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @return toolResourceId
	 */
	public String getToolResourceId();

	/**
	 * Returns the Id of the ToolResource that provides that tools.
	 * 
	 * @param toolResourceId
	 */
	public void setToolResourceId(String toolResourceId);
	
	/**
	 * Method returns the SourceDocumentType for the submitted
	 * parameter.
	 * 
	 * @param parameter
	 * @return sourceDocumentType
	 */
	public SourceDocumentType getSourceDocumentType(String parameter);

	/**
	 * Returns the ToolIOMode for this parameter (STDIN | FILE)
	 * 
	 * @param parameter
	 * @return ToolIOMode
	 */
	public ToolIOMode getInputMode(String parameter);

	/**
	 * Returns the ToolIOMode for this parameter (STDOUT | STDERR | FILE)
	 * 
	 * @param parameter
	 * @return ToolIOMode
	 */
	public ToolIOMode getOutputMode(String parameter);

	/**
	 * Returns the configuration file url for this tool (pise file for example).
	 * 
	 * @return cfgFile url
	 */
	public URL getConfigFileURL();

	/**
	 * Set the configuration file url for this tool (pise file for example).
	 * 
	 * @param configFileUrl
	 */
	public void setConfigFileURL(URL configFileUrl);

	/**
	 * Returns the fully qualified class name of the registered CommandRenderer.
	 * 
	 * @return commandRenderer
	 */
	public String getCommandRendererClassName();

	/**
	 * Set the fully qualified class name of the registered CommandRenderer.
	 * 
	 * @param commandLineRenderer
	 */
	public void setCommandRendererClassName(String commandLineRenderer);

	/**
	 * Return the map that keys the ToolIOMode to each input parameter name.
	 * 
	 * @return parameter map
	 */
	public Map<String, ToolIOMode> getInputParameters();

	/**
	 * Set the map that keys the ToolIOMode to each input parameter name.
	 * 
	 * @param inputParameters - map
	 */
	public void setInputParameters(Map<String, ToolIOMode> inputParameters);

	/**
	 * Return the map that keys the ToolIOMode to each output parameter name.
	 * 
	 * @return parameter map
	 */
	public Map<String, ToolIOMode> getOutputParameters();
	
	/**
	 * Set the map that keys the ToolIOMode to each output parameter name.
	 * 
	 * @param outputParameters
	 */
	public void setOutputParameters(Map<String, ToolIOMode> outputParameters);
	
	/**
	 * Set the SourceDocumentType for an Input or output parameter.
	 * 
	 * @param parameter
	 * @param type
	 */
	public void setSourceDocumentType(String parameter, SourceDocumentType type);

}
