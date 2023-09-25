package fr.yanis.dragonballuhc.events.custom;

import fr.yanis.dragonballuhc.list.TimeList;
import fr.yanis.dragonballuhc.manager.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EpisodeChangeEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private GameManager game;
    private TimeList time;
    private int oldEpisode, newEpisode;

    public EpisodeChangeEvent(GameManager game, int oldEpisode, int newEpisode, TimeList time) {
        this.game = game;
        this.oldEpisode = oldEpisode;
        this.newEpisode = newEpisode;
        this.time = time;
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
    public int getNewEpisode() {
        return newEpisode;
    }
    public int getOldEpisode() {
        return oldEpisode;
    }
    public TimeList getTime() {
        return time;
    }
}
