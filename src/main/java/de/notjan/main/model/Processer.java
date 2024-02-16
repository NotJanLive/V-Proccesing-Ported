package de.notjan.main.model;

import de.notjan.main.utils.SecondsConverter;
import de.notjan.main.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class Processer {

    private Location location;
    private List<ItemStack> requiredItems;
    private List<ItemStack> outputItems;
    private String name;
    private int duration;
    private final String processerID;
    private boolean useHologram;
    private boolean useLargeHologram;
    private ItemStack guiIcon;

    public Processer(String processerID,String name,int duration,Location loc, List<ItemStack> rI, List<ItemStack> oI, boolean useHologram, boolean useLargeHologram, ItemStack guiIcon){
        this.processerID = processerID;
        this.name = name;
        this.duration = duration;
        this.location = loc;
        this.requiredItems = rI;
        this.outputItems = oI;
        this.useHologram = useHologram;
        this.useLargeHologram = useLargeHologram;
        this.guiIcon = guiIcon;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getOutputItems() {return outputItems;}

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getProcesserID() {return processerID;}

    public String getDurationString(){
        return SecondsConverter.convertSeconds(getDuration());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(processerID+"[Name: "+name+", Duration: "+duration+", Location: "+ Serializer.serializeLocation(location)+"]\n");
        sb.append("Required Items: ");
        for(ItemStack i: requiredItems){
            sb.append("["+i.getItemMeta().getDisplayName()+", "+i.getType()+", "+i.getAmount()+"}, ");
        }
        sb.append("\nOutput Items: ");
        for(ItemStack i: outputItems){
            sb.append("["+i.getItemMeta().getDisplayName()+", "+i.getType()+", "+i.getAmount()+"}, ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof Processer)){return false;}
        Processer other = (Processer) obj;
        return other.processerID.equals(this.processerID);
    }

    public boolean isUseHologram() {
        return useHologram;
    }

    public boolean isUseLargeHologram() {
        return useLargeHologram;
    }

    public ItemStack getGuiIcon() {
        return guiIcon;
    }
}