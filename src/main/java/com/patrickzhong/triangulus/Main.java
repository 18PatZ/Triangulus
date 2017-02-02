package com.patrickzhong.triangulus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
	
	HashMap<Player, Triangle> triangles = new HashMap<Player, Triangle>();
	HashMap<Player, List<BukkitTask>> tasks = new HashMap<Player, List<BukkitTask>>();
	
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(!(sender instanceof Player)){
			sender.sendMessage("You must be a player.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("triclear")){
			
			List<BukkitTask> ts = tasks.get(player);
			if(ts != null)
				for(int i = ts.size()-1; i >= 0; i--)
					ts.remove(i).cancel();
			
			player.sendMessage(ChatColor.GRAY+"Triangles cleared.");
		}
		else if(cmd.getName().equalsIgnoreCase("triangulus")){
			
			ItemStack item = new ItemStack(Material.REDSTONE_COMPARATOR);
			setDisp(item, "&b&lTriangulus: Border");
			player.getInventory().addItem(item);
			player.updateInventory();
			player.sendMessage(ChatColor.GRAY+"Left-click to select vertices, right-click to change modes.");
			
		}
		
		return true;
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
					List<BukkitTask> ts = tasks.get(player);
					if(ts == null){
						ts = new ArrayList<BukkitTask>();
						tasks.put(player, ts);
					}
					ts.add(current.draw(outline));
					
					player.sendMessage(ChatColor.GRAY+"Triangulus created.");
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
