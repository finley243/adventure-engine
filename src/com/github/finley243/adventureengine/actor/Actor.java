package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionEnd;
import com.github.finley243.adventureengine.action.ActionTalk;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.actor.component.*;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemApparel;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

import java.util.*;

public class Actor extends GameInstanced implements Noun, Physical, MutableStatHolder, AttackTarget {

	public static final boolean SHOW_HP_CHANGES = true;
	public static final int ACTIONS_PER_TURN = 3;
	public static final int MOVES_PER_TURN = 2;
	public static final int ATTRIBUTE_MIN = 1;
	public static final int ATTRIBUTE_MAX = 10;
	public static final int SKILL_MIN = 1;
	public static final int SKILL_MAX = 10;
	public static final int MAX_HP = 1000;
	public static final int MAX_DAMAGE_RESIST = 1000;
	public static final int MAX_ACTION_POINTS = 10;
	
	public enum Attribute {
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	public enum Skill {
		// BODY
		MELEE(Attribute.BODY),
		THROWING(Attribute.BODY),
		INTIMIDATION(Attribute.BODY),
		// INTELLIGENCE
		SOFTWARE(Attribute.INTELLIGENCE),
		HARDWARE(Attribute.INTELLIGENCE),
		MEDICINE(Attribute.INTELLIGENCE),
		// CHARISMA
		BARTER(Attribute.CHARISMA),
		PERSUASION(Attribute.CHARISMA),
		DECEPTION(Attribute.CHARISMA),
		// DEXTERITY
		HANDGUNS(Attribute.DEXTERITY),
		LONG_ARMS(Attribute.DEXTERITY),
		LOCKPICK(Attribute.DEXTERITY),
		// AGILITY
		STEALTH(Attribute.AGILITY),
		DODGE(Attribute.AGILITY);

		public final Attribute attribute;

		Skill(Attribute attribute) {
			this.attribute = attribute;
		}
	}

	private final String templateID;
	private boolean isKnown;
	private final Area defaultArea;
	private Area area;
	private final StatInt maxHP;
	private int HP;
	private final Map<String, StatInt> damageResistance;
	private final boolean startDisabled;
	private boolean isEnabled;
	private final boolean startDead;
	private boolean isDead;
	private boolean isSleeping;
	private boolean endTurn;
	private final StatInt actionPoints;
	private int actionPointsUsed;
	private final StatBoolean canMove;
	private final StatBoolean canDodge;
	private final Map<Action, Integer> blockedActions;
	private final Map<Attribute, StatInt> attributes;
	private final Map<Skill, StatInt> skills;
	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final ApparelComponent apparelComponent;
	private final EquipmentComponent equipmentComponent;
	private final TargetingComponent targetingComponent;
	private final BehaviorComponent behaviorComponent;
	private int money;
	private ObjectComponentUsable usingObject;
	private final Set<AreaTarget> areaTargets;
	private int sleepCounter;
	private boolean playerControlled;

	public Actor(Game game, String ID, Area area, String templateID, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean playerControlled) {
		super(game, ID);
		this.defaultArea = area;
		this.area = area;
		this.templateID = templateID;
		this.targetingComponent = new TargetingComponent(this);
		this.areaTargets = new HashSet<>();
		this.startDead = startDead;
		this.isDead = startDead;
		this.maxHP = new StatInt("max_hp", this);
		this.damageResistance = new HashMap<>();
		this.actionPoints = new StatInt("action_points", this);
		this.canMove = new StatBoolean("can_move", this, false);
		this.canDodge = new StatBoolean("can_dodge", this, false);
		this.inventory = new Inventory(game, this);
		this.apparelComponent = new ApparelComponent(this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.attributes = new EnumMap<>(Attribute.class);
		for (Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new StatInt("attribute_" + attribute, this));
		}
		this.skills = new EnumMap<>(Skill.class);
		for (Skill skill : Skill.values()) {
			this.skills.put(skill, new StatInt("skill_" + skill, this));
		}
		this.effectComponent = new EffectComponent(game, this, new Context(game, this, this));
		this.behaviorComponent = new BehaviorComponent(this, behaviors);
		this.blockedActions = new HashMap<>();
		this.startDisabled = startDisabled;
		this.playerControlled = playerControlled;
		setEnabled(!startDisabled);
	}

	public void onNewGameInit() {
		if (!startDead) {
			HP = this.maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP);
		}
		for (String damageType : game().data().getDamageTypes()) {
			this.damageResistance.put(damageType, new StatInt("damage_resist_" + damageType, this));
		}
		if (getTemplate().getLootTable() != null) {
			inventory.addItems(getTemplate().getLootTable().generateItems(game()));
		}
	}

