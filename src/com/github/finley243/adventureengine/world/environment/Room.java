package com.github.finley243.adventureengine.world.environment;

import java.util.*;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.object.WorldObject;

/**
 * Represents a self-contained space (e.g. an actual room) that contains smaller areas
 */
public class Room extends GameInstanced implements Noun, StatHolder {

	private final String name;
	private final boolean isProperName;
	private boolean isKnown;
	private final Scene description;
	private final String ownerFaction;
	private final Set<Area> areas;
	private final Map<String, RoomLink> linkedRooms;

	private final Map<String, Script> scripts;

	private boolean hasVisited;

	public Room(Game game, String ID, String name, boolean isProperName, Scene description, String ownerFaction, Set<Area> areas, Map<String, RoomLink> linkedRooms, Map<String, Script> scripts) {
		super(game, ID);
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
		this.ownerFaction = ownerFaction;
		this.areas = areas;
		this.linkedRooms = linkedRooms;
		this.hasVisited = false;
		this.scripts = scripts;
	}
	
	public Set<Area> getAreas(){
		return areas;
	}

	public Set<String> getAreaIDs() {
		Set<String> areaIDs = new HashSet<>();
		for (Area area : areas) {
			areaIDs.add(area.getID());
		}
		return areaIDs;
	}

	public Map<String, RoomLink> getLinkedRooms() {
		return linkedRooms;
	}
	
	public Scene getDescription() {
		return description;
	}
	
	public boolean hasVisited() {
		return hasVisited;
	}

	public void setVisited() {
		hasVisited = true;
	}

	public String getOwnerFaction() {
		return ownerFaction;
	}
	
	public Set<WorldObject> getObjects() {
		Set<WorldObject> objects = new HashSet<>();
		for (Area area : areas) {
			objects.addAll(area.getObjects());
		}
		return objects;
	}
	
	public Set<Actor> getActors() {
		Set<Actor> actors = new HashSet<>();
		for (Area area : areas) {
			actors.addAll(area.getActors());
		}
		return actors;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFormattedName() {
		if (!isProperName()) {
			return LangUtils.addArticle(getName(), !isKnown);
		} else {
			return getName();
		}
	}

	public String getRelativeName() {
		return "in " + getFormattedName();
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
		return isProperName;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public boolean forcePronoun() {
		return false;
	}

	@Override
	public int getValueInt(String name) {
		return 0;
	}

	@Override
	public float getValueFloat(String name) {
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		return switch (name) {
			case "known" -> isKnown();
			case "visited" -> hasVisited();
			default -> false;
		};
	}

	@Override
	public String getValueString(String name) {
		return switch (name) {
			case "id" -> getID();
			case "ownerFaction" -> ownerFaction;
			default -> null;
		};
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return null;
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		switch (name) {
			case "known" -> isKnown = value;
			case "visited" -> hasVisited = value;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {

	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {

	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {

	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return null;
	}

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if (scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(new ContextScript(game(), subject, target));
		}
	}

	public void loadState(SaveData saveData) {
		switch (saveData.getParameter()) {
			case "isKnown" -> this.isKnown = saveData.getValueBoolean();
			case "hasVisited" -> this.hasVisited = saveData.getValueBoolean();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (isKnown) {
			state.add(new SaveData(SaveData.DataType.ROOM, this.getID(), "isKnown", isKnown));
		}
		if (hasVisited) {
			state.add(new SaveData(SaveData.DataType.ROOM, this.getID(), "hasVisited", hasVisited));
		}
		return state;
	}

	@Override
	public int hashCode() {
		return getID().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Room && ((Room) o).getID().equals(this.getID());
	}
	
}
