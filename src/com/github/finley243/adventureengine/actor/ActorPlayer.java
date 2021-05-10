package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	private MenuManager menuHandler;
	
	public ActorPlayer(String ID, String areaID, StatsActor stats) {
		super(ID, areaID, stats, null, false);
		this.menuHandler = new MenuManager();
		Game.EVENT_BUS.register(this);
		Game.EVENT_BUS.register(menuHandler);
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		Game.EVENT_BUS.post(new RenderTextEvent(event.getText()));
		//sleep(200);
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void kill() {
		super.kill();
		Game.EVENT_BUS.post(new PlayerDeathEvent());
	}
	
	@Override
	public Action chooseAction() {
		return menuHandler.actionMenu(this);
	}
	
	public void startDialogue(Actor target) {
		menuHandler.dialogueMenu(this, target);
	}

	public void updateRoomDescription() {
		if(!this.getArea().getRoom().hasVisited()) {
			Game.EVENT_BUS.post(new RenderTextEvent(this.getArea().getRoom().getDescription()));
			Game.EVENT_BUS.post(new RenderTextEvent(""));
			this.getArea().getRoom().setVisited();
		}
	}
	
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
