package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

import java.util.*;

public class Pathfinder {

	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param startArea Start position to path from
	 * @param targetArea Position the path leads to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Area startArea, Area targetArea, String vehicleType) {
		Set<Area> targetSet = new HashSet<>();
		targetSet.add(targetArea);
		return findPath(startArea, targetSet, vehicleType);
	}

	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param startArea Start position to path from
	 * @param targetAreas Positions the path could lead to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Area startArea, Set<Area> targetAreas, String vehicleType) {
		if (targetAreas.contains(startArea)) return Collections.singletonList(startArea);
		Set<Area> hasVisited = new HashSet<>();
		Queue<List<Area>> paths = new LinkedList<>();
		List<Area> startPath = new ArrayList<>();
		startPath.add(startArea);
		paths.add(startPath);
		hasVisited.add(startArea);
		while (!paths.isEmpty()) {
			List<Area> currentPath = paths.remove();
			Area pathEnd = currentPath.get(currentPath.size() - 1);
			if (targetAreas.contains(pathEnd)) {
				return currentPath;
			}
			List<Area> linkedAreasGlobal = new ArrayList<>(pathEnd.getMovableAreas(vehicleType));
			for (WorldObject object : pathEnd.getObjects()) {
				List<ObjectComponentLink> linkComponents = object.getLinkComponents();
				if (!linkComponents.isEmpty()) {
					for (ObjectComponentLink linkComponent : object.getLinkComponents()) {
						linkedAreasGlobal.add(linkComponent.getLinkedObject().getArea());
					}
				}
			}
			Collections.shuffle(linkedAreasGlobal);
			for (Area linkedArea : linkedAreasGlobal) {
				if (!hasVisited.contains(linkedArea)) {
					List<Area> linkedPath = new ArrayList<>(currentPath);
					linkedPath.add(linkedArea);
					paths.add(linkedPath);
					hasVisited.add(linkedArea);
				}
			}
		}
		return null;
	}

	// TODO - Should use reachable areas, not movable areas
	public static Set<Area> areasInRange(Area origin, int range) {
		Queue<List<Area>> paths = new LinkedList<>();
		List<Area> startPath = new ArrayList<>();
		Set<Area> areasInRange = new HashSet<>();
		startPath.add(origin);
		paths.add(startPath);
		areasInRange.add(origin);
		while (!paths.isEmpty()) {
			List<Area> currentPath = paths.remove();
			Area pathEnd = currentPath.get(currentPath.size() - 1);
			List<Area> linkedAreasGlobal = new ArrayList<>(pathEnd.getMovableAreas(null));
			for (Area linkedArea : linkedAreasGlobal) {
				if (!areasInRange.contains(linkedArea)) {
					if (currentPath.size() - 1 < range) {
						List<Area> linkedPath = new ArrayList<>(currentPath);
						linkedPath.add(linkedArea);
						paths.add(linkedPath);
						areasInRange.add(linkedArea);
					}
				}
			}
		}
		return areasInRange;
	}

	// TODO - Should use reachable areas, not movable areas
	public static Set<Actor> actorsInRange(Area origin, int range, boolean throughExits) {
		Set<Area> visited = new HashSet<>();
		Queue<List<Area>> paths = new LinkedList<>();
		List<Area> startPath = new ArrayList<>();
		Set<Actor> actorsInRange = new HashSet<>();
		startPath.add(origin);
		paths.add(startPath);
		visited.add(origin);
		while (!paths.isEmpty()) {
			List<Area> currentPath = paths.remove();
			Area pathEnd = currentPath.get(currentPath.size() - 1);
			actorsInRange.addAll(pathEnd.getActors());
			List<Area> linkedAreasGlobal = new ArrayList<>(pathEnd.getMovableAreas(null));
			if (throughExits) {
				for (WorldObject object : pathEnd.getObjects()) {
					List<ObjectComponentLink> linkComponents = object.getLinkComponents();
					if (!linkComponents.isEmpty()) {
						for (ObjectComponentLink linkComponent : object.getLinkComponents()) {
							linkedAreasGlobal.add(linkComponent.getLinkedObject().getArea());
						}
					}
				}
			}
			for (Area linkedArea : linkedAreasGlobal) {
				if (!visited.contains(linkedArea)) {
					if (currentPath.size() - 1 < range) {
						List<Area> linkedPath = new ArrayList<>(currentPath);
						linkedPath.add(linkedArea);
						paths.add(linkedPath);
						visited.add(linkedArea);
					}
				}
			}
		}
		return actorsInRange;
	}

	// TODO - Should use reachable areas, not movable areas
	public static Actor nearestActor(Area origin, boolean throughExits) {
		Set<Area> visited = new HashSet<>();
		Queue<Area> areaQueue = new LinkedList<>();
		areaQueue.add(origin);
		visited.add(origin);
		while (!areaQueue.isEmpty()) {
			Area currentArea = areaQueue.remove();
			Set<Actor> currentAreaActors = currentArea.getActors();
			if (!currentAreaActors.isEmpty()) {
				return randomActorFromSet(currentAreaActors);
			}
			List<Area> linkedAreasGlobal = new ArrayList<>(currentArea.getMovableAreas(null));
			if (throughExits) {
				for (WorldObject object : currentArea.getObjects()) {
					List<ObjectComponentLink> linkComponents = object.getLinkComponents();
					if (!linkComponents.isEmpty()) {
						for (ObjectComponentLink linkComponent : object.getLinkComponents()) {
							linkedAreasGlobal.add(linkComponent.getLinkedObject().getArea());
						}
					}
				}
			}
			Collections.shuffle(linkedAreasGlobal);
			for (Area linkedArea : linkedAreasGlobal) {
				if (!visited.contains(linkedArea)) {
					areaQueue.add(linkedArea);
					visited.add(linkedArea);
				}
			}
		}
		return null;
	}

	private static Actor randomActorFromSet(Set<Actor> actors) {
		if (actors.isEmpty()) return null;
		List<Actor> actorList = new ArrayList<>(actors);
		return MathUtils.selectRandomFromList(actorList);
	}

}
