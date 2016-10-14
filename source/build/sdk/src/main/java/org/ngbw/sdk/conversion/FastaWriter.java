package org.ngbw.sdk.conversion;

import java.io.IOException;
import java.io.OutputStream;

public class FastaWriter {

	/**
	 * Write a fasta formatted String from the submitted label and sequence
	 * string.
	 * 
	 * @param sequence
	 * @throws IOException
	 */
	public static void write(OutputStream out, String label,
			String sequence) throws IOException {
		StringBuffer sb = new StringBuffer();
		write(sb, label, sequence);
		out.write(sb.toString().getBytes());
	}

	/**
	 * Write a fasta formatted String from the submitted label and sequence
	 * string.
	 * 
	 * @param sequence
	 * @throws IOException
	 */
	public static void write(StringBuffer sb, String label,
			String sequence) throws IOException {
		int width = 80;
		sb.append(">" + label + "\n");
		String seqString = sequence;
		int start = 0;
		int rep = 1;
		while (seqString.length() >= (width * rep)) {
			sb.append(seqString.substring(start, start + width) + "\n");
			rep++;
			start += width;
		}
		int mod = seqString.length() % width;
		if (mod > 0)
			sb.append(seqString.substring(seqString.length() - mod) + "\n");
	}
}
