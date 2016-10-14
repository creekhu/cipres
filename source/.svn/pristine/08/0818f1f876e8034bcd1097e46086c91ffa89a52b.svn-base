/**
 * 
 */
package org.ngbw.sdk.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author hannes
 *
 */
public class XMLHelper {
	
	static DocumentBuilder docBuilder;
	
	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Document parseXML(InputSource is) {
		try {
			Document document = docBuilder.parse(is);
			return document;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Document parseXML(String input) {
		return parseXML(toInputSource(input));
	}
	
	public static Document parseXML(InputStream input) {
		return parseXML(toInputSource(input));
	}
	
	public static Document parseXML(URL input) {
		return parseXML(toInputSource(input));
	}
	
	public static Document parseXML(File input) {
		return parseXML(toInputSource(input));
	}

	public static boolean validateNodeName(Node node, String nodeName) {
		if (node == null)
			throw new NullPointerException("Node cannot be null!");
		if (nodeName == null)
			throw new NullPointerException("Nodename cannot be null!");
		return nodeName.equals(node.getNodeName());
	}
	
	public static NodeList findNodes(Document document, String nodeName) {
		if (document == null)
			throw new NullPointerException("Document cannot be null!");
		if (nodeName == null)
			throw new NullPointerException("Nodename cannot be null!");
		return document.getElementsByTagName(nodeName);
	}
	
	public static NodeList findNodes(Element parentNode, String nodeName) {
		if (parentNode == null)
			throw new NullPointerException("Parent Node cannot be null!");
		if (nodeName == null)
			throw new NullPointerException("Nodename cannot be null!");
		return parentNode.getElementsByTagName(nodeName);
	}
	
	public static Element findNode(Element parentNode, String nodeName) {
		if (parentNode == null)
			throw new NullPointerException("Parent Node cannot be null!");
		if (nodeName == null)
			throw new NullPointerException("Nodename cannot be null!");
		NodeList nodes = parentNode.getElementsByTagName(nodeName);
		if(nodes == null || nodes.getLength() == 0)
			return null;
		if(nodes.getLength() > 1)
			throw new RuntimeException(nodes.getLength() + " nodes found where only 1 was expected");
		return (Element) nodes.item(0);
	}
	
	public static Element findNode(Document document, String nodeName) {
		if (document == null)
			throw new NullPointerException("Document cannot be null!");
		if (nodeName == null)
			throw new NullPointerException("Nodename cannot be null!");
		NodeList nodes = document.getElementsByTagName(nodeName);
		if(nodes == null || nodes.getLength() == 0)
			return null;
		if(nodes.getLength() > 1)
			throw new RuntimeException(nodes.getLength() + " nodes found where only 1 was expected");
		return (Element) nodes.item(0);
	}
	
	private static InputSource toInputSource(String xml) {
		InputSource iso = new InputSource(new StringReader(xml));
		return iso;
	}
	
	private static InputSource toInputSource(File xmlFile) {
		InputSource iso;
		try {
			iso = new InputSource(new FileReader(xmlFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return iso;
	}
	
	private static InputSource toInputSource(URL xmlFile) {
		InputSource iso;
		try {
			iso = new InputSource(xmlFile.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return iso;
	}
	
	private static InputSource toInputSource(InputStream is) {
		InputSource iso;
		iso = new InputSource(is);
		return iso;
	}
}
