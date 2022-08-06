package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
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
import java.util.concurrent.ThreadLocalRandom;

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

	// TODO - Consider replacing the while-loop structure with a recursive structure (each time it would loop, instead call the function again)
	public void sceneMenu(Actor subject, Scene startScene) {
		boolean loop = true;
		boolean redirected;
		Scene lastScene = null;
		Scene currentScene = startScene;
		while (loop) {
			redirected = false;
			currentScene.setVisited();
			for (SceneLine line : currentScene.getLines()) {
				if (currentScene.getType() == SceneType.RANDOM) {
					// TODO - Improve efficiency (filter available lines once, then only select from this subset)
					line = currentScene.getLines().get(ThreadLocalRandom.current().nextInt(currentScene.getLines().size()));
				}
				if (line.shouldShow(subject, (lastScene == null ? null : lastScene.getID()))) {
					for (String text : line.getTextList()) {
						subject.game().eventBus().post(new RenderTextEvent(text));
					}
					line.trigger(subject);
					if (line.hasRedirect()) {
						redirected = true;
						lastScene = currentScene;
						currentScene = subject.game().data().getScene(line.getRedirectID());
						break;
					}
					if (line.shouldExit()) {
						loop = false;
						break;
					}
					if (currentScene.getType() == SceneType.SELECTOR) {
						break;
					}
				}
			}
			if (!redirected) {
				if (currentScene.getChoices().isEmpty()) {
					loop = false;
				} else if (loop) {
					List<SceneChoice> validChoices = new ArrayList<>();
					for (SceneChoice choice : currentScene.getChoices()) {
						if (subject.game().data().getScene(choice.getLinkedId()).canChoose(subject)) {
							validChoices.add(choice);
						}
					}
					subject.game().eventBus().post(new RenderTextEvent(""));
					if (validChoices.size() > 0) {
						SceneChoice selectedChoice = sceneMenuInput(subject.game(), validChoices);
						lastScene = currentScene;
						currentScene = subject.game().data().getScene(selectedChoice.getLinkedId());
						subject.game().eventBus().post(new RenderTextEvent(selectedChoice.getPrompt()));
						subject.game().eventBus().post(new RenderTextEvent(""));
					}
				}
			}
		}
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
