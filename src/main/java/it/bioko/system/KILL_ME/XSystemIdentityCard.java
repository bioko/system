/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package it.bioko.system.KILL_ME;

import it.bioko.system.ConfigurationEnum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class XSystemIdentityCard {

	private String _xSystemName;
	private String _xSystemVersion;
	private ConfigurationEnum _xSystemConfiguration;

	public XSystemIdentityCard(String xSystemName, String xSystemVersion,
			ConfigurationEnum xSystemConfiguration) {
		_xSystemName = xSystemName;
		_xSystemVersion = xSystemVersion;
		_xSystemConfiguration = xSystemConfiguration;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	public String report() {
		StringBuilder result = new StringBuilder(this.getClass().getSimpleName());
		result.append("\n");
		result.append("System: " + _xSystemName);
		result.append("\n");
		result.append("Version: " + _xSystemVersion);
		result.append("\n");
		result.append("Configuration: " + _xSystemConfiguration.name());
		result.append("\n");
		return result.toString();
	}
	
	public String getSystemName() {
		return _xSystemName;
	}
	
	public String getSystemVersion() {
		return _xSystemVersion;
	}
	
	public ConfigurationEnum getSystemConfiguration() {
		return _xSystemConfiguration;
	}

}