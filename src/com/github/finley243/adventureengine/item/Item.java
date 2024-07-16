package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.effect.Effectible;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.component.ItemComponent;
import com.github.finley243.adventureengine.item.component.ItemComponentEffectible;
import com.github.finley243.adventureengine.item.component.ItemComponentFactory;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext;

import java.util.*;

public class Item extends GameInstanced implements Noun, MutableStatHolder, Effectible {

	private Inventory currentInventory;
	private boolean isKnown;
	private final String templateID;
	private final Map<Class<? extends ItemComponent>, ItemComponent> components;

	public Item(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
		this.currentInventory = null;
		this.components = new HashMap<>();
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

	public void onInit() {
		for (ItemComponentTemplate componentTemplate : getTemplate().getComponents()) {
			ItemComponent component = ItemComponentFactory.create(componentTemplate, this);
			if (component == null) throw new UnsupportedOperationException("Cannot add null component to item " + this);
			if (components.containsKey(component.getClass())) {
				throw new UnsupportedOperationException("Item " + this + " already contains a component of type " + component.getClass());
			}
			components.put(component.getClass(), component);
			component.onInit();
		}
	}

	public <T extends ItemComponent> T getComponentOfType(Class<T> componentClass) {
		ItemComponent component = components.get(componentClass);
		return componentClass.cast(component);
	}

	public <T extends ItemComponent> boolean hasComponentOfType(Class<T> componentClass) {
		return components.containsKey(componentClass);
	}

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			Context context = new Context(game(), subject, target, this);
			getTemplate().getScripts().get(entryPoint).execute(context);
		}
	}

	protected ItemTemplate getTemplate() {
		return game().data().getItemTemplate(templateID);
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
	
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(this));
		if (subject.getInventory().itemCount(this) > 1) {
			actions.add(new ActionItemDropAll(this));
		}
		if (this.getDescription() != null) {
			actions.add(new ActionInspectItem(this));
		}
		for (ItemComponent component : components.values()) {
			actions.addAll(component.getInventoryActions(subject));
		}
		for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
			actions.add(new ActionCustom(game(), null, null, this, null, customAction.action(), customAction.parameters(), new MenuDataInventory(this, subject.getInventory()), false));
		}
		return actions;
	}

	public Set<String> getTags() {
		return getTemplate().getTags();
	}

	@Override
	public void addEffect(String effectID) {
		if (hasComponentOfType(ItemComponentEffectible.class)) {
			getComponentOfType(ItemComponentEffectible.class).addEffect(effectID);
		}
	}

	@Override
	public void removeEffect(String effectID) {
		if (hasComponentOfType(ItemComponentEffectible.class)) {
			getComponentOfType(ItemComponentEffectible.class).removeEffect(effectID);
		}
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		for (ItemComponent component : components.values()) {
			Expression componentValue = component.getStatValue(name, context);
			if (componentValue != null) return componentValue;
		}
		return switch (name) {
			case "inventory" -> (currentInventory == null ? null : Expression.constant(currentInventory));
			case "noun" -> Expression.constantNoun(this);
			case "id" -> Expression.constant(getID());
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		for (ItemComponent component : components.values()) {
			boolean success = component.setStatValue(name, value, context);
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
	public StatHolder getSubHolder(String name, String ID) {
		for (ItemComponent component : components.values()) {
			StatHolder componentValue = component.getSubHolder(name, ID);
			if (componentValue != null) return componentValue;
		}
		if ("template".equals(name)) {
			return getTemplate();
		}
		return null;
	}

	@Override
	public StatInt getStatInt(String name) {
		for (ItemComponent component : components.values()) {
			StatInt componentValue = component.getStatInt(name);
			if (componentValue != null) return componentValue;
		}
		return null;
	}

	@Override
	public StatFloat getStatFloat(String name) {
		for (ItemComponent component : components.values()) {
			StatFloat componentValue = component.getStatFloat(name);
			if (componentValue != null) return componentValue;
		}
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		for (ItemComponent component : components.values()) {
			StatBoolean componentValue = component.getStatBoolean(name);
			if (componentValue != null) return componentValue;
		}
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		for (ItemComponent component : components.values()) {
			StatString componentValue = component.getStatString(name);
			if (componentValue != null) return componentValue;
		}
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		for (ItemComponent component : components.values()) {
			StatStringSet componentValue = component.getStatStringSet(name);
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

	public void loadState(SaveData saveData) {}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(hasState()) {
			state.add(new SaveData(SaveData.DataType.ITEM_INSTANCE, this.getID(), null, this.getTemplate().getID()));
		}
		return state;
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
