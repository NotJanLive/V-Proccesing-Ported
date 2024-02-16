package de.notjan.main.model;

import de.notjan.main.Main;
import de.notjan.main.dataconnection.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcesserGUI extends GUI {
    private Processer processer;
    public static String identifier = "Processer: ";
    String processerID;
    public ProcesserGUI(Processer processer){
        super(45, processer.getName(),identifier+processer.getProcesserID(), processer.getProcesserID());
        this.processer = processer;
        this.processerID = processer.getProcesserID();
    }

    @Override
    public void setContent() {
        inv.setItem(22, process_Button(this.processer));
        inv.setItem(10, borderBlack());
        inv.setItem(11, borderBlack());
        inv.setItem(12, borderBlack());
        inv.setItem(13, borderBlack());
        inv.setItem(14, borderBlack());
        inv.setItem(15, borderBlack());
        inv.setItem(16, borderBlack());
        inv.setItem(19, borderBlack());
        inv.setItem(25, borderBlack());
        inv.setItem(28, borderBlack());
        inv.setItem(29, borderBlack());
        inv.setItem(30, borderBlack());
        inv.setItem(31, borderBlack());
        inv.setItem(32, borderBlack());
        inv.setItem(33, borderBlack());
        inv.setItem(34, borderBlack());
        inv.setItem(4, painting());
    }

    @Override
    public void updateInventory() {
        new BukkitRunnable() {
            PlayerSession session;
            @Override
            public void run() {
                if(session==null&&inv.getViewers().size()>0){
                    Player p = (Player) inv.getViewers().get(0);
                    session = PlayerSession.getSession(p);
                }
                if(inv.getViewers().size()==0)
                    this.cancel();
                else {
                    if(session.hasPlayerTask(processer)){
                        ProcessTask task = session.getTask(processer);
                        if(task.isFinished()){
                            // Inventory updates to accept button!
                            //System.out.println("Finished!");
                            inv.setItem(20, acceptButton());
                            inv.setItem(21, acceptButton());
                            inv.setItem(22, acceptButton());
                            inv.setItem(23, acceptButton());
                            inv.setItem(24, acceptButton());

                            inv.setItem(9, acceptButton());
                            inv.setItem(18, acceptButton());
                            inv.setItem(27, acceptButton());

                            inv.setItem(17, acceptButton());
                            inv.setItem(26, acceptButton());
                            inv.setItem(35, acceptButton());

                            inv.setItem(13, borderBlack());
                            inv.setItem(31, borderBlack());
                            inv.setItem(4, painting());
                        }
                        else{
                            int percentage = task.getPercentage();
                            //System.out.println(difference+" Sekunden noch!");
                            //System.out.println(percentage+"%");

                            inv.setItem(22,null);

                            inv.setItem(9, idleButton());
                            inv.setItem(18, idleButton());
                            inv.setItem(27, idleButton());

                            inv.setItem(17, idleButton());
                            inv.setItem(26, idleButton());
                            inv.setItem(35, idleButton());

                            inv.setItem(20, idleButton());
                            inv.setItem(21, idleButton());
                            inv.setItem(22, idleButton());
                            inv.setItem(23, idleButton());
                            inv.setItem(24, idleButton());
                            inv.setItem(4, painting());


                            // Inventory updates its layout!
                            if(percentage>=20)
                                inv.setItem(20, percentage(20));
                            if(percentage>=40)
                                inv.setItem(21, percentage(40));
                            if(percentage>=60)
                                inv.setItem(22, percentage(60));
                            if(percentage>=80)
                                inv.setItem(23, percentage(80));
                            if(percentage>=100)
                                inv.setItem(24, percentage(100));

                            inv.setItem(13, activeTaskButton(task));
                            inv.setItem(31, cancelButton());
                         }
                    }
                    else{

                        inv.setItem(20, border());
                        inv.setItem(21, border());
                        inv.setItem(23, border());
                        inv.setItem(24, border());

                        inv.setItem(22, process_Button(processer));

                        inv.setItem(9, process_Button(null));
                        inv.setItem(18, process_Button(null));
                        inv.setItem(27, process_Button(null));

                        inv.setItem(17, process_Button(null));
                        inv.setItem(26, process_Button(null));
                        inv.setItem(35, process_Button(null));

                        inv.setItem(13, borderBlack());
                        inv.setItem(31, borderBlack());
                        inv.setItem(4, painting());
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin,0L,20L);
    }
}
