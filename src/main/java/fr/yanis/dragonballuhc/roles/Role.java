package fr.yanis.dragonballuhc.roles;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.list.EffectList;
import fr.yanis.dragonballuhc.list.RoleItemType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Role {

    private Player player;
    private boolean isAlive = true;
    private boolean isTaked = false;
    private boolean isTransformed = false;
    private boolean isPermaTransformed = false;
    private boolean canTransform = true;
    private HashMap<RoleItemType, ItemStack> roleItems = new HashMap<>();
    private ArrayList<Role> knowRoles = new ArrayList<Role>();
    private ArrayList<Boolean> canAttack = new ArrayList<>();
    private ArrayList<EffectList> effects = new ArrayList<>();
    private HashMap<String, BukkitTask> tasks = new HashMap<>();

    public abstract boolean isUnDetectable();

    public void onDay(){
        updateWalkSpeed();
    }
    public void onNight(){
        updateWalkSpeed();
    }

    private void updateWalkSpeed() {
        if(effects.contains(EffectList.SPEED1)){
            player.setWalkSpeed(1 * (EffectList.SPEED1.getMultiplier() / 100F + 1));
        }
        if(effects.contains(EffectList.SPEED2)){
            player.setWalkSpeed(1 * (EffectList.SPEED2.getMultiplier() / 100F + 1));
        }
        if(effects.contains(EffectList.SPEED3)){
            player.setWalkSpeed(1 * (EffectList.SPEED3.getMultiplier() / 100F + 1));
        }
    }

    public void onTakeDamage(EntityDamageByEntityEvent e){}
    public void onDealDamage(EntityDamageByEntityEvent e){}
    public float getDamage(float baseDamage){
        if(effects.contains(EffectList.STRENGTH1)){
            return baseDamage * (EffectList.STRENGTH1.getMultiplier() / 100F + 1) ;
        } else if(effects.contains(EffectList.STRENGTH2)){
            return baseDamage * (EffectList.STRENGTH2.getMultiplier() / 100F + 1);
        } else {
            return baseDamage;
        }
    }

    public float getResistance(){
        if(effects.contains(EffectList.RESISTANCE1)){
            return 1 - (EffectList.RESISTANCE1.getMultiplier() / 100F);
        } else if(effects.contains(EffectList.RESISTANCE2)){
            return 1 - (EffectList.RESISTANCE2.getMultiplier() / 100F);
        } else {
            return 1;
        }
    }
    public void onDeath(PlayerDeathEvent e){
        for (BukkitTask task : getTasks().values()) {
            task.cancel();
        }
        setAlive(false);
        for (ItemStack value : getRoleItems().values()) {
            if(e.getDrops().contains(value)){
                e.getDrops().remove(value);
            }
        }
        setAlive(false);
    }
    public void onKill(PlayerDeathEvent e){}
    public void onGainHealth(){}
    public void onReceive(Player player){
        setAlive(true);
        setTaked(true);
        setPlayer(player);
        setCanTransform(true);
        updateActionBar();
    }
    public Player getPlayer() {
            return player;
        }
    public void setPlayer(Player player) {
            this.player = player;
    }
    public boolean isAlive() {
            return isAlive;
    }

    public boolean isCanTransform() {
        return canTransform;
    }
    public boolean canAttack(int attack){
        return canAttack.get(attack - 1);
    }

    public void setAttack(ArrayList<Boolean> canAttack) {
        this.canAttack = canAttack;
    }

    public void updateAttack(int attack, boolean canAttack){
        this.canAttack.set(attack - 1, canAttack);
    }

    public void removeAttack(int attack){
        this.canAttack.remove(attack - 1);
    }

    public void setCanTransform(boolean canTransform) {
        this.canTransform = canTransform;
    }

    public void onGameFinished(){
        for (BukkitTask task : getTasks().values()) {
            task.cancel();
        }
        setTaked(false);
        setAlive(false);
        setTransformed(false);
        setPermaTransformed(false);
        setPlayer(null);
    }
    public void setAlive(boolean alive) {
            isAlive = alive;
    }
    public boolean isTaked() {
            return isTaked;
    }
    public void setTaked(boolean taked) {
        isTaked = taked;
    }
    public ArrayList<Role> getKnowRoles() {
        return knowRoles;
    }
    public void setKnowRoles(ArrayList<Role> knowRoles) {
        this.knowRoles = knowRoles;
    }
    public ArrayList<EffectList> getEffects() {
        return effects;
    }
    public void setEffects(ArrayList<EffectList> effects) {
        this.effects = effects;
    }
    public void addEffect(EffectList effect){
        this.effects.add(effect);
    }
    public void removeEffect(EffectList effect){
        this.effects.remove(effect);
    }
    public boolean hasTransformation(){
        return false;
    }
    public boolean isTransformed() { return isTransformed; }

    public void onUseRoleItem(PlayerInteractEvent e){}

    public boolean isPermaTransformed() {
        return isPermaTransformed;
    }

    public void setPermaTransformed(boolean permaTransformed) {
        isPermaTransformed = permaTransformed;
    }

    public void setTransformed(boolean transformed) {
        isTransformed = transformed;
    }

    public void onDrop(PlayerDropItemEvent e){}
    public void onChat(AsyncPlayerChatEvent e){}

    public void onTransformation(String transfoName, int cd){
        setTransformed(true);
        setCanTransform(false);
        getPlayer().sendMessage("§9=====================");
        getPlayer().sendMessage("§cTu es maintenant transformé !");
        getPlayer().sendMessage("§9=====================");
        addTask(transfoName, new BukkitRunnable() {
            @Override
            public void run() {
                onTransformationEnd(transfoName);
            }
        }.runTaskLater(DBUHCMain.getInstance(), cd));
    }
    public void onTransformationEnd(String transfoName){
        removeTask(transfoName);
        if(!isPermaTransformed()){
            setTransformed(false);
            getPlayer().sendMessage("§9=====================");
            getPlayer().sendMessage("§cTu n'es plus transformé !");
            getPlayer().sendMessage("§9=====================");
        }
    }

    public HashMap<RoleItemType, ItemStack> getRoleItems() {
        return roleItems;
    }

    public void addRoleItem(RoleItemType type, ItemStack item){
        this.roleItems.put(type, item);
    }

    public HashMap<String, BukkitTask> getTasks() {
        return tasks;
    }
    public void setTasks(HashMap<String, BukkitTask> tasks) {
        this.tasks = tasks;
    }
    public void addTask(String name, BukkitTask task){
        this.tasks.put(name, task);
    }
    public void removeTask(String name){
        this.tasks.remove(name);
    }
    public void cancelTask(String name){
        this.tasks.get(name).cancel();
    }
    public void onUseRoleCommand(String[] args){}

    public void updateActionBar(){
        addTask("actionBar", Bukkit.getScheduler().runTaskTimer(DBUHCMain.getInstance(), () -> {
            if(!isAlive()){
                cancelTask("actionBar");
                removeTask("actionBar");
            }
            StringBuilder sb = new StringBuilder();
            for (EffectList effect : getEffects()) {
                sb.append(effect.getDisplay()).append(" ");
            }
            DBUHCMain.getInstance().getActionBarUtils().sendActionBar(getPlayer(), sb.toString());
        }, 0, 20));
    }
}
