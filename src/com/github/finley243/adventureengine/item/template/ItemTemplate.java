package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ItemTemplate extends GameInstanced implements Noun, StatHolder {

	private int generatedCount;

	private final String name;
	private final Scene description;
	private final int price;
	private final Map<String, Script> scripts;
	private final List<ActionCustom.CustomActionHolder> customActions;

	public ItemTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ActionCustom.CustomActionHolder> customActions, int price) {
		super(game, ID);
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.customActions = customActions;
		this.price = price;
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
		if (!isProperName()) {
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

	public abstract Set<String> getTags();

	public Map<String, Script> getScripts() {
		return scripts;
	}

	public List<ActionCustom.CustomActionHolder> getCustomActions() {
		return customActions;
	}

	@Override
	public int getValueInt(String name) {
		if ("price".equals(name)) {
			return price;
		}
		return 0;
	}

	@Override
	public float getValueFloat(String name) {
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		return false;
	}

	@Override
	public String getValueString(String name) {
		return switch (name) {
			case "id" -> getID();
			case "name" -> getName();
			default -> null;
		};
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		if ("tags".equals(name)) {
			return getTags();
		}
		return null;
	}

	@Override
	public void setStateBoolean(String name, boolean value) {

	}

	@Override
	public void setStateInteger(String name, int value) {

	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {

	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {

	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return null;
	}

	public void loadState(SaveData saveData) {
		if (saveData.getParameter().equals("generated_count")) {
			this.generatedCount = saveData.getValueInt();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (generatedCount > 0) {
			state.add(new SaveData(SaveData.DataType.ITEM_TEMPLATE, this.getID(), "generated_count", generatedCount));
		}
		return state;
	}
	
}
