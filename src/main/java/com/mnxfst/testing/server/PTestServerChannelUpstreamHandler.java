/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

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

	/**
	 * Initializes the pipeline factory instance
	 * @param hostname
	 * @param port
	 * @param socketThreadPoolSize
	 */
	public PTestServerChannelUpstreamHandler(String hostname, int port, int socketThreadPoolSize) {
		this.hostname = hostname;
		this.port = port;
		this.socketThreadPoolSize = socketThreadPoolSize;
	}
	
	
}
