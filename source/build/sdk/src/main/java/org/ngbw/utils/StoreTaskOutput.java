/*
 * StoreTaskOutput.java
 */
package org.ngbw.utils;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskOutputSourceDocument;


/**
 *
 * @author Paul Hoover
 *
 */
public class StoreTaskOutput {

	/**
	 * Describes a set of output files from a child process.
	 */
	private static class OutputDescription {

		public final String filePattern;
		public final EntityType entityType;
		public final DataType dataType;
		public final DataFormat format;


		/**
		 * Constructs an object that provides the given characteristics for a file whose name matches
		 * the given pattern. The pattern can be a single name; a whitespace-separated list of file
		 * extensions, as in "*.one *.two *.three"; or a single wild card character, "*".
		 *
		 * @param pattern a string that serves as a pattern for matching file names
		 * @param outputEntityType the <code>EntityType</code> to assign to a file that matches the pattern
		 * @param outputDataType the <code>DataType</code> to assign to a file that matches the pattern
		 * @param outputFormat the <code>DataFormat</code> to assign to a file that matches the pattern
		 */
		public OutputDescription(String pattern, String outputEntityType, String outputDataType, String outputFormat)
		{
			filePattern = pattern;
			entityType = EntityType.valueOf(outputEntityType);
			dataType = DataType.valueOf(outputDataType);
			format = DataFormat.valueOf(outputFormat);
		}
	}

	/**
	 *
	 */
	private static class WildcardFilter implements FilenameFilter {

		private final Pattern m_namePattern;


		/**
		 *
		 * @param fileName
		 */
		public WildcardFilter(String fileName)
		{
			m_namePattern = Pattern.compile(fileName.replaceAll("\\.", "\\\\\\.").replaceAll("\\*", "\\.\\*"));
		}


		/**
		 *
		 * @param dir
		 * @param name
		 * @return
		 */
		public boolean accept(File dir, String name)
		{
			return m_namePattern.matcher(name).matches();
		}
	}


	// public methods


	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
				throw new Exception("usage: StoreTaskOutput dirname");

			String fileSeparator = System.getProperty("file.separator");
			String dirName;

			if (args[0].endsWith(fileSeparator))
				dirName = args[0];
			else
				dirName = args[0] + fileSeparator;

			String fileName = dirName + "_JOBINFO.TXT";
			Long taskId = readTaskId(fileName);

			if (taskId == null)
				throw new Exception("Couldn't find a task id in " + fileName);

			String outputDescr = readOutputDescription(fileName);

			if (outputDescr == null)
				throw new Exception("Couldn't find an output description in " + fileName);

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			storeOutputFiles(taskId, outputDescr, dirName);
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}


	// private methods


	/**
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Long readTaskId(String fileName) throws IOException
	{
		BufferedReader jobInfoFile = new BufferedReader(new FileReader(fileName));

		try {
			String line;

			while ((line = jobInfoFile.readLine()) != null)
			{
				if (line.startsWith("Task ID"))
				{
					String[] str  = line.split("=");
					String tmp = str[1].trim();

					return new Long(tmp);
				}
			}
		}
		finally {
			jobInfoFile.close();
		}

		return null;
	}

	/**
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static String readOutputDescription(String fileName) throws IOException
	{
		BufferedReader jobInfoFile = new BufferedReader(new FileReader(fileName));

		try
		{
			String line;

			while ((line = jobInfoFile.readLine()) != null)
			{
				if (line.startsWith("Output"))
				{
					String[] str  = line.split("=");
					String tmp = str[1].trim();

					return tmp;
				}
			}
		}
		finally
		{
			jobInfoFile.close();
		}

		return null;
	}

	/**
	 *
	 * @param prop
	 * @return
	 * @throws Exception
	 */
	private static Map<String, OutputDescription> parseOutputProp(String prop) throws Exception
	{
		Map<String, OutputDescription> outputDescr = new TreeMap<String, OutputDescription>();
		String[] descriptions = prop.split("\\s*\\)\\s*,\\s*\\(\\s*");

		// trim the opening parentheses from the first description, and the
		// closing parentheses from the last
		int lastOffset = descriptions.length - 1;

		descriptions[0] = descriptions[0].replaceFirst("\\s*\\(\\s*", "");
		descriptions[lastOffset] = descriptions[lastOffset].replaceFirst("\\s*\\)\\s*", "");

		for (int i = 0 ; i < descriptions.length ; i += 1) {
			String[] values = descriptions[i].split("\\s*,\\s*");

			if (values.length != 5)
				throw new Exception("Invalid format for output description " + String.valueOf(i + 1) + ": " + descriptions[i]);

			outputDescr.put(values[0], new OutputDescription(values[1], values[2], values[3], values[4]));
		}

		return outputDescr;
	}

	/**
	 * Stores the files produced by the task child process. Only files that match a pattern from
	 * an <code>OutputDescription</code> object are stored; all other files are ignored.
	 *
	 * @param taskId
	 * @param descr
	 * @param dirName
	 * @throws Exception
	 * @throws Exception
	 */
	private static void storeOutputFiles(long taskId, String descr, String dirName) throws Exception
	{
		Map<String, OutputDescription> outputDescr = parseOutputProp(descr);
		File dir = new File(dirName);
		Task task = new Task(taskId);

		for (Iterator<Map.Entry<String, OutputDescription>> entries = outputDescr.entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<String, OutputDescription> entry = entries.next();
			OutputDescription description = entry.getValue();
			String[] wildcards = description.filePattern.split("\\s+");
			List<TaskOutputSourceDocument> files = new ArrayList<TaskOutputSourceDocument>();

			for (int i = 0 ; i < wildcards.length ; i += 1) {
				String[] names = dir.list(new WildcardFilter(wildcards[i]));

				for (int j = 0 ; j < names.length ; j += 1) {
					try {
						InputStream inStream = new BufferedInputStream(new FileInputStream(dirName + names[j]));

						// Note that we'll retrieve the same file more than once if the tool description
						// specifies a "*" Result and other Results.
						files.add(new TaskOutputSourceDocument(names[j], description.entityType, description.dataType, description.format, inStream, false));
					}
					catch (FileNotFoundException notFoundErr) {
						// do nothing
					}
				}
			}

			String outputParam = entry.getKey();

			if (files.size() > 0) {
				System.out.println("Adding files for output parameter " + outputParam + ":");

				for (Iterator<TaskOutputSourceDocument> elements = files.iterator() ; elements.hasNext() ; )
					System.out.println("    " + elements.next().getName());

				task.output().put(outputParam, files);
			}
			else
				System.out.println("No files found for output parameter " + outputParam);
		}

		task.save();
	}
}
