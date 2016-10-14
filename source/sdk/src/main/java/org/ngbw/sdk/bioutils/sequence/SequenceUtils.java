/*
 * Created on Apr 6, 2004
 *
 * PKR - The Protein Kinase Resource - Development Code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors and University of California.
 * These should be listed in @author doc comments.
 *
 * For more information on The Protein Kinase Resource project
 * and its aims, or to join the kinases@sdsc.edu mailing list,
 * visit the home page at:
 *
 *      http://pkr.sdsc.edu/
 *
 * This source code may be freely  distributed, provided that no
 * charge above the cost of distribution is levied, and that the
 * disclaimer below is always attached to it.
 *
 * Disclaimer:
 * The software is provided as is without any guarantees or warranty.
 * Although the author has attempted to find and correct any bugs
 * in the  free software programs,  the author is not responsible
 * for any damage or losses of any kind caused by the use or misuse
 * of the programs.
 * PKR or the author are under no obligation to provide support,
 * service, corrections, or upgrades  to the free software programs.
 *
 */
package org.ngbw.sdk.bioutils.sequence;

import java.util.HashMap;
import java.util.Map;

import org.ngbw.sdk.core.types.EntityType;

/**
 * @author Roland H. Niedner <br />@sdsc.edu
 *
 * SequenceUtility class
 *
 * is a utility class for biological sequence string manipulations
 *
 * <p>
 * <a href="SequenceUtility.java.html"><i>View Source</i></a>
 * </p>
 */
public class SequenceUtils {
	
	private static final Map<String, String> map1to3 = new HashMap<String, String>();
	private static final Map<String, String> map3to1 = new HashMap<String, String>();
	private static final Map<String, String> tripletTo3aa = new HashMap<String, String>();
	static {
		map1to3.put("A", "ALA");
		map1to3.put("C", "CYS");
		map1to3.put("D", "ASP");
		map1to3.put("E", "GLU");
		map1to3.put("F", "PHE");
		map1to3.put("G", "GLY");
		map1to3.put("H", "HIS");
		map1to3.put("I", "ILE");
		map1to3.put("K", "LYS");
		map1to3.put("L", "LEU");
		map1to3.put("M", "MET");
		map1to3.put("N", "ASN");
		map1to3.put("P", "PRO");
		map1to3.put("Q", "GLN");
		map1to3.put("R", "ARG");
		map1to3.put("S", "SER");
		map1to3.put("T", "THR");
		map1to3.put("V", "VAL");
		map1to3.put("W", "TRP");
		map1to3.put("Y", "TYR");
		map1to3.put("*", "STP");
		map1to3.put("X", "X");
		map1to3.put("*", "*");
		map1to3.put("_", "_");
		map1to3.put("-", "-");
		map1to3.put(" ", " ");
		
		map3to1.put("ALA", "A");
		map3to1.put("CYS", "C");
		map3to1.put("ASP", "D");
		map3to1.put("GLU", "E");
		map3to1.put("PHE", "F");
		map3to1.put("GLY", "G");
		map3to1.put("HIS", "H");
		map3to1.put("ILE", "I");
		map3to1.put("LYS", "K");
		map3to1.put("LEU", "L");
		map3to1.put("MET", "M");
		map3to1.put("ASN", "N");
		map3to1.put("PRO", "P");
		map3to1.put("GLN", "Q");
		map3to1.put("ARG", "R");
		map3to1.put("SER", "S");
		map3to1.put("THR", "T");
		map3to1.put("VAL", "V");
		map3to1.put("TRP", "W");
		map3to1.put("TYR", "Y");
		map3to1.put("STP", "*");
		map3to1.put("X", "X");
		map3to1.put("*", "*");
		map3to1.put("_", "_");
		map3to1.put("-", "-");
		map3to1.put(" ", " ");

		tripletTo3aa.put("GCA", "ALA");
		tripletTo3aa.put("GCC", "ALA");
		tripletTo3aa.put("GCG", "ALA");
		tripletTo3aa.put("GCT", "ALA");
		tripletTo3aa.put("AGA", "ARG");
		tripletTo3aa.put("AGG", "ARG");
		tripletTo3aa.put("CGA", "ARG");
		tripletTo3aa.put("CGC", "ARG");
		tripletTo3aa.put("CGG", "ARG");
		tripletTo3aa.put("CGT", "ARG");
		tripletTo3aa.put("GAC", "ASP");
		tripletTo3aa.put("GAT", "ASP");
		tripletTo3aa.put("AAC", "ASN");
		tripletTo3aa.put("AAT", "ASN");
		tripletTo3aa.put("TGC", "CYS");
		tripletTo3aa.put("TGT", "CYS");
		tripletTo3aa.put("CAA", "GLN");
		tripletTo3aa.put("CAG", "GLN");
		tripletTo3aa.put("GAA", "GLU");
		tripletTo3aa.put("GAG", "GLU");
		tripletTo3aa.put("GGA", "GLY");
		tripletTo3aa.put("GGC", "GLY");
		tripletTo3aa.put("GGG", "GLY");
		tripletTo3aa.put("GGT", "GLY");
		tripletTo3aa.put("ATA", "ILE");
		tripletTo3aa.put("ATC", "ILE");
		tripletTo3aa.put("ATT", "ILE");
		tripletTo3aa.put("CAC", "HIS");
		tripletTo3aa.put("CAT", "HIS");
		tripletTo3aa.put("CTA", "LEU");
		tripletTo3aa.put("CTC", "LEU");
		tripletTo3aa.put("CTG", "LEU");
		tripletTo3aa.put("CTT", "LEU");
		tripletTo3aa.put("TTA", "LEU");
		tripletTo3aa.put("TTG", "LEU");
		tripletTo3aa.put("AAA", "LYS");
		tripletTo3aa.put("AAG", "LYS");
		tripletTo3aa.put("ATG", "MET");
		tripletTo3aa.put("CCA", "PRO");
		tripletTo3aa.put("CCC", "PRO");
		tripletTo3aa.put("CCG", "PRO");
		tripletTo3aa.put("CCT", "PRO");
		tripletTo3aa.put("TTC", "PHE");
		tripletTo3aa.put("TTT", "PHE");
		tripletTo3aa.put("AGC", "SER");
		tripletTo3aa.put("AGT", "SER");
		tripletTo3aa.put("TCA", "SER");
		tripletTo3aa.put("TCC", "SER");
		tripletTo3aa.put("TCG", "SER");
		tripletTo3aa.put("TCT", "SER");
		tripletTo3aa.put("ACA", "THR");
		tripletTo3aa.put("ACC", "THR");
		tripletTo3aa.put("ACG", "THR");
		tripletTo3aa.put("ACT", "THR");
		tripletTo3aa.put("TGG", "TRP");
		tripletTo3aa.put("TAC", "TYR");
		tripletTo3aa.put("TAT", "TYR");
		tripletTo3aa.put("TAA", "STP");
		tripletTo3aa.put("TAG", "STP");
		tripletTo3aa.put("TGA", "STP");
		tripletTo3aa.put("GTA", "VAL");
		tripletTo3aa.put("GTC", "VAL");
		tripletTo3aa.put("GTG", "VAL");
		tripletTo3aa.put("GTT", "VAL");
	}

