package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public class StatsActor {
	
	private String name;
	private boolean isProperName;
	private Pronoun pronoun;
	
	private int maxHP;
	
	public StatsActor(String name, boolean isProperName, Pronoun pronoun) {
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isProperName() {
		return isProperName;
	}
	
	public Pronoun getPronoun() {
		return pronoun;
	}
	
	public int getMaxHP() {
		return maxHP;
	}
	
}
