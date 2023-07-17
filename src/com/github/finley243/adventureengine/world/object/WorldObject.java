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
import com.github.finley243.adventureengine.expression.*;
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
	private int HP;
	private final Map<String, ObjectComponent> components;
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
	public boolean isPlural() {
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
		return getTemplate().getMaxHP() > 0 && HP > 0;
	}

	@Override
	public void damage(Damage damage, Context context) {
		int amount = damage.getAmount();
		amount -= getTemplate().getDamageResistance(damage.getType()) * damage.getArmorMult();
		amount -= amount * getTemplate().getDamageMult(damage.getType());
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
		for (Map.Entry<String, ObjectComponentTemplate> componentEntry : getTemplate().getComponents().entrySet()) {
			ObjectComponent component = ObjectComponentFactory.create(componentEntry.getValue(), componentEntry.getKey(), this);
			components.put(componentEntry.getKey(), component);
			if (component != null) {
				component.onNewGameInit();
			}
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
				ActionCustom action = new ActionCustom(game(), this, null, null, customAction.action(), customAction.parameters(), new String[] {LangUtils.titleCase(this.getName())}, false);
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

	public List<Action> networkActions(Actor subject, String[] menuPath) {
		List<Action> actions = new ArrayList<>();
		for (ActionCustom.CustomActionHolder networkAction : getTemplate().getNetworkActions()) {
			ActionCustom action = new ActionCustom(game(), this, null, null, networkAction.action(), networkAction.parameters(), menuPath, false);
			if (action.canShow(subject)) {
				actions.add(action);
			}
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

	public void triggerScript(String entryPoint, Context context) {
		if (getTemplate().getScripts().containsKey(entryPoint)) {
			getTemplate().getScripts().get(entryPoint).execute(context);
		}
	}

	@Override
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "enabled" -> new ExpressionConstantBoolean(isEnabled);
			case "hidden" -> new ExpressionConstantBoolean(isHidden);
			case "guarded" -> new ExpressionConstantBoolean(isGuarded());
			case "broken" -> new ExpressionConstantBoolean(isBroken());
			case "id" -> new ExpressionConstantString(getID());
			case "template_id" -> new ExpressionConstantString(templateID);
			case "area" -> new ExpressionConstantString(getArea().getID());
			case "room" -> new ExpressionConstantString(getArea().getRoom().getID());
			default -> localVars.getOrDefault(name, getTemplate().getLocalVarsDefault().get(name));
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		switch (name) {
			case "enabled" -> {
				setEnabled(value.getValueBoolean(context));
				return true;
			}
			case "hidden" -> {
				this.isHidden = value.getValueBoolean(context);
				return true;
			}
			case "area" -> {
				setArea(game().data().getArea(value.getValueString(context)));
				return true;
			}
			default -> {
				localVars.put(name, Expression.convertToConstant(value, context));
				return true;
			}
		}
	}

	@Override
	public void modStateInteger(String name, int amount) {
		Context context = new Context(game(), this);
		int valueOld = 0;
		if (localVars.containsKey(name)) {
			valueOld = localVars.get(name).getValueInteger(context);
		} else if (getTemplate().getLocalVarsDefault().containsKey(name)) {
			valueOld = getTemplate().getLocalVarsDefault().get(name).getValueInteger(context);
		}
		localVars.put(name, new ExpressionConstantInteger(valueOld + amount));
	}

	@Override
	public void modStateFloat(String name, float amount) {
		Context context = new Context(game(), this);
		float valueOld = 0;
		if (localVars.containsKey(name)) {
			valueOld = localVars.get(name).getValueFloat(context);
		} else if (getTemplate().getLocalVarsDefault().containsKey(name)) {
			valueOld = getTemplate().getLocalVarsDefault().get(name).getValueFloat(context);
		}
		localVars.put(name, new ExpressionConstantFloat(valueOld + amount));
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
