/*
 * DatasetConfig.java
 */
package org.ngbw.sdk.api.data;


import java.util.Map;

import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.Dataset;


/**
 * A DatasetConfig bundles the Dataset-specific logical annotations and
 * physical DataResource configuration parameters.
 *
 * @author Roland H. Niedner
 * 
 */
public interface DatasetConfig {

	/**
	 * Method returns the id of the DataResource that hosts this Dataset.
	 * 
	 * @return dataResourceId
	 */
	public String getDataResourceId();
	
	/**
	 * Set id of the DataResource that hosts this Dataset.
	 * 
	 * @param dataResourceId
	 */
	public void setDataResourceId(String dataResourceId);
	
	/**
	 * Retrieval of DataResource configuration specific to
	 * the DataResource and the Dataset.
	 * 
	 * @return cfg map (String key, String value)
	 */
	public Map<String, String> getDataResourceConfig();

	/**
	 * Setting of DataResource configuration specific to
	 * the DataResource and the Dataset.
	 * 
	 * @param dataResourceConfig
	 */
	public void setDataResourceConfig(Map<String, String> dataResourceConfig);

	/**
	 * Method returns the centrally registerd designation for
	 * the Dataset on which this information is about.
	 * 
	 * @return Dataset
	 */
	public Dataset getDataset();
	
	/**
	 * Set the centrally registered designation for
	 * the Dataset on which this information is about.
	 * 
	 * @param dataset
	 */
	public void setDataset(Dataset dataset);

	/**
	 * Method returns the descriptive name for
	 * the Dataset on which this information is about.
	 * 
	 * @return name
	 */
	public String getName();
	
	/**
	 * Set the descriptive name for
	 * the Dataset on which this information is about.
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Method returns the SourceDocumentType for the 
	 * records of this Dataset.
	 * 
	 * @return sourceDocumentType
	 */
	public SourceDocumentType getSourceDocumentType();
	
	/**
	 * Method sets the SourceDocumentType for the 
	 * records of this Dataset.
	 * 
	 * @param sourceDocumentType
	 */
	public void setSourceDocumentType(SourceDocumentType sourceDocumentType);
}
