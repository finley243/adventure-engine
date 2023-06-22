package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

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
	private final Map<String, Integer> damageResistance;
	private final List<Limb> limbs;
	private final Set<String> equipSlots;
	private final String defaultEquipSlot;
	private final Map<Actor.Attribute, Integer> attributes;
	private final Map<Actor.Skill, Integer> skills;
	
	private final LootTable lootTable;
	private final String dialogueStart;

	private final Map<String, Script> scripts;
	private final Map<String, Bark> barks;
	
	public ActorTemplate(Game game, String ID, String parentID, String name, Boolean isProperName, Pronoun pronoun, String faction, Boolean isEnforcer, Integer maxHP, Map<String, Integer> damageResistance, List<Limb> limbs, Set<String> equipSlots, String defaultEquipSlot, Map<Actor.Attribute, Integer> attributes, Map<Actor.Skill, Integer> skills, LootTable lootTable, String dialogueStart, Map<String, Script> scripts, Map<String, Bark> barks) {
		super(game, ID);
		if (parentID == null) {
			if (name == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: name");
			if (isProperName == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isProperName");
			if (pronoun == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: pronoun");
			if (faction == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: faction");
			if (isEnforcer == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isEnforcer");
			if (maxHP == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: maxHP");
			if (equipSlots.isEmpty()) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: equipSlots");
			if (defaultEquipSlot == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: defaultEquipSlot");
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
		this.equipSlots = equipSlots;
		this.defaultEquipSlot = defaultEquipSlot;
		this.attributes = attributes;
		this.skills = skills;
		this.lootTable = lootTable;
		this.dialogueStart = dialogueStart;
		this.scripts = scripts;
		this.barks = barks;
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

	public int getDamageResistance(String damageType) {
		if (damageResistance.containsKey(damageType)) {
			return damageResistance.get(damageType);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getDamageResistance(damageType);
		} else {
			return 0;
		}
	}

	public List<Limb> getLimbs() {
		return !limbs.isEmpty() ? limbs : game().data().getActorTemplate(parentID).getLimbs();
	}

	public Set<String> getEquipSlots() {
		return !equipSlots.isEmpty() ? equipSlots : game().data().getActorTemplate(parentID).getEquipSlots();
	}

	public String getDefaultEquipSlot() {
		return defaultEquipSlot != null ? defaultEquipSlot : game().data().getActorTemplate(parentID).getDefaultEquipSlot();
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
	
}
