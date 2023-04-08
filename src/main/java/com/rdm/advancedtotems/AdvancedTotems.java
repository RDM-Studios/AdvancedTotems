package com.rdm.advancedtotems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rdm.advancedtotems.manager.ATModManager;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class AdvancedTotems implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "advancedtotems";
	public static final String MODNAME = "Advanced Totems";
	
	@Override
	public void onInitialize() {
		ATModManager.registerCommon();
	}
	
	public static Identifier prefix(String loc) {
		return new Identifier(MODID, loc);
	}
}