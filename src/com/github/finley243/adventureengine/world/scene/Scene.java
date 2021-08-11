package com.github.finley243.adventureengine.world.scene;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

public class Scene {

	private Condition condition;
	private List<Script> scripts;
	// These lines are printed when the scene is played
	private List<SceneLine> lines;
	
	private boolean isRepeatable;
	private boolean hasPlayed;
	
	public Scene() {
		
	}
	
	public boolean canPlay() {
		if(!isRepeatable && hasPlayed) {
			return false;
		} else {
			return condition.isMet(Data.getActor(Game.PLAYER_ACTOR));
		}
	}
	
	public void play(Area area) {
		for(SceneLine line : lines) {
			if(line.shouldShow()) {
				for(String text : line.getText()) {
					Game.EVENT_BUS.post(new RenderTextEvent(text));
				}
			}
		}
		for(Script script : scripts) {
			script.execute(null);
		}
		hasPlayed = true;
	}

}
