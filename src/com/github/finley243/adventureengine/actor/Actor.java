package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionEnd;
import com.github.finley243.adventureengine.action.ActionMove;
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
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

import java.util.*;

public class Actor extends GameInstanced implements Noun, Physical, StatHolder, AttackTarget {

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
	private final Map<Damage.DamageType, StatInt> damageResistance;
	private final boolean startDisabled;
	private boolean isEnabled;
	private final boolean startDead;
	private boolean isDead;
	private boolean isSleeping;
	private boolean endTurn;
	private final StatInt actionPoints;
	private int actionPointsUsed;
	private final Map<Action, Integer> blockedActions;
	private final Map<Attribute, StatInt> attributes;
	private final Map<Skill, StatInt> skills;
	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final ApparelComponent apparelComponent;
	private final EquipmentComponent equipmentComponent;
	private VendorComponent vendorComponent;
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
		this.maxHP = new StatInt(this);
		this.damageResistance = new EnumMap<Damage.DamageType, StatInt>(Damage.DamageType.class);
		for (Damage.DamageType damageType : Damage.DamageType.values()) {
			this.damageResistance.put(damageType, new StatInt(this));
		}
		this.actionPoints = new StatInt(this);
		this.inventory = new Inventory(game, this);
		this.apparelComponent = new ApparelComponent(this);
		this.equipmentComponent = new EquipmentComponent(this);
		this.attributes = new EnumMap<>(Attribute.class);
		for (Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new StatInt(this));
		}
		this.skills = new EnumMap<>(Skill.class);
		for (Skill skill : Skill.values()) {
			this.skills.put(skill, new StatInt(this));
		}
		this.effectComponent = new EffectComponent(this);
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
		if (getTemplate().getLootTable() != null) {
			inventory.addItems(getTemplate().getLootTable().generateItems(game()));
		}
		if (getTemplate().isVendor()) {
			this.vendorComponent = new VendorComponent(this);
		} else {
			this.vendorComponent = null;
		}
		if (vendorComponent != null) {
			vendorComponent.generateInventory();
		}
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
		if(!isProperName()) {
			return LangUtils.addArticle(getNameState() + getName(), !isKnown());
		} else {
			return getNameState() + getName();
		}
	}

	private String getNameState() {
		if(isDead()) {
			return "dead ";
		} else {
			return "";
		}
	}

	public ActorTemplate getTemplate() {
		return game().data().getActorTemplate(templateID);
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
		boolean newRoom = getArea() == null || !getArea().getRoom().equals(area.getRoom());
		if(this.area != null) {
			this.area.removeActor(this);
		}
		this.area = area;
		area.addActor(this);
		if (isPlayer()) {
			game().eventBus().post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
			if(newRoom) {
				getArea().getRoom().triggerScript("on_player_enter", this, this);
			}
			getArea().triggerScript("on_player_enter", this, this);
		}
	}

	public void onMove(Area lastArea) {
		if (isPlayer()) {
			boolean isRoomChange = !lastArea.getRoom().equals(getArea().getRoom());
			boolean isAreaChange = isRoomChange || !lastArea.equals(getArea());
			if(isRoomChange && getArea().getRoom().getDescription() != null) {
				SceneManager.trigger(game(), this, this, getArea().getRoom().getDescription());
				getArea().getRoom().setKnown();
				for (Area area : getArea().getRoom().getAreas()) {
					area.setKnown();
				}
			}
			if(isAreaChange && getArea().getDescription() != null) {
				SceneManager.trigger(game(), this, this, getArea().getDescription());
				getArea().setKnown();
			}
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean enable) {
		if (area == null) throw new UnsupportedOperationException("Attempt to enable actor in null area: " + this.getID());
		if(isEnabled != enable) {
			isEnabled = enable;
			if(enable) {
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
		return !isUsingObject();
	}

	public boolean isInCover() {
		return isUsingObject() && getUsingObject().getTemplateUsable().userIsInCover();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		switch (name) {
			case "equippedItem":
				return getEquipmentComponent().getEquippedItem();
			case "usingObject":
				return getUsingObject();
		}
		return null;
	}

	public ApparelComponent getApparelComponent() {
		return apparelComponent;
	}

	public List<Limb> getLimbs() {
		return getTemplate().getLimbs();
	}
	
	public EquipmentComponent getEquipmentComponent() {
		return equipmentComponent;
	}

	public int getMoney() {
		return money;
	}
	
	public void adjustMoney(int value) {
		money += value;
	}

	public EffectComponent getEffectComponent() {
		return effectComponent;
	}

	public BehaviorComponent getBehaviorComponent() {
		return behaviorComponent;
	}

	public int getHP() {
		return HP;
	}

	public float getHPProportion() {
		return ((float) HP) / ((float) getMaxHP());
	}

	public int getMaxHP() {
		return maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP);
	}

	public int getDamageResistance(Damage.DamageType damageType) {
		return damageResistance.get(damageType).value(getTemplate().getDamageResistance(damageType), 0, MAX_DAMAGE_RESIST);
	}

	public int getActionPoints() {
		return actionPoints.value(ACTIONS_PER_TURN, 0, MAX_ACTION_POINTS);
	}
	
	public void heal(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount = Math.min(amount, getMaxHP() - HP);
		HP += amount;
		Context context = new Context(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new NounMapper().put("actor", this).build());
		if(SHOW_HP_CHANGES) {
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
		if(damage.getLimb() != null) {
			damageLimb(damage, damage.getLimb());
		} else {
			damageDirect(damage);
		}
	}

	private void damageDirect(Damage damage) {
		for (String effectID : damage.getTargetEffects()) {
			effectComponent.addEffect(game().data().getEffect(effectID));
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
			Context context = new Context(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new NounMapper().put("actor", this).build());
			if (SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null, this, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null, this, null));
		}
	}

	private void damageLimb(Damage damage, Limb limb) {
		for (String effectID : damage.getTargetEffects()) {
			effectComponent.addEffect(game().data().getEffect(effectID));
		}
		int amount = damage.getAmount();
		//amount -= apparelComponent.getDamageResistance(limb.getApparelSlot(), damage.getType()) * damage.getArmorMult();
		amount -= getDamageResistance(damage.getType()) * damage.getArmorMult();
		if(amount < 0) amount = 0;
		if(amount > 0) {
			limb.applyEffects(this);
		}
		amount *= limb.getDamageMult();
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged", this);
			Context context = new Context(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new NounMapper().put("actor", this).build());
			if(SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null, this, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null, this, null));
		}
	}
	
	public void kill() {
		triggerScript("on_death", this);
		Context context = new Context(new NounMapper().put("actor", this).build());
		game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("die"), context, null, null, this, null));
		dropEquippedItem();
		isDead = true;
		if (isPlayer()) {
			game().eventBus().post(new PlayerDeathEvent());
		}
	}

	public void dropEquippedItem() {
		if(equipmentComponent.hasEquippedItem()) {
			Item item = equipmentComponent.getEquippedItem();
			inventory.removeItem(item);
			getArea().getInventory().addItem(item);
			Context context = new Context(new NounMapper().put("actor", this).put("item", item).build());
			game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("drop"), context, null, null, this, null));
		}
	}

	public void dropEquippedItemForce() {
		if(equipmentComponent.hasEquippedItem()) {
			Item item = equipmentComponent.getEquippedItem();
			inventory.removeItem(item);
			Area landingArea = MathUtils.selectRandomFromSet(getArea().getMovableAreas());
			landingArea.getInventory().addItem(item);
			Context context = new Context(Map.of("area", landingArea.getRelativeName(getArea())), new NounMapper().put("actor", this).put("item", item).build());
			game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("forceDrop"), context, null, null, this, null));
		}
	}

	public String getConditionDescription() {
		float hpProportion = getHPProportion();
		if(hpProportion == 1.0f) {
			return "in perfect condition";
		} else if(hpProportion >= 0.9f) {
			return "barely scratched";
		} else if(hpProportion >= 0.75f) {
			return "lightly injured";
		} else if(hpProportion >= 0.55f) {
			return "moderately injured";
		} else if(hpProportion >= 0.35f) {
			return "heavily injured";
		} else if(hpProportion >= 0.15f) {
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
		if(sleepCounter != 0) {
			sleepCounter -= 1;
			if (sleepCounter <= 0) {
				isSleeping = false;
				sleepCounter = 0;
			}
		}
	}
	
	public void onSensoryEvent(SensoryEvent event, boolean visible) {
		if(isActive() && isEnabled()) {
			if (isPlayer()) {
				if (visible) {
					String text = event.getTextVisible();
					if (text != null) {
						game().eventBus().post(new RenderTextEvent(text));
					}
				} else if(event.getTextAudible() != null) {
					String text = event.getTextAudible();
					if (text != null) {
						game().eventBus().post(new RenderTextEvent(text));
					}
				}
			} else {
				if (visible) {
					if (event.isAction()) {
						targetingComponent.onVisibleAction(event.getAction(), event.getSubject());
						switch(event.getAction().responseType()) {
							case STEAL:
								triggerBark("alert_steal", event.getSubject());
								targetingComponent.addCombatant(event.getSubject());
								break;
							case ATTACK:
								triggerBark("alert_attack", event.getSubject());
								targetingComponent.addCombatant(event.getSubject());
								break;
							case BREAK_LOCK:
								triggerBark("alert_break_lock", event.getSubject());
								targetingComponent.addCombatant(event.getSubject());
								break;
							case NONE:
							default:
						}
					} else if (event.isBark()) {
						switch(event.getBark().responseType()) {
							case HOSTILE:
								targetingComponent.addCombatant(event.getTarget());
								break;
							case NONE:
							default:
						}
					}
					if (event.getAction() instanceof ActionMove) {
						targetingComponent.updateTargetArea(event.getSubject(), ((ActionMove) event.getAction()).getDestinationArea());
					}
				} else {
					if (event.isBark()) {
						switch(event.getBark().responseType()) {
							case HOSTILE:
								if (getTemplate().isEnforcer()) {
									targetingComponent.addCombatant(event.getTarget());
								}
								break;
							case NONE:
							default:
						}
					}
				}
			}
		}
	}
	
	public void startUsingObject(ObjectComponentUsable object) {
		this.usingObject = object;
	}
	
	public void stopUsingObject() {
		this.usingObject = null;
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
	
	public boolean hasMeleeTargets() {
		return targetingComponent.hasTargetsOfTypeInArea(TargetingComponent.DetectionState.HOSTILE, getArea());
	}

	public boolean hasWeapon() {
		for(Item item : inventory.getItems()) {
			if(item instanceof ItemWeapon) {
				return true;
			}
		}
		return false;
	}

	public TargetingComponent getTargetingComponent() {
		return targetingComponent;
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
		if(isActive()) {
			if(getTemplate().getDialogueStart() != null) {
				action.add(new ActionTalk(this));
			}
			if(vendorComponent != null && behaviorComponent != null && behaviorComponent.isVendingEnabled()) {
				action.addAll(vendorComponent.getActions(subject));
			}
		} else if(isDead()) {
			action.addAll(inventory.getExternalActions(this, null, subject, false));
		}
		return action;
	}

	public List<Action> availableActions() {
		List<Action> actions = new ArrayList<>();
		if(equipmentComponent.hasEquippedItem()) {
			actions.addAll(equipmentComponent.getEquippedItem().equippedActions(this));
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		actions.addAll(getArea().getItemActions());
		for(WorldObject object : getArea().getObjects()) {
			if (!object.isHidden()) {
				actions.addAll(object.localActions(this));
			}
		}
		if(isUsingObject()) {
			actions.addAll(getUsingObject().getUsingActions(this));
		}
		if(canMove()) {
			actions.addAll(getArea().getMoveActions());
		}
		actions.addAll(getArea().getAreaActions(this));
		for(Item item : inventory.getItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		for(ItemApparel item : apparelComponent.getEquippedItems()) {
			actions.addAll(item.equippedActions(this));
		}
		for(Action currentAction : actions) {
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
		if (getEffectComponent() != null) {
			getEffectComponent().onStartTurn();
		}
		if (getTargetingComponent() != null) {
			getTargetingComponent().updateTurn();
		}
		if (getBehaviorComponent() != null) {
			getBehaviorComponent().update();
		}
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
		while(itr.hasNext()) {
			AreaTarget target = itr.next();
			target.update(this);
			if(target.shouldRemove()) {
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
		for(Area visibleArea : getVisibleAreas()) {
			for(Actor actor : visibleArea.getActors()) {
				if(actor != this && !actor.isInCover()) {
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
		for (WorldObject visibleObject : getVisibleObjects()) {
			if (visibleObject instanceof AttackTarget) {
				attackTargets.add((AttackTarget) visibleObject);
			}
		}
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
		switch (name) {
			case "maxHP":
				return maxHP;
			case "actionPoints":
				return actionPoints;
			case "damageResistPhysical":
				return damageResistance.get(Damage.DamageType.PHYSICAL);
			case "damageResistThermal":
				return damageResistance.get(Damage.DamageType.THERMAL);
			case "damageResistChemical":
				return damageResistance.get(Damage.DamageType.CHEMICAL);
			case "damageResistExplosive":
				return damageResistance.get(Damage.DamageType.EXPLOSIVE);
			case "damageResistElectrical":
				return damageResistance.get(Damage.DamageType.ELECTRICAL);
			case "body":
				return attributes.get(Attribute.BODY);
			case "intelligence":
				return attributes.get(Attribute.INTELLIGENCE);
			case "charisma":
				return attributes.get(Attribute.CHARISMA);
			case "dexterity":
				return attributes.get(Attribute.DEXTERITY);
			case "agility":
				return attributes.get(Attribute.AGILITY);
			case "melee":
				return skills.get(Skill.MELEE);
			case "throwing":
				return skills.get(Skill.THROWING);
			case "intimidation":
				return skills.get(Skill.INTIMIDATION);
			case "software":
				return skills.get(Skill.SOFTWARE);
			case "hardware":
				return skills.get(Skill.HARDWARE);
			case "medicine":
				return skills.get(Skill.MEDICINE);
			case "barter":
				return skills.get(Skill.BARTER);
			case "persuasion":
				return skills.get(Skill.PERSUASION);
			case "deception":
				return skills.get(Skill.DECEPTION);
			case "handguns":
				return skills.get(Skill.HANDGUNS);
			case "longArms":
				return skills.get(Skill.LONG_ARMS);
			case "lockpick":
				return skills.get(Skill.LOCKPICK);
			case "stealth":
				return skills.get(Skill.STEALTH);
			default:
				return null;
		}
	}

	@Override
	public StatFloat getStatFloat(String name) {
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
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
		switch (name) {
			case "maxHP":
				return maxHP.value(getTemplate().getMaxHP(), 0, MAX_HP);
			case "HP":
				return HP;
			case "actionPoints":
				return actionPoints.value(ACTIONS_PER_TURN, 0, MAX_ACTION_POINTS);
			case "money":
				return money;
			case "damageResistPhysical":
				return damageResistance.get(Damage.DamageType.PHYSICAL).value(getTemplate().getDamageResistance(Damage.DamageType.PHYSICAL), 0, MAX_DAMAGE_RESIST);
			case "damageResistThermal":
				return damageResistance.get(Damage.DamageType.THERMAL).value(getTemplate().getDamageResistance(Damage.DamageType.THERMAL), 0, MAX_DAMAGE_RESIST);
			case "damageResistChemical":
				return damageResistance.get(Damage.DamageType.CHEMICAL).value(getTemplate().getDamageResistance(Damage.DamageType.CHEMICAL), 0, MAX_DAMAGE_RESIST);
			case "damageResistExplosive":
				return damageResistance.get(Damage.DamageType.EXPLOSIVE).value(getTemplate().getDamageResistance(Damage.DamageType.EXPLOSIVE), 0, MAX_DAMAGE_RESIST);
			case "damageResistElectrical":
				return damageResistance.get(Damage.DamageType.ELECTRICAL).value(getTemplate().getDamageResistance(Damage.DamageType.ELECTRICAL), 0, MAX_DAMAGE_RESIST);
			case "body":
				return attributes.get(Attribute.BODY).value(getTemplate().getAttribute(Attribute.BODY), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
			case "intelligence":
				return attributes.get(Attribute.INTELLIGENCE).value(getTemplate().getAttribute(Attribute.INTELLIGENCE), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
			case "charisma":
				return attributes.get(Attribute.CHARISMA).value(getTemplate().getAttribute(Attribute.CHARISMA), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
			case "dexterity":
				return attributes.get(Attribute.DEXTERITY).value(getTemplate().getAttribute(Attribute.DEXTERITY), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
			case "agility":
				return attributes.get(Attribute.AGILITY).value(getTemplate().getAttribute(Attribute.AGILITY), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
			case "melee":
				return skills.get(Skill.MELEE).value(getTemplate().getSkill(Skill.MELEE), SKILL_MIN, SKILL_MAX);
			case "throwing":
				return skills.get(Skill.THROWING).value(getTemplate().getSkill(Skill.THROWING), SKILL_MIN, SKILL_MAX);
			case "intimidation":
				return skills.get(Skill.INTIMIDATION).value(getTemplate().getSkill(Skill.INTIMIDATION), SKILL_MIN, SKILL_MAX);
			case "software":
				return skills.get(Skill.SOFTWARE).value(getTemplate().getSkill(Skill.SOFTWARE), SKILL_MIN, SKILL_MAX);
			case "hardware":
				return skills.get(Skill.HARDWARE).value(getTemplate().getSkill(Skill.HARDWARE), SKILL_MIN, SKILL_MAX);
			case "medicine":
				return skills.get(Skill.MEDICINE).value(getTemplate().getSkill(Skill.MEDICINE), SKILL_MIN, SKILL_MAX);
			case "barter":
				return skills.get(Skill.BARTER).value(getTemplate().getSkill(Skill.BARTER), SKILL_MIN, SKILL_MAX);
			case "persuasion":
				return skills.get(Skill.PERSUASION).value(getTemplate().getSkill(Skill.PERSUASION), SKILL_MIN, SKILL_MAX);
			case "deception":
				return skills.get(Skill.DECEPTION).value(getTemplate().getSkill(Skill.DECEPTION), SKILL_MIN, SKILL_MAX);
			case "handguns":
				return skills.get(Skill.HANDGUNS).value(getTemplate().getSkill(Skill.HANDGUNS), SKILL_MIN, SKILL_MAX);
			case "longArms":
				return skills.get(Skill.LONG_ARMS).value(getTemplate().getSkill(Skill.LONG_ARMS), SKILL_MIN, SKILL_MAX);
			case "lockpick":
				return skills.get(Skill.LOCKPICK).value(getTemplate().getSkill(Skill.LOCKPICK), SKILL_MIN, SKILL_MAX);
			case "stealth":
				return skills.get(Skill.STEALTH).value(getTemplate().getSkill(Skill.STEALTH), SKILL_MIN, SKILL_MAX);
		}
		return 0;
	}

	@Override
	public float getValueFloat(String name) {
		if ("hpProportion".equals(name)) {
			return ((float) HP) / ((float) getMaxHP());
		}
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		switch (name) {
			case "enabled":
				return isEnabled;
			case "sleeping":
				return isSleeping;
			case "inCombat":
				return isInCombat();
			case "usingObject":
				return isUsingObject();
			case "dead":
				return isDead;
			case "active":
				return isActive();
		}
		return false;
	}

	@Override
	public String getValueString(String name) {
		switch (name) {
			case "id":
				return getID();
			case "templateID":
				return templateID;
			case "area":
				return getArea().getID();
			case "room":
				return getArea().getRoom().getID();
		}
		return null;
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return null;
	}

	@Override
	public void onStatChange() {
		if(HP > getMaxHP()) {
			HP = getMaxHP();
		}
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		switch (name) {
			case "known":
				isKnown = value;
				break;
			case "enabled":
				setEnabled(value);
				break;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {
		switch (name) {
			case "hp":
				HP = value;
				break;
			case "money":
				money = value;
				break;
		}
	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {
		switch (name) {
			case "area":
				setArea(game().data().getArea(value));
				break;
			case "alertState":
				if (targetingComponent != null) {
					targetingComponent.setAlertState(LoadUtils.stringToEnum(value, TargetingComponent.AlertState.class));
				}
				break;
		}
	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {
		switch (name) {
			case "heal":
				heal(amount);
				break;
			case "damage":
				damageDirect(new Damage(Damage.DamageType.PHYSICAL, amount, null, 1.0f, new ArrayList<>()));
				break;
			case "damageIgnoreArmor":
				damageDirect(new Damage(Damage.DamageType.PHYSICAL, amount, null, 0.0f, new ArrayList<>()));
				break;
			case "money":
				money += amount;
				break;
		}
	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public void triggerEffect(String name) {
		if (name.equals("dropEquipped")) {
			dropEquippedItem();
		} else if (name.equals("dropEquippedForce")) {
			dropEquippedItemForce();
		}
	}

	public boolean triggerScript(String trigger, Actor target) {
		Script script = getTemplate().getScript(trigger);
		if (script != null) {
			script.execute(new ContextScript(game(), this, target, null, null));
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
		switch(saveData.getParameter()) {
			case "hp":
				this.HP = saveData.getValueInt();
				break;
			case "isEnabled":
				setEnabled(saveData.getValueBoolean());
			case "isDead":
				this.isDead = saveData.getValueBoolean();
				break;
			case "isKnown":
				this.isKnown = saveData.getValueBoolean();
				break;
			case "area":
				if(saveData.getValueString() == null) {
					this.area = null;
				} else {
					this.area = game().data().getArea(saveData.getValueString());
				}
				break;
			case "targeting":
				//if(targetingComponent != null) targetingComponent.loadState(saveData);
				break;
			case "inventory":
				if (inventory != null) inventory.loadState(saveData);
				break;
			case "equippedItem":
				this.equipmentComponent.equip((ItemEquippable) game().data().getItemState(saveData.getValueString()));
				break;
			case "equippedApparel":
				this.apparelComponent.equip((ItemApparel) game().data().getItemState(saveData.getValueString()));
				break;
			case "usingObject":
				// TODO - Fix broken save data for using object (needs two IDs, the object and the component)
				//startUsingObject((ObjectComponentUsable) game().data().getObject(saveData.getValueString()).getComponent());
				break;
			case "actionPointsUsed":
				this.actionPointsUsed = saveData.getValueInt();
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(isKnown) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "isKnown", isKnown));
		}
		if(isEnabled == startDisabled) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "isEnabled", isEnabled));
		}
		if(isDead != startDead) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "isDead", isDead));
		}
		if(area != defaultArea) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "area", (area == null ? null : area.getID())));
		}
		if(inventory != null) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "inventory", inventory.saveState()));
		}
		if (targetingComponent != null) {
			//state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "targeting", targetingComponent.saveState()));
		}
		if(equipmentComponent.hasEquippedItem()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equippedItem", equipmentComponent.getEquippedItem().getID()));
		}
		for(ItemApparel item : apparelComponent.getEquippedItems()) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "equippedApparel", item.getID()));
		}
		if(usingObject != null) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "usingObject", usingObject.getID()));
		}
		if(actionPointsUsed != 0) {
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "actionPointsUsed", actionPointsUsed));
		}
		return state;
	}

	@Override
	public String toString() {
		return getID();
	}
	
	@Override
	public int hashCode() {
		return getID().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Actor && this.getID().equals(((Actor) o).getID());
	}
	
}
