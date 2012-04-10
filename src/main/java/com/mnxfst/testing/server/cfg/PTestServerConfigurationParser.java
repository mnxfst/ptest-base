/*
 *  The ptest framework provides you with a performance test utility
 *  Copyright (C) 2012  Christian Kreutzfeldt <mnxfst@googlemail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mnxfst.testing.server.cfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mnxfst.testing.server.exception.ServerConfigurationFailedException;

/**
 * Parses the configuration file of a ptest server instance
 * @author mnxfst
 * @since 21.03.2012
 */
public class PTestServerConfigurationParser {

	private static final String XML_ROOT = "ptest-server";
	
	private static final String XPATH_HOSTNAME = "/ptest-server/hostname";
	private static final String XPATH_PORT = "/ptest-server/port";
	private static final String XPATH_SOCKET_POOL_SIZE = "/ptest-server/socketPoolSize";
	private static final String XPATH_ALL_HANDLER_SETTINGS = "/ptest-server/contextHandlers/*";
	private static final String XPATH_HANDLER_PATH = "path";
	private static final String XPATH_HANDLER_CLASS = "class";
	
	
	protected final XPathExpression xpathExpressionHostname;
	protected final XPathExpression xpathExpressionPort;
	protected final XPathExpression xpathExpressionSocketPoolSize;
	protected final XPathExpression xpathExpressionAllHandlerSettings;
	protected final XPathExpression xpathExpressionHandlerPath;
	protected final XPathExpression xpathExpressionHandlerClass;
	
