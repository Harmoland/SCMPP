package icu.harmo.scmpp.mixin;

import icu.harmo.scmpp.Noticer;
import icu.harmo.scmpp.Scmpp;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilledMapItem.class)
public abstract class FilledMapItemMixin extends NetworkSyncedItem {

    public FilledMapItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "HEAD"), method = "inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V")
    private void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!world.isClient && world instanceof ServerWorld serverWorld && entity instanceof ServerPlayerEntity player &&
                (selected || player.getOffHandStack() == stack)) {
            MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
            if (mapIdComponent != null && mapIdComponent.id() == Scmpp.MAP_ID.id()) {
                Noticer.noticePlayer(player, serverWorld);
            }
        }
    }

}
