package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemInspect;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class Item implements Noun {

	// ID is null if the item is stateless
	private final String ID;
	// Determines whether a new version of this item needs to be created when loading a save file
	private final boolean isGenerated;

	public Item(Game game, boolean isGenerated, String ID) {
		this.isGenerated = isGenerated;
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return getTemplate().getName();
	}

	@Override
	public String getFormattedName() {
		return getTemplate().getFormattedName();
	}

	@Override
	public void setKnown() {

	}

	@Override
	public boolean isKnown() {
		return false;
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

	public String getDescription() {
		return getTemplate().getDescription();
	}

	public void triggerScript(String entryPoint, Actor subject) {
		if(getTemplate().getScripts().containsKey(entryPoint)) {
			getTemplate().getScripts().get(entryPoint).execute(subject);
		}
	}

	public abstract ItemTemplate getTemplate();
	
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionItemDrop(this));
		if(this.getDescription() != null) {
			actions.add(new ActionItemInspect(this));
		}
		return actions;
	}

	public void loadState(SaveData saveData) {}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		/*if(isGenerated) {
			state.add(0, new SaveData(SaveData.DataType.ITEM_INSTANCE, this.getID(), null, this.getTemplate().getID()));
		}*/
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
