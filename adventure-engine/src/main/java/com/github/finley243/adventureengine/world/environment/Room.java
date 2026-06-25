package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a self-contained space (e.g. an actual room) that contains smaller areas
 */
public class Room extends GameInstanced implements Noun, ScriptValueHolder {

	private final ScriptRuntime scriptRuntime;

	private final String name;
	private final Area.AreaNameType nameType;
	private final boolean isProperName;
	private boolean isKnown;
	private final Scene description;
	private final Faction ownerFaction;
	private final Area.RestrictionType restrictionType;
	private final boolean allowAllies;

	private final Map<String, List<Script>> scripts;

	private boolean hasVisited;

	public Room(ScriptRuntime scriptRuntime, String ID, String name, Area.AreaNameType nameType, boolean isProperName, Scene description, Faction ownerFaction, Area.RestrictionType restrictionType, boolean allowAllies, Map<String, List<Script>> scripts) {
		super(ID);
		this.scriptRuntime = scriptRuntime;
		this.name = name;
		this.nameType = nameType;
		this.isProperName = isProperName;
		this.description = description;
		this.ownerFaction = ownerFaction;
		this.restrictionType = restrictionType;
		this.allowAllies = allowAllies;
		this.hasVisited = false;
		this.scripts = scripts;
	}

	public String getRelativeName() {
		return switch (nameType) {
			case IN -> "in";
			case ON -> "on";
			case NEAR -> "near";
			case FRONT -> "in front of";
			case SIDE -> "beside";
			case BEHIND -> "behind";
		};
	}

	public String getMovePhrase(Actor subject) {
		if (Objects.equals(subject.getArea().getRoom(), this)) {
			return switch (nameType) {
				case IN -> "@moveInWithin";
				case ON -> "@moveOnWithin";
				case FRONT -> "@moveFrontWithin";
				case BEHIND -> "@moveBehindWithin";
				case SIDE -> "@moveBesideWithin";
				case NEAR -> "@moveNearWithin";
			};
		}
		return switch (nameType) {
			case IN -> "@moveTo";
			case ON -> "@moveOnto";
			case FRONT -> "@moveFront";
			case BEHIND -> "@moveBehind";
			case SIDE -> "@moveBeside";
			case NEAR -> "@moveToward";
		};
	}
	
	public Scene getDescription() {
		return description;
	}
	
	public boolean hasVisited() {
		return hasVisited;
	}

	public void setVisited() {
		hasVisited = true;
	}

	public Faction getOwnerFaction() {
		return ownerFaction;
	}

	public Area.RestrictionType getRestrictionType() {
		return restrictionType;
	}

	public boolean allowAllies() {
		return allowAllies;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setKnown() {
		isKnown = true;
	}

	@Override
	public boolean isKnown() {
		return isKnown;
	}

	@Override
	public boolean isProperName() {
		return isProperName;
	}

	@Override
	public int pluralCount() {
		return 1;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public Expression getScriptValue(String name, Context context) {
		return switch (name) {
			case "noun" -> Expression.noun(this);
			case "name" -> Expression.string(getName());
			case "visited" -> Expression.bool(hasVisited());
			case "id" -> Expression.string(getID());
			case "owner_faction" -> Expression.string(ownerFaction.getID());
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		switch (name) {
			case "visited" -> {
				this.hasVisited = value.getValueBoolean();
				return true;
			}
		}
		return false;
	}

	public void triggerScript(String entryPoint, Context context) {
		if (scripts.containsKey(entryPoint)) {
			for (Script currentScript : scripts.get(entryPoint)) {
				currentScript.run(scriptRuntime, context);
			}
		}
	}

	@Override
	public int hashCode() {
		return getID().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Room && ((Room) o).getID().equals(this.getID());
	}
	
}
