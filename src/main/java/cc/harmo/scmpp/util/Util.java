package cc.harmo.scmpp.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.random.ChunkRandom;

public class Util {

    public static GridPos blockPos2GridPos(BlockPos blockPos) {
        return new GridPos(blockPos.getX() >> 2, blockPos.getZ() >> 2);
    }

    public static ChunkPos gridPos2ChunkPos(GridPos gridPos) {
        return new ChunkPos(gridPos.x() >> 2, gridPos.z() >> 2);
    }

    public static boolean isSlimeChunk(StructureWorldAccess world, int chunkX, int chunkZ) {
        return ChunkRandom.getSlimeRandom(chunkX, chunkZ, world.getSeed(), 987234911L).nextInt(10) == 0;
    }

    public static boolean hasSlimeSpawnEntry(ServerWorld world, BlockPos pos) {
        return world.getBiome(pos).value().getSpawnSettings().getSpawnEntries(SpawnGroup.MONSTER).getEntries().
                stream().anyMatch(it -> it.type == EntityType.SLIME);
    }

}
