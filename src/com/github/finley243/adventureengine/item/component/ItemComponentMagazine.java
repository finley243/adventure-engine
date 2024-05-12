package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplateMagazine;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatInt;

import java.util.ArrayList;
import java.util.List;

public class ItemComponentMagazine extends ItemComponent {

    private final StatInt clipSize;
    private final StatInt reloadActionPoints;

    private Item ammoType;
    private int ammoCount;

    public ItemComponentMagazine(Item item, ItemComponentTemplate template) {
        super(item, template);
        this.clipSize = new StatInt("clip_size", item);
        this.reloadActionPoints = new StatInt("reload_action_points", item);
        this.ammoType = null;
        this.ammoCount = 0;
    }

    private ItemComponentTemplateMagazine getMagazineTemplate() {
        return (ItemComponentTemplateMagazine) getTemplate();
    }

    @Override
    public boolean hasState() {
        return true;
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        for (String current : getMagazineTemplate().getAmmoTypes()) {
            actions.add(new ActionWeaponReload(getItem(), ItemFactory.create(getItem().game(), current)));
        }
        return actions;
    }

    public int getReloadActionPoints(Context context) {
        return reloadActionPoints.value(getMagazineTemplate().getReloadActionPoints(), 0, 1000, context);
    }

    public int getMagazineSize() {
        return clipSize.value(getMagazineTemplate().getMagazineSize(), 1, 100, new Context(getItem().game(), getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor(), getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor(), getItem()));
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
            ammoType.getComponentOfType(ItemComponentAmmo.class).onUnload(getItem());
        }
        this.ammoType = type;
        if (type != null) {
            type.getComponentOfType(ItemComponentAmmo.class).onLoad(getItem());
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
    public StatInt getStatInt(String name) {
        return switch (name) {
            case "clip_size" -> clipSize;
            case "reload_action_points" -> reloadActionPoints;
            default -> super.getStatInt(name);
        };
    }

    @Override
    public Expression getStatValue(String name, Context context) {
        return switch (name) {
            case "magazine_size" -> Expression.constant(getMagazineSize());
            case "reload_action_points" -> Expression.constant(getReloadActionPoints(context));
            case "ammo_count" -> Expression.constant(ammoCount);
            default -> super.getStatValue(name, context);
        };
    }

    @Override
    public void onStatChange(String name) {
        if ("magazine_size".equals(name) && ammoCount > getMagazineSize()) {
            int difference = ammoCount - getMagazineSize();
            ammoCount = getMagazineSize();
            if (getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor() != null) {
                getItem().getComponentOfType(ItemComponentEquippable.class).getEquippedActor().getInventory().addItems(ammoType.getTemplateID(), difference);
            }
        } else {
            super.onStatChange(name);
        }
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        if ("ammo_type".equals(name)) {
            return ammoType;
        }
        return super.getSubHolder(name, ID);
    }

}
