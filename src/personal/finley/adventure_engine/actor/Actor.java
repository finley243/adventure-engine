package personal.finley.adventure_engine.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.Game;
import personal.finley.adventure_engine.action.ActionMove;
import personal.finley.adventure_engine.action.ActionTalk;
import personal.finley.adventure_engine.action.ActionWait;
import personal.finley.adventure_engine.action.IAction;
import personal.finley.adventure_engine.textgen.Context.Pronoun;
import personal.finley.adventure_engine.world.IAttackTarget;
import personal.finley.adventure_engine.world.INoun;
import personal.finley.adventure_engine.world.IPhysical;
import personal.finley.adventure_engine.world.environment.Area;
import personal.finley.adventure_engine.world.object.ObjectBase;
import personal.finley.adventure_engine.world.template.StatsActor;

public class Actor implements INoun, IPhysical, IAttackTarget {
	
	public enum Skill{
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	private StatsActor stats;
	
	private String ID;
	
	private int HP;
	private boolean isDead;
	
	private Inventory inventory;
	
	private String areaID;
	
	private String topicID;
	
	IController controller;
	
	public Actor(String ID, String areaID, StatsActor stats, String topicID, boolean isDead, IController controller) {
		this.ID = ID;
		this.move(Data.getArea(areaID));
		this.stats = stats;
		this.topicID = topicID;
		this.isDead = isDead;
		this.inventory = new Inventory();
		this.controller = controller;
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

	@Override
	public List<IAction> localActions(Actor subject) {
		if(ID == Game.PLAYER_ACTOR) {
			return new ArrayList<IAction>();
		}
		List<IAction> action = new ArrayList<IAction>();
		if(subject.getID() == Game.PLAYER_ACTOR) { // Player-only actions
			if(topicID != null) {
				action.add(new ActionTalk(this));
			}
		} else { // NPC-only actions
			
		}
		return action;
	}
	
	@Override
	public List<IAction> remoteActions(Actor subject) {
		return new ArrayList<IAction>();
	}
	
	public Set<IPhysical> getVisibleObjects() {
		Set<IPhysical> objects = new HashSet<IPhysical>();
		
		return objects;
	}
	
	public List<IAction> availableActions(){
		List<IAction> actions = new ArrayList<IAction>();
		if(canMove()) {
			for(Area area : getArea().getLinkedAreas()) {
				actions.add(new ActionMove(area));
			}
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(ObjectBase object : getArea().getObjects()) {
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
