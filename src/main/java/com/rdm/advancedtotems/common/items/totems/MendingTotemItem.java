package com.rdm.advancedtotems.common.items.totems;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
					if (!offHandStack.isEmpty() && !(offHandStack.getItem() instanceof MendingTotemItem)) {
						doBreakMending(playerOwner, offHandStack);
					}
				}
			}

			if (!playerOwner.inventory.main.isEmpty()) {
				for (ItemStack curStack : playerOwner.inventory.main) {
					if (!curStack.isEmpty()) {
						if (curStack.isDamageable() && curStack.getDamage() >= curStack.getMaxDamage() - 1 && !(curStack.getItem() instanceof MendingTotemItem)) {
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
						if (!curStack.isEmpty() && curStack.isDamageable() && !(curStack.getItem() instanceof MendingTotemItem)) {
							return curStack.getDamage() >= curStack.getMaxDamage() - 1;
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
							if (curStack.isDamageable() && curStack.isDamaged() && !(curStack.getItem() instanceof MendingTotemItem)) {
								doPassiveMending(playerOwner, curStack);
							}
						}
					}
				}
			}
		}
	}

	private boolean canDamageOffMending(ItemStack totemStack) {
		return totemStack.getItem() instanceof MendingTotemItem && totemStack.getDamage() < totemStack.getMaxDamage() && totemStack.getOrCreateTag().contains("DurabilityHealed") && totemStack.getOrCreateTag().getInt("DurabilityHealed") % 100 == 0; // (totemStack.getDamage() > 0 ? totemStack.getOrCreateTag().getInt("DurabilityHealed") % 100 == 0 : totemStack.getOrCreateTag().getInt("DurabilityHealed") == 100);
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

		if (totemStack.getItem() instanceof MendingTotemItem) {
			final MendingTotemItem mendingTotem = (MendingTotemItem) totemStack.getItem();

			curStack.setDamage(curStack.getDamage() - getBreakMending(mendingTotem));
			mendingTotem.durHealed += getBreakMending(mendingTotem);
			playTotemAnimation(playerOwner, this);

			if (!playerOwner.world.isClient) {
				if (!totemStack.getOrCreateTag().contains("DurabilityHealed") && mendingTotem.durHealed > 0) mendingTotem.durHealed = 0;
				dataAmountHealed.putInt("DurabilityHealed", mendingTotem.durHealed);
				totemStack.setTag(dataAmountHealed);
			}
		}
	}

	private void doPassiveMending(ServerPlayerEntity playerOwner, ItemStack curStack) {
		ItemStack totemStack = findTotemInInventory(this, playerOwner, true);
		NbtCompound dataAmountHealed = new NbtCompound();

		if (totemStack.getItem() instanceof MendingTotemItem) {
			final MendingTotemItem mendingTotem = (MendingTotemItem) totemStack.getItem();

			if (playerOwner.getServerWorld().getTime() % getPassiveMendingInterval(mendingTotem) == 0 && totemStack.getDamage() < totemStack.getMaxDamage() && !(curStack.getItem() instanceof MendingTotemItem)) {
				curStack.setDamage(curStack.getDamage() - (getPassiveMending(mendingTotem)));
				mendingTotem.durHealed += (getPassiveMending(mendingTotem));
			}

			if (!playerOwner.world.isClient) {
				if (!totemStack.getOrCreateTag().contains("DurabilityHealed") && mendingTotem.durHealed > 0) mendingTotem.durHealed = 0;
				dataAmountHealed.putInt("DurabilityHealed", mendingTotem.durHealed);
				totemStack.setTag(dataAmountHealed);
			}
		}
	}
	
	@Override
	public void inventoryTick(ItemStack totemStack, World world, Entity owner, int slot, boolean selected) {
		super.inventoryTick(totemStack, world, owner, slot, selected);
		if (!world.isClient) {
			if (owner instanceof LivingEntity) {
				if (canDamageOffMending(totemStack))
					totemStack.damage(1, (LivingEntity) owner, (pOwner) -> totemStack.setDamage(totemStack.getMaxDamage() - 1));
			}
		}
	}
	
	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient) stack.addEnchantment(Enchantments.MENDING, 1);
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
	public boolean isEnchantable(ItemStack stack) {
		return true;
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
