package de.notjan.main.model;

import java.util.UUID;

public class ProcessTask {

    private final long timestampStarted;
    private final long timestampStop;
    private final String processerID;
    private final UUID uuid;

    public ProcessTask(Processer processer, UUID playerUUID){
        this.processerID = processer.getProcesserID();
        this.uuid = playerUUID;
        this.timestampStarted = System.currentTimeMillis();
        this.timestampStop = timestampStarted+(1000*processer.getDuration());
    }

    public ProcessTask(String processerID,UUID uuid, long started, long stopped){
        this.processerID = processerID;
        this.uuid = uuid;
        this.timestampStarted = started;
        this.timestampStop = stopped;
    }

    public long getTimestampStarted() {
        return timestampStarted;
    }
    public long getTimestampStop() {
        return timestampStop;
    }
    public String getProcesserID() {
        return processerID;
    }
    public UUID getUuid() {
        return uuid;
    }
    public boolean isFinished(){
        return (System.currentTimeMillis() >= timestampStop);
    }
    public float getTimeDifference(){
        if(isFinished())
            return 0;
        return Math.abs((System.currentTimeMillis()-getTimestampStop())/1000);
    }
    public float getDuration(){
        return (getTimestampStop()-getTimestampStarted())/1000;
    }
    public int getPercentage(){
        if(isFinished())
            return 100;
        return (int) ((1-(getTimeDifference()/getDuration()))*100);
    }

    @Override
    public String toString() {
        return (processerID+", "+uuid.toString()+", "+timestampStarted+", "+timestampStop);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof ProcessTask)){return false;}
        ProcessTask other = (ProcessTask) obj;
        if(this.processerID.equals(other.processerID) && this.uuid.equals(other.uuid)){
            return true;
        }
        return false;
    }
}
