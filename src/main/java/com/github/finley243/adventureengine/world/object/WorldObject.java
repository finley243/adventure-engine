package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectObject;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.*;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;

import java.util.*;

/**
 * An object that can exist in the game world
 */
public class WorldObject extends GameInstanced implements Noun, Physical, StatHolder, AttackTarget {

	private final ObjectTemplate template;
	private boolean isKnown;
	private boolean isEnabled;
	private boolean isHidden;
	private Area area;
	private int HP;
	private final Map<Class<? extends ObjectComponent>, ObjectComponent> components;
	private Set<ObjectComponentLink.LinkDataIntermediate> objectLinks;
	private String vehicleObjectOverrideID;
	private final Map<String, Expression> localVars;

	public WorldObject(String ID, ObjectTemplate template, boolean startDisabled, boolean startHidden, Set<ObjectComponentLink.LinkDataIntermediate> objectLinks, String vehicleObjectOverrideID, Map<String, Expression> localVarsDefault) {
		super(ID);
		this.template = template;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.objectLinks = objectLinks;
		this.vehicleObjectOverrideID = vehicleObjectOverrideID;
		this.localVars = localVarsDefault;
		for (ObjectComponentTemplate componentTemplate : getTemplate().getComponents()) {
			ObjectComponent component = ObjectComponentFactory.create(componentTemplate, this);
            if (components.containsKey(component.getClass())) {
				throw new UnsupportedOperationException("Object " + this + " already contains a component of type " + component.getClass());
			}
			components.put(component.getClass(), component);
		}
		this.HP = getTemplate().getMaxHP();
		setEnabled(!startDisabled);
	}

	private ObjectTemplate getTemplate() {
		if (template == null) throw new IllegalStateException("WorldObject has not been initialized");
		return template;
	}

	public void resolveComponentReferences(Registry<WorldObject> objectRegistry) {
		if (components.containsKey(ObjectComponentLink.class)) {
			Map<String, ObjectComponentLink.LinkData> linkDataMap = new HashMap<>();
			for (ObjectComponentLink.LinkDataIntermediate unresolvedLinkData : objectLinks) {
				WorldObject linkedObject = objectRegistry.getFromID(unresolvedLinkData.objectID());
				if (linkedObject == null) throw new GameDataException("WorldObject has invalid linked object reference");
				linkDataMap.put(unresolvedLinkData.linkID(), new ObjectComponentLink.LinkData(unresolvedLinkData.linkID(), linkedObject, unresolvedLinkData.direction()));
			}
			ObjectComponentLink linkComponent = (ObjectComponentLink) components.get(ObjectComponentLink.class);
			linkComponent.resolveLinkedObjects(linkDataMap);
			this.objectLinks = null;
		}
		if (components.containsKey(ObjectComponentVehicle.class)) {
			WorldObject vehicleObjectOverride = objectRegistry.getFromID(vehicleObjectOverrideID);
			if (vehicleObjectOverrideID != null && vehicleObjectOverride == null) throw new GameDataException("WorldObject has invalid vehicle object override reference");
			ObjectComponentVehicle vehicleComponent = (ObjectComponentVehicle) components.get(ObjectComponentVehicle.class);
			vehicleComponent.resolveObjectOverride(vehicleObjectOverride);
			this.vehicleObjectOverrideID = null;
		}
	}
	
	@Override
	public String getName() {
		return getTemplate().getName();
	}
	
