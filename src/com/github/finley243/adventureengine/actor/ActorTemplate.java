package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.item.LootTable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ActorTemplate {
	
	private final String ID;
	private final String parentID;
	
	private final String name;
	private final Boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	private final Boolean isEnforcer;

	private final Integer maxHP;
	private final List<Limb> limbs;
	private final String defaultApparelSlot;
	private final Map<Actor.Attribute, Integer> attributes;
	private final Map<Actor.Skill, Integer> skills;
	
	private final LootTable lootTable;
	private final String dialogueStart;

	private final Boolean isVendor;
	private final String vendorLootTable;
	private final Set<String> vendorBuyTags;
	private final Boolean vendorBuyAll;
	private final Boolean vendorStartDisabled;

	private final Map<String, Script> scripts;
	private final Map<String, Bark> barks;
	
	public ActorTemplate(String ID, String parentID, String name, Boolean isProperName, Pronoun pronoun, String faction, Boolean isEnforcer, Integer maxHP, List<Limb> limbs, String defaultApparelSlot, Map<Actor.Attribute, Integer> attributes, Map<Actor.Skill, Integer> skills, LootTable lootTable, String dialogueStart, Map<String, Script> scripts, Map<String, Bark> barks, Boolean isVendor, String vendorLootTable, Set<String> vendorBuyTags, Boolean vendorBuyAll, Boolean vendorStartDisabled) {
		if (parentID == null) {
			if (name == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: name");
			if (isProperName == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: isProperName");
			if (pronoun == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: pronoun");
			if (faction == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: faction");
			if (isEnforcer == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: isEnforcer");
			if (maxHP == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: maxHP");
			if (defaultApparelSlot == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: defaultApparelSlot");
			for (Actor.Attribute attribute : Actor.Attribute.values()) {
				if (!attributes.containsKey(attribute)) throw new IllegalArgumentException("Must specify parameters for non-parented template: attribute - " + attribute);
			}
			for (Actor.Skill skill : Actor.Skill.values()) {
				if (!skills.containsKey(skill)) throw new IllegalArgumentException("Must specify parameters for non-parented template: skill - " + skill);
			}
			if (lootTable == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: lootTable");
			if (isVendor == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: isVendor");
			if (vendorLootTable == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: vendorLootTable");
			if (vendorBuyTags == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: vendorBuyTags");
			if (vendorBuyAll == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: vendorBuyAll");
			if (vendorStartDisabled == null) throw new IllegalArgumentException("Must specify parameters for non-parented template: vendorStartDisabled");
		}
		this.ID = ID;
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.isEnforcer = isEnforcer;
		this.maxHP = maxHP;
		this.limbs = limbs;
		this.defaultApparelSlot = defaultApparelSlot;
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
		return Objects.requireNonNullElse(name, game.data().getActorTemplate(parentID).getName(game));
	}
	
	public boolean isProperName(Game game) {
		return Objects.requireNonNullElse(isProperName, game.data().getActorTemplate(parentID).isProperName(game));
	}
	
	public Pronoun getPronoun(Game game) {
		return Objects.requireNonNullElse(pronoun, game.data().getActorTemplate(parentID).getPronoun(game));
	}
	
	public String getFaction(Game game) {
		return Objects.requireNonNullElse(faction, game.data().getActorTemplate(parentID).getFaction(game));
	}

	public boolean isEnforcer(Game game) {
		return Objects.requireNonNullElse(isEnforcer, game.data().getActorTemplate(parentID).isEnforcer(game));
	}
	
	public int getMaxHP(Game game) {
		return Objects.requireNonNullElse(maxHP, game.data().getActorTemplate(parentID).getMaxHP(game));
	}

	public List<Limb> getLimbs(Game game) {
		return !limbs.isEmpty() ? limbs : game.data().getActorTemplate(parentID).getLimbs(game);
	}

	public String getDefaultApparelSlot(Game game) {
		return Objects.requireNonNullElse(defaultApparelSlot, game.data().getActorTemplate(parentID).getDefaultApparelSlot(game));
	}

	public int getAttribute(Game game, Actor.Attribute attribute) {
		return attributes.containsKey(attribute) ? attributes.get(attribute) : game.data().getActorTemplate(parentID).getAttribute(game, attribute);
	}

	public int getSkill(Game game, Actor.Skill skill) {
		return skills.containsKey(skill) ? skills.get(skill) : game.data().getActorTemplate(parentID).getSkill(game, skill);
	}
	
	public LootTable getLootTable(Game game) {
		if(lootTable == null && parentID == null) return null;
		return Objects.requireNonNullElse(lootTable, game.data().getActorTemplate(parentID).getLootTable(game));
	}

	public String getDialogueStart(Game game) {
		if(dialogueStart == null && parentID == null) return null;
		return Objects.requireNonNullElse(dialogueStart, game.data().getActorTemplate(parentID).getDialogueStart(game));
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
