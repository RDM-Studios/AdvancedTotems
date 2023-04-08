package com.rdm.advancedtotems.common.registries;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.common.networking.packets.TotemTierSyncPacket;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ATPackets {
	public static final Identifier TOTEM_TIER_UPDATE_PACKET = AdvancedTotems.prefix("totem_tier_sync");
	
	
	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(TOTEM_TIER_UPDATE_PACKET, TotemTierSyncPacket::recieve);
	}
	
	public static void registerS2CPackets() {
		
	}
}
