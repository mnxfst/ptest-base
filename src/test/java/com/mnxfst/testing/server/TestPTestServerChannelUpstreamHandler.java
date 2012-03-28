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

package com.mnxfst.testing.server;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

import com.mnxfst.testing.server.cfg.PTestNameValuePair;
import com.mnxfst.testing.server.cfg.PTestServerConfiguration;
import com.mnxfst.testing.server.exception.ServerConfigurationFailedException;

/**
 * Test case for {@link PTestServerChannelUpstreamHandler}
 * @author mnxfst
 * @since 28.03.2012
 */
public class TestPTestServerChannelUpstreamHandler {

	@Test
	public void testConstructorWithInvalidServerContextSetting() throws ServerConfigurationFailedException {
		try {
			new PTestServerChannelUpstreamHandler(null);
			Assert.fail("Invalid server context setting");
		} catch (ServerConfigurationFailedException e) {
		}
		try {
			new PTestServerChannelUpstreamHandler(new PTestServerConfiguration());
			Assert.fail("Invalid server context setting");
		} catch (ServerConfigurationFailedException e) {
		}
		PTestServerConfiguration cfg = new PTestServerConfiguration();
		cfg.setPort(1);
		cfg.setSocketPoolSize(1);
		cfg.setHostname("localhost");
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Missing context handlers");
		} catch (ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testConstructorWithInvalidHostname() throws ServerConfigurationFailedException {
		PTestServerConfiguration cfg = new PTestServerConfiguration();
		cfg.addContextHandlerSetting("/test", new BasicNameValuePair("test", "test"));
		cfg.setPort(1);
		cfg.setSocketPoolSize(1);
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Hostname is null");
		} catch(ServerConfigurationFailedException e) {
		}
		cfg.setHostname("");
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Hostname is empty");
		} catch(ServerConfigurationFailedException e) {
		}
		cfg.setHostname("    ");
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Hostname contains nothing but spaces");
		} catch(ServerConfigurationFailedException e) {
		}
		cfg.setHostname("    \r\r\n\t\t");
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Hostname contains spaces, carriage return and tab chars but nothing else");
		} catch(ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testConstructorWithInvalidPort() throws ServerConfigurationFailedException {
		PTestServerConfiguration cfg = new PTestServerConfiguration();
		cfg.addContextHandlerSetting("/test", new BasicNameValuePair("test", "test"));
		cfg.setHostname("localhost");
		cfg.setSocketPoolSize(1);
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Port must not be -1");
		} catch (ServerConfigurationFailedException e) {
		}
	}
	
	@Test
	public void testConstructorWithInvalidSocketPoolSize() throws ServerConfigurationFailedException {
		PTestServerConfiguration cfg = new PTestServerConfiguration();
		cfg.setHostname("localhost");
		cfg.setPort(1);
		cfg.addContextHandlerSetting("/test", new BasicNameValuePair("test", "test"));
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Pool size must not be -1");
		} catch (ServerConfigurationFailedException e) {
		}
		cfg.setSocketPoolSize(0);
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Pool size must not be 0");
		} catch (ServerConfigurationFailedException e) {
		}
	}

	@Test
	public void testConstructorWithInvalidHandlerClass() throws ServerConfigurationFailedException {
		PTestServerConfiguration cfg = new PTestServerConfiguration();
		cfg.setHostname("localhost");
		cfg.setPort(1);
		cfg.addContextHandlerSetting("/test", new PTestNameValuePair("test", "test"));
		cfg.setSocketPoolSize(1);
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("No class provided for context");
		} catch(ServerConfigurationFailedException e) {
			//
		}
		cfg.addContextHandlerSetting("/test", new PTestNameValuePair("class", "wtf"));
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("No valid class provided for context");
		} catch(ServerConfigurationFailedException e) {
			//
		}
		cfg.addContextHandlerSetting("/test", new PTestNameValuePair("class", "java.lang.String"));
		try {
			new PTestServerChannelUpstreamHandler(cfg);
			Assert.fail("Valid class but it does not implement the required interface");
		} catch(ServerConfigurationFailedException e) {
		}
		cfg.addContextHandlerSetting("/test", new PTestNameValuePair("class", SamplePTestServerContextRequestHandler.class.getName()));
		new PTestServerChannelUpstreamHandler(cfg);		
	}
}
