package org.ngbw.sdk.bioutils.blast.blasttxt;

import java.util.ArrayList;
import java.util.List;

public class BlastResult {
	public BlastResult() {}
	public String PROGRAM;
	public String PROGRAM_VERSION;
	public String DATASET;
	public String NUMBER_OF_LETTERS;
	public String NUMBER_OF_SEQUENCES;
	public String SCORING_MATRIX;
	public String GAP_PENALTY;
	public String GAP_EXTENSION_PENALTY;
	public String QUERY;
	public List<BlastHit> hits = new ArrayList<BlastHit>();
	void add(BlastHit hit) {
		hits.add(hit);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("BlastOutput:\n"); 
		sb.append("programm: " + PROGRAM + "\n");
		sb.append("version: " + PROGRAM_VERSION + "\n");
		sb.append("scoring matrix: " + SCORING_MATRIX + "\n");
		sb.append("database: " + DATASET + "\n");
		sb.append("database length: " + NUMBER_OF_LETTERS + "\n");
		sb.append("database number of sequences: " + NUMBER_OF_SEQUENCES + "\n");
		sb.append("gap penalty: " + GAP_PENALTY + "\n");
		sb.append("gap extension penalty: " + GAP_EXTENSION_PENALTY + "\n");
		sb.append("query: " + QUERY + "\n");
		sb.append("number of hits: " + hits.size() + "\n");
		for (BlastHit hit : hits)
			sb.append(hit.toString() + "\n");
		return sb.toString();
			
	}
}
