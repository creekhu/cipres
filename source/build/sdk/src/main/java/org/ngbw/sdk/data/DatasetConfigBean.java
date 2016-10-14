/*
 * DatasetConfigBean.java
 */
package org.ngbw.sdk.data;


import java.util.HashMap;
import java.util.Map;

import org.ngbw.sdk.api.data.DatasetConfig;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.shared.SourceDocumentType;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class DatasetConfigBean implements DatasetConfig {
	
	private Dataset dataset;
	private String name;
	private SourceDocumentType sourceDocumentType;
	private String dataResourceId;
	private Map<String, String> dataResourceConfig = new HashMap<String, String>();

	public DatasetConfigBean() {
		super();
	}

	public DatasetConfigBean(Dataset dataset) {
		this();
		this.setDataset(dataset);
	}

	public Map<String, String> getDataResourceConfig() {
		return dataResourceConfig;
	}

	public void setDataResourceConfig(Map<String, String> dataResourceConfig) {
		this.dataResourceConfig = dataResourceConfig;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataResourceId() {
		return dataResourceId;
	}

	public void setDataResourceId(String dataResourceId) {
		this.dataResourceId = dataResourceId;
	}

	public SourceDocumentType getSourceDocumentType() {
		return sourceDocumentType;
	}

	public void setSourceDocumentType(SourceDocumentType sourceDocumentType) {
		this.sourceDocumentType = sourceDocumentType;
	}
}
