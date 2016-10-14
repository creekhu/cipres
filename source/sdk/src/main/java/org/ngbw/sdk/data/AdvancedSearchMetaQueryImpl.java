package org.ngbw.sdk.data;

import java.util.List;
import java.util.Set;

import org.ngbw.sdk.api.data.AdvancedSearchMetaQuery;
import org.ngbw.sdk.api.data.Criterion;
import org.ngbw.sdk.api.data.DatasetService;
import org.ngbw.sdk.core.query.LogicalQueryOperator;
import org.ngbw.sdk.core.query.MultiStringValueQueryOperator;
import org.ngbw.sdk.core.query.MultiValueQueryOperator;
import org.ngbw.sdk.core.query.NullValueQueryOperator;
import org.ngbw.sdk.core.query.QueryOperator;
import org.ngbw.sdk.core.query.SingleStringValueQueryOperator;
import org.ngbw.sdk.core.query.SingleValueQueryOperator;
import org.ngbw.sdk.core.query.TwoValueQueryOperator;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordType;

public class AdvancedSearchMetaQueryImpl extends MetaQueryImpl implements
		AdvancedSearchMetaQuery {

	public AdvancedSearchMetaQueryImpl(DatasetService datasetService, final RecordType recordType) {
		super(datasetService, recordType);
		// TODO Auto-generated constructor stub
	}

	public AdvancedSearchMetaQueryImpl(DatasetService datasetService, final RecordType recordType,
			Dataset dataset) {
		super(datasetService, recordType, dataset);
		// TODO Auto-generated constructor stub
	}

	public AdvancedSearchMetaQueryImpl(DatasetService datasetService, final RecordType recordType,
			Set<Dataset> datasets) {
		super(datasetService, recordType, datasets);
		// TODO Auto-generated constructor stub
	}

	public AdvancedSearchMetaQuery addCriterion(Criterion criterion) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int execute() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(NullValueQueryOperator op, String queryField) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(SingleValueQueryOperator op,
			String queryField, Object value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(SingleStringValueQueryOperator op,
			String queryField, String value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(SingleStringValueQueryOperator op,
			String queryField, String value, boolean caseSensitive) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(TwoValueQueryOperator op, String queryField,
			Object low, Object high) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(MultiValueQueryOperator op,
			String queryField, List<?> values) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(MultiStringValueQueryOperator op,
			String queryField, List<String> values) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Criterion getCriterion(LogicalQueryOperator op, Criterion lhc,
			Criterion rhc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set<QueryOperator> getQueryOperators(String queryField) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set<String> getQueryableFields() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int execute(String searchPhrase) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int execute(String searchPhrase, boolean parallel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
