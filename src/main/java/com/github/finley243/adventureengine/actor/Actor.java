package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.actor.component.*;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.Effectible;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.InventoryOwner;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.component.ItemComponentEquippable;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

import java.util.*;
import java.util.function.Function;

public class Actor extends GameInstanced implements Noun, Physical, MutableStatHolder, AttackTarget, Effectible, InventoryOwner {

	public static final boolean SHOW_HP_CHANGES = true;
	public static final int ATTRIBUTE_MIN = 1;
	public static final int ATTRIBUTE_MAX = 10;
	public static final int SKILL_MIN = 1;
	public static final int SKILL_MAX = 10;
	public static final int MAX_HP = 1000;
	public static final int MAX_DAMAGE_RESIST = 1000;
	public static final float MAX_DAMAGE_MULT = 0.9f;
	public static final int MAX_ACTION_POINTS = 10;
	public static final int MAX_MOVE_POINTS = 10;

	private final ScriptRuntime scriptRuntime;
    private final SensoryEventDispatcher sensoryEventDispatcher;

	private final boolean isPlayer;
	private final boolean startDisabled;
	private boolean isEnabled;
	private boolean isPlayerControlled;

	private final ActorTemplate template;
	private boolean isKnown;
	private final String nameDescriptor;

	private final Area defaultArea;
	private Area area;

	private final StatStringSetRegistry<SenseType> senseTypes;
	private final Set<AreaTarget> areaTargets;

	private int level;
	private int XP;

	private final StatInt maxHP;
	private int HP;
	private final boolean startDead;
	private boolean isDead;
	private final Map<String, StatInt> damageResistance;
	private final Map<String, StatFloat> damageMult;

	private boolean isSleeping;
	private int sleepCounter;
	private boolean isSneaking;
	private Actor carriedActor;
	private ObjectComponentUsable.ObjectUserData usingObject;

	private final StatStringSetRegistry<Effect> equipmentEffects;

	private boolean endTurn;
	private final StatInt actionPoints;
	private final StatInt movePoints;
	private final StatBoolean canPerformActions;
	private final StatBoolean canMove;
	private final StatBoolean canDodge;

	private final Map<String, Integer> attributesBase;
	private final Map<String, Integer> skillsBase;
	private final Map<String, StatInt> attributes;
	private final Map<String, StatInt> skills;

	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final EquipmentComponent equipmentComponent;
	private final TargetingComponent targetingComponent;
	private final BehaviorComponent behaviorComponent;

	private final Map<String, List<Script>> scripts;
	private final Context defaultContext;

