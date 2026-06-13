package com.github.finley243.adventureengine.gamedata;

import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.ScriptParser;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

public record GameData(SensoryEventDispatcher sensoryEventDispatcher, Pathfinder pathfinder, TextGen textGen, PhraseManager phraseManager, AreaRegistry areaRegistry, Registry<Room> roomRegistry, Registry<ActorTemplate> actorTemplateRegistry, ActorRegistry actorRegistry, Registry<ObjectTemplate> objectTemplateRegistry, Registry<WorldObject> objectRegistry, Registry<ItemTemplate> itemTemplateRegistry, MutableRegistry<Item> itemMutableRegistry, Registry<LootTable> lootTableRegistry, Registry<WeaponClass> weaponClassRegistry, Registry<WeaponAttackType> attackTypeRegistry, Registry<Scene> sceneRegistry, Registry<Faction> factionRegistry, Registry<NetworkNode> networkNodeRegistry, Registry<Effect> effectRegistry, Registry<ActionTemplate> actionTemplateRegistry, Registry<LinkType> linkTypeRegistry, Registry<DamageType> damageTypeRegistry, Registry<Attribute> attributeRegistry, Registry<Skill> skillRegistry, Registry<SenseType> senseTypeRegistry, Registry<ObstructionType> obstructionTypeRegistry, Registry<ScriptParser.ScriptData> scriptRegistry) {}
