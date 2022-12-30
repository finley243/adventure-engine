package com.github.finley243.adventureengine.menu;

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
		for(Action action : actions) {
			menuChoices.add(action.getMenuChoices(subject));
		}
		int actionIndex = getMenuInput(subject.game(), menuChoices, false);
		return actions.get(actionIndex);
	}

	public void sceneMenu(Actor subject, Actor target, Scene scene) {
		sceneMenu(subject, target, scene, null);
	}

	private void sceneMenu(Actor subject, Actor target, Scene scene, String lastSceneID) {
		scene.setVisited();
		List<SceneLine> lines = selectValidLines(subject, target, scene, lastSceneID);
		boolean showChoices = true;
		String redirect = null;
		for (SceneLine line : lines) {
			if (scene.getType() != SceneType.SEQUENTIAL || line.shouldShow(subject, target, lastSceneID)) {
				executeLine(subject, target, line);
				if (line.shouldExit()) {
					showChoices = false;
					break;
				} else if (line.hasRedirect()) {
					redirect = line.getRedirectID();
					break;
				}
			}
		}
		if (redirect != null) {
			sceneMenu(subject, target, subject.game().data().getScene(redirect), scene.getID());
		} else if (showChoices) {
			SceneChoice selectedChoice = executeChoice(subject, target, scene.getChoices());
			if (selectedChoice != null) {
				sceneMenu(subject, target, subject.game().data().getScene(selectedChoice.getLinkedId()), scene.getID());
			}
		}
	}

	private List<SceneLine> selectValidLines(Actor subject, Actor target, Scene scene, String lastSceneID) {
		List<SceneLine> lines = new ArrayList<>();
		if (scene.getType() == SceneType.RANDOM) {
			List<SceneLine> validLines = new ArrayList<>();
			for (SceneLine line : scene.getLines()) {
				if (line.shouldShow(subject, target, lastSceneID)) {
					validLines.add(line);
				}
			}
			lines.add(MathUtils.selectRandomFromList(validLines));
		} else if (scene.getType() == SceneType.SELECTOR) {
			for (SceneLine line : scene.getLines()) {
				if (line.shouldShow(subject, target, lastSceneID)) {
					lines.add(line);
					break;
				}
			}
		} else {
			lines.addAll(scene.getLines());
		}
		return lines;
	}

	private void executeLine(Actor subject, Actor target, SceneLine line) {
		for (String text : line.getTextList()) {
			subject.game().eventBus().post(new RenderTextEvent(text));
		}
		line.trigger(subject, target);
	}

	private SceneChoice executeChoice(Actor subject, Actor target, List<SceneChoice> choices) {
		List<SceneChoice> validChoices = new ArrayList<>();
		for (SceneChoice choice : choices) {
			if (subject.game().data().getScene(choice.getLinkedId()).canChoose(subject, target)) {
				validChoices.add(choice);
			}
		}
		if (validChoices.isEmpty()) {
			return null;
		}
		subject.game().eventBus().post(new RenderTextEvent(""));
		SceneChoice selectedChoice = sceneMenuInput(subject.game(), validChoices);
		subject.game().eventBus().post(new RenderTextEvent(selectedChoice.getPrompt()));
		subject.game().eventBus().post(new RenderTextEvent(""));
		return selectedChoice;
	}
	
	private SceneChoice sceneMenuInput(Game game, List<SceneChoice> choices) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		for(SceneChoice choice : choices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), true, new String[]{}));
		}
		int selectionIndex = getMenuInput(game, menuChoices, true);
		return choices.get(selectionIndex);
	}

	private synchronized int getMenuInput(Game game, List<MenuChoice> menuChoices, boolean forcePrompts) {
		this.index = -1;
		game.eventBus().post(new RenderMenuEvent(menuChoices, forcePrompts));
		while(this.index == -1) {
			game.threadControl().pause();
		}
		return this.index;
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		this.index = e.getIndex();
	}
	
}
