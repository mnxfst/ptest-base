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
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mnxfst.testing.server.exception.ServerConfigurationFailedException;

/**
 * Test case for {@link PTestServerConfigurationParser}
 * @author mnxfst
 * @since 22.03.2012
 */
public class TestPTestServerConfigurationParser {

	@Test
	public void testEvaluateStringWithNullInput() {
		try {
			(new PTestServerConfigurationParser()).evaluateString(null,  null);
			Assert.fail("Invalid input, both parameters contain null values");
		} catch(XPathExpressionException e) {
			//
		}
	}

	@Test
	public void testEvaluateStringWithValidExprAndNullDocument() {
		try {
			PTestServerConfigurationParser p = new PTestServerConfigurationParser();
			p.evaluateString(p.xpathExpressionHostname,  null);
			Assert.fail("Valid expression, invalid document");
		} catch(XPathExpressionException e) {
			//
		}
	}

	@Test
	public void testEvaluateStringWithValidDocumentAndInvalidExpression() throws ParserConfigurationException {
		try {
			PTestServerConfigurationParser p = new PTestServerConfigurationParser();			
			p.evaluateString(null, DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			Assert.fail("Invalid expression, valid document");
		} catch(XPathExpressionException e) {
			//
		}
	}
	
	@Test
	public void testEvaluateStringWithExpressionNotMatchingDocument() throws ParserConfigurationException, XPathExpressionException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();			
		String result = p.evaluateString(p.xpathExpressionHostname, DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertTrue("The result must be empty", result.isEmpty());
	}
	
	@Test
	public void testEvaluateStringWithHostnameExpression() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><hostname>testhost</hostname></ptest-server>".getBytes()));
		String result = p.evaluateString(p.xpathExpressionHostname, document);
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertFalse("The result must not be empty", result.isEmpty());
		Assert.assertEquals("The result must be 'testhost'", "testhost", result);
	}
	
