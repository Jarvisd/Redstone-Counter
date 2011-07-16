package jarvisd.redstonecounter;

import jarvisd.redstonecounter.Block.StopWatchSign;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class RCBlockListener extends BlockListener {
	public RedstoneCounter plugin;

	public RCBlockListener(RedstoneCounter instance)	{
		plugin = instance;
	}

	public void	onSignChange(SignChangeEvent event) {
		Block b = event.getBlock();
		String[] lines = event.getLines();	
		 if (lines[0].trim().equalsIgnoreCase(RedstoneCounter.TimerLiteral)) {
			 boolean bExists = false;
			 for (int i = 0; i < plugin.stopWatchSigns.size(); i++) {
				 if (plugin.stopWatchSigns.get(i).getBlock().equals(b)) {
					 bExists = true;
					 break;
				 }
			 }
			 if (!bExists) {
					 StopWatchSign sws = new StopWatchSign(plugin, b);
					 Map<String, String> map = new HashMap<String, String>();
					 String key = b.getX() +""+ b.getY() +""+ b.getZ();
					 plugin.signProp.addMap(key, map);
					 sws.systemTicks = System.currentTimeMillis();
					 plugin.signProp.addString(key,"world",b.getWorld().getName());
					 plugin.signProp.addString(key, "sysTicks", String.valueOf(sws.systemTicks));
					 plugin.signProp.addString(key, "sTicks", String.valueOf(sws.signTicks));
					 plugin.signProp.addString(key, "powered", "false");
					 plugin.signProp.addString(key, "X", String.valueOf(b.getX()));
					 plugin.signProp.addString(key, "Y", String.valueOf(b.getY()));
					 plugin.signProp.addString(key, "Z", String.valueOf(b.getZ()));
					 plugin.stopWatchSigns.add(sws);
			 }
		 }
	}
	
	
	//DEBUG
	public void	onBlockDamage(BlockDamageEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			Sign sign = (Sign)b.getState();
		String[] lines = sign.getLines();	
		 if (lines[0].trim().equalsIgnoreCase(RedstoneCounter.TimerLiteral)) {
			 boolean bExists = false;
			 for (int i = 0; i < plugin.stopWatchSigns.size(); i++) {
				 if (plugin.stopWatchSigns.get(i).getBlock().equals(b)) {
					 bExists = true;
					 break;
				 }
			 }
			 if (!bExists) {
					 StopWatchSign sws = new StopWatchSign(plugin, b);
					 Map<String, String> map = new HashMap<String, String>();
					 String key = b.getX() +""+ b.getY() +""+ b.getZ();
					 plugin.signProp.addMap(key, map);
					 sws.systemTicks = System.currentTimeMillis();
					 plugin.signProp.addString(key,"world",b.getWorld().getName());
					 plugin.signProp.addString(key, "sysTicks", String.valueOf(sws.systemTicks));
					 plugin.signProp.addString(key, "sTicks", String.valueOf(sws.signTicks));
					 plugin.signProp.addString(key, "powered", "false");
					 plugin.signProp.addString(key, "X", String.valueOf(b.getX()));
					 plugin.signProp.addString(key, "Y", String.valueOf(b.getY()));
					 plugin.signProp.addString(key, "Z", String.valueOf(b.getZ()));
					 
					 plugin.stopWatchSigns.add(sws);
			 }
		 }
		}
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			Sign sign = (Sign)b.getState();
			String[] lines = sign.getLines();	
			
			 if (lines[0].trim().equalsIgnoreCase(RedstoneCounter.TimerLiteral)) {
				 String key = b.getX() +""+ b.getY() +""+ b.getZ();
				 for (int i = 0; i < plugin.stopWatchSigns.size(); i++) {
					 if (plugin.stopWatchSigns.get(i).getBlock().equals(b)) {
						 plugin.signProp.remove(key);
						 plugin.signProp.Save();
						 plugin.stopWatchSigns.remove(i);
						 break;
					 }
				 }
			 }
		}
	}
	
	public void	onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) { return; }
		World world = block.getWorld();
		int X = block.getX();
		int Z = block.getZ();
		int Y = block.getY();
		//lets look for a sign.
		Block b = null;
		for (int i = 0; i < 4; i++) {
			if (i == 0) { b = world.getBlockAt(X-1,Y,Z); }
			else if (i == 1) { b = world.getBlockAt(X+1,Y,Z); }
			else if (i == 2) { b = world.getBlockAt(X,Y,Z-1); }
			else if (i == 3) { b = world.getBlockAt(X,Y,Z+1); }
			if (b != null) {
				if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
					Sign sign = (Sign)b.getState();
					String[] lines = sign.getLines();	
					
					 if (lines[0].trim().equalsIgnoreCase(RedstoneCounter.CounterLiteral)) {
						if (event.getOldCurrent() == 0) {	 
							SignCounter(sign);
						}
					 }
					 else if (lines[0].trim().equalsIgnoreCase(RedstoneCounter.TimerLiteral)) {
						 for (StopWatchSign sws : plugin.stopWatchSigns) {
							 if (sws.getBlock().equals(b)) {
								 sws.Powered(event.getOldCurrent() == 0 ? true : false, true);
								 String key = b.getX() +""+ b.getY() +""+ b.getZ();
								 plugin.signProp.addString(key, "powered", String.valueOf(sws.powered));
								 break;
							 }
						 }
					 }
					 
				}
			}
		}
		
	}		
	
	private void SignCounter(Sign sign) {
		String[] lines = sign.getLines();	
		int count = 0;
		int maxAmount = Integer.MAX_VALUE;
		int rollOver = 0;
		if (!lines[1].isEmpty() || lines[1] != null) { // get the maxAmount until rollOver.
				try {
					maxAmount = parseSignLine(lines[1]);
					sign.setLine(1, String.valueOf(formatSignLine(maxAmount)));
				} catch(ParseException e) {
					//System.out.println("Failed to parse sign, invalid max number");
					maxAmount = Integer.MAX_VALUE;
				}
		 }
		
		if (!lines[2].isEmpty() || lines[2] != null) { // get the current count.
		try {
			count = parseSignLine(lines[2]);
		} catch(ParseException e) {
			//System.out.println("Failed to parse sign, invalid number");
				count = 0;
			}
		 }
		if (count < maxAmount) {	
			sign.setLine(2,String.valueOf(formatSignLine(++count))); 
		}
		else {
			count = 0;
			if (!lines[3].isEmpty() || lines[3] != null) { // get the current roll over count.
		try {
			rollOver = parseSignLine(lines[3]);
		} catch(ParseException e) {
			//System.out.println("Failed to parse sign, invalid roll over number");
					rollOver = 0;
				}
			 }
			sign.setLine(2,String.valueOf(formatSignLine(count))); 
			sign.setLine(3, String.valueOf(formatSignLine(++rollOver)));
		}
			
		
			plugin.signsToUpdate.add(sign);

	
		}
	
	
		private String formatSignLine(int amount) {
			NumberFormat format = NumberFormat.getIntegerInstance();
			return format.format(amount);
		}
		private int parseSignLine(String amount) throws ParseException {
			NumberFormat format = NumberFormat.getIntegerInstance();
			return format.parse(amount).intValue();
		}
		
	}	

	

