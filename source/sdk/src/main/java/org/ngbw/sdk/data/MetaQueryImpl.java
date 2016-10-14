/*
 * MetaQueryImpl.java
 */
package org.ngbw.sdk.data;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.api.data.MetaQuery;
import org.ngbw.sdk.api.data.QueryResult;
import org.ngbw.sdk.api.data.ResultTuple;
import org.ngbw.sdk.api.data.SearchHitCollection;
import org.ngbw.sdk.common.util.TimeUtils;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public abstract class MetaQueryImpl implements MetaQuery {

	protected final DatasetService datasetService;
	protected final RecordType recordType;
	protected boolean executed = false;
	protected int maxResults = 500;
	protected Set<Dataset> datasets = new HashSet<Dataset>();
	protected TimeUtils timer = new TimeUtils();
	protected SearchHitCollection results;
	protected Map<Dataset, SearchHitCollection> resultMap = new HashMap<Dataset, SearchHitCollection>();
	protected Map<SearchHit, Object> primaryKeyMap = new HashMap<SearchHit, Object>();
	protected final ExecutorService pool = Executors.newCachedThreadPool();

	protected MetaQueryImpl(final DatasetService datasetService, final RecordType recordType) {
		super();
		this.datasetService = datasetService;
		this.recordType = recordType;
	}
	
	public MetaQueryImpl(final DatasetService datasetService, final RecordType recordType, Dataset dataset) {
		this(datasetService, recordType);
		datasets.add(dataset);
	}
	
	public MetaQueryImpl(final DatasetService datasetService, final RecordType recordType, Set<Dataset> datasets) {
		this(datasetService, recordType);
		this.datasets.addAll(datasets);
	}
	
	public String getSearchTime() {
		if (this.isExecuted())
			return timer.getTimeString();
		return null;
	}

	public SourceDocument getRecordSource(SearchHit searchHit) {
		Dataset dataset = searchHit.getDataset();
		Object primaryKey = primaryKeyMap.get(searchHit);
		DataResource dataResource = datasetService.getDataResource(dataset);
		byte[] data = dataResource.getRecordSource(dataset, primaryKey);
		DataFormat dataFormat = datasetService.getDataFormat(dataset);
		EntityType entityType = datasetService.getEntityType(dataset);
		DataType dataType = datasetService.getDataType(dataset);
		SourceDocumentBean sourceDocument = new SourceDocumentBean(entityType, dataType, dataFormat, data);
		return sourceDocument;
	}
	
	public void setMaxResultsPerDataset(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getResultCount() {
		int total = 0;
		if(results == null) {
			for(Dataset dataset : resultMap.keySet()) {
				total += getResultCount(dataset);
			}
		} else {
			total = results.size();
		}
		return total;
	}

	public boolean isTruncated(Dataset dataset) {
		if (getResultCount(dataset) == maxResults)
			return true;
		return false;
	}

	public List<Dataset> getTruncatedDatasets() {
		List<Dataset> dss = new ArrayList<Dataset>();
		for (Dataset ds : datasets) 
			if (isTruncated(ds)) dss.add(ds);
		return dss;
	}

	public int getResultCount(Dataset dataset) {
		SearchHitCollection thisResults = resultMap.get(dataset);
		if (thisResults == null) return 0;
		return resultMap.get(dataset).size();
	}

	public SearchHitCollection getResults() {
		if (results == null) {
			for(Dataset dataset : resultMap.keySet()) {
				if(results == null) {
					results = resultMap.get(dataset);
				} else {
					results.addAll(resultMap.get(dataset));
				}
			}
		}
		return results;
	}

	public SearchHitCollection getResults(int fromIndex, int toIndex) {
		return getResults().slice(fromIndex, toIndex);
	}

	public SearchHitCollection getResults(Dataset dataset) {
		return resultMap.get(dataset);
	}

	public SearchHitCollection getResults(Dataset dataset, int fromIndex, int toIndex) {
		return resultMap.get(dataset).slice(fromIndex, toIndex);
	}

	public boolean isExecuted() {
		return executed;
	}
	
	protected SearchHitCollection translateQueryResult(Dataset dataset, QueryResult qr) throws ParseException {
		if (qr == null || qr.getSize() == 0)
			return null;

		Set<RecordFieldType> types = datasetService.getFieldTypes(recordType);
		SearchHitCollection drc = new SearchHitCollectionImpl(dataset, recordType, types);

		for (ResultTuple tuple : qr) {
			SearchHit searchHit = new SearchHit(recordType, types, dataset, tuple.getRank());

			for(RecordFieldType recordField : types) {
				String field = datasetService.getFieldName(dataset, recordField);

				searchHit.getField(recordField).setValue(tuple.getValue(field));
			}

			drc.add(searchHit);
			primaryKeyMap.put(searchHit, tuple.getSourceKey());
		}

		return drc;
	}
	
	protected void translateQueryResult(QueryResult qr) throws ParseException {
		Set<RecordFieldType> types = datasetService.getFieldTypes(recordType);
		for (ResultTuple tuple : qr) {
			Dataset dataset = tuple.getDataset();

			if (resultMap.containsKey(dataset) == false) {
				SearchHitCollection drc = new SearchHitCollectionImpl(dataset, recordType, types);
				resultMap.put(dataset, drc);
			}

			SearchHit searchHit = new SearchHit(recordType, types, dataset, tuple.getRank());

			for(RecordFieldType recordField : types) {
				String field = datasetService.getFieldName(dataset, recordField);

				searchHit.getField(recordField).setValue(tuple.getValue(field));
			}

			resultMap.get(dataset).add(searchHit);
			primaryKeyMap.put(searchHit, tuple.getSourceKey());
		}
	}
}
