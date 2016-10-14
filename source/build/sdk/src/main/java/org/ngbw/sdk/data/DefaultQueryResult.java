package org.ngbw.sdk.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.data.QueryResult;
import org.ngbw.sdk.api.data.ResultTuple;

/**
 *
 * @author unwin
 * @author hannes
 */
public class DefaultQueryResult implements QueryResult {

	private Set<String> fields = new HashSet<String>();
	private List<ResultTuple> results = new ArrayList<ResultTuple>();
	
	public DefaultQueryResult(Set<String>fields) {
		super();
		this.fields = fields;
	}

	public DefaultQueryResult(Set<String> fields, List<ResultTuple> results) {
		this(fields);
		this.results = results;
	}

	public Set<String> getReturnedFields() {
		return fields;
	}

	public Integer getSize() {
		return results.size();
	}

	public Iterator<ResultTuple> iterator() {
		return results.iterator();
	}

	public void addResultTuple(ResultTuple rt) {
		results.add(rt);
	}
}
