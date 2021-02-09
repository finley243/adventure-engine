package personal.finley.adventure_engine_2.world.environment;

import java.util.HashSet;
import java.util.Set;

import personal.finley.adventure_engine_2.EnumTypes.Pronoun;
import personal.finley.adventure_engine_2.world.INoun;

public class Room implements INoun {

	private boolean isExterior;
	private boolean isSoundDampened;
	
	private String name;
	private boolean isProperName;
	private String description;
	private boolean hasVisited;
	
	private Set<Area> areas;
	
	public Room(String name, boolean isProperName, String description) {
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
		this.areas = new HashSet<Area>();
		this.hasVisited = false;
	}
	
	public void addArea(Area area) {
		areas.add(area);
	}
	
	public Set<Area> getAreas(){
		return areas;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean hasVisited() {
		return hasVisited;
	}
	
	public void setVisited() {
		hasVisited = true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isProperName() {
		return isProperName;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}
	
}
