package com.github.finley243.adventureengine.world.item.template;

import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ItemTemplate implements Noun {

	private int generatedCount;

	private final String ID;
	private final String name;
	private final String description;
	private final int price;
	private final Map<String, Script> scripts;
	
	public ItemTemplate(String ID, String name, String description, Map<String, Script> scripts, int price) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.price = price;
		this.generatedCount = 1;
	}

	public abstract boolean hasState();

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
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), true);
		} else {
			return getName();
		}
	}

	@Override
	public void setKnown() {}

	@Override
	public boolean isKnown() {
		return false;
	}
	
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

	public abstract Set<String> getTags();

	public Map<String, Script> getScripts() {
		return scripts;
	}

	public void loadState(SaveData saveData) {
		if(saveData.getParameter().equals("generatedCount")) {
			this.generatedCount = saveData.getValueInt();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(generatedCount > 0) {
			state.add(new SaveData(SaveData.DataType.ITEM_STATS, this.getID(), "generatedCount", generatedCount));
		}
		return state;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemTemplate)) {
			return false;
		} else {
			return o == this;
		}
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
}
