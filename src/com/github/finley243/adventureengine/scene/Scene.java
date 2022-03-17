package com.github.finley243.adventureengine.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.load.SaveData;

public class Scene {

	private final String ID;

	private final Condition condition;
	// These lines are printed when the scene is played
	private final List<SceneLine> lines;
	
	private final float chance;
	// As soon as it is possible to play it, skip the random selection and play this one
	// TODO - Replace with priority system
	private final boolean playImmediately;
	private final boolean isRepeatable;
	private final int cooldown;
	private int cooldownCounter;
	private boolean hasPlayed;
	
	public Scene(String ID, Condition condition, List<SceneLine> lines, boolean isRepeatable, boolean playImmediately, float chance, int cooldown) {
		this.ID = ID;
		this.condition = condition;
		this.lines = lines;
		this.isRepeatable = isRepeatable;
		this.hasPlayed = false;
		this.playImmediately = playImmediately;
		this.chance = chance;
		this.cooldown = cooldown;
		this.cooldownCounter = 0;
	}

	public String getID() {
		return ID;
	}
	
	public boolean canPlay(Game game) {
		if(!isRepeatable && hasPlayed) {
			return false;
		} else if(cooldownCounter > 0) {
			return false;
		} else {
			if(ThreadLocalRandom.current().nextFloat() >= chance) {
				return false;
			}
			return condition == null || condition.isMet(game.data().getPlayer());
		}
	}
	
	public boolean playImmediately() {
		return playImmediately;
	}
	
	public void updateCooldown(Game game) {
		if(condition != null && !condition.isMet(game.data().getPlayer())) {
			return;
		}
		if(cooldownCounter > 0) {
			cooldownCounter--;
		}
	}
	
	public void play(Game game) {
		for(SceneLine line : lines) {
			if(line.shouldShow(game)) {
				for(String text : line.getText()) {
					game.eventBus().post(new RenderTextEvent(text));
				}
				line.executeScript(null);
			}
		}
		hasPlayed = true;
		cooldownCounter = cooldown;
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "cooldownCounter":
				this.cooldownCounter = saveData.getValueInt();
				break;
			case "hasPlayed":
				this.hasPlayed = saveData.getValueBoolean();
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(cooldownCounter != 0) {
			state.add(new SaveData(SaveData.DataType.SCENE, this.getID(), "cooldownCounter", cooldownCounter));
		}
		if(hasPlayed) {
			state.add(new SaveData(SaveData.DataType.SCENE, this.getID(), "hasPlayed", hasPlayed));
		}
		return state;
	}

}
