package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.common.network.packets.TotemAnimationPacket;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ATPackets {
	public static final Identifier TOTEM_ANIMATION_PACKET = AdvancedTotems.prefix("totem_animation");
	
	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(TOTEM_ANIMATION_PACKET, TotemAnimationPacket::recieve);
	}
}
