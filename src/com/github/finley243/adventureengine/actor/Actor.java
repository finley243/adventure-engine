package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMove;
import com.github.finley243.adventureengine.action.ActionTalk;
import com.github.finley243.adventureengine.action.ActionWait;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class Actor implements Noun, Physical, AttackTarget {
	
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
	
	private int HP;
	private boolean isDead;
	
	private EnumMap<Attribute, Integer> attributes;
	
	private Inventory inventory;
	private int money;
	
	private Area area;
	
	private String topicID;
	
	private UsableObject usingObject;
	
	public Actor(String ID, String areaID, StatsActor stats, String topicID, boolean isDead) {
		this.ID = ID;
		this.move(Data.getArea(areaID));
		this.stats = stats;
		this.topicID = topicID;
		this.isDead = isDead;
		this.inventory = new Inventory();
		this.attributes = new EnumMap<Attribute, Integer>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, 1);
		}
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return stats.getName();
	}
	
	@Override
	public String getFormattedName() {
		return (isProperName() ? "" : "the ") + getName();
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
			isDead = true;
		}
	}
	
	public void damageIgnoreArmor(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		HP -= amount;
		if(HP <= 0) {
			isDead = true;
		}
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

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> action = new ArrayList<Action>();
		if(topicID != null) {
			action.add(new ActionTalk(this));
		}
		if(!(subject instanceof ActorPlayer)) { // NPC-only actions
			
		}
		return action;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public List<Action> availableActions(){
		List<Action> actions = new ArrayList<Action>();
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(WorldObject object : getArea().getObjects()) {
			actions.addAll(object.localActions(this));
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
		return actions;
	}
	
	public void takeTurn() {
		// Could handle action points here?
		if(!isDead) {
			Action chosenAction = chooseAction();
			chosenAction.choose(this);
		}
	}
	
	private Action chooseAction() {
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
	
}
