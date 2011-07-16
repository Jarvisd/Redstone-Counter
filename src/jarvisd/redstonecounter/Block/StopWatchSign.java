package jarvisd.redstonecounter.Block;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jarvisd.redstonecounter.RedstoneCounter;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class StopWatchSign  {
	private RedstoneCounter plugin;
	private Block block = null;
	public long systemTicks = 0;
	public long signTicks = 0;
	public boolean powered = false;
	
	public StopWatchSign(JavaPlugin plugin, Block block) {
		this.plugin = (RedstoneCounter) plugin;
		this.block = block;
	}
	
	public Block getBlock() {
		return block;
	}
	public void Powered(boolean on, boolean reset) {
		powered = on;
		if (on) {
			systemTicks = System.currentTimeMillis();
			if (reset) {
				signTicks = 0;
			}
		}

		
	}
	public void update() {
		if (powered) {
			if (block.getState() instanceof Sign) {
				Sign sign = (Sign)block.getState();
				long currentTicks = System.currentTimeMillis();
				signTicks += ((currentTicks - systemTicks) ); 
				systemTicks = currentTicks;
				GregorianCalendar gc = new GregorianCalendar(0,0,0,0,0,(int)(signTicks/ 1000));
				String d = "Days: "+gc.get(Calendar.DAY_OF_YEAR);
				String ti = gc.get(Calendar.HOUR_OF_DAY) +":"+gc.get(Calendar.MINUTE) +":"+gc.get(Calendar.SECOND);
				sign.setLine(2, d);
				sign.setLine(3, ti);
				plugin.signsToUpdate.add(sign);
				String key = block.getX() +""+ block.getY() +""+ block.getZ();
				
				plugin.signProp.addString(key, "sTicks", String.valueOf(signTicks));
			}
		}		
	}
	
}
