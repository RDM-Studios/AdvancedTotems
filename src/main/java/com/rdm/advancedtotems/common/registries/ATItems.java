package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.common.items.totems.HasteTotemItem;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.registry.Registry;

public class ATItems {
	private static final Object2ObjectArrayMap<String, Item> ITEMS_REGISTRY_MAP = new Object2ObjectArrayMap<String, Item>();
	
	public static final HasteTotemItem TOTEM_OF_HASTE = registerItem("totem_of_haste", new HasteTotemItem(new Settings()));
	
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
