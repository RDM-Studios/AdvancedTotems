package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.common.items.totems.FluidWalkingTotemItem;
import com.rdm.advancedtotems.common.items.totems.HasteTotemItem;
import com.rdm.advancedtotems.common.items.totems.MendingTotemItem;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.registry.Registry;

public class ATItems {
	private static final Object2ObjectArrayMap<String, Item> ITEMS_REGISTRY_MAP = new Object2ObjectArrayMap<String, Item>();
	
	// Totem of Haste
	public static final HasteTotemItem TOTEM_OF_HASTE = registerItem("totem_of_haste", new HasteTotemItem(new Settings().maxCount(1)).setTier(ATTotemTiers.NONE));
	public static final HasteTotemItem TOTEM_OF_HASTE_IRON = registerItem("totem_of_haste_iron", new HasteTotemItem(new Settings().maxCount(1)).setTier(ATTotemTiers.IRON));
	
	// Totem of Fluid Walking
	public static final FluidWalkingTotemItem TOTEM_OF_FLUID_WALKING = registerItem("totem_of_fluid_walking", new FluidWalkingTotemItem(new Settings().maxCount(1)).setTier(ATTotemTiers.NONE));
	
	// Totem of Mending
	public static final MendingTotemItem TOTEM_OF_MENDING = registerItem("totem_of_mending", new MendingTotemItem(new Settings().maxDamage(100)).setTier(ATTotemTiers.NONE));
	public static final MendingTotemItem TOTEM_OF_MENDING_IRON = registerItem("totem_of_mending_iron", new MendingTotemItem(new Settings().maxDamage(500)).setTier(ATTotemTiers.IRON));
	public static final MendingTotemItem TOTEM_OF_MENDING_DIAMOND = registerItem("totem_of_mending_diamond", new MendingTotemItem(new Settings().maxDamage(1000)).setTier(ATTotemTiers.DIAMOND));
	public static final MendingTotemItem TOTEM_OF_MENDING_NETHERITE = registerItem("totem_of_mending_netherite", new MendingTotemItem(new Settings().maxDamage(3000)).setTier(ATTotemTiers.NETHERITE));
	
	private static <I extends Item> I registerItem(String registryName, I item) {
		if (!ITEMS_REGISTRY_MAP.containsKey(registryName))
			ITEMS_REGISTRY_MAP.put(AdvancedTotems.prefix(registryName).toString(), item);
		return item;
	}
	
	public static void registerItems() {
		for (Entry<String, Item> itemEntry : ITEMS_REGISTRY_MAP.object2ObjectEntrySet())  {
			Registry.register(Registry.ITEM, itemEntry.getKey(), itemEntry.getValue());
		}
	}
}
