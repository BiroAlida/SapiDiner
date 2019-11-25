package com.example.sapidiner.Classes;

import java.util.ArrayList;

public class FoodCategory {
    private String name;
    private ArrayList<String> foods = new ArrayList<>();

    public FoodCategory() {
    }

    public FoodCategory(String name, ArrayList<String> foods) {
        this.name = name;
        this.foods = foods;
    }

    public FoodCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getFoods() {
        return foods;
    }

    public void addFood(String food){
        foods.add(food);
    }

    public void removeFood(String food){
        foods.remove(food);
    }
}
