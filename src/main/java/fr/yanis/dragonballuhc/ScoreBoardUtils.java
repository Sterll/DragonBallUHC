package fr.yanis.dragonballuhc;

import fr.yanis.dragonballuhc.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardUtils {

    public static void updateScoreboard(Player player){
        if(DBUHCMain.getInstance().getGameManager().isStarted()){
            ScoreboardManager scoreboardManager = org.bukkit.Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("§6§lDragonBallUHC");

            Score episode = objective.getScore("§7» §fEpisode§7: §6" + DBUHCMain.getInstance().getGameManager().getEpisode());
            episode.setScore(1);

            Score players = objective.getScore("§7» §fJoueurs§7: §6" + DBUHCMain.getInstance().getGameManager().getAlive().size());
            players.setScore(2);

            Score kills = objective.getScore("§7» §fKills§7: §6" + PlayerManager.getPlayer(player).getKills());
            kills.setScore(3);

            Score role = objective.getScore("§7» §fRôle§7: §6" + (PlayerManager.getPlayer(player).getRole() != null ? PlayerManager.getPlayer(player).getRole().getName() : "§cAucun"));
            role.setScore(4);

            player.setScoreboard(scoreboard);
        }
    }

}
