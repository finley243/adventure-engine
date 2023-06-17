package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.Scene.SceneType;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

	private int index;
	
	public MenuManager() {
		this.index = -1;
	}
	
	public Action actionMenu(List<Action> actions, Actor subject) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (Action action : actions) {
			menuChoices.add(action.getMenuChoices(subject));
		}
		int actionIndex = getMenuInput(subject.game(), menuChoices, false);
		return actions.get(actionIndex);
	}

	public void sceneMenu(Context context, Scene scene) {
		sceneMenu(context, scene, null);
	}

	private void sceneMenu(Context context, Scene scene, String lastSceneID) {
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
		context.game().eventBus().post(new RenderTextEvent(""));
		SceneChoice selectedChoice = sceneMenuInput(context.game(), validChoices);
		context.game().eventBus().post(new RenderTextEvent(selectedChoice.getPrompt()));
		context.game().eventBus().post(new RenderTextEvent(""));
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

	private synchronized int getMenuInput(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		this.index = -1;
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
		while (this.index == -1) {
			game.threadControl().pause();
		}
		return this.index;
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		this.index = e.getIndex();
	}

	private record SceneLineResult(boolean exit, String redirectID) {
		public boolean shouldEndLine() {
			return exit || redirectID != null;
		}
	}
	
}
