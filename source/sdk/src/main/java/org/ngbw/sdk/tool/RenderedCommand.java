/*
 * RenderedCommand.java
 */
package org.ngbw.sdk.tool;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.ngbw.sdk.common.util.StringUtils;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class RenderedCommand 
{
	
	//maps the input parameter name to the input file name
	private Map<String, String> inputFileMap = new HashMap<String, String>();

	//maps the output parameter name to the output file name
	private Map<String, String> outputFileMap = new HashMap<String, String>();

	//holds the rendered command
	private String[] command = null;
	private Properties schedulerProperties = new Properties();

	public Properties getSchedulerProperties() { return schedulerProperties; }

	public RenderedCommand() 
	{
		super();
	}
	
	public RenderedCommand(String[] command, Map<String, String> inputFileMap, Map<String, String> outputFileMap) 
	{
		this.command = command;
		this.inputFileMap = inputFileMap;
		this.outputFileMap = outputFileMap;
	}
	
	/**
	 * Returns the filenames of all input parameters that need to be staged keyed
	 * to their respective parameter. The keys in this map will correspond to the list elements
	 * of the inputParameters parameter above. There might be extra keys for parameter files produced
	 * within the command rendering process.
	 * 
	 * @return inputFileMap
	 */
	public Map<String, String> getInputFileMap() 
	{
		return inputFileMap;
	}

	/**
	 * Method sets the filenames of all input parameters that need to be staged keyed
	 * to their respective parameter.
	 * 
	 * @param inputFileMap
	 */
	public void setInputFileMap(Map<String, String> inputFileMap) 
	{
		this.inputFileMap = inputFileMap;
	}

	/**
	 * Returns the filenames of all produced output files keyed
	 * to a label that identifies the file content with respect to the task.
	 * 
	 * @return outputFileMap
	 */
	public Map<String, String> getOutputFileMap() 
	{
		return outputFileMap;
	}

	/**
	 * Method sets the filenames of all produced output files keyed
	 * to a label that identifies the file content with respect to the task.
	 * 
	 * @param outputFileMap
	 */
	public void setOutputFileMap(Map<String, String> outputFileMap) 
	{
		this.outputFileMap = outputFileMap;
	}

	/**
	 * Method returns the rendered command and arguments as a String Array.
	 * 
	 * @return command
	 */
	public String[] getCommand() 
	{
		return command;
	}

	public String getCommandAsString() 
	{
		return StringUtils.join(getCommand(), " "); 
	}

	/**
	 * Method sets the rendered command and arguments as a String Array.
	 * 
	 * @param command
	 */
	public void setCommand(String[] command) 
	{
		this.command = command;
	}
}
