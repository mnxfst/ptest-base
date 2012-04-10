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

package com.mnxfst.testing.server.handler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.mnxfst.testing.server.PTestServerContextRequestHandler;
import com.mnxfst.testing.server.PTestServerResponseBuilder;
import com.mnxfst.testing.server.cfg.PTestServerConfiguration;
import com.mnxfst.testing.server.exception.ContextInitializationFailedException;

/**
 * 
 * @author mnxfst
 * @since 28.03.2012
 */
public class PTestServerCfgContextHandler implements PTestServerContextRequestHandler {

	private PTestServerConfiguration cfg;
	
	/**
	 * @see com.mnxfst.testing.server.PTestServerContextRequestHandler#initialize(com.mnxfst.testing.server.cfg.PTestServerConfiguration)
	 */
	public void initialize(PTestServerConfiguration properties) throws ContextInitializationFailedException {
		this.cfg = properties;
	}

	/**
	 * @see com.mnxfst.testing.server.PTestServerContextRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, java.util.Map, boolean, org.jboss.netty.channel.MessageEvent)
	 */
	public void processRequest(HttpRequest httpRequest, Map<String, List<String>> requestParameters, boolean keepAlive,	MessageEvent event) {
		
		StringBuffer response = new StringBuffer();
		response.append("<ptestResponse>");
		response.append("<responseCode>").append(PTestServerResponseBuilder.RESPONSE_CODE_OK).append("</responseCode>");
		response.append("<settings>");
		response.append("<hostname>").append(cfg.getHostname()).append("</hostname>");
		response.append("<port>").append(cfg.getPort()).append("</port>");
		response.append("<socketPoolSize>").append(cfg.getSocketPoolSize()).append("</socketPoolSize>");
		response.append("<contextHandlers>");
		for(String contextPath : cfg.getContextHandlerSettings().keySet()) {
			response.append("<context>").append(contextPath).append("</context>");
			response.append("<settings>");
			Set<NameValuePair> settings = cfg.getContextHandlerSettings(contextPath);
			if(settings != null && !settings.isEmpty()) {
				for(NameValuePair nvp : settings) {
					if(nvp != null && nvp.getName() != null && !nvp.getName().trim().isEmpty()) {
						response.append("<key>").append(nvp.getName()).append("</key>");
						response.append("<value>").append(nvp.getValue()).append("</value>");
					}
				}
			}
			response.append("</settings>");			
		}
		response.append("</contextHandlers>");
		response.append("</settings>");
		response.append("</ptestResponse>");
	
		sendResponse(response.toString().getBytes(), keepAlive, event);
	}
	
	/**
	 * Sends a response containing the given message to the calling client
	 * @param responseMessage
	 * @param keepAlive
	 * @param event
	 */
	protected void sendResponse(byte[] responseMessage, boolean keepAlive, MessageEvent event) {
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		
		httpResponse.setContent(ChannelBuffers.copiedBuffer(responseMessage));
		httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		
		if(keepAlive)
			httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.getContent().readableBytes());
		
		ChannelFuture future = event.getChannel().write(httpResponse);
		if(!keepAlive)
			future.addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * Parses out a single int value from the provided list of values. If the result is null, the list did not contain any value
	 * or the value could not be parsed into a integer object   
	 * @param values
	 * @return
	 */
	protected Integer parseSingleIntValue(List<String> values)  {
		
		if(values == null)
			return null;
		
		String tmp = values.get(0);
		if(tmp == null || tmp.isEmpty())
			return null;

		try {
			return Integer.valueOf(values.get(0));
		} catch(NumberFormatException e) {
			
		}
		
		return null;
	}
	

}