	/**
	 * Initializes the instance
	 */
	public PTestServerConfigurationParser() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			xpathExpressionHostname = xpath.compile(XPATH_HOSTNAME);
			xpathExpressionPort = xpath.compile(XPATH_PORT);
			xpathExpressionSocketPoolSize = xpath.compile(XPATH_SOCKET_POOL_SIZE);
			xpathExpressionAllHandlerSettings = xpath.compile(XPATH_ALL_HANDLER_SETTINGS);
			xpathExpressionHandlerClass = xpath.compile(XPATH_HANDLER_CLASS);
			xpathExpressionHandlerPath = xpath.compile(XPATH_HANDLER_PATH);
		} catch(XPathExpressionException e) {
			throw new RuntimeException("Failed to create xpath expressions from preconfigured pattern. Error: " + e.getMessage());
		}		
	}
	
	/**
	 * Parses the contents of the referenced configuration file into an object
	 * @param filename
	 * @return
	 * @throws ServerConfigurationFailedException
	 */
	public PTestServerConfiguration parseServerConfiguration(String filename) throws ServerConfigurationFailedException {
		
		if(filename == null || filename.isEmpty())
			throw new ServerConfigurationFailedException("Missing required config filename");
		
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try {
			FileInputStream fIn = new FileInputStream(filename);
			int c = 0;
			while((c = fIn.read()) != -1)
				bOut.write(c);
		} catch(FileNotFoundException e) {
			throw new ServerConfigurationFailedException("Referenced configuration file does not exist");
		} catch(IOException e) {
			throw new ServerConfigurationFailedException("Failed to read contents from configuration file '"+filename+"'. Error: " + e.getMessage());
		}
		
		return parseServerConfiguration(bOut.toByteArray());
	}

	/**
	 * Parses the contents of the provided configuration file contents into an object
	 * @param configFileContents
	 * @return
	 * @throws ServerConfigurationFailedException
	 */
	public PTestServerConfiguration parseServerConfiguration(byte[] configFileContents) throws ServerConfigurationFailedException {
		
		if(configFileContents == null || configFileContents.length < 1) 
			throw new ServerConfigurationFailedException("Missing required config file contents");
		
		try {
			// parse the provided byte array into a valid document and check resul 
			Document cfgDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(configFileContents));		
			if(cfgDocument == null)
				throw new ServerConfigurationFailedException("Provided contents could not be parsed into a valid document");
			Node rootNode = cfgDocument.getFirstChild();
			if(rootNode == null)
				throw new ServerConfigurationFailedException("The provided document does not have a valid root node");
			if(rootNode.getNodeName() == null || rootNode.getNodeName().isEmpty())
				throw new ServerConfigurationFailedException("The root node of the provided document does not have a valid name");
			if(!rootNode.getNodeName().equalsIgnoreCase(XML_ROOT))
				throw new ServerConfigurationFailedException("The provided document does not contain a valid configuration since it misses the required root node");
			
		
			// extract hostname, port and socket pool size from configuration
			String hostname = evaluateString(xpathExpressionHostname, cfgDocument);
			if(hostname == null || hostname.trim().isEmpty())
				throw new ServerConfigurationFailedException("Missing the required settings for 'hostname'");
			
			Integer port = evaluateInteger(xpathExpressionPort, cfgDocument);
			if(port == null)
				throw new ServerConfigurationFailedException("Missing the required settings for 'port'");
			
			Integer socketPoolSize = evaluateInteger(xpathExpressionSocketPoolSize, cfgDocument);
			if(socketPoolSize == null)
				throw new ServerConfigurationFailedException("Missing the required settings for 'socketPoolSize'");
			
			// extract handler configurations and validate for emptiness and null
			NodeList handlerCfgs = evaluateNodeList(xpathExpressionAllHandlerSettings, cfgDocument);
			if(handlerCfgs == null || handlerCfgs.getLength() < 1)
				throw new ServerConfigurationFailedException("No context handlers defined. Please provide at least one context");
			
			PTestServerConfiguration serverConfiguration = new PTestServerConfiguration(hostname, port, socketPoolSize);
			
			// step through handler configurations and extract path and class
			for(int i = 0; i < handlerCfgs.getLength(); i++) {
				Node handlerCfgNode = handlerCfgs.item(i);
				String ctxPath = evaluateString(xpathExpressionHandlerPath, handlerCfgNode);
				String ctxClass = evaluateString(xpathExpressionHandlerClass, handlerCfgNode);
				
				// validate path and class - invalid values provided for any of them leads to an exception
				if(ctxPath == null || ctxPath.trim().isEmpty())
					throw new ServerConfigurationFailedException("Invalid context path found in configuration file contents");
				if(ctxClass == null || ctxClass.trim().isEmpty())
					throw new ServerConfigurationFailedException("Invalid handler class found in configuration file contents");
						
				// TODO additional configuration				
				serverConfiguration.addContextHandlerSetting(ctxPath, new BasicNameValuePair("class", ctxClass));				
			}
			
			return serverConfiguration;
		} catch(IOException e) {
			throw new ServerConfigurationFailedException("Failed to read contents from provided configuration. Error: " + e.getMessage());
		} catch (SAXException e) {
			throw new ServerConfigurationFailedException("Failed to parse contents from provided configuration. Error: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new ServerConfigurationFailedException("Failed to parse contents from provided configuration. Error: " + e.getMessage());
		} catch (XPathExpressionException e) {
			throw new ServerConfigurationFailedException("Failed to evaluate xpath expression on configuration. Error: " + e.getMessage());
		}
	}
	
	///////////////////////////////////////////// protected methods /////////////////////////////////////////////
	
	/**
	 * Evaluates the given expression on the referenced document into a result object of type string
	 * @param expression
	 * @param document
	 * @return
	 * @throws XPathExpressionException
	 */
	protected String evaluateString(XPathExpression expression, Object document) throws XPathExpressionException {
		if(expression == null)
			throw new XPathExpressionException("Null is not a valid expression");
		if(document == null)
			throw new XPathExpressionException("An xpath expression must not be applied to a NULL document");
		
		return (String)expression.evaluate(document, XPathConstants.STRING);
	}
	
	/**
	 * Evaluates the given expression on the referenced document and returns a result object of type string
	 * @param expression
	 * @param document
	 * @return
	 * @throws XPathExpressionException
	 */
	protected Integer evaluateInteger(XPathExpression expression, Object document) throws XPathExpressionException {

		String value = evaluateString(expression, document);
		if(value != null && !value.isEmpty()) {
			try {
				return Integer.parseInt(value);
			} catch(NumberFormatException e) {
				throw new XPathExpressionException("Failed to parse numerical value from evaluate string '"+value+"'");
			}
		}
		return null;
	}
	
	/**
	 * Evaluates the given expression on the referenced document and returns a result object of type {@link NodeList} 
	 * @param expression
	 * @param document
	 * @return
	 * @throws XPathExpressionException
	 */
	protected NodeList evaluateNodeList(XPathExpression expression, Object document) throws XPathExpressionException {
		if(expression == null)
			throw new XPathExpressionException("Null is not a valid expression");
		if(document == null)
			throw new XPathExpressionException("An xpath expression must not be applied to a NULL document");

		return (NodeList)expression.evaluate(document, XPathConstants.NODESET);
	}
	
}
