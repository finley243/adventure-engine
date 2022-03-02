package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Map;

public abstract class StatsItem implements Noun {

	private int generatedCount;

	private final String ID;
	private final String name;
	private final String description;
	private final int price;
	private final Map<String, Script> scripts;
	
	public StatsItem(String ID, String name, String description, Map<String, Script> scripts, int price) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.price = price;
		this.generatedCount = 1;
	}

	public String generateInstanceID() {
		String newID = ID + "_" + generatedCount;
		generatedCount += 1;
		return newID;
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
		return getFormattedName(true);
	}
	
	@Override
	public String getFormattedName(boolean indefinite) {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
	}

	@Override
	public void setKnown() {}
	
	@Override
	public boolean isProperName() {
		return false;
	}
	
	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public boolean forcePronoun() {
		return false;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getPrice() {
		return price;
	}

	public Map<String, Script> getScripts() {
		return scripts;
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
