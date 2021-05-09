package com.github.finley243.adventureengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class Pathfinder {

	public static int getDistance(Area area1, Area area2) {
		return findPath(area1, area2).size() - 1;
	}
	
	public static List<Area> findPath(Area currentArea, Area targetArea) {
		Set<Area> visited = new HashSet<Area>();
		return findPath(currentArea, targetArea, visited);
	}
	
	private static List<Area> findPath(Area currentArea, Area targetArea, Set<Area> hasVisited) {
		System.out.println("findPath(" + currentArea + ", " + targetArea + ", " + hasVisited + ")");
		if(currentArea.getRoom() == targetArea.getRoom()) {
			return findPathLocal(currentArea, targetArea, hasVisited);
		} else {
			return findPathGlobal(currentArea, targetArea, hasVisited);
		}
	}
	
	private static List<Area> findPathLocal(Area currentArea, Area targetArea, Set<Area> hasVisited) {
		if(currentArea == targetArea) {
			List<Area> path = new ArrayList<Area>();
			path.add(0, currentArea);
			return path;
		}
		hasVisited.add(currentArea);
		List<Area> shortestPath = null;
		if(hasVisited.containsAll(currentArea.getLinkedAreas())) {
			return null;
		}
		for(Area linkedArea : currentArea.getLinkedAreas()) {
			if(!hasVisited.contains(linkedArea)) {
				List<Area> subPath = findPathLocal(linkedArea, targetArea, hasVisited);
				if(subPath != null) {
					subPath.add(0, currentArea);
					if(shortestPath == null || subPath.size() < shortestPath.size()) {
						shortestPath = subPath;
					}
				}
			}
		}
		hasVisited.remove(currentArea);
		return shortestPath;
	}
	
	private static List<Area> findPathGlobal(Area currentArea, Area targetArea, Set<Area> hasVisited) {
		if(currentArea == targetArea) {
			List<Area> path = new ArrayList<Area>();
			path.add(0, currentArea);
			return path;
		}
		hasVisited.add(currentArea);
		List<Area> shortestPath = null;
		Set<Area> linkedAreasGlobal = new HashSet<Area>();
		linkedAreasGlobal.addAll(currentArea.getLinkedAreas());
		for(WorldObject object : currentArea.getObjects()) {
			if(object instanceof ObjectExit) {
				linkedAreasGlobal.add(((ObjectExit) object).getLinkedArea());
			} else if(object instanceof ObjectElevator) {
				linkedAreasGlobal.addAll(((ObjectElevator) object).getLinkedAreas());
			}
		}
		if(hasVisited.containsAll(linkedAreasGlobal)) {
			return null;
		}
		for(Area linkedArea : linkedAreasGlobal) {
			if(!hasVisited.contains(linkedArea)) {
				List<Area> subPath = findPath(linkedArea, targetArea, hasVisited);
				if(subPath != null) {
					subPath.add(0, currentArea);
					if(shortestPath == null || subPath.size() < shortestPath.size()) {
						shortestPath = subPath;
					}
				}
			}
		}
		hasVisited.remove(currentArea);
		return shortestPath;
	}
	
}
