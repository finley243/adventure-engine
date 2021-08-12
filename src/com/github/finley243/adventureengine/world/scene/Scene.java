package com.github.finley243.adventureengine.world.scene;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.script.Script;

public class Scene {

	private String ID;
	private Condition condition;
	private List<Script> scripts;
	// These lines are printed when the scene is played
	private List<SceneLine> lines;
	
	private float chance;
	// As soon as it is possible to play it, skip the random selection and play this one
	private boolean playImmediately;
	private boolean isRepeatable;
	private boolean hasPlayed;
	
	public Scene(String ID, Condition condition, List<Script> scripts, List<SceneLine> lines, boolean isRepeatable, boolean playImmediately, float chance) {
		this.ID = ID;
		this.condition = condition;
		this.scripts = scripts;
		this.lines = lines;
		this.isRepeatable = isRepeatable;
		this.hasPlayed = false;
		this.playImmediately = playImmediately;
		this.chance = chance;
	}
	
	public String getID() {
		return ID;
	}
	
	public boolean canPlay() {
		if(!isRepeatable && hasPlayed) {
			return false;
		} else {
			if(ThreadLocalRandom.current().nextFloat() >= chance) {
				return false;
			}
			if(condition == null) {
				return true;
			}
			return condition.isMet(Data.getActor(Game.PLAYER_ACTOR));
		}
	}
	
	public boolean playImmediately() {
		return playImmediately;
	}
	
	public void play() {
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
	
	public boolean hasPlayed() {
		return hasPlayed;
	}

}
