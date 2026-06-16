package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionWeaponReload;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.AmmoItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.MagazineItemComponentTemplate;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.stat.IntStat;
import com.github.finley243.adventureengine.stat.Stat;

import java.util.List;
import java.util.Objects;

public class MagazineItemComponent extends ItemComponent {

    private final IntStat clipSize;
    private final IntStat reloadActionPoints;

    private ItemTemplate ammoType;
    private int ammoCount;

    public MagazineItemComponent(Item item, ItemComponentTemplate template, ScriptRuntime scriptRuntime) {
        super(item, template);
        this.clipSize = new IntStat("clip_size", item, scriptRuntime);
        this.reloadActionPoints = new IntStat("reload_action_points", item, scriptRuntime);
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
    protected List<Action> getPossibleInventoryActions(ActionDependencies dependencies, Actor subject) {
        List<Action> actions = super.getPossibleInventoryActions(dependencies, subject);
        for (ItemTemplate current : getMagazineTemplate().getAmmoTypes()) {
            actions.add(new ActionWeaponReload(dependencies, getItem(), current));
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

    public void setLoadedAmmoType(ItemTemplate type) {
        if (Objects.equals(type, ammoType)) return;
        if (ammoType != null) {
            for (Effect effect : ammoType.getComponentTemplate(AmmoItemComponentTemplate.class).getWeaponEffects()) {
                getItem().getComponentOfType(EffectableItemComponent.class).removeEffect(effect);
            }
        }
        this.ammoType = type;
        if (type != null) {
            for (Effect effect : type.getComponentTemplate(AmmoItemComponentTemplate.class).getWeaponEffects()) {
                getItem().getComponentOfType(EffectableItemComponent.class).addEffect(effect);
            }
        }
    }

    public ItemTemplate getLoadedAmmoType() {
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
            case "magazine_size" -> Expression.integer(getMagazineSize());
            case "reload_action_points" -> Expression.integer(getReloadActionPoints(context));
            case "ammo_count" -> Expression.integer(ammoCount);
            default -> super.getScriptValue(name, context);
        };
    }

    @Override
    public void onStatChange(String name) {
        if ("magazine_size".equals(name) && ammoCount > getMagazineSize()) {
            int difference = ammoCount - getMagazineSize();
            ammoCount = getMagazineSize();
            if (getItem().getComponentOfType(EquippableItemComponent.class).getEquippedActor() != null) {
                getItem().getComponentOfType(EquippableItemComponent.class).getEquippedActor().getInventory().addItems(ammoType.getID(), difference);
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
