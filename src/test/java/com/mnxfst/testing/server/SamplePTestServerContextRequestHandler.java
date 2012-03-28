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

import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.mnxfst.testing.server.cfg.PTestServerConfiguration;
import com.mnxfst.testing.server.exception.ContextInitializationFailedException;

/**
 * Just does nothing but must be used for test cases
 * @author mnxfst
 * @since 28.03.2012
 */
public class SamplePTestServerContextRequestHandler implements
		PTestServerContextRequestHandler {

	/**
	 * @see com.mnxfst.testing.server.PTestServerContextRequestHandler#initialize(com.mnxfst.testing.server.cfg.PTestServerConfiguration)
	 */
	public void initialize(PTestServerConfiguration properties)
			throws ContextInitializationFailedException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.mnxfst.testing.server.PTestServerContextRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, java.util.Map, boolean, org.jboss.netty.channel.MessageEvent)
	 */
	public void processRequest(HttpRequest httpRequest,
			Map<String, List<String>> requestParameters, boolean keepAlive,
			MessageEvent event) {
		// TODO Auto-generated method stub

	}

}
