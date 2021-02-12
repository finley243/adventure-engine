package personal.finley.adventure_engine_2.world.environment;

import java.util.Set;

import personal.finley.adventure_engine_2.textgen.Context.Pronoun;
import personal.finley.adventure_engine_2.world.INoun;

public class Room implements INoun {

	private String ID;
	
	private boolean isExterior;
	private boolean isSoundDampened;
	
	private String name;
	private boolean isProperName;
	private String description;
	private boolean hasVisited;
	
	private Set<Area> areas;
	
	public Room(String ID, String name, boolean isProperName, String description, Set<Area> areas) {
		this.ID = ID;
		this.name = name;
		this.isProperName = isProperName;
		this.description = description;
		this.areas = areas;
		this.hasVisited = false;
	}
	
	public String getID() {
		return ID;
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
	public String getFormattedName() {
		return (isProperName() ? "" : "the ") + getName();
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
