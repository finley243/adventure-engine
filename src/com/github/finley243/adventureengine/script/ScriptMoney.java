package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;

public class ScriptMoney implements Script {

	private int value;
	
	public ScriptMoney(int value) {
		this.value = value;
	}

	@Override
	public void execute(Actor target) {
		target.addMoney(value);
	}
	
}
