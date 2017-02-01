package com.patrickzhong.triangulus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Triangle {
	
	List<Location> vertices = new ArrayList<Location>();
	int numVerts = 3;
	
	public boolean addVertex(Location loc){
		vertices.add(loc);
		return vertices.size() == numVerts;
	}
	
	public void draw(){
		final List<Location> parts = new ArrayList<Location>();
		
		for(int i = 0; i < numVerts; i++){
			Location l1 = vertices.get(i); // Current vertex
			Vector v1 = l1.toVector();
			Vector v2 = vertices.get((i + 1) % numVerts).toVector(); // Next vertex
			Vector vec = v2.subtract(v1); // Displacement vector
			double length = vec.length();
			vec = vec.normalize(); // Unit vector
			for(double j = 0; j <= length; j += 0.1)
				parts.add(vec.clone().multiply(j).add(v1).toLocation(l1.getWorld())); // Add particles in between
		}
		
		new BukkitRunnable(){
			public void run(){
				for(Location loc : parts)
					NMS.spawnParticle(loc, Color.YELLOW);
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}

}
