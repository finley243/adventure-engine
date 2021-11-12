package com.github.finley243.adventureengine.event.ui;

public class RenderAreaEvent {

    private final String room;
    private final String area;

    public RenderAreaEvent(String room, String area) {
        this.room = room;
        this.area = area;
    }

    public String getRoom() {
        return room;
    }

    public String getArea() {
        return area;
    }

}
