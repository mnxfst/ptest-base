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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test cases for {@link PTestServerResponseBuilder}
 * @author mnxfst
 * @since 28.03.2012
 */
public class TestPTestServerResponseBuilder {
	
	@Test
	public void testBuildErrorResponseWithMissingHostname() {
		Map<String, String> errors = new HashMap<String, String>();
		errors.put("1", "test1");
		errors.put("2", "test2");
		errors.put("3", "test3");
		System.out.println(PTestServerResponseBuilder.buildErrorResponse("test", 8080, errors));
	}

}
