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
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
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
import com.github.finley243.adventureengine.world.path.PathDataArea;

import java.util.*;

public class Actor extends GameInstanced implements Noun, Physical, MutableStatHolder, AttackTarget {

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

	private final String templateID;
	private boolean isKnown;
	private final Area defaultArea;
	private Area area;
	private final StatInt maxHP;
	private int HP;
	private final Map<String, StatInt> damageResistance;
	private final Map<String, StatFloat> damageMult;
	private final boolean startDisabled;
	private boolean isEnabled;
	private final boolean startDead;
	private boolean isDead;
	private boolean isSleeping;
	private boolean endTurn;
	private final StatInt actionPoints;
	private final StatInt movePoints;
	private int actionPointsUsed;
	private final StatBoolean canMove;
	private final StatBoolean canDodge;
	private final Map<Action, Integer> repeatActions;
	private final Map<String, Integer> attributesBase;
	private final Map<String, Integer> skillsBase;
	private final Map<String, StatInt> attributes;
	private final Map<String, StatInt> skills;
	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final EquipmentComponent equipmentComponent;
	private final StatStringSet equipmentEffects;
	private final TargetingComponent targetingComponent;
	private final BehaviorComponent behaviorComponent;
	private Actor carriedActor;
	private int money;
	private ObjectComponentUsable.ObjectUserData usingObject;
	private final StatStringSet senseTypes;
	private final Set<AreaTarget> areaTargets;
	private int sleepCounter;
	private boolean playerControlled;
	private final StatStringSet tags;

	public Actor(Game game, String ID, Area area, String templateID, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean playerControlled) {
		super(game, ID);
		this.defaultArea = area;
		this.area = area;
		this.templateID = templateID;
		this.targetingComponent = new TargetingComponent(this);
		this.areaTargets = new HashSet<>();
		this.senseTypes = new StatStringSet("sense_types", this);
		this.startDead = startDead;
		this.isDead = startDead;
		this.maxHP = new StatInt("max_hp", this);
		this.damageResistance = new HashMap<>();
		this.damageMult = new HashMap<>();
		this.actionPoints = new StatInt("action_points", this);
		this.movePoints = new StatInt("move_points", this);
		this.canMove = new StatBoolean("can_move", this, false);
		this.canDodge = new StatBoolean("can_dodge", this, false);
		this.inventory = new Inventory(game, this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.equipmentEffects = new StatStringSet("equipment_effects", this);
		this.attributesBase = new HashMap<>();
		this.skillsBase = new HashMap<>();
		this.attributes = new HashMap<>();
		this.skills = new HashMap<>();
		this.effectComponent = new EffectComponent(game, this, new Context(game, this, this));
		this.behaviorComponent = new BehaviorComponent(this, behaviors);
		this.repeatActions = new HashMap<>();
		this.startDisabled = startDisabled;
		this.playerControlled = playerControlled;
		this.tags = new StatStringSet("tags", this);
		setEnabled(!startDisabled);
	}

	public void onInit() {
		if (!startDead) {
			HP = maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP, new Context(game(), this, this));
		}
		for (String damageType : game().data().getDamageTypeIDs()) {
			this.damageResistance.put(damageType, new StatInt("damage_resist_" + damageType, this));
			this.damageMult.put(damageType, new StatFloat("damage_mult_" + damageType, this));
		}
		for (String attribute : game().data().getAttributeIDs()) {
			this.attributesBase.put(attribute, getTemplate().getAttribute(attribute));
			this.attributes.put(attribute, new StatInt("attribute_" + attribute, this));
		}
		for (String skill : game().data().getSkillIDs()) {
			this.skillsBase.put(skill, getTemplate().getSkill(skill));
			this.skills.put(skill, new StatInt("skill_" + skill, this));
		}
		if (getTemplate().getLootTable() != null) {
			getTemplate().getLootTable().generateItems(game(), inventory);
		}
		for (String startingEffect : getTemplate().getStartingEffects()) {
			getEffectComponent().addEffect(startingEffect);
		}
	}

	private ActorTemplate getTemplate() {
		return game().data().getActorTemplate(templateID);
	}

	public boolean isPlayer() {
		return this.equals(game().data().getPlayer());
	}
	
