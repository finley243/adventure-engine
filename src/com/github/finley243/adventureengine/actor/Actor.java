package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMove;
import com.github.finley243.adventureengine.action.ActionMultiEnd;
import com.github.finley243.adventureengine.action.ActionTalk;
import com.github.finley243.adventureengine.action.ActionWait;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.actor.ai.BehaviorIdle;
import com.github.finley243.adventureengine.actor.ai.CombatTarget;
import com.github.finley243.adventureengine.actor.ai.PursueTarget;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.CompletedTurnEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemEquippable;
import com.github.finley243.adventureengine.world.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class Actor implements Noun, Physical {
	
	public static final int ACTIONS_PER_TURN = 1;
	
	public enum Attribute {
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	public enum Skill {
		// BODY
		MELEE,
		RESISTANCE,
		// INTELLIGENCE
		SOFTWARE,
		HARDWARE,
		// CHARISMA
		BARTER,
		PERSUASION,
		DECEPTION,
		// DEXTERITY
		HANDGUNS,
		RIFLES,
		// AGILITY
		STEALTH
	}
	
	/*public enum BehaviorState {
		STATIONARY, WANDER, PATROL, ATTACK, PURSUE
	}*/
	
	private StatsActor stats;
	private String ID;
	private String descriptor;
	private Area area;
	private int HP;
	private boolean isEnabled;
	private boolean isDead;
	private boolean isUnconscious;
	private boolean endTurn;
	private int actionPoints;
	private int repeatActionPoints;
	private List<Action> blockedActions;
	private Action repeatAction;
	private boolean isInDialogue;
	// Index: 0 = base, 1 = modifier
	private EnumMap<Attribute, int[]> attributes;
	private List<Effect> effects;
	private Inventory inventory;
	private ItemEquippable equippedItem;
	private int money;
	private UsableObject usingObject;
	private Inventory tradeInventory;
	private Set<CombatTarget> combatTargets;
	private Set<PursueTarget> pursueTargets;
	private BehaviorIdle behaviorIdle;
	
	public Actor(String ID, Area area, StatsActor stats, String descriptor, boolean startDead) {
		this.ID = ID;
		if(area != null) {
			this.move(area);
		}
		this.stats = stats;
		this.descriptor = descriptor;
		this.combatTargets = new HashSet<CombatTarget>();
		this.pursueTargets = new HashSet<PursueTarget>();
		this.isDead = startDead;
		this.isUnconscious = startDead;
		if(!startDead) {
			HP = stats.getMaxHP();
		}
		this.inventory = new Inventory();
		this.attributes = new EnumMap<Attribute, int[]>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new int[] {1, 0});
		}
		this.effects = new ArrayList<Effect>();
		if(stats.getLootTable() != null) {
			inventory.addItems(Data.getLootTable(stats.getLootTable()).generateItems());
		}
		this.blockedActions = new ArrayList<Action>();
		this.behaviorIdle = new BehaviorIdle(stats.getIdle());
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
		return values[0] + values[1];
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
	
	public String getTopicID() {
		return stats.getTopic();
	}
	
	public Faction getFaction() {
		return stats.getFaction();
	}
	
	public void move(Area area) {
		if(this.area != null) {
			this.area.removeActor(this);
		}
		setArea(area);
		area.addActor(this);
	}
	
	public boolean canMove() {
		return !isUsingObject() && !stats.preventMovement();
	}
	
	public Inventory inventory() {
		return inventory;
	}
	
	public Inventory getTradeInventory() {
		return tradeInventory;
	}
	
	public void setEquippedItem(ItemEquippable item) {
		equippedItem = item;
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
	
	public void heal(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount = Math.min(amount, stats.getMaxHP() - HP);
		HP += amount;
		Context context = new Context(this, false);
		Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> gain<s> " + amount + " HP", context));
	}
	
	public void damage(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else {
			Context context = new Context(this, false);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> lose<s> " + amount + " HP", context));
		}
	}
	
	public void kill() {
		isDead = true;
		if(equippedItem != null) {
			inventory.addItem(equippedItem);
			equippedItem = null;
		}
		Context context = new Context(this, false);
		Game.EVENT_BUS.post(new VisualEvent(getArea(), Phrases.get("die"), context));
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public boolean isUnconscious() {
		return isUnconscious;
	}
	
	public boolean isIncapacitated() {
		return isDead || isUnconscious;
	}
	
	public void onVisualEvent(VisualEvent event) {
		
	}
	
	public void onSoundEvent(SoundEvent event) {
		
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
	
	public boolean isInCover() {
		return isUsingObject() && usingObject instanceof ObjectCover;
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
		if(hasRangedWeaponEquipped() && actor.hasMeleeWeaponEquipped()) {
			return true;
		}
		return false;
	}
	
	public boolean hasRangedWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && ((ItemWeapon) equippedItem).isRanged();
	}
	
	public boolean hasMeleeWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && !((ItemWeapon) equippedItem).isRanged();
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
	
	public boolean isPursueTarget(Area area) {
		for(PursueTarget target : pursueTargets) {
			if(target.getTargetArea() == area) {
				return true;
			}
		}
		return false;
	}
	
	public void addPursueTarget(PursueTarget target) {
		if(!pursueTargets.contains(target)) {
			pursueTargets.add(target);
		}
	}
	
	public Set<PursueTarget> getPursueTargets() {
		return pursueTargets;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> action = new ArrayList<Action>();
		if(!isDead) { // Alive
			if(stats.getTopic() != null && !isInCombat()) {
				action.add(new ActionTalk(this));
			}
		} else { // Dead
			action.addAll(inventory.getActions(this));
		}
		return action;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public List<Action> availableActions(){
		List<Action> actions = new ArrayList<Action>();
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
			for(Area area : getArea().getLinkedAreas()) {
				actions.add(new ActionMove(area));
			}
		}
		for(Item item : inventory.getUniqueItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		Iterator<Action> itr = actions.iterator();
		while(itr.hasNext()) {
			Action currentAction = itr.next();
			boolean isBlocked = false;
			for(Action blockedAction : blockedActions) {
				if(currentAction.isRepeatMatch(blockedAction)) {
					isBlocked = true;
					break;
				}
			}
			if(isBlocked) {
				itr.remove();
			}
		}
		actions.add(new ActionWait());
		return actions;
	}
	
	public void takeTurn() {
		if(isDead || !isEnabled()) {
			Game.EVENT_BUS.post(new CompletedTurnEvent());
		} else {
			updateEffects();
			behaviorIdle.update(this);
			this.actionPoints = ACTIONS_PER_TURN;
			this.repeatActionPoints = 0;
			repeatAction = null;
			this.blockedActions.clear();
			this.endTurn = false;
			takeTurnAction();
		}
	}

	private void takeTurnAction() {
		generateCombatTargets();
		generatePursueTargets();
		updatePursueTargets();
		updateCombatTargets();
		if(repeatActionPoints > 0) {
			List<Action> repeatActions = new ArrayList<>();
			for(Action action : availableActions()) {
				if(repeatAction.isRepeatMatch(action)) {
					repeatActions.add(action);
				}
			}
			repeatActions.add(new ActionMultiEnd());
			chooseAction(repeatActions);
			//chosenAction = chooseAction(repeatActions);
			//repeatActionPoints--;
		} else {
			List<Action> validActions = new ArrayList<>();
			for(Action action : availableActions()) {
				if(!action.usesAction() || actionPoints > 0) {
					validActions.add(action);
				}
			}
			chooseAction(validActions);
			/*chosenAction = chooseAction(validActions);
			if(chosenAction.usesAction()) {
				actionPoints--;
			}
			if(chosenAction.actionCount() > 1) {
				repeatActionPoints = chosenAction.actionCount() - 1;
				repeatAction = chosenAction;
			} else if(!chosenAction.canRepeat()) {
				blockedActions.add(chosenAction);
			}*/
		}
	}
	
	public void endTurn() {
		actionPoints = 0;
		endTurn = true;
	}
	
	public void endMultiAction() {
		repeatActionPoints = 0;
	}
	
	public void chooseAction(List<Action> actions) {
		Action choice = highestUtilityAction(actions);
		handleActionSelection(choice);
	}

	public Action highestUtilityAction(List<Action> actions) {
		List<Action> bestActions = new ArrayList<>();
		float maxWeight = 0.0f;
		for(Action currentAction : actions) {
			float currentWeight = currentAction.utility(this);
			if(currentWeight > maxWeight) {
				maxWeight = currentWeight;
				bestActions.clear();
				bestActions.add(currentAction);
			} else if(currentWeight == maxWeight) {
				bestActions.add(currentAction);
			}
		}
		return bestActions.get(ThreadLocalRandom.current().nextInt(bestActions.size()));
	}

	public void handleActionSelection(Action action) {
		if(repeatActionPoints > 0) {
			repeatActionPoints--;
		} else {
			if(action.usesAction()) {
				actionPoints--;
			}
			if(action.actionCount() > 1) {
				repeatActionPoints = action.actionCount() - 1;
				repeatAction = action;
			} else if(!action.canRepeat()) {
				blockedActions.add(action);
			}
		}
		action.choose(this);
		if(!isInDialogue) {
			if(!endTurn) {
				takeTurnAction();
			} else {
				Game.EVENT_BUS.post(new CompletedTurnEvent());
			}
		}
	}

	public void setIsInDialogue(boolean isInDialogue) {
		if(this.isInDialogue && !isInDialogue && !endTurn) {
			takeTurnAction();
		}
		this.isInDialogue = isInDialogue;
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
				}
			}
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
	
	private void generatePursueTargets() {
		
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
	
	public boolean canSee(Actor actor) {
		if(!getArea().getRoom().getActors().contains(actor)) {
			return false;
		} else if(actor.isInCover() && getArea() != actor.getArea()) {
			return false;
		}
		return true;
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
