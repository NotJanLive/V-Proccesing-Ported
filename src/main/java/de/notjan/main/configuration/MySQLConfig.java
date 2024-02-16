package de.notjan.main.configuration;

import de.notjan.main.Main;
import org.bukkit.plugin.Plugin;

public class MySQLConfig extends Configuration{
    public MySQLConfig(Plugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    void setupConfig() {
        config.options().header("Config to setup MySQL-Connection");
        config.addDefault("MySQL.DATABASE","v-processing");
        config.addDefault("MySQL.HOST","localhost");
        config.addDefault("MySQL.PORT",3306);
        config.addDefault("MySQL.USERNAME","root");
        config.addDefault("MySQL.PASSWORD","1234");
        config.addDefault("MySQL.PingTime",600);

        config.options().copyDefaults(true);
        save();
        Main.consoleMessage("&b"+fileName+" loaded successfully!");
    }

    public String getHost(){
        return config.getString("MySQL.HOST");
    }

    public int getPort(){
        return config.getInt("MySQL.PORT");
    }

    public String getDatabase(){
        return config.getString("MySQL.DATABASE");
    }

    public String getUsername(){
        return config.getString("MySQL.USERNAME");
    }

    public String getPassword(){
        return config.getString("MySQL.PASSWORD");
    }

    public int getPingTime(){return config.getInt("MySQL.PingTime");}
}
