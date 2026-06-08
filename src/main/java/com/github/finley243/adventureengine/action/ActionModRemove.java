package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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
    public Context getContext(Game game, Actor subject) {
        Context context = Context.builder(game).subject(subject).parentItem(target).build();
        context.setLocalVariable("mod", Expression.constant(mod));
        return context;
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        target.getComponentOfType(ItemComponentModdable.class).removeMod(game, mod);
        subject.getInventory().addItem(mod, game);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventoryCombine(target, subject.getInventory(), mod, subject.getInventory());
    }

    @Override
    public String getPrompt(Game game, Actor subject) {
        return "Remove";
    }

}
