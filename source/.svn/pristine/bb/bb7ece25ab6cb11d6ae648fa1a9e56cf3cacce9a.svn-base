//Copyright (c) 2000-2003 San Diego Supercomputer Center (SDSC),
//a facility operated jointly by the University of California,
//San Diego (UCSD) and General Atomics, San Diego, California, USA.
//
//Users and possessors of this source code are hereby granted a
//nonexclusive, royalty-free copyright and design patent license to
//use this code in individual software. License is not granted for
//commercial resale, in whole or in part, without prior written
//permission from SDSC. This source is provided "AS IS" without express
//or implied warranty of any kind.

/*
 * Created on Nov 16, 2004
 *
 * by R. Hannes Niedner <hannes@sdsc.edu>
 * 	 Michael Gribskov <gribskov@purdue.edu>
 * 	 Jesus. M. Castagnetto <jmc@sdsc.edu>
 */
package org.ngbw.sdk.bioutils.sequence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ngbw.sdk.core.types.EntityType;

/**
 * @author Roland H. Niedner <br />
 *
 * adapted from Perl code authored by Michael Gribskov
 *
 * calcPi:
 * Calculate the pI iteratively by using the sum of titratable groups.
 * pKa used here are for the side chain of the free amino acid.
 *
 */
class PiCalculator {

	//precision of charge estimate.  This
	//value should give pI to 0.01 pH unit
	private static final double TARGET = 0.001;

	private static final Map<String, Double> PK = new HashMap<String, Double>(9);

	private static final Map<String, Integer> CHARGE = new HashMap<String, Integer>(
			0);
	static {
		PK.put("C", Double.valueOf(10.28));
		PK.put("D", Double.valueOf(3.65));
		PK.put("E", Double.valueOf(4.25));
		PK.put("H", Double.valueOf(6.00));
		PK.put("K", Double.valueOf(10.53));
		PK.put("R", Double.valueOf(12.48));
		PK.put("Y", Double.valueOf(10.07));
		PK.put("Nter", Double.valueOf(8.56));
		PK.put("Cter", Double.valueOf(3.56));

		CHARGE.put("C", Integer.valueOf(-1));
		CHARGE.put("D", Integer.valueOf(-1));
		CHARGE.put("E", Integer.valueOf(-1));
		CHARGE.put("H", Integer.valueOf(1));
		CHARGE.put("K", Integer.valueOf(1));
		CHARGE.put("R", Integer.valueOf(1));
		CHARGE.put("Y", Integer.valueOf(-1));
		CHARGE.put("Nter", Integer.valueOf(1));
		CHARGE.put("Cter", Integer.valueOf(-1));
	}

	static Float calculate(String sequenceString, EntityType entityType) {
		if (sequenceString == null || sequenceString.length() == 0)
			return null;

		if (entityType == null)
			throw new RuntimeException("EntityType is null!");
		if (EntityType.PROTEIN.equals(entityType) == false)
			throw new RuntimeException("Argument must be a Protein.!");

		Map<String, Integer> aminoacidCount = new HashMap<String, Integer>();
		String seq = sequenceString.toUpperCase();
		int sequenceStringLength = seq.length();
		//counting the number of each aa in PK within the seq
		Iterator<String> it = PK.keySet().iterator();
		while (it.hasNext()) {
			String aa = (String) it.next();
			String tempsequenceString = seq;
			Integer aaCount = Integer.valueOf(sequenceStringLength
					- (tempsequenceString.replaceAll(aa, "").length()));
			aminoacidCount.put(aa, aaCount);
			//System.out.println(aa + ":" + aaCount);
		}
		aminoacidCount.put("Nter", Integer.valueOf(1));
		aminoacidCount.put("Cter", Integer.valueOf(1));

		// starting with a pH range of 1.0 - 11.0 iteratively refine the pH
		// until charge is < $TARGET
		double tph = 12.5;
		@SuppressWarnings("unused")
		double top = netCharge(tph, aminoacidCount);
		double bph = 3.5;
		@SuppressWarnings("unused")
		double bottom = netCharge(bph, aminoacidCount);
		double test;
		double testph = 0;

		boolean flag = true;
		int count = 0;
		while (flag) {
			count++;
			testph = (tph + bph) / 2.0;
			test = netCharge(testph, aminoacidCount);

			if (test < 0.0) {
				tph = testph;
				top = test;
			} else {
				bph = testph;
				bottom = test;
			}
			if ((tph - bph) < TARGET)
				flag = false;
			//System.err.println(count + " : " + (tph-bph));
		}
		return new Float(testph);
	}

