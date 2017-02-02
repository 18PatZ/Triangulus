package com.patrickzhong.triangulus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	
	public void draw(boolean outline){
		final List<Location> parts = new ArrayList<Location>();
		
		if(outline){
			for(int i = 0; i < numVerts; i++){
				Location l1 = vertices.get(i); // Current vertex
				Location l2 = vertices.get((i + 1) % numVerts); // Next vertex
				
				addParts(l1, l2, parts);
			}
		}
		else {
			// WARNING: Works only for triangles at the moment.
			
			Location l1 = vertices.get(0);
			Location l2 = vertices.get(1);
			Location l3 = vertices.get(2);
			
			Vector v1 = l2.clone().subtract(l1).toVector();
			Vector v2 = l2.clone().subtract(l3).toVector();
			
			double len1 = v1.length();
			double len2 = v2.length();
			
			double step1 = 0.25; // Step size on side 1
			double step2 = len2 / len1 * step1; // Step size on side 2
			
			v1 = v1.normalize();
			v2 = v2.normalize();
			
			for(double i = 0; i < len1/step1; i ++){
				Location start = l1.clone().add(v1.clone().multiply(i * step1));
				Location end = l3.clone().add(v2.clone().multiply(i * step2));
				addParts(start, end, parts); // Add particles between two sides
			}
		}
		
		// 50 random bright colors
		final Color[] colors = new Color[50];
		Random rand = new Random();
		for(int i = 0; i < 50; i++)
			colors[i] = Color.getHSBColor(rand.nextFloat(), rand.nextFloat(), rand.nextFloat() * 0.2f + 0.8f);
		
		new BukkitRunnable(){
			
			/*Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, 
					Color.BLACK, Color.GRAY, Color.WHITE, Color.PINK};*/
			int counter = 0;
			Color color = colors[0];
			
			Color target;
			
			public void run(){
				
				int period = 40;
				if(counter % period == 0)
					target = colors[counter/period % colors.length]; // Get next color
				
				int dr = target.getRed() - color.getRed();
				int dg = target.getGreen() - color.getGreen();
				int db = target.getBlue() - color.getBlue();
				
				color = new Color(color.getRed()+dr/10, color.getGreen()+dg/10, color.getBlue()+db/10); // Get intermetdiate color
				
				for(Location loc : parts)
					NMS.spawnParticle(loc, color);
				
				counter++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
	
	private void addParts(Location start, Location end, List<Location> parts){
		Vector v1 = start.toVector();
		Vector vec = end.toVector().subtract(v1); // Displacement vector
		double length = vec.length();
		vec = vec.normalize(); // Unit vector
		for(double j = 0; j <= length; j += 0.25)
			parts.add(vec.clone().multiply(j).add(v1).toLocation(start.getWorld())); // Add particles in between
	}

}
