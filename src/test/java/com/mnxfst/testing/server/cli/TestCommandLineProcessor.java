package com.mnxfst.testing.server.cli;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.mnxfst.testing.server.cli.CommandLineOption;
import com.mnxfst.testing.server.cli.CommandLineProcessor;

/**
 * Test case for {@link CommandLineProcessor}
 * @author mnxfst
 *
 */
public class TestCommandLineProcessor {

	@Test
	public void testParseCommandLineWithNullValues() {
		CommandLineProcessor p = new CommandLineProcessor();
		Assert.assertNull("The result must not be null", p.parseCommandLine(null,  null, null));
	}

	@Test
	public void testParseCommandLineWithNullValuesAndEmptyList() {
		CommandLineProcessor p = new CommandLineProcessor();
		Assert.assertNull("The result must not be null", p.parseCommandLine(null,  null, new ArrayList<CommandLineOption>()));
	}
	
	@Test
	public void testParseCommandLineWithValidInput() {
		
		CommandLineProcessor p = new CommandLineProcessor();
		
		List<CommandLineOption> cmdLineOptions = new ArrayList<CommandLineOption>();
		cmdLineOptions.add(new CommandLineOption("test", "t", true, true, Boolean.class, "test description", "test", "Missing required 'test' option"));		
		Assert.assertNull("The result must be empty", p.parseCommandLine("testapp",  null, cmdLineOptions));
		Assert.assertEquals("The result must contain 1 element", 1, p.parseCommandLine(null, new String[]{"-t", "true"}, cmdLineOptions).size());
	}
	
}
