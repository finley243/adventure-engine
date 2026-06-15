package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionEnd;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Attribute;
import com.github.finley243.adventureengine.actor.Skill;
import com.github.finley243.adventureengine.actor.TurnController;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.menu.action.*;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.google.common.eventbus.Subscribe;

import java.util.*;

public class MenuManager {

	private final UIEventBus eventBus;
	private ScriptRuntime scriptRuntime;
	private final ThreadControl threadControl;

	private ChoiceMenuInputEvent choiceInput;
	private NumericMenuInputEvent numericInput;
	
	public MenuManager(UIEventBus eventBus) {
		this.eventBus = eventBus;
		this.threadControl = new ThreadControl();
	}

	public void setScriptRuntime(ScriptRuntime scriptRuntime) {
		if (this.scriptRuntime != null) throw new IllegalStateException("ScriptRuntime has already been set");
		this.scriptRuntime = scriptRuntime;
	}

	private ScriptRuntime getScriptRuntime() {
		if (this.scriptRuntime == null) throw new IllegalStateException("ScriptRuntime has not been set");
		return scriptRuntime;
	}
	
	public Action actionChoiceMenu(Actor actor, TurnController turnController, List<Action> actions) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		Map<String, MenuCategory> categoryMap = new HashMap<>();
		int endTurnIndex = -1;
		for (int i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			if (!action.canShow(actor)) continue;
			if (action instanceof ActionEnd) {
				endTurnIndex = i;
				continue;
			}
			String parentCategory;
			String promptOverride = null;
			boolean showOnRight = false;
			switch (action.getMenuData(actor)) {
				case MenuDataSelf data -> parentCategory = null;
				case MenuDataMove data -> {
					//String areaName = LangUtils.titleCase(/*data.destination.getRelativeName() + " " +*/ data.destination.getName());
					if (!categoryMap.containsKey("move")) {
						categoryMap.put("move", new MenuCategory(MenuCategory.CategoryType.AREA, "move", null, false, false, "Move", null));
					}
					parentCategory = "move";
					//promptOverride = action.getPrompt(actor) + " (" + areaName + (data.direction != null ? ", " + data.direction : "") + ")";
				}
				case MenuDataArea data -> {
					String areaName = LangUtils.titleCase(data.area.getRelativeName() + " " + data.area.getName());
					if (data.isCurrentArea) {
						parentCategory = null;
					} else {
						if (!categoryMap.containsKey("areas")) {
							categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, false, false, "Areas", null));
						}
						String areaCategory = "area_" + data.area.getID();
						if (!categoryMap.containsKey(areaCategory)) {
							categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, "areas", false, false, areaName, null));
						}
						parentCategory = areaCategory;
					}
				}
				case MenuDataActor data -> {
					String actorName = LangUtils.titleCase(data.actor.getName());
					String actorCategory = "actor_" + data.actor.getID();
					if (!categoryMap.containsKey(actorCategory)) {
						categoryMap.put(actorCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, actorCategory, null, false, false, actorName, null));
					}
					parentCategory = actorCategory;
				}
				case MenuDataObject data -> {
					String objectName = LangUtils.titleCase(data.object.getName());
					String objectCategory = "object_" + data.object.getID();
					if (!categoryMap.containsKey(objectCategory)) {
						categoryMap.put(objectCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, objectCategory, null, false, false, objectName, null));
					}
					parentCategory = objectCategory;
				}
				case MenuDataItemWorld data -> {
					String itemName = LangUtils.titleCase(data.item.getName());
					int itemCount = data.inv.itemCount(data.item);
					String itemCategory = "item_" + data.item.getID();
					if (!categoryMap.containsKey(itemCategory)) {
						categoryMap.put(itemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, itemCategory, null, false, false, itemName + (itemCount > 1 ? " (" + itemCount + ")" : ""), null));
					}
					parentCategory = itemCategory;
				}
				case MenuDataInventory data -> {
					String itemName = LangUtils.titleCase(data.item.getName());
					int itemCount = data.inv.itemCount(data.item);
					if (!categoryMap.containsKey("inventory")) {
						categoryMap.put("inventory", new MenuCategory(MenuCategory.CategoryType.INVENTORY, "inventory", null, true, false, "Inventory", null));
					}
					String invItemCategory = "inv_item_" + data.item.getID();
					if (!categoryMap.containsKey(invItemCategory)) {
						String itemDescription = "Temporary item description.";
						categoryMap.put(invItemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, invItemCategory, "inventory", false, false, itemName + (itemCount > 1 ? " (" + itemCount + ")" : ""), itemDescription));
					}
					parentCategory = invItemCategory;
				}
				case MenuDataInventoryCombine data -> {
					String itemName = LangUtils.titleCase(data.item.getName());
					int itemCount = data.inv.itemCount(data.item);
					String combinedItemName = LangUtils.titleCase(data.combinedItem.getName());
					int combinedItemCount = data.combinedInv.itemCount(data.combinedItem);
					if (!categoryMap.containsKey("inventory")) {
						categoryMap.put("inventory", new MenuCategory(MenuCategory.CategoryType.INVENTORY, "inventory", null, true, false, "Inventory", null));
					}
					String invItemCategory = "inv_item_" + data.item.getID();
					if (!categoryMap.containsKey(invItemCategory)) {
						categoryMap.put(invItemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, invItemCategory, "inventory", false, false, itemName + (itemCount > 1 ? " (" + itemCount + ")" : ""), null));
					}
					String combineCategory = "inv_item_" + data.item.getID() + "_combine_" + action.getPrompt(actor);
					if (!categoryMap.containsKey(combineCategory)) {
						categoryMap.put(combineCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, combineCategory, invItemCategory, false, false, action.getPrompt(actor), null));
					}
					parentCategory = combineCategory;
					promptOverride = combinedItemName + (combinedItemCount > 1 ? " (" + combinedItemCount + ")" : "");
				}
				case MenuDataObjectInventory data -> {
					String objectName = LangUtils.titleCase(data.object.getName());
					String itemName = LangUtils.titleCase(data.item.getName());
					String objectCategory = "object_" + data.object.getID();
					if (!categoryMap.containsKey(objectCategory)) {
						categoryMap.put(objectCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, objectCategory, null, false, false, objectName, null));
					}
					String inventoryCategory = "object_ " + data.object.getID() + "_inv";
					if (!categoryMap.containsKey(inventoryCategory)) {
						categoryMap.put(inventoryCategory, new MenuCategory(MenuCategory.CategoryType.INVENTORY_TRANSFER, inventoryCategory, objectCategory, true, false, "Inventory", null));
					}
					/*String itemCategory = "object_" + data.object.getID() + "_inv_" + data.item.getID();
					if (!categoryMap.containsKey(itemCategory)) {
						categoryMap.put(itemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, itemCategory, inventoryCategory, false, !data.isStoreAction, itemName, null));
					}
					parentCategory = itemCategory;*/
					promptOverride = itemName + " (" + action.getPrompt(actor) + ")";
					showOnRight = !data.isStoreAction;
					parentCategory = inventoryCategory;
				}
				case MenuDataActorInventory data -> {
					String actorName = LangUtils.titleCase(data.actor.getName());
					String itemName = LangUtils.titleCase(data.item.getName());
					String actorCategory = "actor_" + data.actor.getID();
					if (!categoryMap.containsKey(actorCategory)) {
						categoryMap.put(actorCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, actorCategory, null, false, false, actorName, null));
					}
					String inventoryCategory = "actor_ " + data.actor.getID() + "_inv";
					if (!categoryMap.containsKey(inventoryCategory)) {
						categoryMap.put(inventoryCategory, new MenuCategory(MenuCategory.CategoryType.INVENTORY_TRANSFER, inventoryCategory, actorCategory, true, false, "Inventory", null));
					}
					/*String itemCategory = "actor_" + data.actor.getID() + "_inv_" + data.item.getID();
					if (!categoryMap.containsKey(itemCategory)) {
						categoryMap.put(itemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, itemCategory, actorCategory, false, !data.isStoreAction, itemName, null));
					}
					parentCategory = itemCategory;*/
					promptOverride = itemName + " (" + action.getPrompt(actor) + ")";
					showOnRight = !data.isStoreAction;
					parentCategory = inventoryCategory;
				}
				case MenuDataNetwork data -> {
					String nodeName = data.node.getName();
					String nodeCategory = "network_" + data.node.getID();
					if (!categoryMap.containsKey(nodeCategory)) {
						categoryMap.put(nodeCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, nodeCategory, null, false, false, nodeName, null));
					}
					parentCategory = nodeCategory;
				}
				case MenuDataAttack data -> {
					String targetName = LangUtils.titleCase(((Noun) data.target).getName());
					String weaponName = data.weapon != null ? LangUtils.titleCase(data.weapon.getName()) : null;
					String targetCategory;
					if (data.target instanceof Actor targetActor) {
						targetCategory = "actor_" + targetActor.getID();
						if (!categoryMap.containsKey(targetCategory)) {
							categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, null, false, false, targetName, null));
						}
					} else if (data.target instanceof WorldObject targetObject) {
						targetCategory = "object_" + targetObject.getID();
						if (!categoryMap.containsKey(targetCategory)) {
							categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, null, false, false, targetName, null));
						}
					} else {
						throw new UnsupportedOperationException("No menu handling implemented for attack target type");
					}
					String attackCategory = targetCategory + "_attack";
					if (!categoryMap.containsKey(attackCategory)) {
						categoryMap.put(attackCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, attackCategory, targetCategory, false, false, "Attack", null));
					}
					if (data.weapon != null) {
						String weaponCategory = attackCategory + "_weapon_" + data.weapon.getID();
						if (!categoryMap.containsKey(weaponCategory)) {
							categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, attackCategory, false, false, weaponName, null));
						}
						parentCategory = weaponCategory;
					} else {
						parentCategory = attackCategory;
					}
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackTargeted data -> {
					String targetName = LangUtils.titleCase(((Noun) data.target).getName());
					String weaponName = data.weapon != null ? LangUtils.titleCase(data.weapon.getName()) : null;
					String limbName = LangUtils.titleCase(data.limb.getName());
					String targetCategory;
					if (data.target instanceof Actor targetActor) {
						targetCategory = "actor_" + targetActor.getID();
						if (!categoryMap.containsKey(targetCategory)) {
							categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, null, false, false, targetName, null));
						}
					} else if (data.target instanceof WorldObject targetObject) {
						targetCategory = "object_" + targetObject.getID();
						if (!categoryMap.containsKey(targetCategory)) {
							categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, null, false, false, targetName, null));
						}
					} else {
						throw new UnsupportedOperationException("No menu handling implemented for attack target type");
					}
					String attackCategory = targetCategory + "_attack";
					if (!categoryMap.containsKey(attackCategory)) {
						categoryMap.put(attackCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, attackCategory, targetCategory, false, false, "Attack", null));
					}
					String weaponCategory;
					if (data.weapon != null) {
						weaponCategory = attackCategory + "_weapon_" + data.weapon.getID();
						if (!categoryMap.containsKey(weaponCategory)) {
							categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, attackCategory, false, false, weaponName, null));
						}
					} else {
						weaponCategory = attackCategory;
					}
					String attackTypeCategory = weaponCategory + "_target_" + ((GameInstanced) data.target).getID() + "_targeted_" + ((ActionAttack) action).getAttackTypeID();
					String attackTypeName = action.getPrompt(actor);
					if (!categoryMap.containsKey(attackTypeCategory)) {
						categoryMap.put(attackTypeCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, attackTypeCategory, weaponCategory, false, false, attackTypeName, null));
					}
					parentCategory = attackTypeCategory;
					promptOverride = limbName + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackArea data -> {
					String targetName = LangUtils.titleCase(data.target.getName());
					String weaponName = data.weapon != null ? LangUtils.titleCase(data.weapon.getName()) : null;
					if (!categoryMap.containsKey("areas")) {
						categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, false, false, "Areas", null));
					}
					String areaCategory = "area_" + data.target.getID();
					if (!categoryMap.containsKey(areaCategory)) {
						categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, "areas", false, false, targetName, null));
					}
					if (data.weapon != null) {
						String weaponCategory = areaCategory + "_attack_weapon_" + data.weapon.getID();
						if (!categoryMap.containsKey(weaponCategory)) {
							categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, areaCategory, false, false, weaponName, null));
						}
						parentCategory = weaponCategory;
					} else {
						parentCategory = areaCategory;
					}
					/*String targetCategory = "attack_weapon" + data.weapon.getID() + "_target_area_" + data.target.getID();
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, weaponCategory, false, false, targetName, null));
					}*/
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				default -> throw new IllegalStateException("Unexpected menu data: " + action.getMenuData(actor));
			}
			String prompt = action.getPrompt(actor);
			int actionPoints = turnController.getFinalActionPointsForAction(action);
			menuChoices.add(new MenuChoice((promptOverride != null ? promptOverride : action.getPrompt(actor)), action.canChoose(actor), actionPoints, showOnRight, parentCategory, prompt));
		}
		List<MenuCategory> menuCategories = new ArrayList<>(categoryMap.values());
		ChoiceMenuInputEvent input = startChoiceMenu(menuChoices, menuCategories, endTurnIndex, false);
		return actions.get(input.getIndex());
	}

	public void sceneMenu(Scene scene, Context context, boolean clearText) {
		sceneMenu(scene, context, null, !clearText);
	}

	public void sceneMenu(Scene scene, Context context, Scene lastScene, boolean isFromRedirect) {
		scene.setTriggered();
		if (!isFromRedirect) {
			eventBus.post(new TextClearEvent());
		}
		switch (scene.getType()) {
			case ALL -> {
				for (SceneLine line : scene.getLines()) {
					Scene.SceneLineResult result = sceneLine(line, lastScene, context, false);
					if (result.exit()) {
						return;
					} else if (result.redirect() != null) {
						sceneMenu(result.redirect(), context, scene, true);
						return;
					}
				}
			}
			case SELECT -> {
				for (SceneLine line : scene.getLines()) {
					if (line.shouldShow(getScriptRuntime(), context, lastScene)) {
						Scene.SceneLineResult result = sceneLine(line, lastScene, context, true);
						if (result.exit()) {
							return;
						} else if (result.redirect() != null) {
							sceneMenu(result.redirect(), context, scene, true);
							return;
						}
						break;
					}
				}
			}
			case RANDOM -> {
				List<SceneLine> validLines = new ArrayList<>();
				for (SceneLine line : scene.getLines()) {
					if (line.shouldShow(getScriptRuntime(), context, lastScene)) {
						validLines.add(line);
					}
				}
				SceneLine selectedLine = MathUtils.selectRandomFromList(validLines);
				Scene.SceneLineResult result = sceneLine(selectedLine, lastScene, context, true);
				if (result.exit()) {
					return;
				} else if (result.redirect() != null) {
					sceneMenu(result.redirect(), context, scene, true);
					return;
				}
			}
		}
		if (!scene.getChoices().isEmpty()) {
			List<SceneChoice> validChoices = new ArrayList<>();
			for (SceneChoice choice : scene.getChoices()) {
				if (choice.getLinkedScene().canChoose(getScriptRuntime(), context)) {
					validChoices.add(choice);
				}
			}
			if (validChoices.isEmpty()) {
				return;
			}
			Scene chosenScene = sceneChoiceMenu(validChoices);
			sceneMenu(chosenScene, context, scene, false);
		}
	}

	private Scene.SceneLineResult sceneLine(SceneLine line, Scene lastScene, Context context, boolean ignoreCondition) {
		if (ignoreCondition || line.shouldShow(getScriptRuntime(), context, lastScene)) {
			line.setTriggered();
			if (line.getText() != null) {
				eventBus.post(new RenderTextEvent(line.getText()));
			}
			if (line.getScriptPre() != null) {
				line.getScriptPre().run(getScriptRuntime(), context);
			}
			if (line.getSubLines() != null) {
				switch (line.getType()) {
					case ALL -> {
						for (SceneLine subLine : line.getSubLines()) {
							Scene.SceneLineResult result = sceneLine(subLine, lastScene, context, false);
							if (result.exit() || result.redirect() != null) return result;
						}
					}
					case SELECT -> {
						for (SceneLine subLine : line.getSubLines()) {
							if (subLine.shouldShow(getScriptRuntime(), context, lastScene)) {
								Scene.SceneLineResult result = sceneLine(subLine, lastScene, context, true);
								if (result.exit() || result.redirect() != null) return result;
								break;
							}
						}
					}
					case RANDOM -> {
						List<SceneLine> validLines = new ArrayList<>(line.getSubLines().size());
						for (SceneLine subLine : line.getSubLines()) {
							if (subLine.shouldShow(getScriptRuntime(), context, lastScene)) {
								validLines.add(subLine);
							}
						}
						SceneLine selectedLine = MathUtils.selectRandomFromList(validLines);
						Scene.SceneLineResult result = sceneLine(selectedLine, lastScene, context, true);
						if (result.exit() || result.redirect() != null) return result;
					}
				}
			}
			if (line.getScriptPost() != null) {
				line.getScriptPost().run(getScriptRuntime(), context);
			}
			if (line.shouldExit()) {
				return new Scene.SceneLineResult(true, null);
			} else if (line.hasRedirect()) {
				return new Scene.SceneLineResult(false, line.getRedirect());
			}
		}
		return new Scene.SceneLineResult(false, null);
	}

	private Scene sceneChoiceMenu(List<SceneChoice> validChoices) {
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), new Action.CanChooseResult(true, null), -1, false, null));
		}
		ChoiceMenuInputEvent input = startChoiceMenu(menuChoices, new ArrayList<>(), -1, true);
		return validChoices.get(input.getIndex()).getLinkedScene();
	}

	public void attributeMenu(Actor actor, int points, Collection<Attribute> attributes) {
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (Attribute attribute : attributes) {
			int actorBase = actor.getAttributeBase(attribute.ID());
			menuFields.add(new NumericMenuField(attribute.ID(), attribute.name(), actorBase, Actor.ATTRIBUTE_MAX, actorBase));
		}
		NumericMenuInputEvent input = startNumericMenu(menuFields, points);
		for (Map.Entry<String, Integer> entry : input.getValues().entrySet()) {
			actor.setAttributeBase(entry.getKey(), entry.getValue());
		}
	}

	public void skillMenu(Actor actor, int points, Collection<Skill> skills) {
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (Skill skill : skills) {
			int actorBase = actor.getSkillBase(skill.ID());
			menuFields.add(new NumericMenuField(skill.ID(), skill.name(), actorBase, Actor.SKILL_MAX, actorBase));
		}
		NumericMenuInputEvent input = startNumericMenu(menuFields, points);
		for (Map.Entry<String, Integer> entry : input.getValues().entrySet()) {
			actor.setSkillBase(entry.getKey(), entry.getValue());
		}
	}

	private ChoiceMenuInputEvent startChoiceMenu(List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, int endTurnIndex, boolean forcePrompts) {
		eventBus.post(new RenderChoiceMenuEvent(menuChoices, menuCategories, endTurnIndex, forcePrompts));
		synchronized (threadControl) {
			while (this.choiceInput == null) {
				threadControl.pause();
			}
		}
		ChoiceMenuInputEvent input = this.choiceInput;
		this.choiceInput = null;
		return input;
	}

	private NumericMenuInputEvent startNumericMenu(List<NumericMenuField> menuFields, int points) {
		eventBus.post(new RenderNumericMenuEvent(menuFields, points));
		synchronized (threadControl) {
			while (this.numericInput == null) {
				threadControl.pause();
			}
		}
		NumericMenuInputEvent input = this.numericInput;
		this.numericInput = null;
		return input;
	}
	
	@Subscribe
	public void onMenuSelectEvent(ChoiceMenuInputEvent e) {
		synchronized (threadControl) {
			this.choiceInput = e;
			threadControl.unpause();
		}
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuInputEvent e) {
		synchronized (threadControl) {
			this.numericInput = e;
			threadControl.unpause();
		}
	}
	
}
