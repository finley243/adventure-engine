package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionItemDrop;
import com.github.finley243.adventureengine.action.ActionItemDropAll;
import com.github.finley243.adventureengine.action.ActionInspectItem;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.object.ObjectItem;

import java.util.ArrayList;
import java.util.List;

public abstract class Item extends GameInstanced implements Noun {

	// ID is null if the item is stateless
	private final String ID;

	public Item(Game game, String ID) {
		super(game);
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

	public Scene getDescription() {
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
		if(subject.inventory().itemCount(this) > 1) {
			actions.add(new ActionItemDropAll(this));
		}
		if(this.getDescription() != null) {
			actions.add(new ActionInspectItem(this));
		}
		return actions;
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

	public static void itemToObject(Game game, Item item, int count, Area area) {
		String ID = item.getID();
		if (ID == null) {
			ID = item.getTemplate().generateInstanceID();
		}
		ObjectItem object = new ObjectItem(game, ID, area, item, count);
		game.data().addObject(object.getID(), object);
	}

	public static Item objectToItem(Game game, ObjectItem objectItem, int count) {
		Item item = objectItem.getItem();
		int remainingCount = objectItem.getCount() - count;
		if (remainingCount <= 0) {
			objectItem.getArea().removeObject(objectItem);
			game.data().removeObject(objectItem.getID());
		} else {
			objectItem.addCount(-count);
		}
		return item;
	}
	
}