	@Test
	public void testEvaluateIntegerWithPortExpression() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><port>123</port></ptest-server>".getBytes()));
		Integer result = p.evaluateInteger(p.xpathExpressionPort, document);
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertEquals("The result must be '123'", 123, result.intValue());
	}
	
	@Test
	public void testEvaluateIntegerWithPortExpressionAndInvalidValue() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><port>123-test</port></ptest-server>".getBytes()));
		try {
			p.evaluateInteger(p.xpathExpressionPort, document);
			Assert.fail("Invalid value provided as port");
		} catch(XPathExpressionException e ) {
			//
		}
	}
	
	@Test
	public void testEvaluateIntegerWithSocketPoolSizeExpression() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><socketPoolSize>321</socketPoolSize></ptest-server>".getBytes()));
		Integer result = p.evaluateInteger(p.xpathExpressionSocketPoolSize, document);
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertEquals("The result must be '321'", 321, result.intValue());
	}
	
	@Test
	public void testEvaluateIntegerWithSocketPoolSizeExpressionValueNotFound() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server></ptest-server>".getBytes()));
		Integer result = p.evaluateInteger(p.xpathExpressionSocketPoolSize, document);
		Assert.assertNull("The result must be null", result);
	}

	@Test
	public void testEvaluateNodeListWithNullInput() {
		try {
			(new PTestServerConfigurationParser()).evaluateNodeList(null, null);
			Assert.fail("Expression is null and document is null");
		} catch (XPathExpressionException e) {
		}
	}

	@Test
	public void testEvaluateNodeListWithNullDocumentInput() {
		try {
			PTestServerConfigurationParser p = new PTestServerConfigurationParser();
			p.evaluateNodeList(p.xpathExpressionAllHandlerSettings, null);
			Assert.fail("Expression is not  null and document is null");
		} catch (XPathExpressionException e) {
		}
	}

	@Test
	public void testEvaluateNodeListWithNullExpressionInput() throws ParserConfigurationException {
		try {
			PTestServerConfigurationParser p = new PTestServerConfigurationParser();
			p.evaluateNodeList(null, DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			Assert.fail("Expression null and document not null");
		} catch (XPathExpressionException e) {
		}
	}
	
	@Test	
	public void testEvaluateNodeListWithValidInput() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><contextHandlers><handler><path>/test</path><class>java.lang.String</class></handler><handler><path>/anotherTest</path><class>java.lang.Integer</class></handler></contextHandlers></ptest-server>".getBytes()));
		NodeList result = p.evaluateNodeList(p.xpathExpressionAllHandlerSettings, document);
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertEquals("The result length must be 2", 2, result.getLength());
	}
	
	@Test	
	public void testEvaluateNodeListAndCheckForHandlers() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		PTestServerConfigurationParser p = new PTestServerConfigurationParser();		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<ptest-server><contextHandlers><handler><path>/test</path><class>java.lang.String</class></handler><handler><path>/anotherTest</path><class>java.lang.Integer</class></handler></contextHandlers></ptest-server>".getBytes()));
		NodeList result = p.evaluateNodeList(p.xpathExpressionAllHandlerSettings, document);
		Assert.assertNotNull("The result must not be null", result);
		Assert.assertEquals("The result length must be 2", 2, result.getLength());
		
		for(int i = 0; i < result.getLength(); i++) {
			Node node = result.item(i);
			String path = p.evaluateString(p.xpathExpressionHandlerPath, node);
			String clazz = p.evaluateString(p.xpathExpressionHandlerClass, node);
			
			if(path != null && path.equals("/test"))
				Assert.assertEquals("The type must be java.lang.String", String.class.getName(), clazz);
			else if(path != null && path.equals("/anotherTest"))
				Assert.assertEquals("The type must be java.lang.Integer", Integer.class.getName(), clazz);
			else
				Assert.fail("Invalid path '"+path+"'");
		}
	}
	
	@Test
	public void testParserServeConfigurationWithNullFilename() {
	
		try {
			String filename = null;
			new PTestServerConfigurationParser().parseServerConfiguration(filename);
			Assert.fail("Missing required file name");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithEmptyFilename() {
	
		try {
			new PTestServerConfigurationParser().parseServerConfiguration("");
			Assert.fail("Missing required file name");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithInvalidFilename() {
	
		try {
			new PTestServerConfigurationParser().parseServerConfiguration("wtf-no-such-file");
			Assert.fail("Invalid file name");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithNullArray() {
	
		try {
			byte[] test = null;
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithEmptyArray() {
	
		try {
			byte[] test = new byte[0];
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingNoXML() {
	
		try {
			byte[] test = "this is by far no valid xml".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingABaseXMLDoc() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnEmptyXMLDoc() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test/>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocMissingHostname() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server/>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
			//
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocHostnameEmpty() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>   </hostname></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocMissingPort() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocPortEmpty() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port></port></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocPortInvalidSpaces() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>     </port></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLDocPortInvalid() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>dsada</port></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLSocketSizeMissing() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLSocketSizeEmpty() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize/></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLSocketSizeInvalidSpaces() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>    </socketPoolSize></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLSocketSizeInvalidValue() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>dsad</socketPoolSize></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLMissingCtxHandlers() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {

		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLEmptyCtxHandlers() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers/></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerMissingAll() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler/></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerEmptyPath() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path/></handler></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerPathWithSpaces() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path>   </path></handler></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerPathMissingClass() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path>/test</path></handler></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerPathEmptyClass() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path>/test</path><class/></handler></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnXMLCtxHandlerPathClassWithSpaces() {
	
		try {
			byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path>/test</path><class>   </class></handler></contextHandlers></ptest-server>".getBytes();
			new PTestServerConfigurationParser().parseServerConfiguration(test);
			Assert.fail("Invalid file contents");
		} catch(ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testParserServeConfigurationWithArrayHavingAnValidXML() throws ServerConfigurationFailedException {
		byte[] test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ptest-server><hostname>test</hostname><port>123</port><socketPoolSize>321</socketPoolSize><contextHandlers><handler><path>/test</path><class>java.lang.String</class></handler><handler><path>/anotherTest</path><class>java.lang.Integer</class></handler></contextHandlers></ptest-server>".getBytes();
		PTestServerConfiguration cfg = new PTestServerConfigurationParser().parseServerConfiguration(test);
		Assert.assertNotNull("The configuration object must not be null", cfg);
		Assert.assertEquals("The hostname must be test", "test", cfg.getHostname());
		Assert.assertEquals("The port must be 123", 123, cfg.getPort());
		Assert.assertEquals("The socket pool size must be 321", 321, cfg.getSocketPoolSize());
		Assert.assertEquals("The size of the context handler set must be 2", 2, cfg.getContextHandlerSettings().size());
		
		Set<NameValuePair> ctxSettings = cfg.getContextHandlerSettings("/test");
		Assert.assertNotNull("The settings must not be null", ctxSettings);
		Assert.assertTrue("The element </test, java.lang.String> must be contained", ctxSettings.contains(new BasicNameValuePair("/test", "java.lang.String")));
		Assert.assertFalse("The element </anotherTest, java.lang.Integer> must not be contained", ctxSettings.contains(new BasicNameValuePair("/anotherTest", "java.lang.Integer")));
		
		ctxSettings = cfg.getContextHandlerSettings("/anotherTest");
		Assert.assertNotNull("The settings must not be null", ctxSettings);
		Assert.assertFalse("The element </test, java.lang.String> must not be contained", ctxSettings.contains(new BasicNameValuePair("/test", "java.lang.String")));
		Assert.assertTrue("The element </anotherTest, java.lang.Integer> must be contained", ctxSettings.contains(new BasicNameValuePair("/anotherTest", "java.lang.Integer")));
	}

}
