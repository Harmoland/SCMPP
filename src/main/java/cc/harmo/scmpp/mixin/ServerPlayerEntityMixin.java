package cc.harmo.scmpp.mixin;

import cc.harmo.scmpp.LocationInfo;
import cc.harmo.scmpp.ScmppTracked;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ScmppTracked {
    @Unique
    private final LocationInfo scmppLocation = new LocationInfo();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Unique
    @Override
    public boolean scmppCheckMove() {
        return LocationInfo.checkAndUpdate((ServerPlayerEntity) (Object) this, scmppLocation);
    }

}
