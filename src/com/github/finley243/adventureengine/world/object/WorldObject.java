package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectObject;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ObjectComponent;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentFactory;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;

import java.util.*;

/**
 * An object that can exist in the game world
 */
public class WorldObject extends GameInstanced implements Noun, Physical, StatHolder, AttackTarget {

	private final String templateID;
	private boolean isKnown;
	private boolean isEnabled;
	private boolean isHidden;
	private final Area defaultArea;
	private Area area;
	private final Map<String, ObjectComponent> components;
	private final Map<String, Boolean> localVarsBoolean;
	private final Map<String, Integer> localVarsInteger;
	private final Map<String, Float> localVarsFloat;
	private final Map<String, String> localVarsString;
	private final Map<String, Set<String>> localVarsStringSet;

	public WorldObject(Game gameInstance, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, Boolean> localVarsBooleanDefault, Map<String, Integer> localVarsIntegerDefault, Map<String, Float> localVarsFloatDefault, Map<String, String> localVarsStringDefault, Map<String, Set<String>> localVarsStringSetDefault) {
		super(gameInstance, ID);
		if (templateID == null) throw new IllegalArgumentException("Object template ID cannot be null: " + ID);
		this.templateID = templateID;
		this.defaultArea = area;
		this.area = area;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.localVarsBoolean = localVarsBooleanDefault;
		this.localVarsInteger = localVarsIntegerDefault;
		this.localVarsFloat = localVarsFloatDefault;
		this.localVarsString = localVarsStringDefault;
		this.localVarsStringSet = localVarsStringSetDefault;
		setEnabled(!startDisabled);
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
	public boolean canBeAttacked() {
		return false;
	}

	@Override
	public void damage(Damage damage) {
		// TODO - Fill in effects of attacking object
	}

	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		if (isEnabled) {
			this.area.removeObject(this);
			area.addObject(this);
			for (ObjectComponent component : components.values()) {
				component.onSetObjectArea(area);
			}
		}
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
			for (ObjectComponent component : components.values()) {
				component.onSetObjectEnabled(enable);
			}
		}
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void onNewGameInit() {
		for (Map.Entry<String, String> componentEntry : getTemplate().getComponents().entrySet()) {
			ObjectComponentTemplate componentTemplate = game().data().getObjectComponentTemplate(componentEntry.getValue());
			ObjectComponent component = ObjectComponentFactory.create(componentTemplate, componentEntry.getKey(), this);
			components.put(componentEntry.getKey(), component);
			if (component != null) {
				component.onNewGameInit();
			}
		}
	}

	public void onStartRound() {}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (!isGuarded()) {
			for (ObjectComponent component : components.values()) {
				if (component.isEnabled() && !component.actionsRestricted()) {
					actions.addAll(component.getActions(subject));
				}
			}
			for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
				ActionCustom action = new ActionCustom(game(), this, null, customAction.action(), customAction.parameters(), new String[] {LangUtils.titleCase(this.getName())}, false);
				if (action.canShow(subject)) {
					actions.add(action);
				}
			}
		}
		return actions;
	}

	@Override
	public List<Action> visibleActions(Actor subject) {
		List<Action> actions = new ArrayList<>();
		if (getDescription() != null) {
			actions.add(new ActionInspectObject(this));
		}
		return actions;
	}

	public boolean isGuarded() {
		for (Actor actor : getArea().getActors()) {
			if (actor.getBehaviorComponent().isGuarding(this)) {
				return true;
			}
		}
		return false;
	}

	public ObjectComponent getComponent(String componentID) {
		return components.get(componentID);
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
			getTemplate().getScripts().get(entryPoint).execute(new Context(game(), subject, target, this));
		}
	}

	@Override
	public int getValueInt(String name) {
		return localVarsInteger.getOrDefault(name, getTemplate().getLocalVarsIntegerDefault().getOrDefault(name, 0));
	}

	@Override
	public float getValueFloat(String name) {
		return localVarsFloat.getOrDefault(name, getTemplate().getLocalVarsFloatDefault().getOrDefault(name, 0.0f));
	}

	@Override
	public boolean getValueBoolean(String name) {
		return switch (name) {
			case "enabled" -> isEnabled;
			case "hidden" -> isHidden;
			case "guarded" -> isGuarded();
			default ->
					localVarsBoolean.getOrDefault(name, getTemplate().getLocalVarsBooleanDefault().getOrDefault(name, false));
		};
	}

	@Override
	public String getValueString(String name) {
		return switch (name) {
			case "id" -> getID();
			case "template_id" -> templateID;
			case "area" -> getArea().getID();
			case "room" -> getArea().getRoom().getID();
			default ->
					localVarsString.getOrDefault(name, getTemplate().getLocalVarsStringDefault().getOrDefault(name, null));
		};
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return localVarsStringSet.getOrDefault(name, getTemplate().getLocalVarsStringSetDefault().getOrDefault(name, null));
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		switch (name) {
			case "known" -> isKnown = value;
			case "enabled" -> setEnabled(value);
			case "hidden" -> isHidden = value;
			default -> localVarsBoolean.put(name, value);
		}
	}

	@Override
	public void setStateInteger(String name, int value) {
		localVarsInteger.put(name, value);
	}

	@Override
	public void setStateFloat(String name, float value) {
		localVarsFloat.put(name, value);
	}

	@Override
	public void setStateString(String name, String value) {
		if ("area".equals(name)) {
			setArea(game().data().getArea(value));
		} else {
			localVarsString.put(name, value);
		}
	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {
		localVarsStringSet.put(name, value);
	}

	@Override
	public void modStateInteger(String name, int amount) {
		int valueOld = localVarsInteger.getOrDefault(name, getTemplate().getLocalVarsIntegerDefault().getOrDefault(name, 0));
		localVarsInteger.put(name, valueOld + amount);
	}

	@Override
	public void modStateFloat(String name, float amount) {
		float valueOld = localVarsFloat.getOrDefault(name, getTemplate().getLocalVarsFloatDefault().getOrDefault(name, 0.0f));
		localVarsFloat.put(name, valueOld + amount);
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return switch (name) {
			case "component" -> getComponent(ID);
			case "area" -> getArea();
			default -> null;
		};
	}

	public void loadState(SaveData saveData) {
		switch (saveData.getParameter()) {
			case "is_known" -> this.isKnown = saveData.getValueBoolean();
			case "is_enabled" -> setEnabled(saveData.getValueBoolean());
			case "area" -> this.area = game().data().getArea(saveData.getValueString());
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (isKnown) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "is_known", isKnown));
		}
		if (!isEnabled) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "is_enabled", isEnabled));
		}
		if (area != defaultArea) {
			state.add(new SaveData(SaveData.DataType.OBJECT, this.getID(), "area", area.getID()));
		}
		return state;
	}

}
