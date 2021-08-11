package com.github.finley243.adventureengine.world.scene;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class SceneLine {

	private Condition condition;
	private List<String> text;
	
	public SceneLine(Condition condition, List<String> text) {
		this.condition = condition;
		this.text = text;
	}
	
	public boolean shouldShow() {
		if(condition == null) return true;
		return condition.isMet(Data.getActor(Game.PLAYER_ACTOR));
	}
	
	public List<String> getText() {
		return text;
	}

}
