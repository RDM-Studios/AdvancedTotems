package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ATItemGroups {
	
	public static final ItemGroup AT_TOTEMS = FabricItemGroupBuilder.create(AdvancedTotems.prefix("totems"))
			.icon(() -> new ItemStack(ATItems.TOTEM_OF_MENDING_NETHERITE))
			.build();

}
