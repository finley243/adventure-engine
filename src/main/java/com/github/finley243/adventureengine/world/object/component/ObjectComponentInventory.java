package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantInventory;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentInventory extends ObjectComponent {

    private final Inventory inventory;

    public ObjectComponentInventory(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
        this.inventory = new Inventory(object.game(), null);
    }

    private ObjectComponentTemplateInventory getTemplateInventory() {
        return (ObjectComponentTemplateInventory) getTemplate();
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(inventory.getExternalActions(getObject(), subject, getTemplateInventory().getTakePrompt(), getTemplateInventory().getTakePhrase(), getTemplateInventory().getStorePrompt(), getTemplateInventory().getStorePhrase(), getTemplateInventory().enableTake(), getTemplateInventory().enableStore()));
        for (ActionCustom.CustomActionHolder customAction : getTemplateInventory().getPerItemActions()) {
            for (Item item : inventory.getItems()) {
                actions.add(new ActionCustom(getObject().game(), null, getObject(), item, null, customAction.action(), customAction.parameters(), new MenuDataObjectInventory(getObject(), item, false, false), false));
            }
        }
        return actions;
    }

    @Override
    public void onInit() {
        super.onInit();
        if (getTemplateInventory().getLootTable() != null) {
            getTemplateInventory().getLootTable().generateItems(getObject().game(), inventory);
        }
    }

    @Override
    public void onStartRound() {
        inventory.onStartRound();
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
