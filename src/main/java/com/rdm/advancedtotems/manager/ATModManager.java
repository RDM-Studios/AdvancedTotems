package com.rdm.advancedtotems.manager;

import com.rdm.advancedtotems.client.ATItemModelPredicates;
import com.rdm.advancedtotems.common.registries.ATItems;
import com.rdm.advancedtotems.common.registries.ATPackets;
import com.rdm.advancedtotems.common.registries.ATTotemTiers;

public class ATModManager {
	
	public static final void registerCommon() {
		ATTotemTiers.registerTotemTiers();
		ATItems.registerItems();
		ATPackets.registerC2SPackets();
	}
	
	public static final void registerClient() {
		ATItemModelPredicates.registerTotemModelPredicates();
		ATPackets.registerS2CPackets();
	}

}
