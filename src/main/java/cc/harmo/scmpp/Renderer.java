package cc.harmo.scmpp;

import cc.harmo.scmpp.util.GridPos;
import cc.harmo.scmpp.util.Util;
import net.minecraft.block.MapColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

import java.util.Arrays;

public class Renderer {
    public static final byte BACKGROUND_COLOR = (byte) (MapColor.GRAY.id * 2 + MapColor.Brightness.NORMAL.id);
    public static final byte PLAYER_GRID_COLOR = (byte) (MapColor.BRIGHT_RED.id * 4 + MapColor.Brightness.NORMAL.id);
    public static final byte PLAYER_SLIME_GRID_COLOR = (byte) (MapColor.YELLOW.id * 4 + MapColor.Brightness.NORMAL.id);
    public static final byte SLIME_CHUNK_COLOR = (byte) (MapColor.LIME.id * 4 + MapColor.Brightness.NORMAL.id);
    public static final byte NORMAL_CHUNK_COLOR_1 = (byte) (MapColor.LIGHT_GRAY.id * 4 + MapColor.Brightness.NORMAL.id);
    public static final byte NORMAL_CHUNK_COLOR_2 = (byte) (MapColor.WHITE.id * 4 + MapColor.Brightness.NORMAL.id);

    public static byte[] generatePattern(PlayerEntity player, ServerWorld world) {
        byte[] pattern = new byte[16384];
        Arrays.fill(pattern, BACKGROUND_COLOR);
        if (!Util.hasSlimeSpawnEntry(world, player.getBlockPos())) return pattern;
        GridPos playerGridPos = Util.blockPos2GridPos(player.getBlockPos());
        final int xBias = 1;
        final int yBias = 1;
        final int width = 2;
        final int height = 2;
        for (int i = -31; i <= 31; ++i) {
            for (int j = -31; j <= 31; ++j) {
                GridPos grid = new GridPos(playerGridPos.x() + i, playerGridPos.z() + j);
                ChunkPos chunk = Util.gridPos2ChunkPos(grid);
                byte color;
                if (Util.isSlimeChunk(world, chunk.x, chunk.z)) color = SLIME_CHUNK_COLOR;
                else if ((chunk.x + chunk.z) % 2 != 0) color = NORMAL_CHUNK_COLOR_1;
                else color = NORMAL_CHUNK_COLOR_2;
                if (i == 0 && j == 0) {
                    if (color == SLIME_CHUNK_COLOR) color = PLAYER_SLIME_GRID_COLOR;
                    else color = PLAYER_GRID_COLOR;
                }
                paintRectangle(pattern, color, xBias + (i + 31) * width, yBias + (j + 31) * height, width, height);
            }
        }
        return pattern;
    }

    private static void paintRectangle(byte[] pattern, byte color, int x, int y, int width, int height) {
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                pattern[pos2index(x + i, y + j)] = color;
    }

    private static int pos2index(int x, int y) {
        return x + y * 128;
    }

}
