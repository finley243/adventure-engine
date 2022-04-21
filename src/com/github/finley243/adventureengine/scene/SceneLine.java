package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;

public class SceneLine {

	private final Condition condition;
	private final List<String> text;
	private final Script script;
	
	public SceneLine(Condition condition, List<String> text, Script script) {
		this.condition = condition;
		this.text = text;
		this.script = script;
	}
	
	public boolean shouldShow(Game game) {
		if(condition == null) return true;
		return condition.isMet(game.data().getPlayer());
	}
	
	public List<String> getText() {
		return text;
	}
	
	public void executeScript(Actor subject) {
		if(script != null) {
			script.execute(subject);
		}
	}

}
