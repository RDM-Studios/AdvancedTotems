package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.api.TotemTier;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;

public class ATRegistries {
	
	public static final Registry<TotemTier> TOTEM_TIERS = FabricRegistryBuilder.createSimple(TotemTier.class, AdvancedTotems.prefix("totem-tiers")).buildAndRegister();

}
