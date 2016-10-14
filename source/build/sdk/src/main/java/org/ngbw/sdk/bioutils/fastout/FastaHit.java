package org.ngbw.sdk.bioutils.fastout;

public class FastaHit {
	public FastaHit() {}
	public String NAMESPACE;
	public String DATASET;
	public String PRIMARY_ID;
	public String NAME;
	public String ZSCORE;
	public String BITS;
	public String OPT;
	public String E_VALUE;
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Hit: "); 
		sb.append("namespace: " + NAMESPACE);
		sb.append("\tdataset: " + DATASET);
		sb.append("\tid: " + PRIMARY_ID);
		sb.append("\tname: " + NAME);
		sb.append("\tZ-score: " + ZSCORE);
		sb.append("\tbits: " + BITS);
		sb.append("\topt: " + OPT);
		sb.append("\tevalue: " + E_VALUE);
		return sb.toString();
	}
}
