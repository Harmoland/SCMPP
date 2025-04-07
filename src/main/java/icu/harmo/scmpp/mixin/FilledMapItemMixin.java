package icu.harmo.scmpp.mixin;

import icu.harmo.scmpp.Noticer;
import icu.harmo.scmpp.Scmpp;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilledMapItem.class)
public abstract class FilledMapItemMixin extends Item {
    public FilledMapItemMixin(Item.Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "HEAD"), method = "inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EquipmentSlot;)V")
    private void onInventoryTick(ItemStack stack, ServerWorld world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if (!world.isClient && entity.isPlayer() && slot != null && slot.getType() == EquipmentSlot.Type.HAND) {
            MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
            if (mapIdComponent != null && mapIdComponent.id() == Scmpp.MAP_ID.id()) {
                Noticer.noticePlayer((ServerPlayerEntity) entity, world);
            }
        }
    }

}
