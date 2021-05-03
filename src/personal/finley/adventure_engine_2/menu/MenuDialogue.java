package personal.finley.adventure_engine_2.menu;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.dialogue.Choice;
import personal.finley.adventure_engine_2.dialogue.Line;
import personal.finley.adventure_engine_2.dialogue.Topic;

public class MenuDialogue {
	
	public static void buildMenuDialogue(Actor target) {
		boolean dialogueLoop = true;
		Topic currentTopic = Data.getTopic(target.getTopicID());
		while(dialogueLoop) {
			System.out.println(target.getName().toUpperCase());
			for(Line line : currentTopic.getLines()) {
				if(line.shouldShow()) {
					for(String text : line.getTextList()) {
						System.out.println(text);
					}
					line.trigger();
					if(line.hasRedirect()) {
						currentTopic = Data.getTopic(line.getRedirectTopicId());
					}
					if(line.shouldExit()) {
						dialogueLoop = false;
						break;
					}
				}
			}
			if(dialogueLoop) {
				List<Choice> validChoices = new ArrayList<Choice>();
				for(Choice choice : currentTopic.getChoices()) {
					if(choice.shouldShow()) {
						validChoices.add(choice);
					}
				}
				System.out.println();
				if(validChoices.size() > 0) {
					for(int i = 0; i < validChoices.size(); i++) {
						System.out.println((i+1) + ") " + validChoices.get(i).getPrompt());
					}
					int response = UserInput.intInRange(1, validChoices.size());
					currentTopic = Data.getTopic(validChoices.get(response - 1).getLinkedId());
				}
			}
			System.out.println();
		}
	}
	
}
