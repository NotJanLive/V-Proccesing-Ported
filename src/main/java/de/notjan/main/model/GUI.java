package de.notjan.main.model;

import de.notjan.main.Main;
import de.notjan.main.configuration.messages.SuccessMessage;
import de.notjan.main.utils.ApiversionChecker;
import de.notjan.main.utils.HiddenStringUtils;
import de.notjan.main.utils.SecondsConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI {
    protected Inventory inv;
    protected String identifier;
    protected String ID;
    public GUI(int size, String title, String identifier, String ID){
        this.inv = Bukkit.createInventory(null,size,ChatColor.translateAlternateColorCodes('&',title));
        this.identifier = identifier;
        this.ID = ID;

        for(int i = 0;i<9;i++){
            inv.setItem(i,border());
        }
        for(int i = inv.getSize()-9;i<inv.getSize();i++){
            inv.setItem(i,border());
        }
        setContent();
        updateInventory();
        inv.setItem(inv.getSize()-1,identifierStack());
    }

    protected BukkitRunnable runAsync(final Runnable runnable){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        r.runTaskAsynchronously(Main.plugin);
        return r;
    }

    public abstract void setContent();
    public abstract void updateInventory();
    private ItemStack identifierStack(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 7);
        }
        else{
            stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SuccessMessage.GUI_Border.getMessage()+ HiddenStringUtils.encodeString(identifier)));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack acceptButton(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 13);
        }
        else{
            stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Accept.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack process_Button(Processer processer){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 1);
        }
        else{
            stack = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Start_Task.getMessage()));
        if(processer!=null){
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&',"&e"+processer.getDurationString()));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_NeededItems.getMessage()));
            for(ItemStack s:processer.getRequiredItems()){
                String displayName = s.getItemMeta().getDisplayName();
                if(displayName == null || displayName.equals(""))
                    displayName = s.getType().toString();
                lore.add(ChatColor.translateAlternateColorCodes('&',"&7"+displayName+" &8[&e"+s.getAmount()+"&8]"));
            }
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_RewardItems.getMessage()));
            for(ItemStack s:processer.getOutputItems()){
                String displayName = s.getItemMeta().getDisplayName();
                if(displayName == null || displayName.equals(""))
                    displayName = s.getType().toString();
                lore.add(ChatColor.translateAlternateColorCodes('&',"&7"+displayName+" &8[&e"+s.getAmount()+"&8]"));
            }
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack emptySlot(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 8);
        }
        else{
            stack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_EMPTY.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack idleButton(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 4);
        }
        else{
            stack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Idle.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack activeTaskButton(ProcessTask task){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 3);
        }
        else{
            stack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();

        float remainingSeconds = Math.abs((System.currentTimeMillis()-task.getTimestampStop())/1000);
        float duration = (task.getTimestampStop()-task.getTimestampStarted())/1000;
        int percentage = (int)((1-(remainingSeconds/duration))*100);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Remaining_Time.getMessage()+""+ SecondsConverter.convertSeconds((long)remainingSeconds)));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&',"&6"+percentage+"%"));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack cancelButton(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 14);
        }
        else{
            stack = new ItemStack(Material.RED_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Cancel_Button.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack border(){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 7);
        }
        else{
            stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Border.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack borderBlack() {
        ItemStack stack;
        if (ApiversionChecker.isLegacyVersion(Main.plugin)) {
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) 15);
        } else {
            stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SuccessMessage.GUI_Border.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack painting() {
        ItemStack stack = new ItemStack(Material.PAINTING, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(this.identifier);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack percentage(int percent){
        ItemStack stack;
        if(ApiversionChecker.isLegacyVersion(Main.plugin)){
            stack = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte) 13);
        }
        else{
            stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE,1);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&b"+percent+"%"));
        stack.setItemMeta(meta);
        return stack;
    }
    public Inventory gui(){return this.inv;}
}
