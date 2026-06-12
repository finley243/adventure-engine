package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorFactory;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ActorLoader {

    private final ScriptParser scriptParser;

    public ActorLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    Actor parseActor(Element element, Area area) throws GameDataException {
        if (element == null) return null;
        String ID = element.getAttribute("id");
        String template = LoadUtils.attribute(element, "template", null);
        String nameDescriptor = LoadUtils.singleTag(element, "descriptor", null);
        List<Behavior> behaviors = loadBehaviors(LoadUtils.singleChildWithName(element, "behaviors"), ID);
        boolean startDead = LoadUtils.attributeBool(element, "startDead", false);
        boolean startDisabled = LoadUtils.attributeBool(element, "startDisabled", false);
        return ActorFactory.create(ID, nameDescriptor, area, template, behaviors, startDead, startDisabled);
    }

    private List<Behavior> loadBehaviors(Element behaviorsElement, String actorID) throws GameDataException {
        if (behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for (Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
            Behavior behavior = loadBehavior(behaviorElement, actorID);
            behaviors.add(behavior);
        }
        return behaviors;
    }

    private Behavior loadBehavior(Element behaviorElement, String actorID) throws GameDataException {
        String type = LoadUtils.attribute(behaviorElement, "type", null);
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(behaviorElement, "condition"), scriptParser, "Behavior(" + actorID + ") - condition");
        Script startScript = LoadUtils.loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptStart"), scriptParser, "Behavior(" + actorID + ") - start script");
        Script eachRoundScript = LoadUtils.loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptEachRound"), scriptParser, "Behavior(" + actorID + ") - round script");
        int duration = LoadUtils.attributeInt(behaviorElement, "duration", 0);
        List<Idle> idles = new ArrayList<>();
        List<Element> idleElements = LoadUtils.directChildrenWithName(behaviorElement, "idle");
        for (Element idleElement : idleElements) {
            Idle idle = loadIdle(idleElement, actorID);
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
                List<Behavior> procedureBehaviors = loadBehaviors(behaviorElement, actorID);
                boolean isCycle = LoadUtils.attributeBool(behaviorElement, "isCycle", false);
                return new BehaviorProcedure(condition, startScript, eachRoundScript, isCycle, procedureBehaviors);
            }
            default -> throw new GameDataException("Behavior has invalid or missing type");
        }
    }

    private Idle loadIdle(Element idleElement, String actorID) throws GameDataException {
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(idleElement, "condition"), scriptParser, "Idle(" + actorID + ") - condition");
        String phrase = LoadUtils.singleTag(idleElement, "phrase", null);
        return new Idle(condition, phrase);
    }

}
