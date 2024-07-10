package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.*;

public class ActorTemplate extends GameInstanced {
	
	private final String parentID;
	
	private final String name;
	private final Boolean isProperName;
	private final Pronoun pronoun;
	
	private final String faction;
	private final Boolean isEnforcer;

	private final Integer actionPoints;
	private final Integer movePoints;

	private final Integer maxHP;
	private final Map<String, Integer> damageResistance;
	private final Map<String, Float> damageMult;
	private final List<Limb> limbs;
	private final Map<String, EquipSlot> equipSlots;
	private final Map<String, Integer> attributes;
	private final Map<String, Integer> skills;
	private final Set<String> senseTypes;
	private final Set<String> tags;
	private final List<String> unarmedAttackTypes;

	private final List<String> startingEffects;
	
	private final LootTable lootTable;
	private final String dialogueStart;

	private final Map<String, Script> scripts;
	private final Map<String, Bark> barks;

	private final List<ActionCustom.CustomActionHolder> customActions;
	private final List<ActionCustom.CustomActionHolder> customInventoryActions;
	
	public ActorTemplate(Game game, String ID, String parentID, String name, Boolean isProperName, Pronoun pronoun, String faction, Boolean isEnforcer, Integer actionPoints, Integer movePoints, Integer maxHP, Map<String, Integer> damageResistance, Map<String, Float> damageMult, List<Limb> limbs, Map<String, EquipSlot> equipSlots, Map<String, Integer> attributes, Map<String, Integer> skills, Set<String> senseTypes, Set<String> tags, List<String> unarmedAttackTypes, List<String> startingEffects, LootTable lootTable, String dialogueStart, Map<String, Script> scripts, Map<String, Bark> barks, List<ActionCustom.CustomActionHolder> customActions, List<ActionCustom.CustomActionHolder> customInventoryActions) {
		super(game, ID);
		if (parentID == null) {
			if (name == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: name");
			if (isProperName == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isProperName");
			if (pronoun == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: pronoun");
			if (faction == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: faction");
			if (isEnforcer == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isEnforcer");
			if (actionPoints == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: actionPoints");
			if (movePoints == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: movesPerTurn");
			if (maxHP == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: maxHP");
			if (equipSlots.isEmpty()) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: equipSlots");
			if (lootTable == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: lootTable");
		}
		this.parentID = parentID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.isEnforcer = isEnforcer;
		this.actionPoints = actionPoints;
		this.movePoints = movePoints;
		this.maxHP = maxHP;
		this.damageResistance = damageResistance;
		this.damageMult = damageMult;
		this.limbs = limbs;
		this.equipSlots = equipSlots;
		this.attributes = attributes;
		this.skills = skills;
		this.senseTypes = senseTypes;
		this.tags = tags;
		this.unarmedAttackTypes = unarmedAttackTypes;
		this.startingEffects = startingEffects;
		this.lootTable = lootTable;
		this.dialogueStart = dialogueStart;
		this.scripts = scripts;
		this.barks = barks;
		this.customActions = customActions;
		this.customInventoryActions = customInventoryActions;
	}

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

	public int getActionPoints() {
		return actionPoints != null ? actionPoints : game().data().getActorTemplate(parentID).getActionPoints();
	}

	public int getMovePoints() {
		return movePoints != null ? movePoints : game().data().getActorTemplate(parentID).getMovePoints();
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

	public float getDamageMult(String damageType) {
		if (damageMult.containsKey(damageType)) {
			return damageMult.get(damageType);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getDamageMult(damageType);
		} else {
			return 0.0f;
		}
	}

	public List<Limb> getLimbs() {
		return !limbs.isEmpty() ? limbs : game().data().getActorTemplate(parentID).getLimbs();
	}

	public Map<String, EquipSlot> getEquipSlots() {
		if (!equipSlots.isEmpty()) {
			return equipSlots;
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getEquipSlots();
		} else {
			return new HashMap<>();
		}
	}

	public int getAttribute(String attribute) {
		if (attributes.containsKey(attribute)) {
			return attributes.get(attribute);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getAttribute(attribute);
		} else {
			return 0;
		}
	}

	public int getSkill(String skill) {
		if (skills.containsKey(skill)) {
			return skills.get(skill);
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getSkill(skill);
		} else {
			return 0;
		}
	}

	public Set<String> getSenseTypes() {
		if (!senseTypes.isEmpty()) {
			return senseTypes;
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getSenseTypes();
		} else {
			return new HashSet<>();
		}
	}

	public Set<String> getTags() {
		if (!tags.isEmpty()) {
			return tags;
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getTags();
		} else {
			return new HashSet<>();
		}
	}

	public List<String> getUnarmedAttackTypes() {
		if (!unarmedAttackTypes.isEmpty()) {
			return unarmedAttackTypes;
		} else if (parentID != null) {
			return game().data().getActorTemplate(parentID).getUnarmedAttackTypes();
		} else {
			return new ArrayList<>();
		}
	}

	public List<String> getStartingEffects() {
		return !startingEffects.isEmpty() || parentID == null ? startingEffects : game().data().getActorTemplate(parentID).getStartingEffects();
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

	public List<ActionCustom.CustomActionHolder> getCustomActions() {
		return customActions;
	}

	public List<ActionCustom.CustomActionHolder> getCustomInventoryActions() {
		return customInventoryActions;
	}
	
}