	public Actor(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher, ItemFactory itemFactory, Registry<SenseType> senseTypeRegistry, Registry<Effect> effectRegistry, Collection<DamageType> allDamageTypes, Collection<Attribute> allAttributes, Collection<Skill> allSkills, String ID, String nameDescriptor, Area area, ActorTemplate template, boolean isPlayer, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean isPlayerControlled) {
		super(ID);
		this.scriptRuntime = scriptRuntime;
        this.sensoryEventDispatcher = sensoryEventDispatcher;
		this.nameDescriptor = nameDescriptor;
		this.defaultArea = area;
		this.area = area;
		this.template = template;
		this.isPlayer = isPlayer;
		this.targetingComponent = new TargetingComponent(this);
		this.areaTargets = new HashSet<>();
		this.senseTypes = new StatStringSetRegistry<>("sense_types", this, senseTypeRegistry, SenseType::ID);
		this.startDead = startDead;
		this.isDead = startDead;
		this.maxHP = new StatInt("max_hp", this);
		this.actionPoints = new StatInt("action_points", this);
		this.movePoints = new StatInt("move_points", this);
		this.canPerformActions = new StatBoolean("can_perform_actions", this, false);
		this.canMove = new StatBoolean("can_move", this, false);
		this.canDodge = new StatBoolean("can_dodge", this, false);
		this.inventory = new Inventory(itemFactory, this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.equipmentEffects = new StatStringSetRegistry<>("equipment_effects", this, effectRegistry, Effect::getID);
		this.effectComponent = new EffectComponent(this, scriptRuntime, Context.builder().subject(this).build());
		this.behaviorComponent = new BehaviorComponent(this, behaviors);
		this.startDisabled = startDisabled;
		this.isPlayerControlled = isPlayerControlled;
		this.scripts = new HashMap<>();
		this.defaultContext = Context.builder().subject(this).build();
		this.damageResistance = new HashMap<>();
		this.damageMult = new HashMap<>();
		for (DamageType damageType : allDamageTypes) {
			String damageTypeID = damageType.ID();
			this.damageResistance.put(damageTypeID, new StatInt("damage_resist_" + damageTypeID, this));
			this.damageMult.put(damageTypeID, new StatFloat("damage_mult_" + damageTypeID, this));
		}
		this.attributesBase = new HashMap<>();
		this.attributes = new HashMap<>();
		for (Attribute attribute : allAttributes) {
			String attributeID = attribute.ID();
			this.attributesBase.put(attributeID, getTemplate().getAttribute(attributeID));
			this.attributes.put(attributeID, new StatInt("attribute_" + attributeID, this));
		}
		this.skillsBase = new HashMap<>();
		this.skills = new HashMap<>();
		for (Skill skill : allSkills) {
			String skillID = skill.ID();
			this.skillsBase.put(skillID, getTemplate().getSkill(skillID));
			this.skills.put(skillID, new StatInt("skill_" + skillID, this));
		}
		this.level = template.getStartingLevel();
		if (!startDead) {
			HP = maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP, Context.from(defaultContext).build());
		}
		setEnabled(!startDisabled);
	}

	public void generateInitialInventory(ItemFactory itemFactory, MutableRegistry<Item> itemMutableRegistry) {
		if (getTemplate().getLootTable() != null) {
			getTemplate().getLootTable().generateItems(inventory, itemFactory, itemMutableRegistry);
		}
	}

	public void applyStartingEffects() {
		for (Effect startingEffect : getTemplate().getStartingEffects()) {
			getEffectComponent().addEffect(startingEffect);
		}
	}

	private ActorTemplate getTemplate() {
		return template;
	}

	public boolean isPlayer() {
		return isPlayer;
	}
	
	@Override
	public String getName() {
		if (nameDescriptor != null) {
			return nameDescriptor + " " + getTemplate().getName();
		}
		return getTemplate().getName();
	}

	@Override
	public void setKnown() {
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		return isKnown;
	}
	
	@Override
	public boolean isProperName() {
		return getTemplate().isProperName();
	}

	@Override
	public int pluralCount() {
		return 1;
	}
	
