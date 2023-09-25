package fr.yanis.dragonballuhc.manager;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.ScoreBoardUtils;
import fr.yanis.dragonballuhc.events.custom.PlayerRoleChange;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.list.ScenarioList;
import fr.yanis.dragonballuhc.roles.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;

public class GameManager {

    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Player> spectators = new ArrayList<Player>();
    private ArrayList<Player> alive = new ArrayList<Player>();
    private ScenarioList scenario;
    private boolean isStarted = false;
    private boolean isPvp = false;
    private boolean isRoleGiven = false;
    private boolean isProtected = false;
    private int episode = 1;
    private BukkitTask episodeTimer;
    public void giveRole(){
        setRoleGiven(true);
        Collections.shuffle(alive);
        int index = 0;
        for (RoleList value : DBUHCMain.getInstance().getConfigManager().getActiveRoles()) {
            PlayerManager.getPlayer(alive.get(index)).setRole(value);
            Bukkit.getPluginManager().callEvent(new PlayerRoleChange(DBUHCMain.getInstance().getGameManager(), alive.get(index), value));
            index++;
        }
    }
    public void setScenario(ScenarioList scenario) {
        this.scenario = scenario;
    }
    public ScenarioList getScenario() {
        return scenario;
    }
    public Player getPlayerWithRole(RoleList role){
        for (Player player : players) {
            if(PlayerManager.getPlayer(player).getRole() == role){
                return player;
            }
        }
        return null;
    }
    public void setEpisode(int episode) {
        this.episode = episode;
    }
    public int getEpisode() {
        return episode;
    }
    public void setStarted(boolean started) {
        isStarted = started;
    }
    public boolean isStarted() {
        return isStarted;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isRoleGiven() {
        return isRoleGiven;
    }

    public void setRoleGiven(boolean roleGiven) {
        isRoleGiven = roleGiven;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }
    public ArrayList<Player> getAlive() {
        return alive;
    }
    public BukkitTask getEpisodeTimer() {
        return episodeTimer;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public boolean isPvp() {
        return isPvp;
    }

    public void setPvp(boolean pvp) {
        isPvp = pvp;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public void addPlayer(Player player){
        players.add(player);
        alive.add(player);
    }
    public void addSpectator(Player player){
        spectators.add(player);
    }
    public void removeAlive(Player player){
        alive.remove(player);
    }
    public void removeSpectator(Player player){
        spectators.remove(player);
    }
}
