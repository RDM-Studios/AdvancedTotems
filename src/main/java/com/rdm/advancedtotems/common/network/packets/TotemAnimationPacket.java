package com.rdm.advancedtotems.common.network.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class TotemAnimationPacket {

	public static void recieve(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		ItemStack totem = buf.readItemStack();
		Entity owner = client.world.getEntityById(buf.readInt());
		
		client.execute(() -> {
			client.particleManager.addEmitter(owner, ParticleTypes.TOTEM_OF_UNDYING);
			client.world.playSound(owner.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, owner.getSoundCategory(), 1.0F, 1.0F, false);
			if (owner.equals(client.player)) client.gameRenderer.showFloatingItem(totem);
		});
	}

}
