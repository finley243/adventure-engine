package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.load.ScriptParser;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;

public interface ScriptRuntime {

    WorldObject getObject(String id);

    Item getItem(String id);

    ItemTemplate getItemTemplate(String id);

    Area getArea(String id);

    Room getRoom(String id);

    Scene getScene(String id);

    Actor getActor(String id);

    Actor getPlayer();

    ScriptParser.ScriptData getScript(String id);

    void addTimer(String id, Timer timer);

    boolean isTimerActive(String id);

    void attributeMenu(Actor actor, int points);

    void skillMenu(Actor actor, int points);

    void sceneMenu(Scene scene, Context context);

    int getYear();

    int getMonth();

    int getDay();

    String getWeekday();

    void setGlobalExpression(String id, Expression expression);

    Expression getGlobalExpression(String id);

}
