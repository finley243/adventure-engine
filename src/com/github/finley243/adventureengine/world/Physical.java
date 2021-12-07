package com.github.finley243.adventureengine.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;

/**
 * Represents an object that is located in the game world
 */
public abstract class Physical {

	private Room room;
	private int x;
	private int y;
	private int xDim;
	private int yDim;

	public Physical(int xDim, int yDim) {
		this.xDim = xDim;
		this.yDim = yDim;
	}

	public Room getRoom() {
		return room;
	}

	public Set<Area> getAreas() {
		Set<Area> areas = new HashSet<>();
		for(int currentX = x; currentX < x + xDim; currentX++) {
			for(int currentY = y; currentY < y + yDim; currentY++) {
				areas.add(room.getArea(currentX, currentY));
			}
		}
		return areas;
	}

	public Area getArea() {
		if(xDim != 1 || yDim != 1) throw new UnsupportedOperationException("Cannot get single area from object with dimensions " + xDim + "x" + yDim);
		if(room == null) return null;
		return room.getArea(x, y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getXDim() {
		return xDim;
	}

	public int getYDim() {
		return yDim;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setDimensions(int xDim, int yDim) {
		this.xDim = xDim;
		this.yDim = yDim;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public void setPosition(Room room, int x, int y) {
		this.room = room;
		this.x = x;
		this.y = y;
	}
	
	// Actions that can be performed within the same area
	public List<Action> localActions(Actor subject) {
		return new ArrayList<>();
	}
	
	// Actions that can be performed anywhere within the same room
	public List<Action> remoteActions(Actor subject) {
		return new ArrayList<>();
	}

	public void executeAction(String action, Actor subject) {

	}
	
}
