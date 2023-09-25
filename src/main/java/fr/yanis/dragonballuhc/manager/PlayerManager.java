package fr.yanis.dragonballuhc.manager;

import fr.yanis.dragonballuhc.list.RoleList;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

    private RoleList role;
    private boolean isDead = false;
    private int kills = 0;

    private static HashMap<Player, PlayerManager> players = new HashMap<Player, PlayerManager>();

    public PlayerManager(Player player) {
        this.role = null;
        this.kills = 0;
        players.put(player, this);
    }
    public PlayerManager(Player player, RoleList role) {
        this.role = role;
        this.kills = 0;
        players.put(player, this);
    }

    public static HashMap<Player, PlayerManager> getPlayers() {
        return players;
    }
    public static PlayerManager getPlayer(Player player){
        return players.get(player);
    }
    public static void removePlayer(Player player){
        players.remove(player);
    }

    public RoleList getRole() {
        return role;
    }
    public void setRole(RoleList role) {
        this.role = role;
    }
    public boolean isDead() {
        return isDead;
    }
    public void setDead(boolean dead) {
        isDead = dead;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill(int kills){
        this.kills += kills;
    }

    public void removeKill(int kills){
        this.kills -= kills;
    }
}