	public Scene getDescription() {
		return getTemplate().getDescription();
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
	public int pluralCount() {
		return 1;
	}

	@Override
	public Pronoun getPronoun() {
		return Pronoun.IT;
	}

	@Override
	public boolean canBeAttacked() {
		return getTemplate().getMaxHP() > 0 && HP > 0;
	}

	@Override
	public ComputedDamage applyEffectsAndComputeDamage(Damage damage, ScriptRuntime scriptRuntime, Context context) {
		int amount = damage.getAmount();
		amount -= Math.round(getTemplate().getDamageResistance(damage.getType()) * damage.getArmorMult());
		amount -= Math.round(amount * getTemplate().getDamageMult(damage.getType()));
		boolean isKillingBlow = HP - amount <= 0;
		return new ComputedDamage(amount, null, isKillingBlow);
	}

	@Override
	public void applyDamage(ComputedDamage computedDamage, ScriptRuntime scriptRuntime, Context context) {
		HP -= computedDamage.amount();
		Context objectContext = Context.from(context).clearVariables().build();
		if (HP <= 0) {
			HP = 0;
			triggerScript("on_broken", scriptRuntime, objectContext);
		} else {
			triggerScript("on_damaged", scriptRuntime, objectContext);
		}
	}

	public boolean isBroken() {
		return getTemplate().getMaxHP() > 0 && HP <= 0;
	}

	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		if (isEnabled) {
			if (this.area != null) {
				this.area.removeObject(this);
			}
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

	public void onStartRound(Game game) {
		for (ObjectComponent component : components.values()) {
			component.onStartRound(game);
		}
	}

	@Override
	public List<Action> localActions(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		List<Action> actions = new ArrayList<>();
		if (!isGuarded()) {
			for (ObjectComponent component : components.values()) {
				actions.addAll(component.getActions(subject));
			}
			for (ActionCustom.CustomActionHolder customAction : getTemplate().getCustomActions()) {
				ActionTemplate customActionTemplate = customAction.action();
				actions.add(new ActionCustom(scriptRuntime, sensoryEventDispatcher, null, this, null, null, customActionTemplate, customAction.parameters(), new MenuDataObject(this), false));
			}
		}
		return actions;
	}

	@Override
	public List<Action> visibleActions(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		List<Action> actions = new ArrayList<>();
		if (getDescription() != null) {
			actions.add(new ActionInspectObject(scriptRuntime, sensoryEventDispatcher, this));
		}
		return actions;
	}

	public List<Action> networkActions(Actor subject, NetworkNode node, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
		List<Action> actions = new ArrayList<>();
		for (ActionCustom.CustomActionHolder networkAction : getTemplate().getNetworkActions()) {
			ActionTemplate customNetworkActionTemplate =  networkAction.action();
			actions.add(new ActionCustom(scriptRuntime, sensoryEventDispatcher, null, this, null, null, customNetworkActionTemplate, networkAction.parameters(), new MenuDataNetwork(node), false));
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

	public boolean isVisible(Actor subject) {
        return !isHidden();
    }

	public <T extends ObjectComponent> T getComponentOfType(Class<T> componentClass) {
		ObjectComponent component = components.get(componentClass);
		return componentClass.cast(component);
	}

	public <T extends ObjectComponent> boolean hasComponentOfType(Class<T> componentClass) {
		return components.containsKey(componentClass);
	}

	public void triggerScript(String entryPoint, ScriptRuntime scriptRuntime, Context context) {
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			for (Script currentScript : getTemplate().getScripts().get(entryPoint)) {
				currentScript.run(scriptRuntime, context);
			}
		}
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		for (ObjectComponent component : components.values()) {
			Expression componentValue = component.getStatValue(name, context);
			if (componentValue != null) return componentValue;
		}
		return switch (name) {
			case "inventory" -> (hasComponentOfType(ObjectComponentInventory.class) ? new ExpressionConstantInventory(getComponentOfType(ObjectComponentInventory.class).getInventory()) : null);
			case "noun" -> new ExpressionConstantNoun(this);
			case "enabled" -> new ExpressionConstantBoolean(isEnabled);
			case "hidden" -> new ExpressionConstantBoolean(isHidden);
			case "guarded" -> new ExpressionConstantBoolean(isGuarded());
			case "broken" -> new ExpressionConstantBoolean(isBroken());
			case "id" -> new ExpressionConstantString(getID());
			case "name" -> new ExpressionConstantString(getName());
			case "template_id" -> new ExpressionConstantString(template.getID());
			case "area" -> new ExpressionConstantString(getArea().getID());
			case "room" -> new ExpressionConstantString(getArea().getRoom() != null ? getArea().getRoom().getID() : null);
			default -> getLocalVariable(name);
		};
	}

	public Expression getLocalVariable(String name) {
		return localVars.getOrDefault(name, getTemplate().getLocalVarsDefault().get(name));
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		for (ObjectComponent component : components.values()) {
			boolean success = component.setStatValue(name, value, context);
			if (success) return true;
		}
		switch (name) {
			case "enabled" -> {
				setEnabled(value.getValueBoolean());
				return true;
			}
			case "hidden" -> {
				this.isHidden = value.getValueBoolean();
				return true;
			}
			default -> {
				localVars.put(name, value);
				return true;
			}
		}
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return switch (name) {
			case "area" -> getArea();
			default -> null;
		};
	}

}

