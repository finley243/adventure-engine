package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.Effectable;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.component.EffectableItemComponent;
import com.github.finley243.adventureengine.item.component.ItemComponent;
import com.github.finley243.adventureengine.item.component.ItemComponentFactory;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item extends GameInstanced implements Noun, ScriptValueHolder, StatHolder, Effectable {

	private Inventory currentInventory;
	private boolean isKnown;
	private final ItemTemplate template;
	private final Map<Class<? extends ItemComponent>, ItemComponent> components;

	Item(String ID, ItemTemplate template, ItemComponentFactory itemComponentFactory) {
		super(ID);
		this.template = template;
		this.currentInventory = null;
		this.components = new HashMap<>();
		for (ItemComponentTemplate componentTemplate : getTemplate().getComponents()) {
			ItemComponent component = itemComponentFactory.create(componentTemplate, this);
			if (component == null) throw new UnsupportedOperationException("Cannot add null component to item " + this);
			if (components.containsKey(component.getClass())) {
				throw new UnsupportedOperationException("Item " + this + " already contains a component of type " + component.getClass());
			}
			components.put(component.getClass(), component);
			component.onInit();
		}
	}

	@Override
	public String getName() {
		return getTemplate().getName();
	}

	@Override
	public void setKnown() {
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		return hasState() && isKnown;
	}

	@Override
	public boolean isProperName() {
		return getTemplate().isProperName();
	}

	@Override
	public int pluralCount() {
		return 1;
	}

	@Override
	public TextContext.Pronoun getPronoun() {
		return getTemplate().getPronoun();
	}

	public Scene getDescription() {
		return getTemplate().getDescription();
	}

	public void onStartRound() {
		for (ItemComponent component : components.values()) {
			component.onStartRound();
		}
	}

	public <T extends ItemComponent> T getComponentOfType(Class<T> componentClass) {
		ItemComponent component = components.get(componentClass);
		return componentClass.cast(component);
	}

	public <T extends ItemComponent> boolean hasComponentOfType(Class<T> componentClass) {
		return components.containsKey(componentClass);
	}

	public void triggerScript(String entryPoint, ScriptRuntime scriptRuntime, Actor subject, Actor target) {
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			for (Script currentScript : getTemplate().getScripts().get(entryPoint)) {
				Context context = Context.builder().subject(subject).target(target).parentItem(this).build();
				currentScript.run(scriptRuntime, context);
			}
		}
	}

	public ItemTemplate getTemplate() {
		return template;
	}

	public String getTemplateID() {
		return getTemplate().getID();
	}

	public boolean hasState() {
        for (ItemComponent component : components.values()) {
			if (component.hasState()) {
				return true;
			}
		}
		return false;
    }
	
	public List<Action> inventoryActions(Actor subject, ActionDependencies dependencies) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(subject, dependencies, this));
		if (subject.getInventory().itemCount(this) > 1) {
			actions.add(new ActionItemDropAll(subject, dependencies, this));
		}
		if (this.getDescription() != null) {
			actions.add(new ActionInspectItem(subject, dependencies, this));
		}
		for (ItemComponent component : components.values()) {
			actions.addAll(component.getInventoryActions(dependencies, subject));
		}
		for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
			ActionTemplate customActionTemplate = customAction.action();
			actions.add(new ActionCustom(subject, dependencies, null, null, this, null, customActionTemplate, customAction.parameters(), new MenuDataInventory(this, subject.getInventory()), false));
		}
		return actions;
	}

	@Override
	public void addEffect(Effect effect) {
		if (hasComponentOfType(EffectableItemComponent.class)) {
			getComponentOfType(EffectableItemComponent.class).addEffect(effect);
		}
	}

	@Override
	public void removeEffect(Effect effect) {
		if (hasComponentOfType(EffectableItemComponent.class)) {
			getComponentOfType(EffectableItemComponent.class).removeEffect(effect);
		}
	}

	@Override
	public Expression getScriptValue(String name, Context context) {
		for (ItemComponent component : components.values()) {
			Expression componentValue = component.getScriptValue(name, context);
			if (componentValue != null) return componentValue;
		}
		return switch (name) {
			case "inventory" -> Expression.inventory(currentInventory);
			case "noun" -> Expression.noun(this);
			case "id" -> Expression.string(getID());
			case "template" -> Expression.valueHolder(getTemplate());
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		for (ItemComponent component : components.values()) {
			boolean success = component.setScriptValue(name, value, context);
			if (success) return true;
		}
		return false;
	}

	public void setInventory(Inventory inventory) {
		this.currentInventory = inventory;
	}

	public Inventory getInventory() {
		return currentInventory;
	}

	@Override
	public Stat getStat(String name) {
		for (ItemComponent component : components.values()) {
			Stat componentValue = component.getStat(name);
			if (componentValue != null) return componentValue;
		}
		return null;
	}

	@Override
	public void onStatChange(String name) {
		for (ItemComponent component : components.values()) {
			component.onStatChange(name);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Item otherItem && getTemplate().equals(otherItem.getTemplate()) && getID().equals(otherItem.getID());
	}

	@Override
	public int hashCode() {
		return (31 * getTemplate().hashCode()) + getID().hashCode();
	}
	
}
