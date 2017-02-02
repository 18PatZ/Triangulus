package com.patrickzhong.triangulus;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NMS {
	

	static String CBPATH;
	static String NMSPATH;
	
	/*static Class worldParts;
	static Class enumPart;
	static Constructor worldPartsCons;*/
	
	static Class worldPartC;
	static Class enumPartC;
	static Constructor worldPartConst;
	static Object enumParticle;
	static Class packetC;
	static Class craftPlayerC;
	static Class playerConnC;
	static Class entPlayerC;
	static Field conn;
	static Method getHandleM;
	static Method sendPacketM;
	
	@SuppressWarnings("unchecked")
	public static void init(Main main){

		String packageName = main.getServer().getClass().getPackage().getName();
		String version = packageName.substring(packageName.indexOf(".v")+2);
		CBPATH = "org.bukkit.craftbukkit.v"+version+".";
		NMSPATH = "net.minecraft.server.v"+version+".";
		
		try {
			/*worldParts = Class.forName(NMSPATH+"PacketPlayOutWorldParticles");
			enumPart = Class.forName(NMSPATH+"EnumParticle");
			worldPartsCons = worldParts.getConstructor(parameterTypes)*/
					
			worldPartC = Class.forName(NMSPATH + "PacketPlayOutWorldParticles");
			enumPartC = Class.forName(NMSPATH + "EnumParticle");
			worldPartConst = worldPartC.getConstructor(enumPartC, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
			enumParticle = enumPartC.getEnumConstants()[30]; // Redstone is index 30

			packetC = Class.forName(NMSPATH + "Packet");
			

			craftPlayerC = Class.forName(CBPATH + "entity.CraftPlayer");

			playerConnC = Class.forName(NMSPATH + "PlayerConnection");
			entPlayerC = Class.forName(NMSPATH + "EntityPlayer");

			conn = entPlayerC.getField("playerConnection");
			getHandleM = craftPlayerC.getMethod("getHandle");
			sendPacketM = playerConnC.getMethod("sendPacket", packetC);
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void spawnParticle(Location loc, Color color){
		float x = (float)loc.getX();
		float y = (float)loc.getY();
		float z = (float)loc.getZ();
		float r = (float)(color.getRed()/255.0);
		float g = (float)(color.getGreen()/255.0);
		float b = (float)(color.getBlue()/255.0);
		
		//PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y, z, r,g,b, 1f, 0);
		//((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		
		try {
			Object packet = packetC.cast(worldPartConst.newInstance(
					enumPartC.cast(enumParticle), true, x, y, z, r,g,b, 1f, 0, new int[0]));
			for(Player player : loc.getWorld().getPlayers())
				sendPacketM.invoke(conn.get(getHandleM.invoke(craftPlayerC.cast(player))), packet);
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
