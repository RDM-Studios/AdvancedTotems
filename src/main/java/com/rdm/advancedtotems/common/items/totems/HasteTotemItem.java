package com.rdm.advancedtotems.common.items.totems;

import com.rdm.advancedtotems.api.NumeralConverter;
import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

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
	public void onTotemUse(Entity owner) {
	}

	@Override
	public void onTotemTick(Entity owner) {
		if (owner instanceof LivingEntity) {
			LivingEntity livingOwner = (LivingEntity) owner;
			livingOwner.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, getCurrentTier().getValue() - 1));
		}
	}
	
	@Override
	public boolean shouldActivateTotem(Entity owner) {
		return false;
	}

	@Override
	public LiteralText getExtendedDescription() {
		return null;
	}

	@Override
	public Formatting getExtendedDescriptionFormatting() {
		return null;
	}

	@Override
	public Formatting getPassiveExtendedDescriptionFormatting() {
		return null;
	}

	@Override
	public LiteralText getPassiveExtendedDescription() {
		return new LiteralText("Grants Haste " + NumeralConverter.toRoman(Integer.parseInt(getCurrentTier().getValue().toString())) + " to the player.");
	}

}
