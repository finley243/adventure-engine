package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
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
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void takeTurn() {
		// Could handle action points here?
		Action chosenAction = menuHandler.actionMenu(this.availableActions());
		chosenAction.choose(this);
	}
	
	public void startDialogue(Actor target) {
		menuHandler.dialogueMenu(target);
	}

	public void updateRoomDescription() {
		if(!this.getArea().getRoom().hasVisited()) {
			Game.EVENT_BUS.post(new RenderTextEvent(this.getArea().getRoom().getDescription()));
			this.getArea().getRoom().setVisited();
		}
	}

}
