package de.notjan.main.utils;

import de.notjan.main.model.Processer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;

public class InventoryHandler {
    public static boolean givePlayerProcessedItems(Player p, Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        List<ItemStack> processedItems = processer.getOutputItems();
        int slotsNeeded = processedItems.size();
        int counter = 0;
        for(ItemStack s:p.getInventory().getContents()){
            if(s==null)
                counter++;
        }
        if(counter >= slotsNeeded){
            for(ItemStack s:processedItems){
                p.getInventory().addItem(s);
            }
            return true;
        }
        return false;
    }
    public static boolean givePlayerRequiredItems(Player p,Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        List<ItemStack> requiredItems = processer.getRequiredItems();
        int slotsNeeded = requiredItems.size();
        int counter = 0;
        for(ItemStack s:p.getInventory().getContents()){
            if(s==null)
                counter++;
        }
        if(counter >= slotsNeeded){
            for(ItemStack s:requiredItems){
                p.getInventory().addItem(s);
            }
            return true;
        }
        return false;
    }


    public static boolean hasPlayerRequiredItems(Player p, Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        Iterator<ItemStack> it = processer.getRequiredItems().iterator();
        int counter = 0;
        while(it.hasNext()){
            ItemStack stack = it.next();
            for(ItemStack i:p.getInventory().getContents()){
                if(i==null)
                    continue;
                String convertedDisplayName = HiddenStringUtils.removeHiddenString(i.getItemMeta().getDisplayName());
                ItemMeta meta = i.getItemMeta();
                meta.setDisplayName(convertedDisplayName);
                i.setItemMeta(meta);
                if(i.isSimilar(stack)){
                    counter+=i.getAmount();
                }
                if(counter>=stack.getAmount())
                    return true;
            }
        }
        return false;
    }
    public static void takeItems(Player p, Processer processer){
        if(p == null)
            return;
        if(processer == null)
            return;
        Iterator<ItemStack> it = processer.getRequiredItems().iterator();
        while(it.hasNext()){
            ItemStack stack = it.next();
            takeItem(p,stack);
        }
    }
    public static void takeItem(Player p, ItemStack stack){
        int toTake = stack.getAmount();
        for(int i = 0;i<p.getInventory().getContents().length;i++){
            ItemStack current = p.getInventory().getItem(i);
            String convertedDisplayName = HiddenStringUtils.removeHiddenString(current.getItemMeta().getDisplayName());
            ItemMeta meta = current.getItemMeta();
            meta.setDisplayName(convertedDisplayName);
            current.setItemMeta(meta);
            if(current!=null && current.isSimilar(stack)){
                if(toTake-current.getAmount() > 0){
                    //System.out.println("Stack ist kleiner: "+toTake+">"+current.getAmount());
                    toTake-=current.getAmount();
                    p.getInventory().setItem(i,null);
                }
                else if(toTake-current.getAmount() < 0){
                    //System.out.println("Stack ist größer: "+toTake+">"+current.getAmount());
                    current.setAmount(current.getAmount()-toTake);
                    break;
                }
                else{
                    //System.out.println("Stack ist gleich: "+toTake+"="+current.getAmount());
                    p.getInventory().setItem(i,null);
                    break;
                }
            }
        }
    }
}
