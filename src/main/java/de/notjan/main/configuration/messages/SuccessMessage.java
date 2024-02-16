package de.notjan.main.configuration.messages;

import de.notjan.main.Main;
import org.bukkit.ChatColor;

public enum SuccessMessage {

    Successfully_Command("&aSuccessfully done!"),
    Successfully_loaded_files("&aSuccessfully loaded your Playerfiles from the database!"),
    Successfully_Processed("&aProcessed Items successfully!"),
    Successfully_Changed_Loc("&aLocation successfully changed!"),
    Process_Begin("&aBeginning processing your items!"),
    GUI_Start_Task("&6Process Items"),
    GUI_Accept("&aTake processed items"),
    GUI_Idle("&eAlready Processing"),
    GUI_Border("&8*"),
    GUI_Seconds("&6Sekunden"),
    GUI_NeededItems("&6Needed Items:"),
    GUI_RewardItems("&6Result:"),
    GUI_Remaining_Time("&eRemaining Time: "),
    GUI_Press_to_Cancel("&4Press to Cancel."),
    GUI_Cancel_Button("&4Click to cancel"),
    GUI_Ready("&6Ready to process!"),
    GUI_Finished("&aFinished processing!"),
    GUI_EMPTY("&8Empty Slot"),
    Holograms_Duration("&eDuration: "),
    Holograms_NeededItems("&6Needed Items: "),
    Holograms_ProcessedItems("&6Reward Items: "),
    ;

    private String message;
    SuccessMessage(String msg){
        this.message = msg;
    }

    public String getDefaultMessage(){return this.message;}
    public String getMessage(){return ChatColor.translateAlternateColorCodes('&', Main.messages.getMessage("Success_Messages."+this.name()));}
}
