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

/**
 * Builds response xml for the ptest server
 * @author mnxfst
 * @since 28.03.2012
 */
public class PTestServerResponseBuilder {

	public static final int RESPONSE_CODE_OK = 0;
	public static final int RESPONSE_CODE_ERROR = 1;
	
	public static final String ERROR_CODE_NO_ASSOCIATED_CTX_HANDLER_FOUND = "no_ctx_handler_found";
	public static final String ERROR_CODE_UNKNOWN_CONTEXT_PATH = "unknown_ctx_path";
	
	private static final String RESPONSE_XML_ROOT = "ptestResponse";
	private static final String RESPONSE_XML_ERRORS_TAG = "errors";
	private static final String RESPONSE_XML_ERROR_TAG = "error";
	private static final String RESPONSE_XML_ERROR_KEY_TAG = "key";
	private static final String RESPONSE_XML_ERROR_MSG_TAG = "message";
	private static final String RESPONSE_XML_RESPONSE_CODE_TAG = "responseCode";
	private static final String RESPONSE_XML_HOSTNAME_TAG = "hostname";
	private static final String RESPONSE_XML_PORT_TAG = "port";
	
	public static String buildErrorResponse(String hostname, int port, Map<String, String> errors) {
		
		StringBuffer response = new StringBuffer();
		response.append("<").append(RESPONSE_XML_ROOT).append(">");
		response.append("<").append(RESPONSE_XML_RESPONSE_CODE_TAG).append(">").append(RESPONSE_CODE_ERROR).append("</").append(RESPONSE_XML_RESPONSE_CODE_TAG).append(">");
		response.append("<").append(RESPONSE_XML_HOSTNAME_TAG).append(">").append(hostname).append("</").append(RESPONSE_XML_HOSTNAME_TAG).append(">");
		response.append("<").append(RESPONSE_XML_PORT_TAG).append(">").append(port).append("</").append(RESPONSE_XML_PORT_TAG).append(">");
		response.append("<").append(RESPONSE_XML_ERRORS_TAG).append(">");
		for(String errorKey : errors.keySet()) {
			response.append("<").append(RESPONSE_XML_ERROR_TAG).append(">");
			response.append("<").append(RESPONSE_XML_ERROR_KEY_TAG).append(">").append(errorKey).append("</").append(RESPONSE_XML_ERROR_KEY_TAG);
			response.append("<").append(RESPONSE_XML_ERROR_MSG_TAG).append(">").append(errors.get(errorKey)).append("</").append(RESPONSE_XML_ERROR_MSG_TAG);
			response.append("</").append(RESPONSE_XML_ERROR_TAG).append(">");
		}
		response.append("</").append(RESPONSE_XML_ERRORS_TAG).append(">");
		response.append("</").append(RESPONSE_XML_ROOT).append(">");
		return response.toString();
	}
	
	public static String buildErrorResponse(String hostname, int port, String errorKey, String msg) {		
		Map<String, String> errors = new HashMap<String, String>();
		errors.put(errorKey, msg);
		return buildErrorResponse(hostname, port, errors);		
	}
	
}
