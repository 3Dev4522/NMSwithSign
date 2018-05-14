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
	
	@EventHandler
	public void joinPlayer(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		ChannelDuplexHandler channel = new ChannelDuplexHandler() {
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if(msg instanceof PacketPlayInUpdateSign) {
					PacketPlayInUpdateSign packet = (PacketPlayInUpdateSign)ctx;
					recvName = packet.b()[0];
				}
				
				super.channelRead(ctx, msg);
			}
		};
		
		ChannelPipeline pipe = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipe.addAfter("packet_handler", player.getName(), channel);
		
	}
	
	@EventHandler
	public void openSign(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		player.sendMessage(recvName);
		
		BlockPosition bp = new BlockPosition(((CraftPlayer) player).getHandle());         
		PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(bp);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		
		player.setDisplayName(recvName);
	}
}
