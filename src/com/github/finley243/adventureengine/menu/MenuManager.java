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
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.RenderLocationEvent;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.TextClearEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;
import com.google.common.eventbus.Subscribe;

public class MenuManager {

	private List<Action> actionList;
	private List<DialogueChoice> dialogueList;
	
	private Action actionChoice;
	private DialogueChoice dialogueChoice;
	
	public MenuManager() {
		this.actionList = null;
		this.dialogueList = null;
		this.actionChoice = null;
		this.dialogueChoice = null;
	}
	
	public Action actionMenu(List<Action> actions) {
		this.actionList = actions;
		List<String> menuStrings = new ArrayList<String>();
		List<MenuData> menuData = new ArrayList<MenuData>();
		for(Action action : actions) {
			menuStrings.add(action.getPrompt());
			menuData.add(action.getMenuData());
		}
		actionChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuData));
		while(actionChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return actionChoice;
	}
	
	public void dialogueMenu(Actor subject, String startTopic) {
		Game.EVENT_BUS.post(new TextClearEvent());
		boolean dialogueLoop = true;
		DialogueTopic currentTopic = Data.getTopic(startTopic);
		while(dialogueLoop) {
			Game.EVENT_BUS.post(new RenderTextEvent(subject.getName().toUpperCase()));
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
				List<DialogueChoice> validChoices = new ArrayList<DialogueChoice>();
				for(DialogueChoice choice : currentTopic.getChoices()) {
					if(choice.shouldShow(subject)) {
						validChoices.add(choice);
					}
				}
				Game.EVENT_BUS.post(new RenderTextEvent(""));
				if(validChoices.size() > 0) {
					DialogueChoice selectedChoice = dialogueMenu(validChoices);
					currentTopic = Data.getTopic(selectedChoice.getLinkedId());
				}
				//Game.EVENT_BUS.post(new RenderTextEvent(""));
			} else {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Game.EVENT_BUS.post(new TextClearEvent());
				Game.EVENT_BUS.post(new RenderLocationEvent());
			}
		}
	}
	
	private DialogueChoice dialogueMenu(List<DialogueChoice> choices) {
		this.dialogueList = choices;
		List<String> menuStrings = new ArrayList<String>();
		List<MenuData> menuData = new ArrayList<MenuData>();
		for(DialogueChoice choice : choices) {
			menuStrings.add(choice.getPrompt());
			menuData.add(new MenuDataGlobal(choice.getPrompt()));
		}
		dialogueChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuData));
		while(dialogueChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Game.EVENT_BUS.post(new TextClearEvent());
		return dialogueChoice;
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent event) {
		if(actionList != null) {
			actionChoice = actionList.get(event.getIndex());
			actionList = null;
		} else if(dialogueList != null) {
			dialogueChoice = dialogueList.get(event.getIndex());
			dialogueList = null;
		}
	}
	
}
