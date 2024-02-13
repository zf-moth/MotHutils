package org.zfmoth.mothutils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zfmoth.mothutils.MotHutils;

@Mixin( PlayerEntity.class )
public abstract class PlayerEntityMixin {
    /**
     * @author MotH
     * @reason Removes the elytra check for specified people
     */
    @Inject(method = "checkFallFlying", at = @At("HEAD"))
    public void logGlide(CallbackInfoReturnable<Boolean> cir) {
        MotHutils.LOGGER.info("FallCheck");
    }

    @ModifyExpressionValue(
            method = "checkFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean onlyGlideIfAllowed(boolean original) {
        // TODO: Add player check
        return true;
    }

    @ModifyExpressionValue(
            method = "checkFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ElytraItem;isUsable(Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean onlyGlideIfAllowed2(boolean original) {
        // TODO: Add player check
        return true;
    }
}
