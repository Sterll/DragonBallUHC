package fr.yanis.dragonballuhc.events.custom;
;
import fr.yanis.dragonballuhc.list.CampList;
import fr.yanis.dragonballuhc.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private GameManager game;
    private Player winner;
    private boolean forced;
    private CampList camp;

    public GameEndEvent(GameManager game, Player winner, boolean forced, CampList camp) {
        this.game = game;
        this.winner = winner;
        this.forced = forced;
        this.camp = camp;
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
    public Player getWinner() {
        return winner;
    }
    public boolean isForced() {
        return forced;
    }
    public CampList getCamp() {
        return camp;
    }
}
