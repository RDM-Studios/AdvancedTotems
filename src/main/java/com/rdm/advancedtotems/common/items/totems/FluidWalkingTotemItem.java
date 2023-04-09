package com.rdm.advancedtotems.common.items.totems;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class FluidWalkingTotemItem extends BaseTotemItem {

	public FluidWalkingTotemItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onTotemUse(Entity owner) {
	}

	@Override
	public void onTotemTick(Entity owner) {
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
		return new LiteralText("Allows for walking on fluids.");
	}

}
