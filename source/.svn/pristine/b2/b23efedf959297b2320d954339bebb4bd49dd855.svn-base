/*
 * FastaOutParser.java
 */
package org.ngbw.sdk.bioutils.fastout;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class FastaOutParser {

	private static final Pattern programmPattern = Pattern.compile("^((?:.?FAST.)|(?:SSEARCH))\\s.+version\\s+([\\d\\.]+)\\s.*$");
	private static final Pattern datasetPattern = Pattern.compile("^ vs\\s+.+/([^/\\.]+)\\.fasta library$");
	private static final Pattern queryPattern = Pattern.compile("^(.*),\\s+(\\d+)\\s+((?:nt)|(?:aa))\\s*$");
	private static final Pattern dbNrOfSequencesPattern = Pattern.compile("^(\\d+)\\s+residues in\\s+(\\d+)\\s+library sequences");	
	private static final Pattern hitsStartPattern = Pattern.compile("^The best scores are:.*$");
	private static final Pattern hitPattern = Pattern.compile("^(.*)\\s+\\(\\s*(\\d+)\\)(\\s+\\[.\\])?\\s+(\\d+)\\s+([\\d\\.]+)\\s+([\\de\\.-]+)\\s*$");

	public static FastaResult parse(String data) {
		if (data == null || data.trim().length() == 0)
			throw new NullPointerException("input data is null or empty!");
		BufferedReader reader = new BufferedReader(new StringReader(data));
		return parse(reader);
	}
	
	public static FastaResult parse(byte[] data) {
		if (data == null || data.length == 0)
			throw new NullPointerException("input data is null or empty!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		return parse(reader);
	}

	private static FastaResult parse(BufferedReader reader) {
		FastaResult blastOut = new FastaResult();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = programmPattern.matcher(line);
				if (matcher.find()) {
					blastOut.PROGRAM = matcher.group(1).trim();
					blastOut.PROGRAM_VERSION = matcher.group(2).trim();
				}
				matcher = datasetPattern.matcher(line);
				if (matcher.find()) {
					blastOut.DATASET = matcher.group(1).trim();
				}
				matcher = queryPattern.matcher(line);
				if (matcher.find()) {
					blastOut.QUERY = matcher.group(1).trim();
					blastOut.QUERY_LENGTH = matcher.group(2).trim();
					blastOut.QUERY_RESIDUE_TYPE = matcher.group(3).trim();
				}
				matcher = dbNrOfSequencesPattern.matcher(line);
				if (matcher.find()) {
					blastOut.NUMBER_OF_LETTERS = matcher.group(1).trim().replaceAll("\\D", "");
					blastOut.NUMBER_OF_SEQUENCES = matcher.group(2).trim().replaceAll("\\D", "");
				}

				if (hitsStartPattern.matcher(line).matches())
					parseHitTable(reader, blastOut);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (blastOut.hits.size() == 0)
			throw new NullPointerException("No Hits could be parsed!");
		return blastOut;
	}

	private static void parseHitTable(BufferedReader reader, FastaResult blastOut) throws Exception
	{
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.length() == 0)
				break;

			Matcher matcher = hitPattern.matcher(line);

			if (!matcher.matches())
				throw new Exception("Unexpected score table format");

			FastaHit newHit = new FastaHit();
			String[] subStrings = matcher.group(1).split("\\|");

			if (subStrings.length < 4)
				throw new Exception("Unexpected score table format");

			newHit.NAMESPACE = subStrings[0];
			newHit.PRIMARY_ID = subStrings[1];
			newHit.DATASET = subStrings[2];

			if (subStrings.length > 4)
				newHit.NAME = subStrings[4];
			else
				newHit.NAME = subStrings[3];

			newHit.ZSCORE = matcher.group(2);
			newHit.OPT = matcher.group(4);
			newHit.BITS = matcher.group(5);
			newHit.E_VALUE = matcher.group(6);

			if (newHit.E_VALUE.startsWith("e"))
				newHit.E_VALUE = "1" + matcher.group(6);

			blastOut.add(newHit);
		}
	}
}
