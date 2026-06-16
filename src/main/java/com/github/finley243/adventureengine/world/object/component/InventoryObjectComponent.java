package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.InventoryObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public class InventoryObjectComponent extends ObjectComponent {

    private final Inventory inventory;

    InventoryObjectComponent(WorldObject object, ObjectComponentTemplate template, ItemFactory itemFactory) {
        super(object, template);
        this.inventory = new Inventory(itemFactory, null);
    }

    private InventoryObjectComponentTemplate getTemplateInventory() {
        return (InventoryObjectComponentTemplate) getTemplate();
    }

    public void generateInitialInventory(ItemFactory itemFactory, MutableRegistry<Item> itemMutableRegistry) {
        if (getTemplateInventory().getLootTable() != null) {
            getTemplateInventory().getLootTable().generateItems(inventory, itemFactory, itemMutableRegistry);
        }
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ActionDependencies dependencies) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(inventory.getExternalActions(subject, dependencies, getObject(), getTemplateInventory().getTakePrompt(), getTemplateInventory().getTakePhrase(), getTemplateInventory().getStorePrompt(), getTemplateInventory().getStorePhrase(), getTemplateInventory().enableTake(), getTemplateInventory().enableStore()));
        for (ActionCustom.CustomActionHolder customAction : getTemplateInventory().getPerItemActions()) {
            for (Item item : inventory.getItems()) {
                ActionTemplate customActionTemplate = customAction.action();
                actions.add(new ActionCustom(subject, dependencies, null, getObject(), item, null, customActionTemplate, customAction.parameters(), new MenuDataObjectInventory(getObject(), item, false, false), false));
            }
        }
        return actions;
    }

    @Override
    public void onStartRound(Game game) {
        inventory.onStartRound();
    }

    @Override
    protected String getStatName() {
        return "inventory";
    }

    @Override
    public Expression getScriptValue(String name, Context context) {
        return switch (name) {
            case "inventory" -> Expression.inventory(inventory);
            default -> super.getScriptValue(name, context);
        };
    }

    public Inventory getInventory() {
        return inventory;
    }

}
