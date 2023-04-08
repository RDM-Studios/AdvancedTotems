package com.rdm.advancedtotems.common.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rdm.advancedtotems.AdvancedTotems;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;

public class ATRecipes {
	
	
	private static JsonObject makeShapedRecipe(CharArrayList ingredientKeys, ObjectArrayList<Identifier> ingredients, ObjectArrayList<String> ingredientTypes, ObjectArrayList<String> recipePattern, Identifier resultItem, int resultItemCount) {
		JsonObject shapedRecipeJson = new JsonObject();
		
		shapedRecipeJson.addProperty("type", "minecraft:crafting_shaped");
		
		JsonArray patternArray = new JsonArray();
		
		for (String patternLine : recipePattern) {
			if (patternLine.length() > 2) {
				AdvancedTotems.LOGGER.warn("Illegal shaped crafting recipe for: " + resultItem.toString() + ", skipping recipe...");
				return null;
			}
		}
		
		patternArray.add(recipePattern.get(0));
		patternArray.add(recipePattern.get(1));
		patternArray.add(recipePattern.get(2));
		
		shapedRecipeJson.add("pattern", patternArray);
		
		JsonObject ingredientKey;
		JsonObject ingredientKeyList = new JsonObject();
		
		for (int i = 0; i < ingredientKeys.size(); i++) {
			ingredientKey = new JsonObject();
			ingredientKey.addProperty(ingredientTypes.get(i), ingredients.get(i).toString());
			ingredientKeyList.add(ingredientKeys.getChar(i) + "", ingredientKey);
		}
		
		shapedRecipeJson.add("key", ingredientKeyList);
		
		JsonObject resultItemObj = new JsonObject();
		
		if (resultItemCount < 1) {
			AdvancedTotems.LOGGER.warn("Attempted to add result item for recipe of: " + resultItem.toString() + " with amount under 1, skipping recipe...");
			return null;
		}
		
		resultItemObj.addProperty("item", resultItem.toString());
		resultItemObj.addProperty("count", resultItemCount);
		
		shapedRecipeJson.add("result", resultItemObj);
		
		return shapedRecipeJson;
	}
}
