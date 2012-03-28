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

package com.mnxfst.testing.server.cfg;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.LangUtils;

/**
 * Replaces the equals and hashCode methods of {@link BasicNameValuePair} since it uses the key and value
 * to compute equals/hashCode. The ptest server configuration requires only ONE entry for each key, thus
 * both methods just work on the key attribute in this implementation
 * @author mnxfst
 * @since 28.03.2012
 */
public class PTestNameValuePair extends BasicNameValuePair {

	private static final long serialVersionUID = 1134063570841745631L;

	/**
	 * TODO write a sufficient comment explaining the constructor unambiguously
	 * @param name
	 * @param value
	 */
	public PTestNameValuePair(String name, String value) {
		super(name, value);
	}

    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object instanceof NameValuePair) {
            BasicNameValuePair that = (BasicNameValuePair) object;
            return this.getName().equals(that.getName());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.getName());
        return hash;
    }

}
