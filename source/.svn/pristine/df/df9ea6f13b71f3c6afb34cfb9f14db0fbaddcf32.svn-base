/*
 * BlastTEXTParser.java
 */
package org.ngbw.sdk.bioutils.blast.blasttxt;


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
public class BlastTEXTParser {
	
	private static final Pattern programmPattern = Pattern.compile("^(.?BLAST.)\\s([\\d\\.]+)\\s");
	private static final Pattern datasetPattern = Pattern.compile("^Database:\\s(.*)$");
	private static final Pattern matrixPattern = Pattern.compile("^Matrix:\\s(.*)$");
	private static final Pattern gappenPattern = Pattern.compile("^Gap Penalties:\\sExistence:\\s(.*),\\sExtension:\\s(.*)$");
	private static final Pattern dbLengthPattern = Pattern.compile("^Length of database:\\s(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern queryPattern = Pattern.compile("^Query=\\s*(.*)$");
	private static final Pattern dbNrOfSequencesPattern = Pattern.compile("^Number of Sequences:\\s(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern hitsStartPattern = Pattern.compile("^Sequences producing significant alignments:.*$");
	private static final Pattern hitPattern = Pattern.compile("^(.*)\\s+(\\d+)\\s+(\\d+\\.\\d+|\\d*e\\-\\d+)\\s*$");

	public static BlastResult parse(String data) {
		if (data == null || data.trim().length() == 0)
			throw new NullPointerException("input data is null or empty!");
		BufferedReader reader = new BufferedReader(new StringReader(data));
		return parse(reader);
	}
	
	public static BlastResult parse(byte[] data) {
		if (data == null || data.length == 0)
			throw new NullPointerException("input data is null or empty!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		return parse(reader);
	}

	private static BlastResult parse(BufferedReader reader) {
		BlastResult blastOut = new BlastResult();
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
				matcher = matrixPattern.matcher(line);
				if (matcher.find()) {
					blastOut.SCORING_MATRIX = matcher.group(1).trim();
				}
				matcher = gappenPattern.matcher(line);
				if (matcher.find()) {
					blastOut.GAP_PENALTY = matcher.group(1);
					blastOut.GAP_EXTENSION_PENALTY = matcher.group(2).trim();
				}
				matcher = queryPattern.matcher(line);
				if (matcher.find()) {
					blastOut.QUERY = matcher.group(1).trim();
				}
				matcher = dbLengthPattern.matcher(line);
				if (matcher.find()) {
					blastOut.NUMBER_OF_LETTERS = matcher.group(1).trim().replaceAll("\\D", "");
				}
				matcher = dbNrOfSequencesPattern.matcher(line);
				if (matcher.find()) {
					blastOut.NUMBER_OF_SEQUENCES = matcher.group(1).trim().replaceAll("\\D", "");
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

	private static void parseHitTable(BufferedReader reader, BlastResult blastOut) throws Exception
	{
		String line = reader.readLine();

		if (line.length() != 0)
			throw new Exception("Unexpected hit table format");

		while ((line = reader.readLine()) != null) {
			if (line.length() == 0)
				break;

			Matcher matcher = hitPattern.matcher(line);

			if (!matcher.matches())
				throw new Exception("Unexpected hit table format");

			BlastHit newHit = new BlastHit();
			String[] subStrings = matcher.group(1).split("\\|");

			if (subStrings.length < 4)
				throw new Exception("Unexpected hit table format");

			newHit.NAMESPACE = subStrings[0];
			newHit.PRIMARY_ID = subStrings[1];
			newHit.DATASET = subStrings[2];

			if (subStrings.length > 4)
				newHit.NAME = subStrings[4];
			else
				newHit.NAME = subStrings[3];

			newHit.SCORE = matcher.group(2);
			newHit.E_VALUE = matcher.group(3);

			if (newHit.E_VALUE.startsWith("e"))
				newHit.E_VALUE = "1" + matcher.group(3);

			blastOut.hits.add(newHit);
		}
	}
}
