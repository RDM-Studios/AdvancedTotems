package com.rdm.advancedtotems.common.items.totems;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class MendingTotemItem extends BaseTotemItem {
	private int durHealed = 0;

	public MendingTotemItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onTotemUse(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;
			ItemStack totemStack = findTotemInInventory(this, playerOwner, false);

			if (!playerOwner.inventory.offHand.isEmpty()) {
				for (ItemStack offHandStack : playerOwner.inventory.offHand) {
					if (!offHandStack.isEmpty()) {
						doBreakMending(playerOwner, offHandStack);
					}
				}
			}

			if (!playerOwner.inventory.main.isEmpty()) {
				for (ItemStack curStack : playerOwner.inventory.main) {
					if (!curStack.isEmpty()) {
						if (curStack.isDamageable() && curStack.getDamage() >= curStack.getMaxDamage() - 5) {
							doBreakMending(playerOwner, curStack);
						}
					}
				}
			}
			Criteria.USED_TOTEM.trigger(playerOwner, totemStack);
		}
	}

	@Override
	public boolean shouldActivateTotem(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;
			ItemStack totemStack = findTotemInInventory(this, playerOwner, false);

			if (totemStack != null) {
				if (!playerOwner.inventory.main.isEmpty()) {
					for (ItemStack curStack : playerOwner.inventory.main) {
						if (!curStack.isEmpty() && curStack.isDamageable()) {
							return curStack.getDamage() >= curStack.getMaxDamage() - 5;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	//TODO Re-add the tiering system I was making, if ever --Meme Man
	@Nullable
	@Override
	public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return null;
	}

	@Override
	public void onTotemTick(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;
			ItemStack totemStack = findTotemInInventory(this, playerOwner, true);

			if (totemStack != null) {
				if (!playerOwner.inventory.armor.isEmpty()) {
					for (ItemStack armorStack : playerOwner.inventory.armor) {
						if (!armorStack.isEmpty()) {
							doPassiveMending(playerOwner, armorStack);
						}
					}
				}
				if (!playerOwner.inventory.offHand.isEmpty()) {
					for (ItemStack offHandStack : playerOwner.inventory.offHand) {
						if (!offHandStack.isEmpty()) {
							doPassiveMending(playerOwner, offHandStack);
						}
					}
				}
				if (!playerOwner.inventory.main.isEmpty()) {
					for (ItemStack curStack : playerOwner.inventory.main) {
						if (!curStack.isEmpty()) {
							if (curStack.isDamageable() && curStack.isDamaged()) {
								doPassiveMending(playerOwner, curStack);
							}
						}
					}
				}
			}
		}
	}

	private boolean canDamageOffMending(ItemStack totemStack) {
		return totemStack.getDamage() < totemStack.getMaxDamage() && totemStack.hasTag() && totemStack.getOrCreateTag().getInt("DurabilityHealed") % 100 == 0;
	}

	@Override
	public LiteralText getExtendedDescription() {
		return new LiteralText("Mends any item in a player's inventory which is about to break by " + getBreakMending(this) + " durability. Activates only when said item is 1 durability.");
	}

	private int getBreakMending(MendingTotemItem mendingTotem) {
		switch (mendingTotem.getCurrentTier().getValue()) {
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

	private int getPassiveMending(MendingTotemItem mendingTotem) {
		switch (mendingTotem.getCurrentTier().getValue()) {
		default:
			return 1;
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 10;
		}
	}

	private int getPassiveMendingInterval(MendingTotemItem mendingTotem) {
		switch (mendingTotem.getCurrentTier().getValue()) {
		default:
			return 100;
		case 1:
			return 100;
		case 2:
			return 60;
		case 3:
			return 40;
		case 4:
			return 20;
		}
	}

	private void doBreakMending(ServerPlayerEntity playerOwner, ItemStack curStack) {
		ItemStack totemStack = findTotemInInventory(this, playerOwner, false);
		NbtCompound dataAmountHealed = new NbtCompound();

		if (curStack.getItem() instanceof BaseTotemItem) return;

		if (totemStack.getItem() instanceof MendingTotemItem) {
			final MendingTotemItem mendingTotem = (MendingTotemItem) totemStack.getItem();

			switch (mendingTotem.getCurrentTier().getValue()) {
			default:
				break;
			case 1:
				curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
				mendingTotem.durHealed += getBreakMending(mendingTotem);
				playTotemAnimation(playerOwner, this);
				break;
			case 2:
				curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
				mendingTotem.durHealed += getBreakMending(mendingTotem);
				playTotemAnimation(playerOwner, this);
				break;
			case 3:
				curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
				mendingTotem.durHealed += getBreakMending(mendingTotem);
				playTotemAnimation(playerOwner, this);
				break;
			case 4:
				curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
				mendingTotem.durHealed += getBreakMending(mendingTotem);
				playTotemAnimation(playerOwner, this);
				break;
			}

			if (!playerOwner.world.isClient) {
				if (!totemStack.getOrCreateTag().contains("DurabilityHealed") && mendingTotem.durHealed > 0) mendingTotem.durHealed = 0;
				dataAmountHealed.putInt("DurabilityHealed", mendingTotem.durHealed);
				totemStack.setTag(dataAmountHealed);
				if (canDamageOffMending(totemStack)) {
					totemStack.damage(10, playerOwner, (pOwner) -> totemStack.setDamage(totemStack.getMaxDamage() - 1));
				}
			}
		}
	}

	private void doPassiveMending(ServerPlayerEntity playerOwner, ItemStack curStack) {
		ItemStack totemStack = findTotemInInventory(this, playerOwner, true);
		NbtCompound dataAmountHealed = new NbtCompound();

		if (curStack.getItem() instanceof BaseTotemItem) return;

		if (totemStack.getItem() instanceof MendingTotemItem) {
			final MendingTotemItem mendingTotem = (MendingTotemItem) totemStack.getItem();

			if (playerOwner.getServerWorld().getTime() % getPassiveMendingInterval(mendingTotem) == 0 && totemStack.getDamage() < totemStack.getMaxDamage()) {
				curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
				mendingTotem.durHealed += getPassiveMending(mendingTotem);
			}
			
			if (!playerOwner.world.isClient) {
				if (!totemStack.getOrCreateTag().contains("DurabilityHealed") && mendingTotem.durHealed > 0) mendingTotem.durHealed = 0;
				dataAmountHealed.putInt("DurabilityHealed", mendingTotem.durHealed);
				totemStack.setTag(dataAmountHealed);
				if (canDamageOffMending(totemStack)) {
					totemStack.damage(1, playerOwner, (pOwner) -> totemStack.setDamage(totemStack.getMaxDamage() - 1));
				}
			}
		}
	}

	@Override
	public LiteralText getPassiveExtendedDescription() {
		return new LiteralText("Mends damageable items in a player's inventory by " + getPassiveMending(this) + " durability every " + (getPassiveMendingInterval(this) > 1 ? getPassiveMendingInterval(this) + " seconds." : "second."));
	}

	@Override
	public Formatting getPassiveExtendedDescriptionFormatting() {
		return null;
	}

	@Override
	public boolean postProcessNbt(NbtCompound nbt) {
		return false;
	}

	@Override
	public Formatting getExtendedDescriptionFormatting() {
		return Formatting.AQUA;
	}
}
