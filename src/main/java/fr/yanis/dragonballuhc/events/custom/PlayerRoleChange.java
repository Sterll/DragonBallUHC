package fr.yanis.dragonballuhc.events.custom;

import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRoleChange extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private GameManager game;
    private Player player;
    private RoleList role;

    public PlayerRoleChange(GameManager game, Player player, RoleList role) {
        this.game = game;
        this.player = player;
        this.role = role;
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
    public Player getPlayer() {
        return player;
    }
    public RoleList getRole() {
        return role;
    }
}
