package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.Menu;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	public ActorPlayer(String ID, String areaID, StatsActor stats) {
		super(ID, areaID, stats, null, false);
	}
	
	@Override
	public void move(Area area) {
		super.move(area);
		if(!this.getArea().getRoom().hasVisited()) {
			Game.EVENT_BUS.post(new TextEvent(this.getArea().getRoom().getDescription()));
			this.getArea().getRoom().setVisited();
		}
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		//Game.EVENT_BUS.post(new TextEvent(event.getText()));
		System.out.println(event.getText());
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public List<Action> localActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	@Override
	public void takeTurn() {
		// Could handle action points here?
		Action chosenAction = Menu.buildMenu(this.availableActions());
		chosenAction.choose(this);
	}

}
