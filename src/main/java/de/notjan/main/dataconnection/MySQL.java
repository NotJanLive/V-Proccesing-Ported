package de.notjan.main.dataconnection;

import de.notjan.main.Main;
import de.notjan.main.configuration.messages.SuccessMessage;
import de.notjan.main.model.ProcessTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class MySQL extends DataConnectionImpl{
    private Connection connection;
    private String host,database,username,password;
    private int port;

    public MySQL(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public synchronized void connect() throws SQLException, ClassNotFoundException {
        if(connection!=null && !connection.isClosed()){
            return;
        }

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        initTables();
        Main.consoleMessage("&bMySQL connection established!");
    }

    public synchronized boolean disconnect() throws SQLException {
        if(connection==null ||connection.isClosed()){
            return false;
        }
        connection.close();
        return true;
    }

    protected void initTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `process_task` (" +
                "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
                "  `processerID` varchar(64) NOT NULL DEFAULT ''," +
                "  `player_uuid` varchar(64) NOT NULL DEFAULT ''," +
                "  `taskStarted` BIGINT(64) NOT NULL DEFAULT '0'," +
                "  `taskEnd` BIGINT(64) NOT NULL DEFAULT '0'," +
                "  PRIMARY KEY (`id`)\n" +
                ") ");
        Main.consoleMessage("&bMySQL Tables loaded loaded successfully!");
    }

    public void createTask(ProcessTask task) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO process_task (player_uuid,processerID,taskStarted,taskEnd) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
        // Player_UUID
        preparedStatement.setString(1,task.getUuid().toString());
        // ProcesserID
        preparedStatement.setString(2,task.getProcesserID());
        // taskStarted
        preparedStatement.setLong(3,task.getTimestampStarted());
        // taskEnd
        preparedStatement.setLong(4,task.getTimestampStop());
        preparedStatement.executeUpdate();
    }

    public void getTasksOfPlayer(Player p){
        runAsync(() -> {
            try {
                ResultSet result = connection.createStatement().executeQuery("SELECT * FROM process_task WHERE player_uuid = '"+p.getUniqueId().toString()+"'");
                while(result.next()){
                    String processerID = result.getString("processerID");
                    Long taskStarted = result.getLong("taskStarted");
                    Long taskEnd = result.getLong("taskEnd");
                    if(!Main.processConfiguration.exist(processerID))
                        return;
                    ProcessTask task = new ProcessTask(processerID,p.getUniqueId(),taskStarted,taskEnd);
                    PlayerSession.getSession(p).addTask(task);
                }
                Bukkit.getScheduler().runTask(Main.plugin, () -> {
                    p.sendMessage(SuccessMessage.Successfully_loaded_files.getMessage());
                });
            } catch (SQLException e) {e.printStackTrace();}
        }, Main.plugin);
    }

    public void removeTask(UUID uuid, String processerID) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM process_task WHERE player_uuid = ? AND processerID = ?",Statement.RETURN_GENERATED_KEYS);
        // Player_UUID
        preparedStatement.setString(1,uuid.toString());
        // ProcesserID
        preparedStatement.setString(2,processerID);
        preparedStatement.executeUpdate();
    }

    @Override
    public boolean isConnected() throws SQLException {
        return !connection.isClosed();
    }
}
