package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet3Chat;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.network.NetServerHandler")
public class NetServerHandlerTransformer extends MiniTransformer {
	@Patch.Method("handleChat(Lnet/minecraft/network/packet/Packet3Chat;)V")
	public void patchHandleChat(PatchContext ctx) {
		ctx.search(
				GETSTATIC("net/minecraft/network/NetServerHandler", "logger", "Ljava/util/logging/Logger;")
		).jumpBefore();

		ctx.add(
				ALOAD(0),
				ALOAD(1),
				INVOKESTATIC("cc/unilock/nilcord/transformer/NetServerHandlerTransformer$Hooks", "chatMessage", "(Lnet/minecraft/network/NetServerHandler;Lnet/minecraft/network/packet/Packet3Chat;)V")
		);
	}

	public static class Hooks {
		public static void chatMessage(NetServerHandler netServerHandler, Packet3Chat packet) {
			NilcordPremain.listener.playerChatMessage(netServerHandler.playerEntity, packet.message);
		}
	}
}
