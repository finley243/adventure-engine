package com.github.finley243.adventureengine.world.template;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public class StatsActor {
	
	private final String ID;
	
	private final String name;
	private final boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	
	private final int maxHP;
	
	private final String lootTable;
	private final String topic;
	
	public StatsActor(String ID, String name, boolean isProperName, Pronoun pronoun, String faction, int maxHP, String lootTable, String topic) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.maxHP = maxHP;
		this.lootTable = lootTable;
		this.topic = topic;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isProperName() {
		return isProperName;
	}
	
	public Pronoun getPronoun() {
		return pronoun;
	}
	
	public String getFaction() {
		return faction;
	}
	
	public int getMaxHP() {
		return maxHP;
	}
	
	public String getLootTable() {
		return lootTable;
	}

	public String getTopic() {
		return topic;
	}
	
}
