package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
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
	
	private String areaID;
	
	private String topicID;
	
	private Controller controller;
	
	public Actor(String ID, String areaID, StatsActor stats, String topicID, boolean isDead, Controller controller) {
		this.ID = ID;
		this.move(Data.getArea(areaID));
		this.stats = stats;
		this.topicID = topicID;
		this.isDead = isDead;
		this.inventory = new Inventory();
		this.controller = controller;
		this.attributes = new EnumMap<Attribute, Integer>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, 1);
		}
	}
	
	public Actor(String ID, String areaID, StatsActor stats) {
		this.ID = ID;
		this.areaID = areaID;
		this.stats = stats;
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
		return Data.getArea(areaID);
	}
	
	public int getAttribute(Attribute attribute) {
		return attributes.get(attribute);
	}
	
	public void setAttribute(Attribute attribute, int value) {
		attributes.put(attribute, value);
	}
	
	public void addAttribute(Attribute attribute, int value) {
		int currentValue = attributes.get(attribute);
		attributes.put(attribute, currentValue + value);
	}
	
	public String getTopicID() {
		return topicID;
	}
	
	public void move(Area area) {
		if(areaID != null) {
			Data.getArea(areaID).removeActor(this);
		}
		areaID = area.getID();
		area.addActor(this);
	}
	
	public boolean canMove() {
		return true;
	}
	
	public Inventory inventory() {
		return inventory;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void addMoney(int value) {
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

	@Override
	public List<Action> localActions(Actor subject) {
		if(ID == Game.PLAYER_ACTOR) {
			return new ArrayList<Action>();
		}
		List<Action> action = new ArrayList<Action>();
		if(subject.getID() == Game.PLAYER_ACTOR) { // Player-only actions
			if(topicID != null) {
				action.add(new ActionTalk(this));
			}
		} else { // NPC-only actions
			
		}
		return action;
	}
	
	@Override
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<Action>();
	}
	
	public Set<Physical> getVisibleObjects() {
		Set<Physical> objects = new HashSet<Physical>();
		
		return objects;
	}
	
	public List<Action> availableActions(){
		List<Action> actions = new ArrayList<Action>();
		if(canMove()) {
			for(Area area : getArea().getLinkedAreas()) {
				actions.add(new ActionMove(area));
			}
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(WorldObject object : getArea().getObjects()) {
			actions.addAll(object.localActions(this));
		}
		actions.add(new ActionWait());
		return actions;
	}
	
	public void takeTurn() {
		// Could handle action points here?
		if(!isDead) {
			controller.chooseAction(this).choose(this);
		}
	}
	
}
