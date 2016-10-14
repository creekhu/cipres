package org.ngbw.sdk.dataresources.lucene;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.ngbw.sdk.api.data.QueryResult;
import org.ngbw.sdk.api.data.SimpleQuery;
import org.ngbw.sdk.common.util.TimeUtils;
import org.ngbw.sdk.core.types.Dataset;

/**
 *
 * @author unwin
 * @author hannes
 */
public class LuceneSimpleQuery implements SimpleQuery {

	private static Log log = LogFactory.getLog(LuceneSimpleQuery.class);
	private final Set<Directory> directories = new HashSet<Directory>();
	private final Dataset dataset;
	private final Searcher searcher;
	private final String searchPhrase;
	private int maxResults = 1000; //default
	private Map<String, IndexFieldType> fields;
	protected TimeUtils timer = new TimeUtils();
	protected boolean closed = false;

	/** Creates a new instance of LuceneSimpleQuery for single indices */
	public LuceneSimpleQuery(Dataset dataset, Directory directory,
			Map<String, IndexFieldType> fields, String searchPhrase) {
		this.dataset = dataset;
		this.directories.add(directory);
		this.searcher = LuceneUtils.getIndexSearcher(directory);
		this.searchPhrase = searchPhrase;
		this.fields = fields;
	}

	/** Creates a new instance of LuceneSimpleQuery for multiple indices */
	public LuceneSimpleQuery(Set<Directory> directories,
			Map<String, IndexFieldType> fields, String searchPhrase) {
		this.dataset = null;
		this.directories.addAll(directories);
		this.searcher = LuceneUtils.getIndexSearcher(directories);
		this.searchPhrase = searchPhrase;
		this.fields = fields;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public QueryResult execute() {
		if (closed)
			throw new RuntimeException("This query has already been executed or closed!");
		timer.start();
		Query query = LuceneUtils.buildSimpleQuery(searchPhrase);
		Hits hits = LuceneUtils.searchIndex(searcher, query);
		timer.takeTime();
		if (log.isInfoEnabled())
			log.info((dataset == null ? "MultidatasetQuery " : dataset.toString() + " ") +
					hits.length() + " hits found for '" + searchPhrase + "' (limit  "
					+ maxResults + ") in " + timer.getTimeString());
		QueryResult result = LuceneUtils.processHits(hits, fields, maxResults);
		close();
		return result;
	}
	
	public void close() {
		try {
			searcher.close();
			if (log.isDebugEnabled())
				log.debug("searcher closed");
		} catch (IOException e) {
			log.warn(e.toString(), e);
		}
		for (Directory dir : directories) {
			try {
				dir.close();
			} catch (IOException e) {
				log.warn(e.toString(), e);
			}
		}
		if (log.isDebugEnabled())
			log.debug("directory/ies closed");
		closed = true;
	}
}