	/**
	 * Translate the 3 letter aminoacid code into the single letter code.
	 * 
	 * @param symbol (3 Letter)
	 * @return  symbol (1 Letter)
	 */
	public static String aaTranslate3to1(String symbol) {
		if(symbol == null) 
			throw new RuntimeException("NULL Symbol!");
		symbol = symbol.toUpperCase();
		if(map3to1.containsKey(symbol))
			return map3to1.get(symbol);
		throw new RuntimeException("Unknown Symbol! " + symbol);
	}

	/**
	 * Translate the 3 letter aminoacid code into the single letter code.
	 * 
	 * @param symbol (1 Letter)
	 * @return  symbol (3 Letter)
	 */
	public static String aaTranslate1to3(String symbol) {
		if(symbol == null) 
			throw new RuntimeException("NULL Symbol!");
		symbol = symbol.toUpperCase();
		if(map1to3.containsKey(symbol))
			return map1to3.get(symbol);
		throw new RuntimeException("Unknown Symbol! " + symbol);
	}
	
	/**
	 * Translate a triplet into the 3-letter code aminoacid (human genecode).
	 * 
	 * @param triplet
	 * @return 3 Letter symbol (aa)
	 */
	public static String triplet2aa3(String triplet) {
		if(triplet == null) 
			throw new RuntimeException("NULL Triplet!");
		triplet = triplet.toUpperCase();
		if(tripletTo3aa.containsKey(triplet))
			return tripletTo3aa.get(triplet);
		throw new RuntimeException("Unknown Symbol! " + triplet);
	}

	
	/**
	 * Translate a triplet into the 1-letter code aminoacid (human genecode).
	 * 
	 * @param triplet
	 * @return 1 Letter symbol (aa)
	 */
	public static String triplet2aa1(String triplet) {
		return aaTranslate3to1(triplet2aa3(triplet));
	}
	
