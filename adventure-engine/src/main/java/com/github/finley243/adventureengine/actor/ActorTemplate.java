package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.*;

public class ActorTemplate extends GameInstanced {

	private final ActorTemplate parent;
	
	private final String name;
	private final Boolean isProperName;
	private final Pronoun pronoun;

	private final Faction faction;
	private final Boolean isEnforcer;

	private final Integer actionPoints;
	private final Integer movePoints;

	private final Integer startingLevel;
	private final Script levelUpThresholdExpression;
	private final Integer maxHP;
	private final Map<String, Integer> damageResistance;
	private final Map<String, Float> damageMult;
	private final List<Limb> limbs;
	private final Map<String, EquipSlot> equipSlots;
	private final Map<String, Integer> attributes;
	private final Map<String, Integer> skills;
	private final Set<SenseType> senseTypes;
	private final List<AttackType> unarmedAttackTypes;

	private final List<Effect> startingEffects;
	
	private final LootTable lootTable;
	private final Scene dialogueStart;

	private final Map<String, List<Script>> scripts;
	private final Map<String, Bark> barks;

	private final List<ActionCustom.CustomActionHolder> customActions;
	private final List<ActionCustom.CustomActionHolder> customInventoryActions;
	
	public ActorTemplate(String ID, ActorTemplate parent, String name, Boolean isProperName, Pronoun pronoun, Faction faction, Boolean isEnforcer, Integer actionPoints, Integer movePoints, Integer startingLevel, Script levelUpThresholdExpression, Integer maxHP, Map<String, Integer> damageResistance, Map<String, Float> damageMult, List<Limb> limbs, Map<String, EquipSlot> equipSlots, Map<String, Integer> attributes, Map<String, Integer> skills, Set<SenseType> senseTypes, List<AttackType> unarmedAttackTypes, List<Effect> startingEffects, LootTable lootTable, Scene dialogueStart, Map<String, List<Script>> scripts, Map<String, Bark> barks, List<ActionCustom.CustomActionHolder> customActions, List<ActionCustom.CustomActionHolder> customInventoryActions) {
		super(ID);
		if (parent == null) {
			if (name == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: name");
			if (isProperName == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isProperName");
			if (pronoun == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: pronoun");
			if (faction == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: faction");
			if (isEnforcer == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: isEnforcer");
			if (actionPoints == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: actionPoints");
			if (movePoints == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: movesPerTurn");
			if (startingLevel == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: startingLevel");
			if (levelUpThresholdExpression == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: levelUpThresholdExpression");
			if (maxHP == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: maxHP");
			if (equipSlots.isEmpty()) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: equipSlots");
			if (lootTable == null) throw new IllegalArgumentException("(Actor: " + ID + ") Must specify parameters for non-parented template: lootTable");
		}
		this.parent = parent;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.faction = faction;
		this.isEnforcer = isEnforcer;
		this.actionPoints = actionPoints;
		this.movePoints = movePoints;
		this.startingLevel = startingLevel;
		this.levelUpThresholdExpression = levelUpThresholdExpression;
		this.maxHP = maxHP;
		this.damageResistance = damageResistance;
		this.damageMult = damageMult;
		this.limbs = limbs;
		this.equipSlots = equipSlots;
		this.attributes = attributes;
		this.skills = skills;
		this.senseTypes = senseTypes;
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
		return name != null ? name : getParent().getName();
	}
	
	public boolean isProperName() {
		return isProperName != null ? isProperName : getParent().isProperName();
	}
	
	public Pronoun getPronoun() {
		return pronoun != null ? pronoun : getParent().getPronoun();
	}
	
	public Faction getFaction() {
		return faction != null ? faction : getParent().getFaction();
	}

	public boolean isEnforcer() {
		return isEnforcer != null ? isEnforcer : getParent().isEnforcer();
	}

	public int getActionPoints() {
		return actionPoints != null ? actionPoints : getParent().getActionPoints();
	}

	public int getMovePoints() {
		return movePoints != null ? movePoints : getParent().getMovePoints();
	}

	public int getStartingLevel() {
		return startingLevel != null ? startingLevel : getParent().getStartingLevel();
	}

	public Script getLevelUpThresholdExpression() {
		return levelUpThresholdExpression != null ? levelUpThresholdExpression : getParent().getLevelUpThresholdExpression();
	}
	
	public int getMaxHP() {
		return maxHP != null ? maxHP : getParent().getMaxHP();
	}

	public int getDamageResistance(String damageType) {
		if (damageResistance.containsKey(damageType)) {
			return damageResistance.get(damageType);
		} else if (parent != null) {
			return getParent().getDamageResistance(damageType);
		} else {
			return 0;
		}
	}

	public float getDamageMult(String damageType) {
		if (damageMult.containsKey(damageType)) {
			return damageMult.get(damageType);
		} else if (parent != null) {
			return getParent().getDamageMult(damageType);
		} else {
			return 0.0f;
		}
	}

	public List<Limb> getLimbs() {
		return !limbs.isEmpty() ? limbs : getParent().getLimbs();
	}

	public Map<String, EquipSlot> getEquipSlots() {
		if (!equipSlots.isEmpty()) {
			return equipSlots;
		} else if (parent != null) {
			return getParent().getEquipSlots();
		} else {
			return new HashMap<>();
		}
	}

	public int getAttribute(String attribute) {
		if (attributes.containsKey(attribute)) {
			return attributes.get(attribute);
		} else if (parent != null) {
			return getParent().getAttribute(attribute);
		} else {
			return 0;
		}
	}

	public int getSkill(String skill) {
		if (skills.containsKey(skill)) {
			return skills.get(skill);
		} else if (parent != null) {
			return getParent().getSkill(skill);
		} else {
			return 0;
		}
	}

	public Set<SenseType> getSenseTypes() {
		if (!senseTypes.isEmpty()) {
			return senseTypes;
		} else if (parent != null) {
			return getParent().getSenseTypes();
		} else {
			return new HashSet<>();
		}
	}

	public List<AttackType> getUnarmedAttackTypes() {
		if (!unarmedAttackTypes.isEmpty()) {
			return unarmedAttackTypes;
		} else if (parent != null) {
			return getParent().getUnarmedAttackTypes();
		} else {
			return new ArrayList<>();
		}
	}

	public List<Effect> getStartingEffects() {
		return !startingEffects.isEmpty() || parent == null ? startingEffects : getParent().getStartingEffects();
	}
	
	public LootTable getLootTable() {
		if (lootTable == null && parent == null) return null;
		return lootTable != null ? lootTable : getParent().getLootTable();
	}

	public Scene getDialogueStart() {
		if (dialogueStart == null && parent == null) return null;
		return dialogueStart != null ? dialogueStart : getParent().getDialogueStart();
	}

	public List<Script> getScripts(String trigger) {
		List<Script> combinedScripts = new ArrayList<>();
		if (scripts.containsKey(trigger)) {
			combinedScripts.addAll(scripts.get(trigger));
		}
		if (parent != null) {
			combinedScripts.addAll(getParent().getScripts(trigger));
		}
		return combinedScripts;
	}

	public Bark getBark(String trigger) {
		if (barks.containsKey(trigger)) {
			return barks.get(trigger);
		} else if (parent != null) {
			return getParent().getBark(trigger);
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

	private ActorTemplate getParent() {
		return parent;
	}
	
}
