package personal.finley.adventure_engine.world.environment;

import java.util.HashSet;
import java.util.Set;

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.textgen.Context.Pronoun;
import personal.finley.adventure_engine.world.Noun;
import personal.finley.adventure_engine.world.object.WorldObject;

public class Area implements Noun {

	private String ID;
	
	// The name of the area
	private String name;
	// Whether the name is a proper name (if false, should be preceded with "the" or "a")
	private boolean isProperName;
	// Whether the name is a "proximity-based" name (if true, formats name as "near [name]", respects proper and improper names)
	private boolean isProximateName;
	// The room containing this area
	private String roomID;
	
	// All areas that can be accessed when in this area
	private Set<String> linkedAreas;
	
	// All objects in this area
	private Set<WorldObject> objects;
	// All actors in this area
	private Set<Actor> actors;
	
	public Area(String ID, String name, boolean isProperName, boolean isProximateName, String roomID, Set<String> linkedAreas, Set<WorldObject> objects) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.isProximateName = isProximateName;
		this.roomID = roomID;
		this.linkedAreas = linkedAreas;
		this.objects = objects;
		this.actors = new HashSet<Actor>();
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getFormattedName() {
		return (isProperName() ? "" : "the ") + getName();
	}
	
	public Set<WorldObject> getObjects(){
		return objects;
	}
	
	public void addObject(WorldObject object) {
		boolean didAdd = objects.add(object);
		if(!didAdd) {
			//System.out.println("Area " + ID + " already contains object " + object + ".");
		}
	}
	
	public void removeObject(WorldObject object) {
		boolean didRemove = objects.remove(object);
		if(!didRemove) {
			//System.out.println("Area " + ID + " does not contain object " + object + ".");
		}
	}
	
	public Set<Actor> getActors(){
		return actors;
	}
	
	public void addActor(Actor actor) {
		boolean didAdd = actors.add(actor);
		if(!didAdd) {
			//System.out.println("Area " + ID + " already contains actor " + actor + ".");
		}
	}
	
	public void removeActor(Actor actor) {
		boolean didRemove = actors.remove(actor);
		if(!didRemove) {
			//System.out.println("Area " + ID + " does not contain actor " + actor + ".");
		}
	}
	
	public Set<Area> getLinkedAreas() {
		Set<Area> output = new HashSet<Area>();
		for(String linkedID : linkedAreas) {
			output.add(Data.getArea(linkedID));
		}
		return output;
	}
	
	public Set<Area> getVisibleAreas() {
		Set<Area> visibleAreas = new HashSet<Area>();
		// Areas in current room
		visibleAreas.addAll(getRoom().getAreas());
		
		// Ledge
		
		return visibleAreas;
	}

	@Override
	public boolean isProperName() {
		return isProperName;
	}
	
	public boolean isProximateName() {
		return isProximateName;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
	public Room getRoom() {
		return Data.getRoom(roomID);
	}
	
}
