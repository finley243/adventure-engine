package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.ScriptParser;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextGen {

	private static final Set<Character> VALID_OPEN_BRACKETS = Set.of('(', '{', '[');
	private static final Set<Character> VALID_CLOSE_BRACKETS = Set.of(')', '}', ']');
	private static final char RANDOM_OPEN = '{';
	private static final char RANDOM_SEPARATOR = '|';
	private static final char CONDITIONAL_OPEN = '(';
	private static final char CONDITIONAL_SEPARATOR = '|';
	private static final char CONDITIONAL_CONDITION_OPEN = '[';
	private static final char CONDITIONAL_CONDITION_CLOSE = ']';

	private static final Map<Integer, String> PLURAL_DESCRIPTORS = new TreeMap<>() {{
		// Integer value represents a maximum count for the descriptor.
		// Descriptors must be arranged in ascending order.
		put(2, "a couple of ");
		put(5, "several ");
	}};
	private static final String DEFAULT_PLURAL_DESCRIPTOR = "some ";

	private static final Map<String, String> VERB_MAP = new HashMap<>() {{
		// e.g. jumps
        put("s", "");
		// e.g. goes/does
        put("es", "");
		// e.g. flies
        put("ies", "y");
		put("doesn't", "don't");
		put("is", "are");
		put("isn't", "aren't");
		put("has", "have");
    }};
	
	private static TextContext lastContext;

	/*
	 * Format for tags: $subject hit$s $object1 with $object2
	 * Format for OR expressions: {this thing|other thing} or {$tag thing|other thing}
	 * Format for conditionals: ([condition]phrase|[other condition]other phrase|default phrase)
	 */

	public static String generate(String line, Context context, TextContext textContext) {
		if (line == null) return null;
		String originalLine = line;
		line = processBlockStatements(line, 0, context, originalLine);
		line = determineContext(line, textContext);
		line = capitalizeSentences(line);
		lastContext = textContext;
		for (Noun object : textContext.getObjects().values()) {
			object.setKnown();
		}
		return line;
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

	private static String capitalizeSentences(String line) {
		if (line == null || line.isEmpty()) {
			return line;
		}
		StringBuilder result = new StringBuilder(line.length());
		boolean capitalizeNext = true;
		for (char currentChar : line.toCharArray()) {
			if (capitalizeNext && Character.isLetter(currentChar)) {
				result.append(Character.toUpperCase(currentChar));
				capitalizeNext = false;
			} else {
				result.append(currentChar);
			}
			if (currentChar == '.') {
				capitalizeNext = true;
			}
		}
		return result.toString();
	}

	private static String determineContext(String line, TextContext context) {
		boolean[] usePronouns = new boolean[context.getObjects().size()];
		if (lastContext != null) {
			List<Noun> objectList = new ArrayList<>(context.getObjects().values());
			List<Noun> lastObjectList = new ArrayList<>(lastContext.getObjects().values());
			for (int i = 0; i < objectList.size(); i++) {
				Noun object = objectList.get(i);
				if (!lastObjectList.isEmpty()) {
					if (lastObjectList.size() <= i && object == lastObjectList.getLast()
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
		return replaceTagsFromContext(line, context, usePronounsMap);
	}

	private static String processBlockStatements(String line, int startIndex, Context context, String originalLine) {
		for (int i = startIndex; i < line.length(); i++) {
			if (line.charAt(i) == RANDOM_OPEN) {
				return processBlockStatements(chooseRandoms(line, originalLine), i, context, originalLine);
			} else if (line.charAt(i) == CONDITIONAL_OPEN) {
				return processBlockStatements(evaluateConditionals(line, context, originalLine), i, context, originalLine);
			} else if (line.charAt(i) == '@') {
				return processBlockStatements(populatePhraseReferences(line, context), i, context, originalLine);
			}
		}
		return line;
	}

	private static String chooseRandoms(String line, String originalLine) {
		return replaceInsideBracketsWithResult(line, RANDOM_OPEN, null, originalLine, (s, _, _) -> {
			List<String> randomChoices = getSeparatedStrings(s, RANDOM_SEPARATOR);
			return MathUtils.selectRandomFromList(randomChoices);
		});
	}

	private static String evaluateConditionals(String line, Context context, String originalLine) {
		return replaceInsideBracketsWithResult(line, CONDITIONAL_OPEN, context, originalLine, TextGen::evaluateConditionalStatement);
	}

	private static String evaluateConditionalStatement(String line, Context context, String originalLine) {
		List<String> conditionalBranches = getSeparatedStrings(line, CONDITIONAL_SEPARATOR);
		for (int i = 0; i < conditionalBranches.size(); i++) {
			String currentBranch = conditionalBranches.get(i);
			if (i < conditionalBranches.size() - 1 && currentBranch.charAt(0) != CONDITIONAL_CONDITION_OPEN) {
				throw new IllegalArgumentException("Non-default conditional branch is missing condition");
			}
			if (i == conditionalBranches.size() - 1 && currentBranch.charAt(0) != CONDITIONAL_CONDITION_OPEN) {
				return currentBranch;
			}
			int conditionCloseIndex = currentBranch.indexOf(CONDITIONAL_CONDITION_CLOSE);
			if (conditionCloseIndex == -1) {
				throw new IllegalArgumentException("Condition is missing closing bracket");
			}
			String conditionString = currentBranch.substring(1, conditionCloseIndex);
			Condition condition = new Condition(ScriptParser.parseExpression(conditionString, "Phrase: " + originalLine));
			if (condition.isMet(context)) {
				return currentBranch.substring(conditionCloseIndex + 1);
			}
		}
		return "";
	}

	private static String populatePhraseReferences(String line, Context context) {
		Pattern tokenPattern = Pattern.compile("@([a-zA-Z0-9_]+)");
		Matcher tokenMatcher = tokenPattern.matcher(line);
		StringBuilder builder = new StringBuilder();
		int lastEnd = 0;
		while (tokenMatcher.find()) {
			int start = tokenMatcher.start();
			int end = tokenMatcher.end();
			String name = tokenMatcher.group(1);
			// TODO - Prevent infinite recursion
			builder.append(line, lastEnd, start);
			String phraseToInsert = Phrases.get(name);
			if (phraseToInsert != null) {
				builder.append(phraseToInsert);
			} else {
				// Appending the original @ symbol causes an infinite recursion
				builder.append("MISSING_PHRASE_").append(name);
			}
			lastEnd = end;
		}
		builder.append(line.substring(lastEnd));
		return builder.toString();
	}

	private static String replaceTagsFromContext(String line, TextContext context, Map<String, Boolean> usePronouns) {
		Pattern tokenPattern = Pattern.compile("\\$([a-zA-Z0-9_']+)");
		Matcher tokenMatcher = tokenPattern.matcher(line);
		List<TextToken> tokens = new ArrayList<>();
		while (tokenMatcher.find()) {
			String value = tokenMatcher.group(1);
			boolean isVerb = VERB_MAP.containsKey(value);
			tokens.add(new TextToken(tokenMatcher.start(), tokenMatcher.end(), value, isVerb));
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
			} else if (tokenMatchesSuffixPattern(tokenName, "'s", context)) {
				String objectKey = getObjectKeyFromSuffixedToken(tokenName, "'s");
				Noun object = context.getObjects().get(objectKey);
				builder.append(usePronouns.get(objectKey) ? object.getPronoun().possessive : LangUtils.possessive(formatNoun(object.getName(), object.isProperName(), object.isKnown(), object.pluralCount()), object.pluralCount() > 1));
				token.usedPronoun = usePronouns.get(objectKey);
				usePronouns.put(objectKey, true);
			} else if (tokenMatchesPossessiveObjectPattern(tokenName, context)) {
				String[] objectKeys = getObjectKeysFromPossessiveObjectToken(tokenName);
				Noun possessor = context.getObjects().get(objectKeys[0]);
				Noun object = context.getObjects().get(objectKeys[1]);
				if (object.isProperName()) {
					builder.append(object.getName());
				} else if (usePronouns.get(objectKeys[1])) {
					builder.append(token.isSubject ? object.getPronoun().subject : object.getPronoun().object);
				} else {
					builder.append(usePronouns.get(objectKeys[0]) ? possessor.getPronoun().possessive : LangUtils.possessive(formatNoun(possessor.getName(), possessor.isProperName(), possessor.isKnown(), possessor.pluralCount()), possessor.pluralCount() > 1));
					builder.append(" ");
					builder.append(object.getName());
				}
				token.usedPronoun = usePronouns.get(objectKeys[1]);
				usePronouns.put(objectKeys[1], true);
			} else if (tokenMatchesSuffixPattern(tokenName, "_self", context)) {
				String objectKey = getObjectKeyFromSuffixedToken(tokenName, "_self");
				builder.append(context.getObjects().get(objectKey).getPronoun().reflexive);
			} else if (tokenMatchesSuffixPattern(tokenName, "_name", context)) {
				String objectKey = getObjectKeyFromSuffixedToken(tokenName, "_name");
				builder.append(context.getObjects().get(objectKey).getName());
			} else if (VERB_MAP.containsKey(tokenName)) {
				Noun subject = context.getObjects().get(getSubjectKeyFromToken(token.subjectToken));
				boolean isThereConstruction = token.value.equals("is") && token.start - 6 >= 0 && line.startsWith("there ", token.start - 6);
                boolean useThirdPerson;
                if (isThereConstruction && token.subjectToken != null && subject instanceof MultiNoun multiNoun) {
                    Noun firstNoun = multiNoun.getFirstNoun();
                    useThirdPerson = firstNoun.pluralCount() <= 1 && firstNoun.getPronoun().thirdPersonVerb;
                } else {
                    useThirdPerson = subject.pluralCount() <= 1 && subject.getPronoun().thirdPersonVerb;
                }
				String conjugatedVerb = useThirdPerson ? tokenName : VERB_MAP.get(tokenName);
				builder.append(conjugatedVerb);
			} else {
				builder.append("$").append(tokenName);
			}
			lastEnd = token.end;
		}
		builder.append(line.substring(lastEnd));
		return builder.toString();
	}

	private static boolean tokenMatchesSuffixPattern(String tokenName, String suffix, TextContext textContext) {
		return tokenName.endsWith(suffix) && textContext.getObjects().containsKey(getObjectKeyFromSuffixedToken(tokenName, suffix));
	}

	private static String getObjectKeyFromSuffixedToken(String tokenName, String suffix) {
		return tokenName.substring(0, tokenName.length() - suffix.length());
	}

	private static boolean tokenMatchesPossessiveObjectPattern(String tokenName, TextContext textContext) {
		if (tokenName == null || !tokenName.contains("'s_")) return false;
		String[] objectKeys = getObjectKeysFromPossessiveObjectToken(tokenName);
		return textContext.getObjects().containsKey(objectKeys[0]) && textContext.getObjects().containsKey(objectKeys[1]);
	}

	private static String[] getObjectKeysFromPossessiveObjectToken(String tokenName) {
		String[] parts = tokenName.split("'s_");
		if (parts.length != 2) throw new IllegalArgumentException("Invalid possessive object token: " + tokenName);
		return parts;
	}

	private static String replaceInsideBracketsWithResult(String line, char openBracketType, Context context, String originalLine, TextProcessor processor) {
		StringBuilder newLine = new StringBuilder();
		int openIndex = -1;
		int closeIndex = -1;
		Deque<Character> openBracketStack = new ArrayDeque<>();
		for (int i = 0; i < line.length(); i++) {
			char currentChar = line.charAt(i);
			if (VALID_OPEN_BRACKETS.contains(currentChar)) {
				if (openBracketStack.isEmpty() && currentChar == openBracketType) {
					openIndex = i;
				}
				openBracketStack.push(currentChar);
			} else if (VALID_CLOSE_BRACKETS.contains(currentChar)) {
				if (openBracketStack.isEmpty()) throw new IllegalArgumentException("Unmatched close bracket");
				if (currentChar != getCorrespondingCloseBracket(openBracketStack.peek())) throw new IllegalArgumentException("Mismatched brackets");
				char lastOpenBracket = openBracketStack.pop();
				if (lastOpenBracket == openBracketType && openBracketStack.isEmpty()) {
					String bracketContents = line.substring(openIndex + 1, i);
					newLine.append(line, closeIndex + 1, openIndex);
					newLine.append(processor.process(bracketContents, context, originalLine));
					closeIndex = i;
				}
			}
		}
		if (!openBracketStack.isEmpty()) throw new IllegalArgumentException("Unmatched open bracket");
		newLine.append(line.substring(closeIndex + 1));
		return newLine.toString();
	}

	private static List<String> getSeparatedStrings(String line, char separator) {
		if (VALID_OPEN_BRACKETS.contains(separator) || VALID_CLOSE_BRACKETS.contains(separator)) throw new IllegalArgumentException("Separator cannot be a bracket");
		List<String> parts = new ArrayList<>();
		int lastSeparatorIndex = -1;
		Deque<Character> openBracketStack = new ArrayDeque<>();
		for (int i = 0; i < line.length(); i++) {
			char currentChar = line.charAt(i);
			if (VALID_OPEN_BRACKETS.contains(currentChar)) {
				openBracketStack.push(currentChar);
			} else if (VALID_CLOSE_BRACKETS.contains(currentChar)) {
				if (openBracketStack.isEmpty()) throw new IllegalArgumentException("Unmatched close bracket");
				if (currentChar != getCorrespondingCloseBracket(openBracketStack.peek())) throw new IllegalArgumentException("Mismatched brackets");
				openBracketStack.pop();
			} else if (currentChar == separator && openBracketStack.isEmpty()) {
				parts.add(line.substring(lastSeparatorIndex + 1, i));
				lastSeparatorIndex = i;
			}
		}
		if (!openBracketStack.isEmpty()) throw new IllegalArgumentException("Unmatched open bracket");
		parts.add(line.substring(lastSeparatorIndex + 1));
		return parts;
	}

	private static String getSubjectKeyFromToken(TextToken token) {
		if (token.value.endsWith("_name")) {
			return token.value.substring(0, token.value.length() - 5);
		}
		return token.value;
	}

	private static String formatNoun(String name, boolean isProper, boolean isDefinite, int pluralCount) {
		if (pluralCount < 1) throw new IllegalArgumentException("Plural count is less than 1");
		if (isProper) {
			return name;
		}
		if (pluralCount == 1) {
			if (isDefinite) {
				return "the " + name;
			}
			return LangUtils.isVowel(name.charAt(0)) ? "an " + name : "a " + name;
        }
		if (isDefinite) {
			return "the " + name;
		}
		for (Map.Entry<Integer, String> entry : PLURAL_DESCRIPTORS.entrySet()) {
			if (pluralCount <= entry.getKey()) {
				return entry.getValue() + name;
			}
		}
		return DEFAULT_PLURAL_DESCRIPTOR + name;
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

	private static char getCorrespondingCloseBracket(char openBracket) {
		return switch (openBracket) {
			case '(' -> ')';
			case '{' -> '}';
			case '[' -> ']';
			default -> throw new IllegalArgumentException("Invalid open bracket: " + openBracket);
		};
	}

	private static class TextToken {
		public final int start;
		public final int end;
		public final String value;
		public final boolean isVerb;
		public boolean isSubject = false;
		public boolean usedPronoun = false;
		public TextToken subjectToken = null;

		TextToken(int start, int end, String value, boolean isVerb) {
			this.start = start;
			this.end = end;
			this.value = value;
			this.isVerb = isVerb;
		}
	}

}