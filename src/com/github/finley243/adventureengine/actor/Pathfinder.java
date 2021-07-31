package com.github.finley243.adventureengine.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class Pathfinder {
	
	/**
	 * Returns the shortest distance between two Areas
	 * @param area1 The first Area to measure between
	 * @param area2 The second Area to measure between
	 * @return The distance from one Area to the other
	 */
	public static int getDistance(Area area1, Area area2) {
		return findPath(area1, area2).size() - 1;
	}
	
	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param currentArea Start position to path from
	 * @param targetArea Position the path leads to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Area currentArea, Area targetArea) {
		Set<Area> visited = new HashSet<Area>();
		return findPath(currentArea, targetArea, visited, -1);
	}
	
	// sizeAllowed: length allowed for sub-path (if -1, any length is allowed), based on current shortest path
	private static List<Area> findPath(Area currentArea, Area targetArea, Set<Area> hasVisited, int sizeAllowed) {
		//System.out.println("findPath(" + currentArea + ", " + targetArea + ", " + hasVisited + ")");
		if(currentArea == targetArea) {
			List<Area> path = new ArrayList<Area>();
			path.add(0, currentArea);
			return path;
		} else if(sizeAllowed == 0) {
			return null;
		}
		hasVisited.add(currentArea);
		Set<Area> linkedAreasGlobal = new HashSet<Area>();
		linkedAreasGlobal.addAll(currentArea.getLinkedAreas());
		if(currentArea.getRoom() != targetArea.getRoom()) {
			for(WorldObject object : currentArea.getObjects()) {
				if(object instanceof ObjectExit) {
					linkedAreasGlobal.add(((ObjectExit) object).getLinkedArea());
				} else if(object instanceof ObjectElevator) {
					linkedAreasGlobal.addAll(((ObjectElevator) object).getLinkedAreas());
				}
			}
		}
		if(hasVisited.containsAll(linkedAreasGlobal)) {
			return null;
		}
		List<Area> shortestPath = null;
		for(Area linkedArea : linkedAreasGlobal) {
			if(!hasVisited.contains(linkedArea)) {
				List<Area> subPath = findPath(linkedArea, targetArea, hasVisited, (sizeAllowed == -1 ? -1 : sizeAllowed - 1));
				if(subPath != null) {
					subPath.add(0, currentArea);
					if(shortestPath == null || subPath.size() < shortestPath.size()) {
						shortestPath = subPath;
						sizeAllowed = subPath.size();
					}
				}
			}
		}
		hasVisited.remove(currentArea);
		return shortestPath;
	}
	
}
