package personal.finley.adventure_engine_2.textgen;

public class LangUtils {

	public static String pluralizeNoun(String noun) {
		String lastLetter = noun.substring(noun.length() - 1);
		String lastTwoLetters = noun.substring(noun.length() - 2);
		if(lastLetter.equals("s") || lastLetter.equals("x") || lastLetter.equals("z") || lastTwoLetters.equals("ch") || lastTwoLetters.equals("sh")) {
			return noun + "es";
		} else if(lastLetter.equals("y") && "aeiou".indexOf(noun.charAt(noun.length() - 2)) < 0) { // Second to last character is not a vowel
			return noun.substring(0, noun.length() - 1) + "ies";
		} else {
			return noun + "s";
		}
	}
	
	public static String capitalize(String word) {
		if(word.length() < 1) {
			return word;
		}
		String firstLetter = word.substring(0, 1);
		String restOfWord = word.substring(1);
		return firstLetter.toUpperCase() + restOfWord;
	}
	
	public static String titleCase(String line) {
		String[] words = line.split(" ");
		for(int i = 0; i < words.length; i++) {
			words[i] = capitalize(words[i]);
		}
		String newLine = "";
		for(String word : words) {
			newLine += word + " ";
		}
		return newLine.trim();
	}
	
	public static String possessive(String noun, boolean plural) {
		if(plural) {
			if(noun.charAt(noun.length() - 1) == 's') {
				return noun + "'";
			} else {
				return noun + "'s";
			}
		} else {
			return noun + "'s";
		}
	}
	
}
