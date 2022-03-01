package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsActor {
	
	private final String ID;
	private final String parentID;
	
	private final String name;
	private final boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	
	private final int maxHP;
	private final List<Limb> limbs;
	private final Map<Actor.Attribute, Integer> attributes;
	private final Map<Actor.Skill, Integer> skills;
	
	private final String lootTable;
	private final String topic;

	private final boolean isVendor;
	private final String vendorLootTable;
	private final Set<String> vendorBuyTags;
	private final boolean vendorBuyAll;
	private final boolean vendorStartDisabled;

	private final Map<String, Script> scripts;
	
	public StatsActor(String ID, String parentID, String name, boolean isProperName, Pronoun pronoun, String faction, int maxHP, List<Limb> limbs, Map<Actor.Attribute, Integer> attributes, Map<Actor.Skill, Integer> skills, String lootTable, String topic, Map<String, Script> scripts, boolean isVendor, String vendorLootTable, Set<String> vendorBuyTags, boolean vendorBuyAll, boolean vendorStartDisabled) {
		this.ID = ID;
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.maxHP = maxHP;
		this.limbs = limbs;
		this.attributes = attributes;
		this.skills = skills;
		this.lootTable = lootTable;
		this.topic = topic;
		this.scripts = scripts;
		this.isVendor = isVendor;
		this.vendorLootTable = vendorLootTable;
		this.vendorBuyTags = vendorBuyTags;
		this.vendorBuyAll = vendorBuyAll;
		this.vendorStartDisabled = vendorStartDisabled;
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

	public int getAttribute(Actor.Attribute attribute) {
		return attributes.containsKey(attribute) ? attributes.get(attribute) : Data.getActorStats(parentID).getAttribute(attribute);
	}

	public int getSkill(Actor.Skill skill) {
		return skills.containsKey(skill) ? skills.get(skill) : Data.getActorStats(parentID).getSkill(skill);
	}
	
	public String getLootTable() {
		if(lootTable == null && parentID.isEmpty()) return null;
		return lootTable != null ? lootTable : Data.getActorStats(parentID).getLootTable();
	}

	public String getTopic() {
		if(topic == null && parentID.isEmpty()) return null;
		return topic != null ? topic : Data.getActorStats(parentID).getTopic();
	}

	public Map<String, Script> getScripts() {
		return scripts;
	}

	public boolean isVendor() {
		return isVendor;
	}

	public String getVendorLootTable() {
		return vendorLootTable;
	}

	public Set<String> vendorBuyTags() {
		return vendorBuyTags;
	}

	public boolean vendorBuyAll() {
		return vendorBuyAll;
	}

	public boolean vendorStartDisabled() {
		return vendorStartDisabled;
	}
	
}
