package fr.yanis.dragonballuhc.command;

import fr.yanis.dragonballuhc.DBUHCMain;
import fr.yanis.dragonballuhc.events.GameEvents;
import fr.yanis.dragonballuhc.events.custom.EpisodeChangeEvent;
import fr.yanis.dragonballuhc.events.custom.GameEndEvent;
import fr.yanis.dragonballuhc.events.custom.GameStartEvent;
import fr.yanis.dragonballuhc.list.RoleList;
import fr.yanis.dragonballuhc.utils.CustomWorldGenerator;
import fr.yanis.dragonballuhc.utils.InventoryBuilder;
import fr.yanis.dragonballuhc.utils.InventorySerializer;
import fr.yanis.dragonballuhc.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private ArrayList<Player> playerChangeInventory = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("dbuhcadmin")){
            if(args.length == 0){
                InventoryBuilder AdminInv = new InventoryBuilder("§cAdministration", 3, DBUHCMain.getInstance());
                // GESTION DES RÔLES
                AdminInv.setItem(0, new ItemBuilder(Material.BOOK).setName("§6Rôles").toItemStack(), (e) -> {
                    InventoryBuilder RInv = new InventoryBuilder("§6Rôles", 6, DBUHCMain.getInstance());
                    int index = 0;
                    for (RoleList role : RoleList.values()) {
                        int finalIndex = index;
                        RInv.setItem(index, new ItemBuilder(Material.BOOK).setName("§6" + role.getName()).setLore("", "§9Le rôle est : " + (DBUHCMain.getInstance().getConfigManager().getActiveRoles().contains(role) ? "§aActivé" : "§cDésactivé")).toItemStack(), (event) -> {
                            if(DBUHCMain.getInstance().getConfigManager().getActiveRoles().contains(role)){
                                DBUHCMain.getInstance().getConfigManager().getActiveRoles().remove(role);
                                event.getWhoClicked().sendMessage("§cLe rôle " + role.getName() + " a été désactivé !");
                                List<String> lore = new ArrayList<>();
                                lore.add("");
                                lore.add("§9Le rôle est : §cDésactivé");
                                RInv.updateLore(finalIndex, lore);
                            } else {
                                DBUHCMain.getInstance().getConfigManager().getActiveRoles().add(role);
                                sender.sendMessage("§aLe rôle " + role.getName() + " a été activé !");
                                List<String> lore = new ArrayList<>();
                                lore.add("");
                                lore.add("§9Le rôle est : §aActivé");
                                RInv.updateLore(finalIndex, lore);
                            }
                        });
                        index++;
                    }
                    RInv.open(Bukkit.getPlayer(sender.getName()));
                });
                // GENERER LE MONDE
                AdminInv.setItem(4, new ItemBuilder(Material.GRASS).setName("§6Générer le monde").toItemStack(), (event) -> {
                    sender.sendMessage("§6Création du monde en cours...");
                    WorldCreator worldCreator = new WorldCreator("UHCMAP");
                    worldCreator.generator();
                    Bukkit.getServer().createWorld(worldCreator);
                });
                // GESTION DE L'INVENTAIRE
                AdminInv.setItem(8, new ItemBuilder(Material.CHEST).setName("§6Définir l'inventaire de base").toItemStack(), (event) -> {
                    event.getWhoClicked().closeInventory();
                    Player player = (Player) event.getWhoClicked();
                    if(!playerChangeInventory.contains(player)){
                        player.sendTitle("§6Définir l'inventaire de base", "§cRecliquez sur l'item pour confirmer");
                        playerChangeInventory.add(player);
                    } else {
                        player.sendTitle("§6Définir l'inventaire de base", "§aL'inventaire de base a été défini !");
                        playerChangeInventory.remove(player);
                        DBUHCMain.getInstance().getConfigManager().setBaseInventory(player.getInventory());
                        DBUHCMain.getInstance().getConfig().set("game.inventory", InventorySerializer.inventoryToBase64(player.getInventory()));
                        DBUHCMain.getInstance().saveConfig();
                    }
                });
                // GESTION DEs TIMERS
                AdminInv.setItem(22, new ItemBuilder(Material.ENDER_PEARL).setName("§9Gestion des timers").setLore("", "§f- §bPermet de choisir quand la protection ou le pvp se désactive/s'active").toItemStack(), clickEvent -> {
                    InventoryBuilder UInv = new InventoryBuilder("§bGestion des timers", 1, DBUHCMain.getInstance());
                    UInv.setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                        event.setCancelled(true);
                    });

                    // PROTECTION TIMER
                    UInv.setItem(1, new ItemBuilder(Material.BEACON).setName("§aProtection").toItemStack(), clickEvent1 -> {
                        InventoryBuilder PInv = new InventoryBuilder("§bProtection", 3, DBUHCMain.getInstance());
                        for (int i = 0; i < 10; i++) {
                            PInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        PInv.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+1").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() + 1);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        PInv.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+2").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() + 2);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        PInv.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+5").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() + 5);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        PInv.setItem(13, new ItemBuilder(Material.BEACON).setName("§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime() + " min").toItemStack(), (event) -> {
                            event.setCancelled(true);
                        });
                        PInv.setItem(14, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-5").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() - 5);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        PInv.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-2").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() - 2);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        PInv.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-1").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setProtectionTime(DBUHCMain.getInstance().getConfigManager().getProtectionTime() - 1);
                            PInv.updateName(13, "§9Protection §b" + DBUHCMain.getInstance().getConfigManager().getProtectionTime());
                        });
                        for (int i = 17; i < 27; i++) {
                            PInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        PInv.open(Bukkit.getPlayer(sender.getName()));
                    });

                    // ROLE TIMER
                    UInv.setItem(4, new ItemBuilder(Material.SKULL_ITEM).setName("§aRôle").toItemStack(), clickEvent1 -> {
                        InventoryBuilder TRInv = new InventoryBuilder("§bRôle", 3, DBUHCMain.getInstance());
                        for (int i = 0; i < 10; i++) {
                            TRInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        TRInv.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+1").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() + 1);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        TRInv.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+2").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() + 2);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        TRInv.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+5").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() + 5);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        TRInv.setItem(13, new ItemBuilder(Material.SKULL_ITEM).setName("§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime() + " min").toItemStack(), (event) -> {
                            event.setCancelled(true);
                        });
                        TRInv.setItem(14, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-5").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() - 5);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        TRInv.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-2").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() - 2);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        TRInv.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-1").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setRoleTime(DBUHCMain.getInstance().getConfigManager().getRoleTime() - 1);
                            TRInv.updateName(13, "§9Rôle §b" + DBUHCMain.getInstance().getConfigManager().getRoleTime());
                        });
                        for (int i = 17; i < 27; i++) {
                            TRInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        TRInv.open(Bukkit.getPlayer(sender.getName()));
                    });

                    // PVP TIMER
                    UInv.setItem(7, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aPvP").toItemStack(), clickEvent1 -> {
                        InventoryBuilder PInv = new InventoryBuilder("§bPvP", 3, DBUHCMain.getInstance());
                        for (int i = 0; i < 10; i++) {
                            PInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        PInv.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+1").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() + 1);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        PInv.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+2").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() + 2);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        PInv.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§a+5").setDurability((short) 5 ).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() + 5);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        PInv.setItem(13, new ItemBuilder(Material.BEACON).setName("§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime() + " min").toItemStack(), (event) -> {
                            event.setCancelled(true);
                        });
                        PInv.setItem(14, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-5").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() - 5);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        PInv.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-2").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() - 2);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        PInv.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§c-1").setDurability((short) 14).toItemStack(), clickEvent2 -> {
                            DBUHCMain.getInstance().getConfigManager().setPvpTime(DBUHCMain.getInstance().getConfigManager().getPvpTime() - 1);
                            PInv.updateName(13, "§9PvP §b" + DBUHCMain.getInstance().getConfigManager().getPvpTime());
                        });
                        for (int i = 17; i < 27; i++) {
                            PInv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§0").setDurability((short) 15).toItemStack(), (event) -> {
                                event.setCancelled(true);
                            });
                        }
                        PInv.open(Bukkit.getPlayer(sender.getName()));
                    });
                    UInv.setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("").setDurability((short) 15).toItemStack(), (event) -> {
                        event.setCancelled(true);
                    });
                    UInv.open(Bukkit.getPlayer(sender.getName()));
                });
                // LANCER & STOPPER LA PARTIE
                AdminInv.setItem(19, new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§aLancer la partie").setDurability((short) 5).toItemStack(), (event) -> {
                    if(Bukkit.getOnlinePlayers().size() == DBUHCMain.getInstance().getConfigManager().getActiveRoles().size()){
                        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(DBUHCMain.getInstance().getGameManager()));
                    } else if (Bukkit.getOnlinePlayers().size() < DBUHCMain.getInstance().getConfigManager().getActiveRoles().size()){
                        sender.sendMessage("§cIl y a trop de rôle activé pour lancer la partie !");
                    } else if (Bukkit.getOnlinePlayers().size() > DBUHCMain.getInstance().getConfigManager().getActiveRoles().size()){
                        sender.sendMessage("§cIl n'y a pas assez de rôle activé pour lancer la partie !");
                    }
                });
                AdminInv.setItem(25, new ItemBuilder(Material.BARRIER).setName("§cStopper la partie").toItemStack(), (event) -> {
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(DBUHCMain.getInstance().getGameManager(), null, false, null));
                });
                AdminInv.open(Bukkit.getPlayer(sender.getName()));
                return true;
            }
        }
        return false;
    }
}
