package fr.yanis.dragonballuhc.events;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.ScoreBoardUtils;
import fr.yanis.dragonballuhc.events.custom.EpisodeChangeEvent;
import fr.yanis.dragonballuhc.events.custom.GameEndEvent;
import fr.yanis.dragonballuhc.events.custom.GameStartEvent;
import fr.yanis.dragonballuhc.events.custom.PlayerRoleChange;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.list.TimeList;
import fr.yanis.dragonballuhc.manager.GameManager;
import fr.yanis.dragonballuhc.manager.PlayerManager;
import fr.yanis.dragonballuhc.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Random;

public class GameEvents implements Listener {

    private static BukkitTask episode_timer;

    @EventHandler
    public void onGameStart(GameStartEvent e) {
        for (Player player : e.getGame().getAlive()) {
            player.sendMessage("§aLa partie va commencer dans 10 secondes !");
        }
        Bukkit.getWorld("UHCMAP").setTime(6000);
        Bukkit.getScheduler().runTaskLater(DBUHCMain.getInstance(), () -> {
            e.getGame().setStarted(true);
            e.getGame().setEpisode(0);
            e.getGame().setPvp(false);
            e.getGame().setProtected(true);
            int centerX = 0;
            int centerY = 150;
            int centerZ = 0;
            int radius = 2500;
            Random random = new Random();
            for (Player player : e.getGame().getAlive()) {
                player.sendMessage("§aLa partie commence !");
                int x = centerX + random.nextInt(radius * 2) - radius;
                int z = centerZ + random.nextInt(radius * 2) - radius;
                Location randomLocation = new Location(Bukkit.getWorld("UHCMAP"), x, centerY, z);
                player.teleport(randomLocation);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.setExp(0);
                player.setLevel(0);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.getInventory().setContents(DBUHCMain.getInstance().getConfigManager().getBaseInventory().getContents());
                ScoreBoardUtils.updateScoreboard(player);
            }

            // Gestion de l'épisode
            episode_timer = Bukkit.getServer().getScheduler().runTaskTimer(DBUHCMain.getInstance(), () -> {
                TimeList time;
                if(e.getGame().getEpisode() % 2 == 0){
                    time = TimeList.NIGHT;
                } else {
                    time = TimeList.DAY;
                }
                Bukkit.getServer().getPluginManager().callEvent(new EpisodeChangeEvent(e.getGame(), e.getGame().getEpisode(), e.getGame().getEpisode() + 1, time));
            }, 0, 20 * (20));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!DBUHCMain.getInstance().getGameManager().isStarted()) cancel();
                    e.getGame().setProtected(false);
                    for (Player player : DBUHCMain.getInstance().getGameManager().getAlive()) {
                        player.sendTitle("§cProtection désactivé", "§7Vous pouvez maintenant prendre des dégâts !");
                    }
                }
            }.runTaskLater(DBUHCMain.getInstance(), 20 * (60L * DBUHCMain.getInstance().getConfigManager().getProtectionTime()));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!DBUHCMain.getInstance().getGameManager().isStarted()) cancel();
                    e.getGame().setPvp(true);
                    for (Player player : DBUHCMain.getInstance().getGameManager().getAlive()) {
                        player.sendTitle("§cPvP activé", "§7Vous pouvez maintenant vous battre !");
                    }
                }
            }.runTaskLater(DBUHCMain.getInstance(), 20 * (60L * DBUHCMain.getInstance().getConfigManager().getPvpTime()));
            new BukkitRunnable() {
                @Override
                public void run() {
                    DBUHCMain.getInstance().getGameManager().giveRole();
                }
            }.runTaskLater(DBUHCMain.getInstance(), 20 * (60L * DBUHCMain.getInstance().getConfigManager().getRoleTime()));
        }, 20 * 10);
    }

    @EventHandler
    public void onEpisodeChange(EpisodeChangeEvent e){
        GameManager gameManager = e.getGame();
        gameManager.setEpisode(e.getNewEpisode());

        // Gestion du temps
        if(e.getTime() == TimeList.DAY){
            Bukkit.getWorld("UHCMAP").setTime(6000);
            for (RoleList value : RoleList.values()) {
                value.getInstance().onDay();
            }
        }
        if(e.getTime() == TimeList.NIGHT){
            Bukkit.getWorld("UHCMAP").setTime(12000);
            for (RoleList value : RoleList.values()) {
                value.getInstance().onNight();
            }
        }
        for (Player player : gameManager.getAlive()) {
            player.sendTitle("§bEpisode " + e.getNewEpisode(), "§9" + e.getTime());
            ScoreBoardUtils.updateScoreboard(player);
        }
    }

    @EventHandler
    public void onPlayerReceiveRole(PlayerRoleChange e){
        Player player = e.getPlayer();
        RoleList role = e.getRole();

        role.getInstance().onReceive(player);
        player.sendTitle("§b" + role.getName(), "§9Tu es dans le camp des " + role.getCamp().getDisplayName());
        ScoreBoardUtils.updateScoreboard(player);
    }

    @EventHandler
    public void onGameStop(GameEndEvent e){
        DBUHCMain.getInstance().getGameManager().getAlive().clear();
        DBUHCMain.getInstance().getGameManager().getPlayers().clear();
        DBUHCMain.getInstance().getGameManager().getSpectators().clear();
        DBUHCMain.getInstance().getGameManager().setStarted(false);
        GameEvents.getEpisode_timer().cancel();
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.teleport(new Location(Bukkit.getWorld("world"), 0, 175, 0));
            players.getInventory().clear();
            players.getInventory().setArmorContents(null);
            players.setHealth(20);
            players.setFoodLevel(20);
            players.setGameMode(GameMode.ADVENTURE);
            players.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            DBUHCMain.getInstance().getGameManager().addPlayer(players);
            PlayerManager.getPlayer(players).setRole(null);
            PlayerManager.getPlayer(players).setDead(false);
            PlayerManager.getPlayer(players).setKills(0);
        }
        for (RoleList role : DBUHCMain.getInstance().getConfigManager().getActiveRoles()) {
            role.getInstance().onGameFinished();
        }
        if(!e.isForced()){
            Bukkit.broadcastMessage("§aLa partie est terminée !");
            Bukkit.broadcastMessage("§aLe gagnant est " + e.getWinner().getDisplayName());
            Bukkit.broadcastMessage("§aLe camp §b" + e.getCamp().getDisplayName() + " §aa gagné !");
        }
    }

    public static BukkitTask getEpisode_timer() {
        return episode_timer;
    }
}
