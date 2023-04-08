package com.rdm.advancedtotems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow public abstract ItemStack getMainHandStack();
	@Shadow public abstract ItemStack getOffHandStack();
	
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tryUseTotem", at = @At("HEAD"))
	private void at$tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> info) {
		if (getMainHandStack().getItem() instanceof BaseTotemItem) {
			BaseTotemItem customTotem = (BaseTotemItem) getMainHandStack().getItem();
			customTotem.onTotemUse(this);
		} else if (getOffHandStack().getItem() instanceof BaseTotemItem) {
			BaseTotemItem customTotem = (BaseTotemItem) getOffHandStack().getItem();
			customTotem.onTotemUse(this);
		}
	}
	
}