/**
 *
 */
package org.ngbw.sdk.bioutils.sequence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ngbw.sdk.core.types.EntityType;

/**
 * @author Roland H. Niedner <br />
 *
 */
class MolWeightCalculator {

	/**
	 * Calculate the molecular weight for a sequence.
	 * Will do the right thing for protein, dna, and rna sequences
	 *
	 * @param sequence
	 * @return mol. weight (Float)
	 */
	static Float calculate(String sequenceString, EntityType entityType) {
		if (sequenceString == null)
			return null;
		Map<String,Integer> aaCount = new HashMap<String,Integer>();
		Map<String,Double> aaWeights = null;

		if (entityType == null) {
			throw new RuntimeException("EntityType is null!");

		} else if (EntityType.PROTEIN.equals(entityType)) {

			aaWeights = new HashMap<String,Double>();
			aaWeights.put("H2O", Double.valueOf(18.01));
			aaWeights.put("A", Double.valueOf(89.09));
			aaWeights.put("C", Double.valueOf(121.16));
			aaWeights.put("D", Double.valueOf(132.10));
			aaWeights.put("E", Double.valueOf(146.13));
			aaWeights.put("F", Double.valueOf(165.19));
			aaWeights.put("G", Double.valueOf( 75.07));
			aaWeights.put("H", Double.valueOf(155.16));
			aaWeights.put("I", Double.valueOf(131.17));
			aaWeights.put("K", Double.valueOf(147.19));
			aaWeights.put("L", Double.valueOf(131.17));
			aaWeights.put("M", Double.valueOf(149.21));
			aaWeights.put("N", Double.valueOf(132.12));
			aaWeights.put("P", Double.valueOf(115.13));
			aaWeights.put("Q", Double.valueOf(146.15));
			aaWeights.put("R", Double.valueOf(175.20));
			aaWeights.put("S", Double.valueOf(105.09));
			aaWeights.put("T", Double.valueOf(119.03));
			aaWeights.put("V", Double.valueOf(117.15));
			aaWeights.put("W", Double.valueOf(204.22));
			aaWeights.put("Y", Double.valueOf(181.19));
			aaWeights.put("B", (aaWeights.get("D") + aaWeights.get("N")) / 2);
			aaWeights.put("Z", (aaWeights.get("E") + aaWeights.get("Q")) / 2);
			Double total = null;
			Collection<Double> weights = aaWeights.values();
			for (Double value : weights) {
				total =+ value;
			}
			//X is a wildcard so we assign the average mol. weight
			aaWeights.put("X", total/aaWeights.size());

			aaCount = new HashMap<String,Integer>();
			aaCount.put("A", 0);
			aaCount.put("B", 0);
			aaCount.put("C", 0);
			aaCount.put("D", 0);
			aaCount.put("E", 0);
			aaCount.put("F", 0);
			aaCount.put("G", 0);
			aaCount.put("H", 0);
			aaCount.put("I", 0);
			aaCount.put("K", 0);
			aaCount.put("L", 0);
			aaCount.put("M", 0);
			aaCount.put("N", 0);
			aaCount.put("P", 0);
			aaCount.put("Q", 0);
			aaCount.put("R", 0);
			aaCount.put("S", 0);
			aaCount.put("T", 0);
			aaCount.put("V", 0);
			aaCount.put("W", 0);
			aaCount.put("Y", 0);
			aaCount.put("X", 0);
			aaCount.put("Z", 0);

			countResidues(sequenceString, aaCount);


		} else if (EntityType.DNA.equals(entityType)) {

			aaWeights = new HashMap<String,Double>();
			aaWeights.put("H2O", Double.valueOf(18.01));
			aaWeights.put("A", Double.valueOf(331));
			aaWeights.put("T", Double.valueOf(306));
			aaWeights.put("G", Double.valueOf(347));
			aaWeights.put("C", Double.valueOf(307));

			aaCount = new HashMap<String,Integer>();
			aaCount.put("A", 0);
			aaCount.put("T", 0);
			aaCount.put("G", 0);
			aaCount.put("C", 0);

			countResidues(sequenceString, aaCount);


		} else if (EntityType.RNA.equals(entityType)) {

			aaWeights = new HashMap<String,Double>();
			aaWeights.put("H2O", Double.valueOf(18.01));
			aaWeights.put("A", Double.valueOf(347));
			aaWeights.put("U", Double.valueOf(324));
			aaWeights.put("T", Double.valueOf(322));
			aaWeights.put("G", Double.valueOf(363));
			aaWeights.put("C", Double.valueOf(323));

			aaCount = new HashMap<String,Integer>();
			aaCount.put("A", 0);
			aaCount.put("U", 0);
			aaCount.put("T", 0);
			aaCount.put("G", 0);
			aaCount.put("C", 0);

			countResidues(sequenceString, aaCount);

		} else {
			throw new RuntimeException("Invalid sequence type!");
		} //end else
		double mw = 0;
		int len = 0;
		for (String residue : aaCount.keySet()) {
		  if (aaCount.get(residue) > 0){
			 len += aaCount.get(residue);
			 mw += aaCount.get(residue) * aaWeights.get(residue);
		  }
		}
		mw -= (len - 1) * aaWeights.get("H2O");
		return new Float(mw);
	}

	private static void countResidues(String seq, Map<String,Integer> aaCount) {
		String[] seqLetters = seq.split("");
		for (String letter : seqLetters) {
			if (letter.equals("") == false)
				aaCount.put(letter, aaCount.get(letter)+1);
		}
	}

	public static void main(String[] args) {
		String sequenceString = "MSVQSSSGSLEGPPSWSQLSTSPTPGSAAAARSLLNHTPPSGRPREGAMDELHSLDPRRQELLEARFTGVASGSTGSTGSCSVGAKASTNNESSNHSFGSLGSLSDKESETPEKKQSESSRGRKRKAENQNESSQGKSIGGRGHKISDYFEYQGGNGSSPVRGIPPAIRSPQNSHSHSTPSSSVRPNSPSPTALAFGDHPIVQPKQLSFKIIQTDLTMLKLAALESNKIQDLEKKEGRIDDLLRANCDLRRQIDEQQKLLEKYKERLNKCISMSKKLLIEKSTQEKLSSREKSMQDRLRLGHFTTVRHGASFTEQWTDGFAFQNLVKQQEWVNQQREDIERQRKLLAKRKPPTANNSQAPSTNSEPKQRKNKAVNGAENDPFVRPNLPQLLTLAEYHEQEEIFKLRLGHLKKEEAEIQAELERLERVRNLHIRELKRINNEDNSQFKDHPTLNERYLLLHLLGRGGFSEVYKAFDLYEQRYAAVKIHQLNKSWRDEKKENYHKHACREYRIHKELDHPRIVKLYDYFSLDTDTFCTVLEYCEGNDLDFYLKQHKLMSEKEARSIVMQIVNALRYLNEIKPPIIHYDLKPGNILLVDGTACGEIKITDFGLSKIMDDDSYGVDGMDLTSQGAGTYWYLPPECFVVGKEPPKISNKVDVWSVGVIFFQCLYGRKPFGHNQSQQDILQENTILKATEVQFPVKPVVSSEAKAFIRRCLAYRKEDRFDVHQLANDPYLLPHMRRSNSSGNLHMAGLTASPTPPSSSIITY";
		String testWeight = "86.72 kilodaltons";

		System.out.println(calculate(sequenceString, EntityType.PROTEIN) + " should be close to: " + testWeight);
	}
}
