package jarvisd.redstonecounter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RCBlockRedstoneListener extends BlockListener {
	public static RedstoneCounter plugin;
	public List<Sign> signsToUpdate = new ArrayList<Sign>();

	public RCBlockRedstoneListener(RedstoneCounter instance)	{
		plugin = instance;
	}

	public void	onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) { return; }
		if (event.getOldCurrent() == 0) {	
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
						 if (lines[0].equalsIgnoreCase("~Counter~")) {
							 SignCounter(sign);
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
		} catch(ParseException e) {
			//System.out.println("Failed to parse sign, invalid max number");
				maxAmount = Integer.MAX_VALUE;
				sign.setLine(1, String.valueOf(formatSignLine(maxAmount)));
			}
		 }
		 else { sign.setLine(1, String.valueOf(formatSignLine(maxAmount))); }
		
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
			
		
			signsToUpdate.add(sign);
			Runnable signUpdaterTask = new Runnable() {
				@Override
		        public void run() {
					for (int i = 0; i < signsToUpdate.size(); i++) {
						signsToUpdate.get(i).update();
						signsToUpdate.remove(i);
					}
				}
				
			};
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, signUpdaterTask);
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

	

