package de.notjan.main.configuration;

import de.notjan.main.Main;
import de.notjan.main.model.Category;
import de.notjan.main.model.Processer;
import de.notjan.main.utils.Serializer;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryConfiguration extends Configuration{

    public Map<String, Category> categoryCache;
    public Map<Location, Category> locationCache;
    public Map<Category, Hologram> hologramCache;

    public CategoryConfiguration(Plugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
        locationCache = new HashMap<>();
        hologramCache = new HashMap<>();
        initCache();
    }

    @Override
    void setupConfig() {

        List<String> list = new ArrayList<>();
        list.add("processer_1");
        List<String> description = new ArrayList<>();
        description.add("&1First Line of GUI");
        description.add("&6V-Processing Ported");
        description.add("&eMade by &4NotJan");

        config.options().header("Config to setup categories of processers");
        config.addDefault("Categories.category_1.CategoryName","&aMill");
        config.addDefault("Categories.category_1.Description",description);
        config.addDefault("Categories.category_1.UseHologram",true);
        config.addDefault("Categories.category_1.UseLargeHologram",true);
        config.addDefault("Categories.category_1.Processers",list);
        config.addDefault("Categories.category_1.Location","none");

        config.options().copyDefaults(true);
        save();
        Main.consoleMessage("&b"+fileName+" loaded successfully!");

    }

    public void initCache(){
        categoryCache = new HashMap<>();
        for(String categoryID:config.getConfigurationSection("Categories").getKeys(false)){
            Category category = getCategory(categoryID);
            if(category!=null){
                categoryCache.put(category.getCategoryID(),category);
                if(category.getLocation()!=null){
                    if(locationCache.containsKey(category.getLocation()))
                        throw new IllegalStateException("Categories need unique Locations! They can't share the same location");
                    locationCache.put(category.getLocation(),category);
                    createHologram(category);
                }
            }
        }
    }

    public boolean changeLocation(String categoryID,Location loc){
        if(categoryID == null)
            return false;
        if(loc == null)
            return false;
        if(!categoryCache.containsKey(categoryID))
            return false;
        if(locationCache.containsKey(loc))
            return false;
        Category category = categoryCache.get(categoryID);
        Location oldLoc = category.getLocation();
        if(oldLoc!=null)
            locationCache.remove(oldLoc);
        if(!changeLoc(categoryID,loc))
            return false;
        categoryCache.get(categoryID).setLocation(loc);
        locationCache.put(loc,categoryCache.get(categoryID));
        createHologram(category);
        return true;
    }

    public boolean exist(String categoryID){
        if(categoryID == null)
            return false;
        return categoryCache.containsKey(categoryID);
    }

    private boolean createHologram(Category category){
        if(category == null)
            return false;
        if(!Main.settings.useHolograms())
            return false;
        if(!category.isUseHologram())
            return false;
        if(category.getLocation()==null)
            return false;
        if(hologramCache.containsKey(category) && !hologramCache.get(category).isDeleted())
            hologramCache.get(category).delete();

        Location loc = category.getLocation();
        Hologram hologram = HolographicDisplaysAPI.get(Main.plugin).createHologram(loc);

        hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&',category.getName()));
        hologram.getLines().appendText("");
        float lines = 2;

        for(int i = 0;i<category.getDescription().size();i++){
            hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&',category.getDescription().get(i)));
            lines++;
        }

        int newY = (int) lines/2;
        Location fixedLoc = new Location(loc.getWorld(),loc.getX()+0.5,loc.getY()+newY+0.5,loc.getZ()+0.5);
        hologram.setPosition(fixedLoc);
        hologramCache.put(category,hologram);
        return true;
    }

    private boolean changeLoc(String categoryID, Location loc){
        if(!exist(categoryID))
            return false;
        config.set("Categories."+categoryID+".Location", Serializer.serializeLocation(loc));
        save();
        return true;
    }

    private Category getCategory(String categoryID){
        if(categoryID == null)
            throw new NullPointerException("getCategory can't handle null as parameter!");
        if(!config.isSet("Categories."+categoryID))
            throw new IllegalArgumentException("Category couldn't be found in config!");
        if(!config.isConfigurationSection("Categories."+categoryID))
            throw new IllegalArgumentException("Category couldn't be found in config!");

        ConfigurationSection section = config.getConfigurationSection("Categories."+categoryID);
        String name = section.getString("CategoryName");
        boolean useHologram = section.getBoolean("UseHologram");
        boolean useLargeHologram = section.getBoolean("UseLargeHologram");
        Location loc = Serializer.deserializeLocation(section.getString("Location"));
        List<String> list = section.getStringList("Processers");
        List<Processer> processerList = list.stream().distinct().map(processerID -> {
           if(Main.processConfiguration.exist(processerID)){
               return Main.processConfiguration.processerCache.get(processerID);
            }
            else
                return null;
        }).filter(processer -> processer!=null).collect(Collectors.toList());
        List<String> description = section.getStringList("Description");
        return new Category(categoryID,name,loc,processerList,useHologram,useLargeHologram,description);
    }


}
