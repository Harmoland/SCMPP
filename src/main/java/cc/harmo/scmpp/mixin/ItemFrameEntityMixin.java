package cc.harmo.scmpp.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cc.harmo.scmpp.Scmpp.MAP_ID;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    @Inject(at = @At("HEAD"), method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", cancellable = true)
    public void setHeldItemStack(ItemStack value, boolean update, CallbackInfo info) {
        if (value.isOf(Items.FILLED_MAP) && value.getOrCreateNbt().getInt("map") == MAP_ID) {
            info.cancel();
        }
    }
}
