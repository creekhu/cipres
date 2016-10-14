/*
 * Configuration.java
 */
package org.ngbw.sdk.core.configuration;


import org.ngbw.sdk.api.conversion.ConversionRegistryBuilder;
import org.ngbw.sdk.api.core.CoreRegistryBuilder;
import org.ngbw.sdk.api.data.DatasetRegistryBuilder;
import org.ngbw.sdk.api.tool.ToolRegistryBuilder;
import org.ngbw.sdk.common.util.Resource;
import org.ngbw.sdk.common.util.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The Configuration is the top level access that will configure the
 * sdk ServiceFactory. The configuration provides an argumentless
 * constructor that will search the classpath for a file called
 * <tt>ngbw.cfg.xml<tt>. 
 * 
 * @author Roland H. Niedner
 * 
 */
public class Configuration {

	private static Log log = LogFactory.getLog(Configuration.class);

	private Class<CoreRegistryBuilder> coreRegistryBuilder;
	private Resource coreRegistryConfig;
	private Class<DatasetRegistryBuilder> datasetRegistryBuilder;
	private Resource datasetRegistryConfig;
	private Class<ToolRegistryBuilder> toolRegistryBuilder;
	private Resource toolRegistryConfig;
	private String extraTools;
	private Class<ConversionRegistryBuilder> conversionRegistryBuilder;
	private Resource conversionRegistryConfig;

	public Configuration() {}

	public Configuration configure() {
		Resource cfg = null;
		try {
			cfg = Resource.getResource("ngbw.cfg.xml");
		} catch (ResourceNotFoundException e) {
			try {
				cfg = Resource.getResource("core/ngbw.cfg.xml");
			} catch (ResourceNotFoundException e1) {
				throw new RuntimeException(e1.toString(), e1);
			}
		}

		log.debug("Loaded ngbw.cfg.xml from " + cfg.getName());
		return this.configure(cfg);
	}

	public Configuration configure(Resource cfg) {
		return ConfigurationBuilder.build(this, cfg);
	}

	public ServiceFactory buildServiceFactory() {
		return new ServiceFactory(this);
	}

	public Resource getCoreRegistryConfig() {
		return coreRegistryConfig;
	}

	public void setCoreRegistryConfig(String coreRegistryConfig) {
		try {
			this.coreRegistryConfig = Resource.getResource(coreRegistryConfig);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public Resource getDatasetRegistryConfig() {
		return datasetRegistryConfig;
	}

	public void setDatasetRegistryConfig(String datasetRegistryConfig) {
		try {
			this.datasetRegistryConfig = Resource.getResource(datasetRegistryConfig);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public Resource getToolRegistryConfig() {
		return toolRegistryConfig;
	}

	public void setToolRegistryConfig(String toolRegistryConfig) {
		try {
			this.toolRegistryConfig = Resource.getResource(toolRegistryConfig);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public String  getExtraTools() {
		log.debug("ExtraTools is " + extraTools);
		return extraTools;
	}

	public void setExtraTools(String extraTools) {
		this.extraTools = extraTools ;
	}


	public Resource getConversionRegistryConfig() {
		return conversionRegistryConfig;
	}

	public void setConversionRegistryConfig(String conversionRegistryConfig) {
		try {
			this.conversionRegistryConfig = Resource.getResource(conversionRegistryConfig);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	/**
	 * @return the conversionServiceProvider
	 */
	public Class<CoreRegistryBuilder> getCoreRegistryBuilder() {
		return coreRegistryBuilder;
	}

	/**
	 * @param coreRegistryBuilder
	 *            the coreRegistryBuilder to set
	 */
	@SuppressWarnings("unchecked")
	public void setCoreRegistryBuilder(String coreRegistryBuilder) {
		try {
			this.coreRegistryBuilder = (Class<CoreRegistryBuilder>) Class
					.forName(coreRegistryBuilder);
		} catch (ClassNotFoundException e) {
			throw new WorkbenchException("CoreRegistryBuilder class cannot be found", e);
		}
	}

	public Class<DatasetRegistryBuilder> getDatasetRegistryBuilder() {
		return datasetRegistryBuilder;
	}

	/**
	 * @param datasetRegistryBuilder
	 *            the datasetRegistryBuilder to set
	 */
	@SuppressWarnings("unchecked")
	public void setDatasetRegistryBuilder(String datasetRegistryBuilder) {
		try {
			this.datasetRegistryBuilder = (Class<DatasetRegistryBuilder>) Class
					.forName(datasetRegistryBuilder);
		} catch (ClassNotFoundException e) {
			throw new WorkbenchException("DatasetRegistryBuilder class cannot be found", e);
		}
	}

	public Class<ToolRegistryBuilder> getToolRegistryBuilder() {
		return toolRegistryBuilder;
	}

	/**
	 * @param toolRegistryBuilder
	 *            the toolRegistryBuilder to set
	 */
	@SuppressWarnings("unchecked")
	public void setToolRegistryBuilder(String toolRegistryBuilder) {
		try {
			this.toolRegistryBuilder = (Class<ToolRegistryBuilder>) Class
					.forName(toolRegistryBuilder);
		} catch (ClassNotFoundException e) {
			throw new WorkbenchException("ToolRegistryBuilder class cannot be found", e);
		}
	}

	/**
	 * @return the conversionServiceProvider
	 */
	public Class<ConversionRegistryBuilder> getConversionRegistryBuilder() {
		return conversionRegistryBuilder;
	}

	/**
	 * @param conversionRegistryBuilder
	 *            the conversionRegistryBuilder to set
	 */
	@SuppressWarnings("unchecked")
	public void setConversionRegistryBuilder(String conversionRegistryBuilder) {
		try {
			this.conversionRegistryBuilder = (Class<ConversionRegistryBuilder>) Class
					.forName(conversionRegistryBuilder);
		} catch (ClassNotFoundException e) {
			throw new WorkbenchException("ConversionRegistryBuilder class cannot be found", e);
		}
	}
}
