/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.mnxfst.testing.server.cfg.PTestServerConfiguration;

/**
 * Channel pipeline factory implementation for mserver
 * @author mnxfst
 * @since 20.03.2012
 */
public class PTestServerChannelPipelineFactory implements ChannelPipelineFactory {

	private static final Logger logger = Logger.getLogger(PTestServerChannelPipelineFactory.class.getName());
	
	private PTestServerConfiguration serverContexSettings = null;

	/**
	 * Initializes the pipeline factory instance
	 * @param hostname
	 * @param port
	 * @param socketThreadPoolSize
	 */
	public PTestServerChannelPipelineFactory(PTestServerConfiguration serverContextSettings) {
		this.serverContexSettings = serverContextSettings;

		if(logger.isDebugEnabled())
			logger.debug("Successfully initializes channel pipeline factory on " + (serverContextSettings != null ? serverContextSettings.getHostname() : null) + ":" + (serverContextSettings != null ? serverContextSettings.getPort() : -1));
	}
	
	/**
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline channelPipeline = Channels.pipeline();

		channelPipeline.addLast("decoder", new HttpRequestDecoder());
		channelPipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		channelPipeline.addLast("encoder", new HttpResponseEncoder());
		channelPipeline.addLast("deflater", new HttpContentCompressor());
		channelPipeline.addLast("handler", new PTestServerChannelUpstreamHandler(serverContexSettings));
		
		return channelPipeline;
	}
	

}
