package fr.yanis.dragonballuhc.events;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.ScoreBoardUtils;
import fr.yanis.dragonballuhc.events.custom.GameEndEvent;
import fr.yanis.dragonballuhc.list.RoleItemType;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.manager.GameManager;
import fr.yanis.dragonballuhc.manager.PlayerManager;
import fr.yanis.dragonballuhc.roles.duo.BlackGoku;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        new PlayerManager(player);
        GameManager gameManager = DBUHCMain.getInstance().getGameManager();
        if(gameManager.isStarted()) {
            gameManager.addSpectator(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(gameManager.getAlive().get(0).getLocation());
        } else {
            gameManager.addPlayer(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 175, 0));
        }
        ScoreBoardUtils.updateScoreboard(player);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(!DBUHCMain.getInstance().getGameManager().isStarted()){
            e.setCancelled(true);
            return;
        }
        if(!DBUHCMain.getInstance().getGameManager().isRoleGiven()) return;
        if(PlayerManager.getPlayer(e.getPlayer()).getRole().getInstance().getRoleItems().containsValue(e.getItemDrop().getItemStack())){
            e.setCancelled(true);
            PlayerManager.getPlayer(e.getPlayer()).getRole().getInstance().onDrop(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e){
        if(!DBUHCMain.getInstance().getGameManager().isStarted()){
            e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof Player && DBUHCMain.getInstance().getGameManager().isProtected()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e){
        if(DBUHCMain.getInstance().getGameManager().isProtected() || !DBUHCMain.getInstance().getGameManager().isStarted()){
            e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player && !DBUHCMain.getInstance().getGameManager().isPvp()){
            e.setCancelled(true);
            return;
        }
        if(!DBUHCMain.getInstance().getGameManager().isRoleGiven()) return;
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            BlackGoku blackGoku = (BlackGoku) RoleList.BlackGoku.getInstance();
            PlayerManager playerManager = PlayerManager.getPlayer((CitizensAPI.getNPCRegistry().getNPC(e.getDamager()) != null ? blackGoku.getPlayer() : (Player) e.getDamager()));
            PlayerManager damagerManager = PlayerManager.getPlayer(CitizensAPI.getNPCRegistry().getNPC(e.getEntity()) != null ? blackGoku.getPlayer() : (Player) e.getEntity());
            e.setDamage(damagerManager.getRole().getInstance().getDamage((float) e.getDamage()) * playerManager.getRole().getInstance().getResistance());
            damagerManager.getRole().getInstance().onDealDamage(e);
            playerManager.getRole().getInstance().onTakeDamage(e);
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e){
        if(!DBUHCMain.getInstance().getGameManager().isStarted()) return;
        if(DBUHCMain.getInstance().getGameManager().getAlive().size() > 1){
            if(DBUHCMain.getInstance().getGameManager().isRoleGiven()){
                PlayerManager.getPlayer(e.getEntity()).getRole().getInstance().onDeath(e);
                PlayerManager.getPlayer(e.getEntity()).setDead(true);
                if(e.getEntity().getKiller() != null){
                    PlayerManager.getPlayer(e.getEntity().getKiller()).getRole().getInstance().onKill(e);
                    PlayerManager.getPlayer(e.getEntity().getKiller()).addKill(1);
                }
            } else {
                if(e.getEntity().getKiller() != null){
                    PlayerManager.getPlayer(e.getEntity().getKiller()).addKill(1);
                }
            }
            DBUHCMain.getInstance().getGameManager().removeAlive(e.getEntity());
            DBUHCMain.getInstance().getGameManager().addSpectator(e.getEntity());
            e.getEntity().setGameMode(GameMode.SPECTATOR);
            e.setDeathMessage(null);
            e.getEntity().teleport(DBUHCMain.getInstance().getGameManager().getAlive().get(0).getLocation());
        } else {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(DBUHCMain.getInstance().getGameManager(), e.getEntity().getKiller(), false, PlayerManager.getPlayer(e.getEntity()).getRole().getCamp()));
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        if(e.getCurrentItem() == null) return;
        if(!DBUHCMain.getInstance().getGameManager().isStarted()) return;
        if(!DBUHCMain.getInstance().getGameManager().isRoleGiven()) return;
        if(e.getInventory().getType() == InventoryType.CHEST && PlayerManager.getPlayer((Player) e.getWhoClicked()).getRole().getInstance().getRoleItems().containsValue(e.getCurrentItem())) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getItem() == null) return;
        if(!DBUHCMain.getInstance().getGameManager().isStarted()) return;
        if(!DBUHCMain.getInstance().getGameManager().isRoleGiven()) return;
        if(PlayerManager.getPlayer(e.getPlayer()).getRole().getInstance().getRoleItems().containsValue(e.getItem())){
            e.setCancelled(true);
            PlayerManager.getPlayer(e.getPlayer()).getRole().getInstance().onUseRoleItem(e);
        }
    }

}
