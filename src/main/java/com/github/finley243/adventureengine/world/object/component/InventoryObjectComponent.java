package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantInventory;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.InventoryObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public class InventoryObjectComponent extends ObjectComponent {

    private final Inventory inventory;

    InventoryObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
        this.inventory = new Inventory(null);
    }

    private InventoryObjectComponentTemplate getTemplateInventory() {
        return (InventoryObjectComponentTemplate) getTemplate();
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ScriptRuntime scriptRuntime) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(inventory.getExternalActions(game, getObject(), subject, getTemplateInventory().getTakePrompt(), getTemplateInventory().getTakePhrase(), getTemplateInventory().getStorePrompt(), getTemplateInventory().getStorePhrase(), getTemplateInventory().enableTake(), getTemplateInventory().enableStore()));
        for (ActionCustom.CustomActionHolder customAction : getTemplateInventory().getPerItemActions()) {
            for (Item item : inventory.getItems()) {
                ActionTemplate customActionTemplate = game.data().getActionTemplate(customAction.action());
                actions.add(new ActionCustom(null, getObject(), item, null, customActionTemplate, customAction.parameters(), new MenuDataObjectInventory(getObject(), item, false, false), false));
            }
        }
        return actions;
    }

    @Override
    public void onInit(Game game) {
        super.onInit(game);
        if (getTemplateInventory().getLootTable() != null) {
            getTemplateInventory().getLootTable().generateItems(game, inventory);
        }
    }

    @Override
    public void onStartRound(Game game) {
        inventory.onStartRound(game);
    }

    @Override
    protected String getStatName() {
        return "inventory";
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "inventory" -> (inventory == null ? null : new ExpressionConstantInventory(inventory));
            default -> super.getStatValue(name, context);
        };
    }

    public Inventory getInventory() {
        return inventory;
    }

}
