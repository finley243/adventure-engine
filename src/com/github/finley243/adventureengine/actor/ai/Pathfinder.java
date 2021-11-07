package com.github.finley243.adventureengine.actor.ai;

import java.util.*;

import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class Pathfinder {

	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param startArea Start position to path from
	 * @param targetArea Position the path leads to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Area startArea, Area targetArea) {
		Set<Area> hasVisited = new HashSet<>();
		Queue<List<Area>> paths = new LinkedList<>();
		List<Area> startPath = new ArrayList<>();
		startPath.add(startArea);
		paths.add(startPath);
		hasVisited.add(startArea);
		while(!paths.isEmpty()) {
			List<Area> currentPath = paths.remove();
			Area pathEnd = currentPath.get(currentPath.size() - 1);
			if(pathEnd.equals(targetArea)) {
				return currentPath;
			}
			Set<Area> linkedAreasGlobal = new HashSet<>(pathEnd.getLinkedAreas());
			if(pathEnd.getRoom() != targetArea.getRoom()) {
				for(WorldObject object : pathEnd.getObjects()) {
					if(object instanceof ObjectExit) {
						linkedAreasGlobal.add(((ObjectExit) object).getLinkedArea());
					} else if(object instanceof ObjectElevator) {
						linkedAreasGlobal.addAll(((ObjectElevator) object).getLinkedAreas());
					}
				}
			}
			for(Area linkedArea : linkedAreasGlobal) {
				if(!hasVisited.contains(linkedArea)) {
					List<Area> linkedPath = new ArrayList<>(currentPath);
					linkedPath.add(linkedArea);
					paths.add(linkedPath);
					hasVisited.add(linkedArea);
				}
			}
		}
		return null;
	}
	
}
