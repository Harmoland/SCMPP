package cc.harmo.scmpp;

import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class Noticer {

    public static void noticePlayer(ServerPlayerEntity player, ServerWorld world) {
        if (((ScmppTracked) player).scmppCheckMove())
            sendMapPacket(player, Renderer.generatePattern(player, world));
    }

    private static void sendMapPacket(ServerPlayerEntity player, byte[] colors) {
        if (colors.length != 16384) return;
        player.networkHandler.sendPacket(new MapUpdateS2CPacket(Scmpp.MAP_ID, (byte) 0, true, null,
                new MapState.UpdateData(0, 0, 128, 128, colors)));
    }

}
