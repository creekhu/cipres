package org.ngbw.sdk.data;

import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.data.ResultTuple;
import org.ngbw.sdk.core.types.Dataset;

/**
 * @author hannes
 * 
 */
public class DefaultResultTuple implements ResultTuple {

	private final Dataset dataset;
	private final Object key;
	private final Map<String, String> values;
	private final float rank;

	public DefaultResultTuple(Dataset dataset, Object recordKey, Map<String, String> values, float rank) {
		this.dataset = dataset;
		this.key = recordKey;
		this.values = values;
		this.rank = rank;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public Object getSourceKey() {
		return key;
	}

	public String getValue(String field) {
		return values.get(field);
	}

	public Set<String> getFields() {
		return values.keySet();
	}

	public float getRank() {
		return rank;
	}
}