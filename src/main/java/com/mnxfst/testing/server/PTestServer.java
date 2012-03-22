/**
 * Copyright (c) 2012, Christian Kreutzfeldt. All rights reserved.
 * Use is subject to license terms.
 */
package com.mnxfst.testing.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.mnxfst.testing.server.cli.CommandLineOption;
import com.mnxfst.testing.server.cli.CommandLineProcessor;

/**
 * Provides a basic server implementation 
 * @author mnxfst
 * @since 20.03.2012
 */
public class PTestServer {

	private static final Logger logger = Logger.getLogger(PTestServer.class.getName());
	
	// names the port to listen to
	public static final String CMD_OPT_PORT = "port";
	public static final String CMD_OPT_PORT_SHORT = "p";
	
	// host name used -- will be passed on to processor 
	public static final String CMD_OPT_HOSTNAME = "hostname";
	public static final String CMD_OPT_HOSTNAME_SHORT = "h";
	
	// size of thread pool used for handling incoming connections
	public static final String CMD_OPT_THREAD_POOL_SIZE = "poolSize";
	public static final String CMD_OPT_THREAD_POOL_SIZE_SHORT = "ps";
	
	// configuration file required for setting up server contexts
	public static final String CMD_OPT_CONFIG_FILE = "configFile";
	public static final String CMD_OPT_CONFIG_FILE_SHORT = "cfg";
	
	// variable name used for storing the host name
	protected static final String CLI_VALUE_MAP_HOSTNAME_KEY = "hostname";
	// variable name used for storing the port
	protected static final String CLI_VALUE_MAP_PORT_KEY = "port";
	// variable name used for storing the socket thread pool size
	protected static final String CLI_VALUE_MAP_SOCKET_THREAD_POOL_SIZE = "socketThreadPoolSize";
	// variable name used for storing the configuration file name
	protected static final String CLI_VALUE_MAP_CONFIG_FILE = "configFilename";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PTestServer().startServer(args);
	}
	
	public void startServer(String[] args) {
		CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
		Map<String, Serializable> commandLineValues = commandLineProcessor.parseCommandLine(PTestServer.class.getName(), args, getCommandLineOptions());
		if(commandLineValues != null && !commandLineValues.isEmpty()) {
			String hostname = (String)commandLineValues.get(CLI_VALUE_MAP_HOSTNAME_KEY);
			Long port = (Long)commandLineValues.get(CLI_VALUE_MAP_PORT_KEY);
			Long socketThreadPoolSize = (Long)commandLineValues.get(CLI_VALUE_MAP_SOCKET_THREAD_POOL_SIZE);
			String cfgFileName = (String)commandLineValues.get(CLI_VALUE_MAP_CONFIG_FILE);
			
			Properties ctxProps = new Properties();
			try {
				ctxProps.load(new FileInputStream(cfgFileName));
			} catch(FileNotFoundException e) {
				throw new RuntimeException("Referenced configuration file '"+cfgFileName+"' not found.");
			} catch(IOException e) {
				throw new RuntimeException("Failed to read from referenced configuration file '"+cfgFileName+"'. Error: " + e.getMessage());
			}

			ChannelFactory channelFactory = null;
			if(socketThreadPoolSize != null && socketThreadPoolSize.longValue() > 0)
				channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newFixedThreadPool(socketThreadPoolSize.intValue()));
			else
				channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
			
			ServerBootstrap serverBootstrap = new ServerBootstrap(channelFactory);
			serverBootstrap.setPipelineFactory(new PTestServerChannelPipelineFactory(hostname, port.intValue(), socketThreadPoolSize.intValue(), ctxProps));
			serverBootstrap.setOption("child.tcpNoDelay", true);
			serverBootstrap.setOption("child.keepAlive", true);			
			serverBootstrap.bind(new InetSocketAddress(port.intValue()));
			
			logger.info("ptest-server successfully started and listening to port '"+port.intValue()+"' for incoming http connections. See documentation for further details.");
		}
	}
	
	/**
	 * Returns the available command-line options 
	 * @return
	 */
	protected static List<CommandLineOption> getCommandLineOptions() {
	
		List<CommandLineOption> options = new ArrayList<CommandLineOption>();
		options.add(new CommandLineOption(CMD_OPT_HOSTNAME, CMD_OPT_HOSTNAME_SHORT, true, true, String.class, "Name of host running the server", CLI_VALUE_MAP_HOSTNAME_KEY, "Required option 'hostname' missing"));
		options.add(new CommandLineOption(CMD_OPT_PORT, CMD_OPT_PORT_SHORT, true, true, Long.class, "Port to use for communication with server", CLI_VALUE_MAP_PORT_KEY, "Required option 'port' missing"));
		options.add(new CommandLineOption(CMD_OPT_THREAD_POOL_SIZE, CMD_OPT_THREAD_POOL_SIZE_SHORT, true, true, Long.class, "Size of socket pool to use", CLI_VALUE_MAP_SOCKET_THREAD_POOL_SIZE, "Required option 'poolSize' missing"));
		options.add(new CommandLineOption(CMD_OPT_CONFIG_FILE, CMD_OPT_CONFIG_FILE_SHORT, true, true, String.class, "Server context configuration file", CLI_VALUE_MAP_CONFIG_FILE,"Required option 'configFile' missing"));
		return options;
	}
	
}
