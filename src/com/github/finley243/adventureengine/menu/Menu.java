package com.github.finley243.adventureengine.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.Choice;
import com.github.finley243.adventureengine.dialogue.Line;
import com.github.finley243.adventureengine.dialogue.Topic;
import com.github.finley243.adventureengine.dialogue.Topic.TopicType;

public class Menu {
	
	/*public static Action buildActionMenu(List<Action> actions) {
		for(int i = 0; i < actions.size(); i++) {
			System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
		}
		
		int inputInt = InputUtils.intInRange(1, actions.size());
		System.out.println();
		//actions.get(inputInt - 1).choose(Data.getPlayer());
		return actions.get(inputInt - 1);
	}*/
	
	public static void buildDialogueMenu(Actor target) {
		boolean menuLoop = true;
		Topic currentTopic = Data.getTopic(target.getTopicID());
		while(menuLoop) {
			System.out.println(target.getName().toUpperCase());
			for(Line line : currentTopic.getLines()) {
				if(line.shouldShow()) {
					for(String text : line.getTextList()) {
						System.out.println(text);
					}
					line.trigger();
					if(line.hasRedirect()) {
						currentTopic = Data.getTopic(line.getRedirectTopicId());
						break;
					}
					if(line.shouldExit()) {
						menuLoop = false;
						break;
					}
					if(currentTopic.getType() == TopicType.SELECTOR) {
						break;
					}
				}
			}
			if(menuLoop) {
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
					int response = InputUtils.intInRange(1, validChoices.size());
					currentTopic = Data.getTopic(validChoices.get(response - 1).getLinkedId());
				}
				System.out.println();
			}
		}
	}
	
}
