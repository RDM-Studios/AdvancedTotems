package com.rdm.advancedtotems.common.items.base;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rdm.advancedtotems.api.IUpgradeableTotem;
import com.rdm.advancedtotems.api.TotemTier;
import com.rdm.advancedtotems.api.TotemTierComparator;
import com.rdm.advancedtotems.common.registries.ATPackets;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public abstract class BaseTotemItem extends Item implements IUpgradeableTotem {
	private TotemTier curTier;
	private TotemTier nextTier;

	public BaseTotemItem(Settings settings) {
		super(settings.maxCount(1));
		initializeTotemTiers();
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
	
	private void initializeTotemTiers() {		
		ItemStack totemStack = getDefaultStack();
		
		if (getDefaultStack().getTag() == null) totemStack.getOrCreateTag();
		
		NbtCompound curTierNbt = totemStack.getOrCreateSubTag("CurrentTier");
		
		if (curTier == null) curTier = getStartingTier();
		if (curTierNbt.getString(curTier.getName()) == null) {
			curTierNbt.putString("CurrentTierName", curTier.getName());
			curTierNbt.putInt("CurrentTierValue", curTier.getValue());
		}
	}
	
	private void handleTotemTiering() {
		ItemStack totemStack = getDefaultStack();
		NbtCompound curTierNbt = totemStack.getOrCreateSubTag("CurrentTier");
		
		if (curTier == null) curTier = getStartingTier();
		if (curTierNbt.getString(curTier.getName()) == null) {
			curTierNbt.putString("CurrentTierName", curTier.getName());
			curTierNbt.putInt("CurrentTierValue", curTier.getValue());
		}
		
		if (!getValidTiers().isEmpty()) Collections.sort(getValidTiers(), new TotemTierComparator());
		else return;
		
		TotemTier nextTier = null;
		
		for (TotemTier totemTier : getValidTiers()) {
			if (totemTier.equals(getCurrentTier())) {
				if (getValidTiers().listIterator(getValidTiers().indexOf(totemTier)).nextIndex() != getValidTiers().size()) {
					nextTier = getValidTiers().listIterator(getValidTiers().indexOf(totemTier)).next();
					this.nextTier = nextTier;
					break;
				}
			}
		}
		
		if (nextTier == null && getCurrentTier() != getStartingTier() && getCurrentTier() != getMaxTier()) this.curTier = getMaxTier();
		if (nextTier.getValue() >= getMaxTier().getValue()) this.nextTier = null;
		if (curTier.getValue() >= getMaxTier().getValue()) this.curTier = getMaxTier();
		
		if (nextTier != null) {
			if (shouldUpgradeTier()) {
				this.curTier = nextTier;
				curTierNbt.putString("CurrentTierName", curTier.getName());
				curTierNbt.putInt("CurrentTierValue", curTier.getValue());
			}
		}
	}
	
	public void syncTier() {
		ItemStack totemStack = getDefaultStack();
		NbtCompound curTierNbt = totemStack.getOrCreateSubTag("CurrentTier");
		
		if (curTierNbt != null) {
			if (curTierNbt.getString("CurrentTierName") != null && curTierNbt.getInt("CurrentTierValue") != 0) {
				this.curTier = ATTotemTiers.getTier(curTierNbt.getString("CurrentTierName"), curTierNbt.getInt("CurrentTierValue"));
			}
		}
		
		
		
		handleTotemTiering();
	}
	
	public boolean shouldUpgradeTier() {
		return false;
	}
	
	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (world.isClient) syncTier();
		else ClientPlayNetworking.send(ATPackets.TOTEM_TIER_UPDATE_PACKET, PacketByteBufs.create());
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
	public boolean postProcessNbt(NbtCompound nbt) {
		syncTier();
		return super.postProcessNbt(nbt);
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
