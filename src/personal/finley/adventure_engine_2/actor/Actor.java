package personal.finley.adventure_engine_2.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.EnumTypes.Pronoun;
import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.action.move.ActionMove;
import personal.finley.adventure_engine_2.world.IActionable;
import personal.finley.adventure_engine_2.world.INoun;
import personal.finley.adventure_engine_2.world.Inventory;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.object.ObjectBase;

public class Actor implements INoun, IActionable {
	
	private String ID;
	
	private String name;
	private boolean isProperName;
	private Pronoun pronoun;
	
	private boolean isDead;
	
	private Inventory inventory;
	
	private String currentAreaID;
	
	private String defaultTopic;
	private Queue<String> topicQueue;
	
	IController controller;
	
	public Actor(String ID, String name, boolean isProperName, Pronoun pronoun, String startingAreaID, String defaultTopic, String initTopic, boolean isDead, IController controller) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
		this.move(Data.getArea(startingAreaID));
		this.defaultTopic = defaultTopic;
		this.topicQueue = new LinkedList<String>();
		if(initTopic != null) {
			topicQueue.add(initTopic);
		}
		this.isDead = isDead;
		this.inventory = new Inventory();
		Data.addActor(ID, this);
		this.controller = controller;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isProperName() {
		return isProperName;
	}
	
	@Override
	public Pronoun getPronoun() {
		return pronoun;
	}
	
	@Override
	public Area getArea() {
		return Data.getArea(currentAreaID);
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
		if(currentAreaID != null) {
			Data.getArea(currentAreaID).removeActor(this);
		}
		currentAreaID = area.getID();
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
	
	public Set<IActionable> getVisibleObjects() {
		Set<IActionable> objects = new HashSet<IActionable>();
		
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
		
		return actions;
	}
	
	public void takeTurn() {
		// Could handle action points here?
		controller.chooseAction(this).choose(this);
	}
	
}
