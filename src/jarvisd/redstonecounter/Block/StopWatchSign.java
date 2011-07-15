package jarvisd.redstonecounter.Block;

import jarvisd.redstonecounter.RedstoneCounter;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class StopWatchSign  {
	private RedstoneCounter plugin;
	private Block block = null;
	public int systemTicks = 0;
	public int signTicks = 0;
	public boolean powered = false;
	
	public StopWatchSign(JavaPlugin plugin, Block block) {
		this.plugin = (RedstoneCounter) plugin;
		this.block = block;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void update() {
		if (block.isBlockIndirectlyPowered() || block.isBlockPowered()) {
			if (block.getState() instanceof Sign) {
				Sign sign = (Sign)block.getState();
				int currentTicks = (int)(System.currentTimeMillis()/1000);
				signTicks += systemTicks - currentTicks;
				systemTicks = currentTicks;
				String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(signTicks));
				sign.setLine(3, time);
				plugin.signsToUpdate.add(sign);
			}
		}		
	}
	

}
