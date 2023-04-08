package com.rdm.advancedtotems.common.items.base;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.api.IUpgradeableTotem;
import com.rdm.advancedtotems.api.TotemTier;
import com.rdm.advancedtotems.api.TotemTierComparator;
import com.rdm.advancedtotems.common.registries.ATPackets;
import com.rdm.advancedtotems.common.registries.ATRecipes;
import com.rdm.advancedtotems.common.registries.ATTotemTiers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public abstract class BaseTotemItem extends Item implements IUpgradeableTotem {
	private TotemTier curTier;
	private TotemTier nextTier;
	private NbtCompound totemTierData = new NbtCompound();
	private final ItemStack totemStack = getDefaultStack();

	public BaseTotemItem(Settings settings) {
		super(settings.maxCount(1).rarity(Rarity.RARE));
		syncTier();
	}

	@Override
	public abstract TotemTier getStartingTier();
	@Override
	public abstract TotemTier getMaxTier();

	@Override
	public ObjectArrayList<TotemTier> getValidTiers() {
		return getDefaultValidTiers();
	}

	@Override
	public abstract void onTotemUse(Entity owner);

	@Override
	public abstract void onTotemTick(Entity owner);

	@Override
	public TotemTier getCurrentTier() {
		return curTier;
	}

	@Nullable
	@Override
	public TotemTier getNextTier() {
		return nextTier;
	}

	public abstract LiteralText getExtendedDescription();

	public abstract Formatting getExtendedDescriptionFormatting();

	public Formatting getTotemBonusFormatting() {
		return Formatting.GREEN;
	}

	@Nullable
	public BaseTotemItem getNextTierTotem(TotemTier currentTier) {
		TotemTier curTier = getCurrentTier();
		TotemTier nextTier = getNextTier();

		if (nextTier == null) return null;
		if (curTier != currentTier) return null;

		if (!(totemStack.getItem() instanceof BaseTotemItem)) return null;

		final BaseTotemItem baseTotem = (BaseTotemItem) totemStack.copy().getItem();
		baseTotem.syncTier();

		return baseTotem;
	}

	public void updateCurrentTier() {		
		if (nextTier != null) {
			this.curTier = nextTier;
			totemTierData.putString("CurrentTierName", curTier.getName());
			totemTierData.putInt("CurrentTierValue", curTier.getValue());
			totemStack.setTag(totemTierData);
		}
	}

	private void initializeTotemTiers() {		
		if (curTier == null) curTier = getStartingTier();
		if (curTier == null && nextTier != null) curTier = getStartingTier();

		totemTierData.putString("CurrentTierName", curTier.getName());
		totemTierData.putInt("CurrentTierValue", curTier.getValue());
		totemStack.setTag(totemTierData);
	}

	private void handleTotemTiering() {
		initializeTotemTiers();

		if (getValidTiers().isEmpty()) return;

		TotemTier nextTier = null;
		Collections.sort(getValidTiers(), new TotemTierComparator());

		for (TotemTier totemTier : getValidTiers()) {
			if (totemTier.equals(getCurrentTier())) {
				if (getValidTiers().listIterator(getValidTiers().indexOf(totemTier) + 1).hasNext()) {
					nextTier = getValidTiers().listIterator(getValidTiers().indexOf(totemTier) + 1).next();
					this.nextTier = nextTier;
					break;
				}
			}
		}

		if (nextTier == null) return;
		if (nextTier != null && nextTier.getValue() > getMaxTier().getValue()) this.nextTier = null;
		if (curTier.getValue() >= getMaxTier().getValue()) this.curTier = getMaxTier();
	}

	public void syncTier() {
		handleTotemTiering();

		if (totemStack.hasTag()) {
			if (totemTierData.getString("CurrentTierName") != null && totemTierData.getInt("CurrentTierValue") != 0 && this.curTier != ATTotemTiers.getTier(totemTierData.getString("CurrentTierName"), totemTierData.getInt("CurrentTierValue"))) {
				this.curTier = ATTotemTiers.getTier(totemTierData.getString("CurrentTierName"), totemTierData.getInt("CurrentTierValue"));
			}
		}
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (world.isClient) ClientPlayNetworking.send(ATPackets.TOTEM_TIER_UPDATE_PACKET, PacketByteBufs.create());
		syncTier();
		updateCurrentTier();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity owner, int slot, boolean selected) {
		if (stack.getItem() instanceof IUpgradeableTotem) {
			if (owner instanceof LivingEntity) {
				LivingEntity livingOwner = (LivingEntity) owner;
				System.out.println("[CURRENT TIER NAME NBT]: " + totemTierData.getString("CurrentTierName"));
				System.out.println("[CURRENT TIER VALUE NBT]: " + totemTierData.getInt("CurrentTierValue"));
				System.out.println("[STARTINGG TIER]: " + getStartingTier().getName());
				System.out.println("[NEXT TIER]: " + getNextTier().getName());
				System.out.println("[MAX TIER]: " + getMaxTier().getName());
				for (TotemTier t : getValidTiers()) {
					System.out.println("[VALID TIERS]: " + t.getName());
				}
				if (livingOwner.getMainHandStack().getItem() == this || livingOwner.getOffHandStack().getItem() == this)
					((IUpgradeableTotem) stack.getItem()).onTotemTick(livingOwner);
			}
		}
	}

	@Override
	public boolean shouldSyncTagToClient() {
		return true;
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new LiteralText("Totem Tier: ")
				.append(new LiteralText(curTier.getName()).formatted(curTier.getTooltipFormatting())));

		if (getExtendedDescription() != null) {
			tooltip.add(new LiteralText("Totem Bonus: ").formatted(getTotemBonusFormatting())
					.append(new LiteralText("(...)").formatted(getExtendedDescriptionFormatting())));

			if (Screen.hasShiftDown() || Screen.hasControlDown()) {
				tooltip.removeIf((text) -> text.toString().contains("(...)"));
				tooltip.add(getExtendedDescription().formatted(getExtendedDescriptionFormatting()));
			}
		}
	}
}
