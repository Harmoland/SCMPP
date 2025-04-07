package icu.harmo.scmpp.mixin;

import icu.harmo.scmpp.Scmpp;
import net.minecraft.component.type.MapIdComponent;
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

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(at = @At(value = "HEAD"), method = "getMapState(Lnet/minecraft/component/type/MapIdComponent;)Lnet/minecraft/item/map/MapState;", cancellable = true)
    private void onGetMapState(MapIdComponent id, CallbackInfoReturnable<MapState> cir) {
        if (Objects.equals(id, Scmpp.MAP_ID)) cir.setReturnValue(null);
    }

    @Inject(at = @At(value = "HEAD"), method = "putMapState(Lnet/minecraft/component/type/MapIdComponent;Lnet/minecraft/item/map/MapState;)V", cancellable = true)
    private void onPutMapState(MapIdComponent id, MapState state, CallbackInfo ci) {
        if (Objects.equals(id, Scmpp.MAP_ID)) ci.cancel();
    }
}