	public ActorTemplate getTemplate() {
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
	public String getFormattedName() {
		String statePrefix = "";
		if (isDead()) {
			statePrefix = "dead ";
		}
		if (!isProperName()) {
			return LangUtils.addArticle(statePrefix + getName(), !isKnown());
		} else {
			return statePrefix + getName();
		}
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
	public Pronoun getPronoun() {
		return getTemplate().getPronoun();
	}

	@Override
	public boolean forcePronoun() {
		return isPlayer();
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		boolean isNewRoom = getArea() == null || !getArea().getRoom().equals(area.getRoom());
		boolean isNewArea = getArea() == null || !getArea().equals(area);
		if (this.area != null) {
			this.area.removeActor(this);
		}
		this.area = area;
		area.addActor(this);
		if (isPlayer()) {
			game().eventBus().post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
			if (isNewRoom && getArea().getRoom().getDescription() != null) {
				SceneManager.trigger(game(), this, this, getArea().getRoom().getDescription());
				getArea().getRoom().setKnown();
				for (Area areaInRoom : getArea().getRoom().getAreas()) {
					areaInRoom.setKnown();
				}
			}
			if (isNewArea && getArea().getDescription() != null) {
				SceneManager.trigger(game(), this, this, getArea().getDescription());
				getArea().setKnown();
			}
			if (isNewRoom) {
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
	
	public int getAttribute(Attribute attribute) {
		return attributes.get(attribute).value(getTemplate().getAttribute(attribute), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
	}

	public int getSkill(Skill skill) {
		return skills.get(skill).value(getTemplate().getSkill(skill), SKILL_MIN, SKILL_MAX);
	}
	
	public Scene getDialogueStart() {
		return game().data().getScene(getTemplate().getDialogueStart());
	}
	
	public Faction getFaction() {
		return game().data().getFaction(getTemplate().getFaction());
	}
	
	public boolean canMove() {
		if (isUsingObject()) {
			return false;
		}
		return canMove.value(true);
	}

	public boolean canPerformLocalActions() {
		if (isUsingObject()) {
			return getUsingObject().getTemplateUsable().userCanPerformLocalActions();
		}
		return true;
	}

	public boolean canDodge() {
		return canDodge.value(true);
	}

	public boolean isInCover() {
		return isUsingObject() && getUsingObject().getTemplateUsable().userIsInCover();
	}

	public List<Limb> getLimbs() {
		return getTemplate().getLimbs();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public ApparelComponent getApparelComponent() {
		return apparelComponent;
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

	public int getMaxHP() {
		return maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP);
	}

	public int getDamageResistance(String damageType) {
		return damageResistance.get(damageType).value(getTemplate().getDamageResistance(damageType), 0, MAX_DAMAGE_RESIST);
	}

	public int getActionPoints() {
		return actionPoints.value(ACTIONS_PER_TURN, 0, MAX_ACTION_POINTS);
	}
	
	public void heal(int amount) {
		if (amount < 0) throw new IllegalArgumentException();
		amount = Math.min(amount, getMaxHP() - HP);
		HP += amount;
		TextContext context = new TextContext(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new MapBuilder<String, Noun>().put("actor", this).build());
		if (SHOW_HP_CHANGES) {
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor gain$s_actor $amount HP", context, null, null, this, null));
		}
		game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null, this, null));
	}

	@Override
	public boolean canBeAttacked() {
		return !isDead();
	}

	@Override
	public void damage(Damage damage) {
		if (damage.getLimb() != null) {
			damageLimb(damage, damage.getLimb());
		} else {
			damageDirect(damage);
		}
	}

	private void damageDirect(Damage damage) {
		for (String effectID : damage.getTargetEffects()) {
			effectComponent.addEffect(effectID);
		}
		int amount = damage.getAmount();
		//amount -= apparelComponent.getDamageResistance(getTemplate().getDefaultApparelSlot(), damage.getType()) * damage.getArmorMult();
		amount -= getDamageResistance(damage.getType()) * damage.getArmorMult();
		HP -= amount;
		if (HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged", this);
			TextContext context = new TextContext(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new MapBuilder<String, Noun>().put("actor", this).build());
			if (SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null, this, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null, this, null));
		}
	}

	private void damageLimb(Damage damage, Limb limb) {
		for (String effectID : damage.getTargetEffects()) {
			effectComponent.addEffect(effectID);
		}
		int amount = damage.getAmount();
		//amount -= apparelComponent.getDamageResistance(limb.getApparelSlot(), damage.getType()) * damage.getArmorMult();
		amount -= getDamageResistance(damage.getType()) * damage.getArmorMult();
		if (amount < 0) amount = 0;
		if (amount > 0) {
			limb.applyEffects(this);
		}
		amount *= limb.getDamageMult();
		HP -= amount;
		if (HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged", this);
			TextContext context = new TextContext(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new MapBuilder<String, Noun>().put("actor", this).build());
			if (SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null, this, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null, this, null));
		}
	}
	
	public void kill() {
		triggerScript("on_death", this);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", this).build());
		game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("die"), context, null, null, this, null));
		if (equipmentComponent.hasEquippedItem()) {
			Item item = equipmentComponent.getEquippedItem();
			inventory.removeItem(item);
			getArea().getInventory().addItem(item);
		}
		isDead = true;
		HP = 0;
		if (isPlayer()) {
			game().eventBus().post(new PlayerDeathEvent());
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
				if (visible) {
					String text = event.getTextVisible();
					if (text != null) {
						game().eventBus().post(new RenderTextEvent(text));
					}
				} else if (event.getTextAudible() != null) {
					String text = event.getTextAudible();
					if (text != null) {
						game().eventBus().post(new RenderTextEvent(text));
					}
				}
			} else {
				if (visible) { // Visible
					if (event.isAction()) {
						targetingComponent.onVisibleAction(event.getAction(), event.getSubject());
					} else if (event.isBark()) {
						if (event.getBark().responseType() == Bark.BarkResponseType.HOSTILE) {
							targetingComponent.addCombatant(event.getTarget());
						}
					}
				} else { // Audible
					if (event.isBark()) {
						if (event.getBark().responseType() == Bark.BarkResponseType.HOSTILE) {
							targetingComponent.addCombatant(event.getTarget());
						}
					}
				}
			}
		}
	}
	
