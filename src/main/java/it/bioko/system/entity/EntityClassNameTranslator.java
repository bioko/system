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

package it.bioko.system.entity;

import org.apache.commons.lang3.StringUtils;

public class EntityClassNameTranslator {

	public static String toHyphened(String simpleName) {
		return splitWithChar(simpleName, "-").toLowerCase();
	}
	
	public static String toFieldName(String simpleName) {
		return StringUtils.uncapitalize(simpleName);
	}

	private static String splitWithChar(String simpleName, String character) {
		return StringUtils.join(simpleName.split("(?=\\B[A-Z])"), character);
	}
	
	public static String fromSplittedWithChar(String splittedName, String character) {
		String[] splits = splittedName.split(character);
		for (int i = 0; i < splits.length; i++) {
			splits[i] = StringUtils.capitalize(splits[i].toLowerCase());
		}
		return StringUtils.join(splits);
	}
	
	public static String fromHyphened(String hyphenedName) {
		return fromSplittedWithChar(hyphenedName, "-");
	}
	
	public static String fromFieldName(String fieldName) {
		return StringUtils.capitalize(fieldName);
	}
	
}
