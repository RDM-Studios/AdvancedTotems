package com.rdm.advancedtotems.common.items.base;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.api.IUpgradeableTotem;
import com.rdm.advancedtotems.api.TotemTier;
import com.rdm.advancedtotems.common.registries.ATItemGroups;
import com.rdm.advancedtotems.common.registries.ATPackets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public abstract class BaseTotemItem extends NetworkSyncedItem implements IUpgradeableTotem {
	private TotemTier curTier;

	public BaseTotemItem(Settings settings) {
		super(settings.rarity(Rarity.UNCOMMON).group(ATItemGroups.AT_TOTEMS));
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseTotemItem> T setTier(TotemTier tier) {
		this.curTier = tier;
		return (T) this;
	}

	@Override
	public abstract void onTotemUse(Entity owner);

	@Override
	public abstract void onTotemTick(Entity owner);

	@Override
	public TotemTier getCurrentTier() {
		return curTier;
	}

	public abstract LiteralText getExtendedDescription();
	public abstract LiteralText getPassiveExtendedDescription();

	public Formatting getPassiveDescriptionFormatting() {
		return Formatting.YELLOW;
	}
	public Formatting getDescriptionFormatting() {
		return Formatting.BLUE;
	}
	public abstract Formatting getPassiveExtendedDescriptionFormatting();
	public abstract Formatting getExtendedDescriptionFormatting();

	public Formatting getTotemBonusFormatting() {
		return Formatting.GREEN;
	}

	@Override
	public boolean shouldActivateTotem(Entity owner) {
		return false;
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity owner, int slot, boolean selected) {
		if (stack.getItem() instanceof IUpgradeableTotem) {
			if (owner instanceof LivingEntity) {
				LivingEntity livingOwner = (LivingEntity) owner;
				if (livingOwner.getMainHandStack().getItem() == this || livingOwner.getOffHandStack().getItem() == this)
					((IUpgradeableTotem) stack.getItem()).onTotemTick(livingOwner);
			}
		}
	}

	@Override
	public boolean shouldSyncTagToClient() {
		return true;
	}
	
	public boolean shouldTickTotem() {
		return true;
	}

	@Nullable
	public static ItemStack findTotemInInventory(BaseTotemItem totem, PlayerEntity player, boolean shouldBeHolding) {
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			ItemStack totemStack = new ItemStack(totem);

			if (!serverPlayer.inventory.offHand.isEmpty()) {
				for (ItemStack offHandStack : serverPlayer.inventory.offHand) {
					if (!offHandStack.isEmpty()) {
						if (offHandStack.isItemEqual(totemStack)) return offHandStack;
					}
				}
			}
			
			for (ItemStack curStack : serverPlayer.inventory.main) {
				if (!curStack.isEmpty()) {
					if (shouldBeHolding) {
						for (Hand hand : Hand.values()) {
							ItemStack handStack = player.getStackInHand(hand);
							if (!handStack.isItemEqual(curStack)) continue;
							curStack = handStack.copy();
							return curStack;
						}
					}
					if (curStack.isItemEqual(totemStack)) {
						return curStack;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static void playTotemAnimation(ServerPlayerEntity playerOwner, BaseTotemItem totem) {
		ItemStack totemStack = new ItemStack(totem);
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeItemStack(totemStack);
		buf.writeInt(playerOwner.getEntityId());
		ServerPlayNetworking.send(playerOwner, ATPackets.TOTEM_ANIMATION_PACKET, buf);
		for (ServerPlayerEntity otherPlayer : PlayerLookup.tracking(playerOwner.getServerWorld(), playerOwner.getBlockPos())) {
			ServerPlayNetworking.send(otherPlayer, ATPackets.TOTEM_ANIMATION_PACKET, buf);
		}
	}

	@Nullable
	public static ItemStack decrementTotem(ServerPlayerEntity owner, BaseTotemItem totem) {
		ItemStack originalTotemStack;
		ItemStack totemStack = owner.getMainHandStack().isItemEqual(totem.getDefaultStack()) ? owner.getMainHandStack() : owner.getOffHandStack();

		if (totemStack.isItemEqual(totem.getDefaultStack())) {
			if (!totemStack.isEmpty()) {
				originalTotemStack = totemStack.copy();
				totemStack.decrement(1);
				if (originalTotemStack != null) {
					owner.incrementStat(Stats.USED.getOrCreateStat(originalTotemStack.getItem()));
					Criteria.USED_TOTEM.trigger(owner, originalTotemStack);
				}
				owner.getServerWorld().sendEntityStatus(owner, (byte)35);
				return originalTotemStack;
			}
		}
		return null;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new LiteralText("Totem Tier: ").formatted(Formatting.BOLD)
				.append(new LiteralText(curTier.getFormattedName()).formatted(curTier.getTooltipFormatting())));

		if (getExtendedDescription() != null || getPassiveExtendedDescription() != null) {
			tooltip.add(new LiteralText("Totem Bonus: ").formatted(getTotemBonusFormatting())
					.append(new LiteralText("(...)").formatted(Formatting.GREEN)));

			if (Screen.hasShiftDown() || Screen.hasControlDown()) {
				tooltip.removeIf((text) -> text.toString().contains("(...)"));
				tooltip.add(new LiteralText("Passive: ").formatted(getPassiveDescriptionFormatting() == null ? Formatting.YELLOW : getPassiveDescriptionFormatting()).formatted(Formatting.BOLD));
				tooltip.add(getPassiveExtendedDescription() == null ? new LiteralText("None.").formatted(Formatting.RED) : getPassiveExtendedDescription().formatted(getPassiveExtendedDescriptionFormatting() == null ? Formatting.GOLD : getPassiveExtendedDescriptionFormatting()));
				tooltip.add(new LiteralText("Ability: ").formatted(getDescriptionFormatting() == null ? Formatting.BLUE : getDescriptionFormatting()).formatted(Formatting.BOLD));
				tooltip.add(getExtendedDescription() == null ? new LiteralText("None.").formatted(Formatting.RED) : getExtendedDescription().formatted(getExtendedDescriptionFormatting() == null ? Formatting.RED : getExtendedDescriptionFormatting()));
			}
		}
	}
}
