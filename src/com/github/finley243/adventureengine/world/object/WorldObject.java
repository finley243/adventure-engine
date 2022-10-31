package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectObject;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ObjectComponent;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object that can exist in the game world
 */
public class WorldObject extends GameInstanced implements Noun, Physical {

	private final String ID;
	private final String name;
	private boolean isKnown;
	private boolean isEnabled;
	private boolean isHidden;
	private final Area defaultArea;
	private Area area;
	private final Scene description;
	private final Map<String, Script> scripts;
	private final List<ActionCustom> customActions;
	private final Map<String, ObjectComponent> components;
	// Key: component ID, Value: linked object ID
	private final Map<String, String> linkedObjects;
	
	public WorldObject(Game gameInstance, String ID, Area area, String name, Scene description, boolean startDisabled, boolean startHidden, Map<String, Script> scripts, List<ActionCustom> customActions, Map<String, String> linkedObjects) {
		super(gameInstance);
		this.ID = ID;
		this.defaultArea = area;
		this.area = area;
		this.name = name;
		this.description = description;
		this.scripts = scripts;
		this.customActions = customActions;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.linkedObjects = linkedObjects;
		setEnabled(!startDisabled);
	}

	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Scene getDescription() {
		return description;
	}

	@Override
	public String getFormattedName() {
		if (!isProperName()) {
			return LangUtils.addArticle(getName(), !isKnown());
		} else {
			return getName();
		}
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
		return false;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public boolean forcePronoun() {
		return false;
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		this.area = area;
	}

	public void setEnabled(boolean enable) {
		if (area != null && isEnabled != enable) {
			isEnabled = enable;
			if (enable) {
				area.addObject(this);
			} else {
				area.removeObject(this);
			}
		}
	}

	public void setHidden(boolean hidden) {
		this.isHidden = hidden;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void onNewGameInit() {
		for (ObjectComponent component : components.values()) {
			component.newGameInit();
		}
	}

	public void onStartRound() {}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (description != null) {
			actions.add(new ActionInspectObject(this));
		}
		for (ObjectComponent component : components.values()) {
			actions.addAll(component.getActions(subject));
		}
		for (ActionCustom customAction : customActions) {
			if (customAction.canShow(subject)) {
				actions.add(customAction);
			}
		}
		return actions;
	}

	public boolean isGuarded() {
		for (Actor actor : getArea().getActors()) {
			if (actor.behaviorComponent().isGuarding(this)) {
				return true;
			}
		}
		return false;
	}

	public void addComponent(String componentID, ObjectComponent component) {
		components.put(componentID, component);
	}

	public ObjectComponent getComponent(String componentID) {
		return components.get(componentID);
	}

	public String getLinkedObjectID(String componentID) {
		if (linkedObjects != null) {
			return linkedObjects.get(componentID);
		}
		return null;
	}

	// TODO - This may need to be optimized (possibly store a separate set of link components?)
	public List<ObjectComponentLink> getLinkComponents() {
		List<ObjectComponentLink> linkComponents = new ArrayList<>();
		for (ObjectComponent component : components.values()) {
			if (component instanceof ObjectComponentLink) {
				linkComponents.add((ObjectComponentLink) component);
			}
		}
		return linkComponents;
	}

	public void triggerScript(String entryPoint, Actor subject, Actor target) {
		if (scripts.containsKey(entryPoint)) {
			scripts.get(entryPoint).execute(subject, target);
		}
	}

	public void loadState(SaveData saveData) {
		switch (saveData.getParameter()) {
			case "isKnown":
				this.isKnown = saveData.getValueBoolean();
				break;
			case "isEnabled":
				setEnabled(saveData.getValueBoolean());
				break;
			case "area":
				this.area = game().data().getArea(saveData.getValueString());
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (isKnown) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isKnown", isKnown));
		}
		if (!isEnabled) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "isEnabled", isEnabled));
		}
		if (area != defaultArea) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "area", area.getID()));
		}
		return state;
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WorldObject && ((WorldObject) o).getID().equals(this.getID());
	}

	@Override
	public String toString() {
		return this.getID();
	}

}
