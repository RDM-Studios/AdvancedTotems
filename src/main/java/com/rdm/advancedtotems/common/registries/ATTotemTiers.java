package com.rdm.advancedtotems.common.registries;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.api.TotemTier;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

public class ATTotemTiers {
	private static final Object2ObjectArrayMap<String, TotemTier> TOTEM_TIERS_REGISTRY_MAP = new Object2ObjectArrayMap<String, TotemTier>();
	
	public static final TotemTier NONE = registerTotemTier("none", new TotemTier("none", 1).setTierFormatting(Formatting.WHITE));
	public static final TotemTier IRON = registerTotemTier("iron", new TotemTier("iron", 2));
	public static final TotemTier DIAMOND = registerTotemTier("diamond", new TotemTier("diamond", 3).setTierFormatting(Formatting.AQUA));
	public static final TotemTier NETHERITE = registerTotemTier("netherite", new TotemTier("netherite", 4).setTierFormatting(Formatting.DARK_GRAY));
	
	private static <T extends TotemTier> T registerTotemTier(String tierName, T totemTier) {
		if (!TOTEM_TIERS_REGISTRY_MAP.containsKey(tierName)) 
			TOTEM_TIERS_REGISTRY_MAP.put(AdvancedTotems.prefix(tierName).toString(), totemTier);
		return totemTier;
	}
	
	@Nullable
	public static TotemTier getTier(String tierName, Integer tierValue) {
		TotemTier targetTier = TOTEM_TIERS_REGISTRY_MAP.get(AdvancedTotems.prefix(tierName).toString());
		if (targetTier != null) {
			TotemTier otherTier = new TotemTier(tierName, tierValue);
			if (targetTier.equals(otherTier)) return targetTier;
		}
		return null;
	}
	
	public static void registerTotemTiers() {
		for (Entry<String, TotemTier> totemTierEntry : TOTEM_TIERS_REGISTRY_MAP.object2ObjectEntrySet()) {
			Registry.register(ATRegistries.TOTEM_TIERS, totemTierEntry.getKey(), totemTierEntry.getValue());
		}
	}
}
