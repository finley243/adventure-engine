package com.github.finley243.adventureengine.scene;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

public class SceneLine {

	private Condition condition;
	private List<String> text;
	private List<Script> scripts;
	
	public SceneLine(Condition condition, List<String> text, List<Script> scripts) {
		this.condition = condition;
		this.text = text;
		this.scripts = scripts;
	}
	
	public boolean shouldShow() {
		if(condition == null) return true;
		return condition.isMet(Data.getActor(Game.PLAYER_ACTOR));
	}
	
	public List<String> getText() {
		return text;
	}
	
	public void executeScripts(Actor subject) {
		for(Script script : scripts) {
			script.execute(subject);
		}
	}

}
