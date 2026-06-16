package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.gamedata.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.component.ItemComponentFactory;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectTemplate;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameDataLoader {

    private static final String DATA_FILE_EXTENSION = ".xml";
    private static final String PHRASE_FILE = "phrases.aphr";
    private static final String SCRIPTS_PATH = "/scripts";

    private static final String NAME_ATTRIBUTE = "attributes";
    private static final String NAME_SKILL = "skills";
    private static final String NAME_SENSE_TYPE = "sense_types";
    private static final String NAME_DAMAGE_TYPE = "damage_types";
    private static final String NAME_WEAPON_CLASS = "weapon_classes";
    private static final String NAME_ATTACK_TYPE = "attack_types";
    private static final String NAME_FACTION = "factions";
    private static final String NAME_OBSTRUCTION_TYPE = "obstruction_types";
    private static final String NAME_LINK_TYPE = "link_types";

    private static final String NAME_EFFECT = "effects";
    private static final String NAME_ACTION_TEMPLATE = "actions";
    private static final String NAME_OBJECT_TEMPLATE = "objects";
    private static final String NAME_ITEM_TEMPLATE = "items";
    private static final String NAME_LOOT_TABLE = "loot_tables";
    private static final String NAME_ACTOR_TEMPLATE = "actors";
    private static final String NAME_NETWORK_NODE = "networks";
    private static final String NAME_SCENE = "scenes";
    private static final String NAME_ROOM = "rooms";
    private static final String NAME_AREA = "areas";

    private final ConfigHandler configHandler;
    private final ScriptRuntime scriptRuntime;
    private final MutableRegistry<Item> itemMutableRegistry;
    private final UIEventBus eventBus;

    public GameDataLoader(ConfigHandler configHandler, ScriptRuntime scriptRuntime, MutableRegistry<Item> itemMutableRegistry, UIEventBus eventBus) {
        this.configHandler = configHandler;
        this.scriptRuntime = scriptRuntime;
        this.itemMutableRegistry = itemMutableRegistry;
        this.eventBus = eventBus;
    }

    public GameData loadData(File dir) throws GameDataException {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Path must be a directory: " + dir.getAbsolutePath());
        }

        ScriptParser scriptParser = new ScriptParser();
        File scriptDir = new File(dir, SCRIPTS_PATH);
        Map<String, ScriptParser.ScriptData> scriptMap;
        if (!scriptDir.exists()) {
            scriptMap = new HashMap<>();
        } else {
            ScriptLoader scriptLoader = new ScriptLoader(scriptParser);
            scriptMap = scriptLoader.loadFromDir(scriptDir);
        }
        Registry<ScriptParser.ScriptData> scriptRegistry = new Registry<>(scriptMap);

        PhraseLoader phraseLoader = new PhraseLoader();
        File phraseFile = new File(dir, PHRASE_FILE);
        if (!phraseFile.exists() || !phraseFile.isFile()) throw new GameDataException("Phrase file does not exist: " + phraseFile.getAbsolutePath());
        Map<String, String> phraseMap = phraseLoader.loadPhrases(phraseFile);
        PhraseManager phraseManager = new PhraseManager(phraseMap);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Pathfinder pathfinder = new Pathfinder();
        SensoryEventDispatcher sensoryEventDispatcher = new SensoryEventDispatcher(pathfinder, eventBus);

        ActionLoader actionLoader = new ActionLoader(scriptParser);
        Map<String, ActionTemplate> actionMap = loadMapFromFileName(dir, NAME_ACTION_TEMPLATE, builder, actionLoader::load);
        Registry<ActionTemplate> actionRegistry = new Registry<>(actionMap);

        WorldTypeLoader worldTypeLoader = new WorldTypeLoader();
        Map<String, ObstructionType> obstructionTypeMap = loadMapFromFileName(dir, NAME_OBSTRUCTION_TYPE, builder, worldTypeLoader::loadObstructionTypes);
        Registry<ObstructionType> obstructionTypeRegistry = new Registry<>(obstructionTypeMap);
        Map<String, LinkType> linkTypeMap = loadMapFromFileName(dir, NAME_LINK_TYPE, builder, e -> worldTypeLoader.loadLinkTypes(e, actionRegistry));
        Registry<LinkType> linkTypeRegistry = new Registry<>(linkTypeMap);

        CharacterTypeLoader characterTypeLoader = new CharacterTypeLoader();
        Map<String, Attribute> attributeMap = loadMapFromFileName(dir, NAME_ATTRIBUTE, builder, characterTypeLoader::loadAttributes);
        Registry<Attribute> attributeRegistry = new Registry<>(attributeMap);
        Map<String, Skill> skillMap = loadMapFromFileName(dir, NAME_SKILL, builder, characterTypeLoader::loadSkills);
        Registry<Skill> skillRegistry = new Registry<>(skillMap);
        Map<String, SenseType> senseTypeMap = loadMapFromFileName(dir, NAME_SENSE_TYPE, builder, e -> characterTypeLoader.loadSenseTypes(e, obstructionTypeRegistry));
        Registry<SenseType> senseTypeRegistry = new Registry<>(senseTypeMap);

        EffectLoader effectLoader = new EffectLoader(scriptParser);
        Map<String, Effect> effectMap = loadMapFromFileName(dir, NAME_EFFECT, builder, effectLoader::load);
        Registry<Effect> effectRegistry = new Registry<>(effectMap);

        CombatTypeLoader combatTypeLoader = new CombatTypeLoader(scriptParser);
        Map<String, DamageType> damageTypeMap = loadMapFromFileName(dir, NAME_DAMAGE_TYPE, builder, combatTypeLoader::loadDamageTypes);
        Registry<DamageType> damageTypeRegistry = new Registry<>(damageTypeMap);
        Map<String, AttackType> attackTypeMap = loadMapFromFileName(dir, NAME_ATTACK_TYPE, builder, e -> combatTypeLoader.loadAttackTypes(e, pathfinder, damageTypeRegistry, effectRegistry));
        Registry<AttackType> attackTypeRegistry = new Registry<>(attackTypeMap);
        Map<String, WeaponClass> weaponClassMap = loadMapFromFileName(dir, NAME_WEAPON_CLASS, builder, e -> combatTypeLoader.loadWeaponClasses(e, attackTypeRegistry, skillRegistry));
        Registry<WeaponClass> weaponClassRegistry = new Registry<>(weaponClassMap);

        FactionLoader factionLoader = new FactionLoader();
        Map<String, Faction> factionMap = loadMapFromFileName(dir, NAME_FACTION, builder, factionLoader::load);
        Registry<Faction> factionRegistry = new Registry<>(factionMap);

        SceneLoader sceneLoader = new SceneLoader(scriptParser);
        Map<String, Scene> sceneMap = loadMapFromFileName(dir, NAME_SCENE, builder, sceneLoader::load);
        Registry<Scene> sceneRegistry = new Registry<>(sceneMap);

        ItemTemplateLoader itemTemplateLoader = new ItemTemplateLoader(configHandler, scriptParser, sceneLoader, actionRegistry, effectRegistry, weaponClassRegistry, damageTypeRegistry);
        Map<String, ItemTemplate> itemTemplateMap = loadMapFromFileName(dir, NAME_ITEM_TEMPLATE, builder, itemTemplateLoader::load);
        Registry<ItemTemplate> itemTemplateRegistry = new Registry<>(itemTemplateMap);

        LootTableLoader lootTableLoader = new LootTableLoader(itemTemplateRegistry);
        Map<String, LootTable> lootTableMap = loadMapFromFileName(dir, NAME_LOOT_TABLE, builder, lootTableLoader::load);
        Registry<LootTable> lootTableRegistry = new Registry<>(lootTableMap);

        ObjectTemplateLoader objectTemplateLoader = new ObjectTemplateLoader(scriptParser, sceneLoader, lootTableLoader, actionRegistry, lootTableRegistry);
        Map<String, ObjectTemplate> objectTemplateMap = loadMapFromFileName(dir, NAME_OBJECT_TEMPLATE, builder, objectTemplateLoader::load);
        Registry<ObjectTemplate> objectTemplateRegistry = new Registry<>(objectTemplateMap);

        NetworkLoader networkLoader = new NetworkLoader();
        Map<String, NetworkNode> networkMap = loadMapFromFileName(dir, NAME_NETWORK_NODE, builder, networkLoader::load);
        Registry<NetworkNode> networkRegistry = new Registry<>(networkMap);

        RoomLoader roomLoader = new RoomLoader(sceneLoader, scriptParser, factionRegistry);
        Map<String, Room> roomMap =  loadMapFromFileName(dir, NAME_ROOM, builder, roomLoader::load);
        Registry<Room> roomRegistry = new Registry<>(roomMap);

        ActorTemplateLoader actorTemplateLoader = new ActorTemplateLoader(scriptParser, lootTableLoader, senseTypeRegistry, actionRegistry, effectRegistry, factionRegistry, attackTypeRegistry, sceneRegistry, lootTableRegistry);
        Map<String, ActorTemplate> actorTemplateMap = loadMapFromFileName(dir, NAME_ACTOR_TEMPLATE, builder, actorTemplateLoader::load);
        Registry<ActorTemplate> actorTemplateRegistry = new Registry<>(actorTemplateMap);

        ItemComponentFactory itemComponentFactory = new ItemComponentFactory(scriptRuntime, attackTypeRegistry, effectRegistry, damageTypeRegistry);
        ItemFactory itemFactory = new ItemFactory(itemTemplateRegistry, itemMutableRegistry, itemComponentFactory);

        ActorLoader actorLoader = new ActorLoader(scriptParser, actorTemplateRegistry, scriptRuntime, eventBus, sensoryEventDispatcher, itemFactory, senseTypeRegistry, effectRegistry, damageTypeRegistry, attributeRegistry, skillRegistry);
        ObjectLoader objectLoader = new ObjectLoader(scriptParser, objectTemplateRegistry);
        ItemLoader itemLoader = new ItemLoader(itemFactory, itemTemplateRegistry);
        AreaLoader areaLoader = new AreaLoader(configHandler, scriptRuntime, scriptParser, sceneLoader, actorLoader, objectLoader, itemLoader, factionRegistry, roomRegistry, obstructionTypeRegistry, linkTypeRegistry, effectRegistry, itemFactory);
        Element areasElement = getRootElementFromFileName(dir, NAME_AREA, builder);
        AreaLoader.AreaLoaderResult areaLoaderResult = areaLoader.load(areasElement);
        AreaRegistry areaRegistry = new AreaRegistry(areaLoaderResult.areas());
        Actor playerActor = actorLoader.loadPlayer(configHandler, areaRegistry);
        Map<String, Actor> actorMap = new HashMap<>(areaLoaderResult.actors());
        actorMap.put(playerActor.getID(), playerActor);
        ActorRegistry actorRegistry = new ActorRegistry(actorMap, playerActor);
        Registry<WorldObject> objectRegistry = new Registry<>(areaLoaderResult.objects());

        for (Actor actor : actorRegistry.getAll()) {
            actor.resolveBehaviorReferences(areaRegistry, objectRegistry, actorRegistry);
        }
        for (Area area : areaRegistry.getAll()) {
            area.resolveAreaLinks(areaRegistry);
        }
        for (WorldObject object : objectRegistry.getAll()) {
            object.resolveComponentReferences(objectRegistry);
        }
        for (Scene scene : sceneRegistry.getAll()) {
            scene.resolveLinkedScenes(sceneRegistry);
        }

        return new GameData(sensoryEventDispatcher, pathfinder, phraseManager, areaRegistry, roomRegistry, actorTemplateRegistry, actorRegistry, objectTemplateRegistry, objectRegistry, itemTemplateRegistry, itemMutableRegistry, lootTableRegistry, weaponClassRegistry, attackTypeRegistry, sceneRegistry, factionRegistry, networkRegistry, effectRegistry, actionRegistry, linkTypeRegistry, damageTypeRegistry, attributeRegistry, skillRegistry, senseTypeRegistry, obstructionTypeRegistry, scriptRegistry);
    }

    private <T> Map<String, T> loadMapFromFileName(File parentDir, String name, DocumentBuilder builder, Function<Element, Map<String, T>> loadFunction) throws GameDataException {
        Element rootElement = getRootElementFromFileName(parentDir, name, builder);
        return loadFunction.apply(rootElement);
    }

    private Element getRootElementFromFileName(File parentDir, String name, DocumentBuilder builder) {
        File file = new File(parentDir, name + DATA_FILE_EXTENSION);
        if (!file.exists()) throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
        return getRootElementFromFile(file, builder);
    }

    private Element getRootElementFromFile(File file, DocumentBuilder builder) {
        Document document;
        try {
            document = builder.parse(file);
        } catch (SAXException | IOException e) {
            throw new GameDataException("Game data file could not be parsed: " + file.getAbsolutePath());
        }
        return document.getDocumentElement();
    }

}
