package org.ngbw.sdk.bioutils.fastout;

import java.util.ArrayList;
import java.util.List;

public class FastaResult {
	public FastaResult() {}
	public String PROGRAM;
	public String PROGRAM_VERSION;
	public String DATASET;
	public String NUMBER_OF_LETTERS;
	public String NUMBER_OF_SEQUENCES;
	public String QUERY;
	public String QUERY_LENGTH;
	public String QUERY_RESIDUE_TYPE;
	public List<FastaHit> hits = new ArrayList<FastaHit>();
	void add(FastaHit hit) {
		hits.add(hit);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("FastaOutput:\n"); 
		sb.append("programm: " + PROGRAM + "\n");
		sb.append("version: " + PROGRAM_VERSION + "\n");
		sb.append("database: " + DATASET + "\n");
		sb.append("database length: " + NUMBER_OF_LETTERS + "\n");
		sb.append("database number of sequences: " + NUMBER_OF_SEQUENCES + "\n");
		sb.append("query: " + QUERY + " " + QUERY_LENGTH + " " +  QUERY_RESIDUE_TYPE + "\n");
		sb.append("number of hits: " + hits.size() + "\n");
		for (FastaHit hit : hits)
			sb.append(hit.toString() + "\n");
		return sb.toString();
			
	}
}
