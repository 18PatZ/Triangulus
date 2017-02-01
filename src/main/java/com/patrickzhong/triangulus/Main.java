package com.patrickzhong.triangulus;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
	
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent ev){
		String disp = disp(ev.getItem());
		if(disp.contains("Triangulus")){
			ev.setCancelled(true);
			boolean outline = disp.length() > 16; // Triangulus: Bord|er
			if(ev.getAction() == Action.LEFT_CLICK_BLOCK){
				// Add vertex
				ev.getPlayer().sendMessage(getLookingAt(ev.getPlayer(), ev.getClickedBlock()).toVector().toString());
			}
			else if (ev.getAction() == Action.RIGHT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_AIR){
				// Change mode
				setDisp(ev.getItem(), "&b&lTriangulus: "+(outline ? "Fill" : "Border"));
				ev.getPlayer().updateInventory();
			}
		}
	}
	
	private Location getLookingAt(Player player, Block block){
		Location pLoc = player.getEyeLocation();
		Vector dir = pLoc.getDirection();
		
		for(double i = 0; i < 10; i += 0.1){ // Interate over the player direction vector
			double x = dir.getX() * i + pLoc.getX(); // Current x position
			double y = dir.getY() * i + pLoc.getY(); // Current y position
			double z = dir.getZ() * i + pLoc.getZ(); // Current z position
			Block b = player.getWorld().getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
			if(b.equals(block))
				return new Location(player.getWorld(), x, y, z);
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
