package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.item.LootTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActorTemplate {
	
	private final String ID;
	private final String parentID;
	
	private final String name;
	private final boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	private final boolean isEnforcer;

	private final int maxHP;
	private final List<Limb> limbs;
	private final Map<Actor.Attribute, Integer> attributes;
	private final Map<Actor.Skill, Integer> skills;
	
	private final LootTable lootTable;
	private final String dialogueStart;

	private final boolean isVendor;
	private final String vendorLootTable;
	private final Set<String> vendorBuyTags;
	private final boolean vendorBuyAll;
	private final boolean vendorStartDisabled;

	private final Map<String, Script> scripts;
	private final Map<String, Bark> barks;
	
	public ActorTemplate(String ID, String parentID, String name, boolean isProperName, Pronoun pronoun, String faction, boolean isEnforcer, int maxHP, List<Limb> limbs, Map<Actor.Attribute, Integer> attributes, Map<Actor.Skill, Integer> skills, LootTable lootTable, String dialogueStart, Map<String, Script> scripts, Map<String, Bark> barks, boolean isVendor, String vendorLootTable, Set<String> vendorBuyTags, boolean vendorBuyAll, boolean vendorStartDisabled) {
		this.ID = ID;
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.isEnforcer = isEnforcer;
		this.maxHP = maxHP;
		this.limbs = limbs;
		this.attributes = attributes;
		this.skills = skills;
		this.lootTable = lootTable;
		this.dialogueStart = dialogueStart;
		this.scripts = scripts;
		this.barks = barks;
		this.isVendor = isVendor;
		this.vendorLootTable = vendorLootTable;
		this.vendorBuyTags = vendorBuyTags;
		this.vendorBuyAll = vendorBuyAll;
		this.vendorStartDisabled = vendorStartDisabled;
	}
	
	public String getID() {
		return ID;
	}

	// TODO - Find a better way to inherit values from parent
	public String getName(Game game) {
		return name != null ? name : game.data().getActorTemplate(parentID).getName(game);
	}
	
	public boolean isProperName(Game game) {
		return name != null ? isProperName : game.data().getActorTemplate(parentID).isProperName(game);
	}
	
	public Pronoun getPronoun(Game game) {
		return pronoun != null ? pronoun : game.data().getActorTemplate(parentID).getPronoun(game);
	}
	
	public String getFaction(Game game) {
		return faction != null ? faction : game.data().getActorTemplate(parentID).getFaction(game);
	}

	public boolean isEnforcer(Game game) {
		if (isEnforcer) {
			return true;
		} else if (parentID != null) {
			return game.data().getActorTemplate(parentID).isEnforcer(game);
		} else {
			return false;
		}
	}
	
	public int getMaxHP(Game game) {
		return maxHP > 0 ? maxHP : game.data().getActorTemplate(parentID).getMaxHP(game);
	}

	public List<Limb> getLimbs(Game game) {
		return !limbs.isEmpty() ? limbs : game.data().getActorTemplate(parentID).getLimbs(game);
	}

	public int getAttribute(Game game, Actor.Attribute attribute) {
		return attributes.containsKey(attribute) ? attributes.get(attribute) : game.data().getActorTemplate(parentID).getAttribute(game, attribute);
	}

	public int getSkill(Game game, Actor.Skill skill) {
		return skills.containsKey(skill) ? skills.get(skill) : game.data().getActorTemplate(parentID).getSkill(game, skill);
	}
	
	public LootTable getLootTable(Game game) {
		if(lootTable == null && parentID.isEmpty()) return null;
		return lootTable != null ? lootTable : game.data().getActorTemplate(parentID).getLootTable(game);
	}

	public String getDialogueStart(Game game) {
		if(dialogueStart == null && parentID.isEmpty()) return null;
		return dialogueStart != null ? dialogueStart : game.data().getActorTemplate(parentID).getDialogueStart(game);
	}

	public Script getScript(Game game, String trigger) {
		if (scripts.containsKey(trigger)) {
			return scripts.get(trigger);
		} else if (parentID != null) {
			return game.data().getActorTemplate(parentID).getScript(game, trigger);
		} else {
			return null;
		}
	}

	public Bark getBark(Game game, String trigger) {
		if (barks.containsKey(trigger)) {
			return barks.get(trigger);
		} else if (parentID != null) {
			return game.data().getActorTemplate(parentID).getBark(game, trigger);
		} else {
			return null;
		}
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
