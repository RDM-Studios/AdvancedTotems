package com.rdm.advancedtotems.api;

import net.minecraft.entity.Entity;

public interface IUpgradeableTotem {
	TotemTier getCurrentTier();
	
	void onTotemUse(Entity owner);
	void onTotemTick(Entity owner);
	
	boolean shouldActivateTotem(Entity owner);
}
