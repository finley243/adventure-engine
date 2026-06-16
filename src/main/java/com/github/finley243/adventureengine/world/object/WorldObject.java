package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.load.GameDataException;
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
import com.github.finley243.adventureengine.script.ScriptValueHolder;
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
public class WorldObject extends GameInstanced implements Noun, Physical, ScriptValueHolder, AttackTarget {

	private final ObjectTemplate template;
	private boolean isKnown;
	private boolean isEnabled;
	private boolean isHidden;
	private Area area;
	private int HP;
	private final Map<Class<? extends ObjectComponent>, ObjectComponent> components;
	private Set<LinkObjectComponent.LinkDataIntermediate> objectLinks;
	private String vehicleObjectOverrideID;
	private final Map<String, Expression> localVars;
	private final Set<Actor> activeGuards;

	public WorldObject(String ID, ObjectTemplate template, boolean startDisabled, boolean startHidden, Set<LinkObjectComponent.LinkDataIntermediate> objectLinks, String vehicleObjectOverrideID, Map<String, Expression> localVarsDefault) {
		super(ID);
		this.template = template;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.objectLinks = objectLinks;
		this.vehicleObjectOverrideID = vehicleObjectOverrideID;
		this.localVars = localVarsDefault;
		this.activeGuards = new HashSet<>();
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
		if (components.containsKey(LinkObjectComponent.class)) {
			Map<String, LinkObjectComponent.LinkData> linkDataMap = new HashMap<>();
			for (LinkObjectComponent.LinkDataIntermediate unresolvedLinkData : objectLinks) {
				WorldObject linkedObject = objectRegistry.getFromID(unresolvedLinkData.objectID());
				if (linkedObject == null) throw new GameDataException("WorldObject has invalid linked object reference");
				linkDataMap.put(unresolvedLinkData.linkID(), new LinkObjectComponent.LinkData(unresolvedLinkData.linkID(), linkedObject, unresolvedLinkData.direction()));
			}
			LinkObjectComponent linkComponent = (LinkObjectComponent) components.get(LinkObjectComponent.class);
			linkComponent.resolveLinkedObjects(linkDataMap);
			this.objectLinks = null;
		}
		if (components.containsKey(VehicleObjectComponent.class)) {
			WorldObject vehicleObjectOverride = objectRegistry.getFromID(vehicleObjectOverrideID);
			if (vehicleObjectOverrideID != null && vehicleObjectOverride == null) throw new GameDataException("WorldObject has invalid vehicle object override reference");
			VehicleObjectComponent vehicleComponent = (VehicleObjectComponent) components.get(VehicleObjectComponent.class);
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
		int amount = damage.amount();
		amount -= Math.round(getTemplate().getDamageResistance(damage.type()) * damage.armorMult());
		amount -= Math.round(amount * getTemplate().getDamageMult(damage.type()));
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

	public void addGuard(Actor actor) {
		activeGuards.add(actor);
	}

	public void removeGuard(Actor actor) {
		activeGuards.remove(actor);
	}

	public boolean isGuarded() {
		activeGuards.removeIf(actor -> !actor.isEnabled() || !actor.isActive());
		return !activeGuards.isEmpty();
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
	public Expression getScriptValue(String name, Context context) {
		for (ObjectComponent component : components.values()) {
			Expression componentValue = component.getScriptValue(name, context);
			if (componentValue != null) return componentValue;
		}
		return switch (name) {
			case "inventory" -> (hasComponentOfType(InventoryObjectComponent.class) ? new InventoryExpression(getComponentOfType(InventoryObjectComponent.class).getInventory()) : null);
			case "noun" -> new NounExpression(this);
			case "enabled" -> new BooleanExpression(isEnabled);
			case "hidden" -> new BooleanExpression(isHidden);
			case "guarded" -> new BooleanExpression(isGuarded());
			case "broken" -> new BooleanExpression(isBroken());
			case "id" -> new StringExpression(getID());
			case "name" -> new StringExpression(getName());
			case "template_id" -> new StringExpression(template.getID());
			case "area" -> new StringExpression(getArea().getID());
			case "room" -> new StringExpression(getArea().getRoom() != null ? getArea().getRoom().getID() : null);
			default -> getLocalVariable(name);
		};
	}

	public Expression getLocalVariable(String name) {
		return localVars.getOrDefault(name, getTemplate().getLocalVarsDefault().get(name));
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		for (ObjectComponent component : components.values()) {
			boolean success = component.setScriptValue(name, value, context);
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
	public ScriptValueHolder getSubHolder(String name, String ID) {
		return switch (name) {
			case "area" -> getArea();
			default -> null;
		};
	}

}

