package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.actor.component.EquipmentComponent;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.EffectComponent;
import com.github.finley243.adventureengine.effect.Effectable;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.InventoryOwner;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.component.EquippableItemComponent;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.UsableObjectComponent;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

import java.util.*;
import java.util.function.Function;

public class Actor extends GameInstanced implements Noun, Physical, ScriptValueHolder, StatHolder, AttackTarget, Effectable, InventoryOwner {

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

	private final StringSetRegistryStat<SenseType> senseTypes;
	private final Set<AreaTarget> areaTargets;

	private int level;
	private int XP;

	private final IntStat maxHP;
	private int HP;
	private final boolean startDead;
	private boolean isDead;
	private final Map<String, IntStat> damageResistance;
	private final Map<String, FloatStat> damageMult;

	private boolean isSleeping;
	private int sleepCounter;
	private boolean isSneaking;
	private Actor carriedActor;
	private UsableObjectComponent.ObjectUserData usingObject;

	private final StringSetRegistryStat<Effect> equipmentEffects;

	private boolean endTurn;
	private final IntStat actionPoints;
	private final IntStat movePoints;
	private final BooleanStat canPerformActions;
	private final BooleanStat canMove;
	private final BooleanStat canDodge;

	private final Map<String, Integer> attributesBase;
	private final Map<String, Integer> skillsBase;
	private final Map<String, IntStat> attributes;
	private final Map<String, IntStat> skills;

	private final List<Behavior> behaviors;

	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final EquipmentComponent equipmentComponent;
	private final TargetingComponent targetingComponent;

	private final Map<String, List<Script>> scripts;
	private final Context defaultContext;

	public Actor(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher, ItemFactory itemFactory, Pathfinder pathfinder, Registry<SenseType> senseTypeRegistry, Registry<Effect> effectRegistry, Collection<DamageType> allDamageTypes, Collection<Attribute> allAttributes, Collection<Skill> allSkills, String ID, String nameDescriptor, Area area, ActorTemplate template, boolean isPlayer, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean isPlayerControlled) {
		super(ID);
		this.scriptRuntime = scriptRuntime;
        this.sensoryEventDispatcher = sensoryEventDispatcher;
		this.nameDescriptor = nameDescriptor;
		this.defaultArea = area;
		this.area = area;
		this.template = template;
		this.isPlayer = isPlayer;
		this.areaTargets = new HashSet<>();
		this.senseTypes = new StringSetRegistryStat<>("sense_types", this, scriptRuntime, senseTypeRegistry, SenseType::ID);
		this.startDead = startDead;
		this.isDead = startDead;
		this.maxHP = new IntStat("max_hp", this, scriptRuntime);
		this.actionPoints = new IntStat("action_points", this, scriptRuntime);
		this.movePoints = new IntStat("move_points", this, scriptRuntime);
		this.canPerformActions = new BooleanStat("can_perform_actions", this, scriptRuntime, false);
		this.canMove = new BooleanStat("can_move", this, scriptRuntime, false);
		this.canDodge = new BooleanStat("can_dodge", this, scriptRuntime, false);
		this.inventory = new Inventory(itemFactory, this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.targetingComponent = new TargetingComponent(pathfinder, this);
		this.equipmentEffects = new StringSetRegistryStat<>("equipment_effects", this, scriptRuntime, effectRegistry, Effect::getID);
		this.effectComponent = new EffectComponent(this, scriptRuntime, Context.builder().subject(this).build());
		this.behaviors = behaviors;
		this.startDisabled = startDisabled;
		this.isPlayerControlled = isPlayerControlled;
		this.scripts = new HashMap<>();
		this.defaultContext = Context.builder().subject(this).build();
		this.damageResistance = new HashMap<>();
		this.damageMult = new HashMap<>();
		for (DamageType damageType : allDamageTypes) {
			String damageTypeID = damageType.ID();
			this.damageResistance.put(damageTypeID, new IntStat("damage_resist_" + damageTypeID, this, scriptRuntime));
			this.damageMult.put(damageTypeID, new FloatStat("damage_mult_" + damageTypeID, this, scriptRuntime));
		}
		this.attributesBase = new HashMap<>();
		this.attributes = new HashMap<>();
		for (Attribute attribute : allAttributes) {
			String attributeID = attribute.ID();
			this.attributesBase.put(attributeID, getTemplate().getAttribute(attributeID));
			this.attributes.put(attributeID, new IntStat("attribute_" + attributeID, this, scriptRuntime));
		}
		this.skillsBase = new HashMap<>();
		this.skills = new HashMap<>();
		for (Skill skill : allSkills) {
			String skillID = skill.ID();
			this.skillsBase.put(skillID, getTemplate().getSkill(skillID));
			this.skills.put(skillID, new IntStat("skill_" + skillID, this, scriptRuntime));
		}
		this.level = template.getStartingLevel();
		if (!startDead) {
			HP = maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP, Context.from(defaultContext).build());
		}
	}

