/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.mnxfst.testing.server.cfg.PTestServerConfiguration;
import com.mnxfst.testing.server.exception.ContextInitializationFailedException;
import com.mnxfst.testing.server.exception.ServerConfigurationFailedException;

/**
 * Implements the handler for incoming requests
 * @author mnxfst
 * @since 20.03.2012
 */
public class PTestServerChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(PTestServerChannelPipelineFactory.class.getName());
	
	private static final String CONTEXT_HANDLER_CLASS_NAME_PROPERTY_KEY = "class";
	
	private PTestServerConfiguration serverContextSettings = null;
	
	private Map<String, PTestServerContextRequestHandler> contextRequestHandlers = new HashMap<String, PTestServerContextRequestHandler>();

	/**
	 * Initializes the pipeline factory instance
	 * @param hostname
	 * @param port
	 * @param socketThreadPoolSize
	 */
	public PTestServerChannelUpstreamHandler(PTestServerConfiguration serverContextSettings) throws ServerConfigurationFailedException {

		// validate provided setting information for valid data
		if(serverContextSettings == null || serverContextSettings.getContextHandlerSettings() == null || serverContextSettings.getContextHandlerSettings().isEmpty())
			throw new ServerConfigurationFailedException("No valid context handler settings found");
		
		if(serverContextSettings.getHostname() == null || serverContextSettings.getHostname().trim().isEmpty())
			throw new ServerConfigurationFailedException("No valid hostname found");
		if(serverContextSettings.getPort() < 0)
			throw new ServerConfigurationFailedException("No valid port found");
		if(serverContextSettings.getSocketPoolSize() < 1)
			throw new ServerConfigurationFailedException("No valid socket thread pool size found");
		
		this.serverContextSettings = serverContextSettings;
		
		// configure the context specifc request handlers
		synchronized (contextRequestHandlers) {
			
			// build log information
			StringBuffer logStr = new StringBuffer();		

			// iterate through context path names 			
			for(Iterator<String> ctxPathNameIterator = serverContextSettings.getContextHandlerSettings().keySet().iterator(); ctxPathNameIterator.hasNext();) {
				
				// get context path and set of key/value pairs containing the context specific settings
				String contextPath = ctxPathNameIterator.next();
				Set<NameValuePair> ctxPathSettings = serverContextSettings.getContextHandlerSettings(contextPath);
				
				// lookup the context handler class
				String contextHandlerClassName = null;
				for(NameValuePair nvp : ctxPathSettings) {
					if(	nvp != null && 
						nvp.getName() != null && nvp.getName().equalsIgnoreCase(CONTEXT_HANDLER_CLASS_NAME_PROPERTY_KEY) &&
						nvp.getValue() != null && !nvp.getValue().trim().isEmpty()) {
						contextHandlerClassName = nvp.getValue().trim();
						break;
					}
				}
				if(contextHandlerClassName == null || contextHandlerClassName.trim().isEmpty())
					throw new ServerConfigurationFailedException("No handler class found for context '"+contextPath+"'");
					
				
				if(logger.isDebugEnabled())
					logger.debug("Attempting to instantiate a handler for context '"+contextPath+"': " + contextHandlerClassName);

				// attempt to instantiate and initialize configured context handler
				try {
					@SuppressWarnings("unchecked")
					Class<? extends PTestServerContextRequestHandler> contextHandlerClazz = (Class<? extends PTestServerContextRequestHandler>) Class.forName(contextHandlerClassName);
					PTestServerContextRequestHandler contextHandler = contextHandlerClazz.newInstance();
					contextHandler.initialize(serverContextSettings);
					contextRequestHandlers.put(contextPath, contextHandler);
				} catch(ClassNotFoundException e) {
					throw new ServerConfigurationFailedException("Context handler class '"+contextHandlerClassName+"' not found");
				} catch(InstantiationException e) {
					throw new ServerConfigurationFailedException("Context handler class '"+contextHandlerClassName+"' could not be instantiated. Error: " + e.getMessage());
				} catch(IllegalAccessException e) {
					throw new ServerConfigurationFailedException("Context handler class '"+contextHandlerClassName+"' could not be accessed. Error: " + e.getMessage());
				} catch (ContextInitializationFailedException e) {
					throw new ServerConfigurationFailedException("Failed to initialize handler class '"+contextHandlerClassName+"' to be used for context '"+contextPath+"'");
				} catch(ClassCastException e) {
					throw new ServerConfigurationFailedException("Provided context handler class '"+contextHandlerClassName+"' does not implement required interface '"+PTestServerContextRequestHandler.class.getName()+"'");
				}
				
				// create log output
				logStr.append("(").append(contextPath).append(", ").append(contextHandlerClassName).append(")");
				if(ctxPathNameIterator.hasNext())
					logStr.append(", ");

			}

			logger.info("consumer[host="+serverContextSettings.getHostname()+", port="+serverContextSettings.getPort()+", socketThreadPoolSize="+serverContextSettings.getSocketPoolSize()+", consumers=["+logStr.toString()+"]]");
		}
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {

		/////////////////////////////////////// PRE-PRODUCTION ///////////////////////////////////////
		
		// extract http request from incoming message, get keep alive attribute as it will be transferred to response and decode query string 		
		HttpRequest httpRequest = (HttpRequest)event.getMessage();
		
		boolean keepAlive = HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(httpRequest.getHeader(HttpHeaders.Names.CONNECTION));		
		QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.getUri());

		// fetch query parameters and handle post request parameters as well
		Map<String, List<String>> queryParams = decoder.getParameters();		
		if(httpRequest.getMethod() == HttpMethod.POST) {
			decoder = new QueryStringDecoder("?" + httpRequest.getContent().toString(CharsetUtil.UTF_8));
			queryParams.putAll(decoder.getParameters());
		}

		/////////////////////////////////////// END: PRE-PRODUCTION ///////////////////////////////////////

		// extract request URI, identify the position of the first slash and check all handlers sequentially against the uri
		String uri = httpRequest.getUri();
		int ctxStartIdx = uri.indexOf('/');
		if(ctxStartIdx != -1) {
			
			boolean handlerFound = false;
			
			// step through context paths and forward the request to the associated handler if the path is contained in the URI
			for(String contextPath : contextRequestHandlers.keySet()) {
				if(uri.indexOf(contextPath) != -1) {
					contextRequestHandlers.get(contextPath).processRequest(httpRequest, queryParams, keepAlive, event);
					handlerFound = true;
				}
				if(handlerFound)
					break;
			}
			
			if(!handlerFound) {
				String errorResponse = PTestServerResponseBuilder.buildErrorResponse(serverContextSettings.getHostname(), serverContextSettings.getPort(), PTestServerResponseBuilder.ERROR_CODE_NO_ASSOCIATED_CTX_HANDLER_FOUND, "No handler found for requested URI '"+uri+"'");
				sendResponse(errorResponse.getBytes(), keepAlive, event);
			}			
			
		} else {
			String errorResponse = PTestServerResponseBuilder.buildErrorResponse(serverContextSettings.getHostname(), serverContextSettings.getPort(), PTestServerResponseBuilder.ERROR_CODE_UNKNOWN_CONTEXT_PATH, "Unknown context path '"+uri+"'");
			sendResponse(errorResponse.getBytes(), keepAlive, event);
		}
		
		
		
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) throws Exception {
		super.exceptionCaught(ctx, event);
		logger.error("Error found while processing incoming request: " + event.getCause().getMessage(), event.getCause());
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
	
	
	
}
