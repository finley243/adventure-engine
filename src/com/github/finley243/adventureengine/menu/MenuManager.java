package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.Scene.SceneType;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManager {

	private int index;
	private NumericMenuConfirmEvent numericMenuReturn;
	
	public MenuManager() {
		this.index = -1;
		this.numericMenuReturn = null;
	}

	public void pauseMenu(Game game) {
		waitForContinue(game);
	}
	
	public Action actionMenu(List<Action> actions, Actor subject) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (Action action : actions) {
			menuChoices.add(action.getMenuChoices(subject));
		}
		int actionIndex = getMenuInput(subject.game(), menuChoices, false);
		return actions.get(actionIndex);
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

	public void sceneMenu(Context context, Scene scene) {
		context.game().eventBus().post(new TextClearEvent());
		sceneMenu(context, scene, null);
	}

	private void sceneMenu(Context context, Scene scene, String lastSceneID) {
		scene.setTriggered();
		List<SceneLine> lines = selectValidLines(context, scene.getType(), scene.getLines(), lastSceneID);
		boolean showChoices = true;
		String redirect = null;
		for (SceneLine line : lines) {
			//waitForContinue(context.game());
			if (scene.getType() != SceneType.SEQUENTIAL || line.shouldShow(context, lastSceneID)) {
				SceneLineResult lineResult = executeLine(context, line, lastSceneID);
				if (lineResult.exit) {
					showChoices = false;
					break;
				} else if (lineResult.redirectID != null) {
					redirect = lineResult.redirectID;
					break;
				}
			}
		}
		if (redirect != null) {
			sceneMenu(context, context.game().data().getScene(redirect), scene.getID());
		} else if (showChoices) {
			SceneChoice selectedChoice = executeChoice(context, scene.getChoices());
			if (selectedChoice != null) {
				sceneMenu(context, context.game().data().getScene(selectedChoice.getLinkedId()), scene.getID());
			}
		}
	}

	private List<SceneLine> selectValidLines(Context context, SceneType type, List<SceneLine> lines, String lastSceneID) {
		List<SceneLine> validLines = new ArrayList<>();
		if (type == SceneType.RANDOM) {
			List<SceneLine> randomSelectionLines = new ArrayList<>();
			for (SceneLine line : lines) {
				if (line.shouldShow(context, lastSceneID)) {
					randomSelectionLines.add(line);
				}
			}
			validLines.add(MathUtils.selectRandomFromList(randomSelectionLines));
		} else if (type == SceneType.SELECTOR) {
			for (SceneLine line : lines) {
				if (line.shouldShow(context, lastSceneID)) {
					validLines.add(line);
					break;
				}
			}
		} else {
			validLines.addAll(lines);
		}
		return validLines;
	}

	private SceneLineResult executeLine(Context context, SceneLine line, String lastSceneID) {
		if (line.getText() != null) {
			context.game().eventBus().post(new RenderTextEvent(line.getText()));
		}
		line.trigger(context);
		if (line.getSubLines() != null) {
			List<SceneLine> lines = selectValidLines(context, line.getType(), line.getSubLines(), lastSceneID);
			for (SceneLine subLine : lines) {
				if (line.getType() != SceneType.SEQUENTIAL || subLine.shouldShow(context, lastSceneID)) {
					SceneLineResult subLineResult = executeLine(context, subLine, lastSceneID);
					if (subLineResult.shouldEndLine()) {
						return subLineResult;
					}
				}
			}
		}
		return new SceneLineResult(line.shouldExit(), line.getRedirectID());
	}

	private SceneChoice executeChoice(Context context, List<SceneChoice> choices) {
		List<SceneChoice> validChoices = new ArrayList<>();
		for (SceneChoice choice : choices) {
			if (context.game().data().getScene(choice.getLinkedId()).canChoose(context)) {
				validChoices.add(choice);
			}
		}
		if (validChoices.isEmpty()) {
			return null;
		}
		//context.game().eventBus().post(new RenderTextEvent(""));
		SceneChoice selectedChoice = sceneMenuInput(context.game(), validChoices);
		context.game().eventBus().post(new TextClearEvent());
		//context.game().eventBus().post(new RenderTextEvent(selectedChoice.getPrompt()));
		//context.game().eventBus().post(new RenderTextEvent(""));
		return selectedChoice;
	}
	
	private SceneChoice sceneMenuInput(Game game, List<SceneChoice> choices) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : choices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		int selectionIndex = getMenuInput(game, menuChoices, true);
		return choices.get(selectionIndex);
	}

	private void waitForContinue(Game game) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		menuChoices.add(new MenuChoice("Continue", true, new String[] {"continue"}));
		getMenuInput(game, menuChoices, true);
	}

	private synchronized int getMenuInput(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		this.index = -1;
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
		while (this.index == -1) {
			game.threadControl().pause();
		}
		return this.index;
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
		this.index = e.getIndex();
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuConfirmEvent e) {
		this.numericMenuReturn = e;
	}

	private record SceneLineResult(boolean exit, String redirectID) {
		public boolean shouldEndLine() {
			return exit || redirectID != null;
		}
	}
	
}
