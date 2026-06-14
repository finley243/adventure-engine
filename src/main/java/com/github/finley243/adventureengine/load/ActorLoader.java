package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.gamedata.AreaRegistry;
import com.github.finley243.adventureengine.gamedata.ConfigHandler;
import com.github.finley243.adventureengine.gamedata.ConfigOption;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ActorLoader {

    private final ScriptParser scriptParser;
    private final Registry<ActorTemplate> actorTemplateRegistry;
    private final ScriptRuntime scriptRuntime;
    private final UIEventBus eventBus;
    private final SensoryEventDispatcher sensoryEventDispatcher;
    private final ItemFactory itemFactory;
    private final Registry<SenseType> senseTypeRegistry;
    private final Registry<Effect> effectRegistry;
    private final Registry<DamageType> damageTypeRegistry;
    private final Registry<Attribute> attributeRegistry;
    private final Registry<Skill> skillRegistry;

    public ActorLoader(ScriptParser scriptParser, Registry<ActorTemplate> actorTemplateRegistry, ScriptRuntime scriptRuntime, UIEventBus eventBus, SensoryEventDispatcher sensoryEventDispatcher, ItemFactory itemFactory, Registry<SenseType> senseTypeRegistry, Registry<Effect> effectRegistry, Registry<DamageType> damageTypeRegistry, Registry<Attribute> attributeRegistry, Registry<Skill> skillRegistry) {
        this.scriptParser = scriptParser;
        this.actorTemplateRegistry = actorTemplateRegistry;
        this.scriptRuntime = scriptRuntime;
        this.eventBus = eventBus;
        this.sensoryEventDispatcher = sensoryEventDispatcher;
        this.itemFactory = itemFactory;
        this.senseTypeRegistry = senseTypeRegistry;
        this.effectRegistry = effectRegistry;
        this.damageTypeRegistry = damageTypeRegistry;
        this.attributeRegistry = attributeRegistry;
        this.skillRegistry = skillRegistry;
    }

    public Actor loadPlayer(ConfigHandler configHandler, AreaRegistry areaRegistry) {
        String ID = configHandler.get(ConfigOption.PLAYER_ID);
        String templateID = configHandler.get(ConfigOption.PLAYER_STATS);
        ActorTemplate template = actorTemplateRegistry.getFromID(templateID);
        if (template == null) throw new GameDataException("Player actor has invalid template");
        String areaID = configHandler.get(ConfigOption.PLAYER_START_AREA);
        Area area = areaRegistry.getFromID(areaID);
        if (area == null) throw new GameDataException("Player actor has invalid area");
        return new Actor(scriptRuntime, sensoryEventDispatcher, itemFactory, senseTypeRegistry, effectRegistry, damageTypeRegistry.getAll(), attributeRegistry.getAll(), skillRegistry.getAll(), ID, null, area, template, true, null, false, false, true);
    }

    Actor parseActor(Element element, Area area) throws GameDataException {
        String ID = LoadUtils.attribute(element, "id", null);
        String templateID = LoadUtils.attribute(element, "template", null);
        ActorTemplate template = actorTemplateRegistry.getFromID(templateID);
        if (template == null) throw new GameDataException("Actor has invalid template");
        String nameDescriptor = LoadUtils.singleTag(element, "descriptor", null);
        List<Behavior> behaviors = parseBehaviors(LoadUtils.singleChildWithName(element, "behaviors"), ID);
        boolean startDead = LoadUtils.attributeBool(element, "startDead", false);
        boolean startDisabled = LoadUtils.attributeBool(element, "startDisabled", false);
        return new Actor(scriptRuntime, sensoryEventDispatcher, itemFactory, senseTypeRegistry, effectRegistry, damageTypeRegistry.getAll(), attributeRegistry.getAll(), skillRegistry.getAll(), ID, nameDescriptor, area, template, false, behaviors, startDead, startDisabled, false);
    }

    private List<Behavior> parseBehaviors(Element behaviorsElement, String actorID) throws GameDataException {
        if (behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for (Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
            Behavior behavior = parseBehavior(behaviorElement, actorID);
            behaviors.add(behavior);
        }
        return behaviors;
    }

    private Behavior parseBehavior(Element behaviorElement, String actorID) throws GameDataException {
        String type = LoadUtils.attribute(behaviorElement, "type", null);
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(behaviorElement, "condition"), scriptParser, "Behavior(" + actorID + ") - condition");
        Script startScript = LoadUtils.loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptStart"), scriptParser, "Behavior(" + actorID + ") - start script");
        Script eachRoundScript = LoadUtils.loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptEachRound"), scriptParser, "Behavior(" + actorID + ") - round script");
        int duration = LoadUtils.attributeInt(behaviorElement, "duration", 0);
        List<Idle> idles = new ArrayList<>();
        List<Element> idleElements = LoadUtils.directChildrenWithName(behaviorElement, "idle");
        for (Element idleElement : idleElements) {
            Idle idle = parseIdle(idleElement, actorID);
            idles.add(idle);
        }
        switch (type) {
            case "move" -> {
                String areaTarget = LoadUtils.attribute(behaviorElement, "area", null);
                return new BehaviorMove(condition, startScript, eachRoundScript, duration, idles, areaTarget);
            }
            case "use" -> {
                String objectTarget = LoadUtils.attribute(behaviorElement, "object", null);
                String slotTarget = LoadUtils.attribute(behaviorElement, "slot", null);
                return new BehaviorUse(condition, startScript, eachRoundScript, duration, idles, objectTarget, slotTarget);
            }
            case "guard" -> {
                String guardTarget = LoadUtils.attribute(behaviorElement, "object", null);
                return new BehaviorGuard(condition, startScript, eachRoundScript, duration, idles, guardTarget);
            }
            case "follow" -> {
                String actorTarget = LoadUtils.attribute(behaviorElement, "actor", null);
                return new BehaviorFollow(condition, startScript, eachRoundScript, duration, idles, actorTarget);
            }
            case "action" -> {
                String actionID = LoadUtils.attribute(behaviorElement, "action", null);
                Condition actionCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(behaviorElement, "actionCondition"), scriptParser, "Behavior(" + actorID + ") - action condition");
                return new BehaviorAction(condition, startScript, eachRoundScript, duration, idles, actionID, actionCondition);
            }
            case "procedure" -> {
                List<Behavior> procedureBehaviors = parseBehaviors(behaviorElement, actorID);
                boolean isCycle = LoadUtils.attributeBool(behaviorElement, "isCycle", false);
                return new BehaviorProcedure(condition, startScript, eachRoundScript, isCycle, procedureBehaviors);
            }
            default -> throw new GameDataException("Behavior has invalid or missing type");
        }
    }

    private Idle parseIdle(Element idleElement, String actorID) throws GameDataException {
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(idleElement, "condition"), scriptParser, "Idle(" + actorID + ") - condition");
        String phrase = LoadUtils.singleTag(idleElement, "phrase", null);
        return new Idle(condition, phrase);
    }

}
