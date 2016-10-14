/*
 * DataDetails.java
 */
package org.ngbw.sdk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.database.UserDataItem;

public class DataDetails
{
	public static DataFormat diagnoseFormat(byte[] data)
	{
		return diagnoseFormat(new String(data));
	}

	/**
		Make a quick attempt at guessing the dataformat, usually based on looking for certain
		words on the first line of the file.

		It's quite possible that this function will guess wrong, so the user should be allowed
		to override the guess and/or we should attempt to validate the format at some point
		after guessing.

		We guess:
			phylip
			nexus
			hennig86
			fasta
			clustal aln
			newick (files that contain nothing but a newick string, or possibly multiple newick strings)
	*/
	public static DataFormat diagnoseFormat(String text)
	{
		String phylipHeader = "^\\s*\\d+\\s+\\d+\\s*$";
		String nexusHeader = "^#NEXUS\\s*$"; // ignorecase when comparing.
		String hennig86Header = "(^NSTATES|^XREAD)(\\s*$|\\s+.*$)"; // ignore case when comparing.
		String fastaHeader= "^>.*$"; 
		String clustalHeader = "^CLUSTAL .*$";

		/*
			This is used on text of the full file, trimmed of leading and trailing whitespace, including
			newlines.  The text must start with "(" and it must end with ");", but embedded whitespace is 
			ignored in the match.  So literally what we're doing is:
				(?s)	enables DOTALL, so that "." will match newline as well as everything else.
				^\\( 	says that the first char must be "("
				.*		says that any string may follow, including embedded newlines
				\\)		says that then we must find a ")"
				[\\s\\S]* followed by any amount of whitespace (includes newlines)
						and non-white space
				;		followed by a semicolon
				$		followed by eof
		*/
		String newickFile = "(?s)^\\(.*\\)[\\s\\S]*;$";

		String firstline = text.split("\\n", 2)[0];
		firstline = firstline.toUpperCase();
		Pattern p;

		if (Pattern.matches(phylipHeader, firstline))
		{
			return DataFormat.PHYLIP;
		}
		if (Pattern.matches(nexusHeader, firstline))
		{
			return DataFormat.NEXUS;
		}
		if (Pattern.matches(hennig86Header, firstline))
		{
			return DataFormat.HENNIG86;
		}
		if (Pattern.matches(fastaHeader, firstline))
		{
			return DataFormat.FASTA;
		}
		if (Pattern.matches(clustalHeader, firstline))
		{
			return DataFormat.CLUSTAL;
		}
		if (Pattern.matches(newickFile, text.trim()))
		{
			return DataFormat.NEWICK;
		}

		return DataFormat.UNKNOWN;
	}


	public static void main(String args[]) throws Exception
	{
		String line;
		File file = new File(args[0]);

		// read file into a byte array
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] buffer = new byte[(int)file.length()];
		in.readFully(buffer);

		DataFormat df = diagnoseFormat(buffer);
		System.out.println("format is " + df);
	}
}
