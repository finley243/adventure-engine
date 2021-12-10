package com.github.finley243.adventureengine.actor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.actor.ai.*;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.item.ItemEquippable;
import com.github.finley243.adventureengine.world.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class Actor implements Noun, Physical {

	public static final boolean SHOW_HP_CHANGES = true;
	public static final int ACTIONS_PER_TURN = 2;
	public static final int ATTRIBUTE_MIN = 1;
	public static final int ATTRIBUTE_MAX = 10;
	public static final int SKILL_MIN = 1;
	public static final int SKILL_MAX = 10;
	public static final int SKILL_ATTR_MIN = ATTRIBUTE_MIN + SKILL_MIN;
	public static final int SKILL_ATTR_MAX = ATTRIBUTE_MAX + SKILL_MAX;
	
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
		FIRST_AID(Attribute.INTELLIGENCE),
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
		EVASION(Attribute.AGILITY);

		public final Attribute attribute;

		Skill(Attribute attribute) {
			this.attribute = attribute;
		}
	}
	
	private final StatsActor stats;
	private final String ID;
	private final String descriptor;
	private Area area;
	private int HP;
	private boolean isEnabled;
	private boolean isDead;
	private boolean isUnconscious;
	private boolean endTurn;
	private int actionPoints;
	private int multiActionPoints;
	private final List<Action> blockedActions;
	// Index: 0 = base, 1 = modifier
	private final EnumMap<Attribute, int[]> attributes;
	private final EnumMap<Skill, int[]> skills;
	private final List<Effect> effects;
	private final Inventory inventory;
	private final ApparelManager apparelManager;
	private ItemEquippable equippedItem;
	private int money;
	private UsableObject usingObject;
	private boolean isCrouching;
	private Inventory tradeInventory;
	private final Set<CombatTarget> combatTargets;
	private final Set<PursueTarget> pursueTargets;
	private final InvestigateTarget investigateTarget;
	private final BehaviorIdle behaviorIdle;
	private final boolean preventMovement;
	
	public Actor(String ID, Area area, StatsActor stats, String descriptor, List<String> idle, boolean preventMovement, boolean startDead) {
		this.ID = ID;
		if(area != null) {
			this.move(area);
		}
		this.stats = stats;
		this.descriptor = descriptor;
		this.preventMovement = preventMovement;
		this.combatTargets = new HashSet<>();
		this.pursueTargets = new HashSet<>();
		this.investigateTarget = new InvestigateTarget();
		this.isDead = startDead;
		this.isUnconscious = startDead;
		if(!startDead) {
			HP = stats.getMaxHP();
		}
		this.inventory = new Inventory();
		this.apparelManager = new ApparelManager();
		this.attributes = new EnumMap<>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new int[] {stats.getAttribute(attribute), 0});
		}
		this.skills = new EnumMap<>(Skill.class);
		for(Skill skill : Skill.values()) {
			this.skills.put(skill, new int[] {stats.getSkill(skill), 0});
		}
		this.effects = new ArrayList<>();
		if(stats.getLootTable() != null) {
			inventory.addItems(Data.getLootTable(stats.getLootTable()).generateItems());
		}
		this.blockedActions = new ArrayList<>();
		this.behaviorIdle = new BehaviorIdle(idle);
		this.isEnabled = true;
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return (descriptor != null ? descriptor + " " : "") + stats.getName();
	}
	
	@Override
	public String getFormattedName(boolean indefinite) {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
	}
	
	@Override
	public boolean isProperName() {
		return stats.isProperName();
	}
	
	@Override
	public Pronoun getPronoun() {
		return stats.getPronoun();
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		this.area = area;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean enable) {
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
		int[] values = attributes.get(attribute);
		int sum = values[0] + values[1];
		if(sum < ATTRIBUTE_MIN) {
			return ATTRIBUTE_MIN;
		} else if(sum > ATTRIBUTE_MAX) {
			return ATTRIBUTE_MAX;
		} else {
			return sum;
		}
	}
	
	public int getAttributeBase(Attribute attribute) {
		return attributes.get(attribute)[0];
	}
	
	public void setAttributeBase(Attribute attribute, int value) {
		attributes.get(attribute)[0] = value;
	}
	
	public void adjustAttributeBase(Attribute attribute, int value) {
		attributes.get(attribute)[0] += value;
	}
	
	public int getAttributeMod(Attribute attribute) {
		return attributes.get(attribute)[1];
	}
	
	public void setAttributeMod(Attribute attribute, int value) {
		attributes.get(attribute)[1] = value;
	}
	
	public void adjustAttributeMod(Attribute attribute, int value) {
		attributes.get(attribute)[1] += value;
	}

	public int getSkill(Skill skill) {
		int[] values = skills.get(skill);
		int sum = values[0] + values[1];
		if(sum < SKILL_MIN) {
			return SKILL_MIN;
		} else if(sum > SKILL_MAX) {
			return SKILL_MAX;
		} else {
			return sum;
		}
	}

	public int getSkillWithAttribute(Skill skill) {
		return getSkill(skill) + getAttribute(skill.attribute);
	}

	public int getSkillBase(Skill skill) {
		return skills.get(skill)[0];
	}

	public void setSkillBase(Skill skill, int value) {
		skills.get(skill)[0] = value;
	}

	public void adjustSkillBase(Skill skill, int value) {
		skills.get(skill)[0] += value;
	}

	public int getSkillMod(Skill skill) {
		return skills.get(skill)[1];
	}

	public void setSkillMod(Skill skill, int value) {
		skills.get(skill)[1] = value;
	}

	public void adjustSkillMod(Skill skill, int value) {
		skills.get(skill)[1] += value;
	}
	
	public String getTopicID() {
		return stats.getTopic();
	}
	
	public Faction getFaction() {
		return Data.getFaction(stats.getFaction());
	}
	
	public void move(Area area) {
		if(this.area != null) {
			this.area.removeActor(this);
		}
		setArea(area);
		area.addActor(this);
	}
	
	public boolean canMove() {
		return !isUsingObject() && !preventMovement;
	}

	public boolean isCrouching() {
		return isCrouching;
	}

	public void setCrouching(boolean state) {
		isCrouching = state;
	}
	
	public Inventory inventory() {
		return inventory;
	}

	public ApparelManager apparelManager() {
		return apparelManager;
	}

	public List<Limb> getLimbs() {
		return stats.getLimbs();
	}
	
	public Inventory getTradeInventory() {
		return tradeInventory;
	}
	
	public void setEquippedItem(ItemEquippable item) {
		equippedItem = item;
	}

	public ItemEquippable getEquippedItem() {
		return equippedItem;
	}
	
	public boolean hasEquippedItem() {
		return equippedItem != null;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void adjustMoney(int value) {
		money += value;
	}
	
	public void addEffect(Effect effect) {
		effect.update(this);
		if(!effect.shouldRemove()) {
			effects.add(effect);
		}
	}

	public void removeEffect(Effect effect) {
		effect.end(this);
		effects.remove(effect);
	}
	
	public void heal(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount = Math.min(amount, stats.getMaxHP() - HP);
		HP += amount;
		if(SHOW_HP_CHANGES) {
			Context context = new Context(this, false);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> gain<s> " + amount + " HP", context, null, null));
		}
	}
	
	public void damage(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount -= apparelManager.getDamageResistance(ApparelManager.ApparelSlot.TORSO);
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else if(SHOW_HP_CHANGES) {
			Context context = new Context(this, false);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> lose<s> " + amount + " HP", context, null, null));
		}
	}

	public void damageLimb(int amount, Limb limb) {
		if(amount < 0) throw new IllegalArgumentException();
		amount -= apparelManager.getDamageResistance(limb.getApparelSlot());
		if(amount < 0) amount = 0;
		if(amount > 0) {
			limb.applyEffects(this);
		}
		amount *= limb.getDamageMult();
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else if(SHOW_HP_CHANGES) {
			Context context = new Context(this, false);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> lose<s> " + amount + " HP", context, null, null));
		}
	}
	
	public void kill() {
		isDead = true;
		Context context = new Context(this, false);
		Game.EVENT_BUS.post(new VisualEvent(getArea(), Phrases.get("die"), context, null, null));
		if(equippedItem != null) {
			getArea().addObject(equippedItem);
			context = new Context(this, false, equippedItem, false);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), Phrases.get("forceDrop"), context, null, null));
			equippedItem = null;
		}
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public boolean isUnconscious() {
		return isUnconscious;
	}
	
	public boolean isActive() {
		return !isDead && !isUnconscious;
	}
	
	public void onVisualEvent(VisualEvent event) {
		if(event.getAction() instanceof ActionMoveExit) {
			for(CombatTarget target : combatTargets) {
				if(target.getTargetActor() == event.getSubject()) {
					target.setUsedExit(((ActionMoveExit) event.getAction()).getExit());
				}
			}
		} else if(event.getAction() instanceof ActionMoveElevator) {
			for(CombatTarget target : combatTargets) {
				if(target.getTargetActor() == event.getSubject()) {
					target.setUsedElevator(((ActionMoveElevator) event.getAction()).getElevator());
				}
			}
		}
	}
	
	public void onSoundEvent(SoundEvent event) {
		investigateTarget.setTargetArea(event.getOrigin());
	}
	
	public void startUsingObject(UsableObject object) {
		this.usingObject = object;
	}
	
	public void stopUsingObject() {
		this.usingObject = null;
	}
	
	public boolean isUsingObject() {
		return this.usingObject != null;
	}
	
	public boolean isInCombat() {
		return combatTargets.size() > 0;
	}
	
	public boolean hasMeleeTargets() {
		for(CombatTarget target : combatTargets) {
			if(target.getTargetActor().getArea() == this.getArea()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean shouldFleeFrom(Actor actor) {
		return hasRangedWeaponEquipped() && actor.hasMeleeWeaponEquipped();
	}
	
	public boolean hasRangedWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && ((ItemWeapon) equippedItem).isRanged();
	}
	
	public boolean hasMeleeWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && !((ItemWeapon) equippedItem).isRanged();
	}

	public boolean hasWeapon() {
		for(Item item : inventory.getItems()) {
			if(item instanceof ItemWeapon) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCombatTarget(Actor actor) {
		for(CombatTarget target : combatTargets) {
			if(target.getTargetActor() == actor) {
				return true;
			}
		}
		return false;
	}
	
	public void addCombatTarget(Actor actor) {
		combatTargets.add(new CombatTarget(actor));
	}

	public Set<CombatTarget> getCombatTargets() {
		return combatTargets;
	}
	
	public void addPursueTarget(PursueTarget target) {
		pursueTargets.add(target);
	}
	
	public Set<PursueTarget> getPursueTargets() {
		return pursueTargets;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> action = new ArrayList<>();
		if(!isDead) { // Alive
			if(stats.getTopic() != null && !isInCombat()) {
				//action.add(new ActionTalk(this));
				action.add(new ActionGeneric(this, "TALK",
						0.0f, true, true, ActionGeneric.ActionMatchType.NONE, 1,
						new MenuData("Talk", "Talk to " + this.getFormattedName(false), true, new String[]{this.getName()})));
			}
		} else { // Dead
			action.addAll(inventory.getActions(this));
		}
		return action;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<>();
	}

	@Override
	public void executeAction(String action, Actor subject) {
		switch(action.toUpperCase()) {
			case "TALK":
				actionTalk(subject);
				break;
			default:
				throw new IllegalArgumentException("Action " + action + " does not exist for object " + this.getClass().getSimpleName());
		}
	}

	private void actionTalk(Actor subject) {
		if(subject instanceof ActorPlayer) {
			((ActorPlayer) subject).startDialogue(this, this.getTopicID());
		}
	}

	public List<Action> availableActions(boolean ignoreBlocked){
		List<Action> actions = new ArrayList<>();
		if(hasEquippedItem()) {
			actions.addAll(equippedItem.equippedActions(this));
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(Actor actor : getArea().getRoom().getActors()) {
			actions.addAll(actor.remoteActions(this));
		}
		for(WorldObject object : getArea().getObjects()) {
			actions.addAll(object.localActions(this));
		}
		for(WorldObject object : getArea().getRoom().getObjects()) {
			actions.addAll(object.remoteActions(this));
		}
		if(isUsingObject()) {
			actions.addAll(usingObject.usingActions());
		}
		if(canMove()) {
			for(Area area : getArea().getMovableAreas()) {
				actions.add(new ActionMove(area));
			}
		}
		for(Item item : inventory.getUniqueItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		for(ItemApparel item : apparelManager.getEquippedItems()) {
			actions.addAll(item.equippedActions(this));
		}
		if(!ignoreBlocked) {
			for(Action currentAction : actions) {
				boolean isBlocked = false;
				for (Action blockedAction : blockedActions) {
					if (blockedAction.isRepeatMatch(currentAction)) {
						isBlocked = true;
						break;
					}
				}
				if (isBlocked) {
					currentAction.disable();
				}
			}
		}
		actions.add(new ActionEnd());
		return actions;
	}
	
	public void takeTurn() {
		if(!isActive() || !isEnabled()) return;
		updateEffects();
		updateCombatTargetsTurn();
		investigateTarget.nextTurn(this);
		behaviorIdle.update(this);
		this.actionPoints = ACTIONS_PER_TURN;
		this.multiActionPoints = 0;
		Action multiAction = null;
		this.blockedActions.clear();
		this.endTurn = false;
		while(!endTurn) {
			generateCombatTargets();
			updatePursueTargets();
			updateCombatTargets();
			investigateTarget.update(this);
			Action chosenAction;
			if(multiActionPoints > 0) {
				List<Action> repeatActions = new ArrayList<>();
				for(Action action : availableActions(true)) {
					if(multiAction.isMultiMatch(action)) {
						repeatActions.add(action);
					}
				}
				repeatActions.add(new ActionEndMulti());
				chosenAction = chooseAction(repeatActions);
				multiActionPoints--;
			} else {
				List<Action> availableActions = availableActions(false);
				for(Action action : availableActions) {
					if(action.usesAction() && actionPoints <= 0) {
						action.disable();
					}
				}
				chosenAction = chooseAction(availableActions);
				if(chosenAction.usesAction()) {
					actionPoints--;
				}
				if(chosenAction.multiCount() > 1) {
					multiActionPoints = chosenAction.multiCount() - 1;
					multiAction = chosenAction;
				}
				if(!chosenAction.canRepeat()) {
					blockedActions.add(chosenAction);
				}
			}
			chosenAction.choose(this);
		}
	}
	
	public void endTurn() {
		actionPoints = 0;
		endTurn = true;
	}
	
	public void endMultiAction() {
		multiActionPoints = 0;
	}
	
	public Action chooseAction(List<Action> actions) {
		int chaos = 1;
		List<List<Action>> bestActions = new ArrayList<>(chaos + 1);
		List<Float> maxWeights = new ArrayList<>(chaos + 1);
		for(int i = 0; i < chaos + 1; i++) {
			bestActions.add(new ArrayList<>());
			maxWeights.add(0.0f);
		}
		for(Action currentAction : actions) {
			if(currentAction.canChoose(this)) {
				float currentWeight = currentAction.utility(this);
				for(int i = 0; i < chaos + 1; i++) {
					if(currentWeight == maxWeights.get(i)) {
						bestActions.get(i).add(currentAction);
						break;
					} else if(currentWeight > maxWeights.get(i)) {
						maxWeights.remove(maxWeights.size() - 1);
						maxWeights.add(i, currentWeight);
						bestActions.remove(bestActions.size() - 1);
						bestActions.add(i, new ArrayList<>());
						bestActions.get(i).add(currentAction);
						break;
					}
				}
			}
		}
		float weightSum = 0.0f;
		for(float weight : maxWeights) {
			weightSum += weight;
		}
		float partialWeightSum = 0.0f;
		float random = ThreadLocalRandom.current().nextFloat();
		for(int i = 0; i < chaos + 1; i++) {
			if(random < partialWeightSum + (maxWeights.get(i) / weightSum)) {
				return bestActions.get(i).get(ThreadLocalRandom.current().nextInt(bestActions.get(i).size()));
			} else {
				partialWeightSum += (maxWeights.get(i) / weightSum);
			}
		}
		return null;
	}
	
	private void updateEffects() {
		Iterator<Effect> itr = effects.iterator();
		while(itr.hasNext()) {
			Effect effect = itr.next();
			effect.update(this);
			if(effect.shouldRemove()) {
				itr.remove();
			}
		}
	}
	
	private void generateCombatTargets() {
		for(Actor actor : getArea().getRoom().getActors()) {
			if(actor != this && canSee(actor) && !actor.isDead()) {
				if(getFaction().getRelationTo(actor.getFaction().getID()) == FactionRelation.ENEMY) {
					if(!isCombatTarget(actor)) {
						addCombatTarget(actor);
					}
				} else if(getFaction().getRelationTo(actor.getFaction().getID()) == FactionRelation.FRIEND) {
					for(CombatTarget allyTarget : actor.getCombatTargets()) {
						if(!isCombatTarget(allyTarget.getTargetActor())) {
							addCombatTarget(allyTarget.getTargetActor());
						}
					}
				} else if(getArea().getRoom().getOwnerFaction() != null && Data.getFaction(getArea().getRoom().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != FactionRelation.FRIEND) {
					if(!isCombatTarget(actor)) {
						addCombatTarget(actor);
					}
				}
			}
		}
	}

	private void updateCombatTargetsTurn() {
		for(CombatTarget target : combatTargets) {
			target.nextTurn();
		}
	}
	
	private void updateCombatTargets() {
		Iterator<CombatTarget> itr = combatTargets.iterator();
		while(itr.hasNext()) {
			CombatTarget target = itr.next();
			target.update(this);
			if(target.shouldRemove()) {
				itr.remove();
			}
		}
	}
	
	private void updatePursueTargets() {
		Iterator<PursueTarget> itr = pursueTargets.iterator();
		while(itr.hasNext()) {
			PursueTarget target = itr.next();
			target.update(this);
			if(target.shouldRemove()) {
				itr.remove();
			}
		}
	}
	
	public boolean canSee(Actor target) {
		return getArea().getVisibleAreas(this).contains(target.getArea()) && (!target.isCrouching || !getArea().isBehindCover(target.getArea()));
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Actor)) {
			return false;
		} else {
			return this == other;
		}
	}
	
}
