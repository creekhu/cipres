package org.ngbw.sdk.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.AsyncProcessRunner;
import org.ngbw.sdk.common.util.FileUtils;
import org.ngbw.sdk.database.SourceDocument;

/*
	Run a program that parses phylogenetic tools input data and returns info
	about the input data as a Properties object.  Throws an exception
	if there's a problem running the external parser, e.g. if the parser
	isn't on the path, has the wrong permissions or has a non-zero
	exit status.

	main method can be used for testing.  Use it as
		sdkrun org.ngbw.sdk.data.ToolInputParser filename [type]
	You must use the absolute pathname of the filename to parse since the current
	directory is changed by sdkrun, so you can for example run on a file in the current
	directory with:
		sdkrun org.ngbw.sdk.data.ToolInputParser `pwd`/filename [type]

*/
public class ToolInputParser
{
	private static final Log log = LogFactory.getLog(ToolInputParser.class.getName());
	public static String parser = "cipres_data_parse.py";
	Map<String, String> properties;

	public ToolInputParser()
	{
	}

	public ToolInputParser parse(SourceDocument doc, String type) throws  Exception
	{
		InputStream is = doc.getDataAsStream();
		parse(is, type);
		is.close();
		return this;
	}

	public ToolInputParser parse(InputStream is, String type) throws  Exception
	{
		AsyncProcessRunner runner = null;
        int exitStatus;
		OutputStream stdin = null;

        try
        {
            String command =  parser;
			if (type != null)
			{
				command += "-t " + type;
			}
			command += " /dev/stdin";
            //log.debug("Running remote command:" + command);

            runner = new AsyncProcessRunner(true);
            runner.start(command);
			stdin = runner.getStdin();

            //log.debug("Writing to remote command's stdin:");
			FileUtils.copy(is, stdin);

            stdin.flush();
            stdin.close();
            stdin = null;
            //log.debug("Waiting for remote to finish.");
            exitStatus = runner.waitForExit();
            if (exitStatus == 0)
            {
                //log.debug("Remote returned exit status 0");
                properties = parseProperties(runner.getStdOut());
				return this;
            } else
            {
                log.error("Returned:" + exitStatus + ". Stdout=" + runner.getStdOut() + " Stderr=" +
                    runner.getStdErr());
				throw new Exception("Error running " + parser); 
            }
        }
        finally
        {
            if (stdin != null)
            {
                try { stdin.close(); } catch (Exception e) {;}
            }
            if (runner != null)
            {
                runner.close();
            }
        }
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	/*
		This property should always be present.  Value may be "unknown"
	*/
	public String getFileType()
	{
		return properties.get("file_type");
	}

	public String getDataType()
	{
		return properties.get("datatype");
	}

	// Returns true only if value is "true" (case ignored)
	public boolean asBoolean(String value)
	{
		return Boolean.parseBoolean(value);
	}

	public int asInt(String value)
	{
		return Integer.parseInt(value);
	}

	private static Map<String, String> parseProperties(String str) throws Exception
	{
		StringReader sr = null;
		try
		{
			sr = new StringReader(str);
			Map<String, String> properties = new HashMap<String, String>();

			// Use a Properties object to load the pairs, then copy to our "properties" map.
			Properties tmpProperties = new Properties();
			tmpProperties.load(sr);

			for (final Entry<Object, Object> entry : tmpProperties.entrySet()) 
			{
			    properties.put((String) entry.getKey(), (String) entry.getValue());
			}


			return properties;
		}
		catch(Exception e)
		{
			sr.close();
			throw e;
		}
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length < 1)
		{
			System.err.println("absolute path of filename to parse is required command line argument");
			System.exit(1);
		}
		String filename = args[0].trim();
		String type = null;
		if (args.length > 1)
		{
			type = args[1].trim();
		}
		System.out.println("filename=" + filename + ", type=" + type);
		FileInputStream is = new FileInputStream(new File(filename));

		ToolInputParser parser = new ToolInputParser();
		Map<String, String> p = parser.parse(is, type).getProperties();
		if (p == null)
		{
			System.out.println("null properties");
		} else
		{
			Properties tmpProperties = new Properties();
			tmpProperties.putAll(p);
			tmpProperties.list(System.out);
		}
		is.close();
	}
}
