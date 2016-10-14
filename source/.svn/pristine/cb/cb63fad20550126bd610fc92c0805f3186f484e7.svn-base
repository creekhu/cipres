package org.ngbw.sdk.tool;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.tool.CommandRenderer;
import org.ngbw.sdk.api.tool.FileHandler;
import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.api.tool.ToolRegistry;
import org.ngbw.sdk.api.tool.ToolResource;
import org.ngbw.sdk.api.tool.ToolResourceType;
import org.ngbw.sdk.database.TaskInputSourceDocument;


public class DefaultToolRegistry implements ToolRegistry {

	private final CoreRegistry coreRegistry;
	private Map<String, ToolConfig> toolConfigMap = new HashMap<String, ToolConfig>();
	private Map<String, ToolConfig> activeToolConfigMap = new HashMap<String, ToolConfig>();
	private HashMap<String, ToolResource> toolResourceMap = new HashMap<String, ToolResource>(10);
	private HashMap<String, String> toolGroupMap = new HashMap<String, String>();
	private String defaultGroup = "default";

	public String getDefaultGroup() { return defaultGroup; }
	public void setDefaultGroup(String group) { this.defaultGroup = group; }


	public DefaultToolRegistry(CoreRegistry coreRegistry)
	{
		this.coreRegistry = coreRegistry;
	}

	public CoreRegistry getCoreRegistry()
	{
		return coreRegistry;
	}

	public Set<String> getToolIds()
	{
		return toolConfigMap.keySet();
	}

	public Set<String> getActiveToolIds()
	{
		return activeToolConfigMap.keySet();
	}

	public Set<String> getResourcesOfType(ToolResourceType type)
	{
		HashSet<String> resources = new HashSet<String>();
		for (String id : toolResourceMap.keySet())
		{
			ToolResource tr = toolResourceMap.get(id);
			if (tr.getType() == type)
			{
				resources.add(tr.getType().toString());
			}
		}
		return resources;
	}


	public ToolConfig getToolConfig(String toolId)
	{
		return toolConfigMap.get(toolId);
	}

	public ToolResource getToolResource(String toolResourceId)
	{
		return toolResourceMap.get(toolResourceId);
	}

	public void registerTool(ToolConfig toolConfig)
	{
		if(toolConfig == null)
			throw new NullPointerException("ToolConfig cannot be null!");

		String toolResourceId = toolConfig.getToolResourceId();

		if(toolResourceId == null)
			throw new NullPointerException("ToolResourceId in the ToolConfig cannot be null!");

		if(toolResourceMap.containsKey(toolResourceId) == false)
			throw new NullPointerException("You must register a ToolResource with the Id " + toolResourceId + " first!");

		toolConfigMap.put(toolConfig.getToolId(), toolConfig);

		if (toolConfig.isActive())
		{
			activeToolConfigMap.put(toolConfig.getToolId(), toolConfig);
		}
	}

	public void registerToolResource(String toolResourceId, ToolResource toolResource)
	{
		toolResourceMap.put(toolResourceId, toolResource);
	}

	public String getToolGroup(String toolId)
	{
		return toolGroupMap.get(toolId);
	}

	public void assignToGroup(String toolId, String toolGroup)
	{
		toolGroupMap.put(toolId, toolGroup);
	}

	// This is a way to override or set the toolResource for a whole group of tools
	// It isn't called from within the sdk or the webapp.
	public void groupSet(String toolGroup, ToolResource toolResource)
	{
		for(String toolId : toolGroupMap.keySet()) {
			String thisGroup = toolGroupMap.get(toolId);

			if (toolGroup.equals(thisGroup)) {
				ToolConfig cfg = toolConfigMap.get(toolId);

				cfg.setToolResourceId(toolResource.getId());
			}
		}
	}

	// This is a way to override or set the commandRenderer for a whole group of tools
	// It isn't called from within the sdk or the web app.
	public void groupSet(String toolGroup, Class<CommandRenderer> commandRenderer)
	{
		for(String toolId : toolGroupMap.keySet()) {
			String thisGroup = toolGroupMap.get(toolId);

			if (toolGroup.equals(thisGroup)) {
				ToolConfig cfg = toolConfigMap.get(toolId);

				cfg.setCommandRendererClassName(commandRenderer.getCanonicalName());
			}
		}
	}

}

