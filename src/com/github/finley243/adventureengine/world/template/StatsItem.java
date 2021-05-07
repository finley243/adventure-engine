package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;

public class StatsItem implements Noun {

	private String ID;
	private String name;
	private int price;
	
	public StatsItem(String ID, String name, int price) {
		this.ID = ID;
		this.name = name;
		this.price = price;
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getFormattedName() {
		return (isProperName() ? "" : "a ") + getName();
	}
	
	@Override
	public boolean isProperName() {
		return false;
	}
	
	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
	public int getPrice() {
		return price;
	}
	
}
