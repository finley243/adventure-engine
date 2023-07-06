package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Item extends GameInstanced implements Noun, StatHolder {

	private boolean isKnown;
	private final String templateID;

	public Item(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
	}

	@Override
	public String getName() {
		return getTemplate().getName();
	}

	@Override
	public String getFormattedName() {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), !isKnown());
		} else {
			return getName();
		}
	}

	@Override
	public void setKnown() {
		if (getTemplate().hasState()) {
			isKnown = true;
		}
	}

	@Override
	public boolean isKnown() {
		return getTemplate().hasState() && isKnown;
	}

	@Override
	public boolean isProperName() {
		return getTemplate().isProperName();
	}

	@Override
	public TextContext.Pronoun getPronoun() {
		return getTemplate().getPronoun();
	}

	@Override
	public boolean forcePronoun() {
		return getTemplate().forcePronoun();
	}

	public Scene getDescription() {
		return getTemplate().getDescription();
	}

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if(getTemplate().getScripts().containsKey(entryPoint)) {
			getTemplate().getScripts().get(entryPoint).execute(new Context(game(), subject, target, this));
		}
	}

	protected ItemTemplate getTemplate() {
		return game().data().getItemTemplate(templateID);
	}

	public String getTemplateID() {
		return getTemplate().getID();
	}

	public boolean hasState() {
		return getTemplate().hasState();
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
		for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
			ActionCustom action = new ActionCustom(game(), null, this, null, customAction.action(), customAction.parameters(), new String[] {"Inventory", Inventory.getItemNameFormatted(this, subject.getInventory())}, false);
			if (action.canShow(subject)) {
				actions.add(action);
			}
		}
		return actions;
	}

	@Override
	public int getValueInt(String name, Context context) {
		return 0;
	}

	@Override
	public float getValueFloat(String name, Context context) {
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name, Context context) {
		return false;
	}

	@Override
	public String getValueString(String name, Context context) {
		if ("id".equals(name)) {
			return getID();
		}
		return null;
	}

	@Override
	public Set<String> getValueStringSet(String name, Context context) {
		return null;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "id" -> new ExpressionConstantString(getID());
			default -> null;
		};
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
		if ("template".equals(name)) {
			return getTemplate();
		}
		return null;
	}

	public void loadState(SaveData saveData) {}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(getTemplate().hasState()) {
			state.add(new SaveData(SaveData.DataType.ITEM_INSTANCE, this.getID(), null, this.getTemplate().getID()));
		}
		return state;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Item otherItem && getTemplate().equals(otherItem.getTemplate()) && (!getTemplate().hasState() || getID().equals(otherItem.getID()));
	}

	@Override
	public int hashCode() {
		if (getTemplate().hasState()) {
			return (31 * getTemplate().hashCode()) + getID().hashCode();
		} else {
			return getTemplate().hashCode();
		}
	}
	
}
