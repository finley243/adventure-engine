package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.DateTimeController;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.GameData;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.TimerManager;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.parse.ScriptFunction;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ScriptRuntimeImpl implements ScriptRuntime {

    private final SensoryEventDispatcher sensoryEventDispatcher;
    private final MenuManager menuManager;
    private final TimerManager timerManager;
    private final Pathfinder pathfinder;
    private final DateTimeController dateTimeController;
    private final MutableRegistry<Expression> globalExpressionRegistry;

    private GameData gameData;

    public ScriptRuntimeImpl(SensoryEventDispatcher sensoryEventDispatcher, MenuManager menuManager, TimerManager timerManager, Pathfinder pathfinder, DateTimeController dateTimeController, MutableRegistry<Expression> globalExpressionRegistry) {
        this.sensoryEventDispatcher = sensoryEventDispatcher;
        this.menuManager = menuManager;
        this.timerManager = timerManager;
        this.pathfinder = pathfinder;
        this.dateTimeController = dateTimeController;
        this.globalExpressionRegistry = globalExpressionRegistry;
    }

    public void setGameData(GameData gameData) {
        if (this.gameData != null) throw new IllegalStateException("GameData already set");
        this.gameData = gameData;
    }

    private GameData gameData() {
        if (gameData == null) throw new IllegalStateException("GameData not yet loaded");
        return gameData;
    }

    @Override
    public WorldObject getObject(String id) {
        return gameData().objectRegistry().getFromID(id);
    }

    @Override
    public Item getItem(String id) {
        return gameData().itemMutableRegistry().getFromID(id);
    }

    @Override
    public ItemTemplate getItemTemplate(String id) {
        return gameData().itemTemplateRegistry().getFromID(id);
    }

    @Override
    public Area getArea(String id) {
        return gameData().areaRegistry().getFromID(id);
    }

    @Override
    public Room getRoom(String id) {
        return gameData().roomRegistry().getFromID(id);
    }

    @Override
    public Scene getScene(String id) {
        return gameData().sceneRegistry().getFromID(id);
    }

    @Override
    public Effect getEffect(String id) {
        return gameData().effectRegistry().getFromID(id);
    }

    @Override
    public Faction getFaction(String id) {
        return gameData().factionRegistry().getFromID(id);
    }

    @Override
    public Actor getActor(String id) {
        return gameData().actorRegistry().getFromID(id);
    }

    @Override
    public Actor getPlayer() {
        return gameData().actorRegistry().getPlayer();
    }

    @Override
    public ScriptFunction getScript(String id) {
        return gameData().scriptRegistry().getFromID(id);
    }

    @Override
    public void addTimer(String id, Timer timer) {
        timerManager.add(id, timer);
    }

    @Override
    public boolean isTimerActive(String id) {
        return timerManager.isActive(id);
    }

    @Override
    public void attributeMenu(Actor actor, int points) {
        menuManager.attributeMenu(actor, points, gameData().attributeRegistry().getAll());
    }

    @Override
    public void skillMenu(Actor actor, int points) {
        menuManager.skillMenu(actor, points, gameData().skillRegistry().getAll());
    }

    @Override
    public void sceneMenu(Scene scene, Context context) {
        menuManager.sceneMenu(scene, context, false);
    }

    @Override
    public int getYear() {
        return dateTimeController.getYear();
    }

    @Override
    public int getMonth() {
        return dateTimeController.getMonth();
    }

    @Override
    public int getDay() {
        return dateTimeController.getDay();
    }

    @Override
    public String getWeekday() {
        return dateTimeController.getWeekday();
    }

    @Override
    public void setGlobalExpression(String id, Expression expression) {
        globalExpressionRegistry.add(id, expression);
    }

    @Override
    public Expression getGlobalExpression(String id) {
        return globalExpressionRegistry.getFromID(id);
    }

    @Override
    public void postSensoryEvent(SensoryEvent event) {
        sensoryEventDispatcher.dispatch(event);
    }

    @Override
    public boolean actorCanSeeTargetActor(Actor observer, Actor target) {
        return target.isVisible(observer) && observer.getLineOfSightActors(pathfinder).contains(target);
    }

}
