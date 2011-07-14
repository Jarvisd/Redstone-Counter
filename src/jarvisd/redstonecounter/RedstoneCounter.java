package jarvisd.redstonecounter;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RedstoneCounter extends JavaPlugin	{
    public void onDisable() {
        System.out.println("RedStone Counter Disabled");
    }
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" ); 
        
        RCBlockRedstoneListener rcListener = new RCBlockRedstoneListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE,rcListener, Priority.Lowest, this);
    }
}