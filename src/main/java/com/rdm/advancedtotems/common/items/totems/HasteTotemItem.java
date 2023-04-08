package com.rdm.advancedtotems.common.items.totems;

import com.rdm.advancedtotems.api.TotemTier;
import com.rdm.advancedtotems.common.items.base.BaseTotemItem;
import com.rdm.advancedtotems.common.registries.ATTotemTiers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class HasteTotemItem extends BaseTotemItem {

	public HasteTotemItem(Settings settings) {
		super(settings);
	}

	@Override
	public ObjectArrayList<TotemTier> getValidTiers() {
		final TotemTier[] validTiers = {ATTotemTiers.NONE, ATTotemTiers.IRON};
		return ObjectArrayList.wrap(validTiers);
	}

	@Override
	public TotemTier getStartingTier() {
		return ATTotemTiers.NONE;
	}

	@Override
	public TotemTier getMaxTier() {
		return ATTotemTiers.IRON;
	}

	@Override
	public void onTotemUse(Entity owner) {
		
	}

	@Override
	public void onTotemTick(Entity owner) {
		if (owner instanceof LivingEntity) {
			LivingEntity livingOwner = (LivingEntity) owner;
			livingOwner.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, getCurrentTier().getValue()));
		}
	}

	@Override
	public LiteralText getExtendedDescription() {
		return new LiteralText("When held, applies haste (level scales with tier)");
	}

	@Override
	public Formatting getExtendedDescriptionFormatting() {
		return Formatting.YELLOW;
	}

}
