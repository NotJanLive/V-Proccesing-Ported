package de.notjan.main.listener;


import de.notjan.main.Main;
import de.notjan.main.configuration.CategoryConfiguration;
import de.notjan.main.configuration.ProcessConfiguration;
import de.notjan.main.configuration.messages.ErrorMessage;
import de.notjan.main.configuration.messages.SuccessMessage;
import de.notjan.main.dataconnection.PlayerSession;
import de.notjan.main.model.*;
import de.notjan.main.utils.InventoryHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        PlayerSession.addToCache(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerSession.removeFromCache(p);
    }

    @EventHandler
    public void blockInteract(PlayerInteractEvent e){
        Block block = e.getClickedBlock();
        Player p = e.getPlayer();

        if(block == null)
            return;

        // Hier steht ein Processer
        if(Main.processConfiguration.locationCache.containsKey(block.getLocation())){
            e.setCancelled(true);
            Processer processer = Main.processConfiguration.locationCache.get(block.getLocation());
            if(processer!=null)
                p.openInventory(new ProcesserGUI(processer).gui());
        }
        // Hier steht eine Kategorie
        else if(Main.categoryConfiguration.locationCache.containsKey(block.getLocation())){
            e.setCancelled(true);
            Category category = Main.categoryConfiguration.locationCache.get(block.getLocation());
            if(category!=null)
                p.openInventory(new CategoryGUI(category).gui());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null)
            return;

        if (!e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            String identifier = e.getClickedInventory().getItem(4).getItemMeta().getDisplayName();

            PlayerSession session = PlayerSession.getSession(p);

            // Es handelt sich um einen Processer-Identifier!


            if (identifier.contains(ProcesserGUI.identifier)) {

                ProcessConfiguration config = Main.processConfiguration;
                identifier = identifier.replace(ProcesserGUI.identifier, "");
                if (!config.exist(identifier))
                    throw new IllegalStateException("The given Processer: " + identifier + " does not exist somehow?");
                Processer processer = config.processerCache.get(identifier);

                if (item.equals(GUI.process_Button(processer))) {
                    if (session.hasPlayerTask(processer)) {
                        p.sendMessage(ErrorMessage.Already_Processing.getMessage());
                        e.setCancelled(true);
                        return;
                    }

                    if (!InventoryHandler.hasPlayerRequiredItems(p, processer)) {
                        p.sendMessage(ErrorMessage.Not_Enough_Items.getMessage());
                        e.setCancelled(true);
                        return;
                    }

                    try {
                        InventoryHandler.takeItems(p, processer);
                        session.createTask(processer);
                        p.sendMessage(SuccessMessage.Process_Begin.getMessage());
                        e.setCancelled(true);
                    } catch (SQLException ex) {
                        System.out.println("There was an error creating a new Task!");
                        ex.printStackTrace();
                    }
                } else if (item.equals(GUI.acceptButton())) {
                    if (session.hasPlayerTask(processer)) {
                        ProcessTask task = session.getTask(processer);
                        if (!task.isFinished())
                            return;
                        if (!InventoryHandler.givePlayerProcessedItems(p, processer)) {
                            p.sendMessage(ErrorMessage.Not_Enough_InvSpace.getMessage());
                            e.setCancelled(true);
                            return;
                        }
                        try {
                            session.removeTask(processer);
                            p.sendMessage(SuccessMessage.Successfully_Processed.getMessage());
                        } catch (SQLException ex) {
                            System.out.println("Error while trying to remove Task from Database!");
                            ex.printStackTrace();
                        }
                    }
                } else if (session.hasPlayerTask(processer)) {
                    ProcessTask task = session.getTask(processer);
                    if (task.isFinished())
                        return;
                    if (item.equals(GUI.cancelButton())) {
                        if (!InventoryHandler.givePlayerRequiredItems(p, processer)) {
                            p.sendMessage(ErrorMessage.Not_Enough_InvSpace.getMessage());
                            e.setCancelled(true);
                            return;
                        }
                        try {
                            session.removeTask(processer);
                        } catch (SQLException ex) {
                            System.out.println("Error while trying to remove Task from Database!");
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (identifier.contains(CategoryGUI.identifier)) {

                CategoryConfiguration config = Main.categoryConfiguration;
                identifier = identifier.replace(CategoryGUI.identifier, "");

                if (!config.exist(identifier))
                    throw new IllegalStateException("The given Category: " + identifier + " does not exist somehow?");

                Category category = config.categoryCache.get(identifier);
                List<Processer> processerList = category.getProcesserList();
                Optional<Processer> processer = processerList.stream().filter(proc -> {
                    if (proc.getGuiIcon().equals(item)) {
                        p.openInventory(new ProcesserGUI(proc).gui());
                        return true;
                    } else if (proc.getGuiIcon().getType().equals(item.getType()) && proc.getGuiIcon().getAmount() == item.getAmount() && proc.getGuiIcon().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                        p.openInventory(new ProcesserGUI(proc).gui());
                        return true;
                    }
                    return false;
                }).findFirst();
            }
            e.setCancelled(true);
        }
    }

}
