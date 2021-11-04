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
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;
import com.google.common.eventbus.Subscribe;

public class MenuManager {

	private List<Action> actionList;
	private List<DialogueChoice> dialogueList;

	private Action actionChoice;
	private DialogueChoice dialogueChoice;

	private Actor dialogueSubject;
	
	public MenuManager() {
		this.actionList = null;
		this.dialogueList = null;
		this.actionChoice = null;
		this.dialogueChoice = null;
		this.dialogueSubject = null;
	}
	
	public Action actionMenu(List<Action> actions) {
		this.actionList = actions;
		List<String> menuStrings = new ArrayList<>();
		List<MenuData> menuData = new ArrayList<>();
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
		dialogueSubject = subject;
		dialogueMenu(startTopic);
	}
	
	private void dialogueMenu(String topic) {
		//Game.EVENT_BUS.post(new TextClearEvent());
		boolean showChoices = true;
		DialogueTopic currentTopic = Data.getTopic(topic);
		//while(showChoices) {
			//Game.EVENT_BUS.post(new RenderTextEvent(subject.getName().toUpperCase()));
			for(DialogueLine line : currentTopic.getLines()) {
				if(line.shouldShow(dialogueSubject)) {
					for(String text : line.getTextList()) {
						Game.EVENT_BUS.post(new RenderTextEvent(text));
					}
					line.trigger(dialogueSubject);
					if(line.hasRedirect()) {
						currentTopic = Data.getTopic(line.getRedirectTopicId());
						break;
					}
					if(line.shouldExit()) {
						showChoices = false;
						break;
					}
					if(currentTopic.getType() == TopicType.SELECTOR) {
						break;
					}
				}
			}
			if(showChoices) {
				List<DialogueChoice> validChoices = new ArrayList<>();
				for(DialogueChoice choice : currentTopic.getChoices()) {
					if(choice.shouldShow(dialogueSubject)) {
						validChoices.add(choice);
					}
				}
				Game.EVENT_BUS.post(new RenderTextEvent(""));
				if(validChoices.size() > 0) {
					drawDialogueMenu(validChoices);
					//DialogueChoice selectedChoice = dialogueMenu(validChoices);
					//currentTopic = Data.getTopic(selectedChoice.getLinkedId());
					//Game.EVENT_BUS.post(new RenderTextEvent(selectedChoice.getPrompt()));
					//Game.EVENT_BUS.post(new RenderTextEvent(""));
				}
			}
		//}
	}
	
	private void drawDialogueMenu(List<DialogueChoice> choices) {
		this.dialogueList = choices;
		List<String> menuStrings = new ArrayList<>();
		List<MenuData> menuData = new ArrayList<>();
		for(DialogueChoice choice : choices) {
			menuStrings.add(choice.getPrompt());
			menuData.add(new MenuDataGlobal(choice.getPrompt()));
		}
		dialogueChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuData));
		/*while(dialogueChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		//Game.EVENT_BUS.post(new TextClearEvent());
		//return dialogueChoice;
	}

	private void onSelectAction(Action selected) {
		//TODO
	}

	private void onSelectDialogue(DialogueChoice selected) {
		Game.EVENT_BUS.post(new RenderTextEvent(selected.getPrompt()));
		Game.EVENT_BUS.post(new RenderTextEvent(""));
		dialogueMenu(selected.getLinkedId());
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent event) {
		if(actionList != null) {
			actionChoice = actionList.get(event.getIndex());
			actionList = null;
			onSelectAction(actionChoice);
		} else if(dialogueList != null) {
			dialogueChoice = dialogueList.get(event.getIndex());
			dialogueList = null;
			onSelectDialogue(dialogueChoice);
		}
	}
	
}
