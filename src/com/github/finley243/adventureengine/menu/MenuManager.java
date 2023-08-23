package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionEnd;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.*;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
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
					String areaName = LangUtils.titleCase(data.destination.getRelativeName() + " " + data.destination.getName() + (data.direction != null ? " (" + data.direction + ")" : ""));
					if (!categoryMap.containsKey("areas")) {
						categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, false, false, "Areas", null));
					}
					String areaCategory = "area_" + data.destination.getID();
					if (!categoryMap.containsKey(areaCategory)) {
						categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, "areas", false, false, areaName, null));
					}
					parentCategory = areaCategory;
				}
				case MenuDataArea data -> {
					String areaName = LangUtils.titleCase(data.destination.getRelativeName() + " " + data.destination.getName());
					if (!categoryMap.containsKey("areas")) {
						categoryMap.put("areas", new MenuCategory(MenuCategory.CategoryType.AREA, "areas", null, false, false, "Areas", null));
					}
					String areaCategory = "area_" + data.destination.getID();
					if (!categoryMap.containsKey(areaCategory)) {
						categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, "areas", false, false, areaName, null));
					}
					parentCategory = areaCategory;
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
						String itemDescription = "This is an item.\n\nValue: " + data.item.getSubHolder("template", null).getStatValue("price", new Context(game, game.data().getPlayer(), game.data().getPlayer())).getValueInteger(new Context(game, game.data().getPlayer(), game.data().getPlayer()));
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
					String inventoryCategory = "object_ " + data.object.getID() + (data.inventoryName != null ? "_inv_" + data.inventoryName : "_inv");
					if (!categoryMap.containsKey(inventoryCategory)) {
						categoryMap.put(inventoryCategory, new MenuCategory(MenuCategory.CategoryType.INVENTORY_TRANSFER, inventoryCategory, objectCategory, true, false, (data.inventoryName != null ? LangUtils.titleCase(data.inventoryName) : "Inventory"), null));
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
					String weaponName = LangUtils.titleCase(data.weapon.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, false, false, "Attack", null));
					}
					String weaponCategory = "attack_weapon_" + data.weapon.getID();
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", false, false, weaponName, null));
					}
					String targetCategory = "attack_weapon" + data.weapon.getID() + "_target_" + ((GameInstanced) data.target).getID();
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, weaponCategory, false, false, targetName, null));
					}
					parentCategory = targetCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackTargeted data -> {
					String targetName = LangUtils.titleCase(((Noun) data.target).getName());
					String weaponName = LangUtils.titleCase(data.weapon.getName());
					String limbName = LangUtils.titleCase(data.limb.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, false, false, "Attack", null));
					}
					String weaponCategory = "attack_weapon_" + data.weapon.getID();
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", false, false, weaponName, null));
					}
					String targetCategory = "attack_weapon" + data.weapon.getID() + "_target_" + ((GameInstanced) data.target).getID();
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, weaponCategory, false, false, targetName, null));
					}
					String limbCategory = "attack_weapon" + data.weapon.getID() + "_target_" + ((GameInstanced) data.target).getID() + "_limb_" + data.limb.getID();
					if (!categoryMap.containsKey(limbCategory)) {
						categoryMap.put(limbCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, limbCategory, targetCategory, false, false, limbName, null));
					}
					parentCategory = limbCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				case MenuDataAttackArea data -> {
					String targetName = LangUtils.titleCase(data.target.getName());
					String weaponName = LangUtils.titleCase(data.weapon.getName());
					if (!categoryMap.containsKey("attack")) {
						categoryMap.put("attack", new MenuCategory(MenuCategory.CategoryType.GENERIC, "attack", null, false, false, "Attack", null));
					}
					String weaponCategory = "attack_weapon_" + data.weapon.getID();
					if (!categoryMap.containsKey(weaponCategory)) {
						categoryMap.put(weaponCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, weaponCategory, "attack", false, false, weaponName, null));
					}
					String areaCategory = "attack_weapon" + data.weapon.getID() + "_target_area";
					if (!categoryMap.containsKey(areaCategory)) {
						categoryMap.put(areaCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, areaCategory, weaponCategory, false, false, "Area", null));
					}
					String targetCategory = "attack_weapon" + data.weapon.getID() + "_target_area_" + data.target.getID();
					if (!categoryMap.containsKey(targetCategory)) {
						categoryMap.put(targetCategory, new MenuCategory(MenuCategory.CategoryType.GENERIC, targetCategory, areaCategory, false, false, targetName, null));
					}
					parentCategory = targetCategory;
					promptOverride = action.getPrompt(actor) + " (" + ((ActionAttack) action).getChanceTag(actor) + ")";
				}
				default -> throw new IllegalStateException("Unexpected menu data: " + action.getMenuData(actor));
			}
			String prompt = action.getPrompt(actor);
			menuChoices.add(new MenuChoice((promptOverride != null ? promptOverride : action.getPrompt(actor)), action.canChoose(actor), action.actionPoints(actor), showOnRight, parentCategory, prompt));
		}
		List<MenuCategory> menuCategories = new ArrayList<>(categoryMap.values());
		startChoiceMenu(game, menuChoices, menuCategories, endTurnIndex, false);
	}

	public void sceneChoiceMenu(SceneChoiceMenuEvent event, Game game, List<SceneChoice> validChoices) {
		this.choiceMenuEvent = event;
		List<MenuChoice> menuChoices = new ArrayList<>();
		for (SceneChoice choice : validChoices) {
			menuChoices.add(new MenuChoice(choice.getPrompt(), new Action.CanChooseResult(true, null), -1, false, null));
		}
		startChoiceMenu(game, menuChoices, new ArrayList<>(), -1, true);
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

	private void startChoiceMenu(Game game, List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, int endTurnIndex, boolean forcePrompts) {
		game.eventBus().post(new RenderChoiceMenuEvent(menuChoices, menuCategories, endTurnIndex, forcePrompts));
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
