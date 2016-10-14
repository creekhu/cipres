/*
 * ServiceFactory.java
 */
package org.ngbw.sdk.core.configuration;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.ConversionRegistryBuilder;
import org.ngbw.sdk.api.conversion.ConversionService;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.CoreRegistryBuilder;
import org.ngbw.sdk.api.data.DatasetRegistry;
import org.ngbw.sdk.api.data.DatasetRegistryBuilder;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.api.tool.ToolRegistry;
import org.ngbw.sdk.api.tool.ToolRegistryBuilder;
import org.ngbw.sdk.common.util.Resource;


/**
 * 
 * @author Roland H. Niedner
 * 
 */
public class ServiceFactory {

	private static Log log = LogFactory.getLog(ServiceFactory.class);

	private Configuration configuration;
	private CoreRegistry coreRegistry;
	private DatasetRegistry datasetRegistry;
	private ToolRegistry toolRegistry;
	private ConversionService conversionService;
	private DatasetService datasetService;
	private ConversionRegistry conversionRegistry;

	ServiceFactory(Configuration configuration) {
		if (log.isInfoEnabled())
			log.info("Building ServiceFactory");
		this.configuration = configuration;
		// initialize all singletons right here
		this.getCoreRegistry();
		this.getDatasetService();
		this.getToolRegistry();
		this.getConversionService();
		if (log.isInfoEnabled())
			log.info("ServiceFactory ready");
	}

	public CoreRegistry getCoreRegistry() {
		if (coreRegistry == null) {
			try {
				CoreRegistryBuilder crb = configuration
						.getCoreRegistryBuilder().newInstance();
				Resource resource = configuration.getCoreRegistryConfig();
				coreRegistry = crb.buildRegistry(resource);
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Cannot instantiate the CoreRegistryBuilder!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Illegal access to the no argumant constructor for the CoreRegistryBuilder",
						e);
			}
		}
		return coreRegistry;
	}

	protected DatasetRegistry getDatasetRegistry() {
		if (datasetRegistry == null) {
			try {
				DatasetRegistryBuilder drb = configuration
						.getDatasetRegistryBuilder().newInstance();
				Resource resource = configuration.getDatasetRegistryConfig();
				datasetRegistry = drb
						.buildRegistry(resource, getCoreRegistry());
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Cannot instantiate the DatasetRegistryBuilder!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Illegal access to the no argumant constructor for the DatasetRegistryBuilder",
						e);
			}
		}
		return datasetRegistry;
	}

	public DatasetService getDatasetService() {
		if (datasetService == null) {
			try {
				datasetService = getDatasetRegistry().getDatasetService();
			} catch (Exception e) {
				throw new WorkbenchException(e);
			}
		}
		return datasetService;
	}

	public ToolRegistry getToolRegistry() {
		if (toolRegistry == null) {
			try {
				ToolRegistryBuilder trb = configuration
						.getToolRegistryBuilder().newInstance();
				Resource resource = configuration.getToolRegistryConfig();
				toolRegistry = trb.buildRegistry(resource, configuration.getExtraTools(), getCoreRegistry());
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Cannot instantiate the ToolRegistryBuilder!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Illegal access to the no argumant constructor for the ToolRegistryBuilder",
						e);
			}
		}
		return toolRegistry;
	}

	protected ConversionRegistry getConversionRegistry() {
		if (conversionRegistry == null) {
			try {
				ConversionRegistryBuilder trb = configuration
						.getConversionRegistryBuilder().newInstance();
				Resource resource = configuration.getConversionRegistryConfig();
				conversionRegistry = trb.buildRegistry(resource,
						getCoreRegistry());
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Cannot instantiate the ConversionRegistryBuilder!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Illegal access to the no argumant constructor for the ConversionRegistryBuilder",
						e);
			}
		}
		return conversionRegistry;
	}

	public ConversionService getConversionService() {
		if (conversionService == null) {
			try {
				conversionService = getConversionRegistry()
						.getConversionService();
			} catch (Exception e) {
				throw new WorkbenchException(e);
			}
		}
		return conversionService;
	}
}
