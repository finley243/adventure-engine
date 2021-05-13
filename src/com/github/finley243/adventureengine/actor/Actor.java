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
import com.github.finley243.adventureengine.action.ActionTalk;
import com.github.finley243.adventureengine.action.ActionWait;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.effect.Effect;
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
import com.github.finley243.adventureengine.world.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class Actor implements Noun, Physical {
	
	public enum Attribute {
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	public enum Skill {
		// BODY
		MELEE,
		// INTELLIGENCE
		HACKING,
		HARDWARE,
		// CHARISMA
		// DEXTERITY
		// AGILITY
	}
	
	private StatsActor stats;
	private String ID;
	private Area area;
	private String topicID;
	private Set<CombatTarget> combatTargets;
	private int HP;
	private boolean isDead;
	private boolean isUnconscious;
	private int actionPoints;
	private EnumMap<Attribute, Integer> attributes;
	private List<Effect> effects;
	private Inventory inventory;
	private ItemWeapon equippedItem;
	private int money;
	private UsableObject usingObject;
	
	public Actor(String ID, String areaID, StatsActor stats, String topicID, boolean startDead) {
		this.ID = ID;
		this.move(Data.getArea(areaID));
		this.stats = stats;
		this.topicID = topicID;
		this.combatTargets = new HashSet<CombatTarget>();
		this.isDead = startDead;
		this.isUnconscious = startDead;
		if(!startDead) {
			HP = stats.getMaxHP();
		}
		this.inventory = new Inventory();
		this.attributes = new EnumMap<Attribute, Integer>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, 1);
		}
		this.effects = new ArrayList<Effect>();
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return stats.getName();
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
	
	public int getAttribute(Attribute attribute) {
		return attributes.get(attribute);
	}
	
	public void setAttribute(Attribute attribute, int value) {
		attributes.put(attribute, value);
	}
	
	public void adjustAttribute(Attribute attribute, int value) {
		int currentValue = attributes.get(attribute);
		attributes.put(attribute, currentValue + value);
	}
	
	public String getTopicID() {
		return topicID;
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
		return !isUsingObject();
	}
	
	public Inventory inventory() {
		return inventory;
	}
	
	public void setEquippedItem(ItemWeapon item) {
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
	
	public boolean isTarget(Actor target) {
		boolean isTarget = combatTargets.contains(new CombatTarget(target));
		System.out.println(this.getName() + " - " + target.getName() + " IS TARGET: " + isTarget);
		return combatTargets.contains(new CombatTarget(target));
	}
	
	public void addTarget(Actor target) {
		combatTargets.add(new CombatTarget(target));
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> action = new ArrayList<Action>();
		if(!isDead) { // Alive
			if(topicID != null) {
				action.add(new ActionTalk(this));
			}
			if(!(subject instanceof ActorPlayer)) { // NPC-only actions
				
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
		actions.add(new ActionWait());
		Iterator<Action> itr = actions.iterator();
		while(itr.hasNext()) {
			if(itr.next().actionPoints() > actionPoints) {
				itr.remove();
			}
		}
		return actions;
	}
	
	public void takeTurn() {
		if(isDead) return;
		updateEffects();
		updateCombatTargets();
		actionPoints = stats.getActionPoints();
		while(actionPoints > -1) {
			Action chosenAction = chooseAction();
			actionPoints -= chosenAction.actionPoints();
			chosenAction.choose(this);
		}
	}
	
	public void endTurn() {
		actionPoints = -1;
	}
	
	public Action chooseAction() {
		List<Action> bestActions = new ArrayList<Action>();
		float maxWeight = 0.0f;
		for(Action currentAction : this.availableActions()) {
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
	
	private void updateEffects() {
		for(Effect effect : effects) {
			effect.update(this);
		}
	}
	
	private void updateCombatTargets() {
		for(Actor actor : getArea().getRoom().getActors()) {
			if(actor.getFaction().getRelationTo(getFaction().getID()) == FactionRelation.ENEMY) {
				CombatTarget newTarget = new CombatTarget(actor);
				combatTargets.add(newTarget);
			}
		}
		Iterator<CombatTarget> itr = combatTargets.iterator();
		while(itr.hasNext()) {
			CombatTarget target = itr.next();
			target.update(canSee(target.getActor()));
			if(target.shouldRemove()) {
				itr.remove();
			}
		}
	}
	
	private boolean canSee(Actor actor) {
		return getArea().getRoom().getActors().contains(actor);
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
