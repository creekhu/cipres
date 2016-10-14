package org.ngbw.sdk.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class SourceFileReader {

	public static void print(File source) throws FileNotFoundException, IOException {
		print(getBufferedReader(source));
	}

	public static void print(File source, boolean showLineNrs) throws FileNotFoundException, IOException {
		print(getBufferedReader(source), showLineNrs);
	}

	public static void print(File source, int nrOfLines) throws FileNotFoundException, IOException {
		print(getBufferedReader(source), nrOfLines);
	}

	public static void print(File source, boolean showLineNrs, int nrOfLines) throws FileNotFoundException, IOException {
		print(getBufferedReader(source), showLineNrs, nrOfLines);
	}

	public static void print(File source, int nrOfLines, int offset) throws FileNotFoundException, IOException {
		print(getBufferedReader(source), false, nrOfLines, offset);
	}

	public static void print(File source, boolean showLineNrs, int nrOfLines,
			int offset) throws FileNotFoundException, IOException {
		print(getBufferedReader(source), showLineNrs, nrOfLines, offset);
	}

	public static void print(URL source) throws IOException {
		print(getBufferedReader(source));
	}

	public static void print(URL source, boolean showLineNrs) throws IOException {
		print(getBufferedReader(source), showLineNrs);
	}

	public static void print(URL source, int nrOfLines) throws IOException {
		print(getBufferedReader(source), nrOfLines);
	}

	public static void print(URL source, boolean showLineNrs, int nrOfLines) throws IOException {
		print(getBufferedReader(source), showLineNrs, nrOfLines);
	}

	public static void print(URL source, int nrOfLines, int offset) throws IOException {
		print(getBufferedReader(source), false, nrOfLines, offset);
	}

	public static void print(URL source, boolean showLineNrs, int nrOfLines,
			int offset) throws IOException {
		print(getBufferedReader(source), showLineNrs, nrOfLines, offset);
	}

	public static void print(BufferedReader br) throws IOException {
		print(br, false);
	}

	public static void print(BufferedReader br, boolean showLineNrs) throws IOException {
		print(br, showLineNrs, 0);
	}

	public static void print(BufferedReader br, int nrOfLines) throws IOException {
		print(br, false, nrOfLines);
	}

	public static void print(BufferedReader br, boolean showLineNrs,
			int nrOfLines) throws IOException {
		if (nrOfLines < 0)
			throw new RuntimeException(
					"The number of lines must be greater than 0!");
		int line = 1;
		if (showLineNrs) {
			while (br.ready()) {
				if (nrOfLines > 0)
					System.out.println(line + ":\t" + br.readLine());
				line++;
				if (nrOfLines < 2 || line > nrOfLines)
					break;
			}
		} else {
			while (br.ready()) {
				if (nrOfLines > 0)
					System.out.println(br.readLine());
				line++;
				if (nrOfLines < 2 || line > nrOfLines)
					break;
			}
		}
	}

	public static void print(BufferedReader br, boolean showLineNrs,
			int nrOfLines, int offset) {
		if (nrOfLines < 0)
			throw new RuntimeException(
					"The number of lines must be greater than 0!");
		if (offset < 0)
			throw new RuntimeException(
					"The offset must be greater than 0!");
		int line = 1;
		if (offset == 0) offset = 1;
		try {
			if (showLineNrs) {
				while (br.ready()) {
					if (nrOfLines > 0 && (offset == 0 || line >= offset))
						System.out.println(line + ":\t" + br.readLine());
					line++;
					if (nrOfLines == 0 || line - offset == nrOfLines)
						break;
				}
			} else {
				while (br.ready()) {
					if (nrOfLines > 0 && (offset == 0 || line >= offset))
						System.out.println(br.readLine());
					line++;
					if (nrOfLines == 0 || line - offset == nrOfLines)
						break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader getBufferedReader(File source) throws FileNotFoundException, IOException {
		return new BufferedReader(getInputStreamReader(source));
	}

	public static InputStreamReader getInputStreamReader(File source) throws FileNotFoundException, IOException {
		return new InputStreamReader(getInputStream(source));
	}

	public static BufferedReader getBufferedReader(URL source) throws IOException {
		return new BufferedReader(getInputStreamReader(source));
	}

	public static InputStreamReader getInputStreamReader(URL source) throws IOException {
		return new InputStreamReader(getInputStream(source));
	}

	public static InputStream getInputStream(URL source) throws IOException {
		InputStream is = null;
		if (source.getFile().indexOf(".gz") > 0) {
			is = new GZIPInputStream(source.openStream());
		} else if (source.getFile().indexOf(".Z") > 0) {
			is = new UncompressInputStream(source.openStream());
		} else {
			is = source.openStream();
		}
		return is;
	}

	public static InputStream getInputStream(File source) throws FileNotFoundException, IOException {
		InputStream is = null;
		if (source.getName().indexOf(".gz") > 0) {
			is = new GZIPInputStream(new FileInputStream(source));
		} else if (source.getName().indexOf(".Z") > 0) {
			is = new UncompressInputStream(new FileInputStream(source));
		} else {
			is = new FileInputStream(source);
		}
		return is;
	}
}
