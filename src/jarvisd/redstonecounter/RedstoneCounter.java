package jarvisd.redstonecounter;

import jarvisd.redstonecounter.Block.StopWatchSign;

import java.util.ArrayList;
import java.util.Iterator;
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
        signProp.Save();
    }
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" ); 
        
        signProp = new SignProperties(this, "RC_Signs.conf");
        
        RCBlockListener rcListener = new RCBlockListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.REDSTONE_CHANGE,rcListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,rcListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE,rcListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE,rcListener, Priority.Lowest, this);
        initStopwatchSigns();
        
		signUpdaterTask = new Runnable() {
			long time = System.currentTimeMillis();
			@Override
	        public void run() {
				synchronized (signsToUpdate) {
					for (int i = 0; i < signsToUpdate.size(); i++) {
						signsToUpdate.get(i).update();
						signsToUpdate.remove(i);
					}
				}
				synchronized (stopWatchSigns) {
					for (StopWatchSign sws : stopWatchSigns) {
						sws.update();
					}
				}
				long t = System.currentTimeMillis() - time;
				if (t >= 300000) { 
					signProp.Save(); 
					System.out.println(";o");
					time = System.currentTimeMillis();
				}
			}
		};
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, signUpdaterTask, 5, 5);
		
		
    }
    
    private void initStopwatchSigns() {
    	signProp.Parse();
    	StopWatchSign sws = null;
    	synchronized (signProp.signsProp) {
    	for (Iterator<String> it = signProp.signsProp.keySet().iterator(); it.hasNext();)  {
    		String key = it.next();
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
    			it.remove();
    			continue;
    		} finally {
    			if (b != null) {
    				
					if (b.getState() instanceof Sign) {
						if (((Sign)b.getState()).getLines()[0].equalsIgnoreCase(TimerLiteral)) {
							sws = new StopWatchSign(this, b);
							try {
								sws.systemTicks = Integer.parseInt(map.get("sysTicks"));
							} catch (NumberFormatException e) { 
								sws.systemTicks = 0;
				    		}
							try {
								sws.signTicks = Integer.parseInt(map.get("sTicks"));
							} catch (NumberFormatException e) { 
								sws.signTicks = 0;
				    		}
							sws.Powered(Boolean.parseBoolean(map.get("powered")), false);
							//sws.powered = Boolean.parseBoolean(map.get("powered"));
							stopWatchSigns.add(sws);
						}
						else { 
							it.remove();
							continue;
						}
					}
					else { 
						it.remove();
						continue;
					}
    			}
    			else { 
    				it.remove();
    				continue;
    			}
    		}
    	}
    	signProp.Save();
    	}
    }
    
}