package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TextGen {

	private static final char RANDOM_OPEN = '{';
	private static final char RANDOM_CLOSE = '}';
	private static final char RANDOM_SEPARATOR = '|';

	private static final String SUBJECT = "$subject";
	private static final String SUBJECT_POSSESSIVE = "$subject's";
	private static final String SUBJECT_REFLEXIVE = "$subjectSelf";

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
	
	private static Context lastContext;

	/*
	 * Format for tags: $subject hit$s $object1 with $object2
	 * Format for OR expressions: {this thing|other thing} or {$tag thing|other thing}
	 */

	public static String generate(String line, Context context) {
		String sentence = "";
		line = chooseRandoms(line);
		line = determineContext(line, context);
		sentence += LangUtils.capitalize(line);
		sentence += ".";
		lastContext = context;
		//context.getSubject().setKnown();
		for(Noun object : context.getObjects().values()) {
			object.setKnown();
		}
		return sentence;
	}
	
	public static void clearContext() {
		lastContext = null;
	}

	private static String determineContext(String line, Context context) {
		boolean useSubjectPronoun = false;
		boolean[] useObjectPronouns = new boolean[context.getObjects().size()];
		if(lastContext != null) {
			/*if(context.getSubject() == lastContext.getSubject()) {
				useSubjectPronoun = true;
			}*/
			/*for(int i = 0; i < context.getObjects().length; i++) {
				Noun object = context.getObjects()[i];
				if(lastContext.getObjects().length > 0) {
					if (lastContext.getObjects().length <= i && object == lastContext.getObjects()[lastContext.getObjects().length - 1]
							|| lastContext.getObjects().length < i && object == lastContext.getObjects()[i]) {
						if (!matchesAnyPronounsUpToObjectIndex(lastContext, object.getPronoun(), i, useSubjectPronoun, useObjectPronouns)) {
							useObjectPronouns[i] = true;
						}
					}
				}
			}*/
			List<Noun> objectList = new ArrayList<>(context.getObjects().values());
			List<Noun> lastObjectList = new ArrayList<>(lastContext.getObjects().values());
			for(int i = 0; i < objectList.size(); i++) {
				Noun object = objectList.get(i);
				if(lastObjectList.size() > 0) {
					if (lastObjectList.size() <= i && object == lastObjectList.get(lastObjectList.size() - 1)
							|| lastObjectList.size() < i && object == lastObjectList.get(i)) {
						if (!matchesAnyPronounsUpToObjectIndex(lastContext, object.getPronoun(), i, useSubjectPronoun, useObjectPronouns)) {
							useObjectPronouns[i] = true;
						}
					}
				}
			}
		}
		List<String> objectTagList = new ArrayList<>(context.getObjects().keySet());
		Map<String, Boolean> useObjectPronounsMap = new LinkedHashMap<>();
		for(int i = 0; i < objectTagList.size(); i++) {
			useObjectPronounsMap.put(objectTagList.get(i), useObjectPronouns[i]);
		}
		/*if (context.getSubject().forcePronoun()) {
			useSubjectPronoun = true;
		}*/
		/*for(int i = 0; i < context.getObjects().length; i++) {
			if(context.getObjects()[i].forcePronoun()) {
				useObjectPronouns[i] = true;
			}
		}*/
		for(String objectTag : context.getObjects().keySet()) {
			if(context.getObjects().get(objectTag).forcePronoun()) {
				useObjectPronounsMap.put(objectTag, true);
			}
		}
		return populateFromContext(line, context, useSubjectPronoun, useObjectPronounsMap);
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

	private static String populateFromContext(String line, Context context, boolean useSubjectPronoun,
											  Map<String, Boolean> useObjectPronouns) {
		//Noun subject = context.getSubject();
		//Noun[] objects = context.getObjects();
		Map<String, Noun> objects = context.getObjects();

		/*line = populatePronoun(line, useSubjectPronoun, subject.getFormattedName(),
				subject.getPronoun().subject, subject.getPronoun().possessive, SUBJECT, SUBJECT_POSSESSIVE);
		line = line.replace(SUBJECT_REFLEXIVE, subject.getPronoun().reflexive);*/

		List<String> objectTags = new ArrayList<>(objects.keySet());
		objectTags.sort(Comparator.comparingInt(String::length));
		Collections.reverse(objectTags);

		/*for(int i = objects.length - 1; i >= 0; i--) {
			 line = populatePronoun(line, useObjectPronouns[i], objects[i].getFormattedName(), objects[i].getPronoun().object
					 , objects[i].getPronoun().possessive, "$object" + (i+1), "$object" + (i+1) + "'s");
		}*/
		for(String objectTag : objectTags) {
			// Object
			line = populatePronoun(line, useObjectPronouns.get(objectTag), objects.get(objectTag).getFormattedName(), objects.get(objectTag).getPronoun().object
					, objects.get(objectTag).getPronoun().possessive, "$" + objectTag, "$" + objectTag + "'s");
			// Subject
			line = populatePronoun(line, useObjectPronouns.get(objectTag), objects.get(objectTag).getFormattedName(), objects.get(objectTag).getPronoun().subject
					, objects.get(objectTag).getPronoun().possessive, "$_" + objectTag, "$" + objectTag + "'s");
			// Reflexive
			line = line.replace("$" + objectTag + "_self", objects.get(objectTag).getPronoun().reflexive);
		}

		List<String> varTags = new ArrayList<>(context.getVars().keySet());
		varTags.sort(Comparator.comparingInt(String::length));
		Collections.reverse(varTags);
		for(String varTag : varTags) {
			line = line.replace("$" + varTag, context.getVars().get(varTag));
		}

		/*line = line.replace(VERB_S, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "s" : ""));
		line = line.replace(VERB_ES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "es" : ""));
		line = line.replace(VERB_IES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "ies" : "y"));
		line = line.replace(VERB_DO_NOT,
				(!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "doesn't" : "don't"));
		line = line.replace(VERB_BE_NOT, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "isn't"
				: (subject.getPronoun() == Pronoun.I ? "am not" : "aren't")));
		line = line.replace(VERB_BE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "is"
				: (subject.getPronoun() == Pronoun.I ? "am" : "are")));
		line = line.replace(VERB_HAVE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "has" : "have"));*/
		for(String objectTag : objectTags) {
			System.out.println("Object: " + objectTag + ", line: " + line);
			line = line.replace(VERB_S + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "s" : ""));
			line = line.replace(VERB_ES + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "es" : ""));
			line = line.replace(VERB_IES + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "ies" : "y"));
			line = line.replace(VERB_DO_NOT + "_" + objectTag,
					(!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "doesn't" : "don't"));
			line = line.replace(VERB_BE_NOT + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "isn't"
					: (objects.get(objectTag).getPronoun() == Pronoun.I ? "am not" : "aren't")));
			line = line.replace(VERB_BE + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "is"
					: (objects.get(objectTag).getPronoun() == Pronoun.I ? "am" : "are")));
			line = line.replace(VERB_HAVE + "_" + objectTag, (!useObjectPronouns.get(objectTag) || objects.get(objectTag).getPronoun().thirdPersonVerb ? "has" : "have"));
			System.out.println(line);
		}
		return line;
	}

	private static String populatePronoun(String line, boolean usePronoun, String formattedName, String pronoun,
			String possessive, String pronounKey, String possessiveKey) {
		if (usePronoun) {
			line = line.replace(possessiveKey, possessive);
			line = line.replace(pronounKey, pronoun);
		} else {
			int indexOf = line.indexOf(pronounKey);
			int indexOfPossessive = line.indexOf(possessiveKey);
			boolean possessiveFirst = (indexOfPossessive != -1) && ((indexOf == -1) || (indexOf > indexOfPossessive));
			if (possessiveFirst) {
				line = line.replaceFirst("\\" + possessiveKey, LangUtils.possessive(formattedName, false));
			}
			line = line.replace(possessiveKey, possessive);
			if (!possessiveFirst) {
				line = line.replaceFirst("\\" + pronounKey, formattedName);
			}
			line = line.replace(pronounKey, pronoun);
		}
		return line;
	}

	// Returns whether there is a matching (and used) pronoun in context that is below the given index
	private static boolean matchesAnyPronounsUpToObjectIndex(Context context, Pronoun pronoun, int index, boolean useSubjectPronoun, boolean[] useObjectPronouns) {
		//if(useSubjectPronoun && context.getSubject().getPronoun() == pronoun) return true;
		List<Noun> objectsList = new ArrayList<>(context.getObjects().values());
		for(int i = 0; i < Math.min(objectsList.size(), index - 1); i++) {
			Pronoun objectPronoun = objectsList.get(i).getPronoun();
			if(useObjectPronouns[i] && objectPronoun == pronoun) return true;
		}
		return false;
	}

}
