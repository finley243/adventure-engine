package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectObject;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.*;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ComponentLink;
import com.github.finley243.adventureengine.world.object.component.ObjectComponent;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentFactory;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;

import java.util.*;

/**
 * An object that can exist in the game world
 */
public class WorldObject extends GameInstanced implements Noun, Physical, StatHolder {

	private final String ID;
	private final String templateID;
	private boolean isKnown;
	private boolean isEnabled;
	private boolean isHidden;
	private final Area defaultArea;
	private Area area;
	private final Map<String, ObjectComponent> components;
	// Key: component ID, Value: linked object and parameters
	private final Map<String, ComponentLink> linkedObjects;
	
	public WorldObject(Game gameInstance, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, ComponentLink> linkedObjects) {
		super(gameInstance);
		if (templateID == null) throw new IllegalArgumentException("Object template ID cannot be null: " + ID);
		this.ID = ID;
		this.templateID = templateID;
		this.defaultArea = area;
		this.area = area;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.linkedObjects = linkedObjects;
		setEnabled(!startDisabled);
	}

	public String getID() {
		return ID;
	}

	public ObjectTemplate getTemplate() {
		return game().data().getObjectTemplate(templateID);
	}
	
	@Override
	public String getName() {
		return getTemplate().getName();
	}
	
	public Scene getDescription() {
		return getTemplate().getDescription();
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
		for (String componentID : getTemplate().getComponents().keySet()) {
			ObjectComponentTemplate componentTemplate = getTemplate().getComponents().get(componentID);
			ObjectComponent component = ObjectComponentFactory.create(componentTemplate, componentID, this);
			addComponent(componentID, component);
			component.onNewGameInit();
		}
	}

	public void onStartRound() {}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (getDescription() != null) {
			actions.add(new ActionInspectObject(this));
		}
		for (ObjectComponent component : components.values()) {
			actions.addAll(component.getActions(subject));
		}
		for (ActionCustom customAction : getTemplate().getCustomActions()) {
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

	public ComponentLink getComponentLink(String componentID) {
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
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			getTemplate().getScripts().get(entryPoint).execute(subject, target);
		}
	}

	@Override
	public StatInt getStatInt(String name) {
		return null;
	}

	@Override
	public StatFloat getStatFloat(String name) {
		return null;
	}

	@Override
	public StatBoolean getStatBoolean(String name) {
		return null;
	}

	@Override
	public StatString getStatString(String name) {
		return null;
	}

	@Override
	public StatStringSet getStatStringSet(String name) {
		return null;
	}

	@Override
	public int getStatValueInt(String name) {
		return 0;
	}

	@Override
	public float getStatValueFloat(String name) {
		return 0;
	}

	@Override
	public boolean getStatValueBoolean(String name) {
		switch (name) {
			case "enabled":
				return isEnabled;
			case "hidden":
				return isHidden;
			case "guarded":
				return isGuarded();
		}
		return false;
	}

	@Override
	public String getStatValueString(String name) {
		switch (name) {
			case "id":
				return getID();
			case "templateID":
				return templateID;
			case "area":
				return getArea().getID();
			case "room":
				return getArea().getRoom().getID();
		}
		return null;
	}

	@Override
	public Set<String> getStatValueStringSet(String name) {
		return null;
	}

	@Override
	public void onStatChange() {

	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		switch (name) {
			case "known":
				isKnown = value;
				break;
			case "enabled":
				setEnabled(value);
				break;
			case "hidden":
				isHidden = value;
				break;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {

	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {
		switch (name) {
			case "area":
				setArea(game().data().getArea(value));
				break;
		}
	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modifyStateInteger(String name, int amount) {

	}

	@Override
	public void modifyStateFloat(String name, float amount) {

	}

	@Override
	public void triggerEffect(String name) {

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
