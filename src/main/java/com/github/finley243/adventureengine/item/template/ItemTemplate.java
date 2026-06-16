package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTemplate extends GameInstanced implements Noun, ScriptValueHolder {

	private int generatedCount;

	private final String name;
	private final Scene description;
	private final int price;
	private final Map<String, List<Script>> scripts;
	private final Map<Class<? extends ItemComponentTemplate>, ItemComponentTemplate> components;
	private final List<ActionCustom.CustomActionHolder> customActions;

	public ItemTemplate(String ID, String name, Scene description, Map<String, List<Script>> scripts, List<ItemComponentTemplate> components, List<ActionCustom.CustomActionHolder> customActions, int price) {
		super(ID);
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.components = new HashMap<>();
		for (ItemComponentTemplate componentTemplate : components) {
			this.components.put(componentTemplate.getClass(), componentTemplate);
		}
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

	public <T extends ItemComponentTemplate> T getComponentTemplate(Class<T> componentClass) {
		ItemComponentTemplate component = components.get(componentClass);
		return componentClass.cast(component);
	}

	public Collection<ItemComponentTemplate> getComponents() {
		return components.values();
	}

	public Map<String, List<Script>> getScripts() {
		return scripts;
	}

	public List<ActionCustom.CustomActionHolder> getCustomActions() {
		return customActions;
	}

	@Override
	public Expression getScriptValue(String name, Context context) {
		return switch (name) {
			case "noun" -> Expression.noun(this);
			case "price" -> Expression.integer(price);
			case "id" -> Expression.string(getID());
			case "name" -> Expression.string(getName());
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		return false;
	}

	@Override
	public ScriptValueHolder getSubHolder(String name, String ID) {
		return null;
	}
	
}
