package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
* This class provides methods for validating an xml document.
* @author Terri Liebowitz<br />
*/

public class XMLValidation
{
	String documentName;
	String displayName;
	XMLErrorHandler eh;

	public XMLValidation(String documentName)
	{
		this.documentName = displayName = documentName;
	}

	public XMLValidation(String documentName, String displayName)
	{
		this.documentName = documentName;
		this.displayName = displayName;
	}

	public boolean isValidInternalDTD() throws Exception
	{
		eh = new XMLErrorHandler(displayName);
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);

			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(eh);
			reader.parse(new InputSource(documentName));
			return eh.errorCount == 0;
		}
		catch (SAXParseException e)
		{
			return false;
		}
	}


	public boolean isWellFormed() throws Exception
	{
		eh = new XMLErrorHandler(displayName);
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(eh);
			reader.parse(new InputSource(documentName));
			return eh.errorCount == 0;
		}
		catch (SAXParseException e)
		{
			return false;
		}
	}

	public ArrayList<String> getErrorMessages()
	{
		return eh.errorMessages;
	}

	public static void main(String args[]) throws Exception
	{
		if (args.length < 1)
		{
			usage();
		}
		XMLValidation validator = new XMLValidation(args[0], (new File(args[0])).getName());
		boolean retval = validator.isValidInternalDTD();
		System.out.println("retval is " + retval);
		if (retval == false)
		{
			for (String msg : validator.getErrorMessages())
			{
				System.out.println(msg);
			}
		}
	}

	private static void usage()
	{
		System.out.printf("Command line argument is missing\n");
		System.exit(-1);
	}
}

class  XMLErrorHandler implements ErrorHandler
{
	String displayName;
	int errorCount;
	ArrayList<String> errorMessages = new ArrayList<String>();

	public XMLErrorHandler() {}
	public XMLErrorHandler(String displayName) { this.displayName = displayName; }

	public void warning(SAXParseException e) 
	{
		log(e);
	}
	public void error(SAXParseException e)
	{
		errorCount++;
		log(e);
	}
	public void fatalError(SAXParseException e)
	{
		errorCount++;
		log(e);
	}

	public void log(SAXParseException e)
	{
		StringBuffer sb = new StringBuffer("Document=");
		sb.append(displayName == null ? e.getSystemId() : displayName);

		sb.append(", line=");
		sb.append(e.getLineNumber());

		sb.append(", col=");
		sb.append(e.getColumnNumber());

		sb.append(" :");
		sb.append(e.getMessage());

		errorMessages.add(sb.toString());
	}
}
