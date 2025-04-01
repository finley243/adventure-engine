package com.github.finley243.adventureengine.textgen;

public class LangUtils {

	public static String pluralizeNoun(String noun) {
		String lastLetter = noun.substring(noun.length() - 1);
		String lastTwoLetters = noun.substring(noun.length() - 2);
		if (lastLetter.equals("s") || lastLetter.equals("x") || lastLetter.equals("z") || lastTwoLetters.equals("ch") || lastTwoLetters.equals("sh")) {
			return noun + "es";
		} else if (lastLetter.equals("y") && "aeiou".indexOf(noun.charAt(noun.length() - 2)) < 0) { // Second to last character is not a vowel
			return noun.substring(0, noun.length() - 1) + "ies";
		} else {
			return noun + "s";
		}
	}
	
	public static String capitalize(String word) {
		if (word.length() < 1) {
			return word;
		}
		for (int i = 0; i < word.length(); i++) {
			if (Character.isAlphabetic(word.charAt(i))) {
				String preCap = word.substring(0, i);
				String cap = word.substring(i, i + 1);
				String postCap = word.substring(i + 1);
				return preCap + cap.toUpperCase() + postCap;
			} else if (Character.isDigit(word.charAt(i))) {
				return word;
			}
		}
		// If no alphabetic characters, return the unmodified string
		return word;
	}
	
	public static String titleCase(String line) {
		String[] words = line.split(" ");
		for (int i = 0; i < words.length; i++) {
			words[i] = capitalize(words[i]);
		}
		StringBuilder newLine = new StringBuilder();
		for (String word : words) {
			newLine.append(word).append(" ");
		}
		return newLine.toString().trim();
	}
	
	public static String possessive(String noun, boolean plural) {
		if (plural) {
			if (noun.charAt(noun.length() - 1) == 's') {
				return noun + "'";
			} else {
				return noun + "'s";
			}
		} else {
			return noun + "'s";
		}
	}
	
	public static String addArticle(String word, boolean indefinite) {
		if (indefinite) {
			boolean startsWithVowel = "aeiou".indexOf(word.charAt(0)) >= 0;
			if (startsWithVowel) {
				return "an " + word;
			} else {
				return "a " + word;
			}
		} else {
			return "the " + word;
		}
	}
	
}
