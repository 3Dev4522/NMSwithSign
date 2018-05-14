package me.typhoon;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayInUpdateSign;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenSignEditor;



public class PluginMain extends JavaPlugin implements Listener{
	
	private String recvName;
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		
	}
	
	public void create(Player player) {
		ChannelDuplexHandler channel = new ChannelDuplexHandler() {
			
			@Override
			public void channelRead(ChannelHandlerContext context, Object packet) throws Exception{
				if(packet instanceof PacketPlayInUpdateSign) {
					PacketPlayInUpdateSign sign = (PacketPlayInUpdateSign)packet;
			
					if(!(sign.b()[0].equals(""))) {
						getLogger().info(sign.b()[0]);
						getLogger().info(player.getName());
						player.setDisplayName(sign.b()[0]);
					}
				}
				
				super.channelRead(context, packet);
			}
		};
		
		ChannelPipeline pipe = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipe.addBefore("packet_handler", player.getName(), channel);
	}
	
	@EventHandler
	public void joinPlayer(PlayerJoinEvent event) {
		create(event.getPlayer());
	}
	
	@EventHandler
	public void openSign(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		BlockPosition bp = new BlockPosition(((CraftPlayer) player).getHandle());         
		PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(bp);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		
		player.setDisplayName(recvName);
	}
}
