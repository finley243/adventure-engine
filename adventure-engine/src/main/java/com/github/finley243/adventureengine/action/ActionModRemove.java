package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ModdableItemComponent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;

public class ActionModRemove extends Action {

    private final Item target;
    private final Item mod;

    public ActionModRemove(Actor subject, ActionDependencies dependencies, Item target, Item mod) {
        super(subject, dependencies);
        this.target = target;
        this.mod = mod;
    }

    @Override
    public String getID() {
        return "item_mod_remove";
    }

    @Override
    public Context getContext() {
        Context context = Context.builder().subject(subject).parentItem(target).build();
        context.setLocalVariable("mod", Expression.valueHolder(mod));
        return context;
    }

    @Override
    public void choose(int repeatActionCount) {
        target.getComponentOfType(ModdableItemComponent.class).removeMod(mod);
        subject.getInventory().addItem(mod);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataInventoryCombine(target, subject.getInventory(), mod, null, subject.getInventory());
    }

    @Override
    public String getPrompt() {
        return "Remove";
    }

}
