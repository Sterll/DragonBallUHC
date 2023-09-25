package fr.yanis.dragonballuhc.roles.duo;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.list.EffectList;
import fr.yanis.dragonballuhc.list.RoleItemType;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.manager.PlayerManager;
import fr.yanis.dragonballuhc.roles.Role;
import fr.yanis.dragonballuhc.utils.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class BlackGoku extends Role {

    //==================================================================================================================
    // ATTACK LIST
    //
    // 1 - Créer un portail | Invoque des clones qui attaquent les joueurs
    //==================================================================================================================

    private ArrayList<Player> bleedingPlayer = new ArrayList<>();
    private boolean wantToBleed = false;

    private int strikeCounter = 0;

    @Override
    public boolean isUnDetectable() {
        return true;
    }

    @Override
    public void onDay() {
        super.onDay();
    }
    public ArrayList<NPC> clones = new ArrayList<>();

    public BlackGoku() {
        Bukkit.getPluginManager().registerEvents(new NPCListener(), DBUHCMain.getInstance());
    }

    @Override
    public void onNight() {
        super.onNight();
    }

    @Override
    public void onTakeDamage(EntityDamageByEntityEvent e) {
        super.onTakeDamage(e);
        if(!getClones().isEmpty()){
            for (NPC clone : getClones()) {
                clone.getNavigator().setTarget(e.getDamager(), true);
            }
            getClones().clear();
        }
        if(getPlayer().getHealth() < 10){
            addEffect(EffectList.RESISTANCE1);
        }
        if(isTransformed()){
            strikeCounter++;
            if(strikeCounter == 10 || strikeCounter == 20 || strikeCounter == 30 || strikeCounter == 40){
                getPlayer().sendTitle("§b" + strikeCounter, "§9/45");
            }
        }
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
        for (NPC clone : getClones()) {
            clone.getNavigator().setTarget(e.getEntity(), true);
        }
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
    public void onGainHealth() {
        super.onGainHealth();
        if(getPlayer().getHealth() >= 10){
            removeEffect(EffectList.RESISTANCE1);
        }
    }

    @Override
    public void onKill(PlayerDeathEvent e) {
        super.onKill(e);
        if(isTransformed() && strikeCounter >= 45){
            setPermaTransformed(true);
            getPlayer().sendTitle("§bTransformation", "§cPermanente");
            removeTask("Terreur Rosé");
        }
    }

    public ArrayList<NPC> getClones() {
        return clones;
    }
    public void setClones(ArrayList<NPC> clones) {
        this.clones = clones;
    }
    public void addClone(NPC npc){
        clones.add(npc);
    }
    public void removeClone(NPC npc){
        clones.remove(npc);
        npc.destroy();
    }

    @Override
    public void onTransformation(String transfoName, int cd) {
        super.onTransformation(transfoName, cd);
        addEffect(EffectList.STRENGTH1);
        for (ItemStack itemStack : getPlayer().getInventory()) {
            if(itemStack != null){
                if(itemStack.equals(getRoleItems().get(RoleItemType.SWORD))){
                    itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 4);
                    getPlayer().updateInventory();
                }
            }
        }
        int slot = getPlayer().getInventory().getHeldItemSlot();
        getPlayer().getInventory().setItem(slot, getRoleItems().get(RoleItemType.TRANSFORMATION_ATTACK_ITEM));
    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        super.onChat(e);
        if(!e.getMessage().isEmpty() && String.valueOf(e.getMessage().charAt(0)).equalsIgnoreCase("!")){
            e.setMessage(e.getMessage().substring(1));
            e.setCancelled(true);
            if(RoleList.Zamasu.getInstance().isTaked() && RoleList.Zamasu.getInstance().isAlive() && RoleList.Zamasu.getInstance().getPlayer().isOnline()){
                RoleList.Zamasu.getInstance().getPlayer().sendMessage("§c" + getPlayer().getName() + "§7: " + e.getMessage());
                getPlayer().sendMessage("§c" + getPlayer().getName() + "§7: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTransformationEnd(String transfoName) {
        super.onTransformationEnd(transfoName);
        if(!isPermaTransformed()){
            removeEffect(EffectList.STRENGTH1);
            strikeCounter = 0;
            for (ItemStack itemStack : getPlayer().getInventory()) {
                if(itemStack != null){
                    if(itemStack.equals(getRoleItems().get(RoleItemType.SWORD_TRANSFORMED))){
                        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                        getPlayer().updateInventory();
                    }
                }
            }
            int slot = 0;
            for (ItemStack itemStack : getPlayer().getInventory()) {
                if(itemStack != null){
                    if(itemStack.equals(getRoleItems().get(RoleItemType.TRANSFORMATION_ATTACK_ITEM))){
                        getPlayer().getInventory().setItem(slot, getRoleItems().get(RoleItemType.TRANSFORMATION));
                    }
                }
                slot++;
            }
            addTask("pendingTransformation", new BukkitRunnable(){
                @Override
                public void run() {
                    setCanTransform(true);
                }
            }.runTaskLater(DBUHCMain.getInstance(), 20 * (60 * 5)));
        }
    }

    public void setWantToBleed(boolean wantToBleed) { this.wantToBleed = wantToBleed; }

    public boolean isWantToBleed() { return wantToBleed; }

    @Override
    public void onReceive(Player player) {
        super.onReceive(player);
        addRoleItem(RoleItemType.TRANSFORMATION, new ItemBuilder(Material.NETHER_STAR).setName("§6Terreur Rosé").setLore(Arrays.asList("", "§f- §c§nPermet de te transformer")).toItemStack());
        addRoleItem(RoleItemType.TRANSFORMATION_ATTACK_ITEM, new ItemBuilder(Material.NETHER_STAR).setName("§6Terreur Rosé").setLore(Arrays.asList("", "§c§nPermet de créer un portail")).toItemStack());
        addRoleItem(RoleItemType.SWORD, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux").setLore(Arrays.asList("", "§c§nLa faux la plus tranchante de ce pays")).addEnchant(Enchantment.DAMAGE_ALL, 3).toItemStack());
        addRoleItem(RoleItemType.SWORD_TRANSFORMED, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux").setLore(Arrays.asList("", "§c§nLa faux la plus tranchante de ce pays")).addEnchant(Enchantment.DAMAGE_ALL, 4).toItemStack());
        getPlayer().getInventory().addItem(getRoleItems().get(RoleItemType.SWORD));
        getPlayer().getInventory().addItem(getRoleItems().get(RoleItemType.TRANSFORMATION));
    }

    @Override
    public void onGameFinished() {
        super.onGameFinished();
        strikeCounter = 0;
        bleedingPlayer.clear();
        wantToBleed = false;
        for (NPC clone : getClones()) {
            clone.destroy();
        }
        getClones().clear();
    }

    @Override
    public void onUseRoleItem(PlayerInteractEvent e) {
        super.onUseRoleItem(e);
        if(e.getItem().equals(getRoleItems().get(RoleItemType.TRANSFORMATION))){
            if(!isTransformed() && isCanTransform()){
                onTransformation("Terreur Rosé", 20 * (60 * 5));
            } else {
                getPlayer().sendMessage("§cTu ne peux pas te transformer !");
            }
        }
        if(e.getItem().equals(getRoleItems().get(RoleItemType.TRANSFORMATION_ATTACK_ITEM))){
            if(canAttack(1)){
                for (NPC clone : getClones()) {
                    clone.destroy();
                }
                getClones().clear();
                for (int i = 1; i < 7; i++) {
                    NPC npc = CitizensAPI.getNPCRegistry().createNPC(getPlayer().getType(), getPlayer().getName());
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, getPlayer().getInventory().getHelmet());
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, getPlayer().getInventory().getChestplate());
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, getPlayer().getInventory().getLeggings());
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, getPlayer().getInventory().getBoots());
                    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, getRoleItems().get(RoleItemType.SWORD_TRANSFORMED));
                    npc.getOrAddTrait(SkinTrait.class).setSkinName(getPlayer().getName());
                    npc.spawn(getPlayer().getLocation().add(2 * i, 0, 0));
                    addClone(npc);
                }
                addTask("removeNpc", new BukkitRunnable(){
                    @Override
                    public void run() {
                        for (NPC clone : getClones()) {
                            clone.destroy();
                        }
                        getClones().clear();
                    }
                }.runTaskLater(DBUHCMain.getInstance(), 20 * (60 * 2) ));
                updateAttack(1, false);
                addTask("useAttack1", new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateAttack(1, true);
                    }
                }.runTaskLater(DBUHCMain.getInstance(), 20 * 5));
            } else {
                getPlayer().sendMessage("§cTu ne peux pas utiliser cette attaque !");
            }
        }
    }

    private class NPCListener implements Listener {
        @EventHandler
        public void onNPCDamage(NPCDamageByEntityEvent e){
            if(e.getDamager() instanceof Player){
                if(getClones().contains(e.getNPC()) && e.getDamager() instanceof Player){
                    if(!e.getDamager().equals(getPlayer())){
                        e.getNPC().getNavigator().setTarget(e.getDamager(), true);
                        e.setDamage(PlayerManager.getPlayer(((Player) e.getDamager()).getPlayer()).getRole().getInstance().getDamage((float) e.getDamage()) * getResistance());
                    } else {
                        e.setCancelled(true);
                        getPlayer().sendMessage("§cTu ne peux pas taper tes clônes !");
                    }
                }
            }
        }

        @EventHandler
        public void onNPCDie(NPCDeathEvent e){
            if(getClones().contains(e.getNPC())){
                removeClone(e.getNPC());
            }
        }
    }
}
