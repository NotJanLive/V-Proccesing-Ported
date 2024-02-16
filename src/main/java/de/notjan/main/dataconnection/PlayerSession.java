package de.notjan.main.dataconnection;

import de.notjan.main.Main;
import de.notjan.main.model.Processer;
import org.bukkit.entity.Player;
import de.notjan.main.model.ProcessTask;

import java.sql.SQLException;
import java.util.*;

public class PlayerSession {
    public static Set<PlayerSession> cache = new HashSet<>();

    public static synchronized PlayerSession addToCache(Player p){
        PlayerSession s = new PlayerSession(p);
        cache.add(s);
        Main.dataConnection.getTasksOfPlayer(p);
        return s;
    }
    public static synchronized PlayerSession getSession(Player p){
        Optional<PlayerSession> opt = cache.stream().filter(session -> session.playerUUID.toString().equals(p.getUniqueId().toString())).findAny();
        if(opt.isPresent())
            return opt.get();
        else
            return addToCache(p);
    }
    public static synchronized boolean removeFromCache(Player p){
        Optional<PlayerSession> opt = cache.stream().filter(session -> session.playerUUID.equals(p.getUniqueId())).findFirst();
        if(!opt.isPresent())
            return false;
        PlayerSession session = opt.get();
        cache.remove(session);
        System.out.println("Removed Player from Cache: "+p.getDisplayName());
        return true;
    }

    private UUID playerUUID;
    private List<ProcessTask> activeTasks;

    public PlayerSession(Player p){
        this.activeTasks = new ArrayList<>();
        this.playerUUID = p.getUniqueId();
    }
    public void createTask(Processer processer) throws SQLException {
        ProcessTask task = new ProcessTask(processer,playerUUID);

        if(hasPlayerTask(processer))
            return;
        activeTasks.add(task);
        Main.dataConnection.createTask(task);
    }

    public void addTask(ProcessTask task){
        if(task == null)
            return;
        activeTasks.add(task);
    }

    public boolean hasPlayerTask(Processer processer){
        ProcessTask task = new ProcessTask(processer,playerUUID);
        return activeTasks.contains(task);
    }

    public ProcessTask getTask(Processer processer){
        Optional<ProcessTask> opt = activeTasks.stream().filter(task -> task.getProcesserID().equals(processer.getProcesserID())).findFirst();
        return opt.orElseGet(null);
    }

    public boolean removeTask(Processer processer) throws SQLException {
        if(!hasPlayerTask(processer))
            return false;
        Main.dataConnection.removeTask(playerUUID,processer.getProcesserID());
        ProcessTask task = activeTasks.stream().filter(t -> t.getProcesserID().equals(processer.getProcesserID())).findAny().get();
        activeTasks.remove(task);
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof PlayerSession)){return false;}
        PlayerSession other = (PlayerSession) obj;
        return this.playerUUID.equals(other.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerUUID);
    }
}
