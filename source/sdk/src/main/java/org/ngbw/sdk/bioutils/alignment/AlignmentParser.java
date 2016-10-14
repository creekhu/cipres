package org.ngbw.sdk.bioutils.alignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.ngbw.sdk.core.types.DataFormat;

/**
 * @author hannes
 * <br />
 * Simple ALignment Parser to tear multiple sequence alignments apart
 * and populate a an AlignmentBean.
 * It parses PHYLIP (interleaved, sequential), FASTA (only aligned!!!),
 * CLUSTALW (ALN) and NEXUS (alignment only - NO tree) and NEEDLE. <br />
 * 
 * Written after BioJava, Pal and others have failed me.
 *
 */
public class AlignmentParser {

	public static Alignment parseAlignment(String input) {
		if (input == null)
			throw new NullPointerException("Input is null!");
		LinkedHashMap<String, StringBuffer> buffers = new LinkedHashMap<String, StringBuffer>();
		BufferedReader br = new BufferedReader(new StringReader(input));
		DataFormat dataFormat = null;
		String line = null;
		try {
			while (br.ready() && (line = br.readLine()) != null) {
				if (line.trim().length() == 0)
					continue;
				else if (line.startsWith("#NEXUS") || line.startsWith("#nexus")) {
					parseNexus(buffers, br);
					dataFormat = DataFormat.NEXUS;
				} else if (line.startsWith("CLUSTAL")
						|| line.startsWith("clustal")) {
					parseClustal(buffers, br);
					dataFormat = DataFormat.CLUSTAL;
				} else if (line.matches("^\\d+ \\d+")) {
					parsePhylip(buffers, br);
					dataFormat = DataFormat.PHYLIP;
				} else if (line.startsWith(">")) {
					parseFasta(line, buffers, br);
					dataFormat = DataFormat.FASTA;
				} else if (line.startsWith("Global: ")  || 
						(line.startsWith("###") && br.readLine().startsWith("# Program: needle"))) {
					parseNeedle(buffers, br);
					dataFormat = DataFormat.NEEDLE;
				} else
					throw new RuntimeException("Unknown Alignment Format!");
			}
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// warn we tried
				e.printStackTrace();
			}
		}
		if (buffers.size() == 0)
			throw new RuntimeException("Unknown Alignment Format!");
		Alignment aln = new Alignment();
		for (String seqId : buffers.keySet()) {
			String sequence = buffers.get(seqId).toString().replaceAll("\\s",
					"");
			aln.addAlignedSequence(seqId, sequence);
		}
		aln.setDataFormat(dataFormat);
		return aln;
	}

	static void parseNexus(LinkedHashMap<String, StringBuffer> buffers,
			BufferedReader br) throws IOException {
		String id, sequ;
		String line;
		boolean inMatrix = false;
		while (br.ready() && (line = br.readLine()) != null) {
			if (line.trim().equalsIgnoreCase("matrix"))
				inMatrix = true;
			else if (inMatrix == false)
				continue;
			else if (line.trim().length() == 0)
				continue;
			else if (line.trim().equals(";"))
				inMatrix = false;
			else {
				String[] split = line.trim().split("\\s+");
				if (split.length != 2)
					throw new RuntimeException("bad line in matrix: " + line);
				else {
					id = split[0];
					sequ = split[1];
					if (buffers.containsKey(id) == false)
						buffers.put(id, new StringBuffer());
					buffers.get(id).append(sequ);
				}
			}
		}
	}

	static void parseClustal(LinkedHashMap<String, StringBuffer> buffers,
			BufferedReader br) throws IOException {
		String id, sequ;
		String line;
		while (br.ready() && (line = br.readLine()) != null) {
			if (line.trim().length() == 0)
				continue;
			else if (line.trim().matches("[\\*\\.\\:\\s]+"))
				continue;
			else if (line.trim().matches("[\\d\\s]+"))
				continue;
			else {
				String[] split = line.trim().split("\\s+");
				if (split.length != 2)
					throw new RuntimeException("bad line in matrix: " + line);
				else {
					id = split[0];
					sequ = split[1];
					if (buffers.containsKey(id) == false)
						buffers.put(id, new StringBuffer());
					buffers.get(id).append(sequ);
				}
			}
		}
	}

	static void parseNeedle(LinkedHashMap<String, StringBuffer> buffers,
			BufferedReader br) throws IOException {
		String id, sequ;
		String line;
		while (br.ready() && (line = br.readLine()) != null) {
			if (line.trim().length() == 0)
				continue;
			else if (line.trim().matches("[\\|\\.\\:\\s]+"))
				continue;
			else if (line.trim().matches("^#.*"))
				continue;
			else if (line.trim().matches("^%.*"))
				continue;
			else if (line.trim().matches("^Overall.*"))
				continue;
			else if (line.trim().matches("^Score.*"))
				continue;
			else {
				//HBA_HUMAN       1         VLSPADKTNVKAAWGKVGAHAGEYGAEALERMFLSFPTTKTYFP 44
				String[] split = line.trim().split("\\s+");
				if (split.length != 4)
					throw new RuntimeException("bad line in matrix: " + line);
				else {
					//testing start
					Integer.valueOf(split[1]);
					//testing end
					Integer.valueOf(split[3]);
					id = split[0];
					sequ = split[2];
					if (buffers.containsKey(id) == false)
						buffers.put(id, new StringBuffer());
					buffers.get(id).append(sequ);
				}
			}
		}
	}

	static void parsePhylip(LinkedHashMap<String, StringBuffer> buffers,
			BufferedReader br) throws IOException {
		List<String> ids = new ArrayList<String>();
		String id;
		String line;
		String sequ;
		boolean isSequential = false;
		boolean firstBlock = true;
		int counter = 0;
		while (br.ready() && (line = br.readLine()) != null) {
			if (line.trim().length() == 0) {
				if (counter != 0) {
					counter = 0;
					firstBlock = false;
				}
			} else if (line.matches("^\\d+ \\d+")) {
				isSequential = true;
				counter = 0;
			} else {
				if (isSequential || firstBlock) {
					String[] split = line.trim().split("\\s+", 2);
					id = split[0];
					sequ = split[1];
					if (ids.contains(id) && ids.indexOf(id) != counter)
						throw new RuntimeException("duplicate id: " + id);
					ids.add(id);
					if (buffers.containsKey(id) == false)
						buffers.put(id, new StringBuffer());
					buffers.get(id).append(sequ);
				} else {
					id = ids.get(counter);
					if (buffers.containsKey(id) == false)
						buffers.put(id, new StringBuffer());
					buffers.get(id).append(line.trim().replaceAll("\\s", ""));
				}
				counter++;
			}
		}
	}

	static void parseFasta(String firstLine,
			LinkedHashMap<String, StringBuffer> buffers, BufferedReader br)
			throws IOException {
		String id = firstLine.replaceFirst(">", "");
		String line;
		while (br.ready() && (line = br.readLine()) != null) {
			if (line.trim().length() == 0)
				continue;
			else if (line.startsWith(">"))
				id = line.replaceFirst(">", "");
			else {
				if (buffers.containsKey(id) == false)
					buffers.put(id, new StringBuffer());
				buffers.get(id).append(line);
			}
		}
	}

}
