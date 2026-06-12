package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.actor.component.*;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.Effectible;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.InventoryOwner;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.component.ItemComponentEquippable;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

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
	private final UIEventBus eventBus;
	private final MenuManager menuManager;
	private final QuestManager questManager;

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
	private int actionPointsUsed;
	private final Map<Action, Integer> repeatActions;
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

	public Actor(ScriptRuntime scriptRuntime, UIEventBus eventBus, MenuManager menuManager, QuestManager questManager, Registry<SenseType> senseTypeRegistry, Registry<Effect> effectRegistry, Collection<DamageType> allDamageTypes, Collection<Attribute> allAttributes, Collection<Skill> allSkills, String ID, String nameDescriptor, Area area, ActorTemplate template, boolean isPlayer, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean isPlayerControlled) {
		super(ID);
		this.scriptRuntime = scriptRuntime;
		this.eventBus = eventBus;
		this.menuManager = menuManager;
		this.questManager = questManager;
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
		this.inventory = new Inventory(this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.equipmentEffects = new StatStringSetRegistry<>("equipment_effects", this, effectRegistry, Effect::getID);
		this.effectComponent = new EffectComponent(this, scriptRuntime, Context.builder().subject(this).build());
		this.behaviorComponent = new BehaviorComponent(this, behaviors);
		this.repeatActions = new HashMap<>();
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
	public void setArea(Area area, Game game) {
		boolean isNewRoom = getArea() == null || !Objects.equals(getArea().getRoom(), area.getRoom());
		boolean isNewArea = getArea() == null || !getArea().equals(area);
		if (this.area != null) {
			this.area.removeActor(this);
		}
		this.area = area;
		area.addActor(this);
		if (isCarryingActor()) {
			getCarriedActor().setArea(area, game);
		}
		if (isPlayer()) {
			onPlayerEnterArea(isNewRoom, isNewArea);
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
	public ComputedDamage applyEffectsAndComputeDamage(Damage damage, Context context) {
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
	public void applyDamage(ComputedDamage computedDamage, Context context) {
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
				SensoryEvent.execute(game, new SensoryEvent(getArea(), "$actor lose$s $amount HP.", modifierContext, true, null, null));
			}
			SensoryEvent.execute(game, new SensoryEvent(getArea(), "$actor $is $condition.", modifierContext, true, null, null));
		}
	}
	
	public void kill(Context context) {
		triggerScript("on_death", context);
		if (context.getTarget() != null && context.getTarget() != this) {
			context.getTarget().triggerScript("on_kill", Context.from(defaultContext).subject(context.getTarget()).target(context.getSubject()).build());
		}
		SensoryEvent.execute(game, new SensoryEvent(getArea(), Phrases.get("die"), context, true, null, null));
		stopUsingObjectOnDeathIfPresent();
		isDead = true;
		HP = 0;
		if (isPlayer()) {
			game.onPlayerDeath();
		}
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

	private void updateSleep() {
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
	
	public void onSensoryEvent(Game game, SensoryEvent event, boolean visible) {
		if (isActive() && isEnabled()) {
			if (isPlayer()) {
                String text = null;
                if (visible) {
                    text = event.getTextVisible();
                }
				if (text == null) {
                    text = event.getTextAudible();
                }
                if (text != null) {
                    game.eventBus().post(new RenderTextEvent(text));
                }
            } else {
				if (event.getContext().getSubject().equals(this)) return;
				if (visible) { // Visible
					if (event.isAction()) {
						targetingComponent.onVisibleAction(game, event.getAction(), event.getContext().getSubject());
					} else if (event.isBark()) {
						targetingComponent.onAudibleBark(event.getBark(), game, event.getContext().getSubject(), event.getContext().getTarget(), true);
					}
				} else { // Audible
					if (event.isBark()) {
						targetingComponent.onAudibleBark(event.getBark(), game, event.getContext().getSubject(), event.getContext().getTarget(), false);
					}
				}
			}
		}
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
			SensoryEvent.execute(game, new SensoryEvent(getArea(), Phrases.get(usableComponent.getEndDeathPhrase(usingSlot)), context, true, null, null));
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
	public List<Action> localActions(Game game, Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (isActive()) {
			if (getTemplate().getDialogueStart() != null) {
				actions.add(new ActionTalk(this));
			}
		} else if (isDead()) {
			actions.addAll(inventory.getExternalActions(game, this, subject, "Take", "takeFrom", null, null, true, false));
			actions.add(new ActionCarryActorStart(this));
		}
		for (ActionCustom.CustomActionHolder actionHolder : getTemplate().getCustomActions()) {
			ActionTemplate customActionTemplate = actionHolder.action();
			actions.add(new ActionCustom(scriptRuntime, this, null, null, null, customActionTemplate, actionHolder.parameters(), new MenuDataActor(this), false));
		}
		for (ActionCustom.CustomActionHolder inventoryActionHolder : getTemplate().getCustomInventoryActions()) {
			for (Item item : inventory.getItems()) {
				ActionTemplate customInventoryActionTemplate = inventoryActionHolder.action();
				actions.add(new ActionCustom(scriptRuntime, this, null, item, null, customInventoryActionTemplate, inventoryActionHolder.parameters(), new MenuDataActorInventory(this, item, false, false), false));
			}
		}
		return actions;
	}

	public List<Action> carriedActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		actions.add(new ActionCarryActorEnd(this));
		return actions;
	}

	@Override
	public List<Action> visibleActions(Actor subject) {
		return new ArrayList<>();
	}

	private List<Action> availableActions(Game game) {
		if (!canPerformActions(Context.from(defaultContext).build())) {
			return new ArrayList<>();
		}
		List<Action> actions = new ArrayList<>();
		if (canPerformLocalActions()) {
			for (Actor actor : getArea().getActors()) {
				actions.addAll(actor.localActions(game, this));
			}
			actions.addAll(getArea().getItemActions(game));
			for (WorldObject object : getArea().getObjects()) {
				if (!object.isHidden() && (!isUsingObject() || !object.equals(getUsingObject().object()))) {
					actions.addAll(object.localActions(game, this));
				}
			}
		}
		if (isUsingObject()) {
			actions.addAll(getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).getUsingActions(game, getUsingObject().slot(), this));
			if (getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanPerformParentActions(getUsingObject().slot())) {
				actions.addAll(getUsingObject().object().localActions(game, this));
			}
		}
		if (isCarryingActor()) {
			actions.addAll(getCarriedActor().carriedActions(this));
		}
		for (Actor visibleActor : getLineOfSightActors(game)) {
			if (visibleActor.isVisible(this)) {
				actions.addAll(visibleActor.visibleActions(this));
			}
		}
		for (WorldObject visibleObject : getLineOfSightObjects(game)) {
			if (visibleObject.isVisible(this)) {
				actions.addAll(visibleObject.visibleActions(this));
			}
		}
		if (canMove(Context.from(defaultContext).build())) {
			actions.addAll(getArea().getMoveActions(game, this, null, null));
		}
		actions.addAll(getArea().getAreaActions(this));
		for (Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(game, this));
		}
		actions.addAll(equipmentComponent.getEquippedActions(game));
		for (WeaponAttackType unarmedAttackType : getTemplate().getUnarmedAttackTypes()) {
			actions.addAll(unarmedAttackType.generateActions(this, null));
		}
		if (isSneaking()) {
			actions.add(new ActionSneakEnd());
		} else {
			actions.add(new ActionSneakStart());
		}
		actions.add(new ActionEnd());
		applyActionConstraints(actions);
		return actions;
	}

	private void applyActionConstraints(List<Action> actions) {
		actions.removeIf(action -> !action.canShow(this));
		for (Action currentAction : actions) {
			boolean isRepeatMatch = false;
			boolean isBlocked = false;
			boolean isRepeatBlocked = false;
			for (Action blockedAction : repeatActions.keySet()) {
				if (blockedAction.isRepeatMatch(currentAction)) {
					isRepeatMatch = true;
				}
				if (repeatActions.get(blockedAction) <= 0) {
					if (isRepeatMatch) {
						isRepeatBlocked = true;
						break;
					} else if (blockedAction.isBlockedMatch(currentAction)) {
						isBlocked = true;
						break;
					}
				}
			}
			if (isRepeatBlocked) {
				currentAction.setDisabled(true, "Repeat limit reached");
			} else if (isBlocked) {
				currentAction.setDisabled(true, "Blocked");
			} else if (!(isRepeatMatch && currentAction.repeatsUseNoActionPoints()) && getActionPoints() - actionPointsUsed < currentAction.actionPoints(this)) {
				currentAction.setDisabled(true, "Not enough action points");
			}
		}
	}

	public void takeTurn(Game game) {
		if (!isEnabled() || isDead()) {
			game.onEndTurn(this);
			return;
		}
		if (isSleeping()) {
			updateSleep();
			game.onEndTurn(this);
			return;
		}
		getEffectComponent().onStartRound(game);
		getInventory().onStartRound(game);
		if (!isPlayerControlled) {
			getTargetingComponent().updateTurn(game);
			getBehaviorComponent().updateTurn();
		}
		this.actionPointsUsed = 0;
		this.repeatActions.clear();
		this.endTurn = false;
		Action lastAction = null;
		int repeatActionCount = 0;
		while (!endTurn) {
			questManager.update(game);
			if (!isPlayerControlled()) {
				updateAreaTargets(game);
				getTargetingComponent().update(game);
				getBehaviorComponent().update();
			}
			List<Action> actionChoices = availableActions(game);
			if (actionChoices.isEmpty()) {
				endTurn(game);
				break;
			}
			Action selectedAction;
			if (isPlayerControlled()) {
				selectedAction = menuManager.actionChoiceMenu(this, actionChoices);
			} else {
				selectedAction = chooseAIAction(game, actionChoices);
			}
			boolean isRepeatMatch = false;
			for (Action repeatAction : repeatActions.keySet()) {
				if (repeatAction.isRepeatMatch(selectedAction)) {
					isRepeatMatch = true;
					int countRemaining = repeatActions.get(repeatAction) - 1;
					repeatActions.put(repeatAction, countRemaining);
					break;
				}
			}
			if (!(isRepeatMatch && selectedAction.repeatsUseNoActionPoints())) {
				actionPointsUsed += selectedAction.actionPoints(this);
			}
			if (!isRepeatMatch && selectedAction.repeatCount(this) > 0) {
				repeatActions.put(selectedAction, selectedAction.repeatCount(this) - 1);
			}
			if (lastAction != null && selectedAction.isRepeatMatch(lastAction)) {
				repeatActionCount += 1;
			} else {
				repeatActionCount = 0;
			}
			selectedAction.choose(this, repeatActionCount);
			getBehaviorComponent().onPerformAction(selectedAction);
			lastAction = selectedAction;
		}
		game.onEndTurn(this);
	}

	public boolean isPlayerControlled() {
		return isPlayerControlled;
	}

	public boolean isRepeatAction(Action action) {
		for (Action repeatAction : repeatActions.keySet()) {
			if (repeatAction.isRepeatMatch(action)) {
				return true;
			}
		}
		return false;
	}
	
	public void endTurn(Game game) {
		actionPointsUsed = 0;
		endTurn = true;
		if (!isPlayerControlled() && shouldIdle()) {
			playIdle(game);
		}
	}

	public Action chooseAIAction(Game game, List<Action> actions) {
		return UtilityUtils.selectActionByUtility(this, actions, 1);
	}

	private void playIdle(Game game) {
		if (getBehaviorComponent() != null) {
			Idle idle = getBehaviorComponent().getIdle();
			if (idle != null) {
				idle.trigger(game, this);
			}
		}
	}

	private boolean shouldIdle() {
		return !isInCombat();
	}
	
	private void updateAreaTargets(Game game) {
		Iterator<AreaTarget> itr = areaTargets.iterator();
		while (itr.hasNext()) {
			AreaTarget target = itr.next();
			target.update(game, this);
			if (target.shouldRemove()) {
				itr.remove();
			}
		}
	}

	public Map<Area, Pathfinder.VisibleAreaData> getVisibleAreas(AreaRegistry areaRegistry) {
		if (isUsingObject() && !getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanSeeOtherAreas(getUsingObject().slot())) {
			return Map.of(getArea(), new Pathfinder.VisibleAreaData(null, Area.pathLengthToDistance(0), List.of(getArea())));
		} else {
			return Pathfinder.getVisibleAreas(areaRegistry, getArea(), this);
		}
	}

	public boolean isVisible(Actor subject) {
        return !(isInCover() && !getArea().equals(subject.getArea()));
    }

	public Set<Actor> getLineOfSightActors(AreaRegistry areaRegistry) {
		Set<Actor> visibleActors = new HashSet<>();
		for (Area visibleArea : getVisibleAreas(areaRegistry).keySet()) {
			for (Actor actor : visibleArea.getActors()) {
				if (!actor.equals(this) && !actor.isInCover()) {
					visibleActors.add(actor);
				}
			}
		}
		return visibleActors;
	}

	public Set<WorldObject> getLineOfSightObjects(Game game) {
		Set<WorldObject> visibleObjects = new HashSet<>();
		for (Area visibleArea : getVisibleAreas(game).keySet()) {
			for (WorldObject object : visibleArea.getObjects()) {
				if (!object.isHidden()) {
					visibleObjects.add(object);
				}
			}
		}
		return visibleObjects;
	}

	public Set<AttackTarget> getLineOfSightAttackTargets(AreaRegistry areaRegistry) {
		Set<AttackTarget> attackTargets = new HashSet<>(getLineOfSightActors(areaRegistry));
		attackTargets.addAll(getLineOfSightObjects(areaRegistry));
		return attackTargets;
	}

	public Set<SenseType> getSenseTypes() {
		Context context = Context.from(defaultContext).build();
		return senseTypes.valueObjects(getTemplate().getSenseTypes(), context);
	}

	public Set<String> getAllBypassedObstructionTypes() {
		Set<String> bypassedTypes = new HashSet<>();
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
			case "equipment_effects" -> Expression.constant(equipmentEffects.value(new HashSet<>(), context));
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
			case "area" -> {
				setArea(context.game().data().getArea(value.getValueString()), context.game());
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

	public void triggerBark(String trigger, Game game, Context context) {
		if (isActive()) {
			Bark bark = getBark(trigger);
			if (bark != null) {
				bark.trigger(game, context);
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

	private void onPlayerEnterArea(boolean isNewRoom, boolean isNewArea) {
		eventBus.post(new RenderAreaEvent(getArea().getRoom() != null ? LangUtils.titleCase(getArea().getRoom().getName()) : null, LangUtils.titleCase(getArea().getName())));
		if (isNewRoom && getArea().getRoom() != null && getArea().getRoom().getDescription() != null) {
			menuManager.sceneMenu(getArea().getRoom().getDescription(), Context.builder().subject(this).target(this).build(), false);
			getArea().getRoom().setKnown();
			for (Area areaInRoom : getArea().getRoom().getAreas()) {
				areaInRoom.setKnown();
			}
		}
		if (isNewArea && getArea().getDescription() != null) {
			menuManager.sceneMenu(getArea().getDescription(), Context.builder().subject(this).target(this).build(), false);
			getArea().setKnown();
		}
		if (isNewRoom && getArea().getRoom() != null) {
			getArea().getRoom().triggerScript("on_player_enter", Context.from(defaultContext).build());
		}
		getArea().triggerScript("on_player_enter", Context.from(defaultContext).build());
	}

}
