package icu.harmo.scmpp.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static icu.harmo.scmpp.Scmpp.MAP_ID;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    @Inject(at = @At("HEAD"), method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", cancellable = true)
    public void setHeldItemStack(ItemStack stack, boolean update, CallbackInfo info) {
        if (stack.isOf(Items.FILLED_MAP)) {
            MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
            if (mapIdComponent != null && mapIdComponent.id() == MAP_ID.id()) {
                info.cancel();
            }
        }
    }
}