	/**
	 * Method make a best guess in trying to detect wether
	 * the sequence string is dna, rna or protein.
	 * The algorithm assumes that if the string
	 * contains only
	 * 		- ATGCatgc characters it is an DNA sequence
	 * 		- ATGCatgc characters it is an RNA sequence
	 * and in every other case (unless see below) its
	 * being labeled a protein.
	 * If the sequence string is null or empty then null is returned.
	 *
	 * CAVE: Its important to consider this before you trustfully use it.
	 * It is recommended that you possible specify a minimum length
	 * before you safely assume the "prediction" is true.
	 *
	 * @param rawSeqString
	 * @return NULL | DNA | RNA | Protein
	 */
	public static EntityType detectSequenceType(
			String rawSeqString) {
		String sequenceString = cleanSequenceString(rawSeqString);
		if (sequenceString == null) {
			return null;
		} else if (sequenceString.toUpperCase().replaceAll("[ATGC ]", "")
				.length() == 0) {
			return EntityType.DNA;
		} else if (sequenceString.toUpperCase().replaceAll("[AUGC ]", "")
				.length() == 0) {
			return EntityType.RNA;
		} else {
			return EntityType.PROTEIN;
		}
	}

	/**
	 * public function cleanSequenceString()
	 * @param rs - raw sequence
	 * @return String
	 *
	 * Method removes all non-letter character from the raw sequence string
	 * and renders the seqString.
	 * CAVE: If you use that method to prepare the
	 * raw sequence string then keep in mind that protein sequences
	 * must have upper case charcters and dna and rna lower case characters
	 *
	 */
	public static String cleanSequenceString(String rs) {
		if (rs == null)
			return null;
		rs = rs.replaceAll("[^A-Za-z]", "");
		return rs;
	}

	/**
	 * public function formatFASTA()
	 * @param seqString
	 * @param descriptiveLine
	 * @param lineLength
	 *
	 * It transforms the seqString into FASTA formatted sequence
	 * and will throw RuntimeExceptions if either rawSequence
	 * or descriptiveLine is not set.
	 *
	 */
	public static String formatFASTA(String seqString, String descriptiveLine,
			int lineLength) {
		StringBuffer fastaString = new StringBuffer(">" + descriptiveLine
				+ "\n");
		try {
			int seqLength = seqString.length();
			for (int pos = 0; pos <= seqLength; pos += lineLength) {
				int end = Math.min(pos + lineLength - 1, seqLength);
				fastaString.append(seqString.substring(pos, end) + "\n");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return fastaString.toString();
	}

	/**
	 * public function formatInt()
	 *
	 * @param myint - is the integer to be formatted
	 * @param maxint - is the length of the formatted string
	 *
	 * @return String
	 *
	 * used by function formatPretty() to
	 * transform an int into a formatted string
	 * pretty much like printf %d
	 *
	 */
	public static String formatInt(int myint, int maxint) {
		Integer myInt = Integer.valueOf(myint);
		Integer maxInt = Integer.valueOf(maxint);
		int prefix = maxInt.toString().length() - myInt.toString().length();
		StringBuffer formattedInt = new StringBuffer();
		for (int i = 0; i < prefix; i++) {
			formattedInt.append(" ");
		}
		formattedInt.append(myint + " ");
		return formattedInt.toString();
	}

	/**
	 * public function formatPretty()
	 * @param seqString
	 * @param blockLength
	 * @param blocksPerLine
	 * @return String
	 *
	 * is the engine used by getFormattedString to return
	 * a (pretty) formatted sequence and will throw
	 * RuntimeExceptions if rawSequence is not set.
	 *
	 */
	public static String formatPretty(String seqString, int blockLength,
			int blocksPerLine) {
		StringBuffer seqBuffer = new StringBuffer(seqString);
		try {
			int seqLength = seqString.length();
			int blockCounter = 0;
			int lineInit = 1;

			for (int i = blockLength; i < seqBuffer.length(); i += blockLength + 1) {
				blockCounter++;

				if (blockCounter == blocksPerLine) {

					String prefix = formatInt(lineInit + blockLength, seqLength);
					seqBuffer.insert(i, "\n" + prefix);
					i += prefix.length();
					blockCounter = 0;
				} else {
					seqBuffer.insert(i, " ");
				}
				lineInit += blockLength;
			}
			seqBuffer.insert(0, formatInt(1, seqLength));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return seqBuffer.toString();
	}

	/**
	 * Calculates the isoelectric point for a protein sequence string.
	 *
	 * @param sequenceString
	 * @param entityType
	 * @return isoelectric point (Float)
	 */
	public static Float calculateIsoelectricPoint(String sequenceString, EntityType entityType) {
		return PiCalculator.calculate(sequenceString, entityType);
	}

	/**
	 * Calculates the molecular weight for a sequence string.
	 *
	 * @param sequenceString
	 * @param entityType
	 * @return molecular weight (Float)
	 */
	public static Float calculateMolecularWeight(String sequenceString, EntityType entityType) {
		return MolWeightCalculator.calculate(sequenceString, entityType);
	}
}
