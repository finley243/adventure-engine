package personal.finley.adventure_engine_2.menu;

import java.util.ArrayList;
import java.util.List;
import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.dialogue.Topic;

public class MenuDialogue {
	
	public static void buildMenuDialogue(Actor target) {
		boolean dialogueLoop = true;
		Topic currentTopic = Data.getTopic(target.getTopic());
		while(dialogueLoop) {
			System.out.println(target.getName().toUpperCase());
			List<String> choices = currentTopic.getChoices();
			List<Topic> choiceTopics = new ArrayList<Topic>();
			for(String currentChoice : choices) {
				choiceTopics.add(Data.getTopic(currentChoice));
			}
			for(int i = 0; i < choiceTopics.size(); i++) {
				System.out.println((i+1) + ") " + choiceTopics.get(i).getPrompt());
			}
			int response = UserInput.intInRange(1, choiceTopics.size());
			currentTopic = choiceTopics.get(response - 1);
			System.out.println();
			if(currentTopic.exit()) {
				dialogueLoop = false;
			}
		}
	}
	
}
