package de.notjan.main.configuration.messages;

import de.notjan.main.Main;
import org.bukkit.ChatColor;

public enum ErrorMessage {
    No_Processer_or_Category("&4Processer&7/&4Category &cdoes not exist!"),
    No_Processer("&cProcesser does not exist!"),
    Not_Enough_Items("&cYou don't have the required Items!"),
    Already_Processing("&cYou are already processing here!"),
    Not_Enough_InvSpace("&cYou don't have enough space in your inventory!"),
    Loc_Already_Used("&cLocation already in use!"),
    Look_At_Block("&cYou need to look at a block in range!"),
    ;

    private String message;
    ErrorMessage(String msg){
        this.message = msg;
    }

    public String getDefaultMessage(){return this.message;}
    public String getMessage(){return ChatColor.translateAlternateColorCodes('&', Main.messages.getMessage("Error_Messages."+this.name()));}
}
