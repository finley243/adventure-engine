package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

import java.util.*;

public class Pathfinder {

	public static final int MAX_VISIBLE_DISTANCE = 15;

	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param startArea Start position to path from
	 * @param targetArea Position the path leads to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Game game, Area startArea, Area targetArea, String vehicleType) {
		Set<Area> targetSet = new HashSet<>();
		targetSet.add(targetArea);
		return findPath(game, startArea, targetSet, vehicleType);
	}

	/**
	 * Returns a list of Areas that represents the shortest path between them
	 * @param startArea Start position to path from
	 * @param targetAreas Positions the path could lead to
	 * @return Shortest path from currentArea to targetArea
	 */
	public static List<Area> findPath(Game game, Area startArea, Set<Area> targetAreas, String vehicleType) {
		if (targetAreas.contains(startArea)) return Collections.singletonList(startArea);
		Map<Area, Area> predecessors = new HashMap<>();
		Queue<Area> queue = new LinkedList<>();
		queue.add(startArea);
		predecessors.put(startArea, null);
		while (!queue.isEmpty()) {
			Area currentArea = queue.remove();
			if (targetAreas.contains(currentArea)) {
				List<Area> path = new ArrayList<>();
				for (Area pathArea = currentArea; pathArea != null; pathArea = predecessors.get(pathArea)) {
					path.add(pathArea);
				}
				Collections.reverse(path);
				return path;
			}
			List<Area> linkedAreasGlobal = new ArrayList<>(currentArea.getMovableAreas(vehicleType));
			for (WorldObject object : currentArea.getObjects()) {
				ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
				if (linkComponent == null) continue;
				linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable(game));
			}
			for (Area linkedArea : linkedAreasGlobal) {
				if (!predecessors.containsKey(linkedArea)) {
					predecessors.put(linkedArea, currentArea);
					queue.add(linkedArea);
				}
			}
		}
		return null;
	}

	public static Map<Area, VisibleAreaData> getVisibleAreas(AreaRegistry areaRegistry, Area origin, Actor actor) {
		Map<Area, VisibleAreaData> visibleAreas = getLineOfSightAreas(areaRegistry, origin, actor.getAllBypassedObstructionTypes(), false);
		for (Area area : new HashSet<>(visibleAreas.keySet())) {
			if (!area.isVisible(actor)) {
				visibleAreas.remove(area);
			}
			// TODO - Check object link visibility condition, check path obstructions
		}
		return visibleAreas;
	}

	public static Map<Area, VisibleAreaData> getLineOfSightAreas(AreaRegistry areaRegistry, Area origin, Set<String> bypassedObstructions, boolean ignoreAllObstructions) {
		Map<Area, VisibleAreaData> visibleAreas = new HashMap<>();
		Map<String, AreaQueueData> possiblyVisibleAreas = getPossiblyVisibleAreas(origin, MAX_VISIBLE_DISTANCE);
		Map<Area, AreaPathData> visibleMap = new HashMap<>();
		for (Map.Entry<String, AreaQueueData> currentAreaEntry : possiblyVisibleAreas.entrySet()) {
			Area currentArea = currentAreaEntry.getValue().area();
			if (currentArea.equals(origin)) {
				List<Area> path = new ArrayList<>();
				path.add(origin);
				visibleAreas.put(currentArea, new VisibleAreaData(null, Area.pathLengthToDistance(0), path));
				visibleMap.put(currentArea, new AreaPathData(null, true, 0));
			} else {
				for (Area visibleArea : new HashSet<>(visibleMap.keySet())) {
					if (!visibleArea.hasDirectVisibleLinkTo(currentArea, areaRegistry) || (!ignoreAllObstructions && visibleArea.hasUnbypassedObstruction(game, bypassedObstructions))) continue;
					AreaLink.CompassDirection linkDirection = visibleArea.getLinkDirectionTo(currentArea, game);
					AreaLink.CompassDirection currentOriginDirection = combinedDirection(visibleMap.get(visibleArea).direction, linkDirection);
					int currentPathLength = visibleMap.get(visibleArea).minPathLength + 1;
					if (currentOriginDirection == null) continue;
					boolean isCurrentPathLinear = visibleMap.get(visibleArea).hasLinearPath && (visibleMap.get(visibleArea).direction == null || visibleMap.get(visibleArea).direction == linkDirection);
					boolean isCurrentPathVisible = isCurrentPathLinear || combinedDirection(visibleMap.get(visibleArea).direction, linkDirection) != null;
					if (!visibleMap.containsKey(currentArea)) {
						visibleMap.put(currentArea, new AreaPathData(currentOriginDirection, isCurrentPathLinear, currentPathLength));
					} else if (!visibleMap.get(currentArea).hasLinearPath && isCurrentPathLinear) {
						visibleMap.get(currentArea).direction = currentOriginDirection;
						visibleMap.get(currentArea).hasLinearPath = true;
					}
					if (isCurrentPathVisible) {
						visibleMap.get(currentArea).visibleLinkCount += 1;
					}
					if (visibleMap.get(currentArea).visibleLinkCount >= 2 || visibleMap.get(currentArea).hasLinearPath) break;
				}
				if (visibleMap.containsKey(currentArea)) {
					if (visibleMap.get(currentArea).visibleLinkCount >= 2 || visibleMap.get(currentArea).hasLinearPath) {
						visibleAreas.put(currentArea, new VisibleAreaData(visibleMap.get(currentArea).direction, Area.pathLengthToDistance(visibleMap.get(currentArea).minPathLength), currentAreaEntry.getValue().path()));
					} else {
						visibleMap.remove(currentArea);
					}
				}
			}
		}
		return visibleAreas;
	}

	private static Map<String, AreaQueueData> getPossiblyVisibleAreas(Area origin, int range) {
		Map<String, AreaQueueData> possiblyVisibleAreas = new LinkedHashMap<>();
		Queue<AreaQueueData> areaQueue = new LinkedList<>();
		List<Area> originPath = new ArrayList<>();
		originPath.add(origin);
		areaQueue.add(new AreaQueueData(origin, 0, originPath));
		while (!areaQueue.isEmpty()) {
			AreaQueueData currentAreaData = areaQueue.remove();
			Area currentArea = currentAreaData.area();
			int currentDistance = currentAreaData.distance();
			List<Area> currentPath = currentAreaData.path();
			possiblyVisibleAreas.put(currentArea.getID(), new AreaQueueData(currentArea, currentDistance, currentPath));
			if (currentDistance < range) {
				for (AreaLink areaLink : currentArea.getDirectVisibleLinkedAreas()) {
					if (!possiblyVisibleAreas.containsKey(areaLink.getArea().getID())) {
						Area linkedArea = areaLink.getArea();
						List<Area> extendedPath = new ArrayList<>(currentPath);
						extendedPath.add(linkedArea);
						AreaQueueData linkedAreaData = new AreaQueueData(linkedArea, currentDistance + 1, extendedPath);
						areaQueue.add(linkedAreaData);
					}
				}
			}
		}
		return possiblyVisibleAreas;
	}

	// TODO - Should use reachable areas, not movable areas
	public static Set<Area> areasInRange(Game game, Area origin, int range, boolean useObjectLinks) {
		Map<Area, Integer> visited = new HashMap<>();
		Queue<Area> queue = new LinkedList<>();
		queue.add(origin);
		visited.put(origin, 0);
		while (!queue.isEmpty()) {
			Area currentArea = queue.remove();
			int currentDepth = visited.get(currentArea);
			if (currentDepth >= range) continue;
			List<Area> linkedAreasGlobal = new ArrayList<>(currentArea.getMovableAreas(null));
			if (useObjectLinks) {
				for (WorldObject object : currentArea.getObjects()) {
					ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
					if (linkComponent == null) continue;
					linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable(game));
				}
			}
			for (Area linkedArea : linkedAreasGlobal) {
				if (!visited.containsKey(linkedArea)) {
					queue.add(linkedArea);
					visited.put(linkedArea, currentDepth + 1);
				}
			}
		}
		return visited.keySet();
	}

	// TODO - Should use reachable areas, not movable areas
	public static Set<Actor> actorsInRange(Game game, Area origin, int range, boolean useObjectLinks) {
		Set<Actor> actorsInRange = new HashSet<>();
		for (Area area : areasInRange(game, origin, range, useObjectLinks)) {
			actorsInRange.addAll(area.getActors());
		}
		return actorsInRange;
	}

	// TODO - Should use reachable areas, not movable areas
	public static Set<Actor> nearestActors(Game game, Area origin, boolean useObjectLinks) {
		Map<Area, Integer> visited = new HashMap<>();
		Queue<Area> queue = new LinkedList<>();
		queue.add(origin);
		visited.put(origin, 0);
		Set<Actor> nearestActors = new HashSet<>();
		int nearestDepth = Integer.MAX_VALUE;
		while (!queue.isEmpty()) {
			Area currentArea = queue.remove();
			int currentDepth = visited.get(currentArea);
			if (currentDepth > nearestDepth) break;
			Set<Actor> currentAreaActors = currentArea.getActors();
			if (!currentAreaActors.isEmpty()) {
				nearestDepth = currentDepth;
				nearestActors.addAll(currentAreaActors);
			}
			List<Area> linkedAreasGlobal = new ArrayList<>(currentArea.getMovableAreas(null));
			if (useObjectLinks) {
				for (WorldObject object : currentArea.getObjects()) {
					ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
					if (linkComponent == null) continue;
					linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable(game));
				}
			}
			for (Area linkedArea : linkedAreasGlobal) {
				if (!visited.containsKey(linkedArea)) {
					queue.add(linkedArea);
					visited.put(linkedArea, currentDepth + 1);
				}
			}
		}
		return nearestActors.isEmpty() ? null : nearestActors;
	}

	private static AreaLink.CompassDirection combinedDirection(AreaLink.CompassDirection dir1, AreaLink.CompassDirection dir2) {
		if (dir1 == null && dir2 != null) {
			return dir2;
		}
		if (dir1 != null && dir2 == null) {
			return dir1;
		}
		if (dir1 == dir2) {
			return dir1;
		}
		switch (dir1) {
			case N -> {
				if (dir2 == AreaLink.CompassDirection.NE || dir2 == AreaLink.CompassDirection.E) return AreaLink.CompassDirection.NE;
				if (dir2 == AreaLink.CompassDirection.NW || dir2 == AreaLink.CompassDirection.W) return AreaLink.CompassDirection.NW;
			}
			case S -> {
				if (dir2 == AreaLink.CompassDirection.SE || dir2 == AreaLink.CompassDirection.E) return AreaLink.CompassDirection.SE;
				if (dir2 == AreaLink.CompassDirection.SW || dir2 == AreaLink.CompassDirection.W) return AreaLink.CompassDirection.SW;
			}
			case W -> {
				if (dir2 == AreaLink.CompassDirection.NW || dir2 == AreaLink.CompassDirection.N) return AreaLink.CompassDirection.NW;
				if (dir2 == AreaLink.CompassDirection.SW || dir2 == AreaLink.CompassDirection.S) return AreaLink.CompassDirection.SW;
			}
			case E -> {
				if (dir2 == AreaLink.CompassDirection.NE || dir2 == AreaLink.CompassDirection.N) return AreaLink.CompassDirection.NE;
				if (dir2 == AreaLink.CompassDirection.SE || dir2 == AreaLink.CompassDirection.S) return AreaLink.CompassDirection.SE;
			}
			case NW -> {
				if (dir2 == AreaLink.CompassDirection.NE) return AreaLink.CompassDirection.N;
				if (dir2 == AreaLink.CompassDirection.SW) return AreaLink.CompassDirection.W;
				if (dir2 == AreaLink.CompassDirection.N || dir2 == AreaLink.CompassDirection.W) return AreaLink.CompassDirection.NW;
			}
			case NE -> {
				if (dir2 == AreaLink.CompassDirection.NW) return AreaLink.CompassDirection.N;
				if (dir2 == AreaLink.CompassDirection.SE) return AreaLink.CompassDirection.E;
				if (dir2 == AreaLink.CompassDirection.N || dir2 == AreaLink.CompassDirection.E) return AreaLink.CompassDirection.NE;
			}
			case SW -> {
				if (dir2 == AreaLink.CompassDirection.NW) return AreaLink.CompassDirection.W;
				if (dir2 == AreaLink.CompassDirection.SE) return AreaLink.CompassDirection.S;
				if (dir2 == AreaLink.CompassDirection.S || dir2 == AreaLink.CompassDirection.W) return AreaLink.CompassDirection.SW;
			}
			case SE -> {
				if (dir2 == AreaLink.CompassDirection.NE) return AreaLink.CompassDirection.E;
				if (dir2 == AreaLink.CompassDirection.SW) return AreaLink.CompassDirection.S;
				if (dir2 == AreaLink.CompassDirection.S || dir2 == AreaLink.CompassDirection.E) return AreaLink.CompassDirection.SE;
			}
		}
		return null;
	}

	private record AreaQueueData(Area area, int distance, List<Area> path) {}

	private static class AreaPathData {

		public AreaLink.CompassDirection direction;
		public int visibleLinkCount;
		public boolean hasLinearPath;
		public int minPathLength;

		AreaPathData(AreaLink.CompassDirection direction, boolean hasLinearPath, int minPathLength) {
			this.direction = direction;
			this.visibleLinkCount = 0;
			this.hasLinearPath = hasLinearPath;
			this.minPathLength = minPathLength;
		}

	}

	public record VisibleAreaData(AreaLink.CompassDirection direction, AreaLink.DistanceCategory distance, List<Area> path) {}

}
