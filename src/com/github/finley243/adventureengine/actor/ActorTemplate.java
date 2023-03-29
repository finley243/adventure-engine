package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActorTemplate extends GameInstanced {
	
	private final String parentID;
	
	private final String name;
	private final Boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	private final Boolean isEnforcer;

	private final Integer maxHP;
	private final Map<Damage.DamageType, Integer> damageResistance;
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
	
	public ActorTemplate(Game game, String ID, String parentID, String name, Boolean isProperName, Pronoun pronoun, String faction, Boolean isEnforcer, Integer maxHP, Map<Damage.DamageType, Integer> damageResistance, List<Limb> limbs, String defaultApparelSlot, Map<Actor.Attribute, Integer> attributes, Map<Actor.Skill, Integer> skills, LootTable lootTable, String dialogueStart, Map<String, Script> scripts, Map<String, Bark> barks, Boolean isVendor, String vendorLootTable, Set<String> vendorBuyTags, Boolean vendorBuyAll, Boolean vendorStartDisabled) {
		super(game, ID);
		if (parentID == null) {
			if (name == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: name");
			if (isProperName == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isProperName");
			if (pronoun == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: pronoun");
			if (faction == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: faction");
			if (isEnforcer == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isEnforcer");
			if (maxHP == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: maxHP");
			for (Damage.DamageType damageType : Damage.DamageType.values()) {
				if (!damageResistance.containsKey(damageType)) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: damage resistance - " + damageType);
			}
			if (defaultApparelSlot == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: defaultApparelSlot");
			for (Actor.Attribute attribute : Actor.Attribute.values()) {
				if (!attributes.containsKey(attribute)) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: attribute - " + attribute);
			}
			for (Actor.Skill skill : Actor.Skill.values()) {
				if (!skills.containsKey(skill)) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: skill - " + skill);
			}
			if (lootTable == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: lootTable");
		}
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.isEnforcer = isEnforcer;
		this.maxHP = maxHP;
		this.damageResistance = damageResistance;
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

	// TODO - Find a better way to inherit values from parent
	public String getName() {
		return name != null ? name : game().data().getActorTemplate(parentID).getName();
	}
	
	public boolean isProperName() {
		return isProperName != null ? isProperName : game().data().getActorTemplate(parentID).isProperName();
	}
	
	public Pronoun getPronoun() {
		return pronoun != null ? pronoun : game().data().getActorTemplate(parentID).getPronoun();
	}
	
	public String getFaction() {
		return faction != null ? faction : game().data().getActorTemplate(parentID).getFaction();
	}

	public boolean isEnforcer() {
		return isEnforcer != null ? isEnforcer : game().data().getActorTemplate(parentID).isEnforcer();
	}
	
	public int getMaxHP() {
		return maxHP != null ? maxHP : game().data().getActorTemplate(parentID).getMaxHP();
	}

	public int getDamageResistance(Damage.DamageType damageType) {
		if (damageResistance.containsKey(damageType)) {
			return damageResistance.get(damageType);
		} else {
			return game().data().getActorTemplate(parentID).getDamageResistance(damageType);
		}
	}

	public List<Limb> getLimbs() {
		return !limbs.isEmpty() ? limbs : game().data().getActorTemplate(parentID).getLimbs();
	}

	public String getDefaultApparelSlot() {
		return defaultApparelSlot != null ? defaultApparelSlot : game().data().getActorTemplate(parentID).getDefaultApparelSlot();
	}

	public int getAttribute(Actor.Attribute attribute) {
		return attributes.containsKey(attribute) ? attributes.get(attribute) : game().data().getActorTemplate(parentID).getAttribute(attribute);
	}

	public int getSkill(Actor.Skill skill) {
		return skills.containsKey(skill) ? skills.get(skill) : game().data().getActorTemplate(parentID).getSkill(skill);
	}
	
	public LootTable getLootTable() {
		if (lootTable == null && parentID == null) return null;
		return lootTable != null ? lootTable : game().data().getActorTemplate(parentID).getLootTable();
	}

	public String getDialogueStart() {
		if (dialogueStart == null && parentID == null) return null;
		return dialogueStart != null ? dialogueStart : game().data().getActorTemplate(parentID).getDialogueStart();
	}

	public Script getScript(String trigger) {
		if (scripts.containsKey(trigger)) {
			return scripts.get(trigger);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getScript(trigger);
		} else {
			return null;
		}
	}

	public Bark getBark(String trigger) {
		if (barks.containsKey(trigger)) {
			return barks.get(trigger);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getBark(trigger);
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
