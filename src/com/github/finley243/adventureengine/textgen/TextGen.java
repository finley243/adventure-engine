package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextGen {

	private static final char RANDOM_OPEN = '{';
	private static final char RANDOM_CLOSE = '}';
	private static final char RANDOM_SEPARATOR = '|';

	// e.g. jumps
	private static final String VERB_S = "$s";
	// e.g. goes/does
	private static final String VERB_ES = "$es";
	// e.g. flies
	private static final String VERB_IES = "$ies";

	private static final String VERB_DO_NOT = "$doesn't";
	private static final String VERB_BE = "$is";
	private static final String VERB_BE_NOT = "$isn't";
	private static final String VERB_HAVE = "$has";
	
	private static TextContext lastContext;

	/*
	 * Format for tags: $_subject hit$s_subject $object1 with $object2
	 * Format for OR expressions: {this thing|other thing} or {$tag thing|other thing}
	 */

	public static String generate(String line, TextContext context) {
		if (line == null) return null;
		String sentence = "";
		line = chooseRandoms(line);
		line = determineContext(line, context);
		sentence += LangUtils.capitalize(line);
		sentence += ".";
		lastContext = context;
		for (Noun object : context.getObjects().values()) {
			object.setKnown();
		}
		return sentence;
	}

	public static String generateVarsOnly(String line, Map<String, String> vars) {
		List<String> varTags = new ArrayList<>(vars.keySet());
		varTags.sort(Comparator.comparingInt(String::length));
		Collections.reverse(varTags);
		for (String varTag : varTags) {
			line = line.replace("$" + varTag, vars.get(varTag));
		}
		return line;
	}
	
	public static void clearContext() {
		lastContext = null;
	}

	private static String determineContext(String line, TextContext context) {
		boolean[] usePronouns = new boolean[context.getObjects().size()];
		if (lastContext != null) {
			List<Noun> objectList = new ArrayList<>(context.getObjects().values());
			List<Noun> lastObjectList = new ArrayList<>(lastContext.getObjects().values());
			for (int i = 0; i < objectList.size(); i++) {
				Noun object = objectList.get(i);
				if (!lastObjectList.isEmpty()) {
					if (lastObjectList.size() <= i && object == lastObjectList.get(lastObjectList.size() - 1)
							|| lastObjectList.size() > i && object == lastObjectList.get(i)) {
						if (!matchesAnyPronounsUpToObjectIndex(lastContext, object.getPronoun(), i, usePronouns)) {
							usePronouns[i] = true;
						}
					}
				}
			}
		}
		List<String> objectTagList = new ArrayList<>(context.getObjects().keySet());
		Map<String, Boolean> usePronounsMap = new LinkedHashMap<>();
		for (int i = 0; i < objectTagList.size(); i++) {
			usePronounsMap.put(objectTagList.get(i), usePronouns[i]);
		}
		for (String objectTag : context.getObjects().keySet()) {
			if (context.getObjects().get(objectTag).getPronoun().forcePronoun) {
				usePronounsMap.put(objectTag, true);
			}
		}
		return populateFromContext(line, context, usePronounsMap);
	}

	private static String chooseRandoms(String line) {
		List<String> parts = new ArrayList<>();
		int openIndex = -1;
		int closeIndex = -1;
		int depth = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == RANDOM_OPEN) {
				if (depth == 0) {
					openIndex = i;
				}
				depth++;
			} else if (line.charAt(i) == RANDOM_CLOSE) {
				depth--;
				if (depth == 0) {
					String lineInBrackets = line.substring(openIndex + 1, i);
					List<String> randomChoices = separateRandomChoices(lineInBrackets);
					String randomChoice = randomChoices.get(ThreadLocalRandom.current().nextInt(randomChoices.size()));
					parts.add(line.substring(closeIndex + 1, openIndex));
					parts.add(chooseRandoms(randomChoice));
					closeIndex = i;
				}
			}
		}
		parts.add(line.substring(closeIndex + 1));
		StringBuilder newLine = new StringBuilder();
		for (String current : parts) {
			newLine.append(current);
		}
		return newLine.toString();
	}

	private static List<String> separateRandomChoices(String line) {
		List<String> parts = new ArrayList<>();
		int indexOfLastSplit = -1;
		int depth = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == RANDOM_OPEN) {
				depth++;
			} else if (line.charAt(i) == RANDOM_CLOSE) {
				depth--;
			} else if (depth == 0 && line.charAt(i) == RANDOM_SEPARATOR) {
				parts.add(line.substring(indexOfLastSplit + 1, i).trim());
				indexOfLastSplit = i;
			}
		}
		parts.add(line.substring(indexOfLastSplit + 1).trim());
		return parts;
	}

	private static String populateFromContext(String line, TextContext context, Map<String, Boolean> usePronouns) {
		Pattern tokenPattern = Pattern.compile("\\$[a-zA-Z0-9_']+");
		Matcher tokenMatcher = tokenPattern.matcher(line);
		List<TextToken> tokens = new ArrayList<>();
		while (tokenMatcher.find()) {
			int start = tokenMatcher.start();
			int end = tokenMatcher.end();
			String value = tokenMatcher.group().substring(1);
			boolean isVerb = value.equals("s") || value.equals("es") || value.equals("ies") || value.equals("doesn't") || value.equals("is") || value.equals("isn't") || value.equals("has");
			tokens.add(new TextToken(start, end, value, isVerb));
		}
		for (int i = 0; i < tokens.size(); i++) {
			TextToken currentToken = tokens.get(i);
			if (currentToken.isVerb && currentToken.value.equals("is") && currentToken.start - 6 >= 0 && line.startsWith("there ", currentToken.start - 6)) {
				currentToken.subjectToken = tokens.get(i + 1);
			} else if (currentToken.isVerb) {
				for (int j = i - 1; j >= 0; j--) {
					if (tokens.get(j).isSubject) {
						currentToken.subjectToken = tokens.get(j);
						break;
					}
				}
			} else {
				currentToken.isSubject = i < tokens.size() - 1 && tokens.get(i + 1).isVerb;
			}
		}
		StringBuilder builder = new StringBuilder();
		int lastEnd = 0;
		for (TextToken token : tokens) {
			int start = token.start;
			String tokenName = token.value;
			builder.append(line, lastEnd, start);
			if (context.getVars().containsKey(tokenName)) {
				builder.append(context.getVars().get(tokenName));
			} else if (context.getObjects().containsKey(tokenName)) {
				Noun object = context.getObjects().get(tokenName);
				builder.append(usePronouns.get(tokenName) ? (token.isSubject ? object.getPronoun().subject : object.getPronoun().object) : formatNoun(object.getName(), object.isProperName(), object.isKnown(), object.pluralCount()));
				token.usedPronoun = usePronouns.get(tokenName);
				usePronouns.put(tokenName, true);
			} else if (tokenName.endsWith("'s") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 2))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 2);
				Noun object = context.getObjects().get(objectKey);
				builder.append(usePronouns.get(objectKey) ? object.getPronoun().possessive : LangUtils.possessive(formatNoun(object.getName(), object.isProperName(), object.isKnown(), object.pluralCount()), object.pluralCount() > 1));
				token.usedPronoun = usePronouns.get(objectKey);
				usePronouns.put(objectKey, true);
			} else if (tokenName.endsWith("_self") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 5))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 5);
				builder.append(context.getObjects().get(objectKey).getPronoun().reflexive);
			} else if (tokenName.endsWith("_name") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 5))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 5);
				builder.append(context.getObjects().get(objectKey).getName());
			} else if (tokenName.equals("s")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "" : "s");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "s" : "");
				}
			} else if (tokenName.equals("es")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "" : "es");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "es" : "");
				}
			} else if (tokenName.equals("ies")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "y" : "ies");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "ies" : "y");
				}
			} else if (tokenName.equals("doesn't")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "don't" : "doesn't");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "doesn't" : "don't");
				}
			} else if (tokenName.equals("is")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "are" : "is");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "is"
							: (subject.getPronoun() == Pronoun.I ? "am" : "are"));
				}
			} else if (tokenName.equals("isn't")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "aren't" : "isn't");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "isn't"
							: (subject.getPronoun() == Pronoun.I ? "am not" : "aren't"));
				}
			} else if (tokenName.equals("has")) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				if (!token.subjectToken.usedPronoun) {
					builder.append(subject.pluralCount() > 1 ? "have" : "has");
				} else {
					builder.append(subject.getPronoun().thirdPersonVerb ? "has" : "have");
				}
			} else {
				builder.append("$").append(tokenName);
			}
			lastEnd = token.end;
		}
		builder.append(line.substring(lastEnd));
		return builder.toString();
	}

	private static String getSubjectKeyFromToken(TextToken token) {
		if (token.value.endsWith("_name")) {
			return token.value.substring(0, token.value.length() - 5);
		}
		return token.value;
	}

	private static String formatNoun(String name, boolean isProper, boolean isDefinite, int pluralCount) {
		if (isProper) {
			return name;
		} else if (pluralCount == 1) {
			if (!isDefinite) {
				boolean startsWithVowel = "aeiou".indexOf(name.charAt(0)) >= 0;
				if (startsWithVowel) {
					return "an " + name;
				} else {
					return "a " + name;
				}
			} else {
				return "the " + name;
			}
		} else {
			if (!isDefinite) {
				if (pluralCount == 2) {
					return "a couple of " + name;
				} else if (pluralCount <= 5) {
					return "several " + name;
				} else {
					return "some " + name;
				}
			} else {
				return "the " + name;
			}
		}
	}

	// Returns whether there is a matching (and used) pronoun in context that is below the given index
	private static boolean matchesAnyPronounsUpToObjectIndex(TextContext context, Pronoun pronoun, int index, boolean[] usePronouns) {
		List<Noun> objectsList = new ArrayList<>(context.getObjects().values());
		for (int i = 0; i < Math.min(objectsList.size(), index - 1); i++) {
			Pronoun objectPronoun = objectsList.get(i).getPronoun();
			if(usePronouns[i] && objectPronoun == pronoun) return true;
		}
		return false;
	}

	private static class TextToken {
		public final int start;
		public final int end;
		public final String value;
		public final boolean isVerb;
		public boolean isSubject;
		public boolean usedPronoun;
		public TextToken subjectToken;

		TextToken(int start, int end, String value, boolean isVerb) {
			this.start = start;
			this.end = end;
			this.value = value;
			this.isVerb = isVerb;
			this.isSubject = false;
			this.usedPronoun = false;
			this.subjectToken = null;
		}
	}

}