	/**netCharge:
	 *
	 * calculate the net charge on the peptide based on the composition given in
	 * aminoacidCount, pKa given in PK and charge given in CHARGE.
	 *
	 */
	private static double netCharge(double ph, Map<String, Integer> aminoacidCount) {

		double totalCharge = 0.0;
		Iterator<String> it = PK.keySet().iterator();
		while (it.hasNext()) {
			String aa = (String) it.next();
			int aaCount = ((Integer) aminoacidCount.get(aa)).intValue();
			int aaCharge = ((Integer) CHARGE.get(aa)).intValue();
			double thisCharge;
			double pKa = ((Double) PK.get(aa)).doubleValue();
			if (aaCharge < 0) {
				thisCharge = aaCount * (1.0 - fractProtonated(pKa, ph));
			} else {
				thisCharge = aaCount * fractProtonated(pKa, ph);
			}
			thisCharge *= aaCharge;
			totalCharge += thisCharge;
		}
		return totalCharge;
	}

	/**fractProtonated:
	 *
	 * Calculate the fraction of a single titrating group that is protonated.
	 *
	 * USAGE: fractH = fractProtonated( double pKa, double pH )
	 */
	private static double fractProtonated(double pka, double ph) {
		if (ph < 0.0) {
			return 0;
		}

		double hplus = Math.pow(10.0, -1.0 * ph);
		hplus /= (hplus + Math.pow(10.0, -1.0 * pka));
		return hplus;
	}

	public static void main(String[] args) {
		String sequenceString = "MSVQSSSGSLEGPPSWSQLSTSPTPGSAAAARSLLNHTPPSGRPREGAMDELHSLDPRRQELLEARFTGVASGSTGSTGSCSVGAKASTNNESSNHSFGSLGSLSDKESETPEKKQSESSRGRKRKAENQNESSQGKSIGGRGHKISDYFEYQGGNGSSPVRGIPPAIRSPQNSHSHSTPSSSVRPNSPSPTALAFGDHPIVQPKQLSFKIIQTDLTMLKLAALESNKIQDLEKKEGRIDDLLRANCDLRRQIDEQQKLLEKYKERLNKCISMSKKLLIEKSTQEKLSSREKSMQDRLRLGHFTTVRHGASFTEQWTDGFAFQNLVKQQEWVNQQREDIERQRKLLAKRKPPTANNSQAPSTNSEPKQRKNKAVNGAENDPFVRPNLPQLLTLAEYHEQEEIFKLRLGHLKKEEAEIQAELERLERVRNLHIRELKRINNEDNSQFKDHPTLNERYLLLHLLGRGGFSEVYKAFDLYEQRYAAVKIHQLNKSWRDEKKENYHKHACREYRIHKELDHPRIVKLYDYFSLDTDTFCTVLEYCEGNDLDFYLKQHKLMSEKEARSIVMQIVNALRYLNEIKPPIIHYDLKPGNILLVDGTACGEIKITDFGLSKIMDDDSYGVDGMDLTSQGAGTYWYLPPECFVVGKEPPKISNKVDVWSVGVIFFQCLYGRKPFGHNQSQQDILQENTILKATEVQFPVKPVVSSEAKAFIRRCLAYRKEDRFDVHQLANDPYLLPHMRRSNSSGNLHMAGLTASPTPPSSSIITY";
		float testPi = (float) 9.44415;

		System.out.println(calculate(sequenceString, EntityType.PROTEIN) + " should be close to: " + testPi);
	}
}