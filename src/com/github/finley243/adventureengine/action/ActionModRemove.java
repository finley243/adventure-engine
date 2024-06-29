package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentModdable;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventoryCombine;

public class ActionModRemove extends Action {

    private final Item target;
    private final Item mod;

    public ActionModRemove(Item target, Item mod) {
        this.target = target;
        this.mod = mod;
    }

    @Override
    public String getID() {
        return "item_mod_remove";
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = new Context(subject.game(), subject, null, target);
        context.setLocalVariable("mod", Expression.constant(mod));
        return context;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        target.getComponentOfType(ItemComponentModdable.class).removeMod(mod);
        subject.getInventory().addItem(mod);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(target, subject.getInventory(), mod, subject.getInventory());
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Remove";
    }

}
