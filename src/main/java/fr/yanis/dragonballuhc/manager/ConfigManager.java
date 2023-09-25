package fr.yanis.dragonballuhc.manager;

import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.roles.Role;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class ConfigManager {
    private ArrayList<RoleList> activeRoles;
    private int role_time;
    private int pvp_time;
    private int protection_time;

    private Inventory baseInventory;

    public ConfigManager(int role_time, int pvp_time, int protection_time) {
        this.activeRoles = new ArrayList<>();
        this.role_time = role_time;
        this.pvp_time = pvp_time;
        this.protection_time = protection_time;
    }

    public void setBaseInventory(Inventory baseInventory) {
        this.baseInventory = baseInventory;
    }
    public Inventory getBaseInventory() {
        return baseInventory;
    }
    public ArrayList<RoleList> getActiveRoles() {
        return activeRoles;
    }
    public void addActiveRole(RoleList role) {
        activeRoles.add(role);
    }
    public void removeActiveRole(RoleList role) {
        activeRoles.remove(role);
    }
    public int getRoleTime() {
        return role_time;
    }
    public void setRoleTime(int role_time) {
        this.role_time = role_time;
    }
    public int getPvpTime() {
        return pvp_time;
    }
    public void setPvpTime(int pvp_time) {
        this.pvp_time = pvp_time;
    }
    public int getProtectionTime() {
        return protection_time;
    }
    public void setProtectionTime(int protection_time) {
        this.protection_time = protection_time;
    }
}
