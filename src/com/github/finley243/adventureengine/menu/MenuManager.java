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
		List<MenuData> menuData = new ArrayList<>();
		for(Action action : actions) {
			menuData.add(action.getMenuData(subject));
		}
		int actionIndex = getMenuInput(subject.game(), menuData);
		return actions.get(actionIndex);
	}

	public void sceneMenu(Actor subject, Scene scene) {
		sceneMenu(subject, scene, null);
	}

	private void sceneMenu(Actor subject, Scene scene, String lastSceneID) {
		scene.setVisited();
		List<SceneLine> lines = selectValidLines(subject, scene, lastSceneID);
		boolean showChoices = true;
		String redirect = null;
		for (SceneLine line : lines) {
			if (line.shouldShow(subject, lastSceneID)) {
				executeLine(subject, line);
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
			sceneMenu(subject, subject.game().data().getScene(redirect), scene.getID());
		} else if (showChoices) {
			SceneChoice selectedChoice = executeChoice(subject, scene.getChoices());
			if (selectedChoice != null) {
				sceneMenu(subject, subject.game().data().getScene(selectedChoice.getLinkedId()), scene.getID());
			}
		}
	}

	private List<SceneLine> selectValidLines(Actor subject, Scene scene, String lastSceneID) {
		List<SceneLine> lines = new ArrayList<>();
		if (scene.getType() == SceneType.RANDOM) {
			List<SceneLine> validLines = new ArrayList<>();
			for (SceneLine line : scene.getLines()) {
				if (line.shouldShow(subject, lastSceneID)) {
					validLines.add(line);
				}
			}
			lines.add(MathUtils.selectRandomFromList(validLines));
		} else if (scene.getType() == SceneType.SELECTOR) {
			for (SceneLine line : scene.getLines()) {
				if (line.shouldShow(subject, lastSceneID)) {
					lines.add(line);
					break;
				}
			}
		} else {
			lines.addAll(scene.getLines());
		}
		return lines;
	}

	private void executeLine(Actor subject, SceneLine line) {
		for (String text : line.getTextList()) {
			subject.game().eventBus().post(new RenderTextEvent(text));
		}
		line.trigger(subject);
	}

	private SceneChoice executeChoice(Actor subject, List<SceneChoice> choices) {
		List<SceneChoice> validChoices = new ArrayList<>();
		for (SceneChoice choice : choices) {
			if (subject.game().data().getScene(choice.getLinkedId()).canChoose(subject)) {
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
		List<MenuData> menuData = new ArrayList<>();
		for(SceneChoice choice : choices) {
			menuData.add(new MenuData(choice.getPrompt(), true));
		}
		int selectionIndex = getMenuInput(game, menuData);
		return choices.get(selectionIndex);
	}

	private synchronized int getMenuInput(Game game, List<MenuData> menuData) {
		this.index = -1;
		game.eventBus().post(new RenderMenuEvent(menuData));
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
