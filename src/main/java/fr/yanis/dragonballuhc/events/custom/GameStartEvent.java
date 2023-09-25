package fr.yanis.dragonballuhc.events.custom;

import fr.yanis.dragonballuhc.manager.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private GameManager game;

    public GameStartEvent(GameManager game) {
        this.game = game;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public GameManager getGame() {
        return game;
    }

}
