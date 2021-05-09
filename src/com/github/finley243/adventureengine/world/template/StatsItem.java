package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Noun;

public abstract class StatsItem implements Noun {

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
	public String getFormattedName(boolean indefinite) {
		if(isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
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
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof StatsItem)) {
			return false;
		} else {
			return o == this;
		}
	}
	
}
