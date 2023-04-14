package com.rdm.advancedtotems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;
import com.rdm.advancedtotems.common.registries.ATItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow public abstract ItemStack getMainHandStack();
	@Shadow public abstract ItemStack getOffHandStack();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void at$tick(CallbackInfo info) {
		if (getMainHandStack().getItem() instanceof BaseTotemItem) {
			BaseTotemItem customTotem = (BaseTotemItem) getMainHandStack().getItem();
			if (customTotem.shouldActivateTotem(this)) {
				customTotem.onTotemUse(this);
			}
		} else if (getOffHandStack().getItem() instanceof BaseTotemItem) {
			BaseTotemItem customTotem = (BaseTotemItem) getOffHandStack().getItem();
			if (customTotem.shouldActivateTotem(this)) {
				customTotem.onTotemUse(this);
			}
		}
	}

	@Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
	public void at$canWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> info) {
		LivingEntity living = (LivingEntity) (Object) this;
		if (living instanceof ServerPlayerEntity) {
			if ((getMainHandStack().isItemEqual(ATItems.TOTEM_OF_FLUID_WALKING.getDefaultStack()) || getOffHandStack().isItemEqual(ATItems.TOTEM_OF_FLUID_WALKING.getDefaultStack())) || BaseTotemItem.findCuriosTotem(ATItems.TOTEM_OF_FLUID_WALKING, (ServerPlayerEntity) living) != null) {
				info.setReturnValue(true);
			}
		}
	}

}