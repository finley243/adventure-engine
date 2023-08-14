package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ProvideActionsEvent;
import com.github.finley243.adventureengine.event.SceneChoiceEvent;
import com.github.finley243.adventureengine.event.SelectActionEvent;
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
	private boolean isSceneMenuOpen;
	private ProvideActionsEvent actionMenuEvent;
	private SceneChoiceEvent sceneChoiceEvent;
	private List<SceneChoice> sceneChoices;
	
	public MenuManager() {
		this.numericMenuReturn = null;
	}

	/*public void pauseMenu(Game game) {
		waitForContinue(game);
	}*/
	
	public void actionMenu(ProvideActionsEvent e) {
		actionMenuEvent = e;
		if (!isSceneMenuOpen) {
			List<MenuChoice> menuChoices = new ArrayList<>();
			for (Action action : e.actions()) {
				menuChoices.add(action.getMenuChoices(e.subject()));
			}
			startChoiceMenu(e.subject().game(), menuChoices, false);
		}
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

	public void sceneChoiceMenu(SceneChoiceEvent sceneChoiceEvent) {
		isSceneMenuOpen = true;
		this.sceneChoiceEvent = sceneChoiceEvent;
		List<SceneChoice> validChoices = new ArrayList<>();
		for (SceneChoice choice : sceneChoiceEvent.getChoices()) {
			if (sceneChoiceEvent.getContext().game().data().getScene(choice.getLinkedId()).canChoose(sceneChoiceEvent.getContext())) {
				validChoices.add(choice);
			}
		}
		if (validChoices.isEmpty()) {
			isSceneMenuOpen = false;
			sceneChoiceEvent.getContext().game().eventQueue().executeNext();
			return;
		}
		sceneChoices = validChoices;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		startChoiceMenu(sceneChoiceEvent.getContext().game(), menuChoices, true);
	}

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
	}

	private void onActionMenuInput(int choiceIndex) {
		Action selectedAction = actionMenuEvent.actions().get(choiceIndex);
		actionMenuEvent.subject().onSelectAction(new SelectActionEvent(selectedAction, actionMenuEvent.lastAction(), actionMenuEvent.actionRepeatCount()));
	}

	private void onSceneMenuInput(int choiceIndex) {
		SceneChoice selectedChoice = sceneChoices.get(choiceIndex);
		sceneChoices = null;
		isSceneMenuOpen = false;
		sceneChoiceEvent.onMenuInput(selectedChoice);
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
		if (isSceneMenuOpen) {
			onSceneMenuInput(e.getIndex());
		} else {
			onActionMenuInput(e.getIndex());
		}
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuConfirmEvent e) {
		this.numericMenuReturn = e;
	}
	
}
