package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.DisplayMenuEvent;
import com.github.finley243.adventureengine.event.DisplayTextEvent;
import com.github.finley243.adventureengine.event.EndPlayerTurnEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.template.StatsActor;
import com.google.common.eventbus.Subscribe;

public class ActorPlayer extends Actor {

	private List<Action> lastActions;
	private MenuSelectEvent lastEvent;
	
	public ActorPlayer(String ID, String areaID, StatsActor stats) {
		super(ID, areaID, stats, null, false);
		Game.EVENT_BUS.register(this);
	}
	
	@Override
	public void move(Area area) {
		super.move(area);
		/*if(!this.getArea().getRoom().hasVisited()) {
			Game.EVENT_BUS.post(new DisplayTextEvent(this.getArea().getRoom().getDescription()));
			this.getArea().getRoom().setVisited();
		}*/
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		Game.EVENT_BUS.post(new DisplayTextEvent(event.getText()));
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void takeTurn() {
		// Could handle action points here?
		//Action chosenAction = Menu.buildMenu(this.availableActions());
		if(!this.getArea().getRoom().hasVisited()) {
			Game.EVENT_BUS.post(new DisplayTextEvent(this.getArea().getRoom().getDescription()));
			this.getArea().getRoom().setVisited();
		}
		lastActions = this.availableActions();
		List<String> choiceStrings = new ArrayList<String>();
		for(int i = 0; i < lastActions.size(); i++) {
			//System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
			choiceStrings.add(lastActions.get(i).getChoiceName());
		}
		Game.EVENT_BUS.post(new DisplayMenuEvent(choiceStrings));
		//chosenAction.choose(this);
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent event) {
		lastActions.get(event.getIndex()).choose(this);
		Game.EVENT_BUS.post(new EndPlayerTurnEvent());
	}

}
