package com.rdm.advancedtotems.common.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rdm.advancedtotems.AdvancedTotems;
import com.rdm.advancedtotems.api.IngredientType;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ATRecipes {
	private static final Object2ObjectArrayMap<Item, JsonObject> RECIPE_MAP = new Object2ObjectArrayMap<>();
	
	public static JsonObject TOTEM_OF_HASTE_IRON_RECIPE = makeShapedRecipe(CharArrayList.wrap(new char[]{'T', 'I'}), ObjectArrayList.wrap(new Item[]{ATItems.TOTEM_OF_HASTE, Items.IRON_BLOCK}), ObjectArrayList.wrap(new IngredientType[]{IngredientType.ITEM, IngredientType.ITEM}), ObjectArrayList.wrap(new String[]{" I ", "ITI", " I "}), ATItems.TOTEM_OF_HASTE.getNextTierTotem(ATTotemTiers.NONE), 1);
	
	private static JsonObject makeShapedRecipe(CharArrayList ingredientKeys, ObjectArrayList<Item> ingredients, ObjectArrayList<IngredientType> ingredientTypes, ObjectArrayList<String> recipePattern, Item resultItem, int resultItemCount) {
		JsonObject shapedRecipeJson = new JsonObject();
		
		shapedRecipeJson.addProperty("type", "minecraft:crafting_shaped");
		
		JsonArray patternArray = new JsonArray();
		
		for (String patternLine : recipePattern) {
			if (patternLine.length() > 3 || patternLine.length() < 3) {
				AdvancedTotems.LOGGER.warn("Illegal shaped crafting recipe line for: " + Registry.ITEM.getId(resultItem) + " with length " + (patternLine.length() > 2 ? "above" : "under") + "2 at " + patternLine + ", skipping recipe...");
				return null;
			}
		}
		
		if (recipePattern.size() > 3) {
			AdvancedTotems.LOGGER.warn("Illegal shaped crafting recipe pattern for: " + Registry.ITEM.getId(resultItem) + ", pattern size is above 2, skipping recipe...");
			return null;
		}
		
		patternArray.add(recipePattern.get(0));
		patternArray.add(recipePattern.get(1));
		patternArray.add(recipePattern.get(2));
		
		shapedRecipeJson.add("pattern", patternArray);
		
		JsonObject ingredientKey;
		JsonObject ingredientKeyList = new JsonObject();
		
		for (int i = 0; i < ingredientKeys.size(); i++) {
			ingredientKey = new JsonObject();
			ingredientKey.addProperty(ingredientTypes.get(i).getTypeName(), Registry.ITEM.getId(ingredients.get(i)).toString());
			ingredientKeyList.add(ingredientKeys.getChar(i) + "", ingredientKey);
		}
		
		shapedRecipeJson.add("key", ingredientKeyList);
		
		JsonObject resultItemObj = new JsonObject();
		
		if (resultItemCount < 1) {
			AdvancedTotems.LOGGER.warn("Attempted to add result item for recipe of: " + Registry.ITEM.getId(resultItem) + " with amount under 1, skipping recipe...");
			return null;
		}
		
		if (resultItem.getMaxCount() < resultItemCount) {
			AdvancedTotems.LOGGER.warn("Attempted to add result item for recipe of: " + Registry.ITEM.getId(resultItem) + " with amount over the item's max count/stack limit, skipping recipe...");
			return null;
		}
		
		resultItemObj.addProperty("item", Registry.ITEM.getId(resultItem).toString());
		resultItemObj.addProperty("count", resultItemCount);
		
		shapedRecipeJson.add("result", resultItemObj);
		
		if (RECIPE_MAP.containsValue(shapedRecipeJson))
			throw new UnsupportedOperationException("Attempted to add duplicate recipe for item: " + resultItem.getTranslationKey());
		
		RECIPE_MAP.put(resultItem, shapedRecipeJson);
		
		return shapedRecipeJson;
	}
	
	public static Object2ObjectArrayMap<Item, JsonObject> getRecipeMap() {
		return RECIPE_MAP;
	}
}
