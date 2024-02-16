package de.notjan.main.utils;

public class SecondsConverter {
    public static String convertSeconds(long seconds){
        if(seconds >=3600)
            return String.format("%02dh:%02dm:%02ds", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        else
            return String.format("%02dm:%02ds", (seconds % 3600) / 60, seconds % 60);
    }
}