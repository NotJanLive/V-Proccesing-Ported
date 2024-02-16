package de.notjan.main.configuration;

import de.notjan.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

public abstract class Configuration {
    protected Plugin plugin;
    protected String fileName;
    protected String pluginDirectory;
    protected File file;
    protected FileConfiguration config;

    public Configuration(Plugin plugin, String fileName, String pluginDirectory){
        this.plugin = plugin;
        this.fileName = fileName;
        this.pluginDirectory = pluginDirectory;
        this.file = new File(Main.plugin.getDataFolder()+pluginDirectory,fileName);
        this.config = YamlConfiguration.loadConfiguration(file);
        setupConfig();
        save();
    }

    abstract void setupConfig();

    public void save(){
        try{
            config.save(file);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    void reload(){
        config = YamlConfiguration.loadConfiguration(file);
        Reader stream = null;
        try {
            stream = new InputStreamReader(Main.getPlugin(Main.class).getResource(fileName), "UTF8");
            if (stream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream);
                config.setDefaults(defConfig);
                save();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
