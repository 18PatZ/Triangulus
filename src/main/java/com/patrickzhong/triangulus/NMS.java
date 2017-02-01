package com.patrickzhong.triangulus;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;

public class NMS {
	
	public static void spawnParticle(Location loc, Color color){
		float x = (float)loc.getX();
		float y = (float)loc.getY();
		float z = (float)loc.getZ();
		float r = (float)(color.getRed()/255.0);
		float g = (float)(color.getGreen()/255.0);
		float b = (float)(color.getBlue()/255.0);
		
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y, z, r,g,b, 1f, 0);
		for(Player player : loc.getWorld().getPlayers())
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

}
