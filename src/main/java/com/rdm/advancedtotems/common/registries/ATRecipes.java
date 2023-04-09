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

	// Totem of Haste
	public static JsonObject TOTEM_OF_HASTE_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'D', 'P', 'T', 'G'}),
			ObjectArrayList.wrap(new Item[]{Items.DIAMOND, Items.GOLDEN_PICKAXE, Items.TOTEM_OF_UNDYING, Items.GOLD_INGOT}),
			ObjectArrayList.wrap(new String[]{" D ", "PTP", " G "}), ATItems.TOTEM_OF_HASTE);
	public static JsonObject TOTEM_OF_HASTE_IRON_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'T', 'I'}),
			ObjectArrayList.wrap(new Item[]{ATItems.TOTEM_OF_HASTE, Items.IRON_BLOCK}),
			ObjectArrayList.wrap(new String[]{" I ", "ITI", " I "}), ATItems.TOTEM_OF_HASTE_IRON);

	// Totem of Fluid Walking
	public static JsonObject TOTEM_OF_FLUID_WALKING_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'H', 'W', 'T', 'L', 'B'}), 
			ObjectArrayList.wrap(new Item[]{Items.GOLDEN_HELMET, Items.WATER_BUCKET, Items.TOTEM_OF_UNDYING, Items.LAVA_BUCKET, Items.GOLDEN_BOOTS}),
			ObjectArrayList.wrap(new String[]{" H ", "WTL", " B "}), ATItems.TOTEM_OF_FLUID_WALKING);

	// Totem of Mending
	public static JsonObject TOTEM_OF_MENDING_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'E', 'X', 'T', 'B'}),
			ObjectArrayList.wrap(new Item[]{Items.ENCHANTING_TABLE, Items.EXPERIENCE_BOTTLE, Items.TOTEM_OF_UNDYING, Items.ENCHANTED_BOOK}),
			ObjectArrayList.wrap(new String[]{" E ", "XTX", " B "}), ATItems.TOTEM_OF_MENDING);
	public static JsonObject TOTEM_OF_MENDING_IRON_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'X', 'I', 'T'}),
			ObjectArrayList.wrap(new Item[]{Items.EXPERIENCE_BOTTLE, Items.IRON_BLOCK, ATItems.TOTEM_OF_MENDING}),
			ObjectArrayList.wrap(new String[]{"XIX", "ITI", "XIX"}), ATItems.TOTEM_OF_MENDING_IRON);
	public static JsonObject TOTEM_OF_MENDING_DIAMOND_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'E', 'D', 'T', 'X'}),
			ObjectArrayList.wrap(new Item[]{Items.ENCHANTED_BOOK, Items.DIAMOND_BLOCK, ATItems.TOTEM_OF_MENDING_IRON, Items.EXPERIENCE_BOTTLE}),
			ObjectArrayList.wrap(new String[]{"XDX", "ETE", "XDX"}), ATItems.TOTEM_OF_MENDING_DIAMOND);
	public static JsonObject TOTEM_OF_MENDING_NETHERITE_RECIPE = makeShapedItemRecipe(CharArrayList.wrap(new char[]{'N', 'X', 'T'}),
			ObjectArrayList.wrap(new Item[]{Items.NETHERITE_INGOT, Items.EXPERIENCE_BOTTLE, ATItems.TOTEM_OF_MENDING_DIAMOND}),
			ObjectArrayList.wrap(new String[]{"ENE", "NTN", "ENE"}), ATItems.TOTEM_OF_MENDING_NETHERITE);

	private static JsonObject makeShapedItemRecipe(CharArrayList ingredientKeys, ObjectArrayList<Item> ingredients, ObjectArrayList<String> recipePattern, Item resultItem) {
		return makeShapedRecipe(ingredientKeys, ingredients, IngredientType.ITEM, recipePattern, resultItem, 1);
	}

	private static JsonObject makeShapedItemRecipe(CharArrayList ingredientKeys, ObjectArrayList<Item> ingredients, ObjectArrayList<String> recipePattern, Item resultItem, int resultItemCount) {
		return makeShapedRecipe(ingredientKeys, ingredients, IngredientType.ITEM, recipePattern, resultItem, resultItemCount);
	}

	private static JsonObject makeShapedRecipe(CharArrayList ingredientKeys, ObjectArrayList<Item> ingredients, IngredientType ingType, ObjectArrayList<String> recipePattern, Item resultItem, int resultItemCount) {
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
			ingredientKey.addProperty(ingType.getTypeName(), Registry.ITEM.getId(ingredients.get(i)).toString());
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
