package com.github.finley243.adventureengine.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.Choice;
import com.github.finley243.adventureengine.dialogue.Line;
import com.github.finley243.adventureengine.dialogue.Topic;
import com.github.finley243.adventureengine.dialogue.Topic.TopicType;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.google.common.eventbus.Subscribe;

public class MenuManager {

	private List<Action> actionList;
	private List<Choice> dialogueList;
	
	private Action actionChoice;
	private Choice dialogueChoice;
	
	public MenuManager() {
		this.actionList = null;
		this.dialogueList = null;
		this.actionChoice = null;
		this.dialogueChoice = null;
	}
	
	public Action actionMenu(Actor subject) {
		List<Action> actions = subject.availableActions();
		this.actionList = actions;
		List<String> menuStrings = new ArrayList<String>();
		List<String[]> menuStructures = new ArrayList<String[]>();
		for(Action action : actions) {
			menuStrings.add(action.getPrompt());
			menuStructures.add(action.getMenuStructure());
		}
		actionChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuStructures));
		while(actionChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return actionChoice;
	}
	
	public void dialogueMenu(Actor subject, Actor target) {
		boolean dialogueLoop = true;
		Topic currentTopic = Data.getTopic(target.getTopicID());
		while(dialogueLoop) {
			Game.EVENT_BUS.post(new RenderTextEvent(target.getName().toUpperCase()));
			for(Line line : currentTopic.getLines()) {
				if(line.shouldShow()) {
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
				List<Choice> validChoices = new ArrayList<Choice>();
				for(Choice choice : currentTopic.getChoices()) {
					if(choice.shouldShow()) {
						validChoices.add(choice);
					}
				}
				Game.EVENT_BUS.post(new RenderTextEvent(""));
				if(validChoices.size() > 0) {
					Choice selectedChoice = dialogueMenu(validChoices);
					currentTopic = Data.getTopic(selectedChoice.getLinkedId());
				}
				Game.EVENT_BUS.post(new RenderTextEvent(""));
			}
		}
	}
	
	private Choice dialogueMenu(List<Choice> choices) {
		this.dialogueList = choices;
		List<String> menuStrings = new ArrayList<String>();
		List<String[]> menuStructures = new ArrayList<String[]>();
		for(Choice choice : choices) {
			menuStrings.add(choice.getPrompt());
			menuStructures.add(new String[] {});
		}
		dialogueChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings, menuStructures));
		while(dialogueChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
