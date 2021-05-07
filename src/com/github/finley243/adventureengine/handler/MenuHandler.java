package com.github.finley243.adventureengine.handler;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.dialogue.Choice;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.EndPlayerTurnEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.google.common.eventbus.Subscribe;

public class MenuHandler {

	private List<Action> actionList;
	private List<Choice> dialogueList;
	
	private Action actionChoice;
	private Choice dialogueChoice;
	
	public MenuHandler() {
		this.actionList = null;
		this.dialogueList = null;
		this.actionChoice = null;
		this.dialogueChoice = null;
	}
	
	public Action actionMenu(List<Action> actions) {
		this.actionList = actions;
		List<String> menuStrings = new ArrayList<String>();
		for(Action action : actions) {
			menuStrings.add(action.getChoiceName());
		}
		actionChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings));
		while(actionChoice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return actionChoice;
	}
	
	public Choice dialogueMenu(List<Choice> choices) {
		this.dialogueList = choices;
		List<String> menuStrings = new ArrayList<String>();
		for(Choice choice : choices) {
			menuStrings.add(choice.getPrompt());
			System.out.println("CHOICE: " + choice.getPrompt());
		}
		dialogueChoice = null;
		Game.EVENT_BUS.post(new RenderMenuEvent(menuStrings));
		System.out.println("REACHED EVENT BUS POST");
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
			Game.EVENT_BUS.post(new EndPlayerTurnEvent());
		} else if(dialogueList != null) {
			dialogueChoice = dialogueList.get(event.getIndex());
			dialogueList = null;
		}
	}
	
}
