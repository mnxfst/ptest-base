/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.mnxfst.testing.server.cfg.PTestServerConfiguration;
import com.mnxfst.testing.server.cfg.PTestServerConfigurationParser;
import com.mnxfst.testing.server.cli.CommandLineOption;
import com.mnxfst.testing.server.cli.CommandLineProcessor;
import com.mnxfst.testing.server.exception.ServerConfigurationFailedException;

/**
 * Provides a basic server implementation 
 * @author mnxfst
 * @since 20.03.2012
 */
public class PTestServer {

	private static final Logger logger = Logger.getLogger(PTestServer.class.getName());
	
	// configuration file required for setting up server contexts
	public static final String CMD_OPT_CONFIG_FILE = "configFile";
	public static final String CMD_OPT_CONFIG_FILE_SHORT = "cfg";
	
	// variable name used for storing the configuration file name
	protected static final String CLI_VALUE_MAP_CONFIG_FILE = "configFilename";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		args = new String[]{"-cfg", "src/test/resources/ptest-base-config.xml"};
		try {
			new PTestServer().startServer(args);
		} catch(ServerConfigurationFailedException e) {
			System.out.println("Failed to initialize the server. Error: " + e.getMessage());
		}
	}
	
	public void startServer(String[] args) throws ServerConfigurationFailedException {
		CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
		Map<String, Serializable> commandLineValues = commandLineProcessor.parseCommandLine(PTestServer.class.getName(), args, getCommandLineOptions());
		if(commandLineValues != null && !commandLineValues.isEmpty()) {
			String cfgFileName = (String)commandLineValues.get(CLI_VALUE_MAP_CONFIG_FILE);
			PTestServerConfigurationParser parser = new PTestServerConfigurationParser();
			PTestServerConfiguration cfg = parser.parseServerConfiguration(cfgFileName);
			
			ChannelFactory channelFactory = null;
			if(cfg.getSocketPoolSize() > 0)
				channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newFixedThreadPool(cfg.getSocketPoolSize()));
			else
				channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
			
			ServerBootstrap serverBootstrap = new ServerBootstrap(channelFactory);
			serverBootstrap.setPipelineFactory(new PTestServerChannelPipelineFactory(cfg));
			serverBootstrap.setOption("child.tcpNoDelay", true);
			serverBootstrap.setOption("child.keepAlive", true);			
			serverBootstrap.bind(new InetSocketAddress(cfg.getPort()));
			
			logger.info("ptest-server successfully started and listening to port '"+cfg.getPort()+"' for incoming http connections. See documentation for further details.");
		}
	}
	
	/**
	 * Returns the available command-line options 
	 * @return
	 */
	protected static List<CommandLineOption> getCommandLineOptions() {
	
		List<CommandLineOption> options = new ArrayList<CommandLineOption>();
		options.add(new CommandLineOption(CMD_OPT_CONFIG_FILE, CMD_OPT_CONFIG_FILE_SHORT, true, true, String.class, "Server context configuration file", CLI_VALUE_MAP_CONFIG_FILE,"Required option 'configFile' missing"));
		return options;
	}
	
}
