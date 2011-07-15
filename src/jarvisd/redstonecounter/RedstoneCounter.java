package jarvisd.redstonecounter;

import jarvisd.redstonecounter.Block.StopWatchSign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RedstoneCounter extends JavaPlugin	{
	public SignProperties signProp;
	public List<Sign> signsToUpdate = new ArrayList<Sign>();
	public List<StopWatchSign> stopWatchSigns = new ArrayList<StopWatchSign>();
	
	public static final String TimerLiteral = "~StopWatch~";
	public static final String CounterLiteral = "~Counter~";
	
	private Runnable signUpdaterTask;
	
    public void onDisable() {
        System.out.println("RedStone Counter Disabled");
        getServer().getScheduler().cancelTasks(this);
    }
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" ); 
        
        signProp = new SignProperties(this, "RC_Signs.conf");
        
        RCBlockRedstoneListener rcListener = new RCBlockRedstoneListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE,rcListener, Priority.Lowest, this);
        
        initStopwatchSigns();
        
		signUpdaterTask = new Runnable() {
			@Override
	        public void run() {
				for (int i = 0; i < signsToUpdate.size(); i++) {
					signsToUpdate.get(i).update();
					signsToUpdate.remove(i);
				}
				for (StopWatchSign sws : stopWatchSigns) {
					sws.update();
				}
			}
		};
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, signUpdaterTask, 20, 20);
		
		
    }
    
    private void initStopwatchSigns() {
    	signProp.Parse();
    	StopWatchSign sws;
    	for (String key : signProp.signsProp.keySet())  {
    		Map<String, String> map = signProp.signsProp.get(key);
    		Block b = null;
    		try {
    		 b = getServer().getWorld(
					map.get("world")).getBlockAt(
							Integer.parseInt(map.get("X")),
							Integer.parseInt(map.get("Y")),
							Integer.parseInt(map.get("Z"))
							); 
    		} catch (NumberFormatException e) { 
    			signProp.remove(key);
    		} finally {
				if (b.getState() instanceof Sign) {
					if (((Sign)b.getState()).getLines()[0].equalsIgnoreCase(TimerLiteral)) {
						sws = new StopWatchSign(this, b);
						int T = 0;
						try {
							T = Integer.parseInt(map.get("sysTicks"));
						} catch (NumberFormatException e) { 
			    			T = 0;
			    		}
						sws.signTicks = T;
						try {
							T = Integer.parseInt(map.get("sTicks"));
						} catch (NumberFormatException e) { 
			    			T = 0;
			    		}
						sws.signTicks = T;
					}
					else { 
						signProp.remove(key);
					}
				}
				else { 
					signProp.remove(key);
				}
    		}
    	}
    	signProp.Save();

    }
    
}