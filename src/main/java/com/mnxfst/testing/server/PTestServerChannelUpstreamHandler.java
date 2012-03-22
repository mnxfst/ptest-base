/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Implements the handler for incoming requests
 * @author mnxfst
 * @since 20.03.2012
 */
public class PTestServerChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(PTestServerChannelPipelineFactory.class.getName());
	
	private String hostname = null;
	private int port = 0;
	private int socketThreadPoolSize = 0;
	private Properties serverContextProperties = null;
	
	private Map<String, PTestServerContextRequestHandler> contextRequestHandlers = new HashMap<String, PTestServerContextRequestHandler>();

	/**
	 * Initializes the pipeline factory instance
	 * @param hostname
	 * @param port
	 * @param socketThreadPoolSize
	 */
	public PTestServerChannelUpstreamHandler(String hostname, int port, int socketThreadPoolSize, Properties serverContextProperties) {
		this.hostname = hostname;
		this.port = port;
		this.socketThreadPoolSize = socketThreadPoolSize;
		this.serverContextProperties = serverContextProperties;
		
		// configure the context specifc request handlers
		synchronized (contextRequestHandlers) {
			StringBuffer logStr = new StringBuffer();		

			for(Iterator<Object> iter = serverContextProperties.keySet().iterator(); iter.hasNext();) {
				String contextPath = (String)iter.next();
				String contextHandlerClassName = serverContextProperties.getProperty(contextPath);
				
				if(logger.isDebugEnabled())
					logger.debug("Attempting to instantiate a handler for context '"+contextPath+"': " + contextHandlerClassName);
				
				try {
					Class<? extends PTestServerContextRequestHandler> contextHandlerClazz = (Class<? extends PTestServerContextRequestHandler>) Class.forName(contextHandlerClassName);
					PTestServerContextRequestHandler contextHandler = contextHandlerClazz.newInstance();
					contextHandler.initialize(serverContextProperties);
//					contextRequestHandlers.putIfAbsent(contextPath, contextHandler);
				} catch(ClassNotFoundException e) {
					throw new RuntimeException("Context handler class '"+contextHandlerClassName+"' not found");
				} catch(InstantiationException e) {
					throw new RuntimeException("Context handler class '"+contextHandlerClassName+"' could not be instantiated. Error: " + e.getMessage());
				} catch(IllegalAccessException e) {
					throw new RuntimeException("Context handler class '"+contextHandlerClassName+"' could not be accessed. Error: " + e.getMessage());
				}
				
				// create log output
				logStr.append(contextHandlerClassName).append(" (").append(contextHandlerClassName).append(")");
				if(iter.hasNext())
					logStr.append(", ");

			}
			logger.info("consumer[host="+hostname+", port="+port+", socketThreadPoolSize="+socketThreadPoolSize+", consumers="+logStr.toString()+"]");
		}
	}
	
	
	
	
}
