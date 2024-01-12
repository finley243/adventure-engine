package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {

    public enum DataType {
        AREA, ROOM, ACTOR, OBJECT, SCENE, VARIABLE, ITEM_TEMPLATE, ITEM_INSTANCE, TIME
    }

    private final DataType type;
    private final String id;
    private final String parameter;

    private final String valueString;
    private final int valueInt;
    private final boolean valueBoolean;
    private final List<SaveData> valueMulti;

    public SaveData(DataType type, String id, String parameter, String valueString) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueString = valueString;
        this.valueInt = 0;
        this.valueBoolean = false;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, int valueInt) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueString = null;
        this.valueInt = valueInt;
        this.valueBoolean = false;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, String valueString, int valueInt) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueString = valueString;
        this.valueInt = valueInt;
        this.valueBoolean = false;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, boolean valueBoolean) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueString = null;
        this.valueInt = 0;
        this.valueBoolean = valueBoolean;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, List<SaveData> valueMulti) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueString = null;
        this.valueInt = 0;
        this.valueBoolean = false;
        this.valueMulti = valueMulti;
    }

    public void apply(Data data) {
        switch (type) {
            case AREA -> {
                Area area = data.getArea(id);
                area.loadState(this);
            }
            case ROOM -> {
                Room room = data.getRoom(id);
                room.loadState(this);
            }
            case ACTOR -> {
                Actor actor = data.getActor(id);
                actor.loadState(this);
            }
            case OBJECT -> {
                WorldObject object = data.getObject(id);
                object.loadState(this);
            }
            case SCENE -> {
                Scene topic = data.getScene(id);
                topic.loadState(this);
            }
            //case VARIABLE -> data.setGlobalInteger(id, valueInt);
            case ITEM_INSTANCE -> ItemFactory.load(data.game(), valueString, id);
            case ITEM_TEMPLATE -> {
                ItemTemplate itemTemplate = data.getItemTemplate(id);
                itemTemplate.loadState(this);
            }
            case TIME -> data.dateTime().loadState(this);
        }
    }

    public boolean isItemInstance() {
        return type == DataType.ITEM_INSTANCE;
    }

    public String getParameter() {
        return parameter;
    }

    public String getValueString() {
        return valueString;
    }

    public int getValueInt() {
        return valueInt;
    }

    public boolean getValueBoolean() {
        return valueBoolean;
    }

    public List<SaveData> getValueMulti() {
        return valueMulti;
    }

}
