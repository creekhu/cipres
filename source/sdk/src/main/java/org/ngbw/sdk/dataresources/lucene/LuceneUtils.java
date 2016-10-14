/*
 * LuceneUtils.java
 */
package org.ngbw.sdk.dataresources.lucene;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.ngbw.sdk.api.data.QueryResult;
import org.ngbw.sdk.api.data.ResultTuple;
import org.ngbw.sdk.core.shared.DatasetRecord;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.data.DefaultQueryResult;
import org.ngbw.sdk.data.DefaultResultTuple;
import org.ngbw.sdk.database.RecordField;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class LuceneUtils {

	public static final String RECORD_ID_FIELD = "RECORD_ID";
	public static final String RECORD_SOURCE_FIELD = "RECORD_SOURCE";
	public static final String SIMPLE_SEARCH_FIELD = "SIMPLE_SEARCH";
	public static final String DATASET_FIELD = "DATASET";
	public static ThreadLocal<Analyzer> fieldIndexAnalyzer = new ThreadLocal<Analyzer>();
	public static ThreadLocal<QueryParser> sourceIndexQueryParser = new ThreadLocal<QueryParser>();
	public static ThreadLocal<Analyzer> sourceIndexAnalyzer = new ThreadLocal<Analyzer>();
	public static ThreadLocal<QueryParser> simpleSearchQueryParser = new ThreadLocal<QueryParser>();
	public static ThreadLocal<Analyzer> idSearchAnalyzer = new ThreadLocal<Analyzer>();
	private static ThreadLocal<QueryParser> idSearchQueryParser = new ThreadLocal<QueryParser>();
	
	/**
	 * Iterate through maxResults Hits and transforms the found documents into
	 * ResultTuples and collates them in the returned QueryResult.
	 * 
	 * @param hits
	 * @param fields
	 * @param maxResults
	 * @return queryResult
	 */
	public static QueryResult processHits(Hits hits, Map<String, IndexFieldType> fields, int maxResults) {
		DefaultQueryResult queryResult = new DefaultQueryResult(fields.keySet());
		for (int x = 0; ((x < hits.length()) && (x < maxResults)); x++) {
			Document hit = null;
			float rank = 0;
			try {
				hit = hits.doc(x);
				rank = hits.score(x);
			} catch (CorruptIndexException e) {
				throw new RuntimeException("Encountered Corrupt index!", e);
			} catch (IOException e) {
				throw new RuntimeException("Encountered IO error!", e);
			}
			ResultTuple tuple = LuceneUtils.populateResultTuple(hit, rank, fields);
			queryResult.addResultTuple(tuple);
		}
		return queryResult;
	}
	
	/**
	 * Method harvest all the field values from a Lucene search hit
	 * into a ResultTuple.
	 * 
	 * @param hit
	 * @param rank
	 * @param fieldIndexTypeLookup
	 * @return resultTuple
	 */
	public static ResultTuple populateResultTuple(Document hit, float rank, Map<String, IndexFieldType> fieldIndexTypeLookup) {
		HashMap<String, String> values = new HashMap<String, String>();
		Object recordKey = null;
		for (String field : fieldIndexTypeLookup.keySet()) {
			IndexFieldType type = fieldIndexTypeLookup.get(field);

			if (type == null)
				throw new NullPointerException("IndexFieldType is null for field " + field);

			values.put(field, getValue(hit, field));
		}
		recordKey = getRecordId(hit);
		DefaultResultTuple tuple = new DefaultResultTuple(getDatasetValue(hit), recordKey, values, rank);
		return tuple;
	}

	/**
	 * Method retrieves all values from a DataRecord and adds them
	 * together with the filtered source record to a Lucene document.
	 * 
	 * @param datasetRecord
	 * @param prefilteredSource
	 * @return document
	 */
	public static Document buildDocument(DatasetRecord datasetRecord, String prefilteredSource, Map<String, IndexFieldType> fieldIndexTypeLookup) {
		if (datasetRecord == null)
			throw new NullPointerException("DataRecord is null!");
		if (prefilteredSource == null)
			throw new NullPointerException("prefilteredSource is null!");
		if (fieldIndexTypeLookup == null)
			throw new NullPointerException("fieldIndexTypeLookup is null!");
		Document document = new Document();
		setDatasetValue(document, datasetRecord.getDataset());
		setSimpleSearchField(document, prefilteredSource);
		List<RecordField> fields = datasetRecord.getFields();
		for (RecordField field : fields)  {
			RecordFieldType fieldType = field.getFieldType();
			IndexFieldType indexType = fieldIndexTypeLookup.get(fieldType.toString());
			if (indexType == null)
				throw new NullPointerException("IndexFieldType is null for RecordField " + field);
			if (IndexFieldType.ID.equals(indexType)) {
				String recordId = field.getValueAsString();
				setKeywordValue(document, field.toString(), recordId);
				setRecordId(document, recordId);
			}
			else if (IndexFieldType.KEYWORD.equals(indexType)) 
				setKeywordValue(document, fieldType.toString(), field.getValueAsString());
			else if(IndexFieldType.DATE.equals(indexType)) 
				setDateValue(document, fieldType.toString(), field.getValueAsDate());
			else if (IndexFieldType.TEXT.equals(indexType)) 
				setTextValue(document, fieldType.toString(), field.getValueAsString());
			else 
				throw new RuntimeException("Unknown IndexFieldType " + indexType);
		}
		return document;
	}
	
	public static Analyzer getPerFieldAnalyzer(Map<String, IndexFieldType> fieldIndexTypeLookup) {
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
				new StandardAnalyzer());
		Set<String> fields = fieldIndexTypeLookup.keySet();
		for (String field : fields)  {
			IndexFieldType type = fieldIndexTypeLookup.get(field.toString());
			if (type == null)
				throw new NullPointerException("IndexFieldType is null for RecordField " + field);
			if (IndexFieldType.ID.equals(type)) 
				analyzer.addAnalyzer(field, new KeywordAnalyzer());
			else if (IndexFieldType.KEYWORD.equals(type)) 
				analyzer.addAnalyzer(field, new KeywordAnalyzer());
			else if(IndexFieldType.DATE.equals(type)) 
				analyzer.addAnalyzer(field, new KeywordAnalyzer());
			else if (IndexFieldType.TEXT.equals(type)) 
				analyzer.addAnalyzer(field, new StandardAnalyzer());
			else 
				throw new RuntimeException("Unknown IndexFieldType " + type);
		}
		return analyzer;
	}
	
	public static void deleteFromIndex(Directory directory, String recordKey) {
		try {
			IndexReader reader = IndexReader.open(directory);
			Term term = new Term(RECORD_ID_FIELD, recordKey);
			reader.deleteDocuments(term);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("IndexReader encountered a corrupt index!", e);
		} catch (IOException e) {
			throw new RuntimeException("IndexReader encountered an IO error!", e);
		}
	}
	
	public static void writeToIndex(IndexWriter writer, Document document) {
		try {
			writer.addDocument(document);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("IndexWriter encountered a corrupt index!", e);
		} catch (IOException e) {
			throw new RuntimeException("IndexWriter encountered an IO error!", e);
		}
	}
	
	public static void writeToSourceIndex(IndexWriter sourceWriter, String srcData, String recordKey) {
		if (srcData == null)
			throw new NullPointerException("sourceData is null!");
		if (recordKey == null)
			throw new NullPointerException("recordKey is null!");
		Document document = new Document();
		setRecordSource(document, recordKey, srcData);
		writeToIndex(sourceWriter, document);
	}
	
	public static Hits searchIndex(Searcher searcher, Query query) {
		Hits hits;
		try {
			hits = searcher.search(query);
		} catch (IOException e) {
			throw new RuntimeException("Searcher encountered an IO error!", e);
		}
		return hits;
	}
	
	public static ResultTuple getResultTupleById(Searcher searcher, String recordKey, Map<String, IndexFieldType> fieldMap) {
		ResultTuple tuple;
		Query query;
		try {
			query = getIdSearchQueryParser().parse(QueryParser.escape(recordKey));
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new RuntimeException("Inavlid search phrase: " + recordKey, e);
		}
		Hits hits;
		try {
			hits = searcher.search(query);
		} catch (IOException e) {
			throw new RuntimeException("Searcher encountered an IO error!", e);
		}
		if (hits.length() == 0)
			throw new NullPointerException("No document in index for record key: " + recordKey);
		else if(hits.length() > 1)
			throw new RuntimeException("IMPOSSIBLE: More than 1 document in the index for record key: " + recordKey);
		Document hit;
		try {
			hit = hits.doc(0);
			tuple = populateResultTuple(hit, hits.score(0), fieldMap);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Corrupt index error whilst accessing hit!", e);
		} catch (IOException e) {
			throw new RuntimeException("IO error whilst accessing hit!", e);
		}
		return tuple;
	}
	
	public static String getRecordSource(Searcher searcher, String recordKey) {
		Query query;
		try {
			query = getSourceIndexQueryParser().parse(QueryParser.escape(recordKey));
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new RuntimeException("Inavlid search phrase: " + recordKey, e);
		}
		Hits hits;
		try {
			hits = searcher.search(query);
		} catch (IOException e) {
			throw new RuntimeException("Searcher encountered an IO error!", e);
		}
		if (hits.length() == 0)
			throw new NullPointerException("No source in index for record key: " + recordKey);
		else if(hits.length() > 1)
			throw new RuntimeException("IMPOSSIBLE: More than 1 source in the index for record key: " + recordKey);
		Document hit;
		try {
			hit = hits.doc(0);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Corrupt index error whilst accessing hit!", e);
		} catch (IOException e) {
			throw new RuntimeException("IO error whilst accessing hit!", e);
		}
		return getValue(hit, RECORD_SOURCE_FIELD);
	}

	public static IndexWriter getIndexWriter(Directory indexDirectory) {
		if (indexDirectory == null)
			throw new NullPointerException("Directory is null!");
		Analyzer analyzer = getFieldIndexAnalyzer();
		if (analyzer == null)
			throw new NullPointerException("FieldIndexAnalyzer is null!");
		IndexWriter writer;
		try {
			writer = new IndexWriter(indexDirectory, analyzer, true);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Found corrupt index at: "
					+ indexDirectory, e);
		} catch (LockObtainFailedException e) {
			throw new RuntimeException("Could not obtain lock for index at: "
					+ indexDirectory, e);
		} catch (IOException e) {
			throw new RuntimeException("Encountered IOError accessing index at: "
					+ indexDirectory, e);
		}
		return writer;
	}
	
	public static IndexWriter getSourceIndexWriter(Directory sourceIndexDirectory) {
		if (sourceIndexDirectory == null)
			throw new NullPointerException("Directory is null!");
		Analyzer analyzer = getSourceIndexAnalyzer();
		if (analyzer == null)
			throw new NullPointerException("SourceIndexAnalyzer is null!");
		IndexWriter writer;
		try {
			writer = new IndexWriter(sourceIndexDirectory, analyzer, true);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Found corrupt index at: "
					+ sourceIndexDirectory, e);
		} catch (LockObtainFailedException e) {
			throw new RuntimeException("Could not obtain lock for index at: "
					+ sourceIndexDirectory, e);
		} catch (IOException e) {
			throw new RuntimeException("Encountered IOError accessing index at: "
					+ sourceIndexDirectory, e);
		}
		return writer;
	}
	
	public static Query buildSimpleQuery(String searchPhrase) {
		Query query;
		try {
			query = getSimpleSearchQueryParser().parse(QueryParser.escape(searchPhrase));
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new RuntimeException("Inavlid search phrase: " + searchPhrase, e);
		}
		return query;
	}
	
	public static Query buildIdQuery(String recordId) {
		Query query;
		try {
			query = getIdSearchQueryParser().parse(QueryParser.escape(recordId));
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new RuntimeException("Inavlid recordId: " + recordId, e);
		}
		return query;
	}
	
	public static Query buildAdvancedQuery(String[] fields, String[] searchPhrases, BooleanClause.Occur[] flags, Analyzer analyzer) {
		Query query;
		try {
			query = MultiFieldQueryParser.parse(fields, searchPhrases, flags, analyzer);
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new RuntimeException("Inavlid search phrase!", e);
		}
		return query;
	}
	
	public static Searcher getIndexSearcher(Directory indexDirectory) {
		if (indexDirectory == null)
			throw new NullPointerException("Directory is null!");
		IndexSearcher searcher;
		try {
			searcher = new IndexSearcher(indexDirectory);
		} catch (CorruptIndexException e) {
			throw new RuntimeException("Found corrupt index at: "
					+ indexDirectory, e);
		} catch (IOException e) {
			throw new RuntimeException("Encountered IOError accessing index at: "
					+ indexDirectory, e);
		}
		return searcher;
	}
	
	public static Searcher getIndexSearcher(Set<Directory> directories) {
		if (directories == null || directories.isEmpty())
			throw new NullPointerException("directories is null or empty!");
		int i = 0;
		Searchable[] searchers = new Searchable[directories.size()];
		for (Directory directory : directories) 
			searchers[i++] = getIndexSearcher(directory);
		ParallelMultiSearcher searcher;
		try {
			searcher = new ParallelMultiSearcher(searchers);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return searcher;
	}
	
	private static Analyzer getFieldIndexAnalyzer() {
		if (fieldIndexAnalyzer.get() == null)
			fieldIndexAnalyzer.set(new StandardAnalyzer());
		return fieldIndexAnalyzer.get();
	}

	private static Analyzer getSourceIndexAnalyzer() {
		if (sourceIndexAnalyzer.get() == null)
			sourceIndexAnalyzer.set(new KeywordAnalyzer());
		return sourceIndexAnalyzer.get();
	}

	private static Analyzer getIdSearchAnalyzer() {
		if (idSearchAnalyzer.get() == null)
			idSearchAnalyzer.set(new KeywordAnalyzer());
		return idSearchAnalyzer.get();
	}

	private static QueryParser getSimpleSearchQueryParser() {
		if (simpleSearchQueryParser.get() == null)
			simpleSearchQueryParser.set(new QueryParser(SIMPLE_SEARCH_FIELD, getFieldIndexAnalyzer()));
		return simpleSearchQueryParser.get();
	}

	private static QueryParser getIdSearchQueryParser() {
		if (idSearchQueryParser.get() == null)
			idSearchQueryParser.set(new QueryParser(RECORD_ID_FIELD, getIdSearchAnalyzer()));
		return idSearchQueryParser.get();
	}

	private static QueryParser getSourceIndexQueryParser() {
		if (sourceIndexQueryParser.get() == null)
			sourceIndexQueryParser.set(new QueryParser(RECORD_ID_FIELD, getSourceIndexAnalyzer()));
		return sourceIndexQueryParser.get();
	}

	/**
	 * Wrap basic retrieving a value from a hit for common error handling.
	 * 
	 * @param hit
	 * @param field
	 * @return value
	 */
	private static String getValue(Document hit, String field) {
		if (hit == null)
			throw new NullPointerException("Document is null!");

		if (field == null)
			throw new NullPointerException("field is null!");

		String value = hit.get(field);

		if (value.length() == 0)
			return null;

		return value;
	}

	/**
	 * Method stores the submitted value 
	 * indexing it tokenized
	 * using the submitted name as field name.
	 * 
	 * @param document
	 * @param name
	 * @param value
	 */
	private static void setTextValue(Document document, String field, String value) {
		if (value == null) return;
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (field == null)
			throw new NullPointerException("field is null!");
		document.add(new Field(field, value, Field.Store.YES,
				Field.Index.TOKENIZED));
	}

	/**
	 * Method stores the submitted value 
	 * indexing it untokenized
	 * using the submitted name as field name.
	 * 
	 * @param document
	 * @param name
	 * @param value
	 */
	private static void setKeywordValue(Document document, String field, String value) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (field == null)
			throw new NullPointerException("field is null!");
		if (value == null)
			return;
		document.add(new Field(field, value, Field.Store.YES,
				Field.Index.UN_TOKENIZED));
	}

	/**
	 * Method sets a date field in the Lucene Document 
	 * storing the submitted date (Day resolution) 
	 * indexing it untokenized
	 * using the submitted name as field name.
	 * 
	 * @param document
	 * @param name
	 * @param date
	 */
	private static void setDateValue(Document document, String field, Date date) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (field == null)
			throw new NullPointerException("field is null!");
		if (date == null)
			return;
		document.add(new Field(field, DateTools.timeToString(date
				.getTime(), DateTools.Resolution.DAY), Field.Store.YES,
				Field.Index.UN_TOKENIZED));
	}
	
	/**
	 * Return the Dataset of that hit.
	 * @param hit
	 * @return
	 */
	private static Dataset getDatasetValue(Document hit) {
		if (hit == null)
			throw new NullPointerException("Document is null!");
		String value = getValue(hit, DATASET_FIELD);
		return Dataset.valueOf(value);
	}

	/**
	 * Method sets the Dataset for this Record
	 * 
	 * @param document
	 * @param dataset
	 */
	private static void setDatasetValue(Document document, Dataset dataset) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (dataset == null)
			throw new NullPointerException("Dataset is null!");
		setKeywordValue(document, DATASET_FIELD, dataset.toString());
	}
	
	/**
	 * Method return the ID for this hit.
	 * This id is NOT the same as the hit id, but will enable us to find
	 * the fulltext record from the source index.
	 * 
	 * @param hit
	 * @return
	 */
	private static Object getRecordId(Document hit) {
		if (hit == null)
			throw new NullPointerException("Document is null!");
		String recordId = getValue(hit, RECORD_ID_FIELD);
		if (recordId == null)
			throw new NullPointerException("Missing RecordId!");
		return recordId;
	}
	
	private static void setRecordId(Document document, String recordId) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (recordId == null)
			throw new NullPointerException("recordId is null!");
		document.add(new Field(RECORD_ID_FIELD, recordId, Field.Store.YES,
				Field.Index.UN_TOKENIZED));
	}

	/**
	 * Method simply stores the submitted source test
	 * with the field name "RecordSource". The text is not
	 * indexed. Use the setSimpleSearchField(String)
	 * method to add the portion of the source
	 * that should be indexed.
	 * 
	 * @param document
	 * @param srcData
	 */
	private static void setRecordSource(Document document, String recordId, String srcData) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (recordId == null)
			throw new NullPointerException("recordKey is null!");
		if (srcData == null)
			throw new NullPointerException("srcData is null!");
		document.add(new Field(RECORD_SOURCE_FIELD, srcData,
				Field.Store.COMPRESS, Field.Index.NO));
		setRecordId(document, recordId);
	}

	/**
	 * Method indexes the submitted filtered sourceRecord 
	 * (everything that should not be indexed is removed)
	 * but does not store the text. This will support
	 * the simple Fulltext search functionality.
	 * The index can be queried with field name: "FullText".
	 * 
	 * @param document
	 * @param filteredSrcData
	 */
	private static void setSimpleSearchField(Document document, String filteredSrcData) {
		if (document == null)
			throw new NullPointerException("Document is null!");
		if (filteredSrcData == null)
			throw new NullPointerException("filteredSrcData is null!");
		document.add(new Field(SIMPLE_SEARCH_FIELD, filteredSrcData,
				Field.Store.NO, Field.Index.TOKENIZED));
	}
}
