package com.example.myapplication;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class Recipe {
    private String label;
    private String image;
    private List<String> ingredients;
    private double calories;
    private double totalWeight;
    private String cuisineType;
    private String mealType;
    private Map<String, Double> nutrients;

    public Recipe(String label, String image, List<String> ingredients, double calories,
                  double totalWeight, String cuisineType, String mealType, Map<String, Double> nutrients) {
        this.label = label;
        this.image = image;
        this.ingredients = ingredients;
        this.calories = calories;
        this.totalWeight = totalWeight;
        this.cuisineType = cuisineType;
        this.mealType = mealType;
        this.nutrients = nutrients;
    }

    // Getters and Setters

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public Map<String, Double> getNutrients() {
        return nutrients;
    }

    public void setNutrients(Map<String, Double> nutrients) {
        this.nutrients = nutrients;
    }

    public static Recipe fromJson(String json) {
        return new Gson().fromJson(json, Recipe.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
