package com.github.finley243.adventureengine.scene;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.RenderTextEvent;

public class Scene {

	private final Condition condition;
	// These lines are printed when the scene is played
	private final List<SceneLine> lines;
	
	private final float chance;
	// As soon as it is possible to play it, skip the random selection and play this one
	private final boolean playImmediately;
	private final boolean isRepeatable;
	private boolean hasPlayed;
	private final int cooldown;
	private int cooldownCounter;
	
	public Scene(Condition condition, List<SceneLine> lines, boolean isRepeatable, boolean playImmediately, float chance, int cooldown) {
		this.condition = condition;
		this.lines = lines;
		this.isRepeatable = isRepeatable;
		this.hasPlayed = false;
		this.playImmediately = playImmediately;
		this.chance = chance;
		this.cooldown = cooldown;
		this.cooldownCounter = 0;
	}
	
	public boolean canPlay() {
		if(!isRepeatable && hasPlayed) {
			return false;
		} else if(cooldownCounter > 0) {
			return false;
		} else {
			if(ThreadLocalRandom.current().nextFloat() >= chance) {
				return false;
			}
			if(condition == null) {
				return true;
			}
			return condition.isMet(Data.getPlayer());
		}
	}
	
	public boolean playImmediately() {
		return playImmediately;
	}
	
	public void updateCooldown() {
		if(!condition.isMet(Data.getPlayer())) {
			return;
		}
		if(cooldownCounter > 0) {
			cooldownCounter--;
		}
	}
	
	public void play() {
		for(SceneLine line : lines) {
			if(line.shouldShow()) {
				for(String text : line.getText()) {
					Game.EVENT_BUS.post(new RenderTextEvent(text));
				}
				line.executeScripts(null);
			}
		}
		hasPlayed = true;
		cooldownCounter = cooldown;
	}

}