	@Override
	public String getName() {
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
		boolean isNewRoom = getArea() == null || !Objects.equals(getArea().getRoom(), area.getRoom());
		boolean isNewArea = getArea() == null || !getArea().equals(area);
		if (this.area != null) {
			this.area.removeActor(this);
		}
		this.area = area;
		area.addActor(this);
		if (isCarryingActor()) {
			getCarriedActor().setArea(area);
		}
		if (isPlayer()) {
			game().eventBus().post(new RenderAreaEvent(getArea().getRoom() != null ? LangUtils.titleCase(getArea().getRoom().getName()) : null, LangUtils.titleCase(getArea().getName())));
			if (isNewRoom && getArea().getRoom() != null && getArea().getRoom().getDescription() != null) {
				game().menuManager().sceneMenu(game(), getArea().getRoom().getDescription(), null, new Context(game(), this, this));
				getArea().getRoom().setKnown();
				for (Area areaInRoom : getArea().getRoom().getAreas()) {
					areaInRoom.setKnown();
				}
			}
			if (isNewArea && getArea().getDescription() != null) {
				game().menuManager().sceneMenu(game(), getArea().getDescription(), null, new Context(game(), this, this));
				getArea().setKnown();
			}
			if (isNewRoom && getArea().getRoom() != null) {
				getArea().getRoom().triggerScript("on_player_enter", this, this);
			}
			getArea().triggerScript("on_player_enter", this, this);
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
		if (!game().data().getAttributeIDs().contains(attribute)) {
			game().log().print("Actor " + this + " - attempted to set base attribute that does not exist: " + attribute);
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
		if (!game().data().getSkillIDs().contains(skill)) {
			game().log().print("Actor " + this + " - attempted to set base skill that does not exist: " + skill);
		} else {
			skillsBase.put(skill, value);
		}
	}
	
	public Scene getDialogueStart() {
		return game().data().getScene(getTemplate().getDialogueStart());
	}
	
	public Faction getFaction() {
		return game().data().getFaction(getTemplate().getFaction());
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
		return maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP, new Context(game(), this, this));
	}

	public int getDamageResistance(String damageType, Context context) {
		return damageResistance.get(damageType).value(getTemplate().getDamageResistance(damageType), 0, MAX_DAMAGE_RESIST, context);
	}

	public float getDamageMult(String damageType, Context context) {
		return damageMult.get(damageType).value(getTemplate().getDamageMult(damageType), 0.0f, MAX_DAMAGE_MULT, context);
	}

	public int getActionPoints() {
		return actionPoints.value(getTemplate().getActionPoints(), 0, MAX_ACTION_POINTS, new Context(game(), this, this));
	}

	public int getMovePoints() {
		return movePoints.value(getTemplate().getMovePoints(), 0, MAX_MOVE_POINTS, new Context(game(), this, this));
	}

	public Set<String> getEquipmentEffects(Item item) {
		return equipmentEffects.value(new HashSet<>(), new Context(game(), this, this, item));
	}

	@Override
	public boolean canBeAttacked() {
		return !isDead();
	}

	@Override
	public void damage(Damage damage, Context context) {
		for (String effectID : damage.getTargetEffects()) {
			getEffectComponent().addEffect(effectID);
		}
		int amount = damage.getAmount();
		int equipmentResistance;
		float equipmentMult;
		if (damage.getLimb() != null) {
			equipmentResistance = getEquipmentComponent().getDamageResistanceLimb(damage.getLimb().getID(), damage.getType());
			equipmentMult = getEquipmentComponent().getDamageMultLimb(damage.getLimb().getID(), damage.getType());
		} else {
			equipmentResistance = getEquipmentComponent().getDamageResistanceMain(damage.getType());
			equipmentMult = getEquipmentComponent().getDamageMultMain(damage.getType());
		}
		int actorResistance = getDamageResistance(damage.getType(), context);
		float actorMult = getDamageMult(damage.getType(), context);
		// TODO - Add additional armor mult for damage mults (part of the Damage object, affected by weapons/attacks/etc.)
		amount -= Math.round(equipmentResistance * damage.getArmorMult());
		amount -= Math.round(amount * equipmentMult);
		amount -= Math.round(actorResistance * damage.getArmorMult());
		amount -= Math.round(amount * actorMult);
		if (damage.getLimb() != null) {
			amount = Math.round(amount * damage.getLimb().getDamageMult());
		}
		if (amount < 0) amount = 0;
		if (damage.getLimb() != null && amount > 0) {
			damage.getLimb().applyEffects(this);
		}
		modifyHP(-amount, context);
	}

	public void modifyHP(int amount, Context context) {
		HP += amount;
		if (HP <= 0) {
			HP = 0;
			kill();
		} else if (amount < 0) {
			Context modifierContext = new Context(game(), this, context.getSubject());
			modifierContext.setLocalVariable("amount", Expression.constant(String.valueOf(-amount)));
			modifierContext.setLocalVariable("condition", Expression.constant(getConditionDescription()));
			triggerScript("on_damaged", modifierContext);
			if (SHOW_HP_CHANGES) {
				SensoryEvent.execute(game(), new SensoryEvent(getArea(), "$actor lose$s $amount HP.", modifierContext, true, null, null));
			}
			SensoryEvent.execute(game(), new SensoryEvent(getArea(), "$actor $is $condition.", modifierContext, true, null, null));
		}
	}
	
	public void kill() {
		Context context = new Context(game(), this, null);
		triggerScript("on_death", context);
		SensoryEvent.execute(game(), new SensoryEvent(getArea(), Phrases.get("die"), context, true, null, null));
		// TODO - Enable held item dropping on death for new equipment system
		// TODO - Remove from usable object, if applicable (for certain types of usable objects)
		isDead = true;
		HP = 0;
		if (isPlayer()) {
			game().onPlayerDeath();
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
	
	public void onSensoryEvent(SensoryEvent event, boolean visible) {
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
                    game().eventBus().post(new RenderTextEvent(text));
                }
            } else {
				if (event.getContext().getSubject().equals(this)) return;
				if (visible) { // Visible
					if (event.isAction()) {
						targetingComponent.onVisibleAction(event.getAction(), event.getContext().getSubject());
					} else if (event.isBark()) {
						if (event.getBark().responseType() == Bark.BarkResponseType.HOSTILE) {
							targetingComponent.addCombatant(event.getContext().getTarget());
						}
					}
				} else { // Audible
					if (event.isBark()) {
						if (event.getBark().responseType() == Bark.BarkResponseType.HOSTILE) {
							targetingComponent.addCombatant(event.getContext().getTarget());
						}
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
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (isActive()) {
			if (getTemplate().getDialogueStart() != null) {
				actions.add(new ActionTalk(this));
			}
		} else if (isDead()) {
			actions.addAll(inventory.getExternalActions(this, subject, "Take", "takeFrom", null, null, true, false));
			actions.add(new ActionCarryActorStart(this));
		}
		for (ActionCustom.CustomActionHolder actionHolder : getTemplate().getCustomActions()) {
			actions.add(new ActionCustom(game(), this, null, null, null, actionHolder.action(), actionHolder.parameters(), new MenuDataActor(this), false));
		}
		for (ActionCustom.CustomActionHolder inventoryActionHolder : getTemplate().getCustomInventoryActions()) {
			for (Item item : inventory.getItems()) {
				actions.add(new ActionCustom(game(), this, null, item, null, inventoryActionHolder.action(), inventoryActionHolder.parameters(), new MenuDataActorInventory(this, item, false, false), false));
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

	public List<Action> availableActions() {
		List<Action> actions = new ArrayList<>();
		if (canPerformLocalActions()) {
			for (Actor actor : getArea().getActors()) {
				actions.addAll(actor.localActions(this));
			}
			actions.addAll(getArea().getItemActions());
			for (WorldObject object : getArea().getObjects()) {
				if (!object.isHidden() && (!isUsingObject() || !object.equals(getUsingObject().object()))) {
					actions.addAll(object.localActions(this));
				}
			}
		}
		if (isUsingObject()) {
			actions.addAll(getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).getUsingActions(getUsingObject().slot(), this));
			if (getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanPerformParentActions(getUsingObject().slot())) {
				actions.addAll(getUsingObject().object().localActions(this));
			}
		}
		if (isCarryingActor()) {
			actions.addAll(getCarriedActor().carriedActions(this));
		}
		for (Actor visibleActor : getLineOfSightActors()) {
			if (visibleActor.isVisible(this)) {
				actions.addAll(visibleActor.visibleActions(this));
			}
		}
		for (WorldObject visibleObject : getLineOfSightObjects()) {
			if (visibleObject.isVisible(this)) {
				actions.addAll(visibleObject.visibleActions(this));
			}
		}
		if (canMove(new Context(game(), this, this))) {
			actions.addAll(getArea().getMoveActions(this, null, null));
		}
		actions.addAll(getArea().getAreaActions(this));
		for (Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		actions.addAll(equipmentComponent.getEquippedActions());
		for (String unarmedAttackType : getTemplate().getUnarmedAttackTypes()) {
			actions.addAll(game().data().getAttackType(unarmedAttackType).generateActions(this, null));
		}
		actions.add(new ActionEnd());
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
		return actions;
	}
	
	public void takeTurn() {
		if (!isEnabled() || isDead()) {
			game().onEndTurn(this);
			return;
		}
		if (isSleeping()) {
			updateSleep();
			game().onEndTurn(this);
			return;
		}
		getEffectComponent().onStartRound();
		if (!playerControlled) {
			getTargetingComponent().updateTurn();
			getBehaviorComponent().updateTurn();
		}
		this.actionPointsUsed = 0;
		this.repeatActions.clear();
		this.endTurn = false;
		nextAction(null, 0);
	}

	public boolean isPlayerControlled() {
		return playerControlled;
	}

	private void nextAction(Action lastAction, int repeatActionCount) {
		if (!playerControlled) {
			updatePursueTargets();
			getTargetingComponent().update();
			getBehaviorComponent().update();
		}
		List<Action> actionChoices = availableActions();
		if (actionChoices.isEmpty()) {
			endTurn();
		}
        Action selectedAction;
        if (isPlayerControlled()) {
            selectedAction = game().menuManager().actionChoiceMenu(game(), this, actionChoices);
        } else {
            selectedAction = chooseAIAction(actionChoices);
        }
        onSelectAction(selectedAction, lastAction, repeatActionCount);
    }

	public boolean isRepeatAction(Action action) {
		for (Action repeatAction : repeatActions.keySet()) {
			if (repeatAction.isRepeatMatch(action)) {
				return true;
			}
		}
		return false;
	}

	public void onSelectAction(Action action, Action lastAction, int repeatActionCount) {
		boolean isRepeatMatch = false;
		for (Action repeatAction : repeatActions.keySet()) {
			if (repeatAction.isRepeatMatch(action)) {
				isRepeatMatch = true;
				int countRemaining = repeatActions.get(repeatAction) - 1;
				repeatActions.put(repeatAction, countRemaining);
				break;
			}
		}
		if (!(isRepeatMatch && action.repeatsUseNoActionPoints())) {
			actionPointsUsed += action.actionPoints(this);
		}
		if (!isRepeatMatch && action.repeatCount(this) > 0) {
			repeatActions.put(action, action.repeatCount(this) - 1);
		}
		if (lastAction != null && action.isRepeatMatch(lastAction)) {
			repeatActionCount += 1;
		} else {
			repeatActionCount = 0;
		}
		action.choose(this, repeatActionCount);
		getBehaviorComponent().onPerformAction(action);
		if (endTurn) {
			game().onEndTurn(this);
		} else {
			nextAction(action, repeatActionCount);
		}
	}
	
	public void endTurn() {
		actionPointsUsed = 0;
		endTurn = true;
		if (!playerControlled && shouldIdle()) {
			playIdle();
		}
	}

	public Action chooseAIAction(List<Action> actions) {
		return UtilityUtils.selectActionByUtility(this, actions, 1);
	}

	private void playIdle() {
		if (getBehaviorComponent() != null) {
			Idle idle = getBehaviorComponent().getIdle();
			if (idle != null) {
				idle.trigger(this);
			}
		}
	}

	private boolean shouldIdle() {
		return !isInCombat();
	}
	
	private void updatePursueTargets() {
		Iterator<AreaTarget> itr = areaTargets.iterator();
		while (itr.hasNext()) {
			AreaTarget target = itr.next();
			target.update(this);
			if (target.shouldRemove()) {
				itr.remove();
			}
		}
	}

	public Map<Area, Pathfinder.VisibleAreaData> getVisibleAreas() {
		if (isUsingObject() && !getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).userCanSeeOtherAreas(getUsingObject().slot())) {
			return Map.of(getArea(), new Pathfinder.VisibleAreaData(null, Area.pathLengthToDistance(0), List.of(new PathDataArea(getArea()))));
		} else {
			return Pathfinder.getVisibleAreas(getArea(), this);
		}
	}

	public boolean isVisible(Actor subject) {
        return !(isInCover() && !getArea().equals(subject.getArea()));
    }

	public Set<Actor> getLineOfSightActors() {
		Set<Actor> visibleActors = new HashSet<>();
		for (Area visibleArea : getVisibleAreas().keySet()) {
			for (Actor actor : visibleArea.getActors()) {
				if (!actor.equals(this) && !actor.isInCover()) {
					visibleActors.add(actor);
				}
			}
		}
		return visibleActors;
	}

	public Set<WorldObject> getLineOfSightObjects() {
		Set<WorldObject> visibleObjects = new HashSet<>();
		for (Area visibleArea : getVisibleAreas().keySet()) {
			for (WorldObject object : visibleArea.getObjects()) {
				if (!object.isHidden()) {
					visibleObjects.add(object);
				}
			}
		}
		return visibleObjects;
	}

	public Set<AttackTarget> getLineOfSightAttackTargets() {
		Set<AttackTarget> attackTargets = new HashSet<>(getLineOfSightActors());
		attackTargets.addAll(getLineOfSightObjects());
		return attackTargets;
	}

	@Override
	public StatInt getStatInt(String name) {
		if (name.startsWith("damage_resist_")) {
			for (String damageType : game().data().getDamageTypeIDs()) {
				if (name.equals("damage_resist_" + damageType)) {
					return damageResistance.get(damageType);
				}
			}
			return null;
		} else if (name.startsWith("attribute_")) {
			for (String attribute : game().data().getAttributeIDs()) {
				if (name.equals("attribute_" + attribute)) {
					return attributes.get(attribute);
				}
			}
			return null;
		} else if (name.startsWith("skill_")) {
			for (String skill : game().data().getSkillIDs()) {
				if (name.equals("skill_" + skill)) {
					return skills.get(skill);
				}
			}
			return null;
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
			for (String damageType : game().data().getDamageTypeIDs()) {
				if (name.equals("damage_mult_" + damageType)) {
					return damageMult.get(damageType);
				}
			}
			return null;
		}
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		if ("can_move".equals(name)) {
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
		} else if ("tags".equals(name)) {
			return tags;
		}
		return null;
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		if (name.startsWith("damage_resist_")) {
			for (String damageType : game().data().getDamageTypeIDs()) {
				if (name.equals("damage_resist_" + damageType)) {
					return Expression.constant(getDamageResistance(damageType, context));
				}
			}
			game().log().print("Actor " + this + " - getStatValue " + name + " references an invalid damage type");
			return Expression.constant(0);
		} else if (name.startsWith("attribute_")) {
			for (String attribute : game().data().getAttributeIDs()) {
				if (name.equals("attribute_" + attribute)) {
					return Expression.constant(getAttribute(attribute, context));
				}
			}
			game().log().print("Actor " + this + " - getStatValue " + name + " references an invalid attribute");
			return Expression.constant(0);
		} else if (name.startsWith("skill_")) {
			for (String skill : game().data().getSkillIDs()) {
				if (name.equals("skill_" + skill)) {
					return Expression.constant(getSkill(skill, context));
				}
			}
			game().log().print("Actor " + this + " - getStatValue " + name + " references an invalid skill");
			return Expression.constant(0);
		} else if (name.startsWith("damage_mult_")) {
			for (String damageType : game().data().getDamageTypeIDs()) {
				if (name.equals("damage_mult_" + damageType)) {
					return Expression.constant(getDamageMult(damageType, context));
				}
			}
			game().log().print("Actor " + this + " - getStatValue " + name + " references an invalid damage type");
			return Expression.constant(0.0f);
		} else if (name.startsWith("has_equipped_")) {
			for (String slot : getTemplate().getEquipSlots().keySet()) {
				if (name.equals("has_equipped_" + slot)) {
					return Expression.constant(equipmentComponent.getEquippedItemInSlot(slot) != null);
				}
			}
			game().log().print("Actor " + this + " - getStatValue " + name + " references an invalid equip slot");
			return Expression.constant(false);
		}
		return switch (name) {
			case "inventory" -> (getInventory() == null ? null : Expression.constant(getInventory()));
			case "noun" -> Expression.constantNoun(this);
			case "max_hp" -> Expression.constant(getMaxHP());
			case "hp" -> Expression.constant(HP);
			case "action_points" -> Expression.constant(getActionPoints());
			case "move_points" -> Expression.constant(getMovePoints());
			case "money" -> Expression.constant(money);
			case "enabled" -> Expression.constant(isEnabled);
			case "sleeping" -> Expression.constant(isSleeping);
			case "in_combat" -> Expression.constant(isInCombat());
			case "using_object" -> Expression.constant(isUsingObject());
			case "in_cover" -> Expression.constant(isInCover());
			case "dead" -> Expression.constant(isDead);
			case "active" -> Expression.constant(isActive());
			case "can_move" -> Expression.constant(canMove(context));
			case "can_dodge" -> Expression.constant(canDodge(context));
			case "id" -> Expression.constant(getID());
			case "template_id" -> Expression.constant(templateID);
			case "area" -> Expression.constant(getArea().getID());
			case "room" -> Expression.constant(getArea().getRoom() != null ? getArea().getRoom().getID() : null);
			case "equipment_effects" -> Expression.constant(equipmentEffects.value(new HashSet<>(), context));
			case "sense_types" -> Expression.constant(senseTypes.value(getTemplate().getSenseTypes(), context));
			case "tags" -> Expression.constant(tags.value(getTemplate().getTags(), context));
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
			game().log().print("Actor " + this + " - setStatValue " + name + " references an invalid equip slot");
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
				playerControlled = value.getValueBoolean();
				return true;
			}
			case "hp" -> {
				HP = MathUtils.bound(value.getValueInteger(), 0, getMaxHP());
				return true;
			}
			case "money" -> {
				money = value.getValueInteger();
				return true;
			}
			case "area" -> {
				setArea(game().data().getArea(value.getValueString()));
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

	public boolean triggerScript(String trigger, Context context) {
		Script script = getTemplate().getScript(trigger);
		if (script != null) {
			script.execute(context);
			return true;
		} else {
			return false;
		}
	}

	public void triggerBark(String trigger, Context context) {
		if (isActive()) {
			Bark bark = getBark(trigger);
			if (bark != null) {
				bark.trigger(context);
			}
		}
	}

	public Bark getBark(String trigger) {
		return getTemplate().getBark(trigger);
	}

	public void loadState(SaveData saveData) {
		switch (saveData.getParameter()) {
			case "hp" -> this.HP = saveData.getValueInt();
			case "is_enabled" -> setEnabled(saveData.getValueBoolean());
			case "is_dead" -> this.isDead = saveData.getValueBoolean();
			case "is_known" -> this.isKnown = saveData.getValueBoolean();
			case "area" -> {
				if (saveData.getValueString() == null) {
					this.area = null;
				} else {
					this.area = game().data().getArea(saveData.getValueString());
				}
			}
			case "inventory" -> {
				if (inventory != null) inventory.loadState(saveData);
			}
			//case "equipped_item" -> this.equipmentComponent.equip((ItemEquippable) game().data().getItemState(saveData.getValueString()));
			//case "equipped_apparel" -> this.equipmentComponent.equip((ItemEquippable) game().data().getItemState(saveData.getValueString()));
			case "action_points_used" -> this.actionPointsUsed = saveData.getValueInt();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (isKnown) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "is_known", isKnown));
		}
		if (isEnabled == startDisabled) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "is_enabled", isEnabled));
		}
		if (isDead != startDead) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "is_dead", isDead));
		}
		if (area != defaultArea) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "area", (area == null ? null : area.getID())));
		}
		if (inventory != null) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "inventory", inventory.saveState()));
		}
		if (targetingComponent != null) {
			//state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "targeting", targetingComponent.saveState()));
		}
		/*if (equipmentComponent.hasEquippedItem()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equipped_item", equipmentComponent.getEquippedItem().getID()));
		}*/
		for (Item item : equipmentComponent.getEquippedItems()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equipped_apparel", item.getID()));
		}
		if (usingObject != null) {
			//state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "using_object", usingObject.getObject().getID()));
		}
		if (actionPointsUsed != 0) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "action_points_used", actionPointsUsed));
		}
		return state;
	}
	
}
