package de.notjan.main.configuration;

import de.notjan.main.Main;
import de.notjan.main.configuration.messages.SuccessMessage;
import de.notjan.main.model.Processer;
import de.notjan.main.utils.ApiversionChecker;
import de.notjan.main.utils.HiddenStringUtils;
import de.notjan.main.utils.Serializer;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessConfiguration extends Configuration{

    public Map<String, Processer> processerCache;
    public Map<Location, Processer> locationCache;
    public Map<Processer, Hologram> hologramCache;

    public ProcessConfiguration(Plugin plugin,String fileName, String pluginDirectory){
        super(plugin,fileName,pluginDirectory);
        locationCache = new HashMap<>();
        hologramCache = new HashMap<>();
        initProcesserCache();
    }

    public boolean exist(String processerID){
        if(processerID == null)
            return false;
        return processerCache.containsKey(processerID);
    }

    // Creates Holograms at Location of a Processer
    // Linescounter for adjusting hologram height
    private boolean createHologram(Processer processer){
        if(processer == null)
            return false;
        if(!Main.settings.useHolograms())
            return false;
        if(!processer.isUseHologram())
            return false;
        if(processer.getLocation() == null)
            return false;
        if(hologramCache.containsKey(processer) && !hologramCache.get(processer).isDeleted()){
            hologramCache.get(processer).delete();
        }
        Location loc = processer.getLocation();
        Hologram hologram = HolographicDisplaysAPI.get(Main.plugin).createHologram(loc);
        hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&',processer.getName()));
        hologram.getLines().appendText("");
        hologram.getLines().appendText(SuccessMessage.Holograms_Duration.getMessage()+processer.getDurationString());
        float lines = 4;
        if(processer.isUseLargeHologram()){
            hologram.getLines().appendText(SuccessMessage.Holograms_NeededItems.getMessage());
            for(ItemStack s:processer.getRequiredItems()){
                hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&',s.getItemMeta().getDisplayName()+" &8["+"&e"+s.getAmount()+"&8]"));
                hologram.getLines().appendItem(s);
                lines+=2;
            }
            hologram.getLines().appendText(SuccessMessage.Holograms_ProcessedItems.getMessage());
            lines++;
            for(ItemStack s:processer.getOutputItems()){
                hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&',s.getItemMeta().getDisplayName()+" &8["+"&e"+s.getAmount()+"&8]"));
                hologram.getLines().appendItem(s);
                lines+=2;
            }
        }
        int newY = (int) lines/2;
        Location fixedLoc = new Location(loc.getWorld(),loc.getX()+0.5,loc.getY()+newY+0.5,loc.getZ()+0.5);
        hologram.setPosition(fixedLoc);
        hologramCache.put(processer,hologram);
        return true;
    }

    // Changes Location of Processer
    public boolean changeLocation (String processerID, Location loc){
        if(processerID == null)
            return false;
        if(loc == null)
            return false;
        if(!processerCache.containsKey(processerID))
            return false;
        if(locationCache.containsKey(loc))
            return false;
        Processer processer = processerCache.get(processerID);
        Location oldLoc = processer.getLocation();
        // Remove from Locationcache
        if(oldLoc!=null)
            locationCache.remove(oldLoc);

        if(!changeLoc(processerID,loc))
            return false;
        processerCache.get(processerID).setLocation(loc);
        locationCache.put(loc,processerCache.get(processerID));
        createHologram(processer);
        return true;
    }

    // Initiates the Processer Cache
    private void initProcesserCache(){
        processerCache = new HashMap<>();
        for(String processerID:config.getConfigurationSection(configSections.PROCESSER.name()).getKeys(false)){
            Processer processer = getProcesser(processerID);
            if(processer!=null){
                processerCache.put(processer.getProcesserID(),processer);
                if(processer.getLocation()!=null){
                    if(locationCache.containsKey(processer.getLocation()))
                        throw new IllegalStateException("Processer need unique locations! They can't share the same location");
                    locationCache.put(processer.getLocation(),processer);
                    createHologram(processer);
                }
            }
        }
    }

    @Override
    void setupConfig() {
            config.options().header("Config to setup processers, amounts etc...");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.PROCESSER_NAME,"&aMill");

            if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.GUI_ITEM+".ID","BONE_MEAL");
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.GUI_ITEM+".SUBID",15);
            }
            else {
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.GUI_ITEM+".ID","BONE_MEAL");
            }

            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.DURATION,60);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.USE_HOLOGRAM,true);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.USE_LARGE_HOLOGRAM,true);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.DISPLAY_NAME,"&7Flour");


            if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.ID, "BONE_MEAL");
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.SUB_ID,15);
            }
            else
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.ID,"BONE_MEAL");

            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.AMOUNT,5);

            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.DISPLAY_NAME,"&6Bread");



            if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.ID,"BREAD");
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.SUB_ID,0);
            }
            else
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.ID,"BREAD");

            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.AMOUNT,1);
            if(ApiversionChecker.isLegacyVersion(Main.plugin))
                config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.SUB_ID,1);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.LOCATION,"none");

            config.options().copyDefaults(true);
            save();
        Main.consoleMessage("&b"+fileName+" loaded successfully!");
    }

    // Changes Location in configfile
    private boolean changeLoc(String processerID, Location loc){
        if(!exist(processerID))
            return false;
        config.set(configSections.PROCESSER.name()+"."+processerID+"."+configSections.LOCATION, Serializer.serializeLocation(loc));
        save();
        return true;
    }

    // Loads Processers from configFile
    private Processer getProcesser (String processerID){
        if(processerID == null)
            throw new NullPointerException("getProcesser can't handle null as parameter!");
        if(!config.isSet(configSections.PROCESSER.name()+"."+processerID)){
            throw new IllegalArgumentException("Processer couldn't be found in config!");
        }
        if(!config.isConfigurationSection(configSections.PROCESSER.name()+"."+processerID))
            throw new IllegalArgumentException("Processer couldn't be found in config!");

        ConfigurationSection section = config.getConfigurationSection(configSections.PROCESSER.name()+"."+processerID);
        String name = section.getString(configSections.PROCESSER_NAME.name());
        int duration = section.getInt(configSections.DURATION.name());
        Location loc = Serializer.deserializeLocation((section.getString(configSections.LOCATION.name())));

        List<ItemStack> requiredItems = new ArrayList<>();
        List<ItemStack> processedItems = new ArrayList<>();

        boolean useHologram = false;
        boolean useLargeHologram = false;

        ItemStack guiIcon;
        String guiIconID = section.getString(configSections.GUI_ITEM+".ID");

        try{
            Material mat = Material.valueOf(guiIconID);

            if(ApiversionChecker.isLegacyVersion(Main.plugin)){

                int subID = section.getInt(configSections.GUI_ITEM+".SUBID");
                guiIcon = new ItemStack(mat,1,(byte)subID);
                ItemMeta meta = guiIcon.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name+HiddenStringUtils.encodeString(processerID)));
                guiIcon.setItemMeta(meta);
            }
            else{
                guiIcon = new ItemStack(mat,1);
                ItemMeta meta = guiIcon.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name+HiddenStringUtils.encodeString(processerID)));
                guiIcon.setItemMeta(meta);
            }
        }
        catch(IllegalArgumentException e){
            Main.consoleMessage("&4The Material&7: &6"+guiIconID+" &4does not exist!");
            for(Material m:Material.values()){
                Main.consoleMessage("&e"+m.name());
            }
            throw new IllegalArgumentException("Material does not exist");
        }



        for(String key:config.getConfigurationSection(section.getCurrentPath()+"."+configSections.required_items).getKeys(false)){
            ConfigurationSection item = config.getConfigurationSection(section.getCurrentPath()+"."+configSections.required_items+"."+key);
            String displayName = config.getString(item.getCurrentPath()+"."+configSections.DISPLAY_NAME);
            String ID = config.getString(item.getCurrentPath()+"."+configSections.ID);
            int amount = config.getInt(item.getCurrentPath()+"."+configSections.AMOUNT);

            ItemStack stack;
            if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                int subID = config.getInt(item.getCurrentPath()+"."+configSections.SUB_ID);
                try{
                    Material mat = Material.valueOf(ID);
                    stack = new ItemStack(mat,amount,(byte)subID);
                }
                catch(IllegalArgumentException e){
                    Main.consoleMessage("&4The Material&7: &6"+ID+" &4does not exist!");
                    for(Material m:Material.values()){
                        Main.consoleMessage("&e"+m.name());
                    }
                    throw new IllegalArgumentException("Material does not exist");
                }
            }
            else {
                stack = new ItemStack(Material.getMaterial(ID),amount);
            }

            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayName));
            stack.setItemMeta(meta);
            requiredItems.add(stack);
        }

        for(String key:config.getConfigurationSection(section.getCurrentPath()+"."+configSections.processed_items).getKeys(false)){
            ConfigurationSection item = config.getConfigurationSection(section.getCurrentPath()+"."+configSections.processed_items+"."+key);
            String displayName = config.getString(item.getCurrentPath()+"."+configSections.DISPLAY_NAME);
            String ID = config.getString(item.getCurrentPath()+"."+configSections.ID);
            int amount = config.getInt(item.getCurrentPath()+"."+configSections.AMOUNT);
            useHologram = config.getBoolean(configSections.PROCESSER+"."+processerID+"."+configSections.USE_HOLOGRAM);
            useLargeHologram = config.getBoolean(configSections.PROCESSER+"."+processerID+"."+configSections.USE_LARGE_HOLOGRAM);

            ItemStack stack;
            if(ApiversionChecker.isLegacyVersion(Main.plugin)){
                int subID = config.getInt(item.getCurrentPath()+"."+configSections.SUB_ID);
                try{
                    Material mat = Material.valueOf(ID);
                    stack = new ItemStack(mat,amount,(byte)subID);
                }
                catch(IllegalArgumentException e){
                    Main.consoleMessage("&4The Material&7: &6"+ID+" &4does not exist!");
                    for(Material m:Material.values()){
                        Main.consoleMessage("&e"+m.name());
                    }
                    throw new IllegalArgumentException("Material does not exist");
                }
            }
            else {
                stack = new ItemStack(Material.getMaterial(ID),amount);
            }

            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayName));
            stack.setItemMeta(meta);
            processedItems.add(stack);
        }
        return new Processer(processerID,name,duration,loc,requiredItems,processedItems,useHologram,useLargeHologram,guiIcon);
    }

    // Will be changed in future
    // Just to avoid writing mistakes.
    enum configSections{
        PROCESSER,
        required_items,
        processed_items,
        DISPLAY_NAME,
        ID,
        AMOUNT,
        DURATION,
        PROCESSER_NAME,
        LOCATION,
        USE_HOLOGRAM,
        USE_LARGE_HOLOGRAM,
        ITEM_ID,
        SUB_ID,
        GUI_ITEM,
    }
}