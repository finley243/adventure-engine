package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.menu.action.*;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {

	private ChoiceMenuEvent choiceMenuEvent;
	private NumericMenuEvent numericMenuEvent;
	
	public MenuManager() {
		this.choiceMenuEvent = null;
		this.numericMenuEvent = null;
	}

	/*public void pauseMenu(Game game) {
		waitForContinue(game);
	}*/
	
	public void actionChoiceMenu(ActionChoiceMenuEvent event, Game game, Actor actor, List<Action> actions) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		Map<String, MenuCategory> categoryMap = new HashMap<>();
		for (Action action : actions) {
			if (!action.canShow(actor)) continue;
			String parentCategory;
			String promptOverride = null;
			switch (action.getMenuData(actor)) {
				case MenuDataSelf dataSelf -> parentCategory = null;
				case MenuDataMove dataMove -> {
					String areaID = dataMove.destination.getID();
					String areaName = LangUtils.titleCase(dataMove.destination.getRelativeName() + " " + dataMove.destination.getName());
					if (!categoryMap.containsKey("area_" + areaID)) {
						categoryMap.put("area_" + areaID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "area_" + areaID, "areas", areaName));
					}
					if (!categoryMap.containsKey("areas")) {
						categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, "Areas"));
					}
					parentCategory = "area_" + areaID;
				}
				case MenuDataArea dataArea -> {
					String areaID = dataArea.destination.getID();
					String areaName = LangUtils.titleCase(dataArea.destination.getRelativeName() + " " + dataArea.destination.getName());
					if (!categoryMap.containsKey("area_" + areaID)) {
						categoryMap.put("area_" + areaID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "area_" + areaID, "areas", areaName));
					}
					if (!categoryMap.containsKey("areas")) {
						categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, "Areas"));
					}
					parentCategory = "area_" + areaID;
				}
				case MenuDataActor dataActor -> {
					String actorID = dataActor.actor.getID();
					String actorName = LangUtils.titleCase(dataActor.actor.getName());
					if (!categoryMap.containsKey("actor_" + actorID)) {
						categoryMap.put("actor_" + actorID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "actor_" + actorID, null, actorName));
					}
					parentCategory = "actor_" + actorID;
				}
				case MenuDataObject dataObject -> {
					String objectID = dataObject.object.getID();
					String objectName = LangUtils.titleCase(dataObject.object.getName());
					if (!categoryMap.containsKey("object_" + objectID)) {
						categoryMap.put("object_" + objectID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "object_" + objectID, null, objectName));
					}
					parentCategory = "object_" + objectID;
				}
				case MenuDataItemWorld dataItemWorld -> {
					String itemID = dataItemWorld.item.getID();
					String itemName = LangUtils.titleCase(dataItemWorld.item.getName());
					int itemCount = dataItemWorld.inv.itemCount(dataItemWorld.item);
					if (!categoryMap.containsKey("item_" + itemID)) {
						categoryMap.put("item_" + itemID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "item_" + itemID, null, itemName + (itemCount > 1 ? " (" + itemCount + ")" : "")));
					}
					parentCategory = "item_" + itemID;
				}
				case MenuDataInventory dataInventory -> {
					String itemID = dataInventory.item.getID();
					String itemName = LangUtils.titleCase(dataInventory.item.getName());
					int itemCount = dataInventory.inv.itemCount(dataInventory.item);
					if (!categoryMap.containsKey("inv_item_" + itemID)) {
						categoryMap.put("inv_item_" + itemID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "inv_item_" + itemID, "inventory", itemName + (itemCount > 1 ? " (" + itemCount + ")" : "")));
					}
					if (!categoryMap.containsKey("inventory")) {
						categoryMap.put("inventory", new MenuCategory(MenuCategory.CategoryType.INVENTORY, "inventory", null, "Inventory"));
					}
					parentCategory = "inv_item_" + itemID;
				}
				case MenuDataInventoryCombine dataInventoryCombine -> {
					String itemID = dataInventoryCombine.item.getID();
					String itemName = LangUtils.titleCase(dataInventoryCombine.item.getName());
					int itemCount = dataInventoryCombine.inv.itemCount(dataInventoryCombine.item);
					String combinedItemName = dataInventoryCombine.combinedItem.getName();
					int combinedItemCount = dataInventoryCombine.combinedInv.itemCount(dataInventoryCombine.combinedItem);
					if (!categoryMap.containsKey("inv_item_" + itemID)) {
						categoryMap.put("inv_item_" + itemID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "inv_item_" + itemID, "inventory", itemName + (itemCount > 1 ? " (" + itemCount + ")" : "")));
					}
					if (!categoryMap.containsKey("inventory")) {
						categoryMap.put("inventory", new MenuCategory(MenuCategory.CategoryType.INVENTORY, "inventory", null, "Inventory"));
					}
					String combineCategory = "inv_item_" + itemID + "_combine_" + action.getPrompt(actor);
					if (!categoryMap.containsKey(combineCategory)) {
						categoryMap.put(combineCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, combineCategory, "inv_item_" + itemID, action.getPrompt(actor)));
					}
					parentCategory = combineCategory;
					promptOverride = LangUtils.titleCase(combinedItemName + (combinedItemCount > 1 ? " (" + combinedItemCount + ")" : ""));
				}
				case MenuDataObjectInventory dataObjectInventory -> {
					String objectID = dataObjectInventory.object.getID();
					String objectName = LangUtils.titleCase(dataObjectInventory.object.getName());
					String itemID = dataObjectInventory.item.getID();
					String itemName = LangUtils.titleCase(dataObjectInventory.item.getName());
					String objectCategory = "object_" + objectID;
					if (!categoryMap.containsKey(objectCategory)) {
						categoryMap.put(objectCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, objectCategory, null, objectName));
					}
					String itemCategory = "object_" + objectID + "_inv_" + itemID;
					if (!categoryMap.containsKey(itemCategory)) {
						categoryMap.put(itemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, itemCategory, objectCategory, itemName));
					}
					parentCategory = itemCategory;
				}
				case MenuDataActorInventory dataActorInventory -> {
					String actorID = dataActorInventory.actor.getID();
					String actorName = LangUtils.titleCase(dataActorInventory.actor.getName());
					String itemID = dataActorInventory.item.getID();
					String itemName = LangUtils.titleCase(dataActorInventory.item.getName());
					String actorCategory = "actor_" + actorID;
					if (!categoryMap.containsKey(actorCategory)) {
						categoryMap.put(actorCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, actorCategory, null, actorName));
					}
					String itemCategory = "actor_" + actorID + "_inv_" + itemID;
					if (!categoryMap.containsKey(itemCategory)) {
						categoryMap.put(itemCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, itemCategory, actorCategory, itemName));
					}
					parentCategory = itemCategory;
				}
				case MenuDataNetwork dataNetwork -> {
					String nodeID = dataNetwork.node.getID();
					String nodeName = dataNetwork.node.getName();
					if (!categoryMap.containsKey("network_" + nodeID)) {
						categoryMap.put("network_" + nodeID, new MenuCategory(MenuCategory.CategoryType.GENERIC, "network_" + nodeID, null, nodeName));
					}
					parentCategory = "network_" + nodeID;
				}
				case MenuDataAttack dataAttack -> {
					String targetID = ((GameInstanced) dataAttack.target).getID();
					String targetName = LangUtils.titleCase(((Noun) dataAttack.target).getName());
					String weaponID = dataAttack.weapon.getID();
					String weaponName = LangUtils.titleCase(dataAttack.weapon.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, "Attack"));
					}
					String weaponCategory = "attack_weapon_" + weaponID;
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", weaponName));
					}
					String targetCategory = "attack_weapon" + weaponID + "_target_" + targetID;
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, weaponCategory, targetName));
					}
					parentCategory = targetCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackTargeted dataAttackTargeted -> {
					String targetID = ((GameInstanced) dataAttackTargeted.target).getID();
					String targetName = LangUtils.titleCase(((Noun) dataAttackTargeted.target).getName());
					String weaponID = dataAttackTargeted.weapon.getID();
					String weaponName = LangUtils.titleCase(dataAttackTargeted.weapon.getName());
					String limbID = dataAttackTargeted.limb.getID();
					String limbName = LangUtils.titleCase(dataAttackTargeted.limb.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, "Attack"));
					}
					String weaponCategory = "attack_weapon_" + weaponID;
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", weaponName));
					}
					String targetCategory = "attack_weapon" + weaponID + "_target_" + targetID;
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, weaponCategory, targetName));
					}
					String allLimbsCategory = "attack_weapon" + weaponID + "_target_" + targetID + "_limb";
					if (!categoryMap.containsKey(allLimbsCategory)) {
						categoryMap.put(allLimbsCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, allLimbsCategory, targetCategory, "Limbs"));
					}
					String limbCategory = "attack_weapon" + weaponID + "_target_" + targetID + "_limb_" + limbID;
					if (!categoryMap.containsKey(limbCategory)) {
						categoryMap.put(limbCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, limbCategory, allLimbsCategory, limbName));
					}
					parentCategory = limbCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackArea dataAttackArea -> {
					String targetID = dataAttackArea.target.getID();
					String targetName = LangUtils.titleCase(dataAttackArea.target.getName());
					String weaponID = dataAttackArea.weapon.getID();
					String weaponName = LangUtils.titleCase(dataAttackArea.weapon.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, "Attack"));
					}
					String weaponCategory = "attack_weapon_" + weaponID;
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", weaponName));
					}
					String areaCategory = "attack_weapon" + weaponID + "_target_area";
					if (!categoryMap.containsKey(areaCategory)) {
						categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, weaponCategory, "Area"));
					}
					String targetCategory = "attack_weapon" + weaponID + "_target_area_" + targetID;
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, areaCategory, targetName));
					}
					parentCategory = targetCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				default -> throw new IllegalStateException("Unexpected menu data: " + action.getMenuData(actor));
			}
			String prompt = action.getPrompt(actor);
			menuChoices.add(new MenuChoice((promptOverride != null ? promptOverride : action.getPrompt(actor)), action.canChoose(actor), action.actionPoints(actor), parentCategory, prompt));
		}
		List<MenuCategory> menuCategories = new ArrayList<>(categoryMap.values());
		startChoiceMenu(game, menuChoices, menuCategories, false);
	}

	public void sceneChoiceMenu(SceneChoiceMenuEvent event, Game game, List<SceneChoice> validChoices) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), new Action.CanChooseResult(true, null), -1, null));
		}
		startChoiceMenu(game, menuChoices, new ArrayList<>(), true);
	}

	public void attributeMenu(AttributeMenuEvent event, Game game, Actor actor, int points) {
		this.numericMenuEvent = event;
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String attribute : game.data().getAttributeIDs()) {
			int actorBase = actor.getAttributeBase(attribute);
			menuFields.add(new NumericMenuField(attribute, game.data().getAttribute(attribute).name(), actorBase, Actor.ATTRIBUTE_MAX, actorBase));
		}
		startNumericMenu(game, menuFields, points);
	}

	public void skillMenu(SkillMenuEvent event, Game game, Actor actor, int points) {
		this.numericMenuEvent = event;
		List<NumericMenuField> menuFields = new ArrayList<>();
		for (String skill : game.data().getSkillIDs()) {
			int actorBase = actor.getSkillBase(skill);
			menuFields.add(new NumericMenuField(skill, game.data().getSkill(skill).name(), actorBase, Actor.SKILL_MAX, actorBase));
		}
		startNumericMenu(game, menuFields, points);
	}

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, boolean forcePrompts) {
		game.eventBus().post(new RenderChoiceMenuEvent(menuChoices, menuCategories, forcePrompts));
	}

	private void startNumericMenu(Game game, List<NumericMenuField> menuFields, int points) {
		game.eventBus().post(new RenderNumericMenuEvent(menuFields, points));
	}
	
	@Subscribe
	public void onMenuSelectEvent(ChoiceMenuInputEvent e) {
		choiceMenuEvent.onChoiceMenuInput(e.getIndex());
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuInputEvent e) {
		numericMenuEvent.onNumericMenuInput(e.getValues());
	}
	
}
