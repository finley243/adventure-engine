package personal.finley.adventure_engine_2.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.action.ActionMove;
import personal.finley.adventure_engine_2.action.ActionWait;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.textgen.Context.Pronoun;
import personal.finley.adventure_engine_2.world.IAttackTarget;
import personal.finley.adventure_engine_2.world.INoun;
import personal.finley.adventure_engine_2.world.IPhysical;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.object.ObjectBase;
import personal.finley.adventure_engine_2.world.template.StatsActor;

public class Actor implements INoun, IPhysical, IAttackTarget {
	
	public enum Skill{
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	private StatsActor stats;
	
	private String ID;
	
	private boolean isDead;
	
	private Inventory inventory;
	
	private String areaID;
	
	private String defaultTopic;
	private Queue<String> topicQueue;
	
	IController controller;
	
	public Actor(String ID, String areaID, StatsActor stats, String defaultTopic, String initTopic, boolean isDead, IController controller) {
		this.ID = ID;
		this.move(Data.getArea(areaID));
		this.stats = stats;
		this.defaultTopic = defaultTopic;
		this.topicQueue = new LinkedList<String>();
		if(initTopic != null) {
			topicQueue.add(initTopic);
		}
		this.isDead = isDead;
		this.inventory = new Inventory();
		this.controller = controller;
	}
	
	public Actor(String ID, String areaID, StatsActor template) {
		
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
	
	public void addTopicToQueue(String topic) {
		topicQueue.add(topic);
	}
	
	public String getTopic() {
		if(!topicQueue.isEmpty()) {
			return topicQueue.remove();
		}
		return defaultTopic;
	}
	
	public void move(Area area) {
		if(areaID != null) {
			Data.getArea(areaID).removeActor(this);
		}
		areaID = area.getID();
		area.addActor(this);
	}
	
	public Inventory inventory() {
		return inventory;
	}
	
	public boolean canMove() {
		return true;
	}

	@Override
	public List<IAction> localActions(Actor subject) {
		if(ID == Game.PLAYER_ACTOR) {
			return new ArrayList<IAction>();
		}
		List<IAction> action = new ArrayList<IAction>();
		if(defaultTopic != null) {
			//action.add(new ActionActorTalk(this));
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
