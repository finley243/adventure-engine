package com.github.finley243.adventureengine.world.template;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public class StatsActor {
	
	private String ID;
	
	private String name;
	private boolean isProperName;
	private Pronoun pronoun;
	
	private Faction faction;
	
	private List<String> idle;
	private boolean preventMovement;
	
	private int maxHP;
	
	private String lootTable;
	private String topic;
	
	public StatsActor(String ID, String name, boolean isProperName, Pronoun pronoun, String faction, List<String> idle, boolean preventMovement, int maxHP, String lootTable, String topic) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = Data.getFaction(faction);
		this.idle = idle;
		this.preventMovement = preventMovement;
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
	
	public Faction getFaction() {
		return faction;
	}
	
	public List<String> getIdle() {
		return idle;
	}
	
	public boolean preventMovement() {
		return preventMovement;
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
