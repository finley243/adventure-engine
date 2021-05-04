package com.github.finley243.adventureengine.textgen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.TextPrintEvent;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;
import com.google.common.eventbus.Subscribe;

public class TextGenerator {
	
	public static final char RANDOM_OPEN = '{';
	public static final char RANDOM_CLOSE = '}';
	public static final char RANDOM_SEPARATOR = '|';
	
	public static final String SUBJECT = "<subject>";
	public static final String SUBJECT_POSSESSIVE = "<subject's>";
	public static final String SUBJECT_REFLEXIVE = "<subjectSelf>";
	public static final String OBJECT = "<object>";
	public static final String OBJECT_POSSESSIVE = "<object's>";
	public static final String OBJECT_2 = "<object2>";
	public static final String OBJECT_2_POSSESSIVE = "<object2's>";
	
	// e.g. jumps
	public static final String VERB_S = "<s>";
	// e.g. goes/does
	public static final String VERB_ES = "<es>";
	// e.g. flies
	public static final String VERB_IES = "<ies>";
	
	public static final String VERB_DO_NOT = "<doesn't>";
	public static final String VERB_BE = "<is>";
	public static final String VERB_BE_NOT = "<isn't>";
	public static final String VERB_HAVE = "<has>";
	
	private List<TextEvent> printQueue;
	
	public TextGenerator() {
		printQueue = new ArrayList<TextEvent>();
	}
	
	@Subscribe
	public void onTextEvent(TextEvent e) {
		printQueue.add(e);
	}
	
	/*
	 * Format for tags: <subject> hit<s> <object> with <object2>
	 * Format for OR expressions: {this thing|other thing} or {<tag> thing|other thing}
	 */
	
	@Subscribe
	public void onTextPrintEvent(TextPrintEvent e) {
		String block = "";
		boolean isOpenSentence = false;
		Context lastContext = null;
		for(TextEvent clause : printQueue) {
			String line = clause.getText();
			Context context = clause.getContext();
			line = chooseRandoms(line);
			
			line = determineContext(line, context, lastContext);
			
			if(context.isCompleteSentence) {
				if(isOpenSentence) {
					block += ". ";
				}
				LangUtils.capitalize(line);
				isOpenSentence = false;
			} else {
				if(!isOpenSentence) {
					block += LangUtils.capitalize(line);
					isOpenSentence = true;
				} else {
					if(context.getBenefitting() != lastContext.getBenefitting()
					&& context.getObject().equals(lastContext.getSubject())
					&& context.getSubject().equals(lastContext.getObject())) {
						block += ", but ";
						block += line;
					} else {
						block += ". ";
						block += LangUtils.capitalize(line);
					}
				}
			}
			lastContext = context;
		}
		block += ".";
		System.out.println(block);
		printQueue.clear();
	}
	
	private String determineContext(String line, Context context, Context lastContext) {
		boolean useSubjectPronoun = false;
		boolean useObjectPronoun = false;
		boolean useObject2Pronoun = false;
		if(lastContext != null && lastContext.getSubject().equals(context.getSubject())) {
			if(context.getSubject().equals(Data.getActor(Game.PLAYER_ACTOR))) {
				useSubjectPronoun = true;
			}
		}
		if(lastContext != null && lastContext.getObject().equals(context.getObject())) {
			if(context.getObject().getPronoun() != context.getSubject().getPronoun()) {
				useObjectPronoun = true;
			}
		}
		if(lastContext != null && lastContext.getObject2().equals(context.getObject2())) {
			if(context.getObject2().getPronoun() != context.getSubject().getPronoun()
			&& context.getObject2().getPronoun() != context.getObject().getPronoun()) {
				useObject2Pronoun = true;
			}
		}
		if(context.getSubject().equals(Data.getActor(Game.PLAYER_ACTOR))) {
			useSubjectPronoun = true;
		}
		if(context.getObject().equals(Data.getActor(Game.PLAYER_ACTOR))) {
			useObjectPronoun = true;
		}
		if(context.getObject2().equals(Data.getActor(Game.PLAYER_ACTOR))) {
			useObject2Pronoun = true;
		}
		return populateFromContext(line, context, useSubjectPronoun, useObjectPronoun, useObject2Pronoun);
	}
	
