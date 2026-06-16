package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.MagazineItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.IntStat;
import com.github.finley243.adventureengine.stat.Stat;

import java.util.List;

public class MagazineItemComponent extends ItemComponent {

    private final IntStat clipSize;
    private final IntStat reloadActionPoints;

    private Item ammoType;
    private int ammoCount;

    public MagazineItemComponent(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.clipSize = new IntStat("clip_size", item);
        this.reloadActionPoints = new IntStat("reload_action_points", item);
        this.ammoType = null;
        this.ammoCount = 0;
    }

    private MagazineItemComponentTemplate getMagazineTemplate() {
        return (MagazineItemComponentTemplate) getTemplate();
    }

    @Override
    public boolean hasState() {
        return true;
    }

    @Override
    protected List<Action> getPossibleInventoryActions(ScriptRuntime scriptRuntime, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(scriptRuntime, subject);
        for (String current : getMagazineTemplate().getAmmoTypes()) {
            actions.add(new ActionWeaponReload(getItem(), ItemFactory.createWithGenID(current)));
        }
        return actions;
    }

    public int getReloadActionPoints(Context context) {
        return reloadActionPoints.value(getMagazineTemplate().getReloadActionPoints(), 0, 1000, context);
    }

    public int getMagazineSize() {
        Actor equippedActor = getItem().getComponentOfType(EquippableItemComponent.class).getEquippedActor();
        return clipSize.value(getMagazineTemplate().getMagazineSize(), 1, 100, Context.builder().subject(equippedActor).parentItem(getItem()).build());
    }

    public int getAmmoRemaining() {
        return ammoCount;
    }

    public float getAmmoFraction() {
        if (getMagazineTemplate().getMagazineSize() == 0) return 1.0f;
        return ((float) ammoCount) / ((float) getMagazineTemplate().getMagazineSize());
    }

    public int reloadCapacity() {
        return getMagazineSize() - getAmmoRemaining();
    }

    public void setLoadedAmmoType(Item type) {
        if (ammoType != null) {
            ammoType.getComponentOfType(AmmoItemComponent.class).onUnload(getItem());
        }
        this.ammoType = type;
        if (type != null) {
            type.getComponentOfType(AmmoItemComponent.class).onLoad(getItem());
        }
    }

    public Item getLoadedAmmoType() {
        return ammoType;
    }

    public void loadAmmo(int amount) {
        ammoCount += amount;
    }

    public void consumeAmmo(int amount) {
        ammoCount -= amount;
        if (ammoCount <= 0) {
            ammoCount = 0;
            setLoadedAmmoType(null);
        }
    }

    public void emptyAmmo() {
        ammoCount = 0;
        setLoadedAmmoType(null);
    }

    @Override
    public Stat getStat(String name) {
        return switch (name) {
            case "clip_size" -> clipSize;
            case "reload_action_points" -> reloadActionPoints;
            default -> super.getStat(name);
        };
    }

    @Override
    public Expression getScriptValue(String name, Context context) {
        return switch (name) {
            case "magazine_size" -> Expression.constant(getMagazineSize());
            case "reload_action_points" -> Expression.constant(getReloadActionPoints(context));
            case "ammo_count" -> Expression.constant(ammoCount);
            default -> super.getScriptValue(name, context);
        };
    }

    @Override
    public void onStatChange(String name) {
        if ("magazine_size".equals(name) && ammoCount > getMagazineSize()) {
            int difference = ammoCount - getMagazineSize();
            ammoCount = getMagazineSize();
            if (getItem().getComponentOfType(EquippableItemComponent.class).getEquippedActor() != null) {
                getItem().getComponentOfType(EquippableItemComponent.class).getEquippedActor().getInventory().addItems(ammoType.getTemplateID(), difference);
            }
        } else {
            super.onStatChange(name);
        }
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
        if ("ammo_type".equals(name)) {
            return ammoType;
        }
        return super.getSubHolder(name, ID);
    }

}
