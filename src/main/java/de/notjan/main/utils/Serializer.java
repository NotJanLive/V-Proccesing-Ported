package de.notjan.main.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Serializer {

    public static String serializeLocation(Location loc){
        if(loc == null)
            return "none";

        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return (world+", "+x+", "+y+", "+z);
    }

    public static Location deserializeLocation(String locString){

        if(!locString.contains(", "))
            return null;
        if(locString.contains("none"))
            return null;

        String[] parts = locString.split(", ");
        World world = Bukkit.getWorld(parts[0]);
        if(world == null)
            throw new NullPointerException("The world "+parts[0]+" does not exist ["+locString+"]");
        try{
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            return new Location(world,x,y,z);
        }
        catch(NumberFormatException e){
            System.out.println(locString+" does not have the required format!");
            e.printStackTrace();
            return null;
        }
    }

}
