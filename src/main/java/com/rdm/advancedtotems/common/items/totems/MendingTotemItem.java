package com.rdm.advancedtotems.common.items.totems;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.common.items.base.BaseTotemItem;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class MendingTotemItem extends BaseTotemItem {

	public MendingTotemItem(Settings settings) {
		super(settings);
	}

	@Override
	public void onTotemUse(Entity owner) {
		if (owner instanceof ServerPlayerEntity) {
			ServerPlayerEntity playerOwner = (ServerPlayerEntity) owner;
			ItemStack totemStack = findTotemInInventory(this, playerOwner, false);
			
			if (!playerOwner.inventory.armor.isEmpty()) {
				for (ItemStack armorStack : playerOwner.inventory.armor) {
					if (!armorStack.isEmpty()) {
						doBreakMending(playerOwner, armorStack);
					}
				}
			}
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
				if (totemStack.getOrCreateTag().getCompound("AmountHealed") != null) System.out.println("[NBT HEALED]: " + totemStack.getOrCreateTag().getCompound("AmountHealed").getInt("DurabilityHealed"));
				if (canDamageOffMending(totemStack)) {
					if (!playerOwner.world.isClient) totemStack.damage(1, playerOwner, (pOwner) -> pOwner.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(Type.HAND, 0)));
				}
			}
		}
	}
	
	private boolean canDamageOffMending(ItemStack totemStack) {
		return totemStack.getDamage() < totemStack.getMaxDamage() && totemStack.getOrCreateTag().getCompound("AmountHealed") != null && totemStack.getOrCreateTag().getCompound("AmountHealed").getInt("DurabilityHealed") != 0 && totemStack.getOrCreateTag().getCompound("AmountHealed").getInt("DurabilityHealed") % 100 == 0;
	}

	@Override
	public LiteralText getExtendedDescription() {
		return new LiteralText("Mends any item in a player's inventory which is about to break by " + getBreakMending() + " durability. Activates only when said item is 1 durability.");
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
			return 10;
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
	
	private void doBreakMending(ServerPlayerEntity playerOwner, ItemStack curStack) {
		ItemStack totemStack = findTotemInInventory(this, playerOwner, false);
		int durHealed = 0;
		NbtCompound dataAmountHealed = new NbtCompound();
		if (curStack.getItem() instanceof BaseTotemItem) return;
		switch (((BaseTotemItem) totemStack.getItem()).getCurrentTier().getValue()) {
		default:
			break;
		case 1:
			curStack.setDamage(curStack.getDamage() - getBreakMending());
			durHealed += getBreakMending();
			playTotemAnimation(playerOwner, this);
			break;
		case 2:
			curStack.setDamage(curStack.getDamage() - getBreakMending());
			durHealed += getBreakMending();
			playTotemAnimation(playerOwner, this);
			break;
		case 3:
			curStack.setDamage(curStack.getDamage() - getBreakMending());
			durHealed += getBreakMending();
			playTotemAnimation(playerOwner, this);
			break;
		case 4:
			curStack.setDamage(curStack.getDamage() - getBreakMending());
			durHealed += getBreakMending();
			playTotemAnimation(playerOwner, this);
			break;
		}
		dataAmountHealed.putInt("DurabilityHealed", dataAmountHealed.getInt("DurabilityHealed") + durHealed);
		totemStack.getOrCreateTag().put("AmountHealed", dataAmountHealed);
	}
	
	private void doPassiveMending(ServerPlayerEntity playerOwner, ItemStack curStack) {
		ItemStack totemStack = findTotemInInventory(this, playerOwner, true);
		int durHealed = 0;
		NbtCompound dataAmountHealed = new NbtCompound();
		if (curStack.getItem() instanceof BaseTotemItem) return;
		switch (((BaseTotemItem) totemStack.getItem()).getCurrentTier().getValue()) {
		default:
			break;
		case 1:
			if (playerOwner.getServerWorld().getTime() % 100 == 0 && totemStack.getDamage() < totemStack.getMaxDamage()) {
				curStack.setDamage(curStack.getDamage() - getPassiveMending());
				durHealed += getPassiveMending();
			}
			break;
		case 2:
			if (playerOwner.getServerWorld().getTime() % 60 == 0 && totemStack.getDamage() < totemStack.getMaxDamage()) {
				curStack.setDamage(curStack.getDamage() - getPassiveMending());
				durHealed += getPassiveMending();
			}
			break;
		case 3:
			if (playerOwner.getServerWorld().getTime() % 40 == 0 && totemStack.getDamage() < totemStack.getMaxDamage()) {
				curStack.setDamage(curStack.getDamage() - getPassiveMending());
				durHealed += getPassiveMending();
			}
			break;
		case 4:
			if (playerOwner.getServerWorld().getTime() % 20 == 0 && totemStack.getDamage() < totemStack.getMaxDamage()) {
				curStack.setDamage(curStack.getDamage() - getPassiveMending());
				durHealed += getPassiveMending();
			}
			break;
		}
		dataAmountHealed.putInt("DurabilityHealed", durHealed);
		totemStack.getOrCreateTag().put("AmountHealed", dataAmountHealed);
	}

	@Override
	public LiteralText getPassiveExtendedDescription() {
		return new LiteralText("Mends damageable items in a player's inventory by " + getPassiveMending() + " durability every " + (getPassiveMendingInterval() > 1 ? getPassiveMendingInterval() + " seconds." : "second."));
	}

	@Override
	public Formatting getPassiveExtendedDescriptionFormatting() {
		return null;
	}

	@Override
	public boolean postProcessNbt(NbtCompound nbt) {
		return true;
	}

	@Override
	public Formatting getExtendedDescriptionFormatting() {
		return Formatting.AQUA;
	}
}
