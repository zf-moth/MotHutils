package org.zfmoth.mothutils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zfmoth.mothutils.MotHutils;

@Mixin( ClientPlayerEntity.class )
public abstract class ClientPlayerEntityMixin {
    /**
     * @author MotH
     * @reason Removes the elytra check for specified people
     */
    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void logGlide(CallbackInfo ci) {
        MotHutils.LOGGER.info("ClientFallCheck");
    }

    @ModifyExpressionValue(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean onlyGlideIfAllowed(boolean original) {
        // TODO: Add player check
        return true;
    }

    @ModifyExpressionValue(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean onlyGlideIfAllowed2(boolean original) {
        // TODO: Add player check
        return true;
    }
}
