package cc.harmo.scmpp.mixin;

import cc.harmo.scmpp.Scmpp;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At(value = "HEAD"), method = "getMapState(Ljava/lang/String;)Lnet/minecraft/item/map/MapState;", cancellable = true)
    private void onGetMapState(String id, CallbackInfoReturnable<MapState> cir) {
        if (Objects.equals(id, FilledMapItem.getMapName(Scmpp.MAP_ID))) cir.setReturnValue(null);
    }

    @Inject(at = @At(value = "HEAD"), method = "putMapState(Ljava/lang/String;Lnet/minecraft/item/map/MapState;)V", cancellable = true)
    private void onPutMapState(String id, MapState state, CallbackInfo ci) {
        if (Objects.equals(id, FilledMapItem.getMapName(Scmpp.MAP_ID))) ci.cancel();
    }

}
