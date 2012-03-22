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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;

/**
 * Contains all information provided through the configuration file
 * 
 * @author mnxfst
 * @since 21.03.2012
 */
public class PTestServerConfiguration implements Serializable {

	private static final long serialVersionUID = -4878486938828728472L;

	private String hostname = null;
	private int port = -1;
	private int socketPoolSize = -1;

	private Map<String, Set<NameValuePair>> contextHandlerSettings = new HashMap<String, Set<NameValuePair>>();

	public PTestServerConfiguration() {
	}

	public PTestServerConfiguration(String hostname, int port,
			int socketPoolSize) {
		this.hostname = hostname;
		this.port = port;
		this.socketPoolSize = socketPoolSize;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname
	 *            the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the socketPoolSize
	 */
	public int getSocketPoolSize() {
		return socketPoolSize;
	}

	/**
	 * @param socketPoolSize
	 *            the socketPoolSize to set
	 */
	public void setSocketPoolSize(int socketPoolSize) {
		this.socketPoolSize = socketPoolSize;
	}

	/**
	 * @return the contextHandlerSettings
	 */
	public Map<String, Set<NameValuePair>> getContextHandlerSettings() {
		return contextHandlerSettings;
	}

	/**
	 * @param contextHandlerSettings
	 *            the contextHandlerSettings to set
	 */
	public void setContextHandlerSettings(
			Map<String, Set<NameValuePair>> contextHandlerSettings) {
		this.contextHandlerSettings = contextHandlerSettings;
	}

	/**
	 * Adds a specific key/value pair to the selected context handler
	 * 
	 * @param ctxHandler
	 * @param keyValue
	 */
	public void addContextHandlerSetting(String ctxPath,
			NameValuePair keyValue) {

		Set<NameValuePair> settings = contextHandlerSettings.get(ctxPath);
		if (settings == null)
			settings = new HashSet<NameValuePair>();
		settings.add(keyValue);
		contextHandlerSettings.put(ctxPath, settings);
	}

	public Set<NameValuePair> getContextHandlerSettings(String ctxPath) {
		return contextHandlerSettings.get(ctxPath);
	}
}
