package Common.data;


import Common.events.Event;
import Common.events.EventRegistry;
import Common.events.GAME_EVENT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameData {

    private float delta;
    private int displayWidth;
    private int displayHeight;
    private float lerp = 3.0f;
    private final GameKeys keys = new GameKeys();
    private EventRegistry eventRegistry = new EventRegistry();

    public GameKeys getKeys() {
        return keys;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public float getDelta() {
        return delta;
    }

    public void setDisplayWidth(int width) {
        this.displayWidth = width;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayHeight(int height) {
        this.displayHeight = height;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public float getLerp() {
        return lerp;
    }

    public void setLerp(float lerp) {
        this.lerp = lerp;
    }

    public EventRegistry getEventRegistry() {
        return eventRegistry;
    }

    public void createEvent(GAME_EVENT gameEvent) {
        eventRegistry.addEvent(gameEvent);
    }
}
