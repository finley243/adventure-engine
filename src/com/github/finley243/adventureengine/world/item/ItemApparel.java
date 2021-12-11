package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionApparelEquip;
import com.github.finley243.adventureengine.action.ActionApparelUnequip;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ApparelManager;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.world.template.StatsApparel;

import java.util.ArrayList;
import java.util.List;

public class ItemApparel extends Item {

	private final StatsApparel stats;
	private List<Effect> effects;
	
	public ItemApparel(StatsApparel stats) {
		super(stats.getName());
		this.stats = stats;
	}
	
	@Override
	public String getDescription() {
		return stats.getDescription();
	}
	
	@Override
	public int getPrice() {
		return stats.getPrice();
	}
	
	@Override
	public String getStatsID() {
		return stats.getID();
	}

	public ApparelManager.ApparelSlot getApparelSlot() {
		return stats.getSlot();
	}

	public int getDamageResistance() {
		return stats.getDamageResistance();
	}

	public void equip(Actor target) {
		if(effects != null) throw new UnsupportedOperationException("Cannot equip ItemApparel " + this.getStatsID() + " because it is already equipped");
		effects = new ArrayList<>();
		for(Effect effect : stats.getEffects()) {
			Effect generatedEffect = effect.generate();
			effects.add(generatedEffect);
			target.addEffect(generatedEffect);
		}
	}

	public void unequip(Actor target) {
		if(effects == null) throw new UnsupportedOperationException("Cannot unequip ItemApparel " + this.getStatsID() + " because it is not equipped");
		for(Effect effect : effects) {
			target.removeEffect(effect);
		}
		effects = null;
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
	
	@Override
	public boolean equalsInventory(Item o) {
		if(!(o instanceof ItemApparel)) {
			return false;
		} else {
			ItemApparel other = (ItemApparel) o;
			return this.stats == other.stats;
		}
	}

}
