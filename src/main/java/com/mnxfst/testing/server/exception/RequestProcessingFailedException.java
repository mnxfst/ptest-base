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

package com.mnxfst.testing.server.exception;

import com.mnxfst.testing.server.PTestServerContextRequestHandler;

/**
 * Thrown by the {@link PTestServerContextRequestHandler} implementations in case the request processing failed due to
 * any critical reason which cannot be handled by simply sending an error message to the caller
 * @author mnxfst
 * @since 10.04.2012
 */
public class RequestProcessingFailedException extends Exception {

	private static final long serialVersionUID = -1285961641188583005L;

	public RequestProcessingFailedException() {
	}

	public RequestProcessingFailedException(String msg) {
		super(msg);
	}

	public RequestProcessingFailedException(Throwable cause) {
		super(cause);
	}

	public RequestProcessingFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
