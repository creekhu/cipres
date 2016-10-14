package org.ngbw.sdk.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * Get the String of the path of the current directory
	 *
	 * @return path
	 */
	public static String getCurrentDirectory() {
		File dir1 = new File(".");
		try {
			return dir1.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("Can't get current directory. "
					+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Get the String of the path of the parent directory
	 *
	 * @return path
	 */
	public static String getParentDirectory() {
		File dir1 = new File("..");
		try {
			return dir1.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("Can't get current directory. "
					+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Write a String into the specified File
	 *
	 * @param file
	 * @param string
	 * @return 0 on success
	 * @throws IOException
	 */
	public static int writeFile(File file, String string) throws IOException {
		FileOutputStream out = null;
		PrintStream printstream = null;
		try
		{
			out = new FileOutputStream(file);
			printstream = new PrintStream(out);
			printstream.println(string);
		}
		finally
		{
			if (printstream != null)
				printstream.close();
			else if (out != null)
				out.close();
		}
		return 0;
	}

	public static int writeFile(File file, byte[] data) throws IOException {
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			out.write(data);
		}
		finally
		{
			if (out != null)
				out.close();
		}
		return 0;
	}




	/**
	 * Returns MD5 checksum (32 character hexkey) for the content
	 * of the submitted file.
	 * @param fileName
	 * @return md5chksum
	 */
	public static String getMD5Checksum(String fileName) {
		return StringUtils.getMD5HexString(readFile(fileName));
	}
	
	/**
	 * Method reads a regular, gzipped (.gz) and compressed (.Z) files into the 
	 * returned string
	 *
	 * @param file
	 * @return content
	 */
	public static String readFileAsString(File file) {
		return new String(readFile(file));
	}

	/**
	 * Method reads a regular, gzipped (.gz) and compressed (.Z) files into the 
	 * returned string
	 *
	 * @param fileName
	 * @return file content
	 */
	public static String readFileAsString(String fileName) {
		BufferedReader reader = null;
		if (fileName == null)
			throw new NullPointerException("fileName is null");
		else if (fileName.endsWith(".gz"))
			reader = getGZIPFileReader(fileName);
		else if (fileName.endsWith(".Z"))
			reader = getCompressedFileReader(fileName);
		else if (fileName.endsWith(".zip"))
			throw new RuntimeException(
					"File can contain multiple zip entries - "
							+ "use getZipFileReader() instead");
		else
			reader = getFileReader(fileName);

		StringBuffer fileData = new StringBuffer();
		try {
			while (reader.ready()) {
				fileData.append(reader.readLine() + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (Exception e) {/*ignore*/
				}
		}

		return fileData.toString();
	}

	/**
	 * Method returns a buffered Reader for the submitted file.
	 * Please close the reader after you're done!
	 * 
	 * @param fileName
	 * @return bufferedReader
	 */
	public static BufferedReader getFileReader(String fileName) {
		return new BufferedReader(new InputStreamReader(
				getFileInputStream(fileName)));
	}

	/**
	 * Method returns a buffered Reader for the gzip file.
	 * Please close the reader after you're done!
	 * 
	 * @param fileName
	 * @return bufferedReader
	 */
	public static BufferedReader getCompressedFileReader(String fileName) {
		// use BufferedReader to get one line at a time
		BufferedReader uncompressReader = null;
		try {
			uncompressReader = new BufferedReader(new InputStreamReader(
					new UncompressInputStream(new FileInputStream(fileName))));
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException("The file was not found: "
					+ fnfe.getMessage());
		} catch (IOException ioe) {
			throw new RuntimeException("An IOException occurred: "
					+ ioe.getMessage());
		}
		return uncompressReader;
	}

	/**
	 * Method returns a buffered Reader for the gzip file.
	 * Please close the reader after you're done!
	 * 
	 * @param fileName
	 * @return bufferedReader
	 */
	public static BufferedReader getGZIPFileReader(String fileName) {
		// use BufferedReader to get one line at a time
		BufferedReader gzipReader = null;
		try {
			gzipReader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(fileName))));
		} catch (FileNotFoundException fnfe) {
			throw new RuntimeException("The file was not found: "
					+ fnfe.getMessage());
		} catch (IOException ioe) {
			throw new RuntimeException("An IOException occurred: "
					+ ioe.getMessage());
		}
		return gzipReader;
	}

	/**
	 * Returns a Map that keys a Reader for each of the files in the Zip file.
	 * Please close the reader after you're done!
	 * 
	 * @param fileName
	 * @return bufferedReaderMap
	 */
	public static Map<String, BufferedReader> getZipArchiveReaders(
			String fileName) {
		Map<String, BufferedReader> brs = new HashMap<String, BufferedReader>();
		ZipFile zipFile = null;
		try {
			// ZipFile offers an Enumeration of all the files in the Zip file
			zipFile = new ZipFile(fileName);
			for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				String name = zipEntry.getName();
				// use BufferedReader to get one line at a time
				BufferedReader zipReader = new BufferedReader(
						new InputStreamReader(zipFile.getInputStream(zipEntry)));
				brs.put(name, zipReader);
			}
		} catch (IOException ioe) {
			throw new RuntimeException("An IOException occurred: "
					+ ioe.getMessage());
		}
		return brs;
	}

	/**
	 * Returns a Map that keys an InputStream for each of the files in the Zip file.
	 * Please close the streams after you're done!
	 * 
	 * @param fileName
	 * @return inputSteamMap
	 */
	public static Map<String, InputStream> getZipArchiveInputStreams(
			String fileName) {
		Map<String, InputStream> brs = new HashMap<String, InputStream>();
		ZipFile zipFile = null;
		try {
			// ZipFile offers an Enumeration of all the files in the Zip file
			zipFile = new ZipFile(fileName);
			for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				String name = zipEntry.getName();
				InputStream is = zipFile.getInputStream(zipEntry);
				if (name.endsWith(".Z"))
					is = new UncompressInputStream(is);
				else if (fileName.endsWith(".gz"))
					is = new GZIPInputStream(is);
				brs.put(name, is);
			}
		} catch (IOException ioe) {
			throw new RuntimeException("An IOException occurred: "
					+ ioe.getMessage());
		}
		return brs;
	}

	public static Map<String, byte[]> readZipArchiveInputStreams(String fileName) {
		Map<String, byte[]> brs = new HashMap<String, byte[]>();
		ZipFile zipFile = null;
		try {
			// ZipFile offers an Enumeration of all the files in the Zip file
			zipFile = new ZipFile(fileName);
			for (Enumeration<?> e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				String name = zipEntry.getName();
				InputStream is = zipFile.getInputStream(zipEntry);
				if (name.endsWith(".Z"))
					is = new UncompressInputStream(is);
				else if (fileName.endsWith(".gz"))
					is = new GZIPInputStream(is);
				brs.put(name, readInputStream(is));
			}
		} catch (IOException ioe) {
			throw new RuntimeException("An IOException occurred: "
					+ ioe.getMessage());
		}
		return brs;
	}

	/**
	 * Method reads the submitted file into a byte[].
	 * It will automatically wrap FileInputStreams from gzipped (.gz) 
	 * and compressed (.Z) files in the appropriate  DeflatorStream.
	 * Please close the stream after you're done!
	 * 
	 * @param fileName
	 * @return inputStream
	 */
	public static InputStream getFileInputStream(String fileName) {
		if (fileName == null)
			throw new NullPointerException("File name cannot be null!");
		InputStream is;
		if (fileName.startsWith("http://") || (fileName.startsWith("ftp://"))) {
			try {
				URL url = new URL(fileName);
				is = url.openStream();
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid url " + fileName, e);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read from url " + fileName,
						e);
			}
		} else {
			File file = new File(fileName);
			if (file.exists() == false)
				throw new RuntimeException(file + " does not exist!");
			if (file.canRead() == false)
				throw new RuntimeException(file + " cannot be read!");
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(file + " does not exist!");
			}
		}
		try {
			if (fileName.endsWith(".Z"))
				is = new UncompressInputStream(is);
			else if (fileName.endsWith(".gz"))
				is = new GZIPInputStream(is);
			else if (fileName.endsWith(".zip"))
				throw new RuntimeException(
						"File can contain multiple zip entries");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return is;
	}

	/**
	 * Method reads the submitted file into a byte[].
	 * It will automatically handle regular, gzipped (.gz) 
	 * and compressed (.Z) files
	 * 
	 * @param fileName
	 * @return bytes
	 */
	public static byte[] readFile(String fileName) {
		File file = new File(fileName);
		return readFile(file);
	}

	/**
	 * Method reads the submitted file into a byte[].
	 * It will automatically handle regular, gzipped (.gz) 
	 * and compressed (.Z) files
	 * 
	 * @param file
	 * @return bytes
	 */
	public static byte[] readFile(File file) {
		if (file == null)
			throw new NullPointerException("File is null!");
		if (file.exists() == false)
			throw new NullPointerException(file + " does not exist!");
		if (file.canRead() == false)
			throw new RuntimeException(file + " cannot be read");
		byte[] content = new byte[(int) file.length()];
		InputStream is = getFileInputStream(file.getPath());
		try {
			is.read(content);
		} catch (IOException e) {
			throw new RuntimeException(file + " cannot be read!");
		} 
		return content;
	}

	public static byte[] readInputStream(InputStream is) throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead = 0;
		while (0 < (bytesRead = is.read(buffer))) {
			os.write(buffer, 0, bytesRead);
			buffer = new byte[1024];
		}
		os.flush();
		return os.toByteArray();
	}

	// just a simple implementation when you don't need the complexity of readFileAsString().
	// be sure it's a reasonably small file!
	public static String fileToString(File f) throws Exception
	{
		byte[] bytes = new byte[(int)f.length()];
		FileInputStream fs = null;
		try
		{
			fs = new FileInputStream(f);
			fs.read(bytes);
			return new String(bytes); 
		}
		finally
		{
			if (fs != null)
			{
				fs.close();
			}
		}
	}

	public static String streamToString(InputStream is, int max) throws Exception
	{
		InputStreamReader reader = null;
		try
		{
			reader = new InputStreamReader(is);
			final char[] buffer = new char[max];
			int len = reader.read(buffer, 0, buffer.length);
			if (len > 0)
			{
				return new String(buffer, 0, len);
			}
			throw new Exception("File is empty");
		}
		finally
		{
			if (reader != null) reader.close();
		}
	}

	public static long copy(InputStream input, OutputStream output) throws IOException 
	{
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) 
		{
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static long copy(InputStream input, OutputStream output, long limit) throws Exception 
	{
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int bytesRead = 0;
		int wrote;
		while (-1 != (bytesRead = input.read(buffer))) 
		{
			if (bytesRead + count > limit)
			{
				wrote = (int)((bytesRead + count) - limit);
				output.write(buffer, 0, wrote);
				throw new Exception("InputStream exceedes size limit of " + limit + "bytes");
			}
			wrote = bytesRead;
			output.write(buffer, 0, wrote);
			count += wrote;
		}
		return count;
	}

}
