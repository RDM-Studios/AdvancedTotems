package com.rdm.advancedtotems.common.networking.packets;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class TotemTierSyncPacket {
	
	public static void recieve(MinecraftServer server, ServerPlayerEntity owner, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		for (ItemStack targetStack : owner.inventory.main) {
			if (targetStack.getItem() instanceof BaseTotemItem) {
				BaseTotemItem baseTotem = (BaseTotemItem) targetStack.getItem();
				baseTotem.syncTier();
			}
		}
	}

}
