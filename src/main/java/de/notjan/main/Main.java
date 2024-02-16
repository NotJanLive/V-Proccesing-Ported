package de.notjan.main;

import de.notjan.main.commands.AdminCommands;
import de.notjan.main.configuration.*;
import de.notjan.main.dataconnection.DataConnectionImpl;
import de.notjan.main.dataconnection.MySQL;
import de.notjan.main.dataconnection.PlayerSession;
import de.notjan.main.dataconnection.SQLite;
import de.notjan.main.listener.Listeners;
import de.notjan.main.utils.ApiversionChecker;
import de.notjan.main.utils.UpdateChecker;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static ProcessConfiguration processConfiguration;
    public static Messages messages;
    public static MySQLConfig mySQLConfig;
    public static Settings settings;
    public static CategoryConfiguration categoryConfiguration;
    public static DataConnectionImpl dataConnection;
    public static String newestVersion;
    private static String PluginName = "V-Processing";

    @Override
    public void onEnable() {
        if(ApiversionChecker.isLegacyVersion(this))
            consoleMessage("&eFound Spigot Legacy Version&7: < &b1.13");
        else
            consoleMessage("&eFound new Spigot Version&7: > &b1.12");
        plugin = this;

        settings = new Settings(this,"settings.yml","/settings");
        if(!checkSoftDependency()){
            this.setEnabled(false);
            return;
        }
        messages = new Messages(this,"messages.yml","/settings");
        mySQLConfig = new MySQLConfig(this,"MySQL.yml","/dataconnection");
        processConfiguration = new ProcessConfiguration(this,"processer.yml","/ingame");
        categoryConfiguration = new CategoryConfiguration(this,"categories.yml","/ingame");
        if(settings.useMySQL()){
            dataConnection = new MySQL(mySQLConfig.getHost(),mySQLConfig.getPort(),mySQLConfig.getDatabase(),mySQLConfig.getUsername(),mySQLConfig.getPassword());
        }
        else {
            try {
                dataConnection = new SQLite(this,"v-Processing","/dataconnection");
            } catch (IOException e) {
                e.printStackTrace();
                this.setEnabled(false);
                consoleMessage("&4Plugin will shutdown due to an error occured.");
                return;
            }
        }
        connectDatabase();
        setupCommands();
        setupEventListener();
        Bukkit.getOnlinePlayers().stream().forEach(player -> PlayerSession.getSession(player));


        Main.consoleMessage("&aPlugin loaded successfully!");

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {connectionCecker();} catch (SQLException e) {e.printStackTrace();} catch (ClassNotFoundException e) {e.printStackTrace();}
        },100L, mySQLConfig.getPingTime()*20L);
    }

    @Override
    public void onDisable() {
        try {
            dataConnection.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reloadPlugin(){
        HolographicDisplaysAPI.get(Main.plugin).deleteHolograms();
        settings = new Settings(Main.plugin,"settings.yml","/settings");
        if(!Main.plugin.checkSoftDependency()){
            Main.plugin.setEnabled(false);
            return;
        }
        messages = new Messages(Main.plugin,"messages.yml","/settings");
        mySQLConfig = new MySQLConfig(Main.plugin,"MySQL.yml","/dataconnection");
        processConfiguration = new ProcessConfiguration(Main.plugin,"processer.yml","/ingame");
        categoryConfiguration = new CategoryConfiguration(Main.plugin,"categories.yml","/ingame");
    }


    public static void consoleMessage(String message){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&c"+PluginName+"&8] "+message));
    }

    private void connectDatabase(){
        try {
            dataConnection.connect();
        } catch (SQLException e) {
            Main.consoleMessage("&4Connection couldn't be established! "+e.getMessage());
            e.printStackTrace();
            this.setEnabled(false);
        } catch (ClassNotFoundException e) {
            Main.consoleMessage("&4JDBC Driver couldn't be found! "+e.getMessage());
            e.printStackTrace();
            this.setEnabled(false);
        }
    }


    private boolean checkSoftDependency(){
        if(!settings.useHolograms())
            return true;
        if(!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            Plugin[] pl = Bukkit.getPluginManager().getPlugins();
            consoleMessage("&ePlugin &bHolographicDisplays &ewas not found!");
            consoleMessage("&eFound Plugins&7: ");
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<pl.length-1;i++){
                sb.append(ChatColor.translateAlternateColorCodes('&',"&e"+pl[i]+"&8, "));
            }
            sb.append(ChatColor.translateAlternateColorCodes('&',"&e"+pl[pl.length-1]));
            Bukkit.getServer().getConsoleSender().sendMessage(sb.toString());
            consoleMessage("&eIf you think this is not your fault report this error to the Developer.");
            consoleMessage("&cThe Plugin will now shutdown!");
            return false;
        }
        String holoVersion = Bukkit.getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion();
        consoleMessage("&eFound HolographicDisplays with Version&7: &b"+holoVersion);
        if(!holoVersion.startsWith("3.0.4"))
        {
            consoleMessage("&eBut you picked the wrong version&7: &6"+ Bukkit.getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion());
            consoleMessage("&cYou need at least the Version&7: &a3.0.4");
            consoleMessage("&cThe Plugin will now shutdown!");
            return false;
        }
        return true;
    }

    private synchronized void connectionCecker() throws SQLException, ClassNotFoundException {
        if(dataConnection.isConnected())
            return;
        dataConnection.connect();
        consoleMessage("&aSuccessfully reconnected Database");
    }

    private void setupCommands(){
        this.getCommand("vproc").setExecutor(new AdminCommands());
    }

    private void setupEventListener(){
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
    }
}
