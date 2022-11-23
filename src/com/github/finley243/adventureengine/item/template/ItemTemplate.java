package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ItemTemplate extends GameInstanced implements Noun {

	private int generatedCount;

	private final String name;
	private final Scene description;
	private final int price;
	private final String attackType;
	private final Map<String, Script> scripts;

	public ItemTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, int price, String attackType) {
		super(game, ID);
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.price = price;
		this.attackType = attackType;
		this.generatedCount = 1;
	}

	public abstract boolean hasState();

	public String generateInstanceID() {
		String newID = getID() + "_" + generatedCount;
		generatedCount += 1;
		return newID;
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
	
	public Scene getDescription() {
		return description;
	}
	
	public int getPrice() {
		return price;
	}

	public String getAttackType() {
		return attackType;
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
			state.add(new SaveData(SaveData.DataType.ITEM_TEMPLATE, this.getID(), "generatedCount", generatedCount));
		}
		return state;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ItemTemplate)) {
			return false;
		} else {
			return this.getID().equals(((ItemTemplate) o).getID());
		}
	}

	@Override
	public int hashCode() {
		return getID().hashCode();
	}
	
}
