package de.notjan.main.configuration;

import de.notjan.main.Main;
import de.notjan.main.configuration.messages.ErrorMessage;
import org.bukkit.plugin.Plugin;
import de.notjan.main.configuration.messages.SuccessMessage;

public class Messages extends Configuration{
    public Messages(Plugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    public String getMessage(String path){
        return config.getString(path);
    }

    @Override
    void setupConfig() {
            for(SuccessMessage message:SuccessMessage.values()){
                config.addDefault("Success_Messages."+message.name(),message.getDefaultMessage());
            }
            for(ErrorMessage message:ErrorMessage.values()){
                config.addDefault("Error_Messages."+message.name(),message.getDefaultMessage());
            }
            config.options().copyDefaults(true);
            save();
        Main.consoleMessage("&b"+fileName+" loaded successfully!");
    }
}