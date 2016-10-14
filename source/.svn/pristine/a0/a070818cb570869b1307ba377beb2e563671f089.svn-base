/*
 * DefaultToolRegistryBuilder.java
 */
package org.ngbw.sdk.tool;


import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.api.tool.ToolIOMode;
import org.ngbw.sdk.api.tool.ToolRegistry;
import org.ngbw.sdk.api.tool.ToolRegistryBuilder;
import org.ngbw.sdk.api.tool.ToolResource;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.XMLHelper;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DefaultToolRegistryBuilder implements ToolRegistryBuilder {

	private static Log log = LogFactory.getLog(DefaultToolRegistryBuilder.class);
	private ToolRegistry toolRegistry;

	/**
		First argument Resource cfg, is just has name of the tool-registry.cfg.xml and methods
		to find it and get it's contents.

		coreRegistry has info about datatypes.
	*/
	public ToolRegistry buildRegistry(Resource cfg, String extraTools, CoreRegistry coreRegistry) {
		toolRegistry = new DefaultToolRegistry(coreRegistry);

		// This just calls a java routine that parses the xml file. 
		Document document = XMLHelper.parseXML(cfg.getInputStream());

		Element toolRegistryNode = XMLHelper.findNode(document, "ToolRegistry");
		handleToolRegistry(toolRegistryNode);
		log.debug("extraTools is " + extraTools);
		if (extraTools != null)
		{
			handleExtraTools(extraTools, toolRegistry);
		}
		return toolRegistry;
	}

	public void handleExtraTools(String extraTools, ToolRegistry toolRegistry)
	{
		String groupId = toolRegistry.getDefaultGroup();
		try
		{
			Resource[] toolResources = Resource.getMatchingResources(extraTools);
			log.debug("Found " + toolResources.length + " extra tools.");
			for (Resource resource : toolResources)
			{
				Document document = XMLHelper.parseXML(resource.getInputStream());
				Element toolNode = XMLHelper.findNode(document, "Tool");
				String toolId = toolNode.getAttribute("id");
				toolRegistry.assignToGroup(toolId, groupId);
				log.debug("Assign Tool: " + toolId + " to group: " + groupId);
				handleTool(toolNode);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	// private methods //
	
	private void handleToolRegistry(Element toolRegistryNode) {
		//process ToolResource Nodes
		NodeList toolResourceNodes = toolRegistryNode.getElementsByTagName("ToolResource");
		if(log.isDebugEnabled()) log.debug("Processing " + toolResourceNodes.getLength() + " ToolResources");
		if(toolResourceNodes == null) return;
		for(int i=0; i<toolResourceNodes.getLength(); i++) {
			Element element = (Element) toolResourceNodes.item(i);
			String toolResourceId = element.getAttribute("id");
			if(log.isDebugEnabled()) log.debug("Dispatching ToolResource " + toolResourceId);
			handleToolResource(element);
		}
		
		//process ToolGroup Nodes
		NodeList toolGroupNodes = toolRegistryNode.getElementsByTagName("ToolGroup");
		//if(log.isDebugEnabled()) log.debug("Processing " + toolGroupNodes.getLength() + " ToolGroups");
		if(toolGroupNodes == null) return;
		for(int i=0; i<toolGroupNodes.getLength(); i++) {
			Element toolGroupNode = (Element) toolGroupNodes.item(i);
			String toolGroupId = toolGroupNode.getAttribute("id");
			String isDefault = toolGroupNode.getAttribute("isdefault");
			String disable = toolGroupNode.getAttribute("disable");
			boolean isDisabled = (disable != null && disable.equals("true"));
			if (isDefault != null && isDefault.equals("1"))
			{
				toolRegistry.setDefaultGroup(toolGroupId);
			}
			//if(log.isDebugEnabled()) log.debug("Dispatching ToolGroup " + toolGroupId);
			if (!isDisabled)
			{
				handleToolGroup(toolGroupNode);
			}
		}
	}
		
	private void handleToolResource(Element element) {
		if (element == null)
			throw new NullPointerException("Element cannot be null!");
		if(log.isDebugEnabled()) log.debug("Handling node " + element.getNodeName());
		String toolResourceId = element.getAttribute("id");
		String type = element.getAttribute("type");
		String fileHandlerClassName = element.getAttribute("filehandler");
		String processWorkerClassName = element.getAttribute("processworker");
		String toolResourceClassName = element.getAttribute("class");
		NodeList paramNodes = element.getElementsByTagName("Parameter");
		Map<String, String> parameters = new HashMap<String, String>();
		for(int i=0; i<paramNodes.getLength(); i++) {
			Element paramNode = (Element) paramNodes.item(i);
			String key = paramNode.getAttribute("key");
			String value = paramNode.getAttribute("value");
			parameters.put(key, value);
			if(log.isDebugEnabled()) log.debug("Set parameter " + key + " = " + value);
		}
		ToolResource toolResource = configureToolResource(toolResourceId, toolResourceClassName, parameters);
		toolResource.setType(type);
		setFileHandlerClass(toolResource, fileHandlerClassName);
		setProcessWorkerClass(toolResource, processWorkerClassName);
		toolRegistry.registerToolResource(toolResourceId, toolResource);
		if(log.isDebugEnabled()) log.debug("registerToolResource " + toolResourceId + " = " + toolResource);
	}

	@SuppressWarnings("unchecked")
	private ToolResource configureToolResource(String toolResourceId, String className,
			Map<String, String> parameters) {
		Class<ToolResource> drClass;
		try {
			drClass = (Class<ToolResource>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class " + className + " not found.", e);
		}
		ToolResource tr;
		try {
			tr = drClass.getConstructor(String.class).newInstance(toolResourceId);
		} catch (InstantiationException e) {
			throw new RuntimeException("Can't instantiate class " + className, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Illegal access trying to instantiate class " + className, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Constructor argument is not a string " + className, e);
		} catch (SecurityException e) {
			throw new RuntimeException("Constructor with a string argument is not visible for " + className, e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Can't invoke constructor with string argument for " + className, e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No constructor with a string argument " + className, e);
		}
		if (tr.isConfigured() == false) {
			if (parameters != null) {
				tr.configure(parameters);
			} else {
				throw new RuntimeException("Missing ToolResource configuration for ToolResource: " + className);
			}
			if (tr.isConfigured() == false)
				throw new RuntimeException("ToolResource configuration: " + className + " failed!");
		}
		return tr;
	}
	
	private void handleToolGroup(Element element) {
		NodeList toolNodes = element.getElementsByTagName("Tool");
		//if(log.isDebugEnabled()) log.debug("Processing " + toolNodes.getLength() + " Tools");
		String groupId = element.getAttribute("id");
		if(toolNodes == null) return;
		for(int i=0; i<toolNodes.getLength(); i++) {
			Element toolNode = (Element) toolNodes.item(i);
			String toolId = toolNode.getAttribute("id");

			// Puts into a map in DefaultToolRegistry (toolId to group map)
			toolRegistry.assignToGroup(toolId, groupId);

			//if(log.isDebugEnabled()) log.debug("Assign Tool: " + toolId + " to group: " + groupId);
			//if(log.isDebugEnabled()) log.debug("Dispatching Tool " + toolId);
			handleTool(toolNode);
		}
	}

	private void handleTool(Element toolNode) {
		if (toolNode == null)
			throw new NullPointerException("Element cannot be null!");
		//if(log.isDebugEnabled()) log.debug("Handling node " + toolNode.getNodeName());
		String toolId = toolNode.getAttribute("id");
		String inactive = toolNode.getAttribute("inactive");
		String toolName = toolNode.getAttribute("name");
		String toolResourceId = toolNode.getAttribute("toolresource");
		String configfile = toolNode.getAttribute("configfile");
		String commandRendererClassName = toolNode.getAttribute("commandrenderer");
		NodeList inputParamNodes = toolNode.getElementsByTagName("InputParameter");
		Map<String, ToolIOMode> inputParameters = new HashMap<String, ToolIOMode>();
		//if(log.isDebugEnabled()) log.debug("Configuring tool " + toolId);

		ToolConfig toolConfig = toolRegistry.getToolConfig(toolId);

		if (toolConfig == null)
			toolConfig = new ToolConfigBean(toolId);

		toolConfig.setName(toolName);
		for(int i=0; i<inputParamNodes.getLength(); i++) {
			Element paramNode = (Element) inputParamNodes.item(i);
			if (paramNode.hasAttribute("id") == false)
				throw new NullPointerException("Node InputParameter attribute id mssing");
			String parameter = paramNode.getAttribute("id");
			if (paramNode.hasAttribute("iomode") == false)
				throw new NullPointerException("Node InputParameter attribute iomode mssing");
			ToolIOMode iomode = ToolIOMode.valueOf(paramNode.getAttribute("iomode"));
			inputParameters.put(parameter, iomode);
			
			if (paramNode.hasAttribute("entityType") == false)
				throw new NullPointerException("Node InputParameter attribute entityType mssing");
			EntityType entityType = EntityType.valueOf(paramNode.getAttribute("entityType"));
			if (paramNode.hasAttribute("dataType") == false)
				throw new NullPointerException("Node InputParameter attribute dataType mssing");
			DataType dataType = DataType.valueOf(paramNode.getAttribute("dataType"));
			if (paramNode.hasAttribute("dataFormat") == false)
				throw new NullPointerException("Node InputParameter attribute dataFormat mssing");
			DataFormat dataFormat = DataFormat.valueOf(paramNode.getAttribute("dataFormat"));
			SourceDocumentType sourceDocumentType = new SourceDocumentType(entityType, dataType, dataFormat);
			toolConfig.setSourceDocumentType(parameter, sourceDocumentType);
			//if(log.isDebugEnabled()) log.debug(toolId + " Input parameter " + parameter
					//+  " mapped to sourceDocumentType: " + sourceDocumentType);
		}
		NodeList outputParamNodes = toolNode.getElementsByTagName("OutputParameter");
		Map<String, ToolIOMode> outputParameters = new HashMap<String, ToolIOMode>();
		for(int i=0; i<outputParamNodes.getLength(); i++) {
			Element paramNode = (Element) outputParamNodes.item(i);
			String parameter = paramNode.getAttribute("id");
			ToolIOMode iomode = ToolIOMode.valueOf(paramNode.getAttribute("iomode"));
			outputParameters.put(parameter, iomode);
			
			if (paramNode.hasAttribute("entityType") == false)
				throw new NullPointerException("Node InputParameter attribute entityType missing!");
			EntityType entityType = EntityType.valueOf(paramNode.getAttribute("entityType"));
			if (paramNode.hasAttribute("dataType") == false)
				throw new NullPointerException("Node InputParameter attribute dataType mssing");
			DataType dataType = DataType.valueOf(paramNode.getAttribute("dataType"));
			if (paramNode.hasAttribute("dataFormat") == false)
				throw new NullPointerException("Node InputParameter attribute dataFormat mssing");
			DataFormat dataFormat = DataFormat.valueOf(paramNode.getAttribute("dataFormat"));
			SourceDocumentType sourceDocumentType = new SourceDocumentType(entityType, dataType, dataFormat);
			toolConfig.setSourceDocumentType(parameter, sourceDocumentType);
			if(log.isDebugEnabled()) log.debug(toolId + " Output parameter" + parameter
					+  " sourceDocumentType: " + sourceDocumentType);
		}
		//if(log.isDebugEnabled()) log.debug(toolId + " Tool Name " + toolName);
		toolConfig.setToolResourceId(toolResourceId);
		//if(log.isDebugEnabled()) log.debug(toolId + " toolResourceId " + toolResourceId);
		toolConfig.setInputParameters(inputParameters);
		//if(log.isDebugEnabled()) log.debug(toolId + " inputParameters " + inputParameters.size());
		toolConfig.setOutputParameters(outputParameters);
		//if(log.isDebugEnabled()) log.debug(toolId + " outputParameters " + outputParameters.size());
		//if(log.isDebugEnabled()) log.debug(toolId + " configfile " + configfile);
		URL configFileUrl = DefaultToolRegistryBuilder.class.getClassLoader().getResource(configfile);
		if (configFileUrl != null) {
			toolConfig.setConfigFileURL(configFileUrl);
			//if(log.isDebugEnabled()) log.debug(toolId + " configFileUrl " + configFileUrl);
		}
		if (commandRendererClassName != null) {
			toolConfig.setCommandRendererClassName(commandRendererClassName);
			//if(log.isDebugEnabled()) log.debug(toolId + " commandRendererClassName " + commandRendererClassName);
		}
		toolConfig.setActive(!Boolean.parseBoolean(inactive));
		toolRegistry.registerTool(toolConfig);
	}

	@SuppressWarnings("unchecked")
	private void setProcessWorkerClass(ToolResource tr, String className) {
		try {
			if (className.trim().length() == 0)
			{
				tr.setProcessWorkerName(null);
			} else
			{
				Class<BaseProcessWorker> processWorkerClass = (Class<BaseProcessWorker>) Class.forName(className);

				tr.setProcessWorkerName(processWorkerClass.getName());
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException("Problem finding ProcessWorkerClass:'"  + className + "'", e);
		}
		catch (NoClassDefFoundError e)
		{
			throw new RuntimeException("Problem finding ProcessWorkerClass:'"  + className + "'", e);
		}

		if(log.isDebugEnabled()) log.debug("ProcessWorker set to " + className);
	}
	
	@SuppressWarnings("unchecked")
	private void setFileHandlerClass(ToolResource tr, String className) {
		try {
			if (className.trim().length() == 0)
			{
				tr.setFileHandlerClass(null);
			} else
			{
				Class<FileHandler> fileHandlerClass = (Class<FileHandler>) Class.forName(className);
				tr.setFileHandlerClass(fileHandlerClass);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException("Problem finding FileHandlerClass:'"  + className + "'", e);
		}
		catch (NoClassDefFoundError e)
		{
			throw new RuntimeException("Problem finding ProcessWorkerClass:'"  + className + "'", e);
		}
		if(log.isDebugEnabled()) log.debug("FileHandler set to " + className);
	}
}
