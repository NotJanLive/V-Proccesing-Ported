package de.notjan.main.dataconnection;

import de.notjan.main.model.ProcessTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;

public abstract class DataConnectionImpl {
    public abstract void connect() throws SQLException, ClassNotFoundException;
    public abstract boolean disconnect() throws SQLException;
    protected abstract void initTables() throws SQLException;
    public abstract void createTask(ProcessTask task) throws SQLException;
    public abstract void getTasksOfPlayer(Player p);
    public abstract void removeTask(UUID uuid, String processerID) throws SQLException;
    public abstract boolean isConnected() throws SQLException;

    static void runAsync(final Runnable runnable, Plugin plugin){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        r.runTaskAsynchronously(plugin);
    }
}
