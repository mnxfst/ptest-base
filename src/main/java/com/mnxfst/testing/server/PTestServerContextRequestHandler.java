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
import com.mnxfst.testing.server.exception.RequestProcessingFailedException;

/**
 * Provides a common interface to all handler implementations processing context
 * specific requests
 * 
 * @author mnxfst
 * @since 21.03.2012
 */
public interface PTestServerContextRequestHandler {

	/**
	 * Initializes the context request handler
	 * 
	 * @param properties
	 * @throws ContextInitializationFailedException
	 */
	public void initialize(PTestServerConfiguration properties) throws ContextInitializationFailedException;

	/**
	 * Handles the incoming request according to its implementation. There is no
	 * further error handling provided by the surrounding
	 * {@link PTestServerChannelUpstreamHandler} but must be implemented by the
	 * request handler itself
	 * 
	 * @param httpRequest
	 * @param requestParameters
	 * @param keepAlive
	 * @param event
	 * @throws RequestProcessingFailedException thrown in case a critical error occurred which cannot be handled by simply sending an error response to the caller
	 */
	public void processRequest(HttpRequest httpRequest, Map<String, List<String>> requestParameters, boolean keepAlive, MessageEvent event) throws RequestProcessingFailedException;

}
