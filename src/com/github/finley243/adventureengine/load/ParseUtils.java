package com.github.finley243.adventureengine.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseUtils {

	public static final Set<Character> OPEN_BRACKETS = new HashSet<Character>(Arrays.asList('(', '{', '[', '<'));
	public static final Set<Character> CLOSE_BRACKETS = new HashSet<Character>(Arrays.asList(')', '}', ']', '>'));
	public static final char STRING_MARKER = '"';
	
	// Does not allow separating at bracket character
	// Respects depth (will ignore any separators within a set of brackets)
	// Returns set with single element if separator is not found at current depth
	public static List<String> separateAtChar(char sep, String line){
		if(OPEN_BRACKETS.contains(sep) || CLOSE_BRACKETS.contains(sep)) {
			throw new IllegalArgumentException("Separator cannot be bracket character. Line: " + line);
		}
		List<String> parts = new ArrayList<String>();
		int indexOfLastSplit = -1;
		int depth = 0;
		boolean isString = false;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == STRING_MARKER) {
				isString = !isString;
			} else if(!isString && OPEN_BRACKETS.contains(line.charAt(i))) {
				depth++;
			} else if(!isString && CLOSE_BRACKETS.contains(line.charAt(i))) {
				depth--;
			} else if(!isString && depth == 0 && line.charAt(i) == sep) {
				parts.add(line.substring(indexOfLastSplit + 1, i).trim());
				indexOfLastSplit = i;
			}
		}
		parts.add(line.substring(indexOfLastSplit + 1, line.length()).trim());
		return parts;
	}
	
	public static List<String> insideBrackets(char open, char close, String line) {
		List<String> parts = new ArrayList<String>();
		int openIndex = -1;
		int closeIndex = -1;
		int depth = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == open) {
				if(depth == 0) {
					openIndex = i;
				}
				depth++;
			} else if(line.charAt(i) == close) {
				depth--;
				if(depth == 0) {
					closeIndex = i;
					parts.add(line.substring(openIndex + 1, closeIndex));
				}
			}
		}
		return parts;
	}
	
	public static boolean hasBrackets(char open, char close, String line) {
		return (line.charAt(0) == open && line.charAt(line.length() - 1) == close);
	}
	
	public static String isolate(String line) {
		return line.substring(1, line.length() - 1);
	}
	
	public static String isolateStringContents(String line) {
		if(line.charAt(0) != STRING_MARKER || line.charAt(line.length() - 1) != STRING_MARKER) {
			throw new IllegalArgumentException("Line is not a string. Line: " + line);
		}
		return line.substring(1, line.length() - 1);
	}
	
	// Format: KEY1:value,KEY2:other value,KEY3:another value
	// sep = separator between key/value pairs
	// keySep = separator between key and value in a pair
	public static Map<String, String> mapTerms(char sep, char keySep, String line){
		Map<String, String> values = new HashMap<String, String>();
		List<String> pairs = separateAtChar(sep, line);
		for(String pair : pairs) {
			List<String> splitPair = separateAtChar(keySep, pair);
			if(splitPair.size() == 2) {
				values.put(splitPair.get(0), splitPair.get(1));
			}
		}
		return values;
	}
	
}
