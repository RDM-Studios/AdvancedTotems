package com.rdm.advancedtotems.api;

import java.util.Comparator;

public class TotemTierComparator implements Comparator<TotemTier> {

	@Override
	public int compare(TotemTier tier, TotemTier otherTier) {
		return tier.getValue() - otherTier.getValue();
	}

}
