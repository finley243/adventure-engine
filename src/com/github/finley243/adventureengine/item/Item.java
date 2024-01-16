package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Item extends GameInstanced implements Noun, StatHolder {

	private Inventory currentInventory;
	private boolean isKnown;
	private final String templateID;

	public Item(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
		this.currentInventory = null;
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

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if(getTemplate().getScripts().containsKey(entryPoint)) {
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
		for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
			actions.add(new ActionCustom(game(), null, null, this, null, customAction.action(), customAction.parameters(), new MenuDataInventory(this, subject.getInventory()), false));
		}
		return actions;
	}

	public Set<String> getTags() {
		return getTemplate().getTags();
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "inventory" -> (currentInventory == null ? null : Expression.constant(currentInventory));
			case "noun" -> Expression.constant((Noun) this);
			case "id" -> Expression.constant(getID());
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
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
		if ("template".equals(name)) {
			return getTemplate();
		}
		return null;
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
