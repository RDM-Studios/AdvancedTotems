package com.rdm.advancedtotems.api;

import org.jetbrains.annotations.NotNull;

import com.rdm.advancedtotems.common.registries.ATTotemTiers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;

public interface IUpgradeableTotem {
	
	ObjectArrayList<TotemTier> getValidTiers();
	
	@NotNull
	default ObjectArrayList<TotemTier> getDefaultValidTiers() {
		final TotemTier[] defaultTiers = {ATTotemTiers.NONE, ATTotemTiers.IRON, ATTotemTiers.DIAMOND, ATTotemTiers.NETHERITE};
		return ObjectArrayList.wrap(defaultTiers);
	}
	
	TotemTier getStartingTier();
	TotemTier getCurrentTier();
	TotemTier getNextTier();
	TotemTier getMaxTier();
	
	void onTotemUse(Entity owner);
	void onTotemTick(Entity owner);
}
