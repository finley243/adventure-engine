package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuConfirmEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManager {

	private NumericMenuConfirmEvent numericMenuReturn;
	private ChoiceMenuEvent choiceMenuEvent;
	
	public MenuManager() {
		this.choiceMenuEvent = null;
		this.numericMenuReturn = null;
	}

	/*public void pauseMenu(Game game) {
		waitForContinue(game);
	}*/
	
	public void actionChoiceMenu(ActionChoiceMenuEvent event, Game game, Actor actor, List<Action> actions) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (Action action : actions) {
			menuChoices.add(action.getMenuChoices(actor));
		}
		startChoiceMenu(game, menuChoices, false);
	}

	public void attributeMenu(Game game, Actor actor, int points) {
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String attribute : game.data().getAttributeIDs()) {
			int actorBase = actor.getAttributeBase(attribute);
			menuFields.add(new NumericMenuField(attribute, game.data().getAttribute(attribute).name(), actorBase, Actor.ATTRIBUTE_MAX, actorBase));
		}
		Map<String, Integer> changedValues = waitForNumericMenuConfirm(game, menuFields, points);
		for (Map.Entry<String, Integer> changedEntry : changedValues.entrySet()) {
			actor.setAttributeBase(changedEntry.getKey(), changedEntry.getValue());
		}
	}

	public void skillMenu(Game game, Actor actor, int points) {
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String skill : game.data().getSkillIDs()) {
			int actorBase = actor.getSkillBase(skill);
			menuFields.add(new NumericMenuField(skill, game.data().getSkill(skill).name(), actorBase, Actor.SKILL_MAX, actorBase));
		}
		Map<String, Integer> changedValues = waitForNumericMenuConfirm(game, menuFields, points);
		for (Map.Entry<String, Integer> changedEntry : changedValues.entrySet()) {
			actor.setSkillBase(changedEntry.getKey(), changedEntry.getValue());
		}
	}

	public void sceneChoiceMenu(SceneChoiceMenuEvent event, Game game, List<SceneChoice> validChoices) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		startChoiceMenu(game, menuChoices, true);
	}

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
	}

	private synchronized Map<String, Integer> waitForNumericMenuConfirm(Game game, List<NumericMenuField> menuFields, int points) {
		this.numericMenuReturn = null;
		game.eventBus().post(new NumericMenuEvent(menuFields, points));
		while (numericMenuReturn == null) {
			game.threadControl().pause();
		}
		return numericMenuReturn.getChangedValues();
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		choiceMenuEvent.onChoiceMenuInput(e.getIndex());
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuConfirmEvent e) {
		this.numericMenuReturn = e;
	}
	
}
