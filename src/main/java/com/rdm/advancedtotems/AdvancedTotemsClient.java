package com.rdm.advancedtotems;

import com.rdm.advancedtotems.manager.ATModManager;

import net.fabricmc.api.ClientModInitializer;

public class AdvancedTotemsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ATModManager.registerClient();
	}

}
