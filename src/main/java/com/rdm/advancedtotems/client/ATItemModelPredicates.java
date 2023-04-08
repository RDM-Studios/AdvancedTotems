package com.rdm.advancedtotems.client;

import com.rdm.advancedtotems.common.items.totems.HasteTotemItem;
import com.rdm.advancedtotems.common.registries.ATItems;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ATItemModelPredicates {
	
	public static void registerTotemModelPredicates() {
		FabricModelPredicateProviderRegistry.register(ATItems.TOTEM_OF_HASTE, new Identifier("none"), (totemStack, world, owner) -> {
			if (owner == null) return 0.0F;
			if (totemStack.getItem() instanceof HasteTotemItem) {
				HasteTotemItem hasteTotem = (HasteTotemItem) totemStack.getItem();
				
				if (hasteTotem.getCurrentTier().equals(hasteTotem.getStartingTier())) return 1.0F;
			}
			return 0.0F;
		});
		FabricModelPredicateProviderRegistry.register(ATItems.TOTEM_OF_HASTE, new Identifier("iron"), (totemStack, world, owner) -> {
			if (owner == null) return 0.0F;
			if (totemStack.getItem() instanceof HasteTotemItem) {
				HasteTotemItem hasteTotem = (HasteTotemItem) totemStack.getItem();
				
				if (hasteTotem.getCurrentTier().equals(hasteTotem.getMaxTier())) return 1.0F;
			}
			return 0.0F;
		});
	}

}