	private String chooseRandoms(String line) {
		List<String> parts = new ArrayList<String>();
		int openIndex = -1;
		int closeIndex = -1;
		int depth = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == RANDOM_OPEN) {
				if(depth == 0) {
					openIndex = i;
				}
				depth++;
			} else if(line.charAt(i) == RANDOM_CLOSE) {
				depth--;
				if(depth == 0) {
					String lineInBrackets = line.substring(openIndex + 1, i);
					List<String> randomChoices = separateRandomChoices(lineInBrackets);
					String randomChoice = randomChoices.get(ThreadLocalRandom.current().nextInt(randomChoices.size()));
					parts.add(line.substring(closeIndex + 1, openIndex));
					parts.add(chooseRandoms(randomChoice));
					closeIndex = i;
				}
			}
		}
		parts.add(line.substring(closeIndex + 1, line.length()));
		String newLine = "";
		for(String current : parts) {
			newLine += current;
		}
		return newLine;
	}
	
	private List<String> separateRandomChoices(String line){
		List<String> parts = new ArrayList<String>();
		int indexOfLastSplit = -1;
		int depth = 0;
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == RANDOM_OPEN) {
				depth++;
			} else if(line.charAt(i) == RANDOM_CLOSE) {
				depth--;
			} else if(depth == 0 && line.charAt(i) == RANDOM_SEPARATOR) {
				parts.add(line.substring(indexOfLastSplit + 1, i).trim());
				indexOfLastSplit = i;
			}
		}
		parts.add(line.substring(indexOfLastSplit + 1, line.length()).trim());
		return parts;
	}
	
	private String populateFromContext(String line, Context context, boolean useSubjectPronoun, boolean useObjectPronoun, boolean useObject2Pronoun) {
		Noun subject = context.getSubject();
		Noun object = context.getObject();
		Noun object2 = context.getObject2();
		if(useSubjectPronoun) {
			line = line.replace(SUBJECT, subject.getPronoun().subject);
			line = line.replace(SUBJECT_POSSESSIVE, subject.getPronoun().possessive);
		} else {
			line = line.replaceFirst(SUBJECT, (subject.isProperName() ? "" : "the ") + subject.getName());
			line = line.replace(SUBJECT, subject.getPronoun().subject);
			line = line.replaceFirst(SUBJECT_POSSESSIVE, (subject.isProperName() ? "" : "the ") + LangUtils.possessive(subject.getName(), false));
			line = line.replace(SUBJECT_POSSESSIVE, subject.getPronoun().possessive);
		}
		line = line.replace(SUBJECT_REFLEXIVE, subject.getPronoun().reflexive);
		
		if(useObjectPronoun) {
			line = line.replace(OBJECT, object.getPronoun().object);
			line = line.replace(OBJECT_POSSESSIVE, object.getPronoun().possessive);
		} else {
			line = line.replaceFirst(OBJECT, (object.isProperName() ? "" : "the ") + object.getName());
			line = line.replace(OBJECT, object.getPronoun().object);
			line = line.replaceFirst(OBJECT_POSSESSIVE, (object.isProperName() ? "" : "the ") + LangUtils.possessive(object.getName(), false));
			line = line.replace(OBJECT_POSSESSIVE, object.getPronoun().possessive);
		}
		
		if(useObject2Pronoun) {
			line = line.replace(OBJECT_2, object2.getPronoun().object);
			line = line.replace(OBJECT_2_POSSESSIVE, object2.getPronoun().possessive);
		} else {
			line = line.replaceFirst(OBJECT_2, (object2.isProperName() ? "" : "the ") + object2.getName());
			line = line.replace(OBJECT_2, object2.getPronoun().object);
			line = line.replaceFirst(OBJECT_2_POSSESSIVE, (object2.isProperName() ? "" : "the ") + LangUtils.possessive(object2.getName(), false));
			line = line.replace(OBJECT_2_POSSESSIVE, object2.getPronoun().possessive);
		}
		
		line = line.replace(VERB_S, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "s" : ""));
		line = line.replace(VERB_ES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "es" : ""));
		line = line.replace(VERB_IES, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "ies" : "y"));
		line = line.replace(VERB_DO_NOT, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "doesn't" : "don't"));
		line = line.replace(VERB_BE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "is" : (subject.getPronoun() == Pronoun.I ? "am" : "are")));
		line = line.replace(VERB_BE_NOT, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "isn't" : (subject.getPronoun() == Pronoun.I ? "am not" : "aren't")));
		line = line.replace(VERB_HAVE, (!useSubjectPronoun || subject.getPronoun().thirdPersonVerb ? "has" : "have"));
		return line;
	}
	
}
