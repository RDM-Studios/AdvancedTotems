package com.rdm.advancedtotems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;
import com.rdm.advancedtotems.common.registries.ATItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity {

	public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
	public void at$onPlayerCollision(PlayerEntity player, CallbackInfo info) {
		ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;
        if (this.world.isClient) {
            return;
        }
        if (orb.pickupDelay == 0 && player.experiencePickUpDelay == 0) {
            ItemStack totem = BaseTotemItem.findTotemInInventory(ATItems.TOTEM_OF_MENDING, player, false);
            ItemStack iTotem = BaseTotemItem.findTotemInInventory(ATItems.TOTEM_OF_MENDING_IRON, player, false);
            ItemStack dTotem = BaseTotemItem.findTotemInInventory(ATItems.TOTEM_OF_MENDING_DIAMOND, player, false);
            ItemStack nTotem = BaseTotemItem.findTotemInInventory(ATItems.TOTEM_OF_MENDING_NETHERITE, player, false);
            player.experiencePickUpDelay = 2;
            player.sendPickup(this, 1);
            //TODO Goofy ahh junior level code -- Meme Man
            if (totem != null) {
            	if (totem.isDamaged()) {
            		int amt = Math.min(orb.getMendingRepairAmount(orb.getExperienceAmount()), totem.getDamage());
            		totem.setDamage(totem.getDamage() - orb.getMendingRepairCost(amt));
            	}
            }
            if (iTotem != null) {
            	if (totem.isDamaged()) {
            		int amt = Math.min(orb.getMendingRepairAmount(orb.getExperienceAmount()), totem.getDamage());
            		totem.setDamage(totem.getDamage() - orb.getMendingRepairCost(amt));
            	}
            }
            if (dTotem != null) {
            	if (totem.isDamaged()) {
            		int amt = Math.min(orb.getMendingRepairAmount(orb.getExperienceAmount()), totem.getDamage());
            		totem.setDamage(totem.getDamage() - orb.getMendingRepairCost(amt));
            	}
            }
            if (nTotem != null) {
            	if (totem.isDamaged()) {
            		int amt = Math.min(orb.getMendingRepairAmount(orb.getExperienceAmount()), totem.getDamage());
            		totem.setDamage(totem.getDamage() - orb.getMendingRepairCost(amt));
            	}
            }
            
            if (orb.getExperienceAmount() > 0) {
                player.addExperience(orb.getExperienceAmount());
            }
            this.remove();
        }
        info.cancel();
	}
}