	@Override
	public Pronoun getPronoun() {
		return getTemplate().getPronoun();
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		if (this.area != null) {
			this.area.removeActor(this);
		}
		this.area = area;
		area.addActor(this);
		if (isCarryingActor()) {
			getCarriedActor().setArea(area);
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean enable) {
		if (area == null) throw new UnsupportedOperationException("Attempt to enable actor in null area: " + this.getID());
		if (isEnabled != enable) {
			isEnabled = enable;
			if (enable) {
				area.addActor(this);
			} else {
				area.removeActor(this);
			}
		}
	}
	
	public int getAttribute(String attribute, Context context) {
		return attributes.get(attribute).value(attributesBase.get(attribute), ATTRIBUTE_MIN, ATTRIBUTE_MAX, context);
	}

	public int getAttributeBase(String attribute) {
		return attributesBase.get(attribute);
	}

	public void setAttributeBase(String attribute, int value) {
		if (!attributesBase.containsKey(attribute)) {
			DebugLogger.print("Actor " + this + " - attempted to set base attribute that does not exist: " + attribute);
		} else {
			attributesBase.put(attribute, value);
		}
	}

	public int getSkill(String skill, Context context) {
		return skills.get(skill).value(skillsBase.get(skill), SKILL_MIN, SKILL_MAX, context);
	}

	public int getSkillBase(String skill) {
		return skillsBase.get(skill);
	}

	public void setSkillBase(String skill, int value) {
		if (!skills.containsKey(skill)) {
			DebugLogger.print("Actor " + this + " - attempted to set base skill that does not exist: " + skill);
		} else {
			skillsBase.put(skill, value);
		}
	}
	
	public Scene getDialogueStart() {
		return getTemplate().getDialogueStart();
	}
	
	public Faction getFaction() {
		return getTemplate().getFaction();
	}

	public boolean canPerformActions(Context context) {
		return canPerformActions.value(true, context);
	}
	
	public boolean canMove(Context context) {
		if (isUsingObject()) {
			return false;
		}
		return canMove.value(true, context);
	}

	public boolean canPerformLocalActions() {
		if (isUsingObject()) {
			return getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanPerformLocalActions(getUsingObject().slot());
		}
		return true;
	}

	public boolean canDodge(Context context) {
		if (isInCover()) {
			return false;
		}
		return canDodge.value(true, context);
	}

	public boolean isInCover() {
		if (isUsingObject()) {
			return getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userIsInCover(getUsingObject().slot());
		}
		return false;
	}

	public boolean canBeCarried() {
		return isDead();
	}

	public void setCarriedActor(Actor carriedActor) {
		this.carriedActor = carriedActor;
	}

	public Actor getCarriedActor() {
		return carriedActor;
	}

	public boolean isCarryingActor() {
		return carriedActor != null;
	}

	public List<Limb> getLimbs() {
		return getTemplate().getLimbs();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public EquipmentComponent getEquipmentComponent() {
		return equipmentComponent;
	}

	public EffectComponent getEffectComponent() {
		return effectComponent;
	}

	@Override
	public void addEffect(Effect effect) {
		effectComponent.addEffect(effect);
	}

	@Override
	public void removeEffect(Effect effect) {
		effectComponent.removeEffect(effect);
	}

	public BehaviorComponent getBehaviorComponent() {
		return behaviorComponent;
	}

	public TargetingComponent getTargetingComponent() {
		return targetingComponent;
	}

	public Map<String, EquipSlot> getEquipSlots() {
		return getTemplate().getEquipSlots();
	}

	public int getMaxHP() {
		return maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP, Context.from(defaultContext).build());
	}

	public int getDamageResistance(String damageType, Context context) {
		return damageResistance.get(damageType).value(getTemplate().getDamageResistance(damageType), 0, MAX_DAMAGE_RESIST, context);
	}

	public float getDamageMult(String damageType, Context context) {
		return damageMult.get(damageType).value(getTemplate().getDamageMult(damageType), 0.0f, MAX_DAMAGE_MULT, context);
	}

	public int getActionPoints() {
		return actionPoints.value(getTemplate().getActionPoints(), 0, MAX_ACTION_POINTS, Context.from(defaultContext).build());
	}

	public int getMovePoints() {
		return movePoints.value(getTemplate().getMovePoints(), 0, MAX_MOVE_POINTS, Context.from(defaultContext).build());
	}

	public Set<Effect> getEquipmentEffects(Item item) {
		return equipmentEffects.valueObjects(new HashSet<>(), scriptRuntime, Context.from(defaultContext).parentItem(item).build());
	}

	@Override
	public boolean canBeAttacked() {
		return !isDead();
	}

	@Override
	public ComputedDamage applyEffectsAndComputeDamage(Damage damage, ScriptRuntime scriptRuntime, Context context) {
		for (Effect effect : damage.getTargetEffects()) {
			getEffectComponent().addEffect(effect);
		}
		int amount = damage.getAmount();
		int equipmentResistance;
		float equipmentMult;
		if (damage.getLimb() != null) {
			equipmentResistance = getEquipmentComponent().getDamageResistanceLimb(damage.getLimb().getID(), damage.getType().ID());
			equipmentMult = getEquipmentComponent().getDamageMultLimb(damage.getLimb().getID(), damage.getType().ID());
		} else {
			equipmentResistance = getEquipmentComponent().getDamageResistanceMain(damage.getType().ID());
			equipmentMult = getEquipmentComponent().getDamageMultMain(damage.getType().ID());
		}
		int actorResistance = getDamageResistance(damage.getType().ID(), context);
		float actorMult = getDamageMult(damage.getType().ID(), context);
		// TODO - Add additional armor mult for damage mults (part of the Damage object, affected by weapons/attacks/etc.)
		amount -= Math.round(equipmentResistance * damage.getArmorMult());
		amount -= Math.round(amount * equipmentMult);
		amount -= Math.round(actorResistance * damage.getArmorMult());
		amount -= Math.round(amount * actorMult);
		if (damage.getLimb() != null) {
			amount = Math.round(amount * damage.getLimb().getDamageMult());
		}
		if (amount < 0) amount = 0;
		boolean isKillingBlow = HP - amount <= 0;
		return new ComputedDamage(amount, damage.getLimb(), isKillingBlow);
	}

	@Override
	public void applyDamage(ComputedDamage computedDamage, ScriptRuntime scriptRuntime, Context context) {
		if (computedDamage.limb() != null && computedDamage.amount() > 0) {
			computedDamage.limb().applyEffects(this);
		}
		modifyHP(-computedDamage.amount(), context);
	}

	public void modifyHP(int amount, Context context) {
		HP += amount;
		if (HP <= 0) {
			Context killContext = Context.from(defaultContext).target(context.getSubject()).build();
			HP = 0;
			kill(killContext);
		} else if (amount < 0) {
			Context modifierContext = Context.from(defaultContext).target(context.getSubject()).build();
			modifierContext.setLocalVariable("amount", Expression.constant(String.valueOf(-amount)));
			modifierContext.setLocalVariable("condition", Expression.constant(getConditionDescription()));
			triggerScript("on_damaged", modifierContext);
			if (SHOW_HP_CHANGES) {
				sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), "$actor lose$s $amount HP.", modifierContext, true, null, null));
			}
			sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), "$actor $is $condition.", modifierContext, true, null, null));
		}
	}
	
	public void kill(Context context) {
		triggerScript("on_death", context);
		if (context.getTarget() != null && context.getTarget() != this) {
			context.getTarget().triggerScript("on_kill", Context.from(defaultContext).subject(context.getTarget()).target(context.getSubject()).build());
		}
		sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), Phrases.get("die"), context, true, null, null));
		stopUsingObjectOnDeathIfPresent();
		isDead = true;
		HP = 0;
	}

	public String getConditionDescription() {
		float hpProportion = ((float) HP) / ((float) getMaxHP());
		if (hpProportion == 1.0f) {
			return "in perfect condition";
		} else if (hpProportion >= 0.9f) {
			return "barely scratched";
		} else if (hpProportion >= 0.75f) {
			return "lightly injured";
		} else if (hpProportion >= 0.55f) {
			return "moderately injured";
		} else if (hpProportion >= 0.35f) {
			return "heavily injured";
		} else if (hpProportion >= 0.15f) {
			return "dangerously injured";
		} else {
			return "clinging to life";
		}
	}
	
	public boolean isDead() {
		return isDead;
	}

	public boolean isSleeping() {
		return isSleeping;
	}

	public boolean isActive() {
		return !isDead() && !isSleeping();
	}

	public void startSleep(int minutes) {
		sleepCounter = DateTimeController.minutesToRounds(minutes);
		isSleeping = true;
	}

	public void updateSleep() {
		if (sleepCounter != 0) {
			sleepCounter -= 1;
			if (sleepCounter <= 0) {
				isSleeping = false;
				sleepCounter = 0;
			}
		}
	}

	public boolean isSneaking() {
		if (isUsingObject()) {
			return false;
		}
		return isSneaking;
	}

	public void setSneaking(boolean sneaking) {
		this.isSneaking = sneaking;
	}
	
	public void setUsingObject(ObjectComponentUsable.ObjectUserData objectUserData) {
		this.usingObject = objectUserData;
	}

	public ObjectComponentUsable.ObjectUserData getUsingObject() {
		return usingObject;
	}
	
	public boolean isUsingObject() {
		return this.usingObject != null;
	}

	private void stopUsingObjectOnDeathIfPresent() {
		if (!isUsingObject()) return;
		WorldObject object = getUsingObject().object();
		ObjectComponentUsable usableComponent = object.getComponentOfType(ObjectComponentUsable.class);
		String usingSlot = getUsingObject().slot();
		if (usableComponent.shouldRemoveUserOnDeath(usingSlot)) {
			usableComponent.removeUser(usingSlot);
			setUsingObject(null);
			Context context = Context.builder().subject(this).target(this).parentObject(object).build();
			sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), Phrases.get(usableComponent.getEndDeathPhrase(usingSlot)), context, true, null, null));
		}
	}
	
	public boolean isInCombat() {
		return targetingComponent.hasTargetsOfType(TargetingComponent.DetectionState.HOSTILE);
	}
	
	public void addPursueTarget(AreaTarget target) {
		areaTargets.add(target);
	}
	
	public Set<AreaTarget> getPursueTargets() {
		return areaTargets;
	}

	@Override
	public List<Action> localActions(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		List<Action> actions = new ArrayList<>();
		if (isActive()) {
			if (getTemplate().getDialogueStart() != null) {
				actions.add(new ActionTalk(scriptRuntime, sensoryEventDispatcher, this));
			}
		} else if (isDead()) {
			actions.addAll(inventory.getExternalActions(scriptRuntime, sensoryEventDispatcher, this, subject, "Take", "takeFrom", null, null, true, false));
			actions.add(new ActionCarryActorStart(scriptRuntime, sensoryEventDispatcher, this));
		}
		for (ActionCustom.CustomActionHolder actionHolder : getTemplate().getCustomActions()) {
			ActionTemplate customActionTemplate = actionHolder.action();
			actions.add(new ActionCustom(scriptRuntime, sensoryEventDispatcher, this, null, null, null, customActionTemplate, actionHolder.parameters(), new MenuDataActor(this), false));
		}
		for (ActionCustom.CustomActionHolder inventoryActionHolder : getTemplate().getCustomInventoryActions()) {
			for (Item item : inventory.getItems()) {
				ActionTemplate customInventoryActionTemplate = inventoryActionHolder.action();
				actions.add(new ActionCustom(scriptRuntime, sensoryEventDispatcher, this, null, item, null, customInventoryActionTemplate, inventoryActionHolder.parameters(), new MenuDataActorInventory(this, item, false, false), false));
			}
		}
		return actions;
	}

	public List<Action> carriedActions(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionCarryActorEnd(scriptRuntime, sensoryEventDispatcher, this));
		return actions;
	}

	@Override
	public List<Action> visibleActions(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		return new ArrayList<>();
	}

	public List<Action> availableActions(Pathfinder pathfinder) {
		if (!canPerformActions(Context.from(defaultContext).build())) {
			return new ArrayList<>();
		}
		List<Action> actions = new ArrayList<>();
		if (canPerformLocalActions()) {
			for (Actor actor : getArea().getActors()) {
				actions.addAll(actor.localActions(this, scriptRuntime, sensoryEventDispatcher));
			}
			actions.addAll(getArea().getItemActions(scriptRuntime, sensoryEventDispatcher));
			for (WorldObject object : getArea().getObjects()) {
				if (!object.isHidden() && (!isUsingObject() || !object.equals(getUsingObject().object()))) {
					actions.addAll(object.localActions(this, scriptRuntime, sensoryEventDispatcher));
				}
			}
		}
		if (isUsingObject()) {
			actions.addAll(getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).getUsingActions(getUsingObject().slot(), this, scriptRuntime));
			if (getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanPerformParentActions(getUsingObject().slot())) {
				actions.addAll(getUsingObject().object().localActions(this, scriptRuntime, sensoryEventDispatcher));
			}
		}
		if (isCarryingActor()) {
			actions.addAll(getCarriedActor().carriedActions(this, scriptRuntime, sensoryEventDispatcher));
		}
		for (Actor visibleActor : getLineOfSightActors(pathfinder)) {
			if (visibleActor.isVisible(this)) {
				actions.addAll(visibleActor.visibleActions(this, scriptRuntime, sensoryEventDispatcher));
			}
		}
		for (WorldObject visibleObject : getLineOfSightObjects(pathfinder)) {
			if (visibleObject.isVisible(this)) {
				actions.addAll(visibleObject.visibleActions(this, scriptRuntime, sensoryEventDispatcher));
			}
		}
		if (canMove(Context.from(defaultContext).build())) {
			actions.addAll(getArea().getMoveActions(scriptRuntime, sensoryEventDispatcher, this, null, null));
		}
		actions.addAll(getArea().getAreaActions(this));
		for (Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(this, scriptRuntime));
		}
		actions.addAll(equipmentComponent.getEquippedActions(scriptRuntime));
		for (WeaponAttackType unarmedAttackType : getTemplate().getUnarmedAttackTypes()) {
			actions.addAll(unarmedAttackType.generateActions(this, null, scriptRuntime));
		}
		if (isSneaking()) {
			actions.add(new ActionSneakEnd(scriptRuntime, sensoryEventDispatcher));
		} else {
			actions.add(new ActionSneakStart(scriptRuntime, sensoryEventDispatcher));
		}
		actions.add(new ActionEnd(scriptRuntime, sensoryEventDispatcher));
		return actions;
	}

	public boolean isPlayerControlled() {
		return isPlayerControlled;
	}
	
	public void endTurn() {
		endTurn = true;
		if (!isPlayerControlled() && shouldIdle()) {
			playIdle();
		}
	}

	public boolean isTurnEnded() {
		return endTurn;
	}

	public void setTurnActive() {
		endTurn = false;
	}

	private void playIdle() {
		if (getBehaviorComponent() != null) {
			Idle idle = getBehaviorComponent().getIdle(scriptRuntime);
			if (idle != null) {
				idle.trigger(this);
			}
		}
	}

	private boolean shouldIdle() {
		return !isInCombat();
	}
	
	public void updateAreaTargets() {
		Iterator<AreaTarget> itr = areaTargets.iterator();
		while (itr.hasNext()) {
			AreaTarget target = itr.next();
			target.update(this);
			if (target.shouldRemove()) {
				itr.remove();
			}
		}
	}

	public Map<Area, Pathfinder.VisibleAreaData> getVisibleAreas(Pathfinder pathfinder) {
		if (isUsingObject() && !getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanSeeOtherAreas(getUsingObject().slot())) {
			return Map.of(getArea(), new Pathfinder.VisibleAreaData(null, Area.pathLengthToDistance(0), List.of(getArea())));
		} else {
			return pathfinder.getVisibleAreas(getArea(), this);
		}
	}

	public boolean isVisible(Actor subject) {
        return !(isInCover() && !getArea().equals(subject.getArea()));
    }

	public Set<Actor> getLineOfSightActors(Pathfinder pathfinder) {
		Set<Actor> visibleActors = new HashSet<>();
		for (Area visibleArea : getVisibleAreas(pathfinder).keySet()) {
			for (Actor actor : visibleArea.getActors()) {
				if (!actor.equals(this) && !actor.isInCover()) {
					visibleActors.add(actor);
				}
			}
		}
		return visibleActors;
	}

	public Set<WorldObject> getLineOfSightObjects(Pathfinder pathfinder) {
		Set<WorldObject> visibleObjects = new HashSet<>();
		for (Area visibleArea : getVisibleAreas(pathfinder).keySet()) {
			for (WorldObject object : visibleArea.getObjects()) {
				if (!object.isHidden()) {
					visibleObjects.add(object);
				}
			}
		}
		return visibleObjects;
	}

	public Set<AttackTarget> getLineOfSightAttackTargets(Pathfinder pathfinder) {
		Set<AttackTarget> attackTargets = new HashSet<>(getLineOfSightActors(pathfinder));
		attackTargets.addAll(getLineOfSightObjects(pathfinder));
		return attackTargets;
	}

	public Set<SenseType> getSenseTypes() {
		Context context = Context.from(defaultContext).build();
		return senseTypes.valueObjects(getTemplate().getSenseTypes(), scriptRuntime, context);
	}

	public Set<ObstructionType> getAllBypassedObstructionTypes() {
		Set<ObstructionType> bypassedTypes = new HashSet<>();
		for (SenseType senseType : getSenseTypes()) {
			bypassedTypes.addAll(senseType.bypassedObstructionTypes());
		}
		return bypassedTypes;
	}

	@Override
	public StatInt getStatInt(String name) {
		if (name.startsWith("damage_resist_")) {
			String damageType = name.substring("damage_resist_".length());
			return damageResistance.get(damageType);
		} else if (name.startsWith("attribute_")) {
			String attribute = name.substring("attribute_".length());
			return attributes.get(attribute);
		} else if (name.startsWith("skill_")) {
			String skill = name.substring("skill_".length());
			return skills.get(skill);
		}
		return switch (name) {
			case "max_hp" -> maxHP;
			case "action_points" -> actionPoints;
			case "move_points" -> movePoints;
			default -> null;
		};
	}

	@Override
	public StatFloat getStatFloat(String name) {
		if (name.startsWith("damage_mult_")) {
			String damageType = name.substring("damage_mult_".length());
			return damageMult.get(damageType);
		}
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		if ("can_perform_actions".equals(name)) {
			return canPerformActions;
		} else if ("can_move".equals(name)) {
			return canMove;
		} else if ("can_dodge".equals(name)) {
			return canDodge;
		}
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		if ("equipment_effects".equals(name)) {
			return equipmentEffects;
		} else if ("sense_types".equals(name)) {
			return senseTypes;
		}
		return null;
	}

	// TODO - Do not pass script Context to evaluate Stat objects (use default context instead)
	@Override
	public Expression getStatValue(String name, Context context) {
		if (name.startsWith("damage_resist_")) {
			return resolveExpressionPrefix(name, "damage_resist_", damageResistance, "damage type", Expression.constant(0), key -> Expression.constant(getDamageResistance(key, context)));
		} else if (name.startsWith("attribute_")) {
			return resolveExpressionPrefix(name, "attribute_", attributes, "attribute", Expression.constant(0), key -> Expression.constant(getAttribute(key, context)));
		} else if (name.startsWith("skill_")) {
			return resolveExpressionPrefix(name, "skill_", skills, "skill", Expression.constant(0), key -> Expression.constant(getSkill(key, context)));
		} else if (name.startsWith("damage_mult_")) {
			return resolveExpressionPrefix(name, "damage_mult_", damageMult, "damage type", Expression.constant(0.0f), key -> Expression.constant(getDamageMult(key, context)));
		} else if (name.startsWith("has_equipped_")) {
			return resolveExpressionPrefix(name, "has_equipped_", getTemplate().getEquipSlots(), "equip slot", Expression.constant(false), key -> Expression.constant(getEquipmentComponent().getEquippedItemInSlot(key) != null));
		}
		return switch (name) {
			case "inventory" -> (getInventory() == null ? null : Expression.constant(getInventory()));
			case "noun" -> Expression.constantNoun(this);
			case "level" -> Expression.constant(level);
			case "xp" -> Expression.constant(XP);
			case "max_hp" -> Expression.constant(getMaxHP());
			case "hp" -> Expression.constant(HP);
			case "action_points" -> Expression.constant(getActionPoints());
			case "move_points" -> Expression.constant(getMovePoints());
			case "enabled" -> Expression.constant(isEnabled);
			case "sleeping" -> Expression.constant(isSleeping);
			case "in_combat" -> Expression.constant(isInCombat());
			case "using_object" -> Expression.constant(isUsingObject());
			case "in_cover" -> Expression.constant(isInCover());
			case "dead" -> Expression.constant(isDead);
			case "active" -> Expression.constant(isActive());
			case "can_perform_actions" -> Expression.constant(canPerformActions(context));
			case "can_move" -> Expression.constant(canMove(context));
			case "can_dodge" -> Expression.constant(canDodge(context));
			case "id" -> Expression.constant(getID());
			case "template_id" -> Expression.constant(template.getID());
			case "area" -> Expression.constant(getArea().getID());
			case "room" -> Expression.constant(getArea().getRoom() != null ? getArea().getRoom().getID() : null);
			case "equipment_effects" -> Expression.constant(equipmentEffects.value(new HashSet<>(), scriptRuntime, context));
			case "sense_types" -> Expression.constant(getSenseTypes());
			default -> null;
		};
	}

	@Override
	public void onStatChange(String name) {
		if ("max_hp".equals(name) && HP > getMaxHP()) {
			HP = getMaxHP();
		}
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		if (name.startsWith("equip_slot_block_")) {
			for (String slot : getEquipSlots().keySet()) {
				if (name.equals("equip_slot_block_" + slot)) {
					getEquipmentComponent().setSlotBlocked(slot, value.getValueBoolean());
					return true;
				}
			}
			DebugLogger.print("Actor " + this + " - setStatValue " + name + " references an invalid equip slot");
			return false;
		}
		switch (name) {
			case "known" -> {
				isKnown = value.getValueBoolean();
				return true;
			}
			case "enabled" -> {
				setEnabled(value.getValueBoolean());
				return true;
			}
			case "player_controlled" -> {
				isPlayerControlled = value.getValueBoolean();
				return true;
			}
			case "level" -> {
				level = value.getValueInteger();
				return true;
			}
			case "xp" -> {
				XP = value.getValueInteger();
				evaluateXPChange();
				return true;
			}
			case "hp" -> {
				HP = MathUtils.bound(value.getValueInteger(), 0, getMaxHP());
				return true;
			}
			case "alert_state" -> {
				targetingComponent.setAlertState(LoadUtils.stringToEnum(value.getValueString(), TargetingComponent.AlertState.class));
				return true;
			}
		}
		return false;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return switch (name) {
			case "equipped_item" -> equipmentComponent.getEquippedItemInSlot(ID);
			case "using_object" -> getUsingObject().object();
			case "area" -> getArea();
			default -> null;
		};
	}

	public void addScript(String trigger, Script script) {
		if (!scripts.containsKey(trigger)) {
			scripts.put(trigger, new ArrayList<>());
		}
		scripts.get(trigger).add(script);
	}

	public void removeScript(String trigger, Script script) {
		if (scripts.containsKey(trigger)) {
			scripts.get(trigger).remove(script);
		}
		if (scripts.get(trigger).isEmpty()) {
			scripts.remove(trigger);
		}
	}

	public void triggerScript(String trigger, Context context) {
		for (Script currentScript : getTemplate().getScripts(trigger)) {
			currentScript.run(scriptRuntime, context);
		}
		if (scripts.containsKey(trigger)) {
			for (Script currentScript : scripts.get(trigger)) {
				currentScript.run(scriptRuntime, context);
			}
		}
	}

	public void triggerBark(String trigger, Context context) {
		if (isActive()) {
			Bark bark = getBark(trigger);
			if (bark != null) {
				bark.trigger(context, sensoryEventDispatcher);
			}
		}
	}

	public Bark getBark(String trigger) {
		return getTemplate().getBark(trigger);
	}

	@Override
	public void onRemoveItemFromInventory(Item item) {
		if (item.hasComponentOfType(ItemComponentEquippable.class)) {
			getEquipmentComponent().unequip(item);
		}
	}

	private Expression resolveExpressionPrefix(String name, String prefix, Map<String, ?> map, String keyType, Expression fallback, Function<String, Expression> resolver) {
		String key = name.substring(prefix.length());
		if (!map.containsKey(key)) {
			DebugLogger.print("Actor " + this + " - getStatValue " + name + " references an invalid " + keyType);
			return fallback;
		}
		return resolver.apply(key);
	}

	private void evaluateXPChange() {
		Context levelUpThresholdContext = Context.from(defaultContext).addVariable("level", Expression.constant(level)).build();
		Expression thresholdReturnValue = getTemplate().getLevelUpThresholdExpression().run(scriptRuntime, levelUpThresholdContext);
		int levelUpXP = thresholdReturnValue.getValueInteger();
		if (XP >= levelUpXP) {
			XP -= levelUpXP;
			level += 1;
			onLevelUp();
		}
	}

	private void onLevelUp() {
		triggerScript("on_level_up", Context.from(defaultContext).build());
	}

}
