package com.github.finley243.adventureengine.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Data;
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
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;
import com.github.finley243.adventureengine.network.Network;
import com.github.finley243.adventureengine.network.action.NetworkAction;
import com.google.common.eventbus.Subscribe;

public class MenuManager {

	private int index;
	
	public MenuManager() {
		this.index = -1;
	}
	
	public Action actionMenu(List<Action> actions) {
		List<String> menuStrings = new ArrayList<>();
		List<MenuData> menuData = new ArrayList<>();
		for(Action action : actions) {
			menuStrings.add(action.getPrompt());
			menuData.add(action.getMenuData());
		}
		int actionIndex = getMenuInput(menuStrings, menuData);
		return actions.get(actionIndex);
	}

	public void networkMenu(Network network, int startLevel) {
		boolean networkLoop = true;
		int level = startLevel;
		while(networkLoop) {
			List<NetworkAction> levelActions = network.getActionsAtLevel(level);
		}
	}
	
	public void dialogueMenu(Actor subject, String startTopic) {
		boolean dialogueLoop = true;
		DialogueTopic currentTopic = Data.getTopic(startTopic);
		while(dialogueLoop) {
			for(DialogueLine line : currentTopic.getLines()) {
				if(line.shouldShow(subject)) {
					for(String text : line.getTextList()) {
						Game.EVENT_BUS.post(new RenderTextEvent(text));
					}
					line.trigger(subject);
					if(line.hasRedirect()) {
						currentTopic = Data.getTopic(line.getRedirectTopicId());
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
					if(choice.shouldShow(subject)) {
						validChoices.add(choice);
					}
				}
				Game.EVENT_BUS.post(new RenderTextEvent(""));
				if(validChoices.size() > 0) {
					DialogueChoice selectedChoice = dialogueMenuInput(validChoices);
					selectedChoice.trigger(subject);
					currentTopic = Data.getTopic(selectedChoice.getLinkedId());
					Game.EVENT_BUS.post(new RenderTextEvent(selectedChoice.getPrompt()));
					Game.EVENT_BUS.post(new RenderTextEvent(""));
				}
			}
		}
	}
	
	private DialogueChoice dialogueMenuInput(List<DialogueChoice> choices) {
		List<String> menuStrings = new ArrayList<>();
		List<MenuData> menuData = new ArrayList<>();
		for(DialogueChoice choice : choices) {
			menuStrings.add(choice.getPrompt());
			menuData.add(new MenuDataGlobal(choice.getPrompt()));
		}
		int dialogueIndex = getMenuInput(menuStrings, menuData);
		return choices.get(dialogueIndex);
	}

	private synchronized int getMenuInput(List<String> menuStrings, List<MenuData> menuData) {
		this.index = -1;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuData));
		while(this.index == -1) {
			Game.THREAD_CONTROL.pause();
		}
		return this.index;
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		this.index = e.getIndex();
	}
	
}
