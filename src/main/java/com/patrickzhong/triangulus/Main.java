package com.patrickzhong.triangulus;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
	
	HashMap<Player, Triangle> triangles = new HashMap<Player, Triangle>();
	
	static Main instance;
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		
		instance = this;
	}
	
	public static Main getInstance(){
		return instance;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent ev){
		triangles.remove(ev.getPlayer());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent ev){
		String disp = disp(ev.getItem());
		if(disp.contains("Triangulus")){
			ev.setCancelled(true);
			boolean outline = disp.length() > 16; // Triangulus: Bord|er
			if(ev.getAction() == Action.LEFT_CLICK_BLOCK){
				// Add vertex
				Player player = ev.getPlayer();
				Location loc = getLookingAt(player, ev.getClickedBlock());
				
				Triangle current = triangles.get(player);
				if(current == null){
					current = new Triangle();
					triangles.put(player, current);
				}
				
				if(current.addVertex(loc)){ // Shape completed
					player.sendMessage(ChatColor.GRAY+"Triangulus created.");
					current.draw(outline);
					triangles.remove(player);
				}
				else
					player.sendMessage(ChatColor.GRAY+"Added new vertex.");
			}
			else if (ev.getAction() == Action.RIGHT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_AIR){
				// Change mode
				setDisp(ev.getItem(), "&b&lTriangulus: "+(outline ? "Fill" : "Border"));
				triangles.remove(ev.getPlayer());
				ev.getPlayer().updateInventory();
			}
		}
	}
	
	private Location getLookingAt(Player player, Block block){
		Location pLoc = player.getEyeLocation();
		Vector dir = pLoc.getDirection();
		
		double dx = dir.getX();
		double dy = dir.getY();
		double dz = dir.getZ();
		
		double step = 0.1;
		for(double i = 0; i < 10; i += step){ // Interate over the player direction vector
			double x = dx * i + pLoc.getX(); // Current x position
			double y = dy * i + pLoc.getY(); // Current y position
			double z = dz * i + pLoc.getZ(); // Current z position
			Block b = player.getWorld().getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
			if(b.equals(block))
				return new Location(player.getWorld(), x - dx*step, y - dy*step, z - dz*step);
		}
		
		return block.getLocation();
	}
	
	private void setDisp(ItemStack item, String disp){
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', disp));
		item.setItemMeta(meta);
	}
	
	private String disp(ItemStack item){
		try {
			return ChatColor.stripColor(item.getItemMeta().getDisplayName());
		}
		catch (NullPointerException e){
			return "";
		}
	}

}
