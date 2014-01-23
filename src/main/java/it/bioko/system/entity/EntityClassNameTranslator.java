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
