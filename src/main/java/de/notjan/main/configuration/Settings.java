package de.notjan.main.configuration;

import de.notjan.main.Main;
import org.bukkit.plugin.Plugin;

public class Settings extends Configuration {
    public Settings(Plugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    void setupConfig() {
        config.options().header("General Settings");
        config.addDefault("SETTINGS.UseMySQL",false);
        config.addDefault("SETTINGS.UseHolographicDisplays",false);
        config.addDefault("SETTINGS.UseUpdateChecker",true);
        config.options().copyDefaults(true);
        save();
        Main.consoleMessage("&bSettings.yml loaded successfully!");
    }

    public boolean useMySQL(){return config.getBoolean("SETTINGS.UseMySQL");}
    public boolean useHolograms(){return config.getBoolean("SETTINGS.UseHolographicDisplays");}
    public boolean useUpdateChecker(){return config.getBoolean("SETTINGS.UseUpdateChecker");}
}
