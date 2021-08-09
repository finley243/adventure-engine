package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public class StatsActor {
	
	private String ID;
	
	private String name;
	private boolean isProperName;
	private Pronoun pronoun;
	
	private Faction faction;
	
	private int maxHP;
	
	private String lootTable;
	
	public StatsActor(String ID, String name, boolean isProperName, Pronoun pronoun, String faction, int maxHP, String lootTable) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = Data.getFaction(faction);
		this.maxHP = maxHP;
		this.lootTable = lootTable;
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
	
	public Faction getFaction() {
		return faction;
	}
	
	public int getMaxHP() {
		return maxHP;
	}
	
	public String getLootTable() {
		return lootTable;
	}
	
}
