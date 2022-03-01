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

	private static final String SUBJECT = "<subject>";
	private static final String SUBJECT_POSSESSIVE = "<subject's>";
	private static final String SUBJECT_REFLEXIVE = "<subjectSelf>";
	private static final String OBJECT = "<object>";
	private static final String OBJECT_POSSESSIVE = "<object's>";
	private static final String OBJECT_2 = "<object2>";
	private static final String OBJECT_2_POSSESSIVE = "<object2's>";

	// e.g. jumps
	private static final String VERB_S = "<s>";
	// e.g. goes/does
	private static final String VERB_ES = "<es>";
	// e.g. flies
	private static final String VERB_IES = "<ies>";

	private static final String VERB_DO_NOT = "<doesn't>";
	private static final String VERB_BE = "<is>";
	private static final String VERB_BE_NOT = "<isn't>";
	private static final String VERB_HAVE = "<has>";
	
	private static Context lastContext;

	/*
	 * Format for tags: <subject> hit<s> <object> with <object2> Format for OR
	 * expressions: {this thing|other thing} or {<tag> thing|other thing}
	 */

	public static String generate(String line, Context context) {
		String sentence = "";
		line = chooseRandoms(line);
		line = determineContext(line, context);
		sentence += LangUtils.capitalize(line);
		sentence += ".";
		lastContext = context;
		context.getSubject().setKnown();
		context.getObject().setKnown();
		context.getObject2().setKnown();
		return sentence;
	}
	
	public static void clearContext() {
		lastContext = null;
	}

	private static String determineContext(String line, Context context) {
		boolean useSubjectPronoun = false;
		boolean useObjectPronoun = false;
		boolean useObject2Pronoun = false;
		if(lastContext != null) {
			if(context.getSubject().equals(lastContext.getSubject())) {
				useSubjectPronoun = true;
			}
			if(context.getObject().equals(lastContext.getObject())) {
				if(!useSubjectPronoun || context.getObject().getPronoun() != context.getSubject().getPronoun()) {
					useObjectPronoun = true;
				}
			}
			if(context.getObject2().equals(lastContext.getObject2())) {
				if((!useSubjectPronoun || context.getObject2().getPronoun() != context.getSubject().getPronoun()) &&
					(!useObjectPronoun || context.getObject2().getPronoun() != context.getObject().getPronoun())) {
					useObject2Pronoun = true;
				}
			}
		}
		if (context.getSubject().equals(Data.getPlayer())) {
			useSubjectPronoun = true;
		}
		if (context.getObject().equals(Data.getPlayer())) {
			useObjectPronoun = true;
		}
		if (context.getObject2().equals(Data.getPlayer())) {
			useObject2Pronoun = true;
		}
		return populateFromContext(line, context, useSubjectPronoun, useObjectPronoun, useObject2Pronoun);
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
			boolean useObjectPronoun, boolean useObject2Pronoun) {
		Noun subject = context.getSubject();
		Noun object = context.getObject();
		Noun object2 = context.getObject2();

		line = populatePronoun(line, useSubjectPronoun, subject.getFormattedName(/*context.indefiniteSubject()*/),
				subject.getPronoun().subject, subject.getPronoun().possessive, SUBJECT, SUBJECT_POSSESSIVE);
		line = line.replace(SUBJECT_REFLEXIVE, subject.getPronoun().reflexive);

		line = populatePronoun(line, useObjectPronoun, object.getFormattedName(/*context.indefiniteObject()*/),
				object.getPronoun().object, object.getPronoun().possessive, OBJECT, OBJECT_POSSESSIVE);

		line = populatePronoun(line, useObject2Pronoun, object2.getFormattedName(/*context.indefiniteObject2()*/),
				object2.getPronoun().object, object2.getPronoun().possessive, OBJECT_2, OBJECT_2_POSSESSIVE);

		line = line.replace(VERB_S, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "s" : ""));
		line = line.replace(VERB_ES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "es" : ""));
		line = line.replace(VERB_IES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "ies" : "y"));
		line = line.replace(VERB_DO_NOT,
				(!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "doesn't" : "don't"));
		line = line.replace(VERB_BE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "is"
				: (subject.getPronoun() == Pronoun.I ? "am" : "are")));
		line = line.replace(VERB_BE_NOT, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "isn't"
				: (subject.getPronoun() == Pronoun.I ? "am not" : "aren't")));
		line = line.replace(VERB_HAVE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "has" : "have"));
		return line;
	}

	private static String populatePronoun(String line, boolean usePronoun, String formattedName, String pronoun,
			String possessive, String pronounKey, String possessiveKey) {
		if (usePronoun) {
			line = line.replace(pronounKey, pronoun);
		} else {
			int indexOf = line.indexOf(pronounKey);
			int indexOfPossessive = line.indexOf(possessiveKey);
			boolean possessiveFirst = (indexOfPossessive != -1) && ((indexOf == -1) || (indexOf > indexOfPossessive));
			if (!possessiveFirst) {
				line = line.replaceFirst(pronounKey, formattedName);
			}
			line = line.replace(pronounKey, pronoun);
			if (possessiveFirst) {
				line = line.replaceFirst(possessiveKey, LangUtils.possessive(formattedName, false));
			}
		}
		line = line.replace(possessiveKey, possessive);
		return line;
	}

}
