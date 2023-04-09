package com.rdm.advancedtotems.common.items.totems;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class MendingTotemItem extends BaseTotemItem {
	private int amountHealed = 0;

	public MendingTotemItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onTotemUse(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;

			for (ItemStack curStack : playerOwner.inventory.main) {
				if (!curStack.isEmpty()) {
					if (curStack.isDamageable() && curStack.isDamaged()) {
						switch (getCurrentTier().getValue()) {
						default:
							break;
						case 1:
							if (curStack.getDamage() >= curStack.getMaxDamage() - 1) {
								curStack.setDamage(curStack.getDamage() - getBreakMending());
								amountHealed += getBreakMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(10, RANDOM, playerOwner);
							break;
						case 2:
							if (curStack.getDamage() >= curStack.getMaxDamage() - 1) {
								curStack.setDamage(curStack.getDamage() - getBreakMending());
								amountHealed += getBreakMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(10, RANDOM, playerOwner);
							break;
						case 3:
							if (curStack.getDamage() >= curStack.getMaxDamage() - 1) {
								curStack.setDamage(curStack.getDamage() - getBreakMending());
								amountHealed += getBreakMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(10, RANDOM, playerOwner);
							break;
						case 4:
							if (curStack.getDamage() >= curStack.getMaxDamage() - 1) {
								curStack.setDamage(curStack.getDamage() - getBreakMending());
								amountHealed += getBreakMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(10, RANDOM, playerOwner);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public void onTotemTick(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;

			for (ItemStack curStack : playerOwner.inventory.main) {
				if (!curStack.isEmpty()) {
					if (curStack.isDamageable() && curStack.isDamaged()) {
						switch (getCurrentTier().getValue()) {
						default:
							break;
						case 1:
							if (playerOwner.getServerWorld().getTime() % 100 == 0 && getDefaultStack().getDamage() < getDefaultStack().getMaxDamage()) {
								curStack.setDamage(curStack.getDamage() - getPassiveMending());
								amountHealed += getPassiveMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(1, RANDOM, playerOwner);
							break;
						case 2:
							if (playerOwner.getServerWorld().getTime() % 60 == 0 && getDefaultStack().getDamage() < getDefaultStack().getMaxDamage()) {
								curStack.setDamage(curStack.getDamage() - getPassiveMending());
								amountHealed += getPassiveMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(1, RANDOM, playerOwner);
							break;
						case 3:
							if (playerOwner.getServerWorld().getTime() % 40 == 0 && getDefaultStack().getDamage() < getDefaultStack().getMaxDamage()) {
								curStack.setDamage(curStack.getDamage() - getPassiveMending());
								amountHealed += getPassiveMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(1, RANDOM, playerOwner);
							break;
						case 4:
							if (playerOwner.getServerWorld().getTime() % 20 == 0 && getDefaultStack().getDamage() < getDefaultStack().getMaxDamage()) {
								curStack.setDamage(curStack.getDamage() - getPassiveMending());
								amountHealed += getPassiveMending();
							}
							if (amountHealed % 100 == 0) getDefaultStack().damage(1, RANDOM, playerOwner);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public LiteralText getExtendedDescription() {
		return new LiteralText("Mends any item in a player's inventory which is about to break by " + getBreakMending() + " durability.");
	}

	private int getBreakMending() {
		switch (getCurrentTier().getValue()) {
		default:
			return 10;
		case 1:
			return 10;
		case 2:
			return 20;
		case 3:
			return 50;
		case 4:
			return 100;
		}
	}

	private int getPassiveMending() {
		switch (getCurrentTier().getValue()) {
		default:
			return 1;
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 100;
		}
	}

	private int getPassiveMendingInterval() {
		switch (getCurrentTier().getValue()) {
		default:
			return 5;
		case 1:
			return 5;
		case 2:
			return 3;
		case 3:
			return 2;
		case 4:
			return 1;
		}
	}

	@Override
	public LiteralText getPassiveExtendedDescription() {
		return new LiteralText("Mends damageable items in a player's inventory by " + getPassiveMending() + " durability every " + getPassiveMendingInterval() + " seconds.");
	}

	@Override
	public Formatting getPassiveExtendedDescriptionFormatting() {
		return Formatting.AQUA;
	}

	@Override
	public boolean postProcessNbt(NbtCompound nbt) {
		return true;
	}

	@Override
	public Formatting getExtendedDescriptionFormatting() {
		return null;
	}
}
