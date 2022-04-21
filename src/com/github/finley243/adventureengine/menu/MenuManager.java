package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.DialogueChoice;
import com.github.finley243.adventureengine.dialogue.DialogueLine;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.dialogue.DialogueTopic.TopicType;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

	private int index;
	
	public MenuManager() {
		this.index = -1;
	}
	
	public Action actionMenu(List<Action> actions, Actor subject) {
		List<MenuData> menuData = new ArrayList<>();
		for(Action action : actions) {
			menuData.add(action.getMenuData(subject));
		}
		int actionIndex = getMenuInput(subject.game(), menuData);
		return actions.get(actionIndex);
	}
	
	public void dialogueMenu(Actor subject, String startTopic) {
		boolean dialogueLoop = true;
		DialogueTopic currentTopic = subject.game().data().getTopic(startTopic);
		while(dialogueLoop) {
			currentTopic.setVisited(subject);
			for(DialogueLine line : currentTopic.getLines()) {
				if(line.shouldShow(subject)) {
					for(String text : line.getTextList()) {
						subject.game().eventBus().post(new RenderTextEvent(text));
					}
					line.trigger(subject);
					if(line.hasRedirect()) {
						currentTopic = subject.game().data().getTopic(line.getRedirectTopicId());
						break;
					}
					if(line.shouldExit()) {
						dialogueLoop = false;
						break;
					}
					if(currentTopic.getType() == TopicType.SELECTOR) {
						break;
					}
				}
			}
			if(dialogueLoop) {
				List<DialogueChoice> validChoices = new ArrayList<>();
				for(DialogueChoice choice : currentTopic.getChoices()) {
					if(subject.game().data().getTopic(choice.getLinkedId()).canChoose(subject)) {
						validChoices.add(choice);
					}
				}
				subject.game().eventBus().post(new RenderTextEvent(""));
				if(validChoices.size() > 0) {
					DialogueChoice selectedChoice = dialogueMenuInput(subject.game(), validChoices);
					currentTopic = subject.game().data().getTopic(selectedChoice.getLinkedId());
					subject.game().eventBus().post(new RenderTextEvent(selectedChoice.getPrompt()));
					subject.game().eventBus().post(new RenderTextEvent(""));
				}
			}
		}
	}
	
	private DialogueChoice dialogueMenuInput(Game game, List<DialogueChoice> choices) {
		List<MenuData> menuData = new ArrayList<>();
		for(DialogueChoice choice : choices) {
			menuData.add(new MenuData(choice.getPrompt(), true));
		}
		int dialogueIndex = getMenuInput(game, menuData);
		return choices.get(dialogueIndex);
	}

	private synchronized int getMenuInput(Game game, List<MenuData> menuData) {
		this.index = -1;
		game.eventBus().post(new RenderMenuEvent(menuData));
		while(this.index == -1) {
			game.threadControl().pause();
		}
		return this.index;
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		this.index = e.getIndex();
	}
	
}
