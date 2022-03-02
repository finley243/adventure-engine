package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

import java.util.ArrayList;
import java.util.List;
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
		context.getSubject().setKnown();
		for(Noun object : context.getObjects()) {
			object.setKnown();
		}
		return sentence;
	}
	
	public static void clearContext() {
		lastContext = null;
	}

	private static String determineContext(String line, Context context) {
		boolean useSubjectPronoun = false;
		boolean[] useObjectPronouns = new boolean[context.getObjects().length];
		if(lastContext != null) {
			if(context.getSubject().equals(lastContext.getSubject())) {
				useSubjectPronoun = true;
			}
			for(int i = 0; i < context.getObjects().length; i++) {
				Noun object = context.getObjects()[i];
				if(lastContext.getObjects().length == 0
						|| lastContext.getObjects().length <= i && object.equals(lastContext.getObjects()[lastContext.getObjects().length - 1])
						|| object.equals(lastContext.getObjects()[i])) {
					if(!matchesAnyPronounsUpToObjectIndex(lastContext, object.getPronoun(), i, useSubjectPronoun, useObjectPronouns)) {
						useObjectPronouns[i] = true;
					}
				}
			}
		}
		if (context.getSubject().equals(Data.getPlayer())) {
			useSubjectPronoun = true;
		}
		for(int i = 0; i < context.getObjects().length; i++) {
			if(context.getObjects()[i].equals(Data.getPlayer())) {
				useObjectPronouns[i] = true;
			}
		}
		return populateFromContext(line, context, useSubjectPronoun, useObjectPronouns);
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
		String newLine = "";
		for (String current : parts) {
			newLine += current;
		}
		return newLine;
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
			boolean[] useObjectPronouns) {
		Noun subject = context.getSubject();
		Noun[] objects = context.getObjects();

		line = populatePronoun(line, useSubjectPronoun, subject.getFormattedName(),
				subject.getPronoun().subject, subject.getPronoun().possessive, SUBJECT, SUBJECT_POSSESSIVE);
		line = line.replace(SUBJECT_REFLEXIVE, subject.getPronoun().reflexive);

		for(int i = 0; i < objects.length; i++) {
			 line = populatePronoun(line, useObjectPronouns[i], objects[i].getFormattedName(), objects[i].getPronoun().object
					 , objects[i].getPronoun().possessive, "$object" + (i+1), "$object" + (i+1) + "'s");
		}

		for(String varTag : context.getVars().keySet()) {
			line = line.replace("$" + varTag, context.getVars().get(varTag));
		}

		line = line.replace(VERB_S, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "s" : ""));
		line = line.replace(VERB_ES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "es" : ""));
		line = line.replace(VERB_IES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "ies" : "y"));
		line = line.replace(VERB_DO_NOT,
				(!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "doesn't" : "don't"));
		line = line.replace(VERB_BE_NOT, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "isn't"
				: (subject.getPronoun() == Pronoun.I ? "am not" : "aren't")));
		line = line.replace(VERB_BE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "is"
				: (subject.getPronoun() == Pronoun.I ? "am" : "are")));
		line = line.replace(VERB_HAVE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "has" : "have"));
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
		if(useSubjectPronoun && context.getSubject().getPronoun() == pronoun) return true;
		for(int i = 0; i < Math.min(context.getObjects().length, index - 1); i++) {
			Pronoun objectPronoun = context.getObjects()[i].getPronoun();
			if(useObjectPronouns[i] && objectPronoun == pronoun) return true;
		}
		return false;
	}

}
