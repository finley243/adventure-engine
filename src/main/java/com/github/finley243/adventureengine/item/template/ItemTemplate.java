package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.*;

public class ItemTemplate extends GameInstanced implements Noun, StatHolder {

	private int generatedCount;

	private final String name;
	private final Scene description;
	private final int price;
	private final Map<String, List<Script>> scripts;
	private final List<ItemComponentTemplate> components;
	private final List<ActionCustom.CustomActionHolder> customActions;

	public ItemTemplate(Game game, String ID, String name, Scene description, Map<String, List<Script>> scripts, List<ItemComponentTemplate> components, List<ActionCustom.CustomActionHolder> customActions, int price) {
		super(game, ID);
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.components = components;
		this.customActions = customActions;
		this.price = price;
		this.generatedCount = 1;
	}

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
	public int pluralCount() {
		return 1;
	}
	
	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
	public Scene getDescription() {
		return description;
	}
	
	public int getPrice() {
		return price;
	}

	public List<ItemComponentTemplate> getComponents() {
		return components;
	}

	public Map<String, List<Script>> getScripts() {
		return scripts;
	}

	public List<ActionCustom.CustomActionHolder> getCustomActions() {
		return customActions;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "noun" -> Expression.constantNoun(this);
			case "price" -> Expression.constant(price);
			case "id" -> Expression.constant(getID());
			case "name" -> Expression.constant(getName());
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		return false;
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