	public void setUsingObject(ObjectComponentUsable object) {
		this.usingObject = object;
	}

	public ObjectComponentUsable getUsingObject() {
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
		List<Action> action = new ArrayList<>();
		if (isActive()) {
			if (getTemplate().getDialogueStart() != null) {
				action.add(new ActionTalk(this));
			}
		} else if (isDead()) {
			action.addAll(inventory.getExternalActions(this, null, subject, false, true, true));
		}
		return action;
	}

	@Override
	public List<Action> visibleActions(Actor subject) {
		return new ArrayList<>();
	}

	public List<Action> availableActions() {
		List<Action> actions = new ArrayList<>();
		if (equipmentComponent.hasEquippedItem()) {
			actions.addAll(equipmentComponent.getEquippedItem().equippedActions(this));
		}
		if (canPerformLocalActions()) {
			for (Actor actor : getArea().getActors()) {
				actions.addAll(actor.localActions(this));
			}
			actions.addAll(getArea().getItemActions());
			for (WorldObject object : getArea().getObjects()) {
				if (!object.isHidden() && (!isUsingObject() || !object.equals(getUsingObject().getObject()))) {
					actions.addAll(object.localActions(this));
				}
			}
		}
		if (isUsingObject()) {
			actions.addAll(getUsingObject().getUsingActions(this));
			if (getUsingObject().getTemplateUsable().userCanPerformParentActions()) {
				actions.addAll(getUsingObject().getObject().localActions(this));
			}
		}
		for (Actor visibleActor : getVisibleActors()) {
			actions.addAll(visibleActor.visibleActions(this));
		}
		for (WorldObject visibleObject : getVisibleObjects()) {
			actions.addAll(visibleObject.visibleActions(this));
		}
		if (canMove()) {
			actions.addAll(getArea().getMoveActions(null, null, "Move"));
		}
		actions.addAll(getArea().getAreaActions(this));
		for (Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		for (ItemApparel item : apparelComponent.getEquippedItems()) {
			actions.addAll(item.equippedActions(this));
		}
		for (Action currentAction : actions) {
			boolean isBlocked = false;
			for (Action blockedAction : blockedActions.keySet()) {
				if (!(blockedActions.get(blockedAction) > 0 && blockedAction.isRepeatMatch(currentAction)) && blockedAction.isBlockedMatch(currentAction)) {
					isBlocked = true;
					break;
				}
			}
			if (isBlocked) {
				currentAction.setDisabled(true);
			}
		}
		actions.add(new ActionEnd());
		return actions;
	}
	
	public void takeTurn() {
		if (!isEnabled() || isDead()) return;
		if (isSleeping()) {
			updateSleep();
			return;
		}
		getEffectComponent().onStartRound();
		getTargetingComponent().updateTurn();
		getBehaviorComponent().update();
		this.actionPointsUsed = 0;
		this.blockedActions.clear();
		this.endTurn = false;
		Action lastAction = null;
		int repeatActionCount = 0;
		while (!endTurn) {
			updatePursueTargets();
			getTargetingComponent().update();
			List<Action> availableActions = availableActions();
			for (Action action : availableActions) {
				if (getActionPoints() - actionPointsUsed < action.actionPoints(this)) {
					action.setDisabled(true);
				}
			}
			Action chosenAction = chooseAction(availableActions);
			actionPointsUsed += chosenAction.actionPoints(this);
			boolean actionIsBlocked = false;
			for (Action repeatAction : blockedActions.keySet()) {
				if (repeatAction.isRepeatMatch(chosenAction)) {
					int countRemaining = blockedActions.get(repeatAction) - 1;
					blockedActions.put(repeatAction, countRemaining);
					actionIsBlocked = true;
					break;
				}
			}
			if (!actionIsBlocked && chosenAction.repeatCount(this) > 0) {
				blockedActions.put(chosenAction, chosenAction.repeatCount(this) - 1);
			}
			if (lastAction != null && chosenAction.isRepeatMatch(lastAction)) {
				repeatActionCount += 1;
			} else {
				repeatActionCount = 0;
			}
			chosenAction.choose(this, repeatActionCount);
			lastAction = chosenAction;
		}
	}
	
	public void endTurn() {
		// May cause issues
		actionPointsUsed = 0;
		endTurn = true;
		if (!playerControlled && shouldIdle()) {
			playIdle();
		}
	}
	
	public Action chooseAction(List<Action> actions) {
		if (playerControlled) {
			return game().menuManager().actionMenu(actions, this);
		} else {
			return UtilityUtils.selectActionByUtility(this, actions, 1);
		}
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

	public Set<Area> getVisibleAreas() {
		if (isUsingObject() && !getUsingObject().getTemplateUsable().userCanSeeOtherAreas()) {
			return Set.of(getArea());
		} else {
			return getArea().getLineOfSightAreas();
		}
	}

	public Set<Actor> getVisibleActors() {
		Set<Actor> visibleActors = new HashSet<>();
		for (Area visibleArea : getVisibleAreas()) {
			for (Actor actor : visibleArea.getActors()) {
				if (actor != this && !actor.isInCover()) {
					visibleActors.add(actor);
				}
			}
		}
		return visibleActors;
	}

	public Set<WorldObject> getVisibleObjects() {
		Set<WorldObject> visibleObjects = new HashSet<>();
		for (Area visibleArea : getVisibleAreas()) {
			for (WorldObject object : visibleArea.getObjects()) {
				if (!object.isHidden()) {
					visibleObjects.add(object);
				}
			}
		}
		return visibleObjects;
	}

	public Set<AttackTarget> getVisibleAttackTargets() {
		Set<AttackTarget> attackTargets = new HashSet<>(getVisibleActors());
		attackTargets.remove(this);
		attackTargets.addAll(getVisibleObjects());
		return attackTargets;
	}

	public boolean canSee(Actor target) {
		return this.equals(target) || getVisibleActors().contains(target);
	}

	public boolean canSee(AttackTarget target) {
		if (target instanceof Actor) {
			return canSee((Actor) target);
		} else {
			return getVisibleAttackTargets().contains(target);
		}
	}

	@Override
	public StatInt getStatInt(String name) {
		if (name.startsWith("damage_resist_")) {
			for (String damageType : game().data().getDamageTypes()) {
				if (name.equals("damage_resist_" + damageType)) {
					return damageResistance.get(damageType);
				}
			}
			return null;
		} else if (name.startsWith("attribute_")) {
			for (Attribute attribute : Attribute.values()) {
				if (name.equals("attribute_" + attribute.toString().toLowerCase())) {
					return attributes.get(attribute);
				}
			}
			return null;
		} else if (name.startsWith("skill_")) {
			for (Skill skill : Skill.values()) {
				if (name.equals("skill_" + skill.toString().toLowerCase())) {
					return skills.get(skill);
				}
			}
			return null;
		}
		return switch (name) {
			case "max_hp" -> maxHP;
			case "action_points" -> actionPoints;
			default -> null;
		};
	}

	@Override
	public StatFloat getStatFloat(String name) {
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
		return null;
	}

	@Override
	public int getValueInt(String name) {
		if (name.startsWith("damage_resist_")) {
			for (String damageType : game().data().getDamageTypes()) {
				if (name.equals("damage_resist_" + damageType)) {
					return damageResistance.get(damageType).value(getTemplate().getDamageResistance(damageType), 0, MAX_DAMAGE_RESIST);
				}
			}
			return 0;
		} else if (name.startsWith("attribute_")) {
			for (Attribute attribute : Attribute.values()) {
				if (name.equals("attribute_" + attribute.toString().toLowerCase())) {
					return attributes.get(attribute).value(getTemplate().getAttribute(attribute), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
				}
			}
			return 0;
		} else if (name.startsWith("skill_")) {
			for (Skill skill : Skill.values()) {
				if (name.equals("skill_" + skill.toString().toLowerCase())) {
					return skills.get(skill).value(getTemplate().getSkill(skill), SKILL_MIN, SKILL_MAX);
				}
			}
			return 0;
		}
		return switch (name) {
			case "max_hp" -> maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP);
			case "hp" -> HP;
			case "action_points" -> actionPoints.value(ACTIONS_PER_TURN, 0, MAX_ACTION_POINTS);
			case "money" -> money;
			default -> 0;
		};
	}

	@Override
	public float getValueFloat(String name) {
		if ("hp_proportion".equals(name)) {
			return ((float) HP) / ((float) getMaxHP());
		}
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		return switch (name) {
			case "enabled" -> isEnabled;
			case "sleeping" -> isSleeping;
			case "in_combat" -> isInCombat();
			case "using_object" -> isUsingObject();
			case "has_equipped_item" -> getEquipmentComponent().hasEquippedItem();
			case "in_cover" -> isInCover();
			case "dead" -> isDead;
			case "active" -> isActive();
			case "can_move" -> canMove();
			case "can_dodge" -> canDodge();
			default -> false;
		};
	}

	@Override
	public String getValueString(String name) {
		return switch (name) {
			case "id" -> getID();
			case "template_id" -> templateID;
			case "area" -> getArea().getID();
			case "room" -> getArea().getRoom().getID();
			default -> null;
		};
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return null;
	}

	@Override
	public void onStatChange(String name) {
		if ("max_hp".equals(name) && HP > getMaxHP()) {
			HP = getMaxHP();
		}
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		switch (name) {
			case "known" -> isKnown = value;
			case "enabled" -> setEnabled(value);
			case "player_controlled" -> playerControlled = value;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {
		switch (name) {
			case "hp" -> HP = value;
			case "money" -> money = value;
		}
	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {
		switch (name) {
			case "area" -> setArea(game().data().getArea(value));
			case "alert_state" -> {
				if (targetingComponent != null) {
					targetingComponent.setAlertState(LoadUtils.stringToEnum(value, TargetingComponent.AlertState.class));
				}
			}
		}
	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {
		switch (name) {
			case "heal" -> heal(amount);
			case "money" -> money += amount;
		}
	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return switch (name) {
			case "equipped_item" -> getEquipmentComponent().getEquippedItem();
			case "using_object" -> getUsingObject();
			case "area" -> getArea();
			default -> null;
		};
	}

	public boolean triggerScript(String trigger, Actor target) {
		Script script = getTemplate().getScript(trigger);
		if (script != null) {
			script.execute(new Context(game(), this, target));
			return true;
		} else {
			return false;
		}
	}

	public void triggerBark(String trigger, Actor target) {
		if (isActive()) {
			Bark bark = getTemplate().getBark(trigger);
			if (bark != null) {
				bark.trigger(this, target);
			}
		}
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
			case "equipped_item" -> this.equipmentComponent.equip((ItemEquippable) game().data().getItemState(saveData.getValueString()));
			case "equipped_apparel" -> this.apparelComponent.equip((ItemApparel) game().data().getItemState(saveData.getValueString()));
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
		if (equipmentComponent.hasEquippedItem()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equipped_item", equipmentComponent.getEquippedItem().getID()));
		}
		for (ItemApparel item : apparelComponent.getEquippedItems()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equipped_apparel", item.getID()));
		}
		if (usingObject != null) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "using_object", usingObject.getID()));
		}
		if (actionPointsUsed != 0) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "action_points_used", actionPointsUsed));
		}
		return state;
	}
	
}
