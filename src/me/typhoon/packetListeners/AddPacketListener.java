package me.typhoon.packetListeners;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public class AddPacketListener {
	
	/*
	 * 이 클래스는 무시하세요
	 */
	
	private Player player;
	private Channel channel;
	
	public AddPacketListener(Player player) {
		this.player = player;
	}
	
	public boolean a(ChannelHandler handler, String pipeName) {
		if(channel.pipeline().get(pipeName) != null) {
			return false;
		}
		
		CraftPlayer cPlayer = (CraftPlayer)player;
		
		channel = cPlayer.getHandle().playerConnection.networkManager.channel;
		channel.pipeline().addAfter("custom_handler", pipeName, handler);
		
		return true;
	}
	
	public boolean d(String pipeName) {
		if(channel.pipeline().get(pipeName) != null) {
			channel.pipeline().remove(pipeName);
			return true;
		}
		
		return false;
	}
}
