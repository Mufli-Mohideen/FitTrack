package com.example.myapplication;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class Recipe {
    private String label; // Recipe name
    private String image; // Image URL
    private List<String> ingredientLines; // Ingredients list
    private List<String> dietLabels; // Diet-related labels
    private double calories; // Total calories
    private List<String> cuisineType; // Cuisine type (updated to List<String>)
    private String source; // Recipe source
    private String url; // URL to the full recipe

    // Getters and setters for each field
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

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public List<String> getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(List<String> cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}