	public void resolveBehaviorReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
		if (behaviors == null) return;
		for (Behavior behavior : behaviors) {
			behavior.resolveReferences(areaRegistry, objectRegistry, actorRegistry);
		}
	}

	public void setInitialEnabledState() {
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
			return getUsingObject().object().getComponentOfType(UsableObjectComponent.class).userCanPerformLocalActions(getUsingObject().slot());
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
			return getUsingObject().object().getComponentOfType(UsableObjectComponent.class).userIsInCover(getUsingObject().slot());
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

	public TargetingComponent getTargetingComponent() {
		return targetingComponent;
	}

	@Override
	public void addEffect(Effect effect) {
		effectComponent.addEffect(effect);
	}

	@Override
	public void removeEffect(Effect effect) {
		effectComponent.removeEffect(effect);
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
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
		return equipmentEffects.valueObjects(new HashSet<>(), Context.from(defaultContext).parentItem(item).build());
	}

	@Override
	public boolean canBeAttacked() {
		return !isDead();
	}

	@Override
	public ComputedDamage applyEffectsAndComputeDamage(Damage damage, ScriptRuntime scriptRuntime, Context context) {
		for (Effect effect : damage.targetEffects()) {
			getEffectComponent().addEffect(effect);
		}
		int amount = damage.amount();
		int equipmentResistance;
		float equipmentMult;
		if (damage.limb() != null) {
			equipmentResistance = getEquipmentComponent().getDamageResistanceLimb(damage.limb().getID(), damage.type().ID());
			equipmentMult = getEquipmentComponent().getDamageMultLimb(damage.limb().getID(), damage.type().ID());
		} else {
			equipmentResistance = getEquipmentComponent().getDamageResistanceMain(damage.type().ID());
			equipmentMult = getEquipmentComponent().getDamageMultMain(damage.type().ID());
		}
		int actorResistance = getDamageResistance(damage.type().ID(), context);
		float actorMult = getDamageMult(damage.type().ID(), context);
		// TODO - Add additional armor mult for damage mults (part of the Damage object, affected by weapons/attacks/etc.)
		amount -= Math.round(equipmentResistance * damage.armorMult());
		amount -= Math.round(amount * equipmentMult);
		amount -= Math.round(actorResistance * damage.armorMult());
		amount -= Math.round(amount * actorMult);
		if (damage.limb() != null) {
			amount = Math.round(amount * damage.limb().getDamageMult());
		}
		if (amount < 0) amount = 0;
		boolean isKillingBlow = HP - amount <= 0;
		return new ComputedDamage(amount, damage.limb(), isKillingBlow);
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
			modifierContext.setLocalVariable("amount", Expression.integer(-amount));
			modifierContext.setLocalVariable("condition", Expression.string(getConditionDescription()));
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
		sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), "@die", context, true, null, null));
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
	
	public void setUsingObject(UsableObjectComponent.ObjectUserData objectUserData) {
		this.usingObject = objectUserData;
	}

	public UsableObjectComponent.ObjectUserData getUsingObject() {
		return usingObject;
	}
	
	public boolean isUsingObject() {
		return this.usingObject != null;
	}

	private void stopUsingObjectOnDeathIfPresent() {
		if (!isUsingObject()) return;
		WorldObject object = getUsingObject().object();
		UsableObjectComponent usableComponent = object.getComponentOfType(UsableObjectComponent.class);
		String usingSlot = getUsingObject().slot();
		if (usableComponent.shouldRemoveUserOnDeath(usingSlot)) {
			usableComponent.removeUser(usingSlot);
			setUsingObject(null);
			Context context = Context.builder().subject(this).target(this).parentObject(object).build();
			sensoryEventDispatcher.dispatch(new SensoryEvent(getArea(), usableComponent.getEndDeathPhrase(usingSlot), context, true, null, null));
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
	public List<Action> localActions(Actor subject, ActionDependencies dependencies) {
		List<Action> actions = new ArrayList<>();
		if (isActive()) {
			if (getTemplate().getDialogueStart() != null) {
				actions.add(new ActionTalk(subject, dependencies, this));
			}
		} else if (isDead()) {
			actions.addAll(inventory.getExternalActions(subject, dependencies, this, "Take", "takeFrom", null, null, true, false));
			actions.add(new ActionCarryActorStart(subject, dependencies, this));
		}
		for (ActionCustom.CustomActionHolder actionHolder : getTemplate().getCustomActions()) {
			ActionTemplate customActionTemplate = actionHolder.action();
			actions.add(new ActionCustom(subject, dependencies, this, null, null, null, customActionTemplate, actionHolder.parameters(), new MenuDataActor(this), false));
		}
		for (ActionCustom.CustomActionHolder inventoryActionHolder : getTemplate().getCustomInventoryActions()) {
			for (Item item : inventory.getItems()) {
				ActionTemplate customInventoryActionTemplate = inventoryActionHolder.action();
				actions.add(new ActionCustom(subject, dependencies, this, null, item, null, customInventoryActionTemplate, inventoryActionHolder.parameters(), new MenuDataActorInventory(this, item, false, false), false));
			}
		}
		return actions;
	}

	public List<Action> carriedActions(Actor subject, ActionDependencies dependencies) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionCarryActorEnd(subject, dependencies, this));
		return actions;
	}

	@Override
	public List<Action> visibleActions(Actor subject, ActionDependencies dependencies) {
		return new ArrayList<>();
	}

	public List<Action> availableActions(ActionDependencies dependencies, Pathfinder pathfinder, Runnable onEndTurnAction) {
		if (!canPerformActions(Context.from(defaultContext).build())) {
			return new ArrayList<>();
		}
		List<Action> actions = new ArrayList<>();
		if (canPerformLocalActions()) {
			for (Actor actor : getArea().getActors()) {
				actions.addAll(actor.localActions(this, dependencies));
			}
			actions.addAll(getArea().getItemActions(this, dependencies));
			for (WorldObject object : getArea().getObjects()) {
				if (!object.isHidden() && (!isUsingObject() || !object.equals(getUsingObject().object()))) {
					actions.addAll(object.localActions(this, dependencies));
				}
			}
		}
		if (isUsingObject()) {
			actions.addAll(getUsingObject().object().getComponentOfType(UsableObjectComponent.class).getUsingActions(getUsingObject().slot(), this, dependencies));
			if (getUsingObject().object().getComponentOfType(UsableObjectComponent.class).userCanPerformParentActions(getUsingObject().slot())) {
				actions.addAll(getUsingObject().object().localActions(this, dependencies));
			}
		}
		if (isCarryingActor()) {
			actions.addAll(getCarriedActor().carriedActions(this, dependencies));
		}
		for (Actor visibleActor : getLineOfSightActors(pathfinder)) {
			if (visibleActor.isVisible(this)) {
				actions.addAll(visibleActor.visibleActions(this, dependencies));
			}
		}
		for (WorldObject visibleObject : getLineOfSightObjects(pathfinder)) {
			if (visibleObject.isVisible(this)) {
				actions.addAll(visibleObject.visibleActions(this, dependencies));
			}
		}
		if (canMove(Context.from(defaultContext).build())) {
			actions.addAll(getArea().getMoveActions(this, dependencies, null, null));
		}
		actions.addAll(getArea().getAreaActions(this, dependencies));
		for (Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(this, dependencies));
		}
		actions.addAll(equipmentComponent.getEquippedActions(dependencies));
		for (AttackType unarmedAttackType : getTemplate().getUnarmedAttackTypes()) {
			actions.addAll(unarmedAttackType.generateActions(this, dependencies, null));
		}
		if (isSneaking()) {
			actions.add(new ActionSneakEnd(this, dependencies));
		} else {
			actions.add(new ActionSneakStart(this, dependencies));
		}
		actions.add(new ActionEnd(this, dependencies, onEndTurnAction));
		return actions;
	}

	public boolean isPlayerControlled() {
		return isPlayerControlled;
	}
	
	public void endTurn() {
		endTurn = true;
	}

	public boolean isTurnEnded() {
		return endTurn;
	}

	public void setTurnActive() {
		endTurn = false;
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
		if (isUsingObject() && !getUsingObject().object().getComponentOfType(UsableObjectComponent.class).userCanSeeOtherAreas(getUsingObject().slot())) {
			return Map.of(getArea(), new Pathfinder.VisibleAreaData(null, Area.pathLengthToDistance(0), List.of(getArea())));
		} else {
			return pathfinder.getVisibleAreas(getArea(), this);
		}
	}

	public boolean isVisible(Actor subject) {
		if (isUsingObject()) {
			return getUsingObject().object().getComponentOfType(UsableObjectComponent.class).userCanSeeOtherAreas(getUsingObject().slot()) || getArea().equals(subject.getArea());
		}
        return true;
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

	public Set<SenseType> getSenseTypes() {
		Context context = Context.from(defaultContext).build();
		return senseTypes.valueObjects(getTemplate().getSenseTypes(), context);
	}

	public Set<ObstructionType> getAllBypassedObstructionTypes() {
		Set<ObstructionType> bypassedTypes = new HashSet<>();
		for (SenseType senseType : getSenseTypes()) {
			bypassedTypes.addAll(senseType.bypassedObstructionTypes());
		}
		return bypassedTypes;
	}

	@Override
	public Stat getStat(String name) {
		if (name.startsWith("damage_resist_")) {
			String damageType = name.substring("damage_resist_".length());
			return damageResistance.get(damageType);
		} else if (name.startsWith("attribute_")) {
			String attribute = name.substring("attribute_".length());
			return attributes.get(attribute);
		} else if (name.startsWith("skill_")) {
			String skill = name.substring("skill_".length());
			return skills.get(skill);
		} else if (name.startsWith("damage_mult_")) {
			String damageType = name.substring("damage_mult_".length());
			return damageMult.get(damageType);
		}
		return switch (name) {
			case "max_hp" -> maxHP;
			case "action_points" -> actionPoints;
			case "move_points" -> movePoints;
			case "can_perform_actions" -> canPerformActions;
			case "can_move" -> canMove;
			case "can_dodge" -> canDodge;
			case "sense_types" -> senseTypes;
			case "equipment_effects" -> equipmentEffects;
			default -> null;
		};
	}

	@Override
	public void onStatChange(String name) {
		if ("max_hp".equals(name) && HP > getMaxHP()) {
			HP = getMaxHP();
		}
	}

	// TODO - Do not pass script Context to evaluate Stat objects (use default context instead)
	@Override
	public Expression getScriptValue(String name, Context context) {
		if (name.startsWith("damage_resist_")) {
			return resolveExpressionPrefix(name, "damage_resist_", damageResistance, "damage type", Expression.integer(0), key -> Expression.integer(getDamageResistance(key, context)));
		} else if (name.startsWith("attribute_")) {
			return resolveExpressionPrefix(name, "attribute_", attributes, "attribute", Expression.integer(0), key -> Expression.integer(getAttribute(key, context)));
		} else if (name.startsWith("skill_")) {
			return resolveExpressionPrefix(name, "skill_", skills, "skill", Expression.integer(0), key -> Expression.integer(getSkill(key, context)));
		} else if (name.startsWith("damage_mult_")) {
			return resolveExpressionPrefix(name, "damage_mult_", damageMult, "damage type", Expression.decimal(0.0f), key -> Expression.decimal(getDamageMult(key, context)));
		} else if (name.startsWith("has_equipped_")) {
			return resolveExpressionPrefix(name, "has_equipped_", getTemplate().getEquipSlots(), "equip slot", Expression.bool(false), key -> Expression.bool(getEquipmentComponent().getEquippedItemInSlot(key) != null));
		}
		return switch (name) {
			case "inventory" -> (getInventory() == null ? null : Expression.inventory(getInventory()));
			case "noun" -> Expression.noun(this);
			case "level" -> Expression.integer(level);
			case "xp" -> Expression.integer(XP);
			case "max_hp" -> Expression.integer(getMaxHP());
			case "hp" -> Expression.integer(HP);
			case "action_points" -> Expression.integer(getActionPoints());
			case "move_points" -> Expression.integer(getMovePoints());
			case "enabled" -> Expression.bool(isEnabled);
			case "sleeping" -> Expression.bool(isSleeping);
			case "in_combat" -> Expression.bool(isInCombat());
			case "using_object" -> Expression.bool(isUsingObject());
			case "in_cover" -> Expression.bool(isInCover());
			case "dead" -> Expression.bool(isDead);
			case "active" -> Expression.bool(isActive());
			case "can_perform_actions" -> Expression.bool(canPerformActions(context));
			case "can_move" -> Expression.bool(canMove(context));
			case "can_dodge" -> Expression.bool(canDodge(context));
			case "id" -> Expression.string(getID());
			case "template_id" -> Expression.string(template.getID());
			case "area" -> Expression.string(getArea().getID());
			case "room" -> Expression.string(getArea().getRoom() != null ? getArea().getRoom().getID() : null);
			case "equipment_effects" -> Expression.set(equipmentEffects.value(new HashSet<>(), context), Expression::string);
			case "sense_types" -> Expression.set(getSenseTypes(), e -> Expression.string(e.ID()));
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
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
	public ScriptValueHolder getSubHolder(String name, String ID) {
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
		if (item.hasComponentOfType(EquippableItemComponent.class)) {
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
		Context levelUpThresholdContext = Context.from(defaultContext).addVariable("level", Expression.integer(level)).build();
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
