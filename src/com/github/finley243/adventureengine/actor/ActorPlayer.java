package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.TextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	public ActorPlayer(String ID, String areaID, StatsActor stats, Controller controller) {
		super(ID, areaID, stats, null, false, controller);
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		//Game.EVENT_BUS.post(new TextEvent(event.getText()));
		System.out.println(event.getText());
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}

}
