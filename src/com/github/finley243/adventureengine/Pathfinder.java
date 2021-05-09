package com.github.finley243.adventureengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.world.environment.Area;

public class Pathfinder {

	public static int getDistance(Area area1, Area area2) {
		boolean isSameRoom = area1.getRoom() == area2.getRoom();
		if(isSameRoom) {
			return findPathLocal(area1, area2).size() - 1;
		} else {
			return 0;
		}
	}
	
	public static List<Area> findPathLocal(Area currentArea, Area targetArea) {
		Set<Area> visited = new HashSet<Area>();
		return findPathLocal(currentArea, targetArea, visited);
	}
	
	private static List<Area> findPathLocal(Area areaCurrent, Area areaTarget, Set<Area> hasVisited) {
		if(areaCurrent == areaTarget) {
			List<Area> path = new ArrayList<Area>();
			path.add(0, areaCurrent);
			return path;
		}
		hasVisited.add(areaCurrent);
		List<Area> shortestPath = null;
		for(Area linkedArea : areaCurrent.getLinkedAreas()) {
			if(!hasVisited.contains(linkedArea)) {
				List<Area> subPath = findPathLocal(linkedArea, areaTarget, hasVisited);
				subPath.add(0, areaCurrent);
				if(shortestPath == null || subPath.size() < shortestPath.size()) {
					shortestPath = subPath;
				}
			}
		}
		return shortestPath;
	}
	
}
