/*
 * SimpleSearchMetaQueryImpl.java
 */
package org.ngbw.sdk.data;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.data.DataResource;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.api.data.QueryResult;
import org.ngbw.sdk.api.data.SearchHitCollection;
import org.ngbw.sdk.api.data.SimpleQuery;
import org.ngbw.sdk.api.data.SimpleSearchMetaQuery;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * @author Roland H. Niedner <br />
 *
 */
public class SimpleSearchMetaQueryImpl extends MetaQueryImpl implements SimpleSearchMetaQuery {

	private static final Log log = LogFactory.getLog(SimpleSearchMetaQueryImpl.class);


	public SimpleSearchMetaQueryImpl(final DatasetService datasetService, final RecordType recordType, Dataset dataset) {
		super(datasetService, recordType, dataset);
	}
	
	public SimpleSearchMetaQueryImpl(final DatasetService datasetService, final RecordType recordType, Set<Dataset> datasets) {
		super(datasetService, recordType, datasets);
	}

	public int execute(String searchPhrase) throws ParseException, InterruptedException, ExecutionException {
		return execute(searchPhrase, true);
	}

	public int execute(String searchPhrase, boolean parallel) throws ParseException, InterruptedException, ExecutionException {
		//reset the query
		if (executed) {
			resultMap = new HashMap<Dataset, SearchHitCollection>();
			results = null;
			timer.reset();
		}
		//harvest the query results
		HashMap<Dataset, Future<?>> queries = 
			new HashMap<Dataset, Future<?>>(datasets.size());
		timer.start();
		for (Dataset dataset : datasets) {
			DataResource dr = datasetService.getDataResource(dataset);
			Map<String, FieldDataType> fields = datasetService.getFields(dataset);
			SimpleQuery query = dr.createSimpleQuery(dataset, fields, searchPhrase);
			query.setMaxResults(maxResults);
			if (parallel) {
				Future<?> foo = pool.submit(new SimpleQueryExecutor(dataset, query));
				queries.put(dataset, foo);
			} else {
				QueryResult qr = query.execute();
				resultMap.put(dataset, translateQueryResult(dataset, qr));
			}
		}

		if (parallel) {
			for (Dataset dataset : datasets) {
				queries.get(dataset).get();
			}
		}
		timer.takeTime();
		executed = true;
		return getResultCount();
	}

	class SimpleQueryExecutor implements Runnable {

		private final SimpleQuery query;
		private final Dataset dataset;
		
		SimpleQueryExecutor(Dataset dataset, SimpleQuery query) {
			this.dataset = dataset;
			this.query = query;
		}
		
		public void run() {
			try {
				QueryResult qr = query.execute();
				resultMap.put(dataset, translateQueryResult(dataset, qr));
			}
			catch (Exception err) {
				log.error("", err);
			}
		}
		
	}
}
