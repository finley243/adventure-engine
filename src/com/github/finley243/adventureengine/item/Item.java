package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionInspectItem;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemDropAll;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Item extends GameInstanced implements Noun, StatHolder {

	private boolean isKnown;

	public Item(Game game, String ID) {
		super(game, ID);
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
	public Context.Pronoun getPronoun() {
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
			getTemplate().getScripts().get(entryPoint).execute(new ContextScript(game(), subject, target, null, this));
		}
	}

	public abstract ItemTemplate getTemplate();
	
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(this));
		if (subject.getInventory().itemCount(this) > 1) {
			actions.add(new ActionItemDropAll(this));
		}
		if (this.getDescription() != null) {
			actions.add(new ActionInspectItem(this));
		}
		if (getTemplate().getAttackType() != null) {
			actions.addAll(game().data().getAttackType(getTemplate().getAttackType()).generateActions(subject, null, this));
		}
		return actions;
	}

	@Override
	public StatInt getStatInt(String name) {
		return null;
	}

	@Override
	public StatFloat getStatFloat(String name) {
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		return null;
	}

	@Override
	public int getValueInt(String name) {
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
		switch (name) {
			case "id":
				return getID();
			case "templateID":
				return getTemplate().getID();
		}
		return null;
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return null;
	}

	@Override
	public void onStatChange() {

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
	public void triggerEffect(String name) {

	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
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
		return o instanceof Item && getTemplate() == ((Item) o).getTemplate() && (!getTemplate().hasState() || getID().equals(((Item) o).getID()));
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
