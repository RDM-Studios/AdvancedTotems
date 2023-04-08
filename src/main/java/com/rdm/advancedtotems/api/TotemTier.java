package com.rdm.advancedtotems.api;

import net.minecraft.util.Formatting;

public class TotemTier {
	private final String tierName;
	private final Integer tierValue;
	private Formatting tierTooltipFormatting = Formatting.GRAY;
	
	public TotemTier(String tierName, Integer tierValue) {
		this.tierName = tierName;
		if (tierValue < 1) 
			throw new UnsupportedOperationException("Attempted to set value for totem tier: " + tierName + " under 1. This can cause major item NBT issues, so this exception is thrown as a failsafe. Please set the totem tier's value to anything above 0!");
		this.tierValue = tierValue;
	}
	
	public TotemTier setTierFormatting(Formatting tierTooltipFormatting) {
		this.tierTooltipFormatting = tierTooltipFormatting;
		return this;
	}
	
	public String getName() {
		return tierName;
	}
	
	public Integer getValue() {
		return tierValue;
	}
	
	public Formatting getTooltipFormatting() {
		return tierTooltipFormatting;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (this.getClass() != other.getClass()) return false;
		if (!(other instanceof TotemTier)) return false;
		
		final TotemTier otherTier = (TotemTier) other;
		
		return this.getName() == otherTier.getName() && this.getValue() == otherTier.getValue();
	}
}
