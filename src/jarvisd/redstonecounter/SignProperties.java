package jarvisd.redstonecounter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

/*
 * How this works......
 * each hashmap contains a hashmap which contains all the data of a single Sign (or anything really)
 * to get the hash map from the main collection, feed it the key "XYZ" block coordinates.
 */
public class SignProperties {
	public Map<String, Map<String, String>> signsProp = new HashMap<String,Map<String,String>>();

	private File propFile;
	
	public SignProperties(JavaPlugin plugin, String file) {
		propFile = new File(plugin.getDataFolder(), file);
		if (!plugin.getDataFolder().exists()) { plugin.getDataFolder().mkdir(); }
		if (!propFile.exists()) { 
			try {
				propFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public Map<String, String> getMap(String key) {
		if (signsProp.containsKey(key)) {
			return signsProp.get(key);
		}
		return null;
	}
	public void addMap(String key, Map<String, String> map) {
		if (!signsProp.containsKey(key)) {
			signsProp.put(key, map);
		}
	}
	
	public String getString(String key, String keyV) {
		if (signsProp.containsKey(key)) {
			Map<String, String> map = signsProp.get(key);
			if (map.containsKey(keyV)){
				return map.get(keyV);
			}
		}
		return null;
	}
	
	public void addString(String key, String keyV, String value) {
		if (signsProp.containsKey(key)) {
			Map<String, String> map = signsProp.get(key);
			map.put(keyV, value);
		}
	}
	
	public void remove(String key) {
		if (signsProp.containsKey(key)) {
			signsProp.remove(key);
		}
	}
	
	/*
	 * Parses Sign data from a file....
	 * 
	 */
	public void Parse() {
		try {
			 BufferedReader inputStream = new BufferedReader(new FileReader(propFile));
			 String line;
			while ((line = inputStream.readLine()) != null) {
				Map<String,String> map = new HashMap<String,String>();
				String[] Tokens = line.split(" ");
				String key = null;
				for (int i = 0; i < Tokens.length; i++) {
					String[] T = Tokens[i].split("=");
					map.put(T[0], T[1]);
					if (T[0].equalsIgnoreCase("X")) { key += T[1]; }
					else if (T[0].equalsIgnoreCase("Y")) { key += T[1]; }
					else if (T[0].equalsIgnoreCase("Z")) { key += T[1]; }
				}
				signsProp.put(key, map);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Save() {
		 try {
			BufferedWriter writerStream = new BufferedWriter(new FileWriter(propFile));
			StringBuilder strBuilder = new StringBuilder();
			for (String key : signsProp.keySet())  {
				Map<String, String> map = signsProp.get(key);
				for (String k : map.keySet()) {
					strBuilder.append(" ");
					strBuilder.append(k);
					strBuilder.append("=");
					strBuilder.append(map.get(k));

				}
				strBuilder.append("\n");
			}
			writerStream.write(strBuilder.toString());
			writerStream.flush();
		 } catch (FileNotFoundException e) {
				e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//System.out.println("RedstoneCounter Saved");
		}
	}
	
}
