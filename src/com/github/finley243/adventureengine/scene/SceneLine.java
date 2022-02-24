package com.github.finley243.adventureengine.scene;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

public class SceneLine {

	private final Condition condition;
	private final List<String> text;
	private final Script script;
	
	public SceneLine(Condition condition, List<String> text, Script script) {
		this.condition = condition;
		this.text = text;
		this.script = script;
	}
	
	public boolean shouldShow() {
		if(condition == null) return true;
		return condition.isMet(Data.getPlayer());
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
