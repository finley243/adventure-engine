package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.*;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.ai.*;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.actor.component.*;
import com.github.finley243.adventureengine.effect.moddable.*;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemApparel;
import com.github.finley243.adventureengine.item.ItemEquippable;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Actor extends GameInstanced implements Noun, Physical, Moddable {

	public static final boolean SHOW_HP_CHANGES = true;
	public static final int ACTIONS_PER_TURN = 2;
	public static final int MOVES_PER_TURN = 2;
	public static final int ATTRIBUTE_MIN = 1;
	public static final int ATTRIBUTE_MAX = 10;
	public static final int SKILL_MIN = 1;
	public static final int SKILL_MAX = 10;
	public static final int MAX_HP = 1000;
	public static final int MAX_ACTION_POINTS = 10;
	
	public enum Attribute {
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	public enum Skill {
		// BODY
		MELEE(Attribute.BODY),
		THROWING(Attribute.BODY),
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

	private final ActorTemplate template;
	private final String ID;
	// If isKnown = true, use definite article, else use indefinite article
	private boolean isKnown;
	private final Area defaultArea;
	private Area area;
	private final ModdableStatInt maxHP;
	private int HP;
	private final boolean startDisabled;
	private boolean isEnabled;
	private final boolean startDead;
	private boolean isDead;
	private boolean isSleeping;
	private boolean endTurn;
	private final ModdableStatInt actionPoints;
	private int actionPointsUsed;
	private final Map<Action, Integer> blockedActions;
	private final EnumMap<Attribute, ModdableStatInt> attributes;
	private final EnumMap<Skill, ModdableStatInt> skills;
	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final ApparelComponent apparelComponent;
	private final EquipmentComponent equipmentComponent;
	private final VendorComponent vendorComponent;
	private final TargetingComponent targetingComponent;
	private final BehaviorComponent behaviorComponent;
	private int money;
	private UsableObject usingObject;
	private final Set<AreaTarget> areaTargets;
	private final InvestigateTarget investigateTarget;
	private int sleepCounter;
	private boolean playerControlled;

	public Actor(Game game, String ID, Area area, ActorTemplate template, List<Behavior> behaviors, boolean startDead, boolean startDisabled, boolean playerControlled) {
		super(game);
		this.ID = ID;
		this.defaultArea = area;
		this.area = area;
		this.template = template;
		this.targetingComponent = new TargetingComponent(this);
		this.areaTargets = new HashSet<>();
		this.investigateTarget = new InvestigateTarget();
		this.startDead = startDead;
		this.isDead = startDead;
		this.maxHP = new ModdableStatInt(this);
		this.actionPoints = new ModdableStatInt(this);
		if(!startDead) {
			HP = this.maxHP.value(template.getMaxHP(game()), 0, MAX_HP);
		}
		this.inventory = new Inventory(game, this);
		this.apparelComponent = new ApparelComponent(this);
		this.equipmentComponent = new EquipmentComponent(this);
		if(template.isVendor()) {
			this.vendorComponent = new VendorComponent(this);
		} else {
			this.vendorComponent = null;
		}
		this.attributes = new EnumMap<>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new ModdableStatInt(this));
		}
		this.skills = new EnumMap<>(Skill.class);
		for(Skill skill : Skill.values()) {
			this.skills.put(skill, new ModdableStatInt(this));
		}
		this.effectComponent = new EffectComponent(this);
		this.behaviorComponent = new BehaviorComponent(this, behaviors);
		this.blockedActions = new HashMap<>();
		this.startDisabled = startDisabled;
		this.playerControlled = playerControlled;
		setEnabled(!startDisabled);
	}

	public void newGameInit() {
		if(template.getLootTable(game()) != null) {
			inventory.addItems(template.getLootTable(game()).generateItems(game()));
		}
		if(vendorComponent != null) {
			vendorComponent.generateInventory();
		}
	}

	public boolean isPlayer() {
		return this.equals(game().data().getPlayer());
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return template.getName(game());
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
		return template;
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
		return template.isProperName(game());
	}
	
	@Override
	public Pronoun getPronoun() {
		return template.getPronoun(game());
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
				getArea().getRoom().triggerScript("on_player_enter", this);
			}
			getArea().triggerScript("on_player_enter", this);
		}
	}

	public void onMove(Area lastArea) {
		if (isPlayer()) {
			boolean isRoomChange = !lastArea.getRoom().equals(getArea().getRoom());
			boolean isAreaChange = isRoomChange || !lastArea.equals(getArea());
			if(isRoomChange && getArea().getRoom().getDescription() != null) {
				SceneManager.trigger(game(), this, getArea().getRoom().getDescription());
				getArea().getRoom().setKnown();
				for (Area area : getArea().getRoom().getAreas()) {
					area.setKnown();
				}
			}
			if(isAreaChange && getArea().getDescription() != null) {
				SceneManager.trigger(game(), this, getArea().getDescription());
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
		return attributes.get(attribute).value(template.getAttribute(game(), attribute), ATTRIBUTE_MIN, ATTRIBUTE_MAX);
	}

	public int getSkill(Skill skill) {
		return skills.get(skill).value(template.getSkill(game(), skill), SKILL_MIN, SKILL_MAX);
	}
	
	public String getTopicID() {
		return template.getTopic(game());
	}
	
	public Faction getFaction() {
		return game().data().getFaction(template.getFaction(game()));
	}
	
	public boolean canMove() {
		return !isUsingObject();
	}

	public boolean isInCover() {
		return isUsingObject() && getUsingObject().userInCover();
	}
	
	public Inventory inventory() {
		return inventory;
	}

	public ApparelComponent apparelComponent() {
		return apparelComponent;
	}

	public List<Limb> getLimbs() {
		return template.getLimbs(game());
	}
	
	public EquipmentComponent equipmentComponent() {
		return equipmentComponent;
	}

	public int getMoney() {
		return money;
	}
	
	public void adjustMoney(int value) {
		money += value;
	}

	public EffectComponent effectComponent() {
		return effectComponent;
	}

	public BehaviorComponent behaviorComponent() {
		return behaviorComponent;
	}

	public int getHP() {
		return HP;
	}

	public float getHPProportion() {
		return ((float) HP) / ((float) getMaxHP());
	}

	public int getMaxHP() {
		return maxHP.value(template.getMaxHP(game()), 0, MAX_HP);
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
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor gain$s_actor $amount HP", context, null, null));
		}
		game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null));
	}
	
	public void damage(Damage damage, Limb limb) {
		if(limb != null) {
			damageLimb(damage, limb);
		} else {
			damageDirect(damage);
		}
	}

	private void damageDirect(Damage damage) {
		int amount = damage.getAmount();
		amount -= apparelComponent.getDamageResistance(ApparelComponent.ApparelSlot.TORSO, damage.getType()) * damage.getArmorMult();
		HP -= amount;
		if (HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged");
			Context context = new Context(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new NounMapper().put("actor", this).build());
			if (SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null));
		}
	}

	private void damageLimb(Damage damage, Limb limb) {
		int amount = damage.getAmount();
		amount -= apparelComponent.getDamageResistance(limb.getApparelSlot(), damage.getType()) * damage.getArmorMult();
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
			triggerScript("on_damaged");
			Context context = new Context(Map.of("amount", String.valueOf(amount), "condition", this.getConditionDescription()), new NounMapper().put("actor", this).build());
			if(SHOW_HP_CHANGES) {
				game().eventBus().post(new SensoryEvent(getArea(), "$_actor lose$s_actor $amount HP", context, null, null));
			}
			game().eventBus().post(new SensoryEvent(getArea(), "$_actor $is_actor $condition", context, null, null));
		}
	}
	
	public void kill() {
		triggerScript("on_death");
		Context context = new Context(new NounMapper().put("actor", this).build());
		game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("die"), context, null, null));
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
			Item.itemToObject(game(), item, 1, getArea());
			Context context = new Context(new NounMapper().put("actor", this).put("item", item).build());
			game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("drop"), context, null, null));
		}
	}

	public void dropEquippedItemForce() {
		if(equipmentComponent.hasEquippedItem()) {
			Item item = equipmentComponent.getEquippedItem();
			inventory.removeItem(item);
			Area landingArea = MathUtils.selectRandomFromSet(getArea().getMovableAreas());
			Item.itemToObject(game(), item, 1, landingArea);
			Context context = new Context(Map.of("area", landingArea.getRelativeName(getArea())), new NounMapper().put("actor", this).put("item", item).build());
			game().eventBus().post(new SensoryEvent(getArea(), Phrases.get("forceDrop"), context, null, null));
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

	public void setSleeping(boolean state) {
		this.isSleeping = state;
	}
	
	public boolean isActive() {
		return !isDead() && !isSleeping();
	}

	public void startSleep(int duration) {
		this.sleepCounter = duration;
		setSleeping(true);
	}

	private void updateSleep() {
		if(sleepCounter != 0) {
			this.sleepCounter -= DateTimeController.MINUTES_PER_ROUND;
			if (sleepCounter <= 0) {
				setSleeping(false);
				this.sleepCounter = 0;
			}
		}
	}
	
	public void onSensoryEvent(SensoryEvent event, boolean visible) {
		if(isActive() && isEnabled()) {
			if (isPlayer()) {
				if (visible) {
					game().eventBus().post(new RenderTextEvent(event.getTextVisible()));
				} else if(event.getTextAudible() != null) {
					game().eventBus().post(new RenderTextEvent(event.getTextAudible()));
				}
			} else {
				if (visible) {
					if (event.getAction() instanceof ActionMove) {
						targetingComponent.updateCombatantArea(event.getSubject(), ((ActionMove) event.getAction()).getDestinationArea());
					}
				} else {
					if (event.getResponseType() == SensoryEvent.ResponseType.INVESTIGATE) {
						investigateTarget.setTargetArea(event.getOrigins()[ThreadLocalRandom.current().nextInt(event.getOrigins().length)]);
						triggerScript("on_investigate_start");
					}
				}
			}
		}
	}
	
	public void startUsingObject(UsableObject object) {
		this.usingObject = object;
	}
	
	public void stopUsingObject() {
		this.usingObject = null;
	}

	public UsableObject getUsingObject() {
		return usingObject;
	}
	
	public boolean isUsingObject() {
		return this.usingObject != null;
	}
	
	public boolean isInCombat() {
		return targetingComponent.hasCombatants();
	}
	
	public boolean hasMeleeTargets() {
		return targetingComponent.hasCombatantsInArea(getArea());
	}

	public boolean hasWeapon() {
		for(Item item : inventory.getItems()) {
			if(item instanceof ItemWeapon) {
				return true;
			}
		}
		return false;
	}

	public TargetingComponent targetingComponent() {
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
			if(template.getTopic(game()) != null) {
				action.add(new ActionTalk(this));
			}
			if(vendorComponent != null && behaviorComponent != null && behaviorComponent.isVendingEnabled()) {
				action.addAll(vendorComponent.getActions(subject));
			}
		} else if(isDead()) {
			action.addAll(inventory.getExternalActions(this, subject));
		}
		return action;
	}

	public List<Action> availableActions(){
		List<Action> actions = new ArrayList<>();
		if(equipmentComponent.hasEquippedItem()) {
			actions.addAll(equipmentComponent.getEquippedItem().equippedActions(this));
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(WorldObject object : getArea().getObjects()) {
			if (!object.isHidden()) {
				actions.addAll(object.localActions(this));
			}
		}
		if(isUsingObject()) {
			actions.addAll(getUsingObject().usingActions());
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
				currentAction.disable();
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
		if (effectComponent() != null) {
			effectComponent().onStartTurn();
		}
		if (targetingComponent() != null) {
			targetingComponent().updateTurn();
		}
		investigateTarget.nextTurn(this);
		if (behaviorComponent() != null) {
			behaviorComponent().update();
		}
		this.actionPointsUsed = 0;
		this.blockedActions.clear();
		this.endTurn = false;
		Action lastAction = null;
		int repeatActionCount = 0;
		while (!endTurn) {
			updatePursueTargets();
			targetingComponent().update();
			investigateTarget.update(this);
			List<Action> availableActions = availableActions();
			for (Action action : availableActions) {
				if (getActionPoints() - actionPointsUsed < action.actionPoints(this)) {
					action.disable();
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
	}
	
	public Action chooseAction(List<Action> actions) {
		if (playerControlled) {
			return game().menuManager().actionMenu(actions, this);
		} else {
			return UtilityUtils.selectActionByUtility(this, actions, 1);
		}
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

	public Set<Actor> getVisibleActors() {
		Set<Actor> visibleActors = new HashSet<>();
		Set<Area> visibleAreas = getArea().getVisibleAreas(this);
		for(Area visibleArea : visibleAreas) {
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
		Set<Area> visibleAreas = getArea().getVisibleAreas(this);
		for (Area visibleArea : visibleAreas) {
			for (WorldObject object : visibleArea.getObjects()) {
				if (!object.isHidden()) {
					visibleObjects.add(object);
				}
			}
		}
		return visibleObjects;
	}
	
	public boolean canSee(Actor target) {
		return this == target || getArea().getVisibleAreas(this).contains(target.getArea()) && !target.isInCover();
	}

	@Override
	public ModdableStatInt getStatInt(String name) {
		switch(name) {
			case "maxHP":
				return maxHP;
			case "actionPoints":
				return actionPoints;
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
	public ModdableStatFloat getStatFloat(String name) {
		return null;
	}

	@Override
	public ModdableStatBoolean getStatBoolean(String name) {
		return null;
	}

	@Override
	public ModdableEffectList getStatEffects(String name) {
		return null;
	}

	@Override
	public void onStatChange() {
		if(HP > getMaxHP()) {
			HP = getMaxHP();
		}
	}

	@Override
	public void modifyState(String name, int amount) {
		switch (name) {
			case "heal":
				heal(amount);
				break;
			case "damage":
				damageDirect(new Damage(Damage.DamageType.PHYSICAL, amount, 1.0f));
				break;
			case "damageIgnoreArmor":
				damageDirect(new Damage(Damage.DamageType.PHYSICAL, amount, 0.0f));
				break;
		}
	}

	@Override
	public void triggerEffect(String name) {
		if (name.equals("dropEquipped")) {
			dropEquippedItem();
		} else if (name.equals("dropEquippedForce")) {
			dropEquippedItemForce();
		}
	}

	public boolean triggerScript(String entryPoint) {
		if(template.getScripts().containsKey(entryPoint)) {
			template.getScripts().get(entryPoint).execute(this);
			return true;
		} else {
			return false;
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
				if(targetingComponent != null) targetingComponent.loadState(saveData);
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
				startUsingObject((UsableObject) game().data().getObject(saveData.getValueString()));
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
			state.add(new SaveData(SaveData.DataType.ACTOR, this.getID(), "targeting", targetingComponent.saveState()));
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
		return ID;
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Actor && this.getID().equals(((Actor) o).getID());
	}
	
}
