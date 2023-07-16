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
			if (context.getObjects().get(objectTag).forcePronoun()) {
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
		StringBuilder builder = new StringBuilder();
		int lastEnd = 0;
		while (tokenMatcher.find()) {
			int start = tokenMatcher.start();
			String tokenName = tokenMatcher.group().substring(1);
			builder.append(line, lastEnd, start);
			if (context.getVars().containsKey(tokenName)) {
				builder.append(context.getVars().get(tokenName));
			} else if (context.getObjects().containsKey(tokenName)) {
				builder.append(usePronouns.get(tokenName) ? context.getObjects().get(tokenName).getPronoun().object : context.getObjects().get(tokenName).getFormattedName());
				usePronouns.put(tokenName, true);
			} else if (tokenName.startsWith("_") && context.getObjects().containsKey(tokenName.substring(1))) {
				String objectKey = tokenName.substring(1);
				builder.append(usePronouns.get(objectKey) ? context.getObjects().get(objectKey).getPronoun().subject : context.getObjects().get(objectKey).getFormattedName());
				usePronouns.put(objectKey, true);
			} else if (tokenName.endsWith("'s") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 2))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 2);
				builder.append(usePronouns.get(objectKey) ? context.getObjects().get(objectKey).getPronoun().possessive : LangUtils.possessive(context.getObjects().get(objectKey).getFormattedName(), context.getObjects().get(objectKey).isPlural()));
				usePronouns.put(objectKey, true);
			} else if (tokenName.endsWith("_self") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 5))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 5);
				builder.append(context.getObjects().get(objectKey).getPronoun().reflexive);
			} else if (tokenName.endsWith("_name") && context.getObjects().containsKey(tokenName.substring(0, tokenName.length() - 5))) {
				String objectKey = tokenName.substring(0, tokenName.length() - 5);
				builder.append(context.getObjects().get(objectKey).getName());
			} else if (tokenName.startsWith("s_") && context.getObjects().containsKey(tokenName.substring(2))) {
				String objectKey = tokenName.substring(2);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "" : "s");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "s" : "");
				}
			} else if (tokenName.startsWith("es_") && context.getObjects().containsKey(tokenName.substring(3))) {
				String objectKey = tokenName.substring(3);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "" : "es");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "es" : "");
				}
			} else if (tokenName.startsWith("ies_") && context.getObjects().containsKey(tokenName.substring(4))) {
				String objectKey = tokenName.substring(4);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "y" : "ies");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "ies" : "y");
				}
			} else if (tokenName.startsWith("doesn't_") && context.getObjects().containsKey(tokenName.substring(8))) {
				String objectKey = tokenName.substring(8);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "don't" : "doesn't");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "doesn't" : "don't");
				}
			} else if (tokenName.startsWith("is_") && context.getObjects().containsKey(tokenName.substring(3))) {
				String objectKey = tokenName.substring(3);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "are" : "is");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "is"
							: (context.getObjects().get(objectKey).getPronoun() == Pronoun.I ? "am" : "are"));
				}
			} else if (tokenName.startsWith("isn't_") && context.getObjects().containsKey(tokenName.substring(6))) {
				String objectKey = tokenName.substring(6);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "aren't" : "isn't");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "isn't"
							: (context.getObjects().get(objectKey).getPronoun() == Pronoun.I ? "am not" : "aren't"));
				}
			} else if (tokenName.startsWith("has_") && context.getObjects().containsKey(tokenName.substring(4))) {
				String objectKey = tokenName.substring(4);
				if (!usePronouns.get(objectKey)) {
					builder.append(context.getObjects().get(objectKey).isPlural() ? "have" : "has");
				} else {
					builder.append(context.getObjects().get(objectKey).getPronoun().thirdPersonVerb ? "has" : "have");
				}
			} else {
				builder.append("$").append(tokenName);
			}
			lastEnd = tokenMatcher.end();
		}
		builder.append(line.substring(lastEnd));
		return builder.toString();
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

}
