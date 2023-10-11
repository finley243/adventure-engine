package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
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
				ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
				if (linkComponent == null) continue;
				linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable());
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

	public static Map<Area, VisibleAreaData> getVisibleAreas(Area origin, Actor actor) {
		Map<Area, VisibleAreaData> visibleAreas = getLineOfSightAreas(origin);
		for (Area area : new HashSet<>(visibleAreas.keySet())) {
			if (!area.isVisible(actor)) {
				visibleAreas.remove(area);
			}
			// TODO - Check object link visibility condition, check path obstructions
		}
		return visibleAreas;
	}

	public static Map<Area, VisibleAreaData> getLineOfSightAreas(Area origin) {
		Map<Area, VisibleAreaData> visibleAreas = new HashMap<>();
		List<Area> possiblyVisibleAreas = getPossiblyVisibleAreas(origin);
		Map<Area, AreaPathData> visibleMap = new HashMap<>();
		for (Area currentArea : possiblyVisibleAreas) {
			if (currentArea.equals(origin)) {
				visibleAreas.put(currentArea, new VisibleAreaData(null, Area.pathLengthToDistance(0)));
				visibleMap.put(currentArea, new AreaPathData(null, true, 0));
			} else {
				for (Area visibleArea : new HashSet<>(visibleMap.keySet())) {
					if (!visibleArea.hasDirectVisibleLinkTo(currentArea) || visibleArea.hasLineOfSightObstruction()) continue;
					AreaLink.CompassDirection linkDirection = visibleArea.getLinkDirectionTo(currentArea);
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
						visibleAreas.put(currentArea, new VisibleAreaData(visibleMap.get(currentArea).direction, Area.pathLengthToDistance(visibleMap.get(currentArea).minPathLength)));
					} else {
						visibleMap.remove(currentArea);
					}
				}
			}
		}
		return visibleAreas;
	}

	private static List<Area> getPossiblyVisibleAreas(Area origin) {
		List<Area> possiblyVisibleAreas = new ArrayList<>();
		Set<Area> possiblyVisibleAreaSet = new HashSet<>();
		Queue<AreaQueueData> areaQueue = new LinkedList<>();
		areaQueue.add(new AreaQueueData(origin, 0));
		possiblyVisibleAreaSet.add(origin);
		while (!areaQueue.isEmpty()) {
			AreaQueueData currentAreaData = areaQueue.remove();
			Area currentArea = currentAreaData.area();
			int currentDistance = currentAreaData.distance();
			possiblyVisibleAreas.add(currentArea);
			if (currentDistance < MAX_VISIBLE_DISTANCE) {
				for (Area linkedArea : currentArea.getDirectVisibleLinkedAreas()) {
					if (!possiblyVisibleAreaSet.contains(linkedArea)) {
						areaQueue.add(new AreaQueueData(linkedArea, currentDistance + 1));
						possiblyVisibleAreaSet.add(linkedArea);
					}
				}
			}
		}
		return possiblyVisibleAreas;
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
	public static Set<Actor> actorsInRange(Area origin, int range, boolean useObjectLinks) {
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
			if (useObjectLinks) {
				for (WorldObject object : pathEnd.getObjects()) {
					ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
					if (linkComponent == null) continue;
					linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable());
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
	public static Actor nearestActor(Area origin, boolean useObjectLinks) {
		Set<Area> visited = new HashSet<>();
		Queue<Area> areaQueue = new LinkedList<>();
		areaQueue.add(origin);
		visited.add(origin);
		while (!areaQueue.isEmpty()) {
			Area currentArea = areaQueue.remove();
			Set<Actor> currentAreaActors = currentArea.getActors();
			if (!currentAreaActors.isEmpty()) {
				return MathUtils.selectRandomFromSet(currentAreaActors);
			}
			List<Area> linkedAreasGlobal = new ArrayList<>(currentArea.getMovableAreas(null));
			if (useObjectLinks) {
				for (WorldObject object : currentArea.getObjects()) {
					ObjectComponentLink linkComponent = object.getComponentOfType(ObjectComponentLink.class);
					if (linkComponent == null) continue;
					linkedAreasGlobal.addAll(linkComponent.getLinkedAreasMovable());
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

	private record AreaQueueData(Area area, int distance) {}

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

	public record VisibleAreaData(AreaLink.CompassDirection direction, AreaLink.DistanceCategory distance) {}

}
