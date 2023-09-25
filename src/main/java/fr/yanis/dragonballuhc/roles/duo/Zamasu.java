package fr.yanis.dragonballuhc.roles.duo;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.list.EffectList;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.roles.Role;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static net.citizensnpcs.api.CitizensAPI.getPlugin;

public class Zamasu extends Role {

    private boolean canGetAbsorption = true;

    private ArrayList<Player> bleedingPlayer = new ArrayList<>();
    private boolean wantToBleed = false;

    @Override
    public boolean isUnDetectable() {
        return true;
    }

    @Override
    public void onUseRoleCommand(String[] args) {
        super.onUseRoleCommand(args);
        if(args.length == 1 && args[0].equalsIgnoreCase("kami")){
            wantToBleed = !wantToBleed;
            getPlayer().sendTitle("§cSaignement", wantToBleed ? "§aActivé" : "§cDésactivé");
        }
    }

    @Override
    public void onDealDamage(EntityDamageByEntityEvent e) {
        super.onDealDamage(e);
        if(!bleedingPlayer.contains(e.getEntity()) && wantToBleed){
            bleedingPlayer.add((Player) e.getEntity());
            ((Player) e.getEntity()).damage(1);
            e.getEntity().sendMessage("§cTu saignes !");
            launchBleed(e, 1);
        }
    }

    public void launchBleed(EntityDamageByEntityEvent e, int time){
        addTask(((Player) e.getEntity()).getDisplayName() + "_bleed", new BukkitRunnable(){
            @Override
            public void run() {
                if(bleedingPlayer.contains(e.getEntity())){
                    ((LivingEntity) e.getEntity()).damage(1);
                    cancelTask(((Player) e.getEntity()).getDisplayName() + "_bleed");
                    removeTask(((Player) e.getEntity()).getDisplayName() + "_bleed");
                    if(time < 3){
                        launchBleed(e, time + 1);
                    } else {
                        e.getEntity().sendMessage("§cTu ne saignes plus !");
                        bleedingPlayer.remove(e.getEntity());
                    }
                }
            }
        }.runTaskLater(DBUHCMain.getInstance(), 20 * 4));
    }

    @Override
    public void onReceive(Player player) {
        super.onReceive(player);
        addEffect(EffectList.SPEED1);
        regen();
    }


    public void regen(){
        addTask("regen", new BukkitRunnable(){
            @Override
            public void run() {
                if(isAlive() && getPlayer().isOnline()){
                    getPlayer().setHealth(getPlayer().getHealth() + 1);
                    cancelTask("regen");
                    removeTask("regen");
                    regen();
                }
            }
        }.runTaskLater(getPlugin(), 20 * 5));
    }
    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        super.onChat(e);
        if(!e.getMessage().isEmpty() && String.valueOf(e.getMessage().charAt(0)).equalsIgnoreCase("!")){
            e.setMessage(e.getMessage().substring(1));
            e.setCancelled(true);
            if(RoleList.BlackGoku.getInstance().isTaked() && RoleList.BlackGoku.getInstance().isAlive() && RoleList.BlackGoku.getInstance().getPlayer().isOnline()){
                RoleList.BlackGoku.getInstance().getPlayer().sendMessage("§c" + getPlayer().getName() + "§7: " + e.getMessage());
                getPlayer().sendMessage("§c" + getPlayer().getName() + "§7: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTakeDamage(EntityDamageByEntityEvent e) {
        super.onTakeDamage(e);
        if(getPlayer().getHealth() <= 2 && canGetAbsorption){
            canGetAbsorption = false;
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 3));
            addTask("absorption", new BukkitRunnable(){
                @Override
                public void run() {
                    if(isAlive() && getPlayer().isOnline()){
                        canGetAbsorption = true;
                        cancelTask("absorption");
                        removeTask("absorption");
                    }
                }
            }.runTaskLater(getPlugin(), 20 * 60));
        }
    }
}
