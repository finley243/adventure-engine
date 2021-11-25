package com.github.finley243.adventureengine.world.template;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public class StatsActor {
	
	private final String ID;
	private final String parentID;
	
	private final String name;
	private final boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	
	private final int maxHP;
	private final List<Limb> limbs;
	
	private final String lootTable;
	private final String topic;
	
	public StatsActor(String ID, String parentID, String name, boolean isProperName, Pronoun pronoun, String faction, int maxHP, List<Limb> limbs, String lootTable, String topic) {
		this.ID = ID;
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.maxHP = maxHP;
		this.limbs = limbs;
		this.lootTable = lootTable;
		this.topic = topic;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getName() {
		return name != null ? name : Data.getActorStats(parentID).getName();
	}
	
	public boolean isProperName() {
		return name != null ? isProperName : Data.getActorStats(parentID).isProperName();
	}
	
	public Pronoun getPronoun() {
		return pronoun != null ? pronoun : Data.getActorStats(parentID).getPronoun();
	}
	
	public String getFaction() {
		return faction != null ? faction : Data.getActorStats(parentID).getFaction();
	}
	
	public int getMaxHP() {
		return maxHP > 0 ? maxHP : Data.getActorStats(parentID).getMaxHP();
	}

	public List<Limb> getLimbs() {
		return !limbs.isEmpty() ? limbs : Data.getActorStats(parentID).getLimbs();
	}
	
	public String getLootTable() {
		return lootTable != null ? lootTable : Data.getActorStats(parentID).getLootTable();
	}

	public String getTopic() {
		return topic != null ? topic : Data.getActorStats(parentID).getTopic();
	}
	
}
