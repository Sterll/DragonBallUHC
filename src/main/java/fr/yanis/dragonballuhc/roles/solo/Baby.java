package fr.yanis.dragonballuhc.roles.solo;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.list.EffectList;
import fr.yanis.dragonballuhc.list.RoleItemType;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.manager.PlayerManager;
import fr.yanis.dragonballuhc.roles.Role;
import fr.yanis.dragonballuhc.utils.InventoryBuilder;
import fr.yanis.dragonballuhc.utils.ItemBuilder;
import fr.yanis.dragonballuhc.utils.JsonMessageBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Baby extends Role {

    private ArrayList<RoleList> rolesStealed = new ArrayList<>();
    private RoleList roleStealed;
    private boolean alreadyRevive = false;

    @Override
    public boolean isUnDetectable() {
        return false;
    }

    @Override
    public void onReceive(Player player) {
        super.onReceive(player);
        addEffect(EffectList.BabySpeed);
        addEffect(EffectList.BabyResistance);
        addEffect(EffectList.BabyStrength);
        addRoleItem(RoleItemType.TRANSFORMATION, new ItemBuilder(Material.NETHER_STAR).setName("§dManipulation").setLore("", "§f- §bSauvegarde les rôles des défunts !").toItemStack());
    }

    @Override
    public void onKill(PlayerDeathEvent e) {
        super.onKill(e);
        rolesStealed.add(PlayerManager.getPlayer(e.getEntity()).getRole());
        getPlayer().sendTitle("§dManipulation", "§aTu as volé le rôle de " + e.getEntity().getName() + " !");
        if(!alreadyRevive){
            getPlayer().sendMessage("§6Souhaite tu résuciter " + e.getEntity().getName() + " ?");
            getPlayer().sendMessage("\n" +
                    new JsonMessageBuilder("Accepter").setUnderlined(true).setColor(ChatColor.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "dbuhc revive " + e.getEntity().getName())).toString());
        }
        if(isTransformed()) roleStealed.getInstance().onKill(e);
    }

    @Override
    public void onUseRoleCommand(String[] args) {
        super.onUseRoleCommand(args);
        if(args.length == 2 && args[0].equalsIgnoreCase("revive")){
            try{
                Player target = Bukkit.getPlayer(args[1]);
                if(!alreadyRevive){
                    if(PlayerManager.getPlayer(target).isDead()){
                        InventoryBuilder RInv = new InventoryBuilder("§dManipulation", 6, DBUHCMain.getInstance());
                        int i = 0;
                        for(RoleList role : rolesStealed){
                            RInv.setItem(i, new ItemBuilder(Material.PAPER).setName("§d" + role.getName()).setLore("", "§f- §bObtiens ce rôle durant 3 minutes").toItemStack(), clickEvent -> {
                                target.spigot().respawn();
                                target.teleport(getPlayer().getLocation());
                                target.setHealth(20);
                                target.setFoodLevel(20);
                                target.setExp(0);
                                target.setLevel(0);
                                target.setGameMode(GameMode.SURVIVAL);
                                rolesStealed.remove(role);
                                getPlayer().closeInventory();
                                PlayerManager.getPlayer(target).setRole(role);
                                role.getInstance().onReceive(target);
                            });
                            i++;
                        }
                        RInv.open(getPlayer());
                    }
                } else {
                    getPlayer().sendMessage("§cTu as déjà utilisé ta résurrection !");
                }
            } catch (Exception e){
                getPlayer().sendMessage("§cLe joueur " + args[1] + " n'existe pas !");
                return;
            }
        }
    }

    @Override
    public void onTakeDamage(EntityDamageByEntityEvent e) {
        super.onTakeDamage(e);
        if(isTransformed()) roleStealed.getInstance().onTakeDamage(e);
    }

    @Override
    public void onDealDamage(EntityDamageByEntityEvent e) {
        super.onDealDamage(e);
        if(isTransformed()) roleStealed.getInstance().onDealDamage(e);
    }

    @Override
    public void onDrop(PlayerDropItemEvent e) {
        super.onDrop(e);
        if(isTransformed()) roleStealed.getInstance().onDrop(e);
    }

    @Override
    public void onNight() {
        super.onNight();
        if(isTransformed()) roleStealed.getInstance().onNight();
    }

    @Override
    public void onDay() {
        super.onDay();
        if(isTransformed()) roleStealed.getInstance().onDay();
    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        super.onChat(e);
        if(isTransformed()) roleStealed.getInstance().onChat(e);
    }

    @Override
    public void onGainHealth() {
        super.onGainHealth();
        if(isTransformed()) roleStealed.getInstance().onGainHealth();
    }

    @Override
    public void onUseRoleItem(PlayerInteractEvent e) {
        super.onUseRoleItem(e);
        if(e.getItem().equals(RoleItemType.TRANSFORMATION)){
            if(!rolesStealed.isEmpty()){
                if(isCanTransform()){
                    InventoryBuilder RInv = new InventoryBuilder("§dManipulation", 6, DBUHCMain.getInstance());
                    int i = 0;
                    for(RoleList role : rolesStealed){
                        RInv.setItem(i, new ItemBuilder(Material.PAPER).setName("§d" + role.getName()).setLore("", "§f- §bObtiens ce rôle durant 3 minutes").toItemStack(), clickEvent -> {
                            getPlayer().sendTitle("§d" + role.getName(), "§aTu as volé ce rôle !");
                            rolesStealed.remove(role);
                            roleStealed = role;
                            e.getPlayer().closeInventory();
                            role.getInstance().onReceive(getPlayer());
                            onTransformation(role.getName(), (3 * 60) * 20);
                        });
                        i++;
                    }
                    RInv.open(getPlayer());
                } else {
                    e.getPlayer().sendMessage("§cTu ne peux pas te transformer pour le moment !");
                }
            } else {
                e.getPlayer().sendMessage("§cIl n'y a plus de rôle à voler !");
            }
        }
    }

    @Override
    public void onTransformationEnd(String transfoName) {
        super.onTransformationEnd(transfoName);
        roleStealed.getInstance().setPlayer(null);
        for (ItemStack value : roleStealed.getInstance().getRoleItems().values()) {
            getPlayer().getInventory().remove(value);
        }
        roleStealed = null;
    }

    public boolean isAlreadyRevive() {
        return alreadyRevive;
    }
}
