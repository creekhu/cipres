// Alignment.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package org.ngbw.sdk.bioutils.alignment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.ngbw.sdk.core.types.DataFormat;

/**
 * 
 * Basic bean for any Sequence Alignment.
 * 
 * @author hannes
 *
 */
public class Alignment {
	
	private LinkedHashMap<String, String>sequences = new LinkedHashMap<String, String>();
	private List<String> ids = new ArrayList<String>();
	private DataFormat dataFormat;
	private int numberOfChars = 0;
	
	public Alignment() {}
	
	protected DataFormat getDataFormat() {
		return dataFormat;
	}

	protected void setDataFormat(DataFormat dataFormat) {
		this.dataFormat = dataFormat;
	}

	public void addAlignedSequence(String id, String sequence) {
		if (id == null)
			throw new NullPointerException("id is null!");
		if (numberOfChars > 0 &&  numberOfChars != sequence.length())
			throw new NullPointerException("Wrong number of sequence characters."
					+ " Alignment Length:" + numberOfChars  + " != "
					+ sequence.length()  + " for " + id +  " (" + sequence  + ")");
		numberOfChars = sequence.length();
		ids.add(id);
		sequences.put(id, sequence);
	}
	
	public List<String> getIdentifiers() {
		return ids;
	}

	public String getIdentifier(int seq) {
		return ids.get(seq);
	}

	/**
	 * Returns string representation of single sequence in
	 * alignment with gap characters included.
	 */
	public String getAlignedSequenceString(int seq) {
		String id = getIdentifier(seq);
		String sequence = sequences.get(id);
		return sequence;
	}

	/**
	 * Returns string representation of single sequence in
	 * alignment with gap characters included.
	 */
	public String getAlignedSequenceString(String id) {
		String sequence = sequences.get(id);
		return sequence;
	}

	/** sequence alignment at (sequence, site) */
	public char getData(int seq, int site) {
		String id = ids.get(seq);
		String sequence = sequences.get(id);
		return sequence.charAt(site);
	}
	
	public char getData(String id, int site) {
		String sequence = sequences.get(id);
		return sequence.charAt(site);
	}

	/**
	 * @return number of sites for each sequence in this alignment
	 */
	public int getSiteCount() {
		return numberOfChars;
	}

	/**
	 * Return number of sequences in this alignment
	 */
	public int getSequenceCount() {
		return sequences.size();
	}
}
