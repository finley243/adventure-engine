package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionApparelEquip;
import com.github.finley243.adventureengine.action.ActionApparelUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.ApparelComponent;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.ApparelTemplate;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class ItemApparel extends Item {

	private final ApparelTemplate stats;

	public ItemApparel(Game game, String ID, Area area, boolean isGenerated, ApparelTemplate stats) {
		super(game, isGenerated, ID, area, stats.getName(), stats.getDescription(), stats.getScripts());
		this.stats = stats;
	}

	@Override
	public ItemTemplate getTemplate() {
		return stats;
	}

	public ApparelComponent.ApparelSlot getApparelSlot() {
		return stats.getSlot();
	}

	public int getDamageResistance() {
		return stats.getDamageResistance();
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
