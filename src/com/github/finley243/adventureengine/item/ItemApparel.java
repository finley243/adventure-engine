package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionApparelEquip;
import com.github.finley243.adventureengine.action.ActionApparelUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.item.template.ApparelTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemApparel extends Item {

	private final String templateID;

	public ItemApparel(Game game, String ID, String templateID) {
		super(game, ID);
		this.templateID = templateID;
	}

	@Override
	public ItemTemplate getTemplate() {
		return getApparelTemplate();
	}

	public ApparelTemplate getApparelTemplate() {
		return (ApparelTemplate) game().data().getItem(templateID);
	}

	public Set<String> getApparelSlots() {
		return getApparelTemplate().getSlots();
	}

	public int getDamageResistance(Damage.DamageType type) {
		return getApparelTemplate().getDamageResistance(type);
	}

	public void onEquip(Actor subject) {
		getApparelTemplate().onEquip(subject);
	}

	public void onUnequip(Actor subject) {
		getApparelTemplate().onUnequip(subject);
	}

	@Override
	public List<Action> inventoryActions(Actor subject) {
		List<Action> actions = super.inventoryActions(subject);
		actions.add(new ActionApparelEquip(this));
		return actions;
	}

	public List<Action> equippedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionApparelUnequip(this));
		return actions;
	}

}
