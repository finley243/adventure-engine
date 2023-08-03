package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ProvideActionsEvent;
import com.github.finley243.adventureengine.event.SelectActionEvent;
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

	private NumericMenuConfirmEvent numericMenuReturn;
	private boolean isSceneMenu;
	private ProvideActionsEvent actionMenuEvent;
	private List<SceneChoice> sceneChoices;
	private Scene currentScene;
	private Context currentSceneContext;
	
	public MenuManager() {
		this.numericMenuReturn = null;
	}

	/*public void pauseMenu(Game game) {
		waitForContinue(game);
	}*/
	
	public void actionMenu(ProvideActionsEvent e) {
		isSceneMenu = false;
		actionMenuEvent = e;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (Action action : e.actions()) {
			menuChoices.add(action.getMenuChoices(e.subject()));
		}
		startChoiceMenu(e.subject().game(), menuChoices, false);
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
		isSceneMenu = true;
		context.game().eventBus().post(new TextClearEvent());
		sceneMenu(context, scene, null);
	}

	private void sceneMenu(Context context, Scene scene, String lastSceneID) {
		currentScene = scene;
		currentSceneContext = context;
		scene.setTriggered();
		List<SceneLine> lines = selectValidLines(context, scene.getType(), scene.getLines(), lastSceneID);
		boolean showChoices = true;
		String redirect = null;
		for (SceneLine line : lines) {
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
			startSceneChoiceMenu(context, scene.getChoices());
		}
	}

	public void onSelectSceneChoice(SceneChoice selectedChoice, Context context, Scene scene) {
		sceneMenu(context, context.game().data().getScene(selectedChoice.getLinkedId()), scene.getID());
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

	private void startSceneChoiceMenu(Context context, List<SceneChoice> choices) {
		List<SceneChoice> validChoices = new ArrayList<>();
		for (SceneChoice choice : choices) {
			if (context.game().data().getScene(choice.getLinkedId()).canChoose(context)) {
				validChoices.add(choice);
			}
		}
		if (validChoices.isEmpty()) {
			return;
		}
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		startChoiceMenu(context.game(), menuChoices, true);
	}

	/*private void waitForContinue(Game game) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		menuChoices.add(new MenuChoice("Continue", true, new String[] {"continue"}));
		getMenuInput(game, menuChoices, true);
	}*/

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
	}

	private void onActionMenuInput(int choiceIndex) {
		Action selectedAction = actionMenuEvent.actions().get(choiceIndex);
		actionMenuEvent.subject().onSelectAction(new SelectActionEvent(selectedAction, actionMenuEvent.lastAction(), actionMenuEvent.actionRepeatCount()));
	}

	private void onSceneMenuInput(int choiceIndex) {
		currentSceneContext.game().eventBus().post(new TextClearEvent());
		SceneChoice selectedChoice = sceneChoices.get(choiceIndex);
		onSelectSceneChoice(selectedChoice, currentSceneContext, currentScene);
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
		if (isSceneMenu) {
			onSceneMenuInput(e.getIndex());
		} else {
			onActionMenuInput(e.getIndex());
		}
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
