package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

	private ChoiceMenuEvent choiceMenuEvent;
	private NumericMenuEvent numericMenuEvent;
	
	public MenuManager() {
		this.choiceMenuEvent = null;
		this.numericMenuEvent = null;
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

	public void sceneChoiceMenu(SceneChoiceMenuEvent event, Game game, List<SceneChoice> validChoices) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		startChoiceMenu(game, menuChoices, true);
	}

	public void attributeMenu(AttributeMenuEvent event, Game game, Actor actor, int points) {
		this.numericMenuEvent = event;
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String attribute : game.data().getAttributeIDs()) {
			int actorBase = actor.getAttributeBase(attribute);
			menuFields.add(new NumericMenuField(attribute, game.data().getAttribute(attribute).name(), actorBase, Actor.ATTRIBUTE_MAX, actorBase));
		}
		startNumericMenu(game, menuFields, points);
	}

	public void skillMenu(SkillMenuEvent event, Game game, Actor actor, int points) {
		this.numericMenuEvent = event;
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String skill : game.data().getSkillIDs()) {
			int actorBase = actor.getSkillBase(skill);
			menuFields.add(new NumericMenuField(skill, game.data().getSkill(skill).name(), actorBase, Actor.SKILL_MAX, actorBase));
		}
		startNumericMenu(game, menuFields, points);
	}

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		game.eventBus().post(new RenderChoiceMenuEvent(menuChoices, forcePrompts));
	}

	private void startNumericMenu(Game game, List<NumericMenuField> menuFields, int points) {
		game.eventBus().post(new RenderNumericMenuEvent(menuFields, points));
	}
	
	@Subscribe
	public void onMenuSelectEvent(ChoiceMenuInputEvent e) {
		if (choiceMenuEvent != null) {
			choiceMenuEvent.onChoiceMenuInput(e.getIndex());
			choiceMenuEvent = null;
		}
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuInputEvent e) {
		if (numericMenuEvent != null) {
			numericMenuEvent.onNumericMenuInput(e.getValues());
			numericMenuEvent = null;
		}
	}
	
}
