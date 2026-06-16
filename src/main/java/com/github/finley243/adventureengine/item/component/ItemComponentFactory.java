package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.*;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class ItemComponentFactory {

    private final ScriptRuntime scriptRuntime;
    private final Registry<AttackType> attackTypeRegistry;
    private final Registry<Effect> effectRegistry;
    private final Registry<DamageType> damageTypeRegistry;

    public ItemComponentFactory(ScriptRuntime scriptRuntime, Registry<AttackType> attackTypeRegistry, Registry<Effect> effectRegistry, Registry<DamageType> damageTypeRegistry) {
        this.scriptRuntime = scriptRuntime;
        this.attackTypeRegistry = attackTypeRegistry;
        this.effectRegistry = effectRegistry;
        this.damageTypeRegistry = damageTypeRegistry;
    }

    public ItemComponent create(ItemComponentTemplate template, Item item) {
        return switch (template) {
            case AmmoItemComponentTemplate typeTemplate -> new AmmoItemComponent(item, typeTemplate);
            case ArmorItemComponentTemplate typeTemplate -> new ArmorItemComponent(item, typeTemplate);
            case ConsumableItemComponentTemplate typeTemplate -> new ConsumableItemComponent(item, typeTemplate);
            case EffectableItemComponentTemplate typeTemplate -> new EffectableItemComponent(item, typeTemplate, scriptRuntime, Context.builder().parentItem(item).build());
            case EquippableItemComponentTemplate typeTemplate -> new EquippableItemComponent(item, typeTemplate);
            case MagazineItemComponentTemplate typeTemplate -> new MagazineItemComponent(item, typeTemplate, scriptRuntime);
            case ModItemComponentTemplate typeTemplate -> new ModItemComponent(item, typeTemplate);
            case ModdableItemComponentTemplate typeTemplate -> new ModdableItemComponent(item, typeTemplate);
            case WeaponItemComponentTemplate typeTemplate -> new WeaponItemComponent(item, typeTemplate, scriptRuntime, attackTypeRegistry, effectRegistry, damageTypeRegistry);
            default -> null;
        };
    }

    public static Class<? extends ItemComponent> getClassFromName(String name) {
        return switch (name) {
            case "ammo" -> AmmoItemComponent.class;
            case "armor" -> ArmorItemComponent.class;
            case "consumable" -> ConsumableItemComponent.class;
            case "effectable" -> EffectableItemComponent.class;
            case "equippable" -> EquippableItemComponent.class;
            case "magazine" -> MagazineItemComponent.class;
            case "mod" -> ModItemComponent.class;
            case "moddable" -> ModdableItemComponent.class;
            case "weapon" -> WeaponItemComponent.class;
            case null, default -> null;
        };
    }

}
