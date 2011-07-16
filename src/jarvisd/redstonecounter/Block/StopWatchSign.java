package jarvisd.redstonecounter.Block;

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
	public void Powered(boolean on) {
		powered = on;
		if (on) {
			systemTicks = System.currentTimeMillis();
		}
		
	}
	public void update() {
		if (powered) {
			if (block.getState() instanceof Sign) {
				Sign sign = (Sign)block.getState();
				long currentTicks = System.currentTimeMillis();
				signTicks = (currentTicks - systemTicks) - 68400000; //subtract 68400000 to rid of 19 hours?
				String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(signTicks));
				sign.setLine(3, time);
				plugin.signsToUpdate.add(sign);
				String key = block.getX() +""+ block.getY() +""+ block.getZ();
				
				plugin.signProp.addString(key, "sTicks", String.valueOf(signTicks));
			}
		}		
	}
	
}
