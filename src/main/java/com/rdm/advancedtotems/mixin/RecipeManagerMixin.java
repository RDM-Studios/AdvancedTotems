package com.rdm.advancedtotems.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rdm.advancedtotems.common.registries.ATRecipes;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	
    @Inject(method = "apply", at = @At("HEAD"))
    public void at$apply(Map<Identifier, JsonElement> recipeMap, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
    	if (!ATRecipes.getRecipeMap().isEmpty()) {
    		for (Entry<Item, JsonObject> recipeEntry : ATRecipes.getRecipeMap().object2ObjectEntrySet()) {
    			if (recipeEntry != null) {
    				recipeMap.put(Registry.ITEM.getId(recipeEntry.getKey()), recipeEntry.getValue());
    			}
    		}
    	}
    }
	
}
