package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionInspectObject;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.menu.action.MenuDataNetwork;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ObjectComponent;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentFactory;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentInventory;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int HP;
	private final Map<Class<? extends ObjectComponent>, ObjectComponent> components;
	private final Map<String, Expression> localVars;

	public WorldObject(Game gameInstance, String ID, String templateID, Area area, boolean startDisabled, boolean startHidden, Map<String, Expression> localVarsDefault) {
		super(gameInstance, ID);
		if (templateID == null) throw new IllegalArgumentException("Object template ID cannot be null: " + ID);
		this.templateID = templateID;
		this.defaultArea = area;
		this.area = area;
		this.isHidden = startHidden;
		this.components = new HashMap<>();
		this.localVars = localVarsDefault;
		setEnabled(!startDisabled);
	}

	private ObjectTemplate getTemplate() {
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
	public void damage(Damage damage, Context context) {
		int amount = damage.getAmount();
		amount -= Math.round(getTemplate().getDamageResistance(damage.getType()) * damage.getArmorMult());
		amount -= Math.round(amount * getTemplate().getDamageMult(damage.getType()));
		HP -= amount;
		if (HP <= 0) {
			HP = 0;
			triggerScript("on_broken", new Context(game(), context.getSubject(), context.getTarget(), this));
		} else {
			triggerScript("on_damaged", new Context(game(), context.getSubject(), context.getTarget(), this));
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
		for (ObjectComponentTemplate componentTemplate : getTemplate().getComponents()) {
			ObjectComponent component = ObjectComponentFactory.create(componentTemplate, this);
			if (components.containsKey(component.getClass())) {
				throw new UnsupportedOperationException("Object " + this + " already contains a component of type " + component.getClass());
			}
			components.put(component.getClass(), component);
            component.onNewGameInit();
        }
		this.HP = getTemplate().getMaxHP();
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
				actions.add(new ActionCustom(game(), null, this, null, null, customAction.action(), customAction.parameters(), new MenuDataObject(this), false));
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

	public List<Action> networkActions(Actor subject, NetworkNode node) {
		List<Action> actions = new ArrayList<>();
		for (ActionCustom.CustomActionHolder networkAction : getTemplate().getNetworkActions()) {
			actions.add(new ActionCustom(game(), null, this, null, null, networkAction.action(), networkAction.parameters(), new MenuDataNetwork(node), false));
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
		ObjectComponent uncastComponents = components.get(componentClass);
		return componentClass.cast(uncastComponents);
	}

	public void triggerScript(String entryPoint, Context context) {
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			getTemplate().getScripts().get(entryPoint).execute(context);
		}
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		for (ObjectComponent component : components.values()) {
			Expression componentValue = component.getStatValue(name, context);
			if (componentValue != null) return componentValue;
		}
		return switch (name) {
			case "inventory" -> (getComponentOfType(ObjectComponentInventory.class) == null ? null : new ExpressionConstantInventory(getComponentOfType(ObjectComponentInventory.class).getInventory()));
			case "noun" -> new ExpressionConstantNoun(this);
			case "enabled" -> new ExpressionConstantBoolean(isEnabled);
			case "hidden" -> new ExpressionConstantBoolean(isHidden);
			case "guarded" -> new ExpressionConstantBoolean(isGuarded());
			case "broken" -> new ExpressionConstantBoolean(isBroken());
			case "id" -> new ExpressionConstantString(getID());
			case "name" -> new ExpressionConstantString(getName());
			case "template_id" -> new ExpressionConstantString(templateID);
			case "area" -> new ExpressionConstantString(getArea().getID());
			case "room" -> new ExpressionConstantString(getArea().getRoom().getID());
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
			case "area" -> {
				setArea(game().data().getArea(value.getValueString()));
				return true;
			}
			default -> {
				localVars.put(name, Expression.convertToConstant(value));
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
