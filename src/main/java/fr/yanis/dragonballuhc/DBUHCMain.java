package fr.yanis.dragonballuhc;

import fr.yanis.dragonballuhc.command.MainCommand;
import fr.yanis.dragonballuhc.command.RoleCommand;
import fr.yanis.dragonballuhc.events.GameEvents;
import fr.yanis.dragonballuhc.events.PlayerEvents;
import fr.yanis.dragonballuhc.events.RoleEvents;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.manager.ConfigManager;
import fr.yanis.dragonballuhc.manager.GameManager;
import fr.yanis.dragonballuhc.roles.Role;
import fr.yanis.dragonballuhc.utils.FileManager;
import fr.yanis.dragonballuhc.utils.InventorySerializer;
import fr.yanis.dragonballuhc.utils.actionbar.ActionBarUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class DBUHCMain extends JavaPlugin {

    private static DBUHCMain instance;
    private GameManager gameManager;
    private ConfigManager configManager;
    private ActionBarUtils actionBarUtils;

    private Inventory baseInventory;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        gameManager = new GameManager();
        actionBarUtils = new ActionBarUtils();
        configManager = new ConfigManager(getConfig().getInt("game.role_episode"), getConfig().getInt("game.pvp_time"), getConfig().getInt("game.protection_time"));
        if(getConfig().getString("game.inventory") != null){
            configManager.setBaseInventory(InventorySerializer.inventoryFromBase64(getConfig().getString("game.inventory")));
        } else {
            Bukkit.getLogger().warning("§cL'inventaire de base n'a pas été trouvé !");
        }
        for (String role : getConfig().getStringList("game.roles")) {
            configManager.addActiveRole(RoleList.getByName(role));
        }
        getServer().getPluginManager().registerEvents(new GameEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new RoleEvents(), this);
        getCommand("dbuhc").setExecutor(new RoleCommand());
        getCommand("dbuhcadmin").setExecutor(new MainCommand());

    }

    @Override
    public void onDisable() {
        getConfig().set("game.role_time", configManager.getRoleTime());
        getConfig().set("game.pvp_time", configManager.getPvpTime());
        getConfig().set("game.protection_time", configManager.getProtectionTime());
        ArrayList<String> roles = new ArrayList<>();
        for (RoleList activeRole : configManager.getActiveRoles()) {
            roles.add(activeRole.getName());
        }
        getConfig().set("game.roles", roles);
        saveConfig();
    }

    public static DBUHCMain getInstance() {
        return instance;
    }
    public GameManager getGameManager() {
        return gameManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ActionBarUtils getActionBarUtils() {
        return actionBarUtils;
    }
}
