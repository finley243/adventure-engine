package com.github.finley243.adventureengine.actor;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	private final MenuManager menuManager;
	private final SceneManager sceneManager;
	
	public ActorPlayer(String ID, Area area, StatsActor stats) {
		super(ID, area, stats, null, false);
		this.menuManager = new MenuManager();
		Game.EVENT_BUS.register(menuManager);
		sceneManager = new SceneManager();
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		Game.EVENT_BUS.post(new RenderTextEvent(event.getText()));
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void move(Area area) {
		super.move(area);
		this.updateAreaDescription();
	}
	
	@Override
	public void kill() {
		super.kill();
		Game.EVENT_BUS.post(new PlayerDeathEvent());
	}
	
	@Override
	public Action chooseAction(List<Action> actions) {
		return menuManager.actionMenu(actions);
	}
	
	public void startDialogue(Actor subject, String startTopic) {
		menuManager.dialogueMenu(subject, startTopic);
	}

	public void updateAreaDescription() {
		if(this.getArea().getDescription() != null) {
			Game.EVENT_BUS.post(new RenderTextEvent(this.getArea().getDescription()));
			Game.EVENT_BUS.post(new RenderTextEvent(""));
		}
	}
	
	public void triggerSceneManager() {
		sceneManager.trigger();
	}

}
