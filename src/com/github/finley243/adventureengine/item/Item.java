package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext;

import java.util.ArrayList;
import java.util.List;

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

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if(getTemplate().getScripts().containsKey(entryPoint)) {
			game().eventQueue().addToEnd(new ScriptEvent(getTemplate().getScripts().get(entryPoint), new Context(game(), subject, target, this)));
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
			actions.add(new ActionCustom(game(), null, null, this, null, customAction.action(), customAction.parameters(), new MenuDataInventory(this), false));
		}
		return actions;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "id" -> new ExpressionConstantString(getID());
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		return false;
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
