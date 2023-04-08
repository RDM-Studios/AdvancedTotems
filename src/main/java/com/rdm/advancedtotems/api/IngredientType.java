package com.rdm.advancedtotems.api;

public enum IngredientType {
	ITEM("item"),
	TAG("tag");
	
	private final String typeName;
	
	private IngredientType(String typeName) {
		this.typeName = typeName;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
