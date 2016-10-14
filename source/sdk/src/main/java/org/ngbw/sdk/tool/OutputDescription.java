/*
	OutputDescription.java
*/
package org.ngbw.sdk.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.TaskOutputSourceDocument;
import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.tool.Tool;

/*
	Represents a tool's possible output files.  Each OutputDescription corresponds to a pisexml parameter
	of type "Results".  Info from the tool registry is also used to get the output's type info. 
	FilePattern can be a single name, a whitespace-separated list of file extensions, as in "*.one *.two *.three", 
	or a single wild card character, "*".

	The set of OutputDescription objects for a tool can be serialized to a string, where each parameter name
	and corresponding OutputDescription is enclosed in parenthesis.  Here's an example for a hypothetical
	tool that has two "Results" parameters:
		(just_tree_files, *.tre, UNKNOWN, UNKNOWN, UNKNOWN), (all_files, *, UNKNOWN, UNKNOWN, UNKNOWN)

*/
class OutputDescription 
{
	private static final Log log= LogFactory.getLog(OutputDescription.class.getName());
	public final String filePattern;
	public final EntityType entityType;
	public final DataType dataType;
	public final DataFormat format;
	public static final List<String> systemFiles = new ArrayList<String>();
	static 
	{
		String systemFilesProperty = Workbench.getInstance().getProperties().getProperty("job.system.files");
		if (systemFilesProperty != null)
		{
			String[] tmp = systemFilesProperty.split(",");
			for (String s : tmp)
			{
				systemFiles.add(s.trim());
			}
		}
	}

	public OutputDescription(String filePattern, String entityType, String dataType, String format)
	{
		this.filePattern = filePattern;
		this.entityType = EntityType.valueOf(entityType);
		this.dataType = DataType.valueOf(dataType);
		this.format = DataFormat.valueOf(format);
	}

	public static Map<String, OutputDescription> parse(String description) throws PropertyException
	{
		Map<String, OutputDescription> results = new TreeMap<String, OutputDescription>();
		if (description != null) 
		{
			String[] descriptions = description.split("\\s*\\)\\s*,\\s*\\(\\s*");

			// trim the opening parentheses from the first description, and the closing parentheses from the last
			int lastOffset = descriptions.length - 1;

			descriptions[0] = descriptions[0].replaceFirst("\\s*\\(\\s*", "");
			descriptions[lastOffset] = descriptions[lastOffset].replaceFirst("\\s*\\)\\s*", "");

			for (int i = 0 ; i < descriptions.length ; i += 1) 
			{
				String[] values = descriptions[i].split("\\s*,\\s*");

				if (values.length != 5)
					throw new PropertyException("invalid format for output description " + String.valueOf(i + 1) + ": " + descriptions[i]);

				results.put(values[0], new OutputDescription(values[1], values[2], values[3], values[4]));
			}
		}
		return results;
	}


	public static String serialize(Map<String, String> outputFileMap, ToolConfig toolConfig)
	{
		StringBuilder outputDescr = new StringBuilder();
		Iterator<Map.Entry<String, String>> entries = outputFileMap.entrySet().iterator();
		if (entries.hasNext()) 
		{
			Map.Entry<String, String> entry = entries.next();
			createOutputDescription(outputDescr, entry.getKey(), entry.getValue(), toolConfig);
			while (entries.hasNext()) {
				entry = entries.next();
				outputDescr.append(",");
				createOutputDescription(outputDescr, entry.getKey(), entry.getValue(), toolConfig);
			}
		}
		return outputDescr.toString();
	}

	private static void createOutputDescription(StringBuilder descriptions, String param, String pattern, ToolConfig toolConfig)
	{
		SourceDocumentType docType = toolConfig.getSourceDocumentType(param);
		EntityType entity;
		DataType data;
		DataFormat format;

		if (docType != null) {
			entity = docType.getEntityType();
			data = docType.getDataType();
			format = docType.getDataFormat();
		}
		else {
			entity = EntityType.UNKNOWN;
			data = DataType.UNKNOWN;
			format = DataFormat.UNKNOWN;
		}

		descriptions.append("(");
		descriptions.append(param);
		descriptions.append(",");
		descriptions.append(pattern);
		descriptions.append(",");
		descriptions.append(entity);
		descriptions.append(",");
		descriptions.append(data);
		descriptions.append(",");
		descriptions.append(format);
		descriptions.append(")");
	}

	public static List<String> filter(List<String> filenames, OutputDescription description)
	{
		List<String> filesThatMatch= new ArrayList<String>();
		String[] patterns = description.filePattern.split("\\s+");

		for (String filename : filenames)
		{
			// See if filename matches any of the patterns.
			// The description is either a single filename or a space separated list of patterns, like "*.jpg *.nex *.aln"
			for (int i = 0 ; i < patterns.length ; i += 1) 
			{
				Pattern namePattern = Pattern.compile(patterns[i].replaceAll("\\.", "\\\\\\.").replaceAll("\\*", "\\.\\*"));
				if (namePattern.matcher(filename).matches()) 
				{
					if (systemFiles.contains(filename))
					{
						;
					} else
					{
						filesThatMatch.add(filename);
					}
					break;
				}
			}
		}
		return filesThatMatch;
	}
}


