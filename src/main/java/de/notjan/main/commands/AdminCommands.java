package de.notjan.main.commands;

import de.notjan.main.Main;
import de.notjan.main.configuration.messages.ErrorMessage;
import de.notjan.main.configuration.messages.SuccessMessage;
import de.notjan.main.model.Category;
import de.notjan.main.model.CategoryGUI;
import de.notjan.main.model.Processer;
import de.notjan.main.model.ProcesserGUI;
import de.notjan.main.utils.ApiversionChecker;
import de.notjan.main.utils.InventoryHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class AdminCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("list")){
                for(String processerID : Main.processConfiguration.processerCache.keySet()){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8- &b"+processerID));
                }
            }
            else if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8]======[&4V-Processing&8]======["));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&8> &bget &8- &6Gives you required Items for the processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&7/&acategory&8> &bset &8- &6Sets the position of the processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&7/&acategory&8> &bopen &8- &6Opens the Processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &blist &8- &6Lists up all processers&7!"));
            }
            else if(args[0].equalsIgnoreCase("reload")){
                Main.reloadPlugin();
            }
        }
        else if(args.length == 2){
            String ID = args[0];

            // Open GUI of Processer / Category

            if(args[1].equalsIgnoreCase("open")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;

                if(Main.processConfiguration.exist(ID)){
                    Processer processer = Main.processConfiguration.processerCache.get(ID);
                    p.openInventory(new ProcesserGUI(processer).gui());
                    return true;
                }
                else if(Main.categoryConfiguration.exist(ID)){
                    Category category = Main.categoryConfiguration.categoryCache.get(ID);
                    p.openInventory(new CategoryGUI(category).gui());
                    return true;
                }
                else {
                    p.sendMessage(ErrorMessage.No_Processer_or_Category.getMessage());
                    return false;
                }
            }

            // Set Location of Processer / Category

            else if(args[1].equalsIgnoreCase("set")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;
                Location newLoc;

                if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                   Block block =  p.getTargetBlock(null,5);
                   if(block == null || block.isEmpty()){
                       p.sendMessage(ErrorMessage.Look_At_Block.getMessage());
                       return false;
                   }
                   newLoc = block.getLocation();
                }
                else {
                    RayTraceResult result = p.rayTraceBlocks(5);
                    if(result==null){
                        p.sendMessage(ErrorMessage.Look_At_Block.getMessage());
                        return false;
                    }

                    newLoc = result.getHitBlock().getLocation();
                }

                if(Main.processConfiguration.exist(ID)){
                    if(Main.categoryConfiguration.locationCache.containsKey(newLoc)){
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                        return false;
                    }

                    if(Main.processConfiguration.changeLocation(ID,newLoc)){
                        p.sendMessage(SuccessMessage.Successfully_Changed_Loc.getMessage());
                    }
                    else {
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                    }

                }
                else if(Main.categoryConfiguration.exist(ID)){
                    if(Main.processConfiguration.locationCache.containsKey(newLoc)){
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                        return false;
                    }


                    if(Main.categoryConfiguration.changeLocation(ID,newLoc)){
                        p.sendMessage(SuccessMessage.Successfully_Changed_Loc.getMessage());
                    }
                    else {
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                    }
                }
                else {
                    p.sendMessage(ErrorMessage.No_Processer_or_Category.getMessage());
                }
            }

            else if(args[1].equalsIgnoreCase("get")){
                if(!(sender instanceof Player))
                    return false;
                if(Main.processConfiguration.exist(ID))
                    sender.sendMessage(ErrorMessage.No_Processer.getMessage());
                Processer processer = Main.processConfiguration.processerCache.get(ID);
                Player p = (Player) sender;
                InventoryHandler.givePlayerRequiredItems(p,processer);
                p.sendMessage(SuccessMessage.Successfully_Command.getMessage());
            }
        }
        return false;
    }
}
