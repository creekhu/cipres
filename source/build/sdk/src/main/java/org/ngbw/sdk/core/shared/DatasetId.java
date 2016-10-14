/**
 * 
 */
package org.ngbw.sdk.core.shared;

import org.ngbw.sdk.core.types.Dataset;

/**
 * @author hannes
 *
 */
public class DatasetId {
	
	public DatasetId(Dataset dataset, Object recordId){
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (recordId == null)
			throw new NullPointerException("recordId is null!");
		this.dataset = dataset;
		this.recordId = recordId;
	}
	
	public DatasetId(String dataset, Object recordId){
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		if (recordId == null)
			throw new NullPointerException("recordId is null!");
		this.dataset = Dataset.valueOf(dataset);
		this.recordId = recordId;
	}
	
	public final Dataset dataset;
	
	public final Object recordId;
}
