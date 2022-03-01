package com.github.finley243.adventureengine.actor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.*;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.actor.ai.*;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.item.ItemEquippable;
import com.github.finley243.adventureengine.world.item.ItemWeapon;
import com.github.finley243.adventureengine.world.object.UsableObject;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class Actor implements Noun, Physical {

	public static final boolean SHOW_HP_CHANGES = true;
	public static final int ACTIONS_PER_TURN = 2;
	public static final int ATTRIBUTE_MIN = 1;
	public static final int ATTRIBUTE_MAX = 10;
	public static final int SKILL_MIN = 1;
	public static final int SKILL_MAX = 10;
	
	public enum Attribute {
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
	public enum Skill {
		// BODY
		MELEE(Attribute.BODY),
		THROWING(Attribute.BODY),
		// INTELLIGENCE
		SOFTWARE(Attribute.INTELLIGENCE),
		HARDWARE(Attribute.INTELLIGENCE),
		MEDICINE(Attribute.INTELLIGENCE),
		// CHARISMA
		BARTER(Attribute.CHARISMA),
		PERSUASION(Attribute.CHARISMA),
		DECEPTION(Attribute.CHARISMA),
		// DEXTERITY
		HANDGUNS(Attribute.DEXTERITY),
		LONG_ARMS(Attribute.DEXTERITY),
		LOCKPICK(Attribute.DEXTERITY),
		// AGILITY
		STEALTH(Attribute.AGILITY),
		EVASION(Attribute.AGILITY);

		public final Attribute attribute;

		Skill(Attribute attribute) {
			this.attribute = attribute;
		}
	}

	private final StatsActor stats;
	private final String ID;
	private final String descriptor;
	// If isKnown = true, use definite article, else use indefinite article
	private boolean isKnown;
	private Area area;
	private int HP;
	private boolean isEnabled;
	private boolean isDead;
	private boolean isUnconscious;
	private boolean endTurn;
	private int actionPoints;
	private final Map<Action, Integer> blockedActions;
	// Index: 0 = base, 1 = modifier
	private final EnumMap<Attribute, int[]> attributes;
	private final EnumMap<Skill, int[]> skills;
	private final EffectComponent effectComponent;
	private final Inventory inventory;
	private final EquipmentComponent equipmentComponent;
	private final VendorComponent vendorComponent;
	private ItemEquippable equippedItem;
	private int money;
	private UsableObject usingObject;
	private boolean isCrouching;
	private final Set<CombatTarget> combatTargets;
	private final Set<PursueTarget> pursueTargets;
	private final InvestigateTarget investigateTarget;
	private final BehaviorIdle behaviorIdle;
	private final boolean preventMovement;

	public Actor(String ID, Area area, StatsActor stats, String descriptor, List<String> idle, boolean preventMovement, boolean startDead, boolean startDisabled) {
		this.ID = ID;
		this.area = area;
		this.stats = stats;
		this.descriptor = descriptor;
		this.preventMovement = preventMovement;
		this.combatTargets = new HashSet<>();
		this.pursueTargets = new HashSet<>();
		this.investigateTarget = new InvestigateTarget();
		this.isDead = startDead;
		this.isUnconscious = startDead;
		if(!startDead) {
			HP = stats.getMaxHP();
		}
		this.inventory = new Inventory();
		this.equipmentComponent = new EquipmentComponent(this);
		if(stats.isVendor()) {
			this.vendorComponent = new VendorComponent(this, stats.getVendorLootTable(), stats.vendorBuyTags(), stats.vendorBuyAll(), stats.vendorStartDisabled());
		} else {
			this.vendorComponent = null;
		}
		this.attributes = new EnumMap<>(Attribute.class);
		for(Attribute attribute : Attribute.values()) {
			this.attributes.put(attribute, new int[] {stats.getAttribute(attribute), 0});
		}
		this.skills = new EnumMap<>(Skill.class);
		for(Skill skill : Skill.values()) {
			this.skills.put(skill, new int[] {stats.getSkill(skill), 0});
		}
		this.effectComponent = new EffectComponent(this);
		if(stats.getLootTable() != null) {
			inventory.addItems(Data.getLootTable(stats.getLootTable()).generateItems());
		}
		this.blockedActions = new HashMap<>();
		this.behaviorIdle = new BehaviorIdle(idle);
		setEnabled(!startDisabled);
	}
	
	public String getID() {
		return ID;
	}
	
	@Override
	public String getName() {
		return (descriptor != null ? descriptor + " " : "") + stats.getName();
	}
	
	@Override
	public String getFormattedName() {
		return getFormattedName(!isKnown);
	}

	@Override
	public String getFormattedName(boolean indefinite) {
		if(!isProperName()) {
			return LangUtils.addArticle(getName(), indefinite);
		} else {
			return getName();
		}
	}

	@Override
	public void setKnown() {
		isKnown = true;
	}
	
	@Override
	public boolean isProperName() {
		return stats.isProperName();
	}
	
	@Override
	public Pronoun getPronoun() {
		return stats.getPronoun();
	}
	
	@Override
	public Area getArea() {
		return area;
	}
	
	@Override
	public void setArea(Area area) {
		this.area = area;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean enable) {
		if(isEnabled != enable) {
			isEnabled = enable;
			if(enable) {
				area.addActor(this);
			} else {
				area.removeActor(this);
			}
		}
	}
	
	public int getAttribute(Attribute attribute) {
		int[] values = attributes.get(attribute);
		int sum = values[0] + values[1];
		if(sum < ATTRIBUTE_MIN) {
			return ATTRIBUTE_MIN;
		} else if(sum > ATTRIBUTE_MAX) {
			return ATTRIBUTE_MAX;
		} else {
			return sum;
		}
	}
	
	public int getAttributeBase(Attribute attribute) {
		return attributes.get(attribute)[0];
	}
	
	public void setAttributeBase(Attribute attribute, int value) {
		attributes.get(attribute)[0] = value;
	}
	
	public void adjustAttributeBase(Attribute attribute, int value) {
		attributes.get(attribute)[0] += value;
	}
	
	public int getAttributeMod(Attribute attribute) {
		return attributes.get(attribute)[1];
	}
	
	public void setAttributeMod(Attribute attribute, int value) {
		attributes.get(attribute)[1] = value;
	}
	
	public void adjustAttributeMod(Attribute attribute, int value) {
		attributes.get(attribute)[1] += value;
	}

	public int getSkill(Skill skill) {
		int[] values = skills.get(skill);
		int sum = values[0] + values[1];
		if(sum < SKILL_MIN) {
			return SKILL_MIN;
		} else if(sum > SKILL_MAX) {
			return SKILL_MAX;
		} else {
			return sum;
		}
	}

	public int getSkillWithAttribute(Skill skill) {
		return getSkill(skill) + getAttribute(skill.attribute);
	}

	public int getSkillBase(Skill skill) {
		return skills.get(skill)[0];
	}

	public void setSkillBase(Skill skill, int value) {
		skills.get(skill)[0] = value;
	}

	public void adjustSkillBase(Skill skill, int value) {
		skills.get(skill)[0] += value;
	}

	public int getSkillMod(Skill skill) {
		return skills.get(skill)[1];
	}

	public void setSkillMod(Skill skill, int value) {
		skills.get(skill)[1] = value;
	}

	public void adjustSkillMod(Skill skill, int value) {
		skills.get(skill)[1] += value;
	}
	
	public String getTopicID() {
		return stats.getTopic();
	}
	
	public Faction getFaction() {
		return Data.getFaction(stats.getFaction());
	}
	
	public void move(Area area) {
		if(this.area != null) {
			this.area.removeActor(this);
		}
		setArea(area);
		area.addActor(this);
	}
	
	public boolean canMove() {
		return !isUsingObject() && !preventMovement;
	}

	public boolean isCrouching() {
		return isCrouching;
	}

	public void setCrouching(boolean state) {
		isCrouching = state;
	}
	
	public Inventory inventory() {
		return inventory;
	}

	public EquipmentComponent equipmentComponent() {
		return equipmentComponent;
	}

	public List<Limb> getLimbs() {
		return stats.getLimbs();
	}
	
	public void setEquippedItem(ItemEquippable item) {
		equippedItem = item;
	}

	public ItemEquippable getEquippedItem() {
		return equippedItem;
	}
	
	public boolean hasEquippedItem() {
		return equippedItem != null;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void adjustMoney(int value) {
		money += value;
	}

	public EffectComponent effectComponent() {
		return effectComponent;
	}

	public int getHP() {
		return HP;
	}

	public float getHPProportion() {
		return ((float) HP) / ((float) stats.getMaxHP());
	}
	
	public void heal(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount = Math.min(amount, stats.getMaxHP() - HP);
		HP += amount;
		Context context = new Context(this);
		if(SHOW_HP_CHANGES) {
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> gain<s> " + amount + " HP", context, null, null));
		}
		Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> <is> " + this.getConditionDescription(), context, null, null));
	}
	
	public void damage(int amount) {
		if(amount < 0) throw new IllegalArgumentException();
		amount -= equipmentComponent.getDamageResistance(EquipmentComponent.ApparelSlot.TORSO);
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged");
			Context context = new Context(this);
			if(SHOW_HP_CHANGES) {
				Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> lose<s> " + amount + " HP", context, null, null));
			}
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> <is> " + this.getConditionDescription(), context, null, null));
		}
	}

	public void damageLimb(int amount, Limb limb) {
		if(amount < 0) throw new IllegalArgumentException();
		amount -= equipmentComponent.getDamageResistance(limb.getApparelSlot());
		if(amount < 0) amount = 0;
		if(amount > 0) {
			limb.applyEffects(this);
		}
		amount *= limb.getDamageMult();
		HP -= amount;
		if(HP <= 0) {
			HP = 0;
			kill();
		} else {
			triggerScript("on_damaged");
			Context context = new Context(this);
			if(SHOW_HP_CHANGES) {
				Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> lose<s> " + amount + " HP", context, null, null));
			}
			Game.EVENT_BUS.post(new VisualEvent(getArea(), "<subject> <is> " + this.getConditionDescription(), context, null, null));
		}
	}
	
	public void kill() {
		triggerScript("on_death");
		isDead = true;
		Context context = new Context(this);
		Game.EVENT_BUS.post(new VisualEvent(getArea(), Phrases.get("die"), context, null, null));
		if(equippedItem != null) {
			getArea().addObject(equippedItem);
			equippedItem.setArea(getArea());
			context = new Context(this, equippedItem);
			Game.EVENT_BUS.post(new VisualEvent(getArea(), Phrases.get("forceDrop"), context, null, null));
			equippedItem = null;
		}
	}

	public String getConditionDescription() {
		float hpProportion = ((float) this.HP) / ((float) stats.getMaxHP());
		if(hpProportion == 1.0f) {
			return "in perfect condition";
		} else if(hpProportion >= 0.9f) {
			return "barely scratched";
		} else if(hpProportion >= 0.75f) {
			return "lightly injured";
		} else if(hpProportion >= 0.55f) {
			return "moderately injured";
		} else if(hpProportion >= 0.35f) {
			return "heavily injured";
		} else if(hpProportion >= 0.15f) {
			return "dangerously injured";
		} else {
			return "clinging to life";
		}
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public boolean isUnconscious() {
		return isUnconscious;
	}
	
	public boolean isActive() {
		return !isDead && !isUnconscious;
	}
	
	public void onVisualEvent(VisualEvent event) {
		if(event.getAction() instanceof ActionMoveArea) {
			for(CombatTarget target : combatTargets) {
				if(target.getTargetActor() == event.getSubject()) {
					target.setLastKnownArea(((ActionMoveArea) event.getAction()).getArea());
				}
			}
		} else if(event.getAction() instanceof ActionMoveExit) {
			for(CombatTarget target : combatTargets) {
				if(target.getTargetActor() == event.getSubject()) {
					target.setLastKnownArea(((ActionMoveExit) event.getAction()).getExit().getLinkedArea());
				}
			}
		} else if(event.getAction() instanceof ActionMoveElevator) {
			for(CombatTarget target : combatTargets) {
				if(target.getTargetActor() == event.getSubject()) {
					//List<Area> possibleAreas = new ArrayList<>(((ActionMoveElevator) event.getAction()).getElevator().getLinkedAreas());
					//target.setLastKnownArea(possibleAreas.get(ThreadLocalRandom.current().nextInt(possibleAreas.size())));
					target.setLastKnownArea(((ActionMoveElevator) event.getAction()).getDestination().getArea());
				}
			}
		}
	}
	
	public void onSoundEvent(SoundEvent event) {
		investigateTarget.setTargetArea(event.getOrigin());
		triggerScript("on_investigate_start");
	}
	
	public void startUsingObject(UsableObject object) {
		this.usingObject = object;
	}
	
	public void stopUsingObject() {
		this.usingObject = null;
	}

	public UsableObject getUsingObject() {
		return usingObject;
	}
	
	public boolean isUsingObject() {
		return this.usingObject != null;
	}
	
	public boolean isInCombat() {
		return combatTargets.size() > 0;
	}
	
	public boolean hasMeleeTargets() {
		for(CombatTarget target : combatTargets) {
			if(target.getTargetActor().getArea() == this.getArea()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRangedWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && ((ItemWeapon) equippedItem).isRanged();
	}
	
	public boolean hasMeleeWeaponEquipped() {
		return equippedItem != null && equippedItem instanceof ItemWeapon && !((ItemWeapon) equippedItem).isRanged();
	}

	public boolean hasWeapon() {
		for(Item item : inventory.getUniqueItems()) {
			if(item instanceof ItemWeapon) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCombatTarget(Actor actor) {
		for(CombatTarget target : combatTargets) {
			if(target.getTargetActor() == actor) {
				return true;
			}
		}
		return false;
	}
	
	public void addCombatTarget(Actor actor) {
		if(combatTargets.isEmpty()) {
			triggerScript("on_combat_start");
		}
		combatTargets.add(new CombatTarget(actor));
	}

	public Set<CombatTarget> getCombatTargets() {
		return combatTargets;
	}
	
	public void addPursueTarget(PursueTarget target) {
		pursueTargets.add(target);
	}
	
	public Set<PursueTarget> getPursueTargets() {
		return pursueTargets;
	}

	@Override
	public List<Action> localActions(Actor subject) {
		List<Action> action = new ArrayList<>();
		if(isActive()) {
			if(stats.getTopic() != null) {
				action.add(new ActionTalk(this));
			}
			if(vendorComponent != null) {
				action.addAll(vendorComponent.getActions(subject));
			}
		} else {
			action.addAll(inventory.getExternalActions(this, subject));
		}
		return action;
	}

	@Override
	public List<Action> adjacentActions(Actor subject) {
		List<Action> action = new ArrayList<>();
		if(isActive()) {
			if(stats.getTopic() != null) {
				action.add(new ActionTalk(this));
			}
			if(vendorComponent != null) {
				action.addAll(vendorComponent.getActions(subject));
			}
		}
		return action;
	}

	public List<Action> availableActions(){
		List<Action> actions = new ArrayList<>();
		if(hasEquippedItem()) {
			actions.addAll(equippedItem.equippedActions(this));
		}
		for(Actor actor : getArea().getActors()) {
			actions.addAll(actor.localActions(this));
		}
		for(Area nearArea : getArea().getNearAreas()) {
			for(Actor actor : nearArea.getActors()) {
				actions.addAll(actor.adjacentActions(this));
			}
		}
		for(WorldObject object : getArea().getObjects()) {
			actions.addAll(object.localActions(this));
		}
		for(Area nearArea : getArea().getNearAreas()) {
			for(WorldObject object : nearArea.getObjects()) {
				actions.addAll(object.adjacentActions(this));
			}
		}
		if(isUsingObject()) {
			actions.addAll(usingObject.usingActions());
		}
		if(canMove()) {
			actions.addAll(getArea().getMoveActions());
		}
		for(Item item : inventory.getUniqueItems()) {
			actions.addAll(item.inventoryActions(this));
		}
		for(ItemApparel item : equipmentComponent.getEquippedItems()) {
			actions.addAll(item.equippedActions(this));
		}
		if(isCrouching()) {
			actions.add(new ActionCrouchStop());
		} else {
			actions.add(new ActionCrouch());
		}
		for(Action currentAction : actions) {
			boolean isBlocked = false;
			for (Action blockedAction : blockedActions.keySet()) {
				if (!(blockedActions.get(blockedAction) > 0 && blockedAction.isRepeatMatch(currentAction)) && blockedAction.isBlockedMatch(currentAction)) {
					isBlocked = true;
					break;
				}
			}
			if (isBlocked) {
				currentAction.disable();
			}
		}
		actions.add(new ActionEnd());
		return actions;
	}
	
	public void takeTurn() {
		if(!isActive() || !isEnabled()) return;
		//triggerScript("on_start_turn");
		effectComponent().onStartTurn();
		updateCombatTargetsTurn();
		investigateTarget.nextTurn(this);
		behaviorIdle.update(this);
		this.actionPoints = ACTIONS_PER_TURN;
		this.blockedActions.clear();
		this.endTurn = false;
		while(!endTurn) {
			generateCombatTargets();
			updatePursueTargets();
			updateCombatTargets();
			investigateTarget.update(this);
			List<Action> availableActions = availableActions();
			for(Action action : availableActions) {
				if(actionPoints < action.actionPoints()) {
					action.disable();
				}
			}
			Action chosenAction = chooseAction(availableActions);
			actionPoints -= chosenAction.actionPoints();
			boolean actionIsBlocked = false;
			for(Action repeatAction : blockedActions.keySet()) {
				if(repeatAction.isRepeatMatch(chosenAction)) {
					int countRemaining = blockedActions.get(repeatAction) - 1;
					blockedActions.put(repeatAction, countRemaining);
					actionIsBlocked = true;
					break;
				}
			}
			if(!actionIsBlocked && chosenAction.repeatCount() > 0) {
				blockedActions.put(chosenAction, chosenAction.repeatCount() - 1);
			}
			chosenAction.choose(this);
		}
	}
	
	public void endTurn() {
		actionPoints = 0;
		endTurn = true;
	}
	
	public Action chooseAction(List<Action> actions) {
		int chaos = 1;
		List<List<Action>> bestActions = new ArrayList<>(chaos + 1);
		List<Float> maxWeights = new ArrayList<>(chaos + 1);
		for(int i = 0; i < chaos + 1; i++) {
			bestActions.add(new ArrayList<>());
			maxWeights.add(0.0f);
		}
		for(Action currentAction : actions) {
			if(currentAction.canChoose(this)) {
				float currentWeight = currentAction.utility(this);
				if(currentWeight != 0) {
					for (int i = 0; i < chaos + 1; i++) {
						if (currentWeight == maxWeights.get(i)) {
							bestActions.get(i).add(currentAction);
							break;
						} else if (currentWeight > maxWeights.get(i)) {
							maxWeights.remove(maxWeights.size() - 1);
							maxWeights.add(i, currentWeight);
							bestActions.remove(bestActions.size() - 1);
							bestActions.add(i, new ArrayList<>());
							bestActions.get(i).add(currentAction);
							break;
						}
					}
				}
			}
		}
		float weightSum = 0.0f;
		for(float weight : maxWeights) {
			weightSum += weight;
		}
		float partialWeightSum = 0.0f;
		float random = ThreadLocalRandom.current().nextFloat();
		for(int i = 0; i < chaos + 1; i++) {
			if(random < partialWeightSum + (maxWeights.get(i) / weightSum)) {
				return bestActions.get(i).get(ThreadLocalRandom.current().nextInt(bestActions.get(i).size()));
			} else {
				partialWeightSum += (maxWeights.get(i) / weightSum);
			}
		}
		return null;
	}
	
	private void generateCombatTargets() {
		for(Actor actor : getVisibleActors()) {
			if(actor != this && !actor.isDead()) {
				if(getFaction().getRelationTo(actor.getFaction().getID()) == FactionRelation.HOSTILE) {
					if(!isCombatTarget(actor)) {
						addCombatTarget(actor);
					}
				} else if(getFaction().getRelationTo(actor.getFaction().getID()) == FactionRelation.ASSIST) {
					for(CombatTarget allyTarget : actor.getCombatTargets()) {
						if(!isCombatTarget(allyTarget.getTargetActor())) {
							addCombatTarget(allyTarget.getTargetActor());
						}
					}
				} else if(getArea().getRoom().getOwnerFaction() != null && Data.getFaction(getArea().getRoom().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != FactionRelation.ASSIST) {
					if(!isCombatTarget(actor)) {
						addCombatTarget(actor);
					}
				}
			}
		}
	}

	private void updateCombatTargetsTurn() {
		for(CombatTarget target : combatTargets) {
			target.nextTurn();
		}
	}
	
	private void updateCombatTargets() {
		boolean hadTargets = !combatTargets.isEmpty();
		Iterator<CombatTarget> itr = combatTargets.iterator();
		while(itr.hasNext()) {
			CombatTarget target = itr.next();
			target.update(this);
			if(target.shouldRemove()) {
				itr.remove();
			}
		}
		if(hadTargets && combatTargets.isEmpty()) {
			triggerScript("on_combat_end");
		}
	}
	
	private void updatePursueTargets() {
		Iterator<PursueTarget> itr = pursueTargets.iterator();
		while(itr.hasNext()) {
			PursueTarget target = itr.next();
			target.update(this);
			if(target.shouldRemove()) {
				itr.remove();
			}
		}
	}

	public Set<Actor> getVisibleActors() {
		Set<Actor> visibleActors = new HashSet<>();
		Set<Area> visibleAreas = getArea().getVisibleAreas(this);
		for(Area visibleArea : visibleAreas) {
			boolean areaBehindCover = getArea().isBehindCover(visibleArea);
			for(Actor actor : visibleArea.getActors()) {
				if(actor != this && !(actor.isCrouching() && areaBehindCover)) {
					visibleActors.add(actor);
				}
			}
		}
		return visibleActors;
	}

	public Set<WorldObject> getVisibleObjects() {
		Set<WorldObject> visibleObjects = new HashSet<>();
		Set<Area> visibleAreas = getArea().getVisibleAreas(this);
		for(Area visibleArea : visibleAreas) {
			visibleObjects.addAll(visibleArea.getObjects());
		}
		return visibleObjects;
	}
	
	public boolean canSee(Actor target) {
		return getArea().getVisibleAreas(this).contains(target.getArea()) && (!target.isCrouching || !getArea().isBehindCover(target.getArea()));
	}

	public boolean triggerScript(String entryPoint) {
		if(stats.getScripts().containsKey(entryPoint)) {
			stats.getScripts().get(entryPoint).execute(this);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return ID.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Actor)) {
			return false;
		} else {
			return this == other;
		}
	}
	
}